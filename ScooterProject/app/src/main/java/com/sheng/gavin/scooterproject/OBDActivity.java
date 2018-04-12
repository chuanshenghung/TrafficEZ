package com.sheng.gavin.scooterproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class OBDActivity extends AppCompatActivity {
    //OBD buleTooth
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private Thread workerThread;
    private byte[] readBuffer;
    private int readBufferPosition;
    private volatile boolean stopWorker;
    private String[] command1 = { "03\r", "ATRV\r"};
    private Handler handler = new Handler();
    private ProgressDialog pDialog;

    //Element
    TextView BTConnect,BTResult;
    Button ReConnectButton;

    //Firebase
    FirebaseUser user;
    FirebaseDatabase db;
    String UID;

    //Firebase into ListView
    DatabaseReference dref;
    ListView listview;
    ArrayList<String> list=new ArrayList<>();

    //
    int count = 1;//記錄

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obd);
        //Element
        BTConnect = (TextView) findViewById(R.id.BTConnect);
        BTResult = (TextView) findViewById(R.id.BTResult);
        ReConnectButton = (Button) findViewById(R.id.ReConnectButton);


        ReConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settintIntent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(settintIntent);
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        new OBD_Connect().execute();

            db = FirebaseDatabase.getInstance();
            user = FirebaseAuth.getInstance().getCurrentUser();
            UID = user.getUid();


        listview=(ListView)findViewById(R.id.OBDListView);
        final ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,list);
        listview.setAdapter(adapter);
        dref=FirebaseDatabase.getInstance().getReference("Users").child(UID).child("OBD 檢測記錄");

        dref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.getValue(String.class);
                list.add(value);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                list.remove(value);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    private void findOBD(){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mmDevice=device;
                BTConnect.setText("已連接OBD");
                break;

            }
        }
    }

    private void sendCommand() throws InterruptedException, IOException {
        for (int i = 0; i < command1.length; i++) {
            mmOutputStream.write(command1[i].getBytes());
            Thread.sleep(200);
        }
    }


    private Runnable RunStart = new Runnable() {
        public void run() {
            try {
                sendCommand();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            handler.postDelayed(RunStart, 1000);
        }
    };


    private void OpenOBD() throws IOException {
        // 固定的UUID
        final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
        UUID uuid = UUID.fromString(SPP_UUID);
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();
        beginListenForData();
    }


    private void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; // This is the ASCII code for a newline character
        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() { // 建立一條新執行緒進入傾聽來自藍芽裝置資料輸入程序
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {
                            final byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    final byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0,	encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition =0;
                                    handler.post(new Runnable() {
                                        public void run() {
                                            if(data.length() > 6){
                                                if(data.substring(0,2).equals("43")){
                                                    BTResult.setText("P0100\n"+"空氣流量");
                                                    if(data.length() > 6){
                                                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
                                                        Date curDate = new Date(System.currentTimeMillis());
                                                        String date = formatter.format(curDate);
                                                        //getTime
                                                        SimpleDateFormat sdf=new SimpleDateFormat("hh:mm:ss");
                                                        String time =sdf.format(new java.util.Date());
                                                        DatabaseReference usersRef = db.getReference("Users");
                                                        usersRef.child(UID).child("OBD 檢測記錄").child(String.valueOf(count++)).setValue("檢測代碼:P0100"
                                                                + "，日期" + date + "，時間" + time);
                                                        workerThread.interrupt();
                                                    }

                                                }
                                            }
                                        }
                                    });
                                } else
                                    readBuffer[readBufferPosition++] = b;
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }});
        workerThread.start();
    }

    class OBD_Connect extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OBDActivity.this);
            pDialog.setMessage("尋找藍芽裝置中...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            try {
                findOBD();
                Thread.sleep(300);
            } catch (Exception e) {
                pDialog.setMessage("Error!");
                pDialog.dismiss();
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            try{
                OpenOBD();
                handler.post(RunStart);
            } catch (Exception e) {
                e.printStackTrace();
                new AlertDialog.Builder(OBDActivity.this)
                        .setTitle("警告訊息")
                        //.setIcon(R.drawable.cross)
                        .setMessage("未尋找到藍芽裝置...")
                        .setNegativeButton("確定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {

                            }}).show();
            }
            pDialog.dismiss();
        }
    }

    private void closeOBD() throws IOException {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            closeOBD();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

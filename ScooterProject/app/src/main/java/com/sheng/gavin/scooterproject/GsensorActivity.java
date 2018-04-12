package com.sheng.gavin.scooterproject;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GsensorActivity extends AppCompatActivity implements SensorEventListener {
//
    //Element
    public TextView Xsensor,Ysensor,Zsensor;
    private SensorManager aSensorManager;
    private final String TAG = "GpsExample";
    private Sensor aSensor;
    private float gravity[] = new float[3];
    TextView d1;
    int danger=0,count=0,X;


    Button test;

    //Firebase
    FirebaseUser user;
    FirebaseDatabase db;
    String UID;

    DatabaseReference dref;
    ListView listview;
    ArrayList<String> list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gsensor);


        Xsensor = (TextView) findViewById(R.id.Xsensor);
        Ysensor = (TextView) findViewById(R.id.Ysensor);
        Zsensor = (TextView) findViewById(R.id.Zsensor);

        aSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        aSensor = aSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        aSensorManager.registerListener((SensorEventListener) this, aSensor, aSensorManager.SENSOR_DELAY_NORMAL);

        //Firebase UID
        db = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        UID = user.getUid();


            listview=(ListView)findViewById(R.id.GsensorListView);
            final ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,list);
            listview.setAdapter(adapter);
            dref=FirebaseDatabase.getInstance().getReference("Users").child(UID).child("G值<違規記錄>");

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


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        gravity[0] = event.values[0];
        gravity[1] = event.values[1];
        gravity[2] = event.values[2];
        Xsensor.setText(""+gravity[0]);
        Ysensor.setText(""+gravity[1]);
        Zsensor.setText(""+gravity[2]);

        X = (int) event.values[0];

        if (X==5||X==-5){
            count++;
            //getDate
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
            Date curDate = new Date(System.currentTimeMillis());
            String date = formatter.format(curDate);
            //getTime
            SimpleDateFormat sdf=new SimpleDateFormat("hh:mm:ss");
            String time =sdf.format(new java.util.Date());
                DatabaseReference usersRef = db.getReference("Users");
                usersRef.child(UID).child("G值<違規記錄>").child(String.valueOf(count)).setValue("違規轉彎" + "，日期" + date + "，時間" + time);

            X = 0;
        }
    }
}

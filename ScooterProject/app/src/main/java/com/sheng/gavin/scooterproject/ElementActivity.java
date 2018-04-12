package com.sheng.gavin.scooterproject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ElementActivity extends AppCompatActivity {

    String[] strArray;
    int[] vArray;
    int[] nArray;
     TextView t2=(TextView)findViewById(R.id.textView3);
    DatabaseReference reference_contacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element);

        reference_contacts= FirebaseDatabase.getInstance().getReference("零件損耗");


        final EditText ed=(EditText)findViewById(R.id.editText);

        Button bt=(Button)findViewById(R.id.button);
        ListView listView = (ListView) findViewById(R.id.ListView);
         final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1);
        listView.setAdapter(adapter);
        strArray = new String[10];
        vArray =new int[10];
        nArray =new int[10];

        reference_contacts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                int i=0;
                for (DataSnapshot ds : dataSnapshot.getChildren() ){
                    if(i==0){
                        adapter.add(ds.child("name").getValue().toString());
                        t2.setText(ds.child("value").getValue().toString());
                        strArray[i]=ds.child("name").getValue().toString();
                        vArray[i]= Integer.parseInt(ds.child("value").getValue().toString());
                        nArray[i]= Integer.parseInt(ds.child("next").getValue().toString());
                        Map<String, Object> referenceUP = new HashMap<String, Object>();
                        String st= String.valueOf(vArray[0]+Integer.parseInt(ds.child("value").getValue().toString()));
                        referenceUP.put("next", Integer.parseInt("0"));
                        reference_contacts.child(String.valueOf(i+1)).updateChildren(referenceUP);
                        i++;
                        continue;
                    }
                    adapter.add(ds.child("name").getValue().toString());
                    strArray[i]=ds.child("name").getValue().toString();
                    vArray[i]= Integer.parseInt(ds.child("value").getValue().toString());


                    Map<String, Object> referenceUP = new HashMap<String, Object>();
                    String st= String.valueOf(vArray[0]+Integer.parseInt(ds.child("value").getValue().toString()));
                    referenceUP.put("next", Integer.parseInt(st));
                    reference_contacts.child(String.valueOf(i+1)).updateChildren(referenceUP);
                    nArray[i]= Integer.parseInt(ds.child("next").getValue().toString());

                    i++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mso(l);
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ed.getText().toString()!="") {
                    Map<String, Object> referenceUP = new HashMap<String, Object>();
                    referenceUP.put("value", Integer.parseInt(ed.getText().toString()));
                    reference_contacts.child("1").updateChildren(referenceUP);
                    Toast.makeText(ElementActivity.this, "更改完成", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void mso(long l) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(strArray[(int) l]);
        if(vArray[0]==0){
            dialog.setMessage("損耗率為0%");
        }else{
            double va=(nArray[(int) l]-vArray[0])/100;
            dialog.setMessage("損耗率"+va+"%\n下次更換的里程數:"+nArray[(int) l]);
        }

//"+String.valueOf(vArray[(int) l][1])+"

        dialog.setCancelable(false);
        dialog.setPositiveButton("已換新", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // 按下PositiveButton要做的事
                Toast.makeText(ElementActivity.this, "收到", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Toast.makeText(ElementActivity.this, "取消", Toast.LENGTH_LONG).show();
            }
        });
        dialog.show();
    }


}
/*
package com.sheng.gavin.scooterproject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class ElementActivity extends AppCompatActivity{
    String[] strArray;
    int[] vArray;
    int[] nArray;
    final TextView t2=(TextView)findViewById(R.id.textView3);
    final DatabaseReference reference_contacts = FirebaseDatabase.getInstance().getReference("零件損耗");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText ed=(EditText)findViewById(R.id.editText);

        Button bt=(Button)findViewById(R.id.button);
        ListView listView = (ListView) findViewById(R.id.ListView);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1);
        listView.setAdapter(adapter);
        strArray = new String[10];
        vArray =new int[10];
        nArray =new int[10];

        reference_contacts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                int i=0;
                for (DataSnapshot ds : dataSnapshot.getChildren() ){
                    if(i==0){
                        adapter.add(ds.child("name").getValue().toString());
                        t2.setText(ds.child("value").getValue().toString());
                        strArray[i]=ds.child("name").getValue().toString();
                        vArray[i]= Integer.parseInt(ds.child("value").getValue().toString());
                        nArray[i]= Integer.parseInt(ds.child("next").getValue().toString());
                        Map<String, Object> referenceUP = new HashMap<String, Object>();
                        String st= String.valueOf(vArray[0]+Integer.parseInt(ds.child("value").getValue().toString()));
                        referenceUP.put("next", Integer.parseInt("0"));
                        reference_contacts.child(String.valueOf(i+1)).updateChildren(referenceUP);
                        i++;
                        continue;
                    }
                    adapter.add(ds.child("name").getValue().toString());
                    strArray[i]=ds.child("name").getValue().toString();
                    vArray[i]= Integer.parseInt(ds.child("value").getValue().toString());


                    Map<String, Object> referenceUP = new HashMap<String, Object>();
                    String st= String.valueOf(vArray[0]+Integer.parseInt(ds.child("value").getValue().toString()));
                    referenceUP.put("next", Integer.parseInt(st));
                    reference_contacts.child(String.valueOf(i+1)).updateChildren(referenceUP);
                    nArray[i]= Integer.parseInt(ds.child("next").getValue().toString());

                    i++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mso(l);
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ed.getText().toString()!="") {
                    Map<String, Object> referenceUP = new HashMap<String, Object>();
                    referenceUP.put("value", Integer.parseInt(ed.getText().toString()));
                    reference_contacts.child("1").updateChildren(referenceUP);
                    Toast.makeText(ElementActivity.this, "更改完成", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void mso(long l) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(strArray[(int) l]);
        if(vArray[0]==0){
            dialog.setMessage("損耗率為0%");
        }else{
            double va=(nArray[(int) l]-vArray[0])/100;
            dialog.setMessage("損耗率"+va+"%\n下次更換的里程數:"+nArray[(int) l]);
        }

//"+String.valueOf(vArray[(int) l][1])+"

        dialog.setCancelable(false);
        dialog.setPositiveButton("已換新", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // 按下PositiveButton要做的事
                Toast.makeText(ElementActivity.this, "收到", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Toast.makeText(ElementActivity.this, "取消", Toast.LENGTH_LONG).show();
            }
        });
        dialog.show();
    }

}

 */
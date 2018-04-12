package com.sheng.gavin.scooterproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RecordDangerActivity extends AppCompatActivity {
    //Firebase
    FirebaseUser user;
    FirebaseDatabase db;
    String UID;

    //Firebase into listView
    DatabaseReference dref;
    ListView GsensorListView,OBDListView,GPSListView;
    ArrayList<String> Gsensorlist=new ArrayList<>();
    ArrayList<String> OBDlist=new ArrayList<>();
    ArrayList<String> GPSlist=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_danger);

        db = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        UID = user.getUid();

        //GsensorListView
        GsensorListView=(ListView)findViewById(R.id.GsensorListView);
        final ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,Gsensorlist);
        GsensorListView.setAdapter(adapter);
        dref=FirebaseDatabase.getInstance().getReference("Users").child(UID).child("G值<違規記錄>");

        dref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.getValue(String.class);
                Gsensorlist.add(value);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Gsensorlist.remove(value);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //OBDListView
        OBDListView=(ListView)findViewById(R.id.OBDListView);
        final ArrayAdapter<String> OBDadapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,OBDlist);
        OBDListView.setAdapter(OBDadapter);
        dref=FirebaseDatabase.getInstance().getReference("Users").child(UID).child("OBD 檢測記錄");

        dref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.getValue(String.class);
                OBDlist.add(value);
                OBDadapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                OBDlist.remove(value);
                OBDadapter.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        GPSListView=(ListView)findViewById(R.id.GPSListView);
        final ArrayAdapter<String> GPSadapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,GPSlist);
        GPSListView.setAdapter(GPSadapter);
        dref=FirebaseDatabase.getInstance().getReference("Users").child(UID).child("超速<違規記錄>");

        dref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.getValue(String.class);
                GPSlist.add(value);
                GPSadapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                GPSlist.remove(value);
                GPSadapter.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });





    }
}

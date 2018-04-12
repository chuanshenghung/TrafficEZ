package com.sheng.gavin.scooterproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    //Firebase
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase db;
    DatabaseReference DF;
    String UID;

    //Element
    Button MainActivityOBDbutton,MainActivityGsensorButton,DownButton2Activity,
            GPSActivityButton,DownButton1,MapActivityButton,ElementActivityButton;
    ImageView pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Firebase
        auth=FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        UID = user.getUid();
        DF = db.getReference();

        //Element
        MainActivityOBDbutton = (Button) findViewById(R.id.ObdActivity);
        MainActivityGsensorButton = (Button) findViewById(R.id.GsensorButton);
        DownButton2Activity = (Button) findViewById(R.id.DownButton2);
        GPSActivityButton = (Button) findViewById(R.id.GPSButton);
        DownButton1 = (Button) findViewById(R.id.DownButton1);
        pass = (ImageView) findViewById(R.id.imageView6);
        MapActivityButton = (Button) findViewById(R.id.Road);
        ElementActivityButton = (Button) findViewById(R.id.Element);

        //OBDActivity
        MainActivityOBDbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent OBDActivity = new Intent(MainActivity.this, OBDActivity.class);
                startActivity(OBDActivity);
            }
        });

        //GsensorActivity
        MainActivityGsensorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent GsensorMainActivity = new Intent(MainActivity.this, GsensorActivity.class);
                startActivity(GsensorMainActivity);
            }
        });

        //Personal_InformationActivity
        DownButton2Activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Personal_InformationActivity = new Intent(MainActivity.this, Personal_InformationActivity.class);
                startActivity(Personal_InformationActivity);
            }
        });

        //GPSActivity
        GPSActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent GPSActivity = new Intent(MainActivity.this, GPSActivity.class);
                startActivity(GPSActivity);
            }
        });

        //RecordDangerActivity
        DownButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent RecordDangerActivity = new Intent(MainActivity.this, RecordDangerActivity.class);
                startActivity(RecordDangerActivity);
            }
        });

        //MapActivity
        MapActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent MapActivity = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(MapActivity);
            }
        });

        //ElementActivity
        ElementActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ElementActivity = new Intent(MainActivity.this, ElementActivity.class);
                startActivity(ElementActivity);
            }
        });


        //Firebase data count
        imageIFelse();

    }

    public void imageIFelse(){
        DF.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.child(UID).child("G值<違規記錄>").getChildrenCount()>=3){
                    pass.setVisibility(View.GONE);
                }else if(dataSnapshot.child(UID).child("超速<違規記錄>").getChildrenCount()>=3){
                    pass.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //logout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    //logout
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.SignOut:
                auth.signOut();
                Intent LoginActivity = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(LoginActivity);
                MainActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        imageIFelse();
    }


}

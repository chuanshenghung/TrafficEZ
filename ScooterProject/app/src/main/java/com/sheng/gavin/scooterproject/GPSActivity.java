package com.sheng.gavin.scooterproject;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class GPSActivity extends AppCompatActivity {
    //GPS
    private LocationManager mLocationManager = null;
    private LocationListener mLocationListener = null;
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 102;

    //Element
    TextView longitude,latitude,speed;

    //Firebase
    FirebaseUser user;
    FirebaseDatabase db;
    String UID;

    //Firebase into listView
    DatabaseReference dref;
    ListView listview;
    ArrayList<String> list=new ArrayList<>();


    int RecordSpeed,RecordSpeedDB;
    int count =0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        //Element
        longitude = (TextView) findViewById(R.id.GPSlongitudeTextView);
        latitude = (TextView) findViewById(R.id.GPSlatitudeTextView);
        speed = (TextView) findViewById(R.id.GPSSpeedTextView);

        //Firebase UID
        db = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        UID = user.getUid();

        //GPS
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }

        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                longitude.setText(String.valueOf(location.getLatitude()));
                latitude.setText(String.valueOf(location.getLongitude()));
                speed.setText(String.valueOf(location.getSpeed()*4));
                RecordSpeed = ((int)(location.getSpeed()*4));
                RecordSpeedDB = RecordSpeed;
                if(RecordSpeed>=80){
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
                    Date curDate = new Date(System.currentTimeMillis());
                    String date = formatter.format(curDate);
                    //getTime
                    SimpleDateFormat sdf=new SimpleDateFormat("hh:mm:ss");
                    String time =sdf.format(new java.util.Date());
                    DatabaseReference usersRef = db.getReference("Users");
                    usersRef.child(UID).child("超速<違規記錄>").child(String.valueOf(count++)).setValue("違規超速"+RecordSpeedDB+"，日期" + date + "，時間" + time);
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };


        listview=(ListView)findViewById(R.id.GPSListView);
        final ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,list);
        listview.setAdapter(adapter);
        dref=FirebaseDatabase.getInstance().getReference("Users").child(UID).child("超速<違規記錄>");

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
    }//onCreate



    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasPermission = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_FINE_LOCATION_PERMISSION);
            }else {

            }
        }
    }

    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100/*mSec*/, 0/*meter*/, mLocationListener);
        Toast.makeText(GPSActivity.this,"定位中" , Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100/*mSec*/, 0/*meter*/, mLocationListener);
        Toast.makeText(GPSActivity.this,"定位中" , Toast.LENGTH_SHORT).show();
    }

    protected void onStop() {
        super.onStop();
        mLocationManager.removeUpdates(mLocationListener);

    }

}

package com.sheng.gavin.scooterproject;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sheng.gavin.scooterproject.R;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    DatabaseReference reference_contacts = FirebaseDatabase.getInstance().getReference("test");
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 102;
    private GoogleMap mMap;
    private LocationManager mLocationManager = null;
    private LocationListener mLocationListener = null;
    Double de=25.0330, dn=121.5654;
    String a;
    private String addressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();
        final double lat = intent.getDoubleExtra("lat", 0.0);
        final double lng = intent.getDoubleExtra("lng", 0.0);
        final Geocoder geocoder = new Geocoder(this);

        // Oboutain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);


        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = new LocationListener() {

            // http://developer.android.<span class="hilite1">com</span>/reference/android/location/Location.html
            public void onLocationChanged(Location location) {

                de=location.getLatitude();

                dn=location.getLongitude();
                a=String.valueOf(de);
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(de, dn, 1); //放入座標
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    addressText = String.format("%s%s%s",
                            address.getAdminArea(), //城市
                            address.getLocality(), //區
                            address.getThoroughfare()
                    );

                }
                reference_contacts.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        adapter.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if(addressText.equals(ds.child("location").getValue().toString())){

                                AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
                                dialog.setTitle("目前位置");
                                dialog.setMessage(addressText+"此區域曾有事故發生請提高警覺");
                                dialog.setPositiveButton("我了解了",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        // TODO Auto-generated method stub
                                    }

                                });

                                dialog.show();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

                onMapReady(mMap);



            }


            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };



    }




    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int hasPermission = checkSelfPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION);


            if (hasPermission != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_FINE_LOCATION_PERMISSION);

            } else {

                //    Toast.makeText(MainActivity.this, "IAKA2", Toast.LENGTH_SHORT).show();


            }
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestLocationPermission();
            return;

        }

        mMap=googleMap;


        LatLng sydney = new LatLng(de, dn);
        mMap.addMarker(new MarkerOptions().position(sydney));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationButtonClickListener(
                new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {

                        LocationManager locationManager =
                                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Criteria criteria = new Criteria();

                        criteria.setAccuracy(Criteria.ACCURACY_FINE);

                        String provider = locationManager.getBestProvider(criteria, true);

                        Location location = locationManager.getLastKnownLocation(provider);
                        if (location != null) {
                            Log.i("LOCATION", location.getLatitude() + "/" +
                                    location.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(location.getLatitude(), location.getLongitude())
                                    , 15));
                        }
                        return false;
                    }
                }
        );
    }




    protected void onStart() { // ⇔ onStop
        super.onStart();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100/*mSec*/, 0/*meter*/, mLocationListener);

    }
    protected void onStop() { // ⇔ onStart
        super.onStop();
        mLocationManager.removeUpdates(mLocationListener);
    }
}
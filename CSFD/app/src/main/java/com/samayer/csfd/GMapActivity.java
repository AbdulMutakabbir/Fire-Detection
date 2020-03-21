package com.samayer.csfd;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class GMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "GMapActivity";
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private Boolean LocationPermissionGranted = false;
    private GoogleMap gMap;
    private FusedLocationProviderClient flpc;

    private TextView txtLoc;
    private EditText txtMsg;
    private ImageButton sendSMS,sendEmail;
    private String latLng;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmap);

        getLocationPermission();
        txtLoc = findViewById(R.id.txtLoc);
        txtMsg = findViewById(R.id.txtMsg);

        sendSMS = findViewById(R.id.sendSMS);
        sendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String details = "Description: "+txtMsg.getText();
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", "8985039326");
                smsIntent.putExtra("sms_body",latLng+"\n\n\n"+details);
                startActivity(smsIntent);

                Toast.makeText(GMapActivity.this,"Authorities were informed",Toast.LENGTH_SHORT);
                //SmsManager.getDefault().sendTextMessage("9652715848", null,latLng+"\n\n\n"+details, null,null);
                /*SmsManager sms = SmsManager.getDefault();
                PendingIntent sentPI;
                String SENT = "SMS_SENT";
                sentPI = PendingIntent.getBroadcast(GMapActivity.this, 0,new Intent(SENT), 0);
                sms.sendTextMessage("9652715848", null,latLng+"\n\n\n"+details , sentPI, null);
            */
            }
        });

        sendEmail = findViewById(R.id.sendEmail);
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String details = "Description: "+txtMsg.getText();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, "majestic1998@emailaddress.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "CSFD: File warning");
                intent.putExtra(Intent.EXTRA_TEXT, latLng+"\n\n\n"+details);
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });
    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(GMapActivity.this);
    }

    public void getLocationPermission(){
        String[] permissions = {FINE_LOCATION,COURSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                LocationPermissionGranted = true;
                initMap();
            }
            else{
                ActivityCompat.requestPermissions(this,permissions,1001);
            }
        }
        else{
            ActivityCompat.requestPermissions(this,permissions,1001);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LocationPermissionGranted = false;
        switch(requestCode){
            case 1001: {
                if(grantResults.length>0){
                    for(int i=0; i<grantResults.length; i++){
                        if(grantResults[i]!=PackageManager.PERMISSION_GRANTED){
                            LocationPermissionGranted = false;
                            return;
                        }
                    }
                    LocationPermissionGranted = true;
                    initMap();
                }
            }
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "GetLocation(): getting device location...");
        flpc = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (LocationPermissionGranted) {
                @SuppressLint("MissingPermission") Task location = flpc.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "GetLocation(): got Location!");
                            Location currentLocation = (Location) task.getResult();
                            latLng = "Latitude: "+currentLocation.getLatitude()+"\nLongitude"+currentLocation.getLongitude()+"\nAltitude: "+currentLocation.getAltitude();
                            txtLoc.setText(latLng);
                            moveMap(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15f);
                        } else {
                            Log.d(TAG, "GetLocation(): unbale to get Location!");
                            Toast.makeText(GMapActivity.this, "Can not get current location.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "GetLocation(): SecurityException..." + e.getMessage());
        }
    }

    private void moveMap(LatLng latLng, float zoom) {
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        gMap = googleMap;
        if (LocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            gMap.setMyLocationEnabled(true);
            gMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

}

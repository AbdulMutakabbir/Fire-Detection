package com.samayer.csfd;

        import android.annotation.SuppressLint;
        import android.content.pm.PackageManager;
        import android.location.Location;
        import android.support.annotation.NonNull;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
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

public class LocationActivity extends AppCompatActivity {

    private static final String TAG = "GMapActivity";
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private Boolean LocationPermissionGranted = false;
    private FusedLocationProviderClient flpc;
    private TextView txtLoc;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        txtLoc = findViewById(R.id.txtLoc);

        getLocationPermission();
    }


    public void getLocationPermission(){
        String[] permissions = {FINE_LOCATION,COURSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                LocationPermissionGranted = true;
                getDeviceLocation();
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
                    getDeviceLocation();
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
                            String latLng = "Latitude: "+currentLocation.getLatitude()+"\nLongitude"+currentLocation.getLongitude();
                            txtLoc.setText(latLng);
                            //new ClientSocket("tcp://0.tcp.ngrok.io",18482,latLng);
                        } else {
                            Log.d(TAG, "GetLocation(): unbale to get Location!");
                            txtLoc.setText("Unable to get Location");
                            Toast.makeText(LocationActivity.this, "Can not get current location.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "GetLocation(): SecurityException..." + e.getMessage());
        }
    }

}

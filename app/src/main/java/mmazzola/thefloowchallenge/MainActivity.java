package mmazzola.thefloowchallenge;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Activity mActivity = this;
    private GoogleMap mMap;
    private Location mLastLocation;
    private CameraPosition mCameraPosition;
    private LocationCallback mLocationCallback;
    private ToggleButton toggleUserPositionButton;
    private FusedLocationProviderClient mFusionClient;

    //CONSTANTS
    private final int PERMISSION_REQUEST_CODE = 1;
    private final String[] PERMISSION_REQUESTED = {Manifest.permission.ACCESS_FINE_LOCATION};
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final float ZOOM_LEVEL = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        toggleUserPositionButton = findViewById(R.id.toggleUserPosition);

        mapFragment.getMapAsync(this);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        //Initialize Location Services
        try {
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                handleLocationUpdate(locationResult);
                }
            };
        }catch(SecurityException e){
            Log.e("PERMISSION ", e.getLocalizedMessage());
        }
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,PERMISSION_REQUESTED, PERMISSION_REQUEST_CODE );
            return;
        }
        mMap.setMyLocationEnabled(true);
        mFusionClient = new FusedLocationProviderClient(this);
        toggleUserPositionButton.setChecked(false);
        toggleUserPositionButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            try {
                if (b) {
                    mFusionClient.requestLocationUpdates(createLocationRequest(), mLocationCallback, Looper.myLooper());
                } else {
                    mFusionClient.removeLocationUpdates(mLocationCallback);
                }
            }catch(SecurityException e){
                ActivityCompat.requestPermissions(mActivity,PERMISSION_REQUESTED, PERMISSION_REQUEST_CODE );
            }
            }
        });
    }

    public LocationRequest createLocationRequest(){
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        return mLocationRequest;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            switch (requestCode) {
                case PERMISSION_REQUEST_CODE:
                    if (grantResults.length > 0 && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                        mMap.setMyLocationEnabled(true);
                    }else{
                        disableApplication();
                    }
            }
        }catch(SecurityException e){
            Log.e("PERMISSION ", e.getLocalizedMessage());
        }
    }

    private void handleLocationUpdate(LocationResult locationResult) {
        mLastLocation = locationResult.getLastLocation();
        if(toggleUserPositionButton.isChecked()) {
            updateUI();
        }
    }

    private void updateUI(){
        mCameraPosition = CameraPosition.fromLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), mCameraPosition == null ? ZOOM_LEVEL : mMap.getCameraPosition().zoom);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
    }

    private void disableApplication(){
        toggleUserPositionButton.setChecked(false);
        toggleUserPositionButton.setEnabled(false);
    }
}

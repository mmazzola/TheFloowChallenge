package mmazzola.thefloowchallenge.activity;

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
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;

import mmazzola.thefloowchallenge.R;
import mmazzola.thefloowchallenge.model.Journey;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Activity mActivity = this;
    private GoogleMap mMap;
    private Location mLastLocation;
    private CameraPosition mCameraPosition;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusionClient;

    //CONSTANTS
    private final int PERMISSION_REQUEST_CODE = 1;
    private final String[] PERMISSION_REQUESTED = {Manifest.permission.ACCESS_FINE_LOCATION};
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final float ZOOM_LEVEL = 13;

    //ROUTE
    private ToggleButton toggleUserPositionButton;
    private boolean isTrackingJourney = false;
    private Journey currentJourney;
    private Polyline currentDraw;
    private List<Journey> allJourneys = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        toggleUserPositionButton = findViewById(R.id.toggleUserPosition);

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
                    if(isTrackingJourney) {
                        handleLocationUpdate(locationResult.getLastLocation());
                    }
                }
            };
            mapFragment.getMapAsync(this);
        }catch(SecurityException e){
            Log.e("PERMISSION ", e.getLocalizedMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                    initMap();
                }else{
                    disableApplication();
                }
        }
    }

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
        initMap();
    }

    private void initMap(){
        try {
            mMap.setMyLocationEnabled(true);
            mFusionClient = new FusedLocationProviderClient(this);
            toggleUserPositionButton.setChecked(false);
            toggleUserPositionButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    isTrackingJourney = b;
                    try {
                        if (isTrackingJourney) {
                            //Start Tracking new Journey
                            mFusionClient.requestLocationUpdates(createLocationRequest(), mLocationCallback, Looper.myLooper());
                        } else {
                            //Terminate Journey Tracking
                            mFusionClient.removeLocationUpdates(mLocationCallback);
                            terminateRoute();
                        }
                    }catch(SecurityException e){
                        Log.e("PERMISSION ", e.getLocalizedMessage());
                        ActivityCompat.requestPermissions(mActivity,PERMISSION_REQUESTED, PERMISSION_REQUEST_CODE );
                    }
                }
            });
        }catch(SecurityException e){
            Log.e("PERMISSION ", e.getLocalizedMessage());
            ActivityCompat.requestPermissions(mActivity,PERMISSION_REQUESTED, PERMISSION_REQUEST_CODE );
        }
    }

    public LocationRequest createLocationRequest(){
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        return mLocationRequest;
    }

    private void handleLocationUpdate(Location location) {
        LatLng currentPoint = new LatLng(location.getLatitude(), location.getLongitude());
        if(currentJourney == null){
            startRoute(currentPoint);
        }
        currentJourney.addPoint(currentPoint);
        mLastLocation = location;
        updateUI(currentPoint);
    }

    private void startRoute(LatLng startPoint){
        currentJourney = new Journey(startPoint);
        currentDraw = mMap.addPolyline(currentJourney.getDrawOptions());
    }

    private void terminateRoute() {
        if(currentJourney != null) {
            currentJourney.setDraw(currentDraw);
            allJourneys.add(currentJourney);
            currentJourney = null;
        }else{
            //TODO: PROMPT NOT ENOUGH DATA
        }
    }

    private void updateUI(LatLng newPoint){
        mCameraPosition = CameraPosition.fromLatLngZoom(newPoint, mCameraPosition == null ? ZOOM_LEVEL : mMap.getCameraPosition().zoom);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        currentJourney.addPoint(newPoint);
        List<LatLng> points = currentDraw.getPoints();
        points.add((newPoint));
        currentDraw.setPoints(points);
    }

    private void disableApplication(){
        toggleUserPositionButton.setChecked(false);
        toggleUserPositionButton.setEnabled(false);
    }
}
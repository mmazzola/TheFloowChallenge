package mmazzola.thefloowchallenge.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
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
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import mmazzola.thefloowchallenge.JourneyAdapter;
import mmazzola.thefloowchallenge.R;
import mmazzola.thefloowchallenge.model.Journey;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback , MapActivity{

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
    private static final float NEW_POINT_LOCATION_DISTANCE = 5f;
    private static final PolylineOptions drawoptions = new PolylineOptions().color(Color.RED).width(5);

    //ROUTE
    private ToggleButton toggleUserPositionButton;
    private boolean isTrackingJourney = false;
    private Journey currentJourney;
    private Polyline currentDraw;
    private List<Journey> allJourneys = new ArrayList<>();
    private JourneyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        toggleUserPositionButton = findViewById(R.id.toggleUserPosition);
        RecyclerView mRecyclerView = findViewById(R.id.journeysGrid);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.HORIZONTAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new JourneyAdapter(allJourneys, this);
        mRecyclerView.setAdapter(mAdapter);

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
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mFusionClient = new FusedLocationProviderClient(this);
            toggleUserPositionButton.setChecked(false);
            toggleUserPositionButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    isTrackingJourney = b;
                    mAdapter.setClickable(!isTrackingJourney);
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
            mLastLocation = location;
            updateUI(currentPoint);
        }
        else if(mLastLocation == null || mLastLocation.distanceTo(location) >= NEW_POINT_LOCATION_DISTANCE) {
            currentJourney.addPoint(currentPoint);
            mLastLocation = location;
            updateUI(currentPoint);
        }
    }

    private void startRoute(LatLng startPoint){
        currentJourney = new Journey(startPoint);
        currentDraw = mMap.addPolyline(drawoptions);
    }

    private void terminateRoute() {
        if(currentJourney != null && currentJourney.getPoints().size() > 1) {
            findViewById(R.id.empty_journey_list).setVisibility(View.GONE);
            currentJourney.endJourney();
            mAdapter.addJourney(currentJourney);
            currentJourney = null;
            clearMap();
        }else{
            Toast.makeText(this, "Not enough data collected to store a journey. Please try again!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(LatLng newPoint){
        mCameraPosition = CameraPosition.fromLatLngZoom(newPoint, mCameraPosition == null ? ZOOM_LEVEL : mMap.getCameraPosition().zoom);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        List<LatLng> points = currentDraw.getPoints();
        points.add((newPoint));
        currentDraw.setPoints(points);
    }

    private void disableApplication(){
        toggleUserPositionButton.setChecked(false);
        toggleUserPositionButton.setEnabled(false);
    }

    @Override
    public void updateMapWithJourney(List<LatLng> points) {
        currentDraw = mMap.addPolyline(drawoptions);
        currentDraw.setPoints(points);
    }

    @Override
    public void clearMap() {
        currentDraw.remove();
    }
}
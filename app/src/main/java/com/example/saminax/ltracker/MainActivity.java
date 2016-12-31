package com.example.saminax.ltracker;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    LocationRequest mLocationRequest;
   // boolean mRequestingLocationUpdates= true;
    String mLastUpdateTime="00:00:00";
    GoogleMap mainMap;
    Marker marker;
    Switch switchRecordingOnOff;
    Button buttonRefresh;

    TextView mLatitudeText, mLongitudeText;
    private MapFragment mMapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFields();
        createLocationRequest();
        connectGoogleAPI();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("Samina", "------onConnected Permission value:" + ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION));
        Log.e("Samina", "------onConnected PackageManagerPermission value:" + PackageManager.PERMISSION_GRANTED);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Samina", "Could not connect GoogleAPI");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Log.e("Samina", "GoogleAPI Connected successfully");
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime=getCurrentTimeStamp();


            startLocationUpdates(mGoogleApiClient,mLocationRequest);

            if (mCurrentLocation != null) {
                mMapFragment.getMapAsync(this);
                updateUI();
            }

        }



    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.e("Samina", "--------onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("Samina", "-------onConnectionFailed");
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.e("Samina", "-------onMapReady");

        mainMap=map;

        if(marker!=null) marker.remove();
        addCurrentMarker(mainMap);
        changeCameraPosition(mainMap);

    }

    @Override
    public void onLocationChanged(Location location) {

        mCurrentLocation = location;

        if(switchRecordingOnOff.isChecked()) {
            saveFile();
            Log.e("Samina_On_Loc_Change", "Last file update at "+mLastUpdateTime);
        }


        mLastUpdateTime=getCurrentTimeStamp();
        mLatitudeText.setText(String.valueOf(mCurrentLocation.getLatitude()));
        mLongitudeText.setText(String.valueOf(mCurrentLocation.getLongitude()));
        if(marker!=null) marker.remove();
        addCurrentMarker(mainMap);

    }

    @Override
    protected void onDestroy() {
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }


    void initializeFields() {
        mLatitudeText = (TextView) findViewById(R.id.mLatitudeText);
        mLongitudeText = (TextView) findViewById(R.id.mLongitudeText);
        mLatitudeText.setText("Latitude");
        mLongitudeText.setText("Longitude");

        switchRecordingOnOff= (Switch) findViewById(R.id.switchRecordingOnOff);

        mMapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mMapFragment);

        buttonRefresh = (Button) findViewById(R.id.buttonRefresh);

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mCurrentLocation!=null)updateUI();
            }
        });

    }

    void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    void connectGoogleAPI() {

        if (mGoogleApiClient == null)
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

    }

    void startLocationUpdates(GoogleApiClient mGoogleApiClient,LocationRequest mLocationRequest) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Samina_Start_Update", "Failed");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Log.e("Samina_Start_Update", "Successful");
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest,this);
    }

    void updateUI(){
        mLatitudeText.setText(String.valueOf(mCurrentLocation.getLatitude()));
        mLongitudeText.setText(String.valueOf(mCurrentLocation.getLongitude()));
        mMapFragment.getMapAsync(this);
    }


    private void addCurrentMarker(GoogleMap map){

        if(mCurrentLocation!=null && map!=null)
        marker= map.addMarker(new MarkerOptions()
                .position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                .title(mLastUpdateTime));
    }

    private void changeCameraPosition(GoogleMap map){
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())).zoom(14.0f).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.moveCamera(cameraUpdate);
    }

    void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }


    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        super.onStop();
    }


    private void saveFile(){
        try
        {
            File path = new File(Environment.getExternalStorageDirectory(), "LTracker_Notes");
            if (!path.exists())
            {
                path.mkdirs();
            }
            File myfile = new File(path, getCurrentDate()+ ".txt");

            FileWriter writer = new FileWriter(myfile,true);
            writer.append(getCurrentTimeStamp()+"  " + mCurrentLocation.getLatitude() + "  " + mCurrentLocation.getLongitude()  + "\n");
            writer.flush();
            writer.close();

        }
        catch(IOException e)
        {
            e.printStackTrace();

        }
    }

    String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date());

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }
    String getCurrentDate(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDateTime = dateFormat.format(new Date());

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public void goToSelectDateActivity(View view) {
        Intent intent = new Intent(this, SelectDateActivity.class);
        startActivity(intent);
    }

    public void onRecordingModeChanged(View view){
        Switch switchRecordingOnOff= (Switch) findViewById(R.id.switchRecordingOnOff);
        if(switchRecordingOnOff.isChecked()){
            Log.e("Samina_On/off", "Switch On");
        }
        else
            Log.e("Samina_On/off", "Switch off");
    }

}

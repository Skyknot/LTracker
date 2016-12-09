package com.example.saminax.ltracker;
//AIzaSyAkrO0LPwQDPFdp6BwQmuIMKIcU0xcdt6c//

import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MainActivity extends AppCompatActivity implements  OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public GoogleApiClient mGoogleApiClient;

    Location mLastLocation;
    TextView mLatitudeText, mLongitudeText;
    //GoogleMap mMap;
    MapFragment mMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFields();
        connectGoogleAPI();

        mMapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mMapFragment);
        mMapFragment.getMapAsync(this);



    }

    void initializeFields(){
        mLatitudeText = (TextView) findViewById(R.id.mLatitudeText);
        mLongitudeText = (TextView) findViewById(R.id.mLongitudeText);
        mLatitudeText.setText("Latitude");
        mLongitudeText.setText("Longitude");




        //mMapFragment =(MapFragment) findFragmentById(R.id.mMapFragment);
    }

    void connectGoogleAPI(){
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null)
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("Samina","------onConnected Permission value:"+ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION));
        Log.e("Samina","------onConnected PackageManagerPermission value:"+PackageManager.PERMISSION_GRANTED);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e("Samina","Could not connect GoogleAPI");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
        else{
            Log.e("Samina","GoogleAPI Connected successfully");

        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("Samina","--------onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("Samina","-------onConnectionFailed");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("Samina","-------onMapReady");
    }

    /*@Override
    public void onMapReady(GoogleMap map) {
        Log.e("Samina","-------onMapReady");
        map.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
    }*/
}

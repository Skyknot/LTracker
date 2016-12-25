package com.example.saminax.ltracker;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.Timer;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static android.R.attr.delay;

public class DisplayLocationHistory extends AppCompatActivity implements OnMapReadyCallback{
    private String date;
    private TextView textViewFileFound;
    private double latitude,longitude;
    private String time;
    private MapFragment mapFragment;
    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_location_history);

        TextView textviewDate= (TextView) findViewById(R.id.textviewDate);
        textViewFileFound= (TextView) findViewById(R.id.textViewFileFound);
        mapFragment= (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mMapFragment2);

        Intent intent = getIntent();
        date = intent.getStringExtra(SelectDateActivity.EXTRA_MESSAGE);
        textviewDate.setText(date);


        mapFragment.getMapAsync(this);
       // delay();
        if(mMap==null){
            Log.e("Delay", "Ekhono Null");
        }





    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMap=null;
    }
    void delay(){
        //for(long k=0;k<999999999;k++)
        for(long i=0;i<999999999;i++){
            //int j=(int)i;
        }

    }

    void drawPath() throws IOException {
        String fileName=date+".txt";
        File path = new File(Environment.getExternalStorageDirectory(), "LTracker_Notes");
        if (!path.exists())
        {
            path.mkdirs();
        }

        boolean found=false;
        String[] listOfFiles= path.list();
        for(int i=0;i<listOfFiles.length;i++){
            Log.e("Samina", "Expected filename: "+fileName+"  List Item "+i+": "+listOfFiles[i]);
            if(listOfFiles[i].equals(fileName)){
               found=true;

            }
        }

        if(found){

            File myfile = new File(path, fileName);
            textViewFileFound.setText("Here is your location tracking on "+date);



            BufferedReader reader = new BufferedReader(new FileReader(myfile));

            String line;
            while ((line = reader.readLine()) != null) {
                if(line.equals("")==false){
                    Scanner scanner=new Scanner(line);
                    Log.e("Read File",line);
                    String trackDate= scanner.next();
                    String trackTime= scanner.next();
                    Double trackLat=scanner.nextDouble();
                    Double trackLon=scanner.nextDouble();

                    Log.e("Check Scanner ", trackDate+" "+trackTime+" "+trackLat+" "+trackLon );
                    latitude=trackLat;
                    longitude=trackLon;
                    time=trackTime;

                    //mapFragment.getMapAsync(this);
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .title(time));
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(14.0f).build();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                    mMap.moveCamera(cameraUpdate);

                }

            }
            reader.close();

        }
        else{
            Log.e("Samina","No tracking history on "+ date);
            textViewFileFound.setText("No tracking record on "+ date+"!");

        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.e("Samina", "-------onMapReady in 2nd fragment");
        //if(time!=null)
            mMap=map;
        if(mMap==null){
            Log.e("Delay", "Ekhono Null");
        }
        try {
            drawPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
            /*map.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(time));*/

        /*CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(14.0f).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.moveCamera(cameraUpdate);*/
    }
}

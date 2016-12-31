package com.example.saminax.ltracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
    Button buttonDrawPloyline;
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

       /* buttonRefresh = (Button) findViewById(R.id.buttonRefresh);

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mCurrentLocation!=null)updateUI();
            }
        });*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMap=null;
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


                }
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(14.0f).build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                mMap.moveCamera(cameraUpdate);

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
            drawPolyline();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void drawPolyline() throws IOException {
        String fileName=date+".txt";
        File path = new File(Environment.getExternalStorageDirectory(), "LTracker_Notes");
        if (!path.exists())
        {
            path.mkdirs();
        }

        boolean found=false;
        String[] listOfFiles= path.list();
        for(int i=0;i<listOfFiles.length;i++){
            //Log.e("Samina", "Expected filename: "+fileName+"  List Item "+i+": "+listOfFiles[i]);
            if(listOfFiles[i].equals(fileName)){
                found=true;

            }
        }

        if(found){

            File myfile = new File(path, fileName);
            textViewFileFound.setText("Here is your location tracking on "+date);
            ArrayList<LatLng> points =new ArrayList<LatLng>();



            BufferedReader reader = new BufferedReader(new FileReader(myfile));

            String line;


            String prevTime="00:00:00";
            int counter=0;
            LatLng prePoint;

            while ((line = reader.readLine()) != null) {


                if(line.equals("")==false){
                    Scanner scanner=new Scanner(line);
                    //Log.e("Read File",line);
                    String trackDate= scanner.next();
                    String trackTime= scanner.next();
                    Double trackLat=scanner.nextDouble();
                    Double trackLon=scanner.nextDouble();

                   // Log.e("Check Scanner ", trackDate+" "+trackTime+" "+trackLat+" "+trackLon );
                    latitude=trackLat;
                    longitude=trackLon;
                    time=trackTime;


                    //Log.e("Time Difference",""+ timeDiffenceInSeconds(prevTime,time));


                    if(timeDiffenceInSeconds(prevTime,time)<=20){
                        points.add(new LatLng(latitude,longitude));
                    }
                    else{

                        mMap.addPolyline(new PolylineOptions()
                                .addAll(points)
                                .width(5)
                                .color(Color.RED));
                        points.clear();

                        points.add(new LatLng(latitude,longitude));


                       /* mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .title(time));*/
                    }

                    prevTime=time;

                }


            }
            reader.close();

            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(14.0f).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            mMap.moveCamera(cameraUpdate);

        }
        else{
            //Log.e("Samina","No tracking history on "+ date);
            textViewFileFound.setText("No tracking record on "+ date+"!");

        }
    }

    private long timeDiffenceInSeconds(String t1,String t2){


        return (((t2.charAt(0)-48)*10 + (t2.charAt(1)-48))*3600 + ((t2.charAt(3)-48)*10 + (t2.charAt(4)-48))*60 +((t2.charAt(6)-48)*10 + (t2.charAt(7)-48)))
                -(((t1.charAt(0)-48)*10 + (t1.charAt(1)-48))*3600 + ((t1.charAt(3)-48)*10 + (t1.charAt(4)-48))*60 +((t1.charAt(6)-48)*10 + (t1.charAt(7)-48)));


    }
}

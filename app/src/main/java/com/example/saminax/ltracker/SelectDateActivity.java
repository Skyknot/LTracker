package com.example.saminax.ltracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

public class SelectDateActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.saminax.ltracker.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_date);

        //Intent intent = getIntent();


    }

   public void goToDisplayHistoryActivity(View view){
        Intent intent = new Intent(this, DisplayLocationHistory.class);
        DatePicker mDatepicker= (DatePicker) findViewById(R.id.datePicker);
        int month=mDatepicker.getMonth();
        month++;
        String date=mDatepicker.getYear()+"-"+month +"-"+ mDatepicker.getDayOfMonth();
        intent.putExtra(EXTRA_MESSAGE, date);
        startActivity(intent);
    }

}

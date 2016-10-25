package com.example.aniket.echo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    static String eventName;
    static String eventDescription;
    static String venue;
    static String timeStamp;
    static String timings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.detail_layout);
        setContentView(R.layout.activity_detail);
if(savedInstanceState==null){
    Bundle extras = getIntent().getExtras();
    if(extras != null)
    {
        eventName=extras.getString("eventName");
        eventDescription=extras.getString("eventDescription");
        venue=extras.getString("venue");
        timeStamp=extras.getString("timeStamp");
        timings=extras.getString("timings");


    }
    Log.e("Cheackin",eventName);
     TextView t1 =(TextView)findViewById(R.id.eventName);
     t1.setText(eventName);
    //t1.setText("ashhad");
    TextView t2=(TextView)findViewById(R.id.eventDescription);
    t2.setText(eventDescription);

    TextView t3 =(TextView)findViewById(R.id.venue);
    t3.setText(venue);
    TextView t4 =(TextView)findViewById(R.id.timings);
    t4.setText(timings);

    TextView t5 =(TextView)findViewById(R.id.timestamp);
    t5.setText(timeStamp);
}
   }
}

package com.example.aniket.echo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener , LoadJSONTask.Listener, AdapterView.OnItemClickListener {

    private ListView mListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static final String URL = "http://172.16.80.199:5000/getdata";

    private List<HashMap<String, String>> mAndroidMapList = new ArrayList<>();

    @Override
    public void onRefresh() {


        swipeRefreshLayout.setRefreshing(false);
        Intent intent= new Intent(getApplicationContext(), Main2Activity.class);
        //intent.setData(Uri.parse("http://www.javatpoint.com"));
        startActivity(intent);

        //new LoadJSONTask(this).execute(URL);
        //Log.e("refres","going to refresh");



    }

    private static final String KEY_eventName = "eventName";
    private static final String KEY_venue = "venue";
    private static final String KEY_timings = "timings";
    private static final String KEY_timeStamp = "timeStamp";
    private static final String KEY_eventDescription = "eventDescription";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setOnItemClickListener(this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);


        new LoadJSONTask(this).execute(URL);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);





        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }



    @Override
    public void onLoaded(List<AndroidVersion> androidList) {

        for (AndroidVersion android : androidList) {

            HashMap<String, String> map = new HashMap<>();
            Log.e("gonna loads",android.getEventName());


            map.put(KEY_eventName,android.getEventName());
            map.put(KEY_venue,android.getVenue());
            map.put(KEY_eventDescription,android.getEventDescription());
            map.put(KEY_timings,android.getTimings());
            map.put(KEY_timeStamp,android.getTimeStamp());




            mAndroidMapList.add(map);
        }

        loadListView();
    }

    @Override
    public void onError() {

        Toast.makeText(this, "Error !", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

 //       Toast.makeText(this, mAndroidMapList.get(i).get(KEY_NAME), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Main2Activity.this, DetailActivity.class);
        intent.putExtra("eventName",mAndroidMapList.get(i).get(KEY_eventName));
        intent.putExtra("eventDescription",mAndroidMapList.get(i).get(KEY_eventDescription));
        intent.putExtra("timestamp",mAndroidMapList.get(i).get(KEY_timeStamp));
        intent.putExtra("timings",mAndroidMapList.get(i).get(KEY_timings));
        intent.putExtra("venue",mAndroidMapList.get(i).get(KEY_venue));
        startActivity(intent);
    }

    private void loadListView() {

        Log.e("sd","sds");

        ListAdapter adapter = new SimpleAdapter(Main2Activity.this, mAndroidMapList, R.layout.list_json,
                new String[]{KEY_eventName, KEY_venue, KEY_timings},
                new int[]{R.id.eventName, R.id.venue, R.id.timings});

        mListView.setAdapter(adapter);

    }

}
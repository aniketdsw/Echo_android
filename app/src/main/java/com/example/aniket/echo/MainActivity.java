package com.example.aniket.echo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import android.widget.AdapterView.OnItemClickListener;
import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    Button btn;
    TextView EventName;
    TextView EventDesc;
    AutoCompleteTextView Venue;
    TextView Time;
    Button datePicker;
    Button AccessTime;
    TextView DisplayTime;
    TextView DisplayDate;
    private int CalendarHour, CalendarMinute;
    String format;
    Calendar calendar;
    TimePickerDialog timepickerdialog;
    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private static final String API_KEY = "";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button)findViewById(R.id.button);
        EventName=(TextView)findViewById(R.id.editText1);
        EventDesc=(TextView)findViewById(R.id.editText3);
        Venue=(AutoCompleteTextView)findViewById(R.id.editText2);
      //  Time=(TextView)findViewById(R.id.editText4);
        AccessTime = (Button) findViewById(R.id.timePicker1);
        datePicker = (Button) findViewById(R.id.datePicker);
        DisplayTime=(TextView)findViewById(R.id.TimetextView);
        DisplayDate=(TextView)findViewById(R.id.DatetextView);
      //  View.OnClickListener actvClicked = new View.OnClickListener();

        Venue.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
     // Venue.setOnItemClickListener(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("got clicked");

                //get the data
                String evNm=EventName.getText().toString();
                String evDs=EventDesc.getText().toString();
                String evVn=Venue.getText().toString();
             //   String evTm=Time.getText().toString();
                String evTm= DisplayDate.getText().toString() +" "+ DisplayTime.getText().toString() ;
                Log.e("Time Sent",evTm);
                new StoreEchoInDb(evNm,evDs,evVn,evTm).execute();



                //here send the data to server

            }
        });

        datePicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                DialogFragment dialogfragment = new DatePickerDialogClass();

                dialogfragment.show(ft,"Date Picker Dialog");

            }
        });
        AccessTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                calendar = Calendar.getInstance();
                CalendarHour = calendar.get(Calendar.HOUR_OF_DAY);
                CalendarMinute = calendar.get(Calendar.MINUTE);


                timepickerdialog = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                if (hourOfDay == 0) {

                                    hourOfDay += 12;

                                    format = "AM";
                                }
                                else if (hourOfDay == 12) {

                                    format = "PM";

                                }
                                else if (hourOfDay > 12) {

                                    hourOfDay -= 12;

                                    format = "PM";

                                }
                                else {

                                    format = "AM";
                                }



                                DisplayTime.setText(hourOfDay + ":" + minute + format);
                            }
                        }, CalendarHour, CalendarMinute, false);
                timepickerdialog.show();

            }
        });



    }

    public class StoreEchoInDb extends AsyncTask<String, Void, String> {
        private final String evNm,evDs,evVn,evTm;
        String st = "";
        StoreEchoInDb(String evNm,String evDs,String evVn,String evTm){
            this.evNm=evNm;
            this.evDs=evDs;
            this.evVn=evVn;
            this.evTm=evTm;

        }

        @Override
        protected String doInBackground(String... args) {

            try {

                URL url = new URL("http://172.16.80.199:5000/data"); // here is your URL path

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("eventName", evNm);
                postDataParams.put("eventDescription", evDs);
                postDataParams.put("venue", evVn);
                postDataParams.put("timings", evTm);
                postDataParams.put("timeStamp", "dummy");
                Log.e("params", postDataParams.toString());

                String json=postDataParams.toString(2);



                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                OutputStream os = conn.getOutputStream();
                os.write(json.getBytes());
                os.flush();

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                String output;
                System.out.println("Output from Server .... \n");

                while ((output = br.readLine()) != null) {
                    st = st + output;
                    System.out.println(output);
                }

                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
        //    Toast.makeText(getApplicationContext(), result,
         //           Toast.LENGTH_LONG).show();
          //  Intent intent = new Intent(MainActivity.this, Main2Activity.class);
        //    startActivity(intent);
            finish();

        }


    }//end of async class

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

    public static class DatePickerDialogClass extends DialogFragment implements DatePickerDialog.OnDateSetListener{
        Context context;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){

            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);



            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    AlertDialog.BUTTON_POSITIVE ,this,year,month,day);

            return  datepickerdialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day){
            String MonthString=null;
            month+=1;
            if(month==0)
            {
                MonthString="JAN";
            }
            if(month==2)
            {
                MonthString="FEB";
            }
            if(month==3)
            {
                MonthString="MAR";
            }
            if(month==4)
            {
                MonthString="APR";
            }
            if(month==5)
            {
                MonthString="MAY";
            }
            if(month==6)
            {
                MonthString="JUN";
            }
            if(month==7)
            {
                MonthString="JUL";
            }
            if(month==8)
            {
                MonthString="AUG";
            }
            if(month==9)
            {
                MonthString="SEP";
            }
            if(month==10)
            {
                MonthString="OCT";
            }
            if(month==11)
            {
                MonthString="NOV";
            }
            if(month==12)
            {
                MonthString="DEC";
            }

            TextView textview = (TextView)getActivity().findViewById(R.id.DatetextView);

            textview.setText(day + "," + MonthString + ":" + year);

        }
    }

    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public static ArrayList autocomplete(String input) {
        ArrayList resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
          //  sb.append("&components=country:all");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
          //  Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
          //  Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
           // Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }
    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index).toString();
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new Filter.FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

}

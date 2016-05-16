package hk.edu.cuhk.ie.iems5722.group4_our_days;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import com.github.clans.fab.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import hk.edu.cuhk.ie.iems5722.group4_our_days.event.event_add_activity;
import hk.edu.cuhk.ie.iems5722.group4_our_days.event.event_item;
import hk.edu.cuhk.ie.iems5722.group4_our_days.event.eventquery_activity;
import hk.edu.cuhk.ie.iems5722.group4_our_days.login_register.UserLoginActivity;


public class MainActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ArrayList<item> msglist = new ArrayList<>();
    private String url = "http://54.187.237.119/iems5722/get_chatrooms";
    private TextView mInformationTextView;
    MsgAdapter adapter;
    String FILENAME = "User_file";
    String FILENAME_U = "user_name_file";
    String FILENAME_CHAT = "chatroom_file";
    String FILENAME_PG = "gender_file";
    private TextView user_name1TV;
    private TextView user_name2TV;
    private TextView user1_locationTV;
    private TextView user2_locationTV;
    private TextView user1_weatherTV;
    private TextView user2_weatherTV;
    private TextView calendarsubtextTV;
    private ArrayList<event_item> event_list = new ArrayList<>();
    EventAdapter event_adapter;
    private Button addEventB;

    private String user_id=null;
    private String user_id2 = null;
    private String user_name1=null;
    private String user_name2=null;
    private String today=null;
    private int event_id;
    private String event_name=null;
    private String event_time=null;
    private String event_type;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private int today_y;
    private int today_m;
    private int today_d;
    private int event_y;
    private int event_m;
    private int event_d;
    private int chatroom_id;
    private int phonenumber;
    private int day_result;
    private String count_text;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    private double latitude;
    private double longitude;
    private int latitude_2=22;
    private int longitude_2=114;
    private int latitude_1=22;
    private int longitude_1=114;
    private String user1_gender;
    private String user2_gender;
    private ImageView user1_icon;
    private ImageView user2_icon;

    //private String url = "http://104.155.195.255/iems5722/get_chatrooms";
    private GoogleApiClient client;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent_one = getIntent();
        user_id = intent_one.getStringExtra("user_id");

        initial_user_id();
        setContentView(R.layout.activity_main);
        new getMainTask().execute();
        new getOwnWeatherTASK().execute();
        new getPartnerWeatherTASK().execute();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Our Days");
        //TextView titleTV = (TextView) this.findViewById(R.id.toolbar_title);
        //titleTV.setText("Our Days");
        buildGoogleApiClient();
        FloatingActionButton phone = (FloatingActionButton) findViewById(R.id.menu_item2);
        FloatingActionButton chat = (FloatingActionButton) findViewById(R.id.menu_item1);

        today = formatter.format(new Date(System.currentTimeMillis()));
        today_y = Integer.parseInt(today.substring(0, 4));
        today_m = Integer.parseInt(today.substring(5, 7));
        today_d = Integer.parseInt(today.substring(8, 10));
        //get reference
        user1_icon = (ImageView) this.findViewById(R.id.user1_icon);
        user2_icon = (ImageView) this.findViewById(R.id.user2_icon);
        user_name1TV = (TextView) this.findViewById(R.id.main_user_name1);
        user_name2TV = (TextView) this.findViewById(R.id.main_user_name2);
        user1_locationTV = (TextView) this.findViewById((R.id.user1_location));
        user2_locationTV = (TextView) this.findViewById(R.id.user2_location);
        user1_weatherTV = (TextView) this.findViewById(R.id.user1_weather);
        user2_weatherTV = (TextView) this.findViewById(R.id.user2_weather);
        calendarsubtextTV = (TextView) this.findViewById(R.id.calandersubtext);
        calendarsubtextTV.setText(today);
        addEventB = (Button) this.findViewById(R.id.main_add_button);



        event_adapter = new EventAdapter(this, event_list);
        ListView event_listview = (ListView) findViewById(R.id.listView_calendar);
        event_listview.setAdapter(event_adapter);
        event_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                event_item event = (event_item) arg0.getAdapter().getItem(position);
                Intent event_intent = new Intent();
                event_intent.putExtra("event_id", event.getEvent_id());
                event_intent.putExtra("event_name", event.getEvent_name());
                event_intent.putExtra("event_time", event.getEvent_time());
                event_intent.putExtra("event_type", event.getEvent_type());
                event_intent.setClass(MainActivity.this, eventquery_activity.class);
                MainActivity.this.startActivity(event_intent);
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("chatroom_id", chatroom_id);
                //intent.putExtra("partner_gender",user2_gender);
                //intent.putExtra("user_name",user_name1);
                intent.setClass(MainActivity.this, chat_activity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phonenumber=Integer.parseInt(user_id2);
                makecall(phonenumber);
            }
        });
        addEventB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addevent_intent = new Intent();
                //  String chatroom_id_s = Integer.toString(chatroom_id);
                addevent_intent.putExtra("chatroom_id", chatroom_id);
                addevent_intent.setClass(MainActivity.this, event_add_activity.class);
                MainActivity.this.startActivity(addevent_intent);
            }
        });
        /*
        new GetAsyncTask().execute();
        adapter = new MsgAdapter(this, msglist);
        final ListView listView2 = (ListView) findViewById(R.id.listView_calendar);
        listView2.setAdapter(adapter);
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                item rm = (item) arg0.getAdapter().getItem(position);
                Intent intent = new Intent();
                int room_id = rm.getType();
                String room_name = rm.getContent();
                intent.putExtra("room_id",room_id);
                intent.putExtra("room_name",room_name);
                intent.setClass(MainActivity.this, chat_activity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        */
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onResume() {
        initial_user_id();
        //Toast.makeText(MainActivity.this,"",Toast.LENGTH_LONG).show();
        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this,resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            //这里处理逻辑代码
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.logout)
        {
            //refresh();
            logout();
        }
        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    public void logout(){
        File f=new File(getFilesDir(), FILENAME);
        if(f.exists()){
            //Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_LONG).show();
            f.delete();
        }
        File fu = new File(getFilesDir(),FILENAME_U);
        if(fu.exists()){
            //Toast.makeText(MainActivity.this, "删除成功2", Toast.LENGTH_LONG).show();
            fu.delete();
        }
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, UserLoginActivity.class);
        MainActivity.this.startActivity(intent);
        this.finish();
    }
    public void refresh() {
        event_list.clear();
        Toast.makeText(MainActivity.this,"refreshed",Toast.LENGTH_LONG).show();
        new getMainTask().execute();
    }
    public void makecall(int phonenumber){
        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
        String tel = "tel:"+ phonenumber;
        phoneIntent.setData(Uri.parse(tel));
        try {
            startActivity(phoneIntent);
            finish();
        }catch (android.content.ActivityNotFoundException ex){
            Toast.makeText(MainActivity.this,
                    "Call faild, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }
    public boolean isleapyear(int year)  //是否是闰年
    {
        return ((year % 4 == 0 && year % 100 != 0)|| year % 400 == 0);
    }
    public int countdays(int year, int month, int day){
        int mon[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int sum = (year - 1) * 365;  //自公元1年来的天数
        int i;

        sum += (year - 1) / 4 + 1;  //能被4整除的都加上
        sum -= (year - 1) / 100 + 1;  //其中被100整除的不是闰年
        sum += (year - 1) / 400 + 1;  //实际上能被400整除的即为闰年

        for(i = 0; i < month - 1; ++i)  //将本年的剩下的整月加上
            sum += mon[i];

        if(isleapyear(year) && month > 2)  //本年是否闰2月
            ++sum;

        sum += day;  //本月的日子加上

        return sum;
    }
    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://hk.edu.cuhk.ie.iems5722.a1_1155073647/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://hk.edu.cuhk.ie.iems5722.a1_1155073647/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            latitude_1 =(int)latitude;
            longitude_1 = (int)longitude;
            updatelocation();
            //new get_user2_locationTASK().execute();
            new getOwnWeatherTASK().execute();
            //Toast.makeText(MainActivity.this,"location:"+latitude+longitude,Toast.LENGTH_LONG).show();
        } else {
        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    public class EventAdapter extends ArrayAdapter<event_item> {
        private int resourceId;

        public EventAdapter(Context context, ArrayList<event_item> objects) { super(context, 0, objects);}
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            event_item eventItem = getItem(position);
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.main_event_item,parent, false);
            TextView textViewName = (TextView) convertView.findViewById(R.id.event_name);
            TextView textViewCount =(TextView) convertView.findViewById(R.id.event_countdays);

            String eventItem_type= eventItem.getEvent_type();
            String eventItem_time=eventItem.getEvent_time();

            //計算事件的日期

            String days = null;
            event_y = Integer.parseInt(eventItem_time.substring(0, 4));
            event_m = Integer.parseInt(eventItem_time.substring(5, 7));
            event_d = Integer.parseInt(eventItem_time.substring(8, 10));
            int user_age = today_y-event_y;
            if (eventItem_type.equals("0")){
                if (event_m>today_m || (event_m == today_m && event_d== today_d)){
                    event_y = today_y;
                }
                else{
                    event_y = today_y+1;
                    user_age+=1;
                }
            }


            int difdays = countdays(event_y,event_m,event_d)-countdays(today_y,today_m,today_d);

            if (difdays>=0){
                day_result =difdays;
                days =Integer.toString(difdays)+" days";
                count_text = " Coming ";
                textViewCount.setBackgroundDrawable(getResources().getDrawable(R.drawable.dayscountdown_shape));
            }
            if(difdays<0){
                int difdays2 = -difdays;
                day_result = difdays2;
                days = Integer.toString(difdays2)+" days";
                count_text = " Already ";
                textViewCount.setBackgroundDrawable(getResources().getDrawable(R.drawable.dayscount_shape));

            }


            if(eventItem_type.equals("0")){
                textViewName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.birthday, 0, 0, 0);
                textViewName.setText(eventItem.getEvent_name() + count_text);
                textViewCount.setText(days);
            }
            if(eventItem_type.equals("1")){
                textViewName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.date, 0, 0, 0);
                textViewName.setText(eventItem.getEvent_name()+count_text);
                textViewCount.setText(days);
            }
            if(eventItem_type.equals("2")) {
                textViewName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.anniversary,0,0,0);
                textViewName.setText(eventItem.getEvent_name()+count_text);
                textViewCount.setText(days);
            }
            return convertView;
        }
    }

    public void updatelocation(){
        new updatelocationTASK().execute();
    }

    public class updatelocationTASK extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... params) {
            try {
                String results = "";
                String url = "http://54.187.237.119/iems5722/update_location";
                URL url_object = new URL(url);
                HttpURLConnection conn =
                        (HttpURLConnection) url_object.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                String postContent = "?user_id=" + user_id+"&latitude="+latitude +"&longitude="+longitude;
                dos.write(postContent.getBytes());
                dos.flush();
                dos.close();
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        results += line;
                    }
                } else {
                    results = "";
                }

                return results;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }


        @Override
        protected void onPostExecute(String results) {
            Log.i(TAG, "results:"+results);
            JSONObject json = null;
            try {
                json = new JSONObject(results);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {

                String status = json.getString("status");
                String result_message = json.getString("message");

                if(status.equals("OK")) {
                   // Toast.makeText(MainActivity.this,"Location Updated!",Toast.LENGTH_LONG).show();
                }else if (status.equals("ERROR")){

                    Toast.makeText(MainActivity.this,"ERROR:"+result_message,Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(MainActivity.this,"Can not get main",Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public class get_user2_locationTASK extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... params) {

            try {
                String getmain_url = "http://54.187.237.119/iems5722/get_user2_location?user_id2="+user_id2;
                String results="";
                URL url_object = new URL(getmain_url);
                HttpURLConnection conn =
                        (HttpURLConnection)url_object.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        results += line;
                    }
                } else {
                    results = "";
                }

                return results;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }


        @Override
        protected void onPostExecute(String results) {
            Log.i(TAG, "results:"+results);
            JSONObject json = null;
            try {
                json = new JSONObject(results);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {

                String status = json.getString("status");
                if(status.equals("OK")) {
                        latitude_2 = json.getInt("latitude");
                        longitude_2 = json.getInt("longitude");
                        new getPartnerWeatherTASK().execute();
                        //Toast.makeText(MainActivity.this,"Get Partner's location",Toast.LENGTH_LONG).show();
                    }
                else if (status.equals("ERROR")){
                    String result_message = json.getString("message");
                    //user2_locationTV.setText("No Location");
                    //user2_weatherTV.setText(" ");
                    //Toast.makeText(MainActivity.this,"ERROR:"+result_message,Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(MainActivity.this,"Can not get partner's location",Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    public class getOwnWeatherTASK extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... params) {

            try {
                String getmain_url = "http://api.openweathermap.org/data/2.5/weather?lat="+latitude_1+"&lon="+longitude_1+"&appid=72444c1d085c6a0ebde167545c648bc7";
                String results = "";
                URL url_object = new URL(getmain_url);
                HttpURLConnection conn =
                        (HttpURLConnection) url_object.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        results += line;
                    }
                } else {
                    results = "";
                }

                return results;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }


        @Override
        protected void onPostExecute(String results) {
            Log.i(TAG, "results:" + results);
            JSONObject json = null;
            try {
                json = new JSONObject(results);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {

                String location_name = json.getString("name");
                JSONArray array = json.getJSONArray("weather");
                JSONObject obj = array.getJSONObject(0);
                String location_weather = obj.getString("main");
                user1_locationTV.setText(location_name);
                user1_weatherTV.setText(location_weather);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    public class getPartnerWeatherTASK extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... params) {

            try {
                String getmain_url = "http://api.openweathermap.org/data/2.5/weather?lat="+latitude_2+"&lon="+longitude_2+"&appid=72444c1d085c6a0ebde167545c648bc7";
                String results = "";
                URL url_object = new URL(getmain_url);
                HttpURLConnection conn =
                        (HttpURLConnection) url_object.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        results += line;
                    }
                } else {
                    results = "";
                }

                return results;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }


        @Override
        protected void onPostExecute(String results) {
            //Toast.makeText(MainActivity.this,"get weather2",Toast.LENGTH_LONG).show();
            Log.i(TAG, "results:" + results);
            JSONObject json = null;
            try {
                json = new JSONObject(results);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {

                String location_name = json.getString("name");
                JSONArray array = json.getJSONArray("weather");
                JSONObject obj = array.getJSONObject(0);
                String location_weather = obj.getString("main");
                user2_locationTV.setText(location_name);
                user2_weatherTV.setText(location_weather);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class getMainTask extends AsyncTask<Void,Void,String>
    {

        @Override
        protected String doInBackground(Void... params) {
            try {
                String getmain_url = "http://54.187.237.119/iems5722/get_main?user_id="+user_id;
                String results="";
                URL url_object = new URL(getmain_url);
                HttpURLConnection conn =
                        (HttpURLConnection)url_object.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        results += line;
                    }
                } else {
                    results = "";
                }

                return results;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }


        @Override
        protected void onPostExecute(String results) {
            //Toast.makeText(MainActivity.this,"result:"+results,Toast.LENGTH_LONG).show();
            Log.i(TAG, "results:"+results);
            JSONObject json = null;
            try {
                json = new JSONObject(results);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                String status = json.getString("status");

                //加上對方user_id2
                if(status.equals("OK")) {
                    JSONObject array = json.getJSONObject("data");
                    user_id2 = array.getString("user_id2");
                    user_name1 = array.getString("user_name1");
                    user_name2 = array.getString("user_name2");
                    user1_gender = array.getString("user_gender1");
                    user2_gender = array.getString("user_gender2");
                    chatroom_id = array.getInt("chatroom_id");
                    savePartnerGender(user2_gender);
                    saveChatroomId(chatroom_id);
                    Log.i(TAG, "chatroomID:" + chatroom_id);
                    JSONArray messagearray = array.getJSONArray("events");
                    for (int i = messagearray.length() - 1; i > -1; i--) {
                        JSONObject msgobj = messagearray.getJSONObject(i);
                        event_id = msgobj.getInt("event_id");
                        event_name = msgobj.getString("event_name");
                        event_time = msgobj.getString("event_time");
                        event_type = msgobj.getString("event_type");
                        event_item event_content = new event_item(event_id, event_name, event_time, event_type);
                        event_list.add(event_content);
                    }
                    event_adapter.notifyDataSetChanged();
                    //listView.setSelection(adapter.getCount() - 1);
                    new get_user2_locationTASK().execute();
                }else if (status.equals("ERROR")){
                    String result_message = json.getString("message");
                    Toast.makeText(MainActivity.this,"ERROR:"+result_message,Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(MainActivity.this,"Can not get main",Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            user_name1TV.setText(user_name1);
            user_name2TV.setText(user_name2);
            saveUserInfo(user_name1);
            //Toast.makeText(MainActivity.this,"gender1:"+user1_gender+"gender2:"+user2_gender,Toast.LENGTH_LONG).show();
            if(user1_gender.equals("female")){
                user1_icon.setImageDrawable(getResources().getDrawable(R.drawable.female_icon));
            }else if (user1_gender.equals("male")){
                user1_icon.setImageDrawable(getResources().getDrawable(R.drawable.male_icon));
            }
            if(user2_gender.equals("female")){
                user2_icon.setImageDrawable(getResources().getDrawable(R.drawable.female_icon));
            }else if (user2_gender.equals("male")){
                user2_icon.setImageDrawable(getResources().getDrawable(R.drawable.male_icon));
            }

        }
    }





    public void initial_user_id(){
        String user_id_from_file=null;
        try {
            FileInputStream inputStream = this.openFileInput(FILENAME);
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

            while (inputStream.read(bytes) != -1) {
                arrayOutputStream.write(bytes, 0, bytes.length);
            }
            inputStream.close();
            arrayOutputStream.close();

            String userstr = new String(arrayOutputStream.toByteArray());

            JSONObject json;
            try {
                json = new JSONObject(userstr);
                if( json.has("key"))
                    user_id_from_file =  json.getString("key");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (user_id_from_file == null)
        {
            //Toast.makeText(MainActivity.this,"No Initial ID",Toast.LENGTH_LONG).show();
        }
        else
        {
            user_id = user_id_from_file;
            //Toast.makeText(MainActivity.this,"Yes Initial"+user_id,Toast.LENGTH_LONG).show();

        }
    }

    public void saveUserInfo(String username) {
        try {

            JSONObject form = new JSONObject();
            try {
                form.put("user_name",username);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            String rightsaveformat = form.toString();

            FileOutputStream outputStream = openFileOutput(FILENAME_U, Activity.MODE_PRIVATE);
            outputStream.write(rightsaveformat.getBytes());
            outputStream.flush();
            outputStream.close();
            //Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void savePartnerGender(String partner_gender) {
        try {

            JSONObject form = new JSONObject();
            try {
                form.put("partner_gender",user2_gender);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            String rightsaveformat = form.toString();

            FileOutputStream outputStream = openFileOutput(FILENAME_PG, Activity.MODE_PRIVATE);
            outputStream.write(rightsaveformat.getBytes());
            outputStream.flush();
            outputStream.close();
            //Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveChatroomId(int chatroom_id) {
        try {

            JSONObject form = new JSONObject();
            try {
                form.put("chatroom_id",chatroom_id);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            String rightsaveformat = form.toString();

            FileOutputStream outputStream = openFileOutput(FILENAME_CHAT, Activity.MODE_PRIVATE);
            outputStream.write(rightsaveformat.getBytes());
            outputStream.flush();
            outputStream.close();
            //Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}




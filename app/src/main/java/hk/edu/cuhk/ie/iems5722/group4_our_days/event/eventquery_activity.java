package hk.edu.cuhk.ie.iems5722.group4_our_days.event;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import hk.edu.cuhk.ie.iems5722.group4_our_days.MainActivity;
import hk.edu.cuhk.ie.iems5722.group4_our_days.R;

/**
 * Created by xuxiaohong on 23/4/16.
 */
public class eventquery_activity extends AppCompatActivity {

    private TextView eventtimeTV;
    private ImageView eventiconIV;
    private TextView eventnameTV;
    private TextView eventtypeTV;
    private TextView eventcountTV;
    private Button editB;
    private Button deleteB;

    private int event_id;
    private String event_time;
    private String event_name;
    private String event_type;
    private String event_count;

    private String today=null;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private int today_y;
    private int today_m;
    private int today_d;
    private int event_y;
    private int event_m;
    private int event_d;
    private String days;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_query);
        setTitle("Our Days");
        Intent intent = getIntent();
        event_id = intent.getIntExtra("event_id", 0);
        event_name = intent.getStringExtra("event_name");
        event_type = intent.getStringExtra("event_type");
        event_time = intent.getStringExtra("event_time");


        eventtimeTV = (TextView) this.findViewById(R.id.query_event_time);
        eventiconIV = (ImageView) this.findViewById(R.id.query_event_icon);
        eventnameTV = (TextView) this.findViewById(R.id.query_event_name);
        //eventtypeTV = (TextView) this.findViewById(R.id.query_event_type);
        eventcountTV = (TextView) this.findViewById(R.id.query_event_count);
        editB = (Button) this.findViewById(R.id.event_query_edit);
        deleteB = (Button) this.findViewById(R.id.event_query_delete);

        today = formatter.format(new Date(System.currentTimeMillis()));
        today_y = Integer.parseInt(today.substring(0, 4));
        today_m = Integer.parseInt(today.substring(5, 7));
        today_d = Integer.parseInt(today.substring(8, 10));
        event_y = Integer.parseInt(event_time.substring(0, 4));
        event_m = Integer.parseInt(event_time.substring(5, 7));
        event_d = Integer.parseInt(event_time.substring(8, 10));


        //計算事件的日期
        int user_age = today_y-event_y;
        if (event_type.equals("0")){
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
            days =Integer.toString(difdays)+" days left";
        }
        if(difdays<0){
            int difdays2 = -difdays;
            days = "Already "+Integer.toString(difdays2)+" days";
        }

        initial_event();
        //Toast.makeText(eventquery_activity.this,"event_name:"+event_name+"event_time:"+event_time+"event_type:"+event_type,Toast.LENGTH_LONG).show();
        editB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent event_intent = new Intent();
                event_intent.putExtra("event_id",event_id);
                event_intent.putExtra("event_name",event_name);
                event_intent.putExtra("event_time",event_time);
                event_intent.putExtra("event_type",event_type);
                event_intent.setClass(eventquery_activity.this, event_edit_activity.class);
                eventquery_activity.this.startActivity(event_intent);
                eventquery_activity.this.finish();
            }
        });

        deleteB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
            }
        });

    }

    public void initial_event(){
        if(event_type.equals("0")){
            eventiconIV.setImageDrawable(getResources().getDrawable(R.drawable.birthday));
        }
        if(event_type.equals("1")){
            eventiconIV.setImageDrawable(getResources().getDrawable(R.drawable.date));
        }
        if(event_type.equals("2")){
            eventiconIV.setImageDrawable(getResources().getDrawable(R.drawable.anniversary));
        }
        eventnameTV.setText(event_name);
        eventtimeTV.setText(event_time);
        eventcountTV.setText(days);
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
    public void showDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(eventquery_activity.this);
        builder.setMessage("Are you sure you want to delete?");
        builder.setTitle("Delete Warning");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            new deleteTASK().execute();
            dialog.dismiss();
            Intent intent = new Intent();
            intent.setClass(eventquery_activity.this, MainActivity.class);
            eventquery_activity.this.startActivity(intent);
            eventquery_activity.this.finish();
        }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        }
        });
        builder.create().show();
    }

    /**
     * Connect to the server by socket,
     * and send register information to server,
     * get return information from server.
     */
    class deleteTASK extends AsyncTask<String, Integer, String> {
		/*
		 * AsyncTask定义了三种泛型类型 Params，Progress和Result。

			Params 启动任务执行的输入参数，比如HTTP请求的URL。
			Progress 后台任务执行的百分比。
			Result 后台执行任务最终返回的结果，比如String。
		 */

        @Override
        protected String doInBackground(String... params) {
            try {
                String results = "";
                String url = "http://54.187.237.119/iems5722/delete_event";
                URL url_object = new URL(url);
                HttpURLConnection conn =
                        (HttpURLConnection) url_object.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                String postContent = "?event_id=" + event_id;
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

            String result_message=null;
            String status = null;
            JSONObject json = null;
            try {
                json = new JSONObject(results);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try{
                status = json.getString("status");
                result_message = json.getString("message");
            }catch (JSONException e) {
                e.printStackTrace();
            }
            if (status.equals("OK")) {
                eventquery_activity.this.finish();
            }else if (status.equals("ERROR")) {
                Toast.makeText(eventquery_activity.this, "ERROR:"+result_message, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(eventquery_activity.this, "Something Wrong happened", Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(UserRegisterActivity.this, message, Toast.LENGTH_SHORT).show();

        }
    }

}

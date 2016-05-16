package hk.edu.cuhk.ie.iems5722.group4_our_days.event;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import hk.edu.cuhk.ie.iems5722.group4_our_days.MainActivity;
import hk.edu.cuhk.ie.iems5722.group4_our_days.R;

/**
 * Created by xuxiaohong on 23/4/16.
 */
public class event_edit_activity extends AppCompatActivity {
    private int event_id;
    private String event_time;
    private String event_name;
    private String event_type;
    private String event_type_name;

    private EditText eventtimeET;
    private ImageView eventiconIV;
    private EditText eventnameET;
    private EditText eventtypeET;
    private Button okB;

    private String[] single_list = {"birthday", "date","anniversary"};
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_edit);
        this.setTitle("Event");
        Intent intent = getIntent();
        event_id = intent.getIntExtra("event_id", 0);
        event_name = intent.getStringExtra("event_name");
        event_time = intent.getStringExtra("event_time");
        event_type = intent.getStringExtra("event_type");

        switch (event_type){
            case "0":
                event_type_name = "birthday";
                break;
            case "1":
                event_type_name = "date";
                break;
            case "2":
                event_type_name = "anniversary";
                break;
            default:
                break;
        }

        eventnameET = (EditText) this.findViewById(R.id.event_name_edit);
        eventtypeET = (EditText) this.findViewById(R.id.event_type_edit);
        eventtimeET = (EditText) this.findViewById(R.id.event_time_edit);
        okB = (Button) this. findViewById(R.id.event_edit_ok);
        eventnameET.setText(event_name);
        eventtimeET.setText(event_time);
        eventtypeET.setText(event_type_name);

        eventtypeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypePickerDialog();
            }
        });

        eventtimeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        okB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addInfo()) {
                    updateEvent();

                }

            }
        });
    }

    public void showTypePickerDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Choose the type");
        builder.setSingleChoiceItems(single_list, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = single_list[which];
                eventtypeET.setText(str);
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void showDatePickerDialog() {
        // 設定初始日期
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        // 跳出日期選擇器
        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String mOfYear=null;
                        String dOfMonth=null;
                        // 完成選擇，顯示日期
                        int k=monthOfYear/10;
                        if (k<1){
                            mOfYear = "0"+Integer.toString(monthOfYear + 1);
                        }
                        if(k==1){
                            mOfYear = Integer.toString(monthOfYear + 1);
                        }
                        int j=dayOfMonth/10;
                        if(j<1){
                            dOfMonth = "0"+Integer.toString(dayOfMonth);
                        }
                        if(j>=1){
                            dOfMonth = Integer.toString(dayOfMonth);
                        }
                        String date=Integer.toString(year);
                        date +=mOfYear;
                        date +=dOfMonth;
                        // date +=Integer.toString(dayOfMonth);
                        eventtimeET.setText(date);

                    }
                }, mYear, mMonth, mDay);
        dpd.show();
    }
    private boolean addInfo(){
        event_name = eventnameET.getText().toString();
        event_time = eventtimeET.getText().toString();
        event_type_name = eventtypeET.getText().toString();
        switch (event_type_name) {
            case "birthday":
                event_type = "0";
                break;
            case "date":
                event_type = "1";
                break;
            case "anniversary":
                event_type = "1";
                break;
            default:
                break;
        }
        if("".equals(event_name)){
            Toast.makeText(event_edit_activity.this, "Event Name cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if("".equals(event_time)){
            Toast.makeText(event_edit_activity.this, "Event Time cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }else if("".equals(event_type_name)){
            Toast.makeText(event_edit_activity.this, "Event Type cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else return true;

    }

    private void updateEvent(){
        new updateEventTASK().execute();
        /*
        Intent saveintent = new Intent();
        saveintent.putExtra("event_id",event_id);
        saveintent.putExtra("event_name",event_name);
        saveintent.putExtra("event_type",event_type);
        saveintent.putExtra("event_time",event_time);
        //Toast.makeText(event_edit_activity.this,"event_name:"+event_name+"event_time:"+event_time+"event_type:"+event_type,Toast.LENGTH_LONG).show();
        saveintent.setClass(event_edit_activity.this, eventquery_activity.class);
        event_edit_activity.this.startActivity(saveintent);
        event_edit_activity.this.finish();
        */
    }

    /**
     * Connect to the server by socket,
     * and send register information to server,
     * get return information from server.
     */
    class updateEventTASK extends AsyncTask<String, Integer, String> {
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
                String url = "http://54.187.237.119/iems5722/update_event";
                URL url_object = new URL(url);
                HttpURLConnection conn =
                        (HttpURLConnection) url_object.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                String postContent = "?event_id=" + event_id + "&event_name=" + event_name + "&event_time=" + event_time + "&event_type=" + event_type;
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



            if (status.equals("OK")) {
                Intent intent = new Intent();
                intent.setClass(event_edit_activity.this, MainActivity.class);
                event_edit_activity.this.startActivity(intent);
                event_edit_activity.this.finish();

            }
            else if (status.equals("ERROR")) {
                result_message = json.getString("message");
                Toast.makeText(event_edit_activity.this, "ERROR:"+result_message, Toast.LENGTH_SHORT).show();
            }else
            {
                Toast.makeText(event_edit_activity.this, "Something Wrong happened", Toast.LENGTH_SHORT).show();

            }
            }catch (JSONException e) {
                e.printStackTrace();
            }
            //Toast.makeText(UserRegisterActivity.this, message, Toast.LENGTH_SHORT).show();

        }
    }

}

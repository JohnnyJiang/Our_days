package hk.edu.cuhk.ie.iems5722.group4_our_days.login_register;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import hk.edu.cuhk.ie.iems5722.group4_our_days.MainActivity;
import hk.edu.cuhk.ie.iems5722.group4_our_days.R;

/**
 * Created by xuxiaohong on 20/4/16.
 */
public class CoupleRegisterActivity extends AppCompatActivity {

    EditText anniversaryET;

    String anniversary;
    String user_id1=null;
    String user_id2=null;
    String user_birthday1=null;
    String user_birthday2=null;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupleregister);
        this.setTitle("Our Days - Register");

        //get reference
        anniversaryET = (EditText) this.findViewById(R.id.anniversary);
        Button cpIB = (Button) this.findViewById(R.id.couple_reg);

        Intent intent = getIntent();
        user_id1 = intent.getStringExtra("user_id1");
        user_id2 = intent.getStringExtra("user_id2");
        user_birthday1 = intent.getStringExtra("user_birthday1");
        user_birthday2 = intent.getStringExtra("user_birthday2");


        anniversaryET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }

        });
        /**
         * 设置registerButton 的 listener
         * 用户点击
         * 如果注册信息合法，则调动注册的操作
         * 否则反馈用户注册信息不合法及原因
         */
        cpIB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                    registerAct();

            }
        });
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
                        anniversaryET.setText(date);

                    }
                }, mYear, mMonth, mDay);
        dpd.show();
    }

    /**
     * 将用户名和密码写入一个String类型，
     * 并以此为参数调用注册的操作
     */
    private void registerAct() {
        anniversary = anniversaryET.getText().toString();
        registerTASK rt = new registerTASK();
        rt.execute();


    }
    class registerTASK extends AsyncTask<String, Integer, String> {
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
                String url = "http://54.187.237.119/iems5722/couple_register";
                URL url_object = new URL(url);
                HttpURLConnection conn =
                        (HttpURLConnection) url_object.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                String postContent = "user_id1="+user_id1+"&user_id2=" + user_id2+"&anniversary="+anniversary+"&user_birthday1="+user_birthday1+"&user_birthday2="+user_birthday2;
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
        protected void onPostExecute(String message) {
			/*
			 * onPostExecute(Result)  相当于Handler 处理UI的方式，
			 * 在这里面可以使用在doInBackground 得到的结果处理操作UI。
			 * 此方法在主线程执行，任务执行的结果作为此方法的参数返回
			 */
            if (message.equals("couple register success")) {

                /**
                 * 进入login的Activity
                 */
                Intent cpregisterintent = new Intent();
                cpregisterintent.putExtra("user_id",user_id1);
                cpregisterintent.setClass(CoupleRegisterActivity.this, MainActivity.class);
                startActivity(cpregisterintent);
                CoupleRegisterActivity.this.finish();
            }
            else if (message.equals("register failure")) {
                Toast.makeText(CoupleRegisterActivity.this, "User Name " + user_id1 + " existed!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(CoupleRegisterActivity.this, "Something Wrong happened", Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(UserRegisterActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }

}

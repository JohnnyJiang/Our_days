package hk.edu.cuhk.ie.iems5722.group4_our_days.login_register;

import android.app.Activity;
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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import hk.edu.cuhk.ie.iems5722.group4_our_days.R;
import hk.edu.cuhk.ie.iems5722.group4_our_days.account.User;

/**
 * Created by xuxiaohong on 17/4/16.
 */
public class UserRegisterActivity extends AppCompatActivity {


    EditText user_idET;
    EditText user_nameET;
    EditText user_birthdayET;
    EditText user_genderET;
    EditText user_pw1ET;
    EditText user_pw2ET;


    String user_id = null;
    String user_name = null;
    String user_gender = null;
    String user_birthday = null;
    String user_pw1 = null;
    String user_pw2 = null;

    String FILENAME = "User_file";
    private int mYear, mMonth, mDay;
    private String[] single_list = {"female", "male"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_1);
        this.setTitle("Our Days - Log in");

        //get reference
        user_idET = (EditText) this.findViewById(R.id.reg1_user_id);
        user_nameET = (EditText) this.findViewById(R.id.reg1_user_name);
        user_birthdayET = (EditText) this.findViewById(R.id.reg1_user_birthday);
        user_genderET = (EditText) this.findViewById(R.id.reg1_user_gender);
        user_pw1ET = (EditText) this.findViewById(R.id.reg1_user_pw1);
        user_pw2ET = (EditText) this.findViewById(R.id.reg1_user_pw2);
        Button nextIB = (Button) this.findViewById(R.id.reg1_next);

        user_birthdayET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }

        });
        user_genderET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGenderPickerDialog();
            }
        });
        /**
         * 设置registerButton 的 listener
         * 用户点击
         * 如果注册信息合法，则调动注册的操作
         * 否则反馈用户注册信息不合法及原因
         */
        nextIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registerIsSuccess()) {
                    registerAct();
                }
            }
        });
    }
    public void showGenderPickerDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose your gender");
        builder.setSingleChoiceItems(single_list, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String str = single_list[which];
                user_genderET.setText(str);
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
                        user_birthdayET.setText(date);

                    }
                }, mYear, mMonth, mDay);
        dpd.show();
    }
    /**
     * Judge the user input is valid or not
     * @return ture -- valid
     *         false -- invalid
     */
    private boolean registerIsSuccess() {
        user_id = user_idET.getText().toString();
        user_name = user_nameET.getText().toString();
        user_birthday = user_birthdayET.getText().toString();
        user_gender = user_genderET.getText().toString();
        user_pw1 = user_pw1ET.getText().toString();
        user_pw2 = user_pw2ET.getText().toString();

        if ("".equals(user_id)) {
    		/*
    		 * Test user id which cannot be empty.
    		 */
            Toast.makeText(UserRegisterActivity.this, "UserID cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if ("".equals(user_name)) {
    		/*
    		 * Test user name which cannot be empty.
    		 */
            Toast.makeText(UserRegisterActivity.this, "User Name cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if ("".equals(user_birthday)) {
    		/*
    		 * Test birthday which cannot be empty.
    		 */
            Toast.makeText(UserRegisterActivity.this, "Birthday cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if ("".equals(user_gender)) {
    		/*
    		 * Test gender which cannot be empty.
    		 */
            Toast.makeText(UserRegisterActivity.this, "Gender cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if ("".equals(user_pw1)) {
    		/*
    		 * Test password which cannot be empty.
    		 */
            Toast.makeText(UserRegisterActivity.this, "Password cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!user_pw1.equals(user_pw2)) {
    		/*
    		 * Test the password1 must equal to password2.
    		 */
            Toast.makeText(UserRegisterActivity.this, "Passwords must be the same!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    /**
     * 将用户名和密码写入一个String类型，
     * 并以此为参数调用注册的操作
     */
    private void registerAct() {

        new registerTASK().execute();

    }

    /**
     * Record the user information.
     */
    private boolean recordUser() {
        User user = new User();
        user.setUserid(user_idET.getText().toString());
        user.setUserName(user_nameET.getText().toString());
        user.setUserbirthday(user_birthdayET.getText().toString());
        user.setUsergender(user_genderET.getText().toString());
        user.setPassword(user_pw1ET.getText().toString());
        user.setOperation("register");

        return true;
    }

    /**
     * Connect to the server by socket,
     * and send register information to server,
     * get return information from server.
     */
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
                String url = "http://54.187.237.119/iems5722/user_register";
                URL url_object = new URL(url);
                HttpURLConnection conn =
                        (HttpURLConnection) url_object.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                String postContent = "?user_id="+user_id+"&user_name=" + user_name + "&password=" + user_pw1+"&gender="+user_gender+"&birthday="+user_birthday;
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
            if (message.equals("register success")) {
                recordUser();
                saveUserInfo(user_id);
                /**
                 * 进入login的Activity
                 */
                Intent registerintent = new Intent();
                registerintent.setClass(UserRegisterActivity.this, match_activity.class);
                registerintent.putExtra("user_id1", user_id);
                registerintent.putExtra("user_birthday1",user_birthday);
                startActivity(registerintent);
                UserRegisterActivity.this.finish();
            }
            else if (message.equals("register failure")) {
                Toast.makeText(UserRegisterActivity.this,  "User Name " + user_name + " existed!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(UserRegisterActivity.this, "Something Wrong happened", Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(UserRegisterActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 存取用户名，记录用户已经登录过
     * @param userID
     */
    public void saveUserInfo(String userID) {
        try {

            JSONObject form = new JSONObject();
            try {
                form.put("key",userID);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            String rightsaveformat = form.toString();

            FileOutputStream outputStream = openFileOutput(FILENAME, Activity.MODE_PRIVATE);
            outputStream.write(rightsaveformat.getBytes());
            outputStream.flush();
            outputStream.close();
            //Toast.makeText(FileTest.this, "保存成功", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

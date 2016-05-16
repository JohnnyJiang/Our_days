package hk.edu.cuhk.ie.iems5722.group4_our_days.login_register;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import hk.edu.cuhk.ie.iems5722.group4_our_days.MainActivity;
import hk.edu.cuhk.ie.iems5722.group4_our_days.R;

/**
 * Created by xuxiaohong on 17/4/16.
 */
public class UserLoginActivity extends AppCompatActivity {

    TextView couplechatTV;
    EditText user_idET;
    EditText user_pwET;
    Button registerButton;
    Button loginButton;

    String user_id = null;
    String user_pw = null;
    String FILENAME = "User_file";
    String FILENAME_PW = "User_pw";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Our Days - Log in");

		/*
		 * 如果用户曾经登录过了
		 * 则跳过此activity，打开main activity
         */

        if (isLoginBefore()) { //已经登录过了
            initial_user_id();
            initial_user_pw();
            loginAct();
            /*Intent mainintent = new Intent();
            mainintent.putExtra("user_id",user_id);
            mainintent.setClass(UserLoginActivity.this, MainActivity.class);
            startActivity(mainintent);*/
            //UserLoginActivity.this.finish();
        }





        setContentView(R.layout.login);

        //get reference
        //couplechatTV = (TextView) this.findViewById(R.id.CoupleChat);
        user_idET = (EditText) this.findViewById(R.id.login_user_id);
        user_pwET = (EditText) this.findViewById(R.id.login_user_pw);
        loginButton = (Button) this.findViewById(R.id.LogInButton);
        registerButton = (Button) this.findViewById(R.id.RegisterButton);

        //set listener
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerintent = new Intent();
                registerintent.setClass(UserLoginActivity.this, UserRegisterActivity.class);
                startActivity(registerintent);
                UserLoginActivity.this.finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginInfoCorrect()){
                    user_id = user_idET.getText().toString();
                    user_pw = user_pwET.getText().toString();
                    loginAct();
                }
            }
        });

    }

    private boolean loginInfoCorrect(){
        user_id = user_idET.getText().toString();
        user_pw = user_pwET.getText().toString();
        if ("".equals(user_id)) {
            Toast.makeText(UserLoginActivity.this, "UserID cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }else if ("".equals(user_pw)){
            Toast.makeText(UserLoginActivity.this, "User Password cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else return true;
    }
    /**
     * 将用户名和密码写入一个String类型，
     * 并以此为参数调用注册的操作
     */
    private void loginAct() {


        String url = "http://54.187.237.119/iems5722/user_login?user_id=" + user_id + "&password="+ user_pw;
        new loginTASK().execute(url);

    }
    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return super.getSharedPreferences(name, mode);
    }
    class loginTASK extends AsyncTask<String, Integer, String> {
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
                URL url_object = new URL(params[0]);
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
            String result_message = null;
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
                saveUserInfo(user_id);
                saveUserPw(user_pw);
                Intent mainintent = new Intent();
                mainintent.setClass(UserLoginActivity.this, MainActivity.class);
                mainintent.putExtra("user_id",user_id);
                startActivity(mainintent);
                UserLoginActivity.this.finish();
            }else if(status.equals("NOT MATCH")){
                Intent matchintent = new Intent();
                matchintent.putExtra("user_id1",user_id);
                matchintent.setClass(UserLoginActivity.this,match_activity.class);
                startActivity(matchintent);
            }
            else if (status.equals("ERROR")) {
                Toast.makeText(UserLoginActivity.this,   "ERROR:"+result_message, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(UserLoginActivity.this,  "Something Wrong Happened", Toast.LENGTH_SHORT).show();
            }
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
    public void saveUserPw(String password) {
        try {

            JSONObject form = new JSONObject();
            try {
                form.put("password",password);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            String rightsaveformat = form.toString();

            FileOutputStream outputStream = openFileOutput(FILENAME_PW, Activity.MODE_PRIVATE);
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

    public void initial_user_pw(){
        String user_pw_from_file=null;
        try {
            FileInputStream inputStream = this.openFileInput(FILENAME_PW);
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
                if( json.has("password"))
                    user_pw_from_file =  json.getString("password");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (user_pw_from_file == null)
        {
            //Toast.makeText(MainActivity.this,"No Initial ID",Toast.LENGTH_LONG).show();
        }
        else
        {
            user_pw = user_pw_from_file;
            //Toast.makeText(MainActivity.this,"Yes Initial"+user_id,Toast.LENGTH_LONG).show();

        }
    }

    /**
     * 通过打开userinfo文件
     * 判断用户名存在与否
     * 来判断用户是否曾经登录过
     * @return ture - 登录过
     *         flase - 没登录过
     */
    boolean isLoginBefore(){
        String username = null;
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
                    username =  json.getString("key");
                    user_id = username;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (username == null)
            return false;
        else
            return true;
    }


}


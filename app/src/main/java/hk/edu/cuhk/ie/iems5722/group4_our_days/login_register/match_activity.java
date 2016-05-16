package hk.edu.cuhk.ie.iems5722.group4_our_days.login_register;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import hk.edu.cuhk.ie.iems5722.group4_our_days.MainActivity;
import hk.edu.cuhk.ie.iems5722.group4_our_days.R;

/**
 * Created by xuxiaohong on 26/4/16.
 */
public class match_activity extends AppCompatActivity {

    EditText match_partnerET;
    String match_partner;
    String user_id1=null;
    String user_id2=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match);
        this.setTitle("Our Days - Matching");

        Intent intent = getIntent();
        user_id1 = intent.getStringExtra("user_id1");
        //get reference
        match_partnerET = (EditText) this.findViewById(R.id.match_partner);
        Button cpIB = (Button) this.findViewById(R.id.couple_match);


        cpIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(matchInfo()) {
                    match();
                }
            }
        });

    }

    private boolean matchInfo(){
        user_id2 = match_partnerET.getText().toString();
        if("".equals(user_id2))
        {
            Toast.makeText(match_activity.this, "Partner Telephone cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }else if(user_id1.equals(user_id2))
        {
            Toast.makeText(match_activity.this,"Cannot Match with Your own!", Toast.LENGTH_LONG).show();
            return false;
        }
        else return true;
    }
    public void match(){
        match_partner = match_partnerET.getText().toString();
        new matchTASK().execute();
    }

    class matchTASK extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String results = "";
                String url = "http://54.187.237.119/iems5722/match";
                URL url_object = new URL(url);
                HttpURLConnection conn =
                        (HttpURLConnection) url_object.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                String postContent = "?user_id1="+user_id1+"&user_id2=" + user_id2;
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

                Toast.makeText(match_activity.this, "Match Success", Toast.LENGTH_SHORT).show();
                Intent cpregisterintent = new Intent();
                cpregisterintent.putExtra("user_id",user_id1);
                cpregisterintent.setClass(match_activity.this, MainActivity.class);
                startActivity(cpregisterintent);
                match_activity.this.finish();
            }
            else if (status.equals("ERROR")) {
                Toast.makeText(match_activity.this, "ERROR:"+result_message, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(match_activity.this, "Something Wrong happened", Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(UserRegisterActivity.this, message, Toast.LENGTH_SHORT).show();


        }

    }
}


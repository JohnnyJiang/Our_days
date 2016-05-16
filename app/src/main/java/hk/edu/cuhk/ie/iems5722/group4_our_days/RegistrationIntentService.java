package hk.edu.cuhk.ie.iems5722.group4_our_days;

/**
 * Created by jiang on 2016/4/4.
 */
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegistrationIntentService extends IntentService {

    String FILENAME = "User_file";
    String FILENAME_CHAT = "chatroom_file";
    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    private String user_id;
    private int user_id_upload;
    private int chatroom_id;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);

            // Subscribe to topic channels
            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        user_id = null;
        initial_user_id();
        user_id_upload = Integer.parseInt(user_id);
        initial_chatroom_id();
        new MyPostAysncTask().execute(token);
}
    public class MyPostAysncTask extends AsyncTask<String ,Void, String> {
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String ... params) {

            try {
                String results="";
                String url = "http://54.187.237.119/iems5722/submit_push_token";
                URL url_object = new URL(url);
                HttpURLConnection conn =
                        (HttpURLConnection)url_object.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                String postContent = "?chatroom_id="+ chatroom_id+ "&token=" + params[0];
                dos.write(postContent.getBytes());
                dos.flush();
                dos.close();
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                } else {
                    results = "";
                }

                return results;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
        protected void onPostExecute(String results) {
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
            //Toast.makeText(MainActivity.this, "No Initial", Toast.LENGTH_LONG).show();
        }
        else
        {
            user_id = user_id_from_file;
            //Toast.makeText(MainActivity.this,"Yes Initial"+user_id,Toast.LENGTH_LONG).show();

        }
    }
    public void initial_chatroom_id(){
        String chatroom_id_from_file=null;
        try {
            FileInputStream inputStream = this.openFileInput(FILENAME_CHAT);
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

            while (inputStream.read(bytes) != -1) {
                arrayOutputStream.write(bytes, 0, bytes.length);
            }
            inputStream.close();
            arrayOutputStream.close();

            String room = new String(arrayOutputStream.toByteArray());

            JSONObject json;
            try {
                json = new JSONObject(room);
                if( json.has("chatroom_id"))
                    chatroom_id_from_file =  json.getString("chatroom_id");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (chatroom_id_from_file == null)
        {
            //Toast.makeText(MainActivity.this,"No Initial",Toast.LENGTH_LONG).show();
        }
        else
        {
            chatroom_id = Integer.parseInt(chatroom_id_from_file);
            //Toast.makeText(MainActivity.this,"Yes Initial"+user_id,Toast.LENGTH_LONG).show();

        }
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
        }
        // [END subscribe_topics]

        }
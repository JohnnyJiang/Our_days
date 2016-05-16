package hk.edu.cuhk.ie.iems5722.group4_our_days;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by jiang on 2016/1/28.
 */
public class chat_activity extends AppCompatActivity {
    private Menu optionsMenu;
    private MyGetAsyncTask mTask;
    private int room_id;
    private String room_name;
    private int user_id;
    private int pages;
    private int total_pages;
    private String text;
    private ImageView imgShow;
    private String path;
    private String user_name=null;
    private String url;
    private final int ALBUM_OK = 1;
    private final int CUT_OK = 2;
    private String message_content="asd";
    private String partner_gender;
    Uri bitmap = null;
    String FILENAME = "User_file";
    String FILENAME_U = "user_name_file";
    String FILENAME_PG = "gender_file";
    MAdapter adapter;
    private ListView listView;
    ArrayList<message> msglist = new ArrayList<message>();
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
    SnowView snow = null;
    private ImageView partner_iconIV;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTitle("Our Days");
        setContentView(R.layout.chat_activity);

        initial_user_id();
        initial_user_name();
        initial_partner_gender();
        Intent intent = getIntent();
        //user_name = intent.getStringExtra("user_name");
        //partner_gender = intent.getStringExtra("partner_gender");
        room_id = intent.getIntExtra("chatroom_id", 0);
        ImageButton sendbutton = (ImageButton) findViewById(R.id.sendButton);
        ImageButton sendImage = (ImageButton) findViewById(R.id.sendImage);


        //imgShow = (ImageView) findViewById(R.id.imgShow);
        adapter = new MAdapter(this, msglist);
        adapter.a = user_id;
        adapter.partner_gender = partner_gender;
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        final EditText message = (EditText) findViewById(R.id.editText);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (listView.getFirstVisiblePosition() == 0 && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    Scrollload();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }

            public void Scrollload() {
                pages++;
                url = "http://54.187.237.119/iems5722/get_messages?chatroom_id=" + room_id + "&page=" + pages;
                if (pages <= total_pages) {
                    new LoadAsyncTask().execute(url);
                }

            }
        });


        room_name = "";
        this.setTitle(room_name);
        pages = 1;
        url = "http://54.187.237.119/iems5722/get_messages?chatroom_id=" + room_id + "&page=" + pages;
        mTask = new MyGetAsyncTask();
        mTask.execute(url);
        adapter.refresh(msglist);
        listView.setSelection(adapter.getCount() - 1);
        Move(0);
        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
                albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(albumIntent, ALBUM_OK);
            }
        });


        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = message.getText().toString().trim();
                text = content;
                String date = formatter.format(new Date());
                if (!content.equals("")) {
                    message msg = new message(content, user_name, date, user_id, hk.edu.cuhk.ie.iems5722.group4_our_days.message.MSG_TYPE_TEXT);
                    msglist.add(msg);
                    adapter.notifyDataSetChanged();
                    //listView.setSelection(ListView.FOCUS_DOWN);
                    new MyPostAysncTask().execute();
                    message.setText("");
                    adapter.refresh(msglist);
                    listView.setSelection(adapter.getCount()-1);
                } else
                    Toast.makeText(chat_activity.this, "No empty input!", Toast.LENGTH_LONG).show();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        // 显示或者隐藏输入法
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        return super.onTouchEvent(event);
    }

    public void Move(int count) {
        snow = (SnowView) findViewById(R.id.snow);
        snow.MAX_SNOW_COUNT=count;
        snow.bringToFront();
        snow.LoadSnowImage();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        snow.SetView(dm.heightPixels, dm.widthPixels);
        update();
    }

    public void judgement(){
        if (message_content.equals("snow")){
            Move(100);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==ALBUM_OK) {
            ContentResolver resolver = getContentResolver();
            Uri originalUri = data.getData();
            try {
                Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 0, out);
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(originalUri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path = cursor.getString(column_index);
                //Toast.makeText(chat_activity.this, path, Toast.LENGTH_SHORT).show();
                new ImagePostAysncTask().execute();
                new ImageDBAysncTask().execute(path);
                String date = formatter.format(new Date());
                message msg = new message(path, user_name, date, user_id, message.MSG_TYPE_PHOTO);
                msglist.add(msg);
                adapter.notifyDataSetChanged();
                refresh();
                listView.setSelection(adapter.getCount() - 1);
            }catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }
    public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private RefreshHandler mRedrawHandler = new RefreshHandler();

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "chat_activity Page", // TODO: Define a title for the content shown.
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

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "chat_activity Page", // TODO: Define a title for the content shown.
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

    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            //snow.addRandomSnow();
            snow.invalidate();
            sleep(100);
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    }



    public void update() {
        snow.addRandomSnow();
        mRedrawHandler.sleep(100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                back();
                break;
            case R.id.action_refresh:
                refresh();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed (){
        Intent intent = new Intent();
        intent.setClass(chat_activity.this, MainActivity.class);
        intent.putExtra("user_id", user_id);
        //Toast.makeText(chat_activity.this, "user_id:" + user_id, Toast.LENGTH_LONG).show();
        chat_activity.this.startActivity(intent);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            back();
            //这里写一下逻辑代码
            return true;//返回true则不会处理回退事件；
            //返回super.onKeyDown(keyCode, event)则表示按照Android中默认方式处理
        }
        return super.onKeyDown(keyCode, event);
    }
    public void back() {
        Intent intent = new Intent();
        intent.setClass(chat_activity.this, MainActivity.class);
        intent.putExtra("user_id", user_id);
        //Toast.makeText(chat_activity.this,"user_id:"+user_id,Toast.LENGTH_LONG).show();
        chat_activity.this.startActivity(intent);
    }

    public void refresh() {
        msglist.clear();
        String url1 = "http://54.187.237.119/iems5722/get_messages?chatroom_id=" + room_id + "&page=1";
        new MyGetAsyncTask().execute(url1);
        judgement();
    }



    public class MyGetAsyncTask extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
        }

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

        protected void onPostExecute(String results) {

            JSONObject json = null;
            try {
                json = new JSONObject(results);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                JSONObject array = json.getJSONObject("data");
                pages = array.getInt("current_page");
                total_pages = array.getInt("total_pages");
                JSONArray messagearray = array.getJSONArray("messages");
                for (int i = messagearray.length() - 1; i > -1; i--) {
                    JSONObject msgobj = messagearray.getJSONObject(i);
                    message_content = msgobj.getString("message");
                    String name = msgobj.getString("name");
                    String timestamp = msgobj.getString("timestamp");
                    String savedtime = timestamp.substring(timestamp.length() - 8, timestamp.length() - 3);
                    int user_id = msgobj.getInt("user_id");
                    int type = msgobj.getInt("type");
                    message room_content = new message(message_content, name, savedtime, user_id, type);
                    msglist.add(room_content);
                }
                adapter.notifyDataSetChanged();
                listView.setSelection(adapter.getCount() - 1);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class LoadAsyncTask extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
        }

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

        protected void onPostExecute(String results) {

            JSONObject json = null;
            try {
                json = new JSONObject(results);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                JSONObject array = json.getJSONObject("data");
                pages = array.getInt("current_page");
                JSONArray messagearray = array.getJSONArray("messages");
                for (int i = 0; i < messagearray.length(); i++) {
                    JSONObject msgobj = messagearray.getJSONObject(i);
                    String message = msgobj.getString("message");
                    String name = msgobj.getString("name");
                    String timestamp = msgobj.getString("timestamp");
                    String savedtime = timestamp.substring(timestamp.length() - 8, timestamp.length() - 3);
                    int user_id = msgobj.getInt("user_id");
                    int type = msgobj.getInt("type");
                    message room_content = new message(message, name, savedtime, user_id, type);
                    msglist.add(0, room_content);
                }
                adapter.notifyDataSetChanged();
                listView.setSelection(messagearray.length());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class MyPostAysncTask extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String results = "";
                String url = "http://54.187.237.119/iems5722/send_message";
                URL url_object = new URL(url);
                HttpURLConnection conn =
                        (HttpURLConnection) url_object.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                String postContent = "?chatroom_id=" + room_id + "&user_id=" + user_id+ "&name=" + user_name + "&message=" + text;
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
    public class ImageDBAysncTask extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String results = "";
                String url = "http://54.187.237.119/iems5722/send_message";
                URL url_object = new URL(url);
                HttpURLConnection conn =
                        (HttpURLConnection) url_object.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                String postContent = "?chatroom_id=" + room_id + "&user_id=" + user_id + "&name=" +user_name+ "&message=" + params[0];
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

    public class ImagePostAysncTask extends AsyncTask<Object, Integer, Void> {
        private ProgressDialog dialog = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String uploadedFile = path;
        File uploadFile=new File(uploadedFile);
        long totalSize = uploadFile.length();
        private static final String CHARSET = "utf-8";
        protected void onPreExecute() {
           /* dialog = new ProgressDialog(chat_activity.this);
            dialog.setMessage("正在上传...");
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setProgress(0);
            dialog.show();*/
        }

        @Override
        protected Void doInBackground(Object... arg0) {

            //String fileName = params[0].substring(params[0].lastIndexOf("/") + 1);
            long length = 0;
            int progress;
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 256 * 1024;
            try {
                FileInputStream fileInputStream = new FileInputStream(new File(
                        uploadedFile));
                String url = "http://54.187.237.119/iems5722/upload";
                URL url_object = new URL(url);
                HttpURLConnection conn =
                        (HttpURLConnection) url_object.openConnection();
                conn.setChunkedStreamingMode(128 * 1024);

                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type",
                        "multipart/form-data;boundary=" + boundary);

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                String postContent = "?chatroom_id=" + room_id + "&user_id=" + user_id + "&name=" + user_name;
                StringBuffer sb = new StringBuffer();
                sb.append(twoHyphens);
                sb.append(boundary);
                sb.append(lineEnd);
                sb.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                        + uploadedFile + "\"" + lineEnd);
                sb.append("Content-Type: application/octet-stream; charset="
                        + CHARSET + lineEnd);
                sb.append(lineEnd);
                dos.write(sb.toString().getBytes());
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    length += bufferSize;
                    progress = (int) ((length * 100) / totalSize);
                    publishProgress(progress);

                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                dos.write(postContent.getBytes());
                //publishProgress(100);
                // Responses from the server (code and message)
                int serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            //dialog.setProgress(progress[0]);
        }
        protected void onPostExecute(Void results) {
            try {
                //dialog.dismiss();
                // TODO Auto-generated method stub
            } catch (Exception e) {
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

            String userstr = arrayOutputStream.toString();


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
           // Toast.makeText(chat_activity.this,"User ID No Initial",Toast.LENGTH_LONG).show();
        }
        else
        {
            user_id = Integer.parseInt(user_id_from_file);
            //Toast.makeText(MainActivity.this,"Yes Initial"+user_id,Toast.LENGTH_LONG).show();

        }

    }

    public void initial_user_name(){
        String user_name_from_file=null;
        try {
            FileInputStream inputStream = this.openFileInput(FILENAME_U);
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            while (inputStream.read(bytes) != -1) {
                arrayOutputStream.write(bytes, 0, bytes.length);
            }
            inputStream.close();
            arrayOutputStream.close();

            String userstr = arrayOutputStream.toString();


            JSONObject json;
            try {
                json = new JSONObject(userstr);
                if( json.has("user_name"))
                    user_name_from_file =  json.getString("user_name");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(user_name_from_file == null)
        {
            //  Toast.makeText(chat_activity.this,"UserName No Initial",Toast.LENGTH_LONG).show();
        }
        else
        {
            user_name =user_name_from_file;
            //Toast.makeText(MainActivity.this,"Yes Initial"+user_id,Toast.LENGTH_LONG).show();

        }
    }

    public void initial_partner_gender(){
        String partner_gender_from_file=null;
        try {
            FileInputStream inputStream = this.openFileInput(FILENAME_PG);
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
                if( json.has("partner_gender"))
                    partner_gender_from_file =  json.getString("partner_gender");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (partner_gender_from_file == null)
        {
            //Toast.makeText(MainActivity.this,"No Initial ID",Toast.LENGTH_LONG).show();
        }
        else
        {
            partner_gender = partner_gender_from_file;
            //Toast.makeText(MainActivity.this,"Yes Initial"+user_id,Toast.LENGTH_LONG).show();

        }
    }

}


package sg.edu.nus.idmiapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GetImageByLocationActivity extends AppCompatActivity {
    String[] urlArray = new String[0];
    EditText latitude;
    EditText longitude;
    Bitmap[] bitmap;
    String[] fileArray = new String[0];
    private static final int MSG_SUCCESS = 0;
    private static final int MSG_FAILURE = 1;
    private static final int MSG_OUT_OF_CACHE = 2;
    private Thread mThread;
    private final int expireTime = 30 * 60 * 60 * 24 * 7;   // expire time of the cache files
    private final long maximumCacheSize = 1024 * 1024 * 300; // maximum local image cache size
    ArrayList<HashMap<String, String>> imageSetArray;

    private static final String serverIP = "http://ec2-54-218-40-64.us-west-2.compute.amazonaws.com:8080";

    private Handler mHandler = new Handler() {
        public void handleMessage (Message msg) {//此方法在ui线程运行
            switch(msg.what) {
                case MSG_SUCCESS:
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    ViewGroup group = (ViewGroup) findViewById(R.id.viewGroup);
                    ImageView[] imageViews = new ImageView[bitmap.length];
                    for (int i = 0; i < imageViews.length; i++) {
                        ImageView imageView = new ImageView(getApplication());
                        imageView.setLayoutParams(new AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.MATCH_PARENT, AppBarLayout.LayoutParams.WRAP_CONTENT));
                        imageViews[i] = imageView;
                        imageView.setImageBitmap(bitmap[i]);
                        group.addView(imageView);
                    }
                    break;

                case MSG_FAILURE:
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    Toast.makeText(getApplication(), "can not find the image", Toast.LENGTH_LONG).show();
                    break;
                case MSG_OUT_OF_CACHE:
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    alertView("You do not have enough space, please clear your cache firstly!");
            }
        }
    };

    /*
    listen to clear cache
     */
    public void clearCache(View view){
        delCacheFile(this.getApplicationContext().getFilesDir().getAbsolutePath(), -1);
    }

    /*
    listen to map button
     */
    public void goToMarkerView(View view){
        Intent intent = new Intent();
        intent.setClass(this, MarkerActivity.class);
        intent.putExtra("imageSetArray", this.imageSetArray);
        startActivity(intent);
    }

    public boolean delCacheFile(String path, int expireTime) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                long lastModeify = temp.lastModified();
                long current = new Date().getTime();
                Log.e("File create date", "current=" + current + "  lastModeify="
                        + lastModeify);
                if (current - lastModeify > (expireTime)) {
                    temp.delete();
                }
            }
        }
        return flag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set layout
        setContentView(R.layout.activity_get_image_by_location);

        //get fab button and set listener
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                ViewGroup group = (ViewGroup) findViewById(R.id.viewGroup);
                group.removeAllViews();
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                mThread = new Thread(getImageThread);
                mThread.start();

            }
        });

        // clear the local cache images once the application start
        int tempExpireTime = this.expireTime;
        while(this.getFolderSize(new File(this.getApplicationContext().getFilesDir().getPath())) > this.maximumCacheSize){
            this.delCacheFile(getApplicationContext().getFilesDir().getPath(), tempExpireTime);
            if(tempExpireTime > 0){
                tempExpireTime = tempExpireTime - 60 * 60 * 24;
            }
        }
    }

    Runnable getImageThread = new Runnable() {
        @Override
        public void run()
        {

            try {

                //get latitude & lognitude from text view
                latitude = (EditText)findViewById(R.id.latitude);
                longitude = (EditText)findViewById(R.id.longitude);
                String lat = latitude.getText().toString();
                String lon = longitude.getText().toString();

                int unCachedFileSize = 0;
                ArrayList<String> cachedFile = new ArrayList<>();
                ArrayList<String> unCachedFile = new ArrayList<>();

                //request images info from server by the point
//                  String path = serverIP + "/IcubeServer/enquiryImagesWithCoordinate?latitude="+lat+"&longitude="+lon;
                String path = serverIP + "/IcubeServer/getImageInfoWithCoordinate?latitude="+lat+"&longitude="+lon;
                getImageSets(path);

                if(null != imageSetArray){
                    int total = imageSetArray.size();
                    ArrayList<String> imageNameArray = new ArrayList<>();
                    for(int i = 0;i < total;i++){
                        imageNameArray.add(((HashMap<String, String>)imageSetArray.get(i)).get("imageName"));
                    }
                    urlArray = new String[total];
                    fileArray = new String[total];
                    for(int i=0;i<total;i++){
                        urlArray[i] = (String)imageNameArray.get(i);
                        String[] temp = urlArray[i].split("/");
                        fileArray[i] = temp[temp.length-1];
                        System.out.println(fileArray[i]);
                    }
                }

                if(urlArray.length != 0){
                    bitmap = new Bitmap[urlArray.length];
                    for(int i=0;i<urlArray.length;i++) {
                        // request images from image server
                        File f = new File(getApplicationContext().getFilesDir().getAbsolutePath(), fileArray[i]);
                        String filePath = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + fileArray[i];
                        if (f.exists()) {
                            cachedFile.add(filePath);
                        } else {
                            unCachedFile.add(urlArray[i]);
                            unCachedFileSize += Integer.parseInt(imageSetArray.get(i).get("size"));
                        }
                    }
                    if(unCachedFileSize + getFolderSize(new File(getApplicationContext().getFilesDir().getPath())) > maximumCacheSize){
                        mHandler.obtainMessage(MSG_OUT_OF_CACHE).sendToTarget();
                        return ;
                    } else{
                        setBitMaps(cachedFile, unCachedFile);
                    }

                    mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
                }
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
            }
        }
    };

    private void setBitMaps(List<String> cachedFile, List<String> uncachedFile) throws IOException {
        int count = 0;
        if(cachedFile.size() > 0){
            for(int i = 0; i < cachedFile.size(); i++){
                this.bitmap[count] = BitmapFactory.decodeFile(cachedFile.get(i));
                count++;
            }
        }
        if(uncachedFile.size() > 0){
            for(int i = 0; i < uncachedFile.size(); i++){
                byte[] data = getImage(uncachedFile.get(i));
                bitmap[count] = BitmapFactory.decodeByteArray(data, 0, data.length);
                String[] temp = uncachedFile.get(i).split("/");
                File file = new File(this.getApplicationContext().getFilesDir().getAbsolutePath() + "/" + temp[temp.length - 1]);
                FileOutputStream out = new FileOutputStream(file);
                bitmap[count].compress(Bitmap.CompressFormat.PNG, 90, out);
                count++;
                out.flush();
                out.close();
            }
        }
    }

    private void alertView(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }



    /*
    Get image information by url
     */
    public void getImageSets(String path) throws Exception{
        URL url = new URL(path);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(15 * 1000);
        if (con.getResponseCode() == 200){
            InputStream is = con.getInputStream();
            byte[] data = readStream(is);
            JSONArray jsonArray = new JSONArray(new String(data));
            JSONObject jsonObject;
            this.imageSetArray = new ArrayList<>();
            for(int i = 0;i < jsonArray.length();i++){
                jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("imageName",jsonObject.getString("imageName"));
                map.put("latitude",jsonObject.getString("latitude"));
                map.put("longitude",jsonObject.getString("longitude"));
                map.put("size", jsonObject.getString("size"));
                this.imageSetArray.add(map);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_get_image_by_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private static byte[] readStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            bout.write(buffer, 0, len);
        }
        bout.close();
        inputStream.close();
        return bout.toByteArray();
    }

    public byte[] getImage(String path) throws IOException {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.setReadTimeout(5*1000);
        InputStream inputStream = conn.getInputStream();
        byte[] data = new byte[0];
        try {
            data = readStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;

    }

    public long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

}

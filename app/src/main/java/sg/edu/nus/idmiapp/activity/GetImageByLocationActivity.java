package sg.edu.nus.idmiapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;

import java.util.ArrayList;

import java.util.HashMap;

import sg.edu.nus.idmiapp.R;
import sg.edu.nus.idmiapp.dao.ImageDAO;
import sg.edu.nus.idmiapp.service.CacheService;
import sg.edu.nus.idmiapp.service.ImageService;
import sg.edu.nus.idmiapp.service.impl.CacheServiceImpl;
import sg.edu.nus.idmiapp.service.impl.ImageServiceImpl;
import sg.edu.nus.idmiapp.utils.Configure;
import sg.edu.nus.idmiapp.utils.UIMessage;

public class GetImageByLocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    String[] urlArray = new String[0];
    Bitmap[] bitmap;
    String[] fileArray = new String[0];
    private Thread mThread;
    ArrayList<ImageDAO> imageSetArray;
    private GoogleApiClient mGoogleApiClient;
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    private CacheService cacheService;
    private ImageService imageService;
    private ViewGroup imageViewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set service
        this.cacheService = new CacheServiceImpl();
        this.imageService = new ImageServiceImpl();

        //set layout
        setContentView(R.layout.activity_show_image);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        GoogleMap map = mapFragment.getMap();
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng position) {
                goToMarkerView();
            }
        });
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();

        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        // clear the local cache images once the application start
        this.cacheService.clearCacheOnStart(this.getApplicationContext().getFilesDir().getPath());

    }



    private Handler mHandler = new Handler() {
        public void handleMessage (Message msg) {
            switch(msg.what) {
                case UIMessage.MSG_SUCCESS:
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    imageViewGroup = (ViewGroup) findViewById(R.id.viewGroup);
                    ImageView[] imageViews = new ImageView[bitmap.length];
                    for (int i = 0; i < imageViews.length; i++) {
                        ImageView imageView = new ImageView(getApplication());
                        imageView.setLayoutParams(new AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.MATCH_PARENT, AppBarLayout.LayoutParams.WRAP_CONTENT));
                        imageViews[i] = imageView;
                        imageView.setImageBitmap(bitmap[i]);
                        imageViewGroup.addView(imageView);
                    }
                    break;

                case UIMessage.MSG_FAILURE:
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    Toast.makeText(getApplication(), "can not find the image", Toast.LENGTH_LONG).show();
                    break;
                case UIMessage.MSG_OUT_OF_CACHE:
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    alertView("You do not have enough space, please clear your cache firstly!");
                    break;
                case UIMessage.MSG_NO_IMAGE:
                    alertView("Please get the image firstly!");
                    break;
            }
        }
    };

    /*
    listen to get image button
     */
    public void getImage(View view){
        imageViewGroup = (ViewGroup) findViewById(R.id.viewGroup);
        imageViewGroup.removeAllViews();
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        mThread = new Thread(getImageThread);
        mThread.start();
    }

    /*
    listen to clear cache button
     */
    public void clearCache(View view){
        this.cacheService.delCacheFile(this.getApplicationContext().getFilesDir().getAbsolutePath(), -1);
        this.imageSetArray = null;
        imageViewGroup = (ViewGroup) findViewById(R.id.viewGroup);
        imageViewGroup.removeAllViews();
    }

    public void goToMarkerView(){
        if(null == this.imageSetArray){
            mHandler.obtainMessage(UIMessage.MSG_NO_IMAGE).sendToTarget();
            return ;
        }
        Intent intent = new Intent();
        intent.setClass(this, MarkerActivity.class);
        intent.putExtra("imageSetArray", this.imageSetArray);
        startActivity(intent);
    }





    Runnable getImageThread = new Runnable() {
        @Override
        public void run()
        {

            try {

                int unCachedFileSize = 0;
                ArrayList<String> cachedFile = new ArrayList<>();
                ArrayList<String> unCachedFile = new ArrayList<>();

                //request images info from server by the point
//                  String path = serverIP + "/IcubeServer/enquiryImagesWithCoordinate?latitude="+lat+"&longitude="+lon;
                String path = Configure.serverIP + "/IcubeServer/getImageInfoWithCoordinate?latitude=" + Configure.lat + "&longitude=" + Configure.lon;
                imageSetArray = imageService.getImageSets(path);

                if(null != imageSetArray){
                    int total = imageSetArray.size();
                    ArrayList<String> imageNameArray = new ArrayList<>();
                    for(int i = 0;i < total;i++){
                        imageNameArray.add(imageSetArray.get(i).getImageNameWithCloudStorageURL());
                    }
                    urlArray = new String[total];
                    fileArray = new String[total];
                    for(int i=0; i < total; i++){
                        urlArray[i] = imageNameArray.get(i);
                        String[] temp = urlArray[i].split("/");
                        fileArray[i] = temp[temp.length-1];
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
                            unCachedFileSize += imageSetArray.get(i).getSize();
                        }
                    }
                    if(unCachedFileSize + cacheService.enquiryFolderSize(new File(getApplicationContext().getFilesDir().getPath())) > Configure.maximumCacheSize){
                        mHandler.obtainMessage(UIMessage.MSG_OUT_OF_CACHE).sendToTarget();
                        return ;
                    } else{
                        bitmap = imageService.getBitMaps(cachedFile, unCachedFile, getApplicationContext().getFilesDir().getPath());
                    }

                    mHandler.obtainMessage(UIMessage.MSG_SUCCESS).sendToTarget();
                }
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.obtainMessage(UIMessage.MSG_FAILURE).sendToTarget();
            }
        }
    };



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

    /*
    map init
     */
    @Override
    public void onMapReady(GoogleMap map) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);

        } else {
            Toast.makeText(this, "No Permission", Toast.LENGTH_LONG).show();
        }
    }


}

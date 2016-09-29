package sg.edu.nus.idmiapp.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import sg.edu.nus.idmiapp.R;
import sg.edu.nus.idmiapp.dao.ImageDAO;
import sg.edu.nus.idmiapp.service.CacheService;
import sg.edu.nus.idmiapp.service.ImageService;
import sg.edu.nus.idmiapp.service.impl.CacheServiceImpl;
import sg.edu.nus.idmiapp.service.impl.ImageServiceImpl;
import sg.edu.nus.idmiapp.utils.Configure;
import sg.edu.nus.idmiapp.utils.Permission;
import sg.edu.nus.idmiapp.utils.UIMessage;

public class GetImageByLocationActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
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
    private Double latitude;
    private Double longitude;
   // private Boolean isReceivePicture;
    private Timer timer;
    private TimerTask timertask;
    private GoogleMap map;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       /* if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            this.alertView("Please allow the location service in the setting");
            return ;
        }*/

        //set service
        this.cacheService = new CacheServiceImpl();
        this.imageService = new ImageServiceImpl();

        //set layout
        setContentView(R.layout.activity_show_image);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*GoogleMap map = mapFragment.getMap();

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng position) {
                goToMarkerView();
            }
        });*/
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        RelativeLayout map_button = (RelativeLayout) findViewById(R.id.map_button);
        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMarkerView();
            }
        });
        // clear the local cache images once the application start
        //check file path
        if(this.getApplicationContext().getFilesDir()!= null){
            this.cacheService.clearCacheOnStart(this.getApplicationContext().getFilesDir().getPath());
        }


      //  isReceivePicture = false;
       /* final int WHAT = 102;
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WHAT:
                        SupportMapFragment mapFragment =
                                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        //mapFragment.getMapAsync(this);
                        GoogleMap map = mapFragment.getMap();
                        break;
                }
            }
        };*/

        timertask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                //message.what = WHAT;
                message.obj = System.currentTimeMillis();
            //   handler.sendMessage(message);
                mHandler.obtainMessage(UIMessage.MSG_TIMER).sendToTarget();
            }
        };

        timer = new Timer();
        // 参数：
        // 1000，延时1秒后执行。
        // 2000，每隔2秒执行1次task。
        timer.schedule(timertask, 1000, 2000);

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
    }



    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
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
                       // addMarkersOfPicture();
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
                case UIMessage.MSG_TIMER:
                  /*  SupportMapFragment mapFragment =
                            (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    //mapFragment.getMapAsync(this);
                    GoogleMap map = mapFragment.getMap();
                    Location location = getCurrentLocation();
                    LatLng myLocation = new LatLng(location.getLatitude(),location.getLongitude());
                    //map.addMarker(new MarkerOptions().position(myLocation).title("Here you are"));
                    Log.i("latitude",location.getLatitude()+"");
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,15));*/
                    break;
            }
        }
    };

    /*
    listen to get image button
     */
    public void getImage(View view) {
        imageViewGroup = (ViewGroup) findViewById(R.id.viewGroup);
        imageViewGroup.removeAllViews();
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        mThread = new Thread(getImageThread);
        mThread.start();
    }

    /*
    listen to clear cache button
     */
    public void clearCache(View view) {
        if(this.getApplicationContext().getFilesDir()!= null) {
            this.cacheService.delCacheFile(this.getApplicationContext().getFilesDir().getAbsolutePath(), -1);
        }
        this.imageSetArray = null;
        imageViewGroup = (ViewGroup) findViewById(R.id.viewGroup);
        imageViewGroup.removeAllViews();
    }

    public void goToMarkerView() {
        if (null == this.imageSetArray) {
            mHandler.obtainMessage(UIMessage.MSG_NO_IMAGE).sendToTarget();
            return;
        }
        Intent intent = new Intent();
        intent.setClass(this, MarkerActivity.class);
        intent.putExtra("imageSetArray", this.imageSetArray);
        startActivity(intent);
    }


    Runnable getImageThread = new Runnable() {
        @Override
        public void run() {

            try {

                int unCachedFileSize = 0;
                ArrayList<String> cachedFile = new ArrayList<>();
                ArrayList<String> unCachedFile = new ArrayList<>();

                //request images info from server by the point
//                  String path = serverIP + "/IcubeServer/enquiryImagesWithCoordinate?latitude="+lat+"&longitude="+lon;
                String path = Configure.serverIP + "/IcubeServer/getImageInfoWithCoordinate?latitude=" + Configure.lat + "&longitude=" + Configure.lon;
                imageSetArray = imageService.getImageSets(path);

                if (null != imageSetArray) {
                    int total = imageSetArray.size();
                    ArrayList<String> imageNameArray = new ArrayList<>();
                    for (int i = 0; i < total; i++) {
                        imageNameArray.add(imageSetArray.get(i).getImageNameWithCloudStorageURL());
                    }
                    urlArray = new String[total];
                    fileArray = new String[total];
                    for (int i = 0; i < total; i++) {
                        urlArray[i] = imageNameArray.get(i);
                        String[] temp = urlArray[i].split("/");
                        fileArray[i] = temp[temp.length - 1];
                    }
                }

                if (urlArray.length != 0) {
                    bitmap = new Bitmap[urlArray.length];
                    if(getApplicationContext().getFilesDir()!= null) {
                        for (int i = 0; i < urlArray.length; i++) {
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
                    }
                    if (unCachedFileSize + cacheService.enquiryFolderSize(new File(getApplicationContext().getFilesDir().getPath())) > Configure.maximumCacheSize) {
                        mHandler.obtainMessage(UIMessage.MSG_OUT_OF_CACHE).sendToTarget();
                        return;
                    } else {
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

    /***
     * Get current location when the app start using android Gps
     *
     */
    public Location getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        //check permission
        if(!Permission.checkLocationPermission(this)){
            return null;
        }
        // get gps support
        Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        if (location == null) {
            //get network support
            location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        }
        return location;

    }

    /***
     * Add marker of the buildings in the map zoom the marker
     *
     */
    public void addMarkersOfPicture(){
     //   isReceivePicture = true;
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        map = mapFragment.getMap();
        for (ImageDAO item : this.imageSetArray) {

            LatLng latLng = new LatLng(Double.parseDouble(item.getLatitude()), Double.parseDouble(item.getLongitude()));
            map.addMarker(new MarkerOptions().position(latLng).title(item.getImageNameWithCloudStorageURL()));

        }
        LatLng latLng = new LatLng(Double.parseDouble(this.imageSetArray.get(0).getLatitude()), Double.parseDouble(this.imageSetArray.get(0).getLongitude()));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
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
        Location location = getCurrentLocation();
        if(null == location){
            alertView("please get location permission in system setting");
            return;
        }
       /* LatLng myLocation = new LatLng(location.getLatitude(),location.getLongitude());
        //map.addMarker(new MarkerOptions().position(myLocation).title("Here you are"));
        Log.i("latitude",location.getLatitude()+"");
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,15));*/


    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

   private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.

        }
    }
  private void handleNewLocation(Location location) {

      double currentLatitude = location.getLatitude();
      double currentLongitude = location.getLongitude();

      LatLng latLng = new LatLng(currentLatitude, currentLongitude);

      /*MarkerOptions options = new MarkerOptions()
              .position(latLng)
              .title("I am here!");
      map.addMarker(options);*/
      map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
      Log.i("latitude",location.getLatitude()+"");
  }



    /**
     * Callback called when connected to GCore. Implementation of {@link GoogleApiClient.ConnectionCallbacks}.
     */
    @Override

    public void onConnected(Bundle bundle) {
        if(!Permission.checkLocationPermission(this)){

        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
        }
    }

    /**
     * Callback called when disconnected from GCore. Implementation of {@link GoogleApiClient.ConnectionCallbacks}.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        // Do nothing
    }

    /**
     * Implementation of {@link GoogleApiClient.OnConnectionFailedListener}.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do nothing
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }


}

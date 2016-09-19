package sg.edu.nus.idmiapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import sg.edu.nus.idmiapp.R;
import sg.edu.nus.idmiapp.dao.ImageDAO;
import sg.edu.nus.idmiapp.utils.Permission;

public class MarkerActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMyLocationButtonClickListener {

    private ArrayList<ImageDAO> imageSetArray;
    private GoogleApiClient mGoogleApiClient;
    private TextView mMessageView;
    private Context mcontext;
    private int height;
    private int width;
    private Animation animation = null;
    private  View viewOfPopWindow;
    private Bitmap displayImage;
    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        mcontext = MarkerActivity.this;
        mMessageView = (TextView) findViewById(R.id.message_text);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        this.imageSetArray = (ArrayList) getIntent().getSerializableExtra("imageSetArray");
        if (null != imageSetArray) {
            Toast.makeText(this, "got image", Toast.LENGTH_LONG).show();
        }
      //  RelativeLayout mainlayout = (RelativeLayout) findViewById(R.id.mainlayout);
        RelativeLayout mapview = (RelativeLayout) findViewById(R.id.mapview);
        LinearLayout forpopwindow = (LinearLayout)findViewById(R.id.forpopwindow);
        viewOfPopWindow = forpopwindow;
        ViewGroup.LayoutParams arams = mapview.getLayoutParams();
        width = arams.width ;
        height = arams.height ;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            map.setOnMyLocationButtonClickListener(this);
        } else {
            Toast.makeText(this, "No Permission", Toast.LENGTH_LONG).show();
        }


        // Add a marker in the map
        if (null == this.imageSetArray) {
            return;
        }
        for (ImageDAO item : this.imageSetArray) {
            LatLng latLng = new LatLng(Double.parseDouble(item.getLatitude()), Double.parseDouble(item.getLongitude()));
            map.addMarker(new MarkerOptions().position(latLng).title(item.getImageNameWithCloudStorageURL()));
        }

        map.setOnMarkerClickListener(this);
        LatLng latLng = new LatLng(Double.parseDouble(this.imageSetArray.get(0).getLatitude()), Double.parseDouble(this.imageSetArray.get(0).getLongitude()));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

    }


    /**
     * Button to get current Location. This demonstrates how to get the current Location as required
     * without needing to register a LocationListener.
     */
    public void showMyLocation(View view) {
        if (mGoogleApiClient.isConnected()) {
            String msg = "Location = "
                    + LocationServices.FusedLocationApi;
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
//            getLastLocation(mGoogleApiClient)
        }
    }

    /**
     * Implementation of {@link LocationListener}.
     */
    @Override
    public void onLocationChanged(Location location) {
        mMessageView.setText("Location = " + location);
    }

    /**
     * Callback called when connected to GCore. Implementation of {@link GoogleApiClient.ConnectionCallbacks}.
     */
    @Override

    public void onConnected(Bundle connectionHint) {
        if(!Permission.checkLocationPermission(this)){
            return ;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                REQUEST,
                this);  // LocationListener
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
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(this,marker.getId(),Toast.LENGTH_LONG).show();
       // View v =  new View.OnClickListener()
       Intent intent = new Intent();

        intent.putExtra("imageName", marker.getTitle());
        intent.setClass(this,ShowImageByLocation.class);
        startActivity(intent);

       /* RelativeLayout mapview = (RelativeLayout) findViewById(R.id.mapview);
        ViewGroup.LayoutParams mParams = mapview.getLayoutParams();
        mParams.width +=700;
        mParams.height +=400;
        mapview.setLayoutParams(mParams);

        initPopWindow(viewOfPopWindow);*/

        return true;
    }
    
}
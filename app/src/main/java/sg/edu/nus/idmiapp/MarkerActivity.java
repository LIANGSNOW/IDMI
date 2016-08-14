package sg.edu.nus.idmiapp;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class MarkerActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private ArrayList<HashMap<String, String>> imageSetArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        this.imageSetArray = (ArrayList) getIntent().getSerializableExtra("imageSetArray");
        if(null != imageSetArray){
            String text = (imageSetArray.get(0)).get("imageName");
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Add a marker in the map
        if(null == this.imageSetArray){
            return ;
        }
        for(HashMap<String, String> item : this.imageSetArray){
            LatLng latLng = new LatLng(Double.parseDouble(item.get("latitude")), Double.parseDouble(item.get("longitude")));
            map.addMarker(new MarkerOptions().position(latLng).title(item.get("imageName")));
        }
        map.setOnMarkerClickListener(this);
        LatLng latLng = new LatLng(Double.parseDouble(this.imageSetArray.get(0).get("latitude")), Double.parseDouble(this.imageSetArray.get(0).get("longitude")));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(this,marker.getId(),Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.putExtra("imageName", marker.getTitle());
        intent.setClass(this,ShowImageByLocation.class);
        startActivity(intent);
        return true;
//        if (marker.equals(myMarker))
//        {
//            //handle click here
//        }
    }

}
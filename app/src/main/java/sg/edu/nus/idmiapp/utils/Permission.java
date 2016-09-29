package sg.edu.nus.idmiapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by zz on 19/9/16.
 */
public class Permission {

    public static boolean checkLocationPermission(Activity activity){
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions( activity, new String[] {  Manifest.permission.ACCESS_COARSE_LOCATION },
                    UIMessage.MSG_ACCESS_COARSE_LOCATION );
           /* ActivityCompat.requestPermissions( activity, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION },
                    UIMessage.MSG_ACCESS_FINE_LOCATION );*/
            return true;
        }
        return false;
    }

}

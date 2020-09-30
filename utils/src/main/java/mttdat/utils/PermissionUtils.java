package mttdat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class PermissionUtils {
    public static boolean isAllPermissionGranted(int[] grantResults){
        for(int grantResult : grantResults){
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    public static boolean isAllPermissionGranted(Context context, String... permissions){
        for(String permission : permissions){
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    public static boolean isPermissionNeedExplained(Activity activity, String... permissions){

        for(String permission : permissions){
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }

        return false;
    }
}

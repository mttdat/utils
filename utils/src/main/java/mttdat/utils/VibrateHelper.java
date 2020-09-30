package mttdat.utils;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibrateHelper {

    public static void vibrate(Context context, int milliseconds) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {

            // Deprecated in API 26
            v.vibrate(milliseconds);
        }
    }

    /**
     * @param vibrationEffect : The strength of the vibration. This must be a value between 1 and 255
     * */
    public static void vibrate(Context context, int milliseconds, int vibrationEffect) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(milliseconds, vibrationEffect));
        } else {

            // Deprecated in API 26
            v.vibrate(milliseconds);
        }
    }
}

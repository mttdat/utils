package mttdat.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * Created by swagsoft on 5/24/17.
 */

public class NotificationUtils {

    private static final String TAG = "NotificationUtils";
    public static final int UNKNOWN_TYPE = Integer.MIN_VALUE;

    private HashMap<Integer, Integer> ids;  // Id - Type.

    private NotificationManager notificationManager;

    public NotificationUtils(Context context){
        ids = new HashMap<>();
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static Notification newInstance(Context context, int iconRes, PendingIntent pendingIntent,
                                           String title, String body, boolean canNotDismiss, String channelId) {

        return newInstance(context, iconRes, pendingIntent, title,
                body, canNotDismiss, false, Notification.PRIORITY_DEFAULT, channelId);
    }

    public static Notification newInstance(Context context, int iconRes, PendingIntent pendingIntent,
                                           String title, String body, boolean canNotDismiss, boolean autoCancel,
                                           int importance, String channelId) {

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), iconRes);

        if(icon == null){
            return null;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(iconRes)
                .setShowWhen(false)
                .setAutoCancel(autoCancel)
                .setLargeIcon(
                        Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)                                                    // Click notification event.
                .setOngoing(canNotDismiss)                                                          // Can't swipe to dismiss.
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(channelId);
        }else {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))         // Default sound.
                    .setLights(Color.argb(1, 8, 255, 8), 1000, 700)
                    .setVibrate(new long[] { 1000, 1000})
                    .setPriority(importance);
        }

        return builder.build();
    }

    public void showForegroundNotification(Service service, Notification notification, int id){

        // Notify.
        service.startForeground(
                id,
                notification
        );

        addId(id, UNKNOWN_TYPE);
    }

    public void showNotification(Notification notification, int id){

        // Notify.
        notificationManager.notify(id, notification);
        addId(id, UNKNOWN_TYPE);
    }

    public void showNotification(Notification notification, int id, int type){

        // Notify.
        notificationManager.notify(id, notification);
        addId(id, type);
    }

    public int generateUniqueId(){

        Random r = new Random();

        // r.nextInt(Integer.MAX_VALUE - 0) + 0;
        int estimatedId = r.nextInt(Integer.MAX_VALUE);

        // Check if estimated id exists, if yes, estimate a new one.
        if(ids != null) {
            while (ids.containsKey(estimatedId)) {
                estimatedId = r.nextInt(Integer.MAX_VALUE);
            }
        }

        return estimatedId;
    }

    public void addId(int id, int type){
        if(ids == null){
            return;
        }

        ids.put(id, type);
    }

    public void removeId(int id){
        if(ids == null){
            return;
        }

        ids.remove(id);
    }

    public void closeNotificationByType(int type){

        if(ids == null){
            return;
        }

        // This will avoid ConcurrentModificationException, when trying to remove element and iterate its list.
        for(Iterator<Map.Entry<Integer, Integer>> iterator = ids.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            if(entry.getValue() == type) {
                iterator.remove();
                notificationManager.cancel(entry.getKey());
            }
        }
    }

    public void closeNotificationById(int id){
        if(ids == null){
            return;
        }

        ids.remove(id);
        notificationManager.cancel(id);
    }

    public void closeForegroundNotificationById(Service service, int id){
        if(ids == null){
            return;
        }

        ids.remove(id);

        service.stopForeground(true);
    }

    public void closeAllNotification(){
        notificationManager.cancelAll();
    }

    public void createChannelNotification(String channelId, String channelName){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }else {
            Log.i(TAG, "Your device android version doesn't support for notification channel.");
        }
    }

    public void createChannelNotification(String channelId, String channelName, int importance){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    importance
            );

            channel.setLightColor(Color.argb(1, 8, 255, 8));
            channel.setVibrationPattern(new long[] { 1000, 1000});
            channel.enableVibration(true);
            channel.enableLights(true);

            notificationManager.createNotificationChannel(channel);
        }else {
            Log.i(TAG, "Your device android version doesn't support for notification channel.");
        }
    }
}

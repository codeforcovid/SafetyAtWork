package com.safeatwork.data.cloud;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.safeatwork.R;
import com.safeatwork.common.PrefSession;

import java.util.Random;

import static com.safeatwork.model.Constants.ADMIN_ACCESS;
import static com.safeatwork.model.Constants.APP_NAME;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String TAG = APP_NAME+"_MyFirebaseMessagingService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG,remoteMessage.getNotification().getBody());
        PrefSession session = new PrefSession(this);
        String user_name = session.getUserName();
        String adminAccess = session.getAdminAccess();

        Log.d(TAG,"name "+user_name+" admin "+adminAccess);
        if(adminAccess.equals(ADMIN_ACCESS)) {
            if (!remoteMessage.getNotification().getTitle().contains(user_name)) {
                shownotification(remoteMessage.getNotification());
            }
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d(TAG,"NEW_TOKEN: "+s);
    }

    public void shownotification(RemoteMessage.Notification message){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.****.*****.***"; //your app package name

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Safe at Work");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.notification_small)
                .setContentTitle(message.getTitle())
                .setContentText(message.getBody())
                .setContentInfo("Update");

        notificationManager.notify(new Random().nextInt(),notificationBuilder.build());
    }

}
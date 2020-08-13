package com.safeatwork.bluetooth;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

public class App extends Application {
    private static final String TAG = "Covid__App";
    private static boolean activityVisible;
    public static final String CHANNEL_ID = "ALARM_SERVICE_CHANNEL";
    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
        Log.d(TAG, "activityResumed activityVisible: " + activityVisible);
    }

    public static void activityPaused() {
        activityVisible = false;
        Log.d(TAG, "activityPaused activityVisible: " + activityVisible);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Inside app to create Channels");
        /*NotificationHelper notificationHelper = new NotificationHelper(this);
        notificationHelper.createChannels();*/

        createNotificationChannnel();
    }

    private void createNotificationChannnel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alarm Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);

            NotificationHelper notificationHelper = new NotificationHelper(this);
            notificationHelper.createChannels();
        }
    }
}

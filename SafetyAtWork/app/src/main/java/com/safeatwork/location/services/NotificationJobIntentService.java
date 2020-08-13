package com.safeatwork.location.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.TaskStackBuilder;

import com.safeatwork.R;

import com.safeatwork.ui.DashBoardActivity;

import static androidx.core.app.NotificationCompat.BigTextStyle;
import static androidx.core.app.NotificationCompat.Builder;
import static androidx.core.app.NotificationCompat.DEFAULT_SOUND;

/**
 * An {@link JobIntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NotificationJobIntentService extends JobIntentService {
    private static final String TAG = "NotificationJobIntentService";
    private static final int JOB_ID = 1000;
    private static final String HAND_WASH_ID = "channel_01";
    private static final String WEAR_MASK_ID = "channel_02";
    private static Context mContext;
    private static final int NOTIFY_HAND_WASH = 1;
    private static final int NOTIFY_WEAR_MASK = 2;
    private static final int NOTIFY_IMMUNITY_DRINK = 3;

    public static void enqueueWork(Context context, Intent work) {
        mContext = context;
        enqueueWork(context, NotificationJobIntentService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        // Build notification
        if (null != intent) {
            if (intent.getAction().equals(mContext.getString(R.string.hand_wash))) {
                sendNotification(NOTIFY_HAND_WASH);
            } else if (intent.getAction().equals(mContext.getString(R.string.wear_mask))) {
                sendNotification(NOTIFY_WEAR_MASK);
            }
        }
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    private void sendNotification(int notifyType) {
        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelID;
        int title = 0, description = 0;

        switch (notifyType) {
            case NOTIFY_HAND_WASH:
                title = R.string.hand_wash;
                description = R.string.hand_wash_description;
                channelID = HAND_WASH_ID;
                break;
            case NOTIFY_WEAR_MASK:
                title = R.string.wear_mask;
                description = R.string.wear_mask_description;
                channelID = WEAR_MASK_ID;
                break;
            default:
                channelID = "";
                break;
        }

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(channelID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }

        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), DashBoardActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(DashBoardActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        Builder builder = new Builder(this, channelID);
        builder.setContentTitle(mContext.getString(title))
                .setContentText(mContext.getString(description))
                .setStyle(new BigTextStyle()
                        .bigText(mContext.getString(description)))
                .setDefaults(DEFAULT_SOUND)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(notificationPendingIntent);
        ;

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(channelID); // Channel ID
        }

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }
}

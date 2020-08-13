package com.safeatwork.location.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.safeatwork.R;
import com.safeatwork.location.services.NotificationJobIntentService;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBroadcastReceiver";

    public GeofenceBroadcastReceiver(){
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(final Context context, final Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence : geofenceList) {
            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }

        int transitionType = geofencingEvent.getGeofenceTransition();
        Log.d(TAG, "onReceive: transitionType " + transitionType);
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Intent enterIntent = new Intent(context.getString(R.string.hand_wash));
                enterIntent.setClass(context, NotificationJobIntentService.class);
                NotificationJobIntentService.enqueueWork(context, enterIntent);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Intent exitIntent = new Intent(context.getString(R.string.wear_mask));
                exitIntent.setClass(context, NotificationJobIntentService.class);
                NotificationJobIntentService.enqueueWork(context, exitIntent);
                break;
        }
    }
}

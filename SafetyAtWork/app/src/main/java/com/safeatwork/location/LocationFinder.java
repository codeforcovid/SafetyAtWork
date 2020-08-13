package com.safeatwork.location;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.safeatwork.location.receiver.GeofenceBroadcastReceiver;

import java.util.ArrayList;

public class LocationFinder {
    private static final String TAG = "LocationFinder";
    private static PendingIntent geofencePendingIntent;
    private static GeofencingClient mGeofencingClient;
    private static Context mContext;
    private static ArrayList<Geofence> mGeofences = new ArrayList<Geofence>();
    private String requestID;
    private double latitude;
    private double longitude;
    private float radius;

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getRadius() {
        return radius;
    }

    public String getRequestID() {
        return requestID;
    }

    public LocationFinder(Context context, GeofencingClient geofencingClient) {
        mContext = context;
        mGeofencingClient = geofencingClient;
    }

    private Geofence buildGeofence() {
        double latitude = this.getLatitude();
        double longitude = this.getLongitude();
        float radius = this.getRadius();

        if ((latitude != 0) && (longitude != 0) && (radius != 0)) {
            return new Geofence.Builder()
                    .setRequestId(this.getRequestID())
                    .setCircularRegion(latitude, longitude, radius)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT |
                            Geofence.GEOFENCE_TRANSITION_DWELL)
                    .setLoiteringDelay(5000)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .build();
        } else {
            Log.e(TAG, "buildGeofence failed");
        }

        return null;
    }

    private GeofencingRequest buildGeofenceRequest(Geofence geofence) {
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();
    }

    private PendingIntent geofencePendingIntent() {
        if (geofencePendingIntent == null) {
            Intent intent = new Intent(mContext, GeofenceBroadcastReceiver.class);
            geofencePendingIntent = PendingIntent.getBroadcast(
                    mContext,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return geofencePendingIntent;
    }

    public void addGeofence() {
        final Geofence geofence = (Geofence) buildGeofence();
        final GeofencingRequest geofencingRequest = (GeofencingRequest) buildGeofenceRequest(geofence);

        if (geofence != null && ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGeofencingClient.addGeofences(geofencingRequest, geofencePendingIntent())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: Geofence added...");
                            mGeofences.add(geofence);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            String errorMessage = getErrorString(e);
                            Log.d(TAG, "onFailure: Geofence added: " + errorMessage);
                        }
                    });
        }
    }

    public void removeGeofence() {
        mGeofencingClient.removeGeofences(geofencePendingIntent())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence removed...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        String errorMessage = getErrorString(e);
                        Log.d(TAG, "onFailure: Geofence removed: " + errorMessage);
                    }
                });
    }

    public String getErrorString(Exception e) {
        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            switch (apiException.getStatusCode()) {
                case GeofenceStatusCodes
                        .GEOFENCE_NOT_AVAILABLE:
                    return "GEOFENCE_NOT_AVAILABLE";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_GEOFENCES:
                    return "GEOFENCE_TOO_MANY_GEOFENCES";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    return "GEOFENCE_TOO_MANY_PENDING_INTENTS";
            }
        }
        return e.getLocalizedMessage();
    }
}

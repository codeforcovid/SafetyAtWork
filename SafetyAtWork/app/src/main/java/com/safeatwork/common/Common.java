package com.safeatwork.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Common {

    public static boolean isEmpty(String string) {
        if (string == null) {
            return true;
        }
        if (string.equals("") || string.equals("empty")) {
            return true;
        }
        return false;
    }

    static ConnectivityManager connectivityManager;

    public static boolean isOnline(Context context) {
        try {
            connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            Log.i("networkInfo", networkInfo.toString());
            if (networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return false;
    }
}

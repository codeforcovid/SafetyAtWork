package com.safeatwork.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmNotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "Covid_AlarmReceiver";
    BluetoothAdapter mBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"OnReceive: AlarmNotification "+mBluetoothAdapter.toString());
        if(mBluetoothAdapter!=null && mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.startDiscovery();
        }

    }
}

package com.safeatwork.bluetooth;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.safeatwork.common.PrefSession;
import com.safeatwork.ui.login.LoginActivity;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static android.bluetooth.BluetoothClass.Device.Major.PHONE;
import static android.bluetooth.BluetoothClass.Device.PHONE_SMART;


public class DiscoverService extends Service {

    private static final String TAG = "Covid_DiscoverService";
    BluetoothAdapter mBluetoothAdapter;
    HashMap<String, ListAdapterModel> mappedData = new HashMap<>();
    NotificationHelper notificationHelper;
    NotificationManager notificationManager;
    int selectedValue;


    private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            notificationHelper = new NotificationHelper(context);
            Log.d(TAG, "onReceive: ");
            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    Log.d(TAG, "ACTION_FOUND: ");
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                    double distance = getDistance(rssi);
                    assert device != null;
                    Log.d(TAG, "Name: " + device.getName() + "/n Address: " + device.getAddress() + "/n Rssi: " + rssi + "/n distance: " + distance);
                    BluetoothClass bluetoothClass = device.getBluetoothClass();
                    int deviceClass = bluetoothClass.getDeviceClass();
                    int majorDeviceClass = bluetoothClass.getMajorDeviceClass();
                    Log.d(TAG, "getDeviceClass: " + deviceClass);
                    Log.d(TAG, "getMajorDeviceClass: " + majorDeviceClass);
                    if (distance <= 2 && (majorDeviceClass == PHONE || deviceClass == PHONE_SMART || deviceClass == 284)) {
                        Log.d(TAG, "distance <= 2: " + distance);
                        String bluetoothName = (device.getName() == null) ? "" : "(" + device.getName() + ") ";
                        //..ListAdapterModel listAdapterModel = new ListAdapterModel(device.getName(), device.getAddress(), rssi, new DecimalFormat("0.00").format(distance));

                        //get unique notification id
                        PrefSession session = new PrefSession(context);
                        int notification_id = session.getUniqueNotifyId();

                        if (mappedData.containsKey(device.getAddress())) {
                            ListAdapterModel listAdapterModel = mappedData.get(device.getAddress());
                            int warningCount = 0;
                            if (listAdapterModel != null) {
                                warningCount = listAdapterModel.getWarningCount() + 1;
                            }
                            Log.d(TAG, "mappedData.containsKey ");
                            mappedData.replace(listAdapterModel.getAddress(), listAdapterModel,
                                    new ListAdapterModel(device.getName(), device.getAddress(), rssi, new DecimalFormat("0.00").format(distance), warningCount, listAdapterModel.getNotify_id()));
                            notificationManager.notify(listAdapterModel.getNotify_id(),
                                    notificationHelper.getNotification("Maintain Social distance", "Phone " + bluetoothName + "is nearby (Warning: " + warningCount + ").", LoginActivity.class));

                        } else {
                            ListAdapterModel listAdapterModel = new ListAdapterModel(device.getName(), device.getAddress(), rssi, new DecimalFormat("0.00").format(distance), 1, notification_id);
                            Log.d(TAG, "mappedData.containsKey else");
                            mappedData.put(listAdapterModel.getAddress(), listAdapterModel);
                            notificationManager.notify(listAdapterModel.getNotify_id(), notificationHelper.getNotification("Maintain Social distance", "Phone " + bluetoothName + "is nearby (Warning: 1)", LoginActivity.class));
                        }

                        for (Map.Entry m : mappedData.entrySet()) {
                            Log.d(TAG, "key in service: " + m.getKey() + "value: " + (m.getValue().toString()));
                        }

                        Log.d(TAG, "isActivityVisible: " + App.isActivityVisible());
                        //Send broadcast if activity is visible
//                        if (App.isActivityVisible())
                        sendBroadcast(mappedData);
                    } else if (distance > 2 && (majorDeviceClass == PHONE || deviceClass == PHONE_SMART || deviceClass == 284) && mappedData.containsKey(device.getAddress())) {
                            ListAdapterModel listAdapterModel = mappedData.get(device.getAddress());
                            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancel(listAdapterModel.getNotify_id());
                            mappedData.remove(device.getAddress());
                        sendBroadcast(mappedData);
                    }
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.d(TAG, "Discovery started");
                    Toast.makeText(getApplicationContext(), "DISCOVERY_STARTED", Toast.LENGTH_SHORT).show();
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.d(TAG, "Discovery finished again restart");
                    Toast.makeText(getApplicationContext(), "DISCOVERY_FINISHED", Toast.LENGTH_SHORT).show();
                    startAlarm(true);
                    break;

                case BluetoothAdapter.ACTION_SCAN_MODE_CHANGED:
                    int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
                    switch (mode) {
                        case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        case BluetoothAdapter.SCAN_MODE_NONE:
                            Log.d(TAG, "Discoverability Disabled, Again enabling discoverability");
                            Toast.makeText(getApplicationContext(), "Discoverability Disabled, Again enabling discoverability", Toast.LENGTH_SHORT).show();
                            if (mBluetoothAdapter.isEnabled()) enableDiscoverability();
                            break;
                    }
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            Log.d(TAG, "onReceive: STATE OFF");
                            notificationManager.notify(1, notificationHelper.getNotification("Safe at Work", "Bluetooth must be ON at all times to give social distancing updates", LoginActivity.class));
                            Log.d(TAG, "Bluetooth is off. Cancelling  discovery and cancel alarm");
                            startAlarm(false);
                            mBluetoothAdapter.cancelDiscovery();
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                            notificationManager.notify(1, notificationHelper.getNotification("Discovering devices", "Stay safe from Covid-19", LoginActivity.class));
                            mBluetoothAdapter.startDiscovery();
                            break;
                    }
                default:
                    break;

            }

        }
    };

    void startAlarm(boolean setAlarm) {
        Log.d(TAG, "Start Alarm");

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(this, AlarmNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, 0);
        Log.d(TAG, "Duration in alarm: " + selectedValue);
        if (setAlarm) {
            if (manager != null && mBluetoothAdapter.isEnabled()) {
                Log.d(TAG, "elapsedRealtime() " + SystemClock.elapsedRealtime());
                manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (selectedValue * 60 * 1000), pendingIntent);

            }
        } else {
            assert manager != null;
            Log.d(TAG, "Cancel alarm");
            manager.cancel(pendingIntent);
        }
    }

    public void sendBroadcast(HashMap<String, ListAdapterModel> mappedData) {
        Intent intent = new Intent("com.example.ADDLIST_ACTION");
        intent.putExtra("DATA_KEY", mappedData);
        sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.d(TAG, "OnStartCommand called");
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        ;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        notificationHelper = new NotificationHelper(this);
        selectedValue = intent.getIntExtra("DURATION", 1);
        Log.d(TAG, "selectedValue: " + selectedValue);

        if (mBluetoothAdapter.isEnabled()) {
            enableDiscoverability();
            mBluetoothAdapter.startDiscovery();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            this.registerReceiver(bluetoothReceiver, filter);
        }

        startForeground(1, notificationHelper.getNotification("Discovering devices", "Stay safe from Covid-19", LoginActivity.class));
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service onDestroy Called");
        unregisterReceiver(bluetoothReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    double getDistance(int rssi) {
        int txPower = -59;
        double distance = Math.pow(10d, ((double) txPower - rssi) / (10 * 2));
        Log.d(TAG, "distance calculated : " + distance);
        return distance;
    }

    private void enableDiscoverability() {
        Log.d(TAG, "enableDiscoverability() ScanMode: " + mBluetoothAdapter.getScanMode() + "Bt state: " + mBluetoothAdapter.isEnabled());
        if (mBluetoothAdapter.isEnabled() && mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            discoverableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(discoverableIntent);
        }
    }

}

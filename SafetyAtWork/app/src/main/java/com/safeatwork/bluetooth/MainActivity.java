/*
package com.safeatwork.bluetooth;


import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.safeatwork.R;

public class MainActivity extends AppCompatActivity implements Serializable, AdapterView.OnItemSelectedListener {
    private static final String TAG = "Covid_MainActivity";
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                }
            }
        }
    };
    public int mSelectedValue;
    public Set<ListAdapterModel> mBTDevices = new LinkedHashSet<>();
    public DeviceListAdapter mDeviceListAdapter;
    BluetoothAdapter mBluetoothAdapter;
    Switch mSwitch_ONOFF;
    Switch mSwitch_discoverDevices;
    ListView mLvNewDevices;
    Spinner mSpinner;
    ArrayList<ListAdapterModel> mBTDevicesArraylist = new ArrayList<>();
    SharedPreferences mSharedpreferences;
    public static final String PREFERENCE = "PREFERENCE";
    public static final String DURATIONKEY = "DURATION_KEY";

    public BroadcastReceiver addListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive:" + action);
            assert action != null;
            if (action.equals("com.example.ADDLIST_ACTION")) {
                mBTDevicesArraylist.clear();
                Map<String, ListAdapterModel> parcelMap = (HashMap<String, ListAdapterModel>) intent.getSerializableExtra("DATA_KEY");
                if (parcelMap != null) {
                    for (Map.Entry m : parcelMap.entrySet()) {
                        Log.d(TAG, "key in activity: " + m.getKey() + "value: " + (m.getValue().toString()));
                        mBTDevicesArraylist.add((ListAdapterModel) m.getValue());
                    }
                    mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevicesArraylist);
                    mLvNewDevices.setAdapter(mDeviceListAdapter);
                }
            }
        }
    };
    private Boolean mRec1Status = false;
    private Boolean mAddListRecStatus = false;

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "OnPause called");
        App.activityPaused();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Onresume Called");
        App.activityResumed();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "OnRestart called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "Onstop called");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwitch_ONOFF = findViewById(R.id.bluetoothONOFF);
        mSwitch_discoverDevices = findViewById(R.id.discoverDevices);
        mLvNewDevices = findViewById(R.id.lvNewDevices);
        mSpinner = findViewById(R.id.spinner);


        mBTDevices = new LinkedHashSet<>();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //TurnOn bluetooth switch if Bluetooth is enabled in user device.
        if (mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "Bluetooth already enabled by user so setting switch to ON");
            mSwitch_ONOFF.setChecked(true);
        }

        if (isMyServiceRunning(DiscoverService.class)) {
            mSwitch_discoverDevices.setChecked(true);
        }

        mSwitch_ONOFF.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(enableBTIntent);
                    IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                    mRec1Status = true;
                    registerReceiver(mBroadcastReceiver1, BTIntent);
                } else {
                    mBluetoothAdapter.disable();
                    IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                    mRec1Status = true;
                    registerReceiver(mBroadcastReceiver1, BTIntent);
                }
            }
        });

        mSwitch_discoverDevices.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mBluetoothAdapter.isEnabled()) {
                        Log.d(TAG, "isMyServiceRunning: " + isMyServiceRunning(DiscoverService.class));
                        if (!isMyServiceRunning(DiscoverService.class)) {
                            Intent serviceIntent = new Intent(MainActivity.this, DiscoverService.class);
                            serviceIntent.putExtra("DURATION", mSelectedValue);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                ContextCompat.startForegroundService(MainActivity.this, serviceIntent);
                            } else {
                                startService(serviceIntent);
                            }
                        }
                    } else {
                        Log.d(TAG, "Bluetooth enabled from discover devices");
                        mSwitch_ONOFF.setChecked(true);
                        mSwitch_discoverDevices.setChecked(true);
                    }
                } else {
                    Intent serviceIntent = new Intent(MainActivity.this, DiscoverService.class);
                    Log.d(TAG, "CancelDiscovery");
                    stopService(serviceIntent);
                }

            }
        });

        IntentFilter filter = new IntentFilter("com.example.ADDLIST_ACTION");
        mAddListRecStatus = true;
        registerReceiver(addListReceiver, filter);

        mSharedpreferences = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        if (mSharedpreferences.contains(DURATIONKEY)) {
            mSpinner.setSelection(mSharedpreferences.getInt(DURATIONKEY, 1));
            Log.d(TAG, "Saved duration: "+mSharedpreferences.getInt(DURATIONKEY,1));
        }
        // Spinner
        mSpinner.setOnItemSelectedListener(this);

        List<Integer> categories = new ArrayList<>();
        categories.add(1);
        categories.add(2);
        categories.add(3);
        categories.add(4);
        categories.add(5);
        categories.add(6);
        categories.add(7);
        categories.add(8);
        categories.add(9);
        categories.add(10);
        categories.add(0);

        ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(dataAdapter);

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mRec1Status)
                unregisterReceiver(mBroadcastReceiver1);
            if (mAddListRecStatus)
                unregisterReceiver(addListReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        SharedPreferences.Editor editor = mSharedpreferences.edit();
        editor.putInt(DURATIONKEY, position);
        editor.apply();
        mSelectedValue = Integer.parseInt(item);
        Log.d(TAG, "selectedValue: " + mSelectedValue);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
*/

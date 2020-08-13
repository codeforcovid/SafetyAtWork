/*
package com.safeatwork.location;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.safeatwork.R;
import com.safeatwork.location.fragment.CreateAlarmFragment;
import com.safeatwork.location.receiver.GeofenceBroadcastReceiver;
import com.safeatwork.location.services.GeocodeAddressIntentService;
import com.safeatwork.location.utils.Constants;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";

    private static final String KEY_NAME_NOTIFY = "NOTIFICATION";
    private static final String KEY_WORK_LOCATION = "WORK_LOCATION";
    private static final String KEY_HOME_LOCATION = "HOME_LOCATION";
    private static final int LAUNCH_MAPS_ACTIVITY = 1;
    private double mCurLatitude;
    private double mCurLongitude;

    private AddressResultReceiver mResultReceiver;
    private Address address;
    private int fetchType = Constants.USE_ADDRESS_NAME;

    private LocationFinder mHomeLocationFinder;
    private LocationFinder mWorkLocationFinder;
    private GeofencingClient geofencingClient;
    private float GEOFENCE_RADIUS = 100;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";
    private static boolean mIsCurrentLocation;

    private Context mContext;

    private EditText addressEdit;
    private Spinner mWorkLocation;
    private SwitchCompat mAllowNotification;

    private SharedPreferences mAppPref;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Boolean isChecked = false;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        mResultReceiver = new AddressResultReceiver(null);
        geofencingClient = LocationServices.getGeofencingClient(this);
        mContext = getApplicationContext();

        mAppPref = getApplicationContext().
                getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        mEditor = mAppPref.edit();

        //Toggle button to allow notifications for this app
        mAllowNotification = (SwitchCompat) findViewById(R.id.allowNotification);
        mAllowNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    registerGeofenceBroadcastReceiver();
                } else {
                    unregisterGeofenceBroadcastReceiver();
                }
                mEditor.putBoolean(KEY_NAME_NOTIFY, isChecked);
                mEditor.commit();
            }
        });

        isChecked = mAppPref.getBoolean(KEY_NAME_NOTIFY, false);
        Log.d(TAG, "isChecked: " + isChecked);
        mAllowNotification.setChecked(isChecked);
        if (!isChecked) {
            Log.d(TAG, "Allow notification off");
            unregisterGeofenceBroadcastReceiver();
        }

        // User address locator
        mWorkLocationFinder = (LocationFinder) new LocationFinder(mContext, geofencingClient);
        mHomeLocationFinder = (LocationFinder) new LocationFinder(mContext, geofencingClient);

        // Home location
        addressEdit = (EditText) findViewById(R.id.addressEdit);
        Log.d(TAG, "Home location: " + mAppPref.getString(KEY_HOME_LOCATION, null));
        addressEdit.setText(mAppPref.getString(KEY_HOME_LOCATION, null));

        mWorkLocation = (Spinner) findViewById(R.id.workLocation);
        Log.d(TAG, "Work location: " + mAppPref.getString(KEY_WORK_LOCATION, null));
        mWorkLocation.setPrompt(mAppPref.getString(KEY_WORK_LOCATION, null));
        // Work location click listener
        mWorkLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Object item = adapterView.getItemAtPosition(i);
                Log.d(TAG, "onItemSelected: " + item.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //TODO
            }
        });

        // Avoid keyboard during activity start for home location
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void registerGeofenceBroadcastReceiver() {
        Log.d(TAG, "registerGeofenceBroadcastReceiver");
        PackageManager pm = SettingsActivity.this.getPackageManager();
        ComponentName componentName = new ComponentName(SettingsActivity.this, GeofenceBroadcastReceiver.class);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void unregisterGeofenceBroadcastReceiver() {
        Log.d(TAG, "unregisterGeofenceBroadcastReceiver");
        PackageManager pm = SettingsActivity.this.getPackageManager();
        ComponentName componentName = new ComponentName(SettingsActivity.this, GeofenceBroadcastReceiver.class);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void onGetCurrentLocationClicked(View view) {
        Intent mapActivity = new Intent(this, MapsActivity.class);
        startActivityForResult(mapActivity, LAUNCH_MAPS_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_MAPS_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Lat: " + data.getDoubleExtra("Latitude", 0) + " Long: " +
                        data.getDoubleExtra("Longitude", 0));
                mCurLatitude = data.getDoubleExtra("Latitude", 1);
                mCurLongitude = data.getDoubleExtra("Longitude", 1);
                mIsCurrentLocation = true;
            }
        }
    }

    public void onAlarmClicked(View view) {
        setContentView(R.layout.fragment_createalarm);
        Fragment fragment = new CreateAlarmFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.schedule_alarm, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();
    }

    public void onButtonClicked(View view) {
        String workLocation = mWorkLocation.getItemAtPosition(mWorkLocation.getSelectedItemPosition()).toString();
        Log.d(TAG, "Work location: " + workLocation + " Pref: " +
                mAppPref.getString(KEY_WORK_LOCATION, null));
        if (!workLocation.equals(mAppPref.getString(KEY_WORK_LOCATION, null))) {
            //Add geofence for work location
            String location = (String) getResources().obtainTypedArray(R.array.work_location_values)
                    .getText(mWorkLocation.getSelectedItemPosition());
            String[] separated = location.split(",");
            mWorkLocationFinder.setLatitude(Double.parseDouble(separated[0]));
            mWorkLocationFinder.setLongitude(Double.parseDouble(separated[1]));
            mWorkLocationFinder.setRadius(GEOFENCE_RADIUS);
            mWorkLocationFinder.setRequestID(GEOFENCE_ID);
            Toast.makeText(mContext, "Work Location:\n" + "Latitude: " + mWorkLocationFinder.getLatitude() + "\n" +
                    "Longitude: " + mWorkLocationFinder.getLongitude(), Toast.LENGTH_LONG).show();
            mWorkLocationFinder.addGeofence();
            mEditor.putString(KEY_WORK_LOCATION, workLocation);
            mEditor.commit();
        } else {
            Toast.makeText(mContext, "Same work location as before", Toast.LENGTH_LONG).show();
        }

        if (mIsCurrentLocation && mCurLatitude != 0 && mCurLongitude != 0) {
            mHomeLocationFinder.setLatitude(mCurLatitude);
            mHomeLocationFinder.setLongitude(mCurLongitude);
            mHomeLocationFinder.setRadius(GEOFENCE_RADIUS);
            mHomeLocationFinder.setRequestID(GEOFENCE_ID);
            mHomeLocationFinder.addGeofence();
        } else {
            //Add geofence for home location
            Intent intent = new Intent(mContext, GeocodeAddressIntentService.class);
            intent.putExtra(Constants.RECEIVER, mResultReceiver);
            intent.putExtra(Constants.FETCH_TYPE_EXTRA, fetchType);
            if (fetchType == Constants.USE_ADDRESS_NAME) {
                String editText = addressEdit.getText().toString();
                if (TextUtils.isEmpty(editText)) {
                    Toast.makeText(mContext, "Please enter your home location", Toast.LENGTH_LONG).show();
                    return;
                } else if (editText.equals(mAppPref.getString(KEY_HOME_LOCATION, null))) {
                    return;
                }
                intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA, editText);
                mEditor.putString(KEY_HOME_LOCATION, editText);
                mEditor.commit();

                Log.e(TAG, "Starting Service");
                mContext.startService(intent);
            }
        }
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            if (resultCode == Constants.SUCCESS_RESULT) {
                address = resultData.getParcelable(Constants.RESULT_ADDRESS);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "Home Location:\n" + "Latitude: " + address.getLatitude() + "\n" +
                                "Longitude: " + address.getLongitude(), Toast.LENGTH_LONG).show();
                    }
                });
                mHomeLocationFinder.removeGeofence();
                mHomeLocationFinder.setLatitude(address.getLatitude());
                mHomeLocationFinder.setLongitude(address.getLongitude());
                mHomeLocationFinder.setRadius(GEOFENCE_RADIUS);
                mHomeLocationFinder.setRequestID(GEOFENCE_ID);
                mHomeLocationFinder.addGeofence();
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext,
                                resultData.getString(Constants.RESULT_DATA_KEY),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}*/

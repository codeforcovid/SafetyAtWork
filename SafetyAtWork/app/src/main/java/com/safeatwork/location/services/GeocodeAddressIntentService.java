package com.safeatwork.location.services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.safeatwork.location.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GeocodeAddressIntentService extends IntentService {
    private static final String TAG = "GeocodeAddressIntentService";
    protected ResultReceiver resultReceiver;

    public GeocodeAddressIntentService() {
        super(TAG);
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            String errorMessage = "";
            List<Address> addresses = null;
            int fetchType = intent.getIntExtra(Constants.FETCH_TYPE_EXTRA, 0);

            if (fetchType == Constants.USE_ADDRESS_NAME) {
                String name = intent.getStringExtra(Constants.LOCATION_NAME_DATA_EXTRA);
                try {
                    addresses = geocoder.getFromLocationName(name, 1);
                } catch (IOException e) {
                    errorMessage = "Service not available";
                    Log.e(TAG, errorMessage, e);
                }
            } else {
                errorMessage = "Unknown Type";
            }

            resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);
            if (addresses == null || addresses.size() == 0) {
                if (errorMessage.isEmpty()) {
                    errorMessage = "Not Found";
                }
                deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage, null);
            } else {
                for (Address address : addresses) {
                    String outputAddress = "";
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        outputAddress += " --- " + address.getAddressLine(i);
                    }
                }
                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();

                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));
                }
                Log.i(TAG, "Address Found");
                deliverResultToReceiver(Constants.SUCCESS_RESULT,
                        TextUtils.join(System.getProperty("line.separator"),
                                addressFragments), address);
            }
        }
    }

    private void deliverResultToReceiver(int resultCode, String message, Address address) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.RESULT_ADDRESS, address);
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        resultReceiver.send(resultCode, bundle);
    }
}

package com.safeatwork.bluetooth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.safeatwork.R;

import java.util.ArrayList;


public class DeviceListAdapter extends ArrayAdapter<ListAdapterModel> {

    private static final String TAG = "Covid_DeviceListAdapter";
    private LayoutInflater mLayoutInflater;
    private ArrayList<ListAdapterModel> mDevices;
    private int mViewResourceId;

    public DeviceListAdapter(Context context, int tvResourceId, ArrayList<ListAdapterModel> devices) {
        super(context, tvResourceId, devices);
        this.mDevices = devices;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = tvResourceId;
    }


    @SuppressLint("ViewHolder")
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(mViewResourceId, null);

        ListAdapterModel device = mDevices.get(position);

        if (device != null) {
            TextView deviceName = convertView.findViewById(R.id.tvDeviceName);
            TextView deviceAdress = convertView.findViewById(R.id.tvDeviceAddress);
            TextView rssi = convertView.findViewById(R.id.deviceRssi);
            TextView distance = convertView.findViewById(R.id.deviceDistance);

            Log.d(TAG, "Inside getView()");

            if (deviceName != null) {
                deviceName.setText(device.getName());
            }
            if (deviceAdress != null) {
                deviceAdress.setText(device.getAddress());
            }
            if (rssi != null) {
                rssi.setText("RSSI: " + String.valueOf(device.getRssi()));
            }
            if (distance != null) {
                distance.setText("Distance: " + device.getDistance());
            }
        }
        return convertView;
    }


}

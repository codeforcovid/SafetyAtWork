package com.safeatwork.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;


public class ListAdapterModel implements Parcelable {

    public static final Creator<ListAdapterModel> CREATOR = new Creator<ListAdapterModel>() {
        @Override
        public ListAdapterModel createFromParcel(Parcel in) {
            return new ListAdapterModel(in);
        }

        @Override
        public ListAdapterModel[] newArray(int size) {
            return new ListAdapterModel[size];
        }
    };

    private final String TAG = "Covid_ListAdapterModel";
    String name;
    String address;
    int rssi;
    String distance;
    int warningCount=0;
    int notify_id=0;

    public ListAdapterModel() {

    }

    public ListAdapterModel(String name, String address, int rssi, String distance, int warningCount,int notify_id) {
        this.name = name;
        this.address = address;
        this.rssi = rssi;
        this.distance = distance;
        this.warningCount = warningCount;
        this.notify_id = notify_id;
    }

    protected ListAdapterModel(Parcel in) {
        name = in.readString();
        address = in.readString();
        rssi = in.readInt();
        warningCount = in.readInt();
        notify_id = in.readInt();
        distance = in.readString();
    }


    public int getNotify_id() {
        return notify_id;
    }

    public void setNotify_id(int notify_id) {
        this.notify_id = notify_id;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(int warningCount) {
        this.warningCount = warningCount;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    @Override
    public String toString() {
        return "ListAdapterModel{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", rssi=" + rssi +
                ", distance='" + distance + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Log.d(TAG, "Inside WriteToParcel");
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeInt(rssi);
        parcel.writeInt(warningCount);
        parcel.writeInt(notify_id);
        parcel.writeString(distance);
    }
}

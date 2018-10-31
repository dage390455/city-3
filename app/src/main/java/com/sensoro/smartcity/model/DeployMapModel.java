package com.sensoro.smartcity.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.amap.api.maps.model.LatLng;

public class DeployMapModel implements Parcelable {
    public String address;
    public String signal;
    public long updatedTime;
    public LatLng latLng;
    public String sn;
    public int deployType;

    public DeployMapModel() {

    }

    protected DeployMapModel(Parcel in) {
        address = in.readString();
        signal = in.readString();
        updatedTime = in.readLong();
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        sn = in.readString();
        deployType = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(signal);
        dest.writeLong(updatedTime);
        dest.writeParcelable(latLng, flags);
        dest.writeString(sn);
        dest.writeInt(deployType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DeployMapModel> CREATOR = new Creator<DeployMapModel>() {
        @Override
        public DeployMapModel createFromParcel(Parcel in) {
            return new DeployMapModel(in);
        }

        @Override
        public DeployMapModel[] newArray(int size) {
            return new DeployMapModel[size];
        }
    };
}

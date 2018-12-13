package com.sensoro.smartcity.server.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class DeployDeviceDetail implements Parcelable {


    private String sn;
    private String blePassword;
    private String band;
    private List<Integer> channelMask;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getBlePassword() {
        return blePassword;
    }

    public void setBlePassword(String blePassword) {
        this.blePassword = blePassword;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public List<Integer> getChannelMask() {
        return channelMask;
    }

    public void setChannelMask(List<Integer> channelMask) {
        this.channelMask = channelMask;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sn);
        dest.writeString(this.blePassword);
        dest.writeString(this.band);
        dest.writeList(this.channelMask);
    }

    public DeployDeviceDetail() {

    }

    protected DeployDeviceDetail(Parcel in) {
        this.sn = in.readString();
        this.blePassword = in.readString();
        this.band = in.readString();
        this.channelMask = new ArrayList<Integer>();
        in.readList(this.channelMask, Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<DeployDeviceDetail> CREATOR = new Parcelable.Creator<DeployDeviceDetail>() {
        @Override
        public DeployDeviceDetail createFromParcel(Parcel source) {
            return new DeployDeviceDetail(source);
        }

        @Override
        public DeployDeviceDetail[] newArray(int size) {
            return new DeployDeviceDetail[size];
        }
    };
}

package com.sensoro.common.server.bean;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class ContractsTemplateInfo implements Serializable, Comparable<ContractsTemplateInfo> {
    private String deviceType;
    private String name;
    private String hardwareVersion;
    private int quantity;
    private int price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    @Override
    public int compareTo(@NonNull ContractsTemplateInfo o) {
        return o.deviceType.compareTo(this.deviceType);
//        return 0;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(this.deviceType);
//        dest.writeString(this.name);
//        dest.writeString(this.hardwareVersion);
//        dest.writeInt(this.quantity);
//        dest.writeInt(this.price);
//    }

//    public ContractsTemplateInfo() {
//    }
//
//    protected ContractsTemplateInfo(Parcel in) {
//        this.deviceType = in.readString();
//        this.name = in.readString();
//        this.hardwareVersion = in.readString();
//        this.quantity = in.readInt();
//        this.price = in.readInt();
//    }

//    public static final Creator<ContractsTemplateInfo> CREATOR = new Creator<ContractsTemplateInfo>() {
//        @Override
//        public ContractsTemplateInfo createFromParcel(Parcel source) {
//            return new ContractsTemplateInfo(source);
//        }
//
//        @Override
//        public ContractsTemplateInfo[] newArray(int size) {
//            return new ContractsTemplateInfo[size];
//        }
//    };
}

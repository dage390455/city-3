package com.sensoro.smartcity.server.bean;

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
}

package com.sensoro.smartcity.model;

import com.sensoro.smartcity.server.bean.DeviceInfo;

import java.io.Serializable;

public class DeployResultModel implements Serializable {
    public int resultCode;
    public int scanType;
    public long updateTime;
    public String errorMsg;
    public String sn;
    public String name;
    public String contact;
    public String phone;
    public String address;
    public int deviceStatus;
    public DeviceInfo deviceInfo;
}

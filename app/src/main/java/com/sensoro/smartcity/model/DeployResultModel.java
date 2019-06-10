package com.sensoro.smartcity.model;

import com.sensoro.common.model.DeployContactModel;
import com.sensoro.common.server.bean.DeployControlSettingData;
import com.sensoro.common.server.bean.DeviceInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeployResultModel implements Serializable {
    public int resultCode;
    public int scanType;
    public long updateTime;
    public String errorMsg;
    public String signal;
    public int deviceStatus;
    public String sn;
    public String name;
    public String contact;
    public String wxPhone;
    public String phone;
    public String address;
    public String deviceType;
    public DeployControlSettingData settingData;
    public int stationStatus;
    public DeviceInfo deviceInfo;
    public Long deployTime;
    public List<DeployContactModel> deployContactModelList = new ArrayList<>();


}

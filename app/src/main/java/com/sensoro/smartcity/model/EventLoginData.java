package com.sensoro.smartcity.model;

import java.io.Serializable;

public final class EventLoginData implements Serializable {
    public String phoneId;
    public String userId;
    public String userName;
    public String phone;
    public String roles;
    public boolean isSupperAccount;
    public boolean hasStation;
    public boolean hasContract;
    public boolean hasScanLogin;
    public boolean hasSubMerchant = true;
    public boolean hasMerchantChange = true;
    public boolean hasInspectionTaskList = false;
    public boolean hasInspectionTaskModify = false;
    public boolean hasInspectionDeviceList = false;
    public boolean hasInspectionDeviceModify = false;
    public boolean hasAlarmInfo = false;
    public boolean hasMalfunction = false;
    public boolean hasDeviceBrief = false;
    public boolean hasSignalCheck = false;
    public boolean hasSignalConfig = false;
    public boolean hasBadSignalUpload = false;
    public boolean hasDevicePositionCalibration = false;
    public boolean needAuth = false;

    @Override
    public String toString() {
        return "EventLoginData{" +
                "phoneId='" + phoneId + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", phone='" + phone + '\'' +
                ", roles='" + roles + '\'' +
                ", isSupperAccount=" + isSupperAccount +
                ", hasStation=" + hasStation +
                ", hasContract=" + hasContract +
                ", hasScanLogin=" + hasScanLogin +
                ", hasSubMerchant=" + hasSubMerchant +
                ", hasMerchantChange=" + hasMerchantChange +
                ", hasAlarmInfo=" + hasAlarmInfo +
                ", hasMalfunction=" + hasMalfunction +
                ", hasDeviceBrief=" + hasDeviceBrief +
                ", hasSignalCheck=" + hasSignalCheck +
                ", hasSignalConfig=" + hasSignalConfig +
                ", hasInspectionTaskList=" + hasInspectionTaskList +
                ", hasInspectionTaskModify=" + hasInspectionTaskModify +
                ", hasInspectionDeviceList=" + hasInspectionDeviceList +
                ", hasInspectionDeviceModify=" + hasInspectionDeviceModify +
                ", hasBadSignalUpload=" + hasBadSignalUpload +
                ", hasDevicePositionCalibration=" + hasDevicePositionCalibration +
                ", needAuth=" + needAuth +
                '}';
    }
}

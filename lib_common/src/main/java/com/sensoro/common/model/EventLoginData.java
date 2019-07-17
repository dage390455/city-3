package com.sensoro.common.model;

import java.io.Serializable;

public final class EventLoginData implements Serializable {
    public String phoneId;
    public String userId;
    public String userName;
    public String phone;
    public String roles;
    public boolean isSupperAccount;
    public boolean hasStationDeploy;
    public boolean hasContract;
    public boolean hasContractCreate;
    public boolean hasContractModify;
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
    public boolean hasForceUpload = false;
    public boolean hasDevicePositionCalibration = false;
    public boolean hasDeviceMuteShort = false;
    public boolean hasDeviceMuteLong = false;
    public boolean hasDeviceMuteTime = false;
    public boolean hasDeviceFirmwareUpdate = false;
    public boolean hasDeviceDemoMode = false;
    public boolean needAuth = false;
    public boolean hasControllerAid = false;
    public boolean hasDeviceCameraList = false;
    public boolean hasDeviceCameraDeploy = false;
    //TODO
    public boolean hasStationList = false;
    public boolean hasNameplateList = false;
    public boolean hasNameplateDeploy = false;
    public boolean hasMonitorTaskList = false;
    public boolean hasMonitorTaskConfirm = false;
//    "nameplate": [
//            "modify",
//            "deploy",
//            "list",
//            "delete",
//            "export",
//            "add"
//            ],

    @Override
    public String toString() {
        return "EventLoginData{" +
                "phoneId='" + phoneId + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", phone='" + phone + '\'' +
                ", roles='" + roles + '\'' +
                ", isSupperAccount=" + isSupperAccount +
                ", hasStationDeploy=" + hasStationDeploy +
                ", hasContract=" + hasContract +
                ", hasContractCreate=" + hasContractCreate +
                ", hasContractModify=" + hasContractModify +
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
                ", hasForceUpload=" + hasForceUpload +
                ", hasDevicePositionCalibration=" + hasDevicePositionCalibration +
                ", hasDeviceMuteShort=" + hasDeviceMuteShort +
                ", hasDeviceMuteLong=" + hasDeviceMuteLong +
                ", hasDeviceMuteTime=" + hasDeviceMuteTime +
                ", hasDeviceFirmwareUpdate=" + hasDeviceFirmwareUpdate +
                ", hasDeviceDemoMode=" + hasDeviceDemoMode +
                ", needAuth=" + needAuth +
                ", hasControllerAid=" + hasControllerAid +
                ", hasDeviceCameraList=" + hasDeviceCameraList +
                ", hasDeviceCameraDeploy=" + hasDeviceCameraDeploy +
                ", hasStationList=" + hasStationList +
                ", hasNameplateList=" + hasNameplateList +
                ", hasNameplateDeploy=" + hasNameplateDeploy +
                ", hasMonitorTaskList=" + hasMonitorTaskList +
                ", hasMonitorTaskConfirm=" + hasMonitorTaskConfirm +
                '}';
    }
}

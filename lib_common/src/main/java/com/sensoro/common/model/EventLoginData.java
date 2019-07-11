package com.sensoro.common.model;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventLoginData that = (EventLoginData) o;
        return isSupperAccount == that.isSupperAccount &&
                hasStationDeploy == that.hasStationDeploy &&
                hasContract == that.hasContract &&
                hasContractCreate == that.hasContractCreate &&
                hasContractModify == that.hasContractModify &&
                hasScanLogin == that.hasScanLogin &&
                hasSubMerchant == that.hasSubMerchant &&
                hasMerchantChange == that.hasMerchantChange &&
                hasInspectionTaskList == that.hasInspectionTaskList &&
                hasInspectionTaskModify == that.hasInspectionTaskModify &&
                hasInspectionDeviceList == that.hasInspectionDeviceList &&
                hasInspectionDeviceModify == that.hasInspectionDeviceModify &&
                hasAlarmInfo == that.hasAlarmInfo &&
                hasMalfunction == that.hasMalfunction &&
                hasDeviceBrief == that.hasDeviceBrief &&
                hasSignalCheck == that.hasSignalCheck &&
                hasSignalConfig == that.hasSignalConfig &&
                hasForceUpload == that.hasForceUpload &&
                hasDevicePositionCalibration == that.hasDevicePositionCalibration &&
                hasDeviceMuteShort == that.hasDeviceMuteShort &&
                hasDeviceMuteLong == that.hasDeviceMuteLong &&
                hasDeviceFirmwareUpdate == that.hasDeviceFirmwareUpdate &&
                hasDeviceDemoMode == that.hasDeviceDemoMode &&
                needAuth == that.needAuth &&
                hasControllerAid == that.hasControllerAid &&
                hasDeviceCameraList == that.hasDeviceCameraList &&
                hasDeviceCameraDeploy == that.hasDeviceCameraDeploy &&
                hasStationList == that.hasStationList &&
                hasNameplateList == that.hasNameplateList &&
                hasNameplateDeploy == that.hasNameplateDeploy &&
                hasMonitorTaskList == that.hasMonitorTaskList &&
                hasMonitorTaskConfirm == that.hasMonitorTaskConfirm &&
                userId.equals(that.userId) &&
                phone.equals(that.phone) &&
                roles.equals(that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, phone, roles, isSupperAccount, hasStationDeploy, hasContract, hasContractCreate, hasContractModify, hasScanLogin, hasSubMerchant, hasMerchantChange, hasInspectionTaskList, hasInspectionTaskModify, hasInspectionDeviceList, hasInspectionDeviceModify, hasAlarmInfo, hasMalfunction, hasDeviceBrief, hasSignalCheck, hasSignalConfig, hasForceUpload, hasDevicePositionCalibration, hasDeviceMuteShort, hasDeviceMuteLong, hasDeviceFirmwareUpdate, hasDeviceDemoMode, needAuth, hasControllerAid, hasDeviceCameraList, hasDeviceCameraDeploy, hasStationList, hasNameplateList, hasNameplateDeploy, hasMonitorTaskList, hasMonitorTaskConfirm);
    }

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

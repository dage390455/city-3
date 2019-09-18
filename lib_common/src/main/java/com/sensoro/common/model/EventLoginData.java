package com.sensoro.common.model;

import java.io.Serializable;

public final class EventLoginData implements Serializable {
    public String phoneId;
    public String userId;
    public String userName;
    public String phone;
    public String roles;
    public String accountId;

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
    //
    public boolean hasDeviceMuteShort = false;
    public boolean hasDeviceMuteLong = false;
    public boolean hasDeviceMuteTime = false;
    public boolean hasDeviceControlReset = false;
    public boolean hasDeviceControlPassword = false;
    public boolean hasDeviceControlView = false;
    public boolean hasDeviceControlCheck = false;
    public boolean hasDeviceControlConfig = false;
    public boolean hasDeviceControlOpen = false;
    public boolean hasDeviceControlClose = false;
    //
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
    public boolean hasIBeaconSearchDemo = false;
    //    _iBeaconSearchDemo
    public boolean hasMonitorTaskList = false;
    public boolean hasMonitorTaskConfirm = false;
    public boolean hasDeployOfflineTask = false;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EventLoginData that = (EventLoginData) o;
        boolean hasChange = false;
        try {
            hasChange = isSupperAccount == that.isSupperAccount &&
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
                    hasDeviceControlReset == that.hasDeviceControlReset &&
                    hasDeviceControlPassword == that.hasDeviceControlPassword &&
                    hasDeviceControlView == that.hasDeviceControlView &&
                    hasDeviceControlCheck == that.hasDeviceControlCheck &&
                    hasDeviceControlConfig == that.hasDeviceControlConfig &&
                    hasDeviceControlOpen == that.hasDeviceControlOpen &&
                    hasDeviceControlClose == that.hasDeviceControlClose &&
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
//                    hasControllerAid == that.hasControllerAid &&
                    hasDeviceCameraList == that.hasDeviceCameraList &&
                    hasDeviceCameraDeploy == that.hasDeviceCameraDeploy &&
                    hasStationList == that.hasStationList &&
                    hasNameplateList == that.hasNameplateList &&
                    hasNameplateDeploy == that.hasNameplateDeploy &&
                    hasMonitorTaskList == that.hasMonitorTaskList &&
                    hasMonitorTaskConfirm == that.hasMonitorTaskConfirm &&
                    hasDeployOfflineTask == that.hasDeployOfflineTask &&
                    userId.equals(that.userId) &&
                    phone.equals(that.phone) &&
                    accountId.equals(that.accountId) &&
                    roles.equals(that.roles);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasChange;
    }

//    @Override
//    public int hashCode() {
//        return Objects.hash(userId, accountId, phone, roles, isSupperAccount, hasStationDeploy, hasContract, hasContractCreate, hasContractModify, hasScanLogin, hasSubMerchant, hasMerchantChange, hasInspectionTaskList, hasInspectionTaskModify, hasInspectionDeviceList, hasInspectionDeviceModify, hasAlarmInfo, hasMalfunction, hasDeviceBrief, hasSignalCheck, hasSignalConfig, hasForceUpload, hasDevicePositionCalibration, hasDeviceMuteShort, hasDeviceMuteLong, hasDeviceFirmwareUpdate, hasDeviceDemoMode, needAuth, hasControllerAid, hasDeviceCameraList, hasDeviceCameraDeploy, hasStationList, hasNameplateList, hasNameplateDeploy, hasMonitorTaskList, hasMonitorTaskConfirm);
//    }

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
                ", hasIBeaconSearchDemo=" + hasIBeaconSearchDemo +
                ", hasMonitorTaskList=" + hasMonitorTaskList +
                ", hasMonitorTaskConfirm=" + hasMonitorTaskConfirm +
                ", accountId=" + accountId +
                ", hasDeviceControlReset=" + hasDeviceControlReset +
                ", hasDeviceControlPassword=" + hasDeviceControlPassword +
                ", hasDeviceControlView=" + hasDeviceControlView +
                ", hasDeviceControlCheck=" + hasDeviceControlCheck +
                ", hasDeviceControlConfig=" + hasDeviceControlConfig +
                ", hasDeviceControlOpen=" + hasDeviceControlOpen +
                ", hasDeviceControlClose=" + hasDeviceControlClose +
                ", hasDeployOfflineTask=" + hasDeployOfflineTask +
                '}';
    }
}

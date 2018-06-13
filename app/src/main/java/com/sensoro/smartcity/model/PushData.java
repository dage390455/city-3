package com.sensoro.smartcity.model;

import com.sensoro.smartcity.server.bean.DeviceInfo;

import java.util.List;

public class PushData {
    public boolean isAlarmStatus() {
        return isAlarmStatus;
    }

    public void setAlarmStatus(boolean alarmStatus) {
        isAlarmStatus = alarmStatus;
    }

    private boolean isAlarmStatus;

    public List<DeviceInfo> getDeviceInfoList() {
        return deviceInfoList;
    }

    public void setDeviceInfoList(List<DeviceInfo> deviceInfoList) {
        this.deviceInfoList = deviceInfoList;
    }

    private List<DeviceInfo> deviceInfoList;

    @Override
    public String toString() {
        return "isAlarmStatus = " + isAlarmStatus + "，deviceInfoList = " + deviceInfoList.size();
    }
}

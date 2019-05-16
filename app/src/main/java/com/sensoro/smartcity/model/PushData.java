package com.sensoro.smartcity.model;

import com.sensoro.common.server.bean.DeviceInfo;

import java.util.List;

public class PushData {
    public List<DeviceInfo> getDeviceInfoList() {
        return deviceInfoList;
    }

    public void setDeviceInfoList(List<DeviceInfo> deviceInfoList) {
        this.deviceInfoList = deviceInfoList;
    }

    private List<DeviceInfo> deviceInfoList;

    @Override
    public String toString() {
        return "deviceInfoList = " + deviceInfoList.size();
    }
}

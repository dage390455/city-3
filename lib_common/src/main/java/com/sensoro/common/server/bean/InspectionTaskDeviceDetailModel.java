package com.sensoro.common.server.bean;

import java.util.List;

public class InspectionTaskDeviceDetailModel {

    private int count;
    private List<InspectionTaskDeviceDetail> devices;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<InspectionTaskDeviceDetail> getDevices() {
        return devices;
    }

    public void setDevices(List<InspectionTaskDeviceDetail> devices) {
        this.devices = devices;
    }

}

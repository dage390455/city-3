package com.sensoro.smartcity.server.bean;

import java.util.List;

public class InspectionTaskDeviceModel {

    /**
     * count : 4
     * devices : [{"id":"5ba9b3b2f11db9772ee33026","name":"一个有节操的传感器","taskId":"5ba9b3b2f11db9772ee33021","sn":"01921117C6DC0A6A","deviceType":"smoke","status":0,"lonlat":[0,0],"timecost":"","tags":["栋栋真帅","杨哥救我","长寿么么哒"],"malfunction":{"tags":[],"remark":"","handle":-1}},{"id":"5ba9b3b2f11db9772ee33023","name":"一个有节操的传感器","taskId":"5ba9b3b2f11db9772ee33021","sn":"01531117C6D184F3","deviceType":"smoke","status":1,"lonlat":[0,0],"timecost":"","tags":["栋栋真帅","杨哥救我","长寿么么哒"],"malfunction":{"tags":[1,4,2,5],"remark":"","handle":0}},{"id":"5ba9b3b2f11db9772ee33024","name":"一个有节操的传感器","taskId":"5ba9b3b2f11db9772ee33021","sn":"01581117C64FA30B","deviceType":"smoke","status":1,"lonlat":[0,0],"timecost":"","tags":["栋栋真帅","杨哥救我","长寿么么哒"],"malfunction":{"tags":[1,4,2,5],"remark":"","handle":0}},{"id":"5ba9b3b2f11db9772ee33025","name":"一个有节操的传感器","taskId":"5ba9b3b2f11db9772ee33021","sn":"01601117C6E3C88D","deviceType":"smoke","status":1,"lonlat":[0,0],"timecost":"","tags":["栋栋真帅 ","杨哥救我","长寿么么哒"],"malfunction":{"tags":[1,4,2,5],"remark":"","handle":0}}]
     */

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

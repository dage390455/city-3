package com.sensoro.smartcity.server.bean;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by sensoro on 17/7/26.
 */

public class DeviceHistoryInfo implements Serializable {
    private double lonlat[];
    private long updatedTime;
    private Map<String, SensorStruct> sensorDetails;

    public double[] getLonlat() {
        return lonlat;
    }

    public void setLonlat(double[] lonlat) {
        this.lonlat = lonlat;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Map<String, SensorStruct> getSensorDetails() {
        return sensorDetails;
    }

    public void setSensorDetails(Map<String, SensorStruct> sensorDetails) {
        this.sensorDetails = sensorDetails;
    }
}

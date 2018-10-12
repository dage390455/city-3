package com.sensoro.smartcity.server.bean;

import java.util.List;

public class DeviceTypeStyles {
    private String id;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private String category;

    @Override
    public String toString() {
        return "DeviceTypeStyles{" +
                "id='" + id + '\'' +
                ", unionType='" + unionType + '\'' +
                ", mergeType='" + mergeType + '\'' +
                ", thresholdSupported='" + thresholdSupported + '\'' +
                ", intervalSupported='" + intervalSupported + '\'' +
                ", sensorTypes=" + sensorTypes +
                ", alarmReceive=" + alarmReceive +
                ", category=" + category +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUnionType() {
        return unionType;
    }

    public void setUnionType(String unionType) {
        this.unionType = unionType;
    }

    public String getMergeType() {
        return mergeType;
    }

    public void setMergeType(String mergeType) {
        this.mergeType = mergeType;
    }

    public String getThresholdSupported() {
        return thresholdSupported;
    }

    public void setThresholdSupported(String thresholdSupported) {
        this.thresholdSupported = thresholdSupported;
    }

    public String getIntervalSupported() {
        return intervalSupported;
    }

    public void setIntervalSupported(String intervalSupported) {
        this.intervalSupported = intervalSupported;
    }

    public List<String> getSensorTypes() {
        return sensorTypes;
    }

    public void setSensorTypes(List<String> sensorTypes) {
        this.sensorTypes = sensorTypes;
    }

    public boolean isAlarmReceive() {
        return alarmReceive;
    }

    public void setAlarmReceive(boolean alarmReceive) {
        this.alarmReceive = alarmReceive;
    }

    private String unionType;
    private String mergeType;
    private String thresholdSupported;
    private String intervalSupported;
    private List<String> sensorTypes;
    private boolean alarmReceive;

}

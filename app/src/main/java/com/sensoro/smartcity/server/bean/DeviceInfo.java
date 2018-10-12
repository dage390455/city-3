package com.sensoro.smartcity.server.bean;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by sensoro on 17/7/25.
 */

public class DeviceInfo implements Serializable, Comparable {

    private int id;
    private String sn;
    private String signal;
    private String name;
    private String unionType;
    private String contact;
    private String content;
    private String deviceType;
    private long updatedTime;
    private long createTime;
    private double lonlat[];
    private String sensorTypes[];
    private int status;
    private String tags[];
    private SensorInfo sensorData;
    private int interval;
    private int alarmStatus;
    private AlarmInfo alarms;
    private int _level;
    private int sort;
    private String level_display;
    private String lastUpdatedTime;
    private boolean isNewDevice;
    private boolean isPushDevice;

    private Map<String, SensorStruct> sensoroDetails;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String address;

    public DeviceInfo() {
        isPushDevice = false;
        isNewDevice = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public double[] getLonlat() {
        return lonlat;
    }

    public void setLonlat(double[] lonlat) {
        this.lonlat = lonlat;
    }

    public String[] getSensorTypes() {
        return sensorTypes;
    }

    public void setSensorTypes(String[] sensorTypes) {
        this.sensorTypes = sensorTypes;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public SensorInfo getSensorData() {
        return sensorData;
    }

    public void setSensorData(SensorInfo sensorData) {
        this.sensorData = sensorData;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(int alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    public AlarmInfo getAlarms() {
        return alarms;
    }

    public void setAlarms(AlarmInfo alarms) {
        this.alarms = alarms;
    }

    public int get_level() {
        return _level;
    }

    public void set_level(int _level) {
        this._level = _level;
    }

    public String getLevel_display() {
        return level_display;
    }

    public void setLevel_display(String level_display) {
        this.level_display = level_display;
    }

    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(String lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public String getSignal() {
        return signal;
    }

    public void setSignal(String signal) {
        this.signal = signal;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Map<String, SensorStruct> getSensoroDetails() {
        return sensoroDetails;
    }

    public void setSensoroDetails(Map<String, SensorStruct> sensoroDetails) {
        this.sensoroDetails = sensoroDetails;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUnionType() {
        return unionType;
    }

    public void setUnionType(String unionType) {
        this.unionType = unionType;
    }

    public boolean isNewDevice() {
        return isNewDevice;
    }

    public void setNewDevice(boolean newDevice) {
        isNewDevice = newDevice;
    }

    public boolean isPushDevice() {
        return isPushDevice;
    }

    public void setPushDevice(boolean pushDevice) {
        isPushDevice = pushDevice;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        DeviceInfo anotherSensorInfo = (DeviceInfo) o;

        if (this.getSort() < anotherSensorInfo.getSort()) {
            return -1;
        } else if (this.getSort() == anotherSensorInfo.getSort()) {
            if (this.getUpdatedTime() < anotherSensorInfo.getUpdatedTime()) {
                return 1;
            } else if (this.getUpdatedTime() == anotherSensorInfo.getUpdatedTime()) {
                return 0;
            } else {
                return -1;
            }
        } else {
            return 1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        try {
            if (this == obj) {
                return true;
            }
            if (obj instanceof DeviceInfo) {
                DeviceInfo deviceInfo = (DeviceInfo) obj;
                return this.sn.equalsIgnoreCase(deviceInfo.sn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int hashCode() {
        try {
            return sn.hashCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.hashCode();
    }

}

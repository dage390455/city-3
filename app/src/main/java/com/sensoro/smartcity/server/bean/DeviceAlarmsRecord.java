package com.sensoro.smartcity.server.bean;

import java.io.Serializable;

public class DeviceAlarmsRecord implements Serializable {

    /**
     * alarmStatus : 2
     * sensorTypes : installed
     * _id : 5bd6ca0ebafe726a9e127cbb
     */

    private int alarmStatus;
    private String sensorTypes;
    private String _id;

    public int getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(int alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    public String getSensorTypes() {
        return sensorTypes;
    }

    public void setSensorTypes(String sensorTypes) {
        this.sensorTypes = sensorTypes;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}

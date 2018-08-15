package com.sensoro.smartcity.server.bean;

import android.support.annotation.NonNull;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by sensoro on 17/7/26.
 */

public class SensorStruct implements Serializable, Comparable<SensorStruct> {

    private String sensorType;
    private Object value;

    private String unit;
    private int sort;

    public SensorStruct() {

    }

    public SensorStruct(String sensorType, JSONObject jsonObject) throws Exception {
        this.value = jsonObject.get("value");
        this.unit = jsonObject.getString("unit");
        this.sensorType = sensorType;
    }

    public SensorStruct(String sensorType, Object value, String unit) {
        this.sensorType = sensorType;
        this.value = value;
        this.unit = unit;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getUnit() {

        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "SensorStruct{" +
                "value=" + value +
                ", unit='" + unit + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull SensorStruct another) {
        if (this.getSort() < another.getSort()) {
            return -1;
        } else if (this.getSort() == another.getSort()) {
            return 0;
        } else {
            return 1;
        }
    }

}

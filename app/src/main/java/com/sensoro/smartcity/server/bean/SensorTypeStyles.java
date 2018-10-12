package com.sensoro.smartcity.server.bean;

public class SensorTypeStyles {
    private String id;
    private String name;
    private boolean isBool;
    private String unit;
    private int min;
    private String alarm;
    private String recovery;
    private String trueMean;
    private String falseMean;
    private int max;

    //
    @Override
    public String toString() {
        return "SensorTypeStyles{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", isBool=" + isBool +
                ", unit='" + unit + '\'' +
                ", min=" + min +
                ", alarm='" + alarm + '\'' +
                ", recovery='" + recovery + '\'' +
                ", trueMean='" + trueMean + '\'' +
                ", falseMean='" + falseMean + '\'' +
                ", max=" + max +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBool() {
        return isBool;
    }

    public void setBool(boolean bool) {
        isBool = bool;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public String getRecovery() {
        return recovery;
    }

    public void setRecovery(String recovery) {
        this.recovery = recovery;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
    public String getTrueMean() {
        return trueMean;
    }

    public void setTrueMean(String trueMean) {
        this.trueMean = trueMean;
    }

    public String getFalseMean() {
        return falseMean;
    }

    public void setFalseMean(String falseMean) {
        this.falseMean = falseMean;
    }


}

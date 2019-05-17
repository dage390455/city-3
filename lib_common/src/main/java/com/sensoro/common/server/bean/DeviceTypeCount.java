package com.sensoro.common.server.bean;

/**
 * Created by sensoro on 17/12/6.
 */

public class DeviceTypeCount {

    private int alarm;
    private int inactive;
    private int normal;
    private int offline;
    private int malfunction;

    public int getMalfunction() {
        return malfunction;
    }

    public void setMalfunction(int malfunction) {
        this.malfunction = malfunction;
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }

    public int getInactive() {
        return inactive;
    }

    public void setInactive(int inactive) {
        this.inactive = inactive;
    }

    public int getNormal() {
        return normal;
    }

    public void setNormal(int normal) {
        this.normal = normal;
    }

    public int getOffline() {
        return offline;
    }

    public void setOffline(int offline) {
        this.offline = offline;
    }
}

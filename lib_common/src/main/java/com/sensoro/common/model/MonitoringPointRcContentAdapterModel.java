package com.sensoro.common.model;


import com.sensoro.common.R;

public class MonitoringPointRcContentAdapterModel {
    public int statusColorId;
    public String content;
    public String unit;
    public String name;

    public boolean hasAlarmStatus() {
        return R.color.sensoro_alarm == statusColorId;
    }
}

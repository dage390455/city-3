package com.sensoro.smartcity.adapter.model;

import com.sensoro.smartcity.R;

public class MonitoringPointRcContentAdapterModel {
    public int statusColorId;
    public String content;
    public String unit;
    public String name;

    public boolean hasAlarmStatus() {
        return R.color.sensoro_alarm == statusColorId;
    }
}

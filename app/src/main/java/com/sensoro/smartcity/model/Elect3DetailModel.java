package com.sensoro.smartcity.model;

import com.sensoro.smartcity.R;

public class Elect3DetailModel {
    public int index;
    public String text;
    public int textColor;
    public int backgroundColor;

    public boolean hasAlarmStatus() {
        return R.color.c_922c2c == textColor;
    }
}

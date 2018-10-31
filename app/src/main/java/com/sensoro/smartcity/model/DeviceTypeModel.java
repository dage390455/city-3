package com.sensoro.smartcity.model;

import android.support.annotation.DrawableRes;

import java.util.List;

public class DeviceTypeModel {
    public String name;

    public @DrawableRes
    int iconRes;
    //请求字段
    public String requestType;
    //过滤字段
    public String matcherType;
    public List<String> deviceTypes;

    public DeviceTypeModel(String name, int iconRes, String requestType, String matcherType) {
        this.name = name;
        this.iconRes = iconRes;
        this.requestType = requestType;
        this.matcherType = matcherType;
    }

    public DeviceTypeModel() {
    }
}

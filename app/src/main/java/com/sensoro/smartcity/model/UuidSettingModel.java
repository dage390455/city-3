package com.sensoro.smartcity.model;

public class UuidSettingModel {
    public String name;
    public String uuid;
    public boolean isCheck;

    public UuidSettingModel() {
    }

    public UuidSettingModel(String name, String uuid) {
        this.name = name;
        this.uuid = uuid;
    }
}

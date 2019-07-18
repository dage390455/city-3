package com.sensoro.smartcity.model;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class UuidSettingModel implements Serializable {
    public String name;
    public String uuid;
    public boolean isCheck;

    public UuidSettingModel() {
    }

    public UuidSettingModel(String name, String uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        try {
            if (obj instanceof UuidSettingModel) {
                return this.uuid.equals(((UuidSettingModel) obj).uuid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.equals(obj);
    }
}

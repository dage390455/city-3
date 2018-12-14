package com.sensoro.smartcity.server.bean;

import java.io.Serializable;

public class DeployContralSettingData implements Serializable {
    private int initValue;
    public int getInitValue() {
        return initValue;
    }

    public void setInitValue(int initValue) {
        this.initValue = initValue;
    }
}

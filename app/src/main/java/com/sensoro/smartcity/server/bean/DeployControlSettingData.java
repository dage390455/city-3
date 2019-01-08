package com.sensoro.smartcity.server.bean;

import java.io.Serializable;

public class DeployControlSettingData implements Serializable {
    private int initValue;
    private Double diameterValue;
    public int getInitValue() {
        return initValue;
    }

    public void setInitValue(int initValue) {
        this.initValue = initValue;
    }

    public Double getDiameterValue() {
        return diameterValue;
    }

    public void setDiameterValue(Double diameterValue) {
        this.diameterValue = diameterValue;
    }
}

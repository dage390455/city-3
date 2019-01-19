package com.sensoro.smartcity.server.bean;

import java.io.Serializable;

public class DeployControlSettingData implements Serializable {
    private int initValue;
    // 线径
    private Double diameterValue;

    // 线材  0 铜 1 铝
    private int wireMaterial;

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

    public int getWireMaterial() {
        return wireMaterial;
    }

    public void setWireMeterial(int wireMaterial) {
        this.wireMaterial = wireMaterial;
    }
}

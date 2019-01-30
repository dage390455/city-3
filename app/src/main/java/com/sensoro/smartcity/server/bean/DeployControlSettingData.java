package com.sensoro.smartcity.server.bean;

import java.io.Serializable;

public class DeployControlSettingData implements Serializable {
    //部署特殊处理
    private int initValue;
    // 线径
    private Double wireDiameter;

    // 线材  0 铜 1 铝
    private int wireMaterial;

    public int getSwitchSpec() {
        return initValue;
    }

    public void setInitValue(int switchSpec) {
        this.initValue = switchSpec;
    }

    public Double getWireDiameter() {
        return wireDiameter;
    }

    public void setWireDiameter(Double wireDiameter) {
        this.wireDiameter = wireDiameter;
    }

    public int getWireMaterial() {
        return wireMaterial;
    }

    public void setWireMaterial(int wireMaterial) {
        this.wireMaterial = wireMaterial;
    }
}

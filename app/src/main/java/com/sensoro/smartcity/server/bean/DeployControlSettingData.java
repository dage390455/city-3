package com.sensoro.smartcity.server.bean;

import java.io.Serializable;

public class DeployControlSettingData implements Serializable {
    private int switchSpec;
    // 线径
    private Double wireDiameter;

    // 线材  0 铜 1 铝
    private int wireMaterial;

    public int getSwitchSpec() {
        return switchSpec;
    }

    public void setInitValue(int switchSpec) {
        this.switchSpec = switchSpec;
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

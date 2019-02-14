package com.sensoro.smartcity.server.bean;

import java.io.Serializable;

public class DeployControlSettingData implements Serializable {
    //部署特殊处理
    private int switchSpec;
    // 线径
    private Double wireDiameter;

    // 线材  0 铜 1 铝
    private Integer wireMaterial;

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

    public Integer getWireMaterial() {
        return wireMaterial;
    }

    public void setWireMaterial(Integer wireMaterial) {
        this.wireMaterial = wireMaterial;
    }
}

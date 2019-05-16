package com.sensoro.common.server.bean;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class DeployControlSettingData implements Serializable {
    //此字段不进行序列化
    @Expose(serialize = false, deserialize = false)
    private Integer inputValue;
    //部署特殊处理
    private Integer switchSpec;
    // 线径
    private Double wireDiameter;

    // 线材  0 铜 1 铝
    private Integer wireMaterial;

    public Integer getSwitchSpec() {
        return switchSpec;
    }

    public void setSwitchSpec(Integer switchSpec) {
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

    public Integer getInputValue() {
        return inputValue;
    }

    public void setInputValue(Integer inputValue) {
        this.inputValue = inputValue;
    }
}

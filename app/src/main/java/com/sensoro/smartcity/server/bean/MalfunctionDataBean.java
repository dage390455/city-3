package com.sensoro.smartcity.server.bean;

import java.io.Serializable;

public class MalfunctionDataBean implements Serializable {
    /**
     * circuitShort : {"description":"传感器短路","typeDescription":"circuitShort","type":6}
     * lowVoltage : {"description":"低电","typeDescription":"lowVoltage","type":5}
     * disassembly : {"description":"被拆卸","typeDescription":"disassembly","type":4}
     * description : diy msg
     */

    private String description;
    private String typeDescription;
    private int type;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

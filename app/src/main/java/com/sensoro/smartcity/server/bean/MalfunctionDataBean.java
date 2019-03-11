package com.sensoro.smartcity.server.bean;

import java.io.Serializable;
import java.util.Map;

public class MalfunctionDataBean implements Serializable {

    private String description;
    private String typeDescription;
    private int type;
    private Map<String, MalfunctionDataBean> details;

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

    public Map<String, MalfunctionDataBean> getDetails() {
        return details;
    }

    public void setDetails(Map<String, MalfunctionDataBean> details) {
        this.details = details;
    }
}

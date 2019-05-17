package com.sensoro.common.server.bean;

import java.io.Serializable;

public class DeviceCameraUserManagerBean implements Serializable {
    /**
     * contacts : 15110041945
     * name : 高鹏
     */

    private String contacts;
    private String name;

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

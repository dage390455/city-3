package com.sensoro.common.model;

import java.io.Serializable;

public class DeviceNotificationBean implements Serializable {

    /**
     * types : phone
     */

    private String types;
    private String contact;
    private String content;

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
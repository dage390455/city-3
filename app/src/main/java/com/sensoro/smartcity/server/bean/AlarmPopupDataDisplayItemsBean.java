package com.sensoro.smartcity.server.bean;

public class AlarmPopupDataDisplayItemsBean {
    /**
     * refer : reason
     * require : true
     */

    private String id;
    private boolean require;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isRequire() {
        return require;
    }

    public void setRequire(boolean require) {
        this.require = require;
    }
}

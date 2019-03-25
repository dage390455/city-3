package com.sensoro.smartcity.server.bean;

import java.io.Serializable;
import java.util.Map;

public class AlarmPopupDataBean implements Serializable {
    private Map<String, AlarmPopupDataConfigBean> config;
    private Map<String, AlarmPopupDataDisplayBean> display;

    public Map<String, AlarmPopupDataConfigBean> getConfig() {
        return config;
    }

    public void setConfig(Map<String, AlarmPopupDataConfigBean> config) {
        this.config = config;
    }

    public Map<String, AlarmPopupDataDisplayBean> getDisplay() {
        return display;
    }

    public void setDisplay(Map<String, AlarmPopupDataDisplayBean> display) {
        this.display = display;
    }
}

package com.sensoro.smartcity.server.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class AlarmPopupDataBean implements Serializable {
    private Map<String, AlarmPopupDataConfigBean> config;
    private List<AlarmPopupDataDisplayBean> display;

    public Map<String, AlarmPopupDataConfigBean> getConfig() {
        return config;
    }

    public void setConfig(Map<String, AlarmPopupDataConfigBean> config) {
        this.config = config;
    }

    public List<AlarmPopupDataDisplayBean> getDisplay() {
        return display;
    }

    public void setDisplay(List<AlarmPopupDataDisplayBean> display) {
        this.display = display;
    }
}

package com.sensoro.common.server.bean;

import java.io.Serializable;
import java.util.List;

public class AlarmPopupDataGroupsBean implements Serializable {
    private List<Integer> displayStatus;
    private List<String> mergeTypes;
    private List<String> sensorTypes;
    private List<AlarmPopupDataLabelsBean> labels;

    public List<Integer> getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(List<Integer> displayStatus) {
        this.displayStatus = displayStatus;
    }

    public List<String> getMergeTypes() {
        return mergeTypes;
    }

    public void setMergeTypes(List<String> mergeTypes) {
        this.mergeTypes = mergeTypes;
    }

    public List<String> getSensorTypes() {
        return sensorTypes;
    }

    public void setSensorTypes(List<String> sensorTypes) {
        this.sensorTypes = sensorTypes;
    }

    public List<AlarmPopupDataLabelsBean> getLabels() {
        return labels;
    }

    public void setLabels(List<AlarmPopupDataLabelsBean> labels) {
        this.labels = labels;
    }
}

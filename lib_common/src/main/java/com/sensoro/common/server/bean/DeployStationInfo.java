package com.sensoro.common.server.bean;

import java.util.List;

public class DeployStationInfo {
    private String _id;
    private String appId;
    //    private UsersBean users;
    private String sn;
    private String status;
    private int normalStatus;
    private String type;
    private String firmwareVersion;
    private String hardwareVersion;
    private String netacm;
    private String name;
    private long updatedTime;
    private String id;
    private List<Double> lonlatLabel;
    private List<Double> lonlat;
    private List<String> tags;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNormalStatus() {
        return normalStatus;
    }

    public void setNormalStatus(int normalStatus) {
        this.normalStatus = normalStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getNetacm() {
        return netacm;
    }

    public void setNetacm(String netacm) {
        this.netacm = netacm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Double> getLonlatLabel() {
        return lonlatLabel;
    }

    public void setLonlatLabel(List<Double> lonlatLabel) {
        this.lonlatLabel = lonlatLabel;
    }

    public List<Double> getLonlat() {
        return lonlat;
    }

    public void setLonlat(List<Double> lonlat) {
        this.lonlat = lonlat;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }


    @Override
    public String toString() {
        return "DeployStationInfo{" +
                "_id='" + _id + '\'' +
                ", appId='" + appId + '\'' +
                ", sn='" + sn + '\'' +
                ", status='" + status + '\'' +
                ", normalStatus=" + normalStatus +
                ", type='" + type + '\'' +
                ", firmwareVersion='" + firmwareVersion + '\'' +
                ", hardwareVersion='" + hardwareVersion + '\'' +
                ", netacm='" + netacm + '\'' +
                ", name='" + name + '\'' +
                ", updatedTime=" + updatedTime +
                ", id='" + id + '\'' +
                ", lonlatLabel=" + lonlatLabel +
                ", lonlat=" + lonlat +
                ", tags=" + tags +
                '}';
    }
}

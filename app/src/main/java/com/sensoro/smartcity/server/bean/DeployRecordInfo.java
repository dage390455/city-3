package com.sensoro.smartcity.server.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DeployRecordInfo implements Serializable {


    private String _id;
    private String id;
    private int count;
    private String sn;
    private String deviceName;
    private long createdTime;
    private String deployStaff;
    private NotificationBean notification;
    private String signalQuality;
    private String deviceType;
    private String deviceOwners;
    private String owners;
    private String appId;
    private String unionType;
    private String wxPhone;
    private List<String> tags;
    private List<Double> lonlat;
    private List<String> deployPics;
    private Map<String, DeployControlSettingData> config;


    public Map<String, DeployControlSettingData> getConfig() {
        return config;
    }

    public void setConfig(Map<String, DeployControlSettingData> config) {
        this.config = config;
    }

    public String getWxPhone() {
        return wxPhone;
    }

    public void setWxPhone(String wxPhone) {
        this.wxPhone = wxPhone;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public String getDeployStaff() {
        return deployStaff;
    }

    public void setDeployStaff(String deployStaff) {
        this.deployStaff = deployStaff;
    }

    public NotificationBean getNotification() {
        return notification;
    }

    public void setNotification(NotificationBean notification) {
        this.notification = notification;
    }

    public String getSignalQuality() {
        return signalQuality;
    }

    public void setSignalQuality(String signalQuality) {
        this.signalQuality = signalQuality;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceOwners() {
        return deviceOwners;
    }

    public void setDeviceOwners(String deviceOwners) {
        this.deviceOwners = deviceOwners;
    }

    public String getOwners() {
        return owners;
    }

    public void setOwners(String owners) {
        this.owners = owners;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUnionType() {
        return unionType;
    }

    public void setUnionType(String unionType) {
        this.unionType = unionType;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Double> getLonlat() {
        return lonlat;
    }

    public void setLonlat(List<Double> lonlat) {
        this.lonlat = lonlat;
    }

    public List<String> getDeployPics() {
        return deployPics;
    }

    public void setDeployPics(List<String> deployPics) {
        this.deployPics = deployPics;
    }

    public static class NotificationBean implements Serializable {
        /**
         * contact : 刘为强
         * content : 17876856915
         */

        private String contact;
        private String content;

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
}

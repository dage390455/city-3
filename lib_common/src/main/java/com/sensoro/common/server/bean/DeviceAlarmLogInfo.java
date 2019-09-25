package com.sensoro.common.server.bean;


import androidx.annotation.NonNull;

import com.sensoro.common.model.DeviceNotificationBean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by sensoro on 17/11/13.
 */

public class DeviceAlarmLogInfo implements Serializable, Comparable<DeviceAlarmLogInfo> {
    private String _id;
    private String appId;
    private String deviceSN;
    private String deviceName;
    private String sensorType;
    private String unionType;
    private String deviceType;
    private String _updatedTime;
    private List<String> cameras;
    private long updatedTime;
    private long createdTime;
    private AlarmInfo.RuleInfo[] rules;
    private AlarmInfo.RecordInfo[] records;
    private double[] deviceLonlat;

    private DeviceNotificationBean deviceNotification;
    private List<DeviceNotificationBean> deviceNotifications;
    private boolean isDeleted;
    private int displayStatus;
    private int sort;
    private Map<String, Object> sensorData;
    private String event;
    //1正常
    private int alarmStatus;
    private boolean isClosed =false;

    public boolean getClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }
    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    private Metadata metadata;


    //    "metadata": {
//        "cids": [
//        "540672047",
//                "540672048"
//            ],
//        "picUrl": {
//            "540672047": "https://city-video-cdn.sensoro.com/001C2711A8AE_1568619402_1568619432.jpeg?e=1568710853&token=5Bf2KpUYTwT76bN_L1wuOKmiCDQbEOU-Fe4NRb-I:-VTrJ5NohMsgnuVHStH8auKSVRs=",
//                    "5406
//            2019-09-18 20:56:27.474 18589-29318/com.sensoro.smartcity E/sensoro_log-->: 72048": "https://city-video-cdn.sensoro.com/001C2711A8AE_1568619402_1568619432.jpeg?e=1568710853&token=5Bf2KpUYTwT76bN_L1wuOKmiCDQbEOU-Fe4NRb-I:-VTrJ5NohMsgnuVHStH8auKSVRs="
//        },
//        "updateTime": "2019-09-17T07:36:57.000Z",
//                "sn": "72057600540409950"
//    }
    public static class Metadata implements Serializable {
        private List<String> cids;
        private List<MetadataPic> picUrl;
        private String sn;

        public List<String> getCids() {
            return cids;
        }

        public void setCids(List<String> cids) {
            this.cids = cids;
        }

        public List<MetadataPic> getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(List<MetadataPic> picUrl) {
            this.picUrl = picUrl;
        }

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public static class MetadataPic implements Serializable{
            private String cid;
            private String pictureUrl;

            public String getCid() {
                return cid;
            }

            public void setCid(String cid) {
                this.cid = cid;
            }

            public String getPictureUrl() {
                return pictureUrl;
            }

            public void setPictureUrl(String pictureUrl) {
                this.pictureUrl = pictureUrl;
            }
        }
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Map<String, Object> getSensorData() {
        return sensorData;
    }

    public void setSensorData(Map<String, Object> sensorData) {
        this.sensorData = sensorData;
    }

    public double[] getDeviceLonlat() {
        return deviceLonlat;
    }

    public void setDeviceLonlat(double[] deviceLonlat) {
        this.deviceLonlat = deviceLonlat;
    }

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

    public String getDeviceSN() {
        return deviceSN;
    }

    public void setDeviceSN(String deviceSN) {
        this.deviceSN = deviceSN;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public String getUnionType() {
        return unionType;
    }

    public void setUnionType(String unionType) {
        this.unionType = unionType;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String get_updatedTime() {
        return _updatedTime;
    }

    public void set_updatedTime(String _updatedTime) {
        this._updatedTime = _updatedTime;
    }

    public AlarmInfo.RuleInfo[] getRules() {
        return rules;
    }

    public void setRules(AlarmInfo.RuleInfo[] rules) {
        this.rules = rules;
    }

    public AlarmInfo.RecordInfo[] getRecords() {
        return records;
    }

    public void setRecords(AlarmInfo.RecordInfo[] records) {
        this.records = records;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public int getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(int displayStatus) {
        this.displayStatus = displayStatus;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public DeviceNotificationBean getDeviceNotification() {
        return deviceNotification;
    }

    public void setDeviceNotification(DeviceNotificationBean deviceNotification) {
        this.deviceNotification = deviceNotification;
    }

    public List<String> getCameras() {
        return cameras;
    }

    public void setCameras(List<String> cameras) {
        this.cameras = cameras;
    }

    public int getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(int alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    public List<DeviceNotificationBean> getDeviceNotifications() {
        return deviceNotifications;
    }

    public void setDeviceNotifications(List<DeviceNotificationBean> deviceNotifications) {
        this.deviceNotifications = deviceNotifications;
    }


    @Override
    public int compareTo(@NonNull DeviceAlarmLogInfo anotherAlarmLogInfo) {
        if (this.getSort() < anotherAlarmLogInfo.getSort()) {
            return -1;
        } else if (this.getSort() == anotherAlarmLogInfo.getSort()) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceAlarmLogInfo that = (DeviceAlarmLogInfo) o;
        return Objects.equals(_id, that._id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id);
    }
}

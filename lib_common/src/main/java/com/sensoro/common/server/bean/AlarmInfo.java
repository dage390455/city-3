package com.sensoro.common.server.bean;

import com.sensoro.common.model.DeviceNotificationBean;
import com.sensoro.common.model.SecurityRisksAdapterModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sensoro on 17/7/26.
 */

public class AlarmInfo implements Serializable {

    private List<DeviceNotificationBean> notifications;
    private DeviceNotificationBean notification;
    private RuleInfo rules[];
    private BatteryInfo battery[];
    private String createTime;


    public RuleInfo[] getRules() {
        return rules;
    }

    public void setRules(RuleInfo[] rules) {
        this.rules = rules;
    }

    public BatteryInfo[] getBattery() {
        return battery;
    }

    public void setBattery(BatteryInfo[] battery) {
        this.battery = battery;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<DeviceNotificationBean> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<DeviceNotificationBean> notifications) {
        this.notifications = notifications;
    }

    public DeviceNotificationBean getNotification() {
        return notification;
    }

    public void setNotification(DeviceNotificationBean notification) {
        this.notification = notification;
    }

    public class BatteryInfo implements Serializable {

        private float thresholds;
        private String conditionType;
        private String sensorTypes;

        public float getThresholds() {
            return thresholds;
        }

        public void setThresholds(float thresholds) {
            this.thresholds = thresholds;
        }

        public String getConditionType() {
            return conditionType;
        }

        public void setConditionType(String conditionType) {
            this.conditionType = conditionType;
        }

        public String getSensorTypes() {
            return sensorTypes;
        }

        public void setSensorTypes(String sensorTypes) {
            this.sensorTypes = sensorTypes;
        }
    }


    public class RuleInfo implements Serializable {
        private String sensorTypes;
        private float thresholds;
        private String conditionType;

        public String getSensorTypes() {
            return sensorTypes;
        }

        public void setSensorTypes(String sensorTypes) {
            this.sensorTypes = sensorTypes;
        }

        public float getThresholds() {
            return thresholds;
        }

        public void setThresholds(float thresholds) {
            this.thresholds = thresholds;
        }

        public String getConditionType() {
            return conditionType;
        }

        public void setConditionType(String conditionType) {
            this.conditionType = conditionType;
        }
    }

    public class RecordInfo implements Serializable {
        private String type;
        private String sensorType;
        private int thresholds;
        private long updatedTime;
        private String source;
        private String status;
        //
        private Integer displayStatus;
        private Integer place;
        private Integer reason;
        private String name;
        private String remark;
        private Event[] phoneList;
        private Integer fireStage;
        private Integer fireType;
        private List<SecurityRisksAdapterModel> danger;

        public List<SecurityRisksAdapterModel> getDanger() {
            return danger;
        }

        public void setDanger(List<SecurityRisksAdapterModel> danger) {
            this.danger = danger;
        }

        public void setFireStage(Integer fireStage) {
            this.fireStage = fireStage;
        }

        public Integer getFireType() {
            return fireType;
        }

        public void setFireType(Integer fireType) {
            this.fireType = fireType;
        }

        //
        private List<String> images;

        private List<ScenesData> scenes;

        public List<ScenesData> getScenes() {
            return scenes;
        }

        public void setScenes(List<ScenesData> scenes) {
            this.scenes = scenes;
        }

        public Integer getPlace() {
            return place;
        }

        public void setPlace(Integer place) {
            this.place = place;
        }

        public Integer getReason() {
            return reason;
        }

        public void setReason(Integer reason) {
            this.reason = reason;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public long getTimeout() {
            return timeout;
        }

        public void setTimeout(long timeout) {
            this.timeout = timeout;
        }

        private long timeout;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSensorType() {
            return sensorType;
        }

        public void setSensorType(String sensorType) {
            this.sensorType = sensorType;
        }

        public int getThresholds() {
            return thresholds;
        }

        public void setThresholds(int thresholds) {
            this.thresholds = thresholds;
        }

        public long getUpdatedTime() {
            return updatedTime;
        }

        public void setUpdatedTime(long updatedTime) {
            this.updatedTime = updatedTime;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public Integer getDisplayStatus() {
            return displayStatus;
        }

        public void setDisplayStatus(Integer displayStatus) {
            this.displayStatus = displayStatus;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Event[] getPhoneList() {
            return phoneList;
        }

        public void setPhoneList(Event[] phoneList) {
            this.phoneList = phoneList;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public Integer getFireStage() {
            return fireStage;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public class Event implements Serializable {

            private String name;
            private String type;
            private String error_msg;
            private String number;
            private String source;
            private String receiveTime;
            private long updatedTime;
            private int reciveStatus;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getError_msg() {
                return error_msg;
            }

            public void setError_msg(String error_msg) {
                this.error_msg = error_msg;
            }

            public String getNumber() {
                return number;
            }

            public void setNumber(String number) {
                this.number = number;
            }

            public String getReceiveTime() {
                return receiveTime;
            }

            public void setReceiveTime(String receiveTime) {
                this.receiveTime = receiveTime;
            }

            public int getReciveStatus() {
                return reciveStatus;
            }

            public void setReciveStatus(int reciveStatus) {
                this.reciveStatus = reciveStatus;
            }

            public long getUpdatedTime() {
                return updatedTime;
            }

            public void setUpdatedTime(long updatedTime) {
                this.updatedTime = updatedTime;
            }

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }
        }
    }

    public class OwnerInfo implements Serializable {
        private String _id;
        private String name;
        private String id;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

}

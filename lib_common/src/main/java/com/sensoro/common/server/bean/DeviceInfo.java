package com.sensoro.common.server.bean;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by sensoro on 17/7/25.
 */

public class DeviceInfo implements Serializable, Comparable {

    private int id;
    private String sn;
    private String signal;
    private String name;
    private String unionType;
    private String contact;
    private String content;
    private String deviceType;
    private long updatedTime;
    private long createTime;
    private List<Double> lonlat;
    private String sensorTypes[];
    private int status;
    private List<String> tags;
    private SensorInfo sensorData;
    private Integer interval;
    private int alarmStatus;
    private AlarmInfo alarms;
    private int _level;
    private int sort;
    private String level_display;
    private String lastUpdatedTime;
    private boolean isNewDevice;
    private boolean isPushDevice;
    private String mergeType;
    private Map<String, SensorStruct> sensoroDetails;
    private List<DeviceAlarmsRecord> alarmsRecords;
    private String malfunctionType;
    private Map<String, MalfunctionDataBean> malfunctionData;
    private String address;
    private boolean deployFlag;
    private List<String> deployPics;
    private Long deployTime;
    private DeployControlSettingData config;
    private Integer demoMode;
//    private DeviceGroup deviceGroup;
    private String deviceGroup;

    private String appId;
    private String hardwareVersion;
    private String msgId;
    private boolean entityNameExist;
    private long relationTime;
    private OtherBean other;
    private ErrorBean error;
    private boolean selfCheckStatus;
    private Integer malfunctionStatus;
    private String _updatedTime;
    private String band;
    private String blePassword;
    private List<?> hitsRecords;
    private List<?> malfunctionRecords;
    private List<Integer> channelMask;
    private String wxPhone;
    private boolean notOwn;
    private String firmwareVersion;

    public boolean isDeployFlag() {
        return deployFlag;
    }

    public void setDeployFlag(boolean deployFlag) {
        this.deployFlag = deployFlag;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public DeviceInfo() {
        isPushDevice = false;
        isNewDevice = false;
    }

    public String getMalfunctionType() {
        return malfunctionType;
    }

    public void setMalfunctionType(String malfunctionType) {
        this.malfunctionType = malfunctionType;
    }

    public Map<String, MalfunctionDataBean> getMalfunctionData() {
        return malfunctionData;
    }

    public void setMalfunctionData(Map<String, MalfunctionDataBean> malfunctionData) {
        this.malfunctionData = malfunctionData;
    }

    public List<DeviceAlarmsRecord> getAlarmsRecords() {
        return alarmsRecords;
    }

    public void setAlarmsRecords(List<DeviceAlarmsRecord> alarmsRecords) {
        this.alarmsRecords = alarmsRecords;
    }

    public String getMergeType() {
        return mergeType;
    }

    public void setMergeType(String mergeType) {
        this.mergeType = mergeType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
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

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public List<Double> getLonlat() {
        return lonlat;
    }

    public void setLonlat(List<Double> lonlat) {
        this.lonlat = lonlat;
    }

    public String[] getSensorTypes() {
        return sensorTypes;
    }

    public void setSensorTypes(String[] sensorTypes) {
        this.sensorTypes = sensorTypes;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public SensorInfo getSensorData() {
        return sensorData;
    }

    public void setSensorData(SensorInfo sensorData) {
        this.sensorData = sensorData;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(int alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    public AlarmInfo getAlarms() {
        return alarms;
    }

    public void setAlarms(AlarmInfo alarms) {
        this.alarms = alarms;
    }

    public int get_level() {
        return _level;
    }

    public void set_level(int _level) {
        this._level = _level;
    }

    public String getLevel_display() {
        return level_display;
    }

    public void setLevel_display(String level_display) {
        this.level_display = level_display;
    }

    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(String lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public String getSignal() {
        return signal;
    }

    public void setSignal(String signal) {
        this.signal = signal;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Map<String, SensorStruct> getSensoroDetails() {
        return sensoroDetails;
    }

    public void setSensoroDetails(Map<String, SensorStruct> sensoroDetails) {
        this.sensoroDetails = sensoroDetails;
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

    public String getUnionType() {
        return unionType;
    }

    public void setUnionType(String unionType) {
        this.unionType = unionType;
    }

    public boolean isNewDevice() {
        return isNewDevice;
    }

    public void setNewDevice(boolean newDevice) {
        isNewDevice = newDevice;
    }

    public boolean isPushDevice() {
        return isPushDevice;
    }

    public void setPushDevice(boolean pushDevice) {
        isPushDevice = pushDevice;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        DeviceInfo anotherSensorInfo = (DeviceInfo) o;

        if (this.getSort() < anotherSensorInfo.getSort()) {
            return -1;
        } else if (this.getSort() == anotherSensorInfo.getSort()) {
            if (this.getUpdatedTime() < anotherSensorInfo.getUpdatedTime()) {
                return 1;
            } else if (this.getUpdatedTime() == anotherSensorInfo.getUpdatedTime()) {
                return 0;
            } else {
                return -1;
            }
        } else {
            return 1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        try {
            if (this == obj) {
                return true;
            }
            if (obj instanceof DeviceInfo) {
                DeviceInfo deviceInfo = (DeviceInfo) obj;
                return this.sn.equalsIgnoreCase(deviceInfo.sn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int hashCode() {
        try {
            return sn.hashCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.hashCode();
    }

    /**
     * 根据socket推送的对象改变原有对象属性
     *
     * @param deviceInfo
     * @return
     */
    public DeviceInfo cloneSocketData(DeviceInfo deviceInfo) {
        if (deviceInfo != null) {
            this.status = deviceInfo.getStatus();
            Map<String, MalfunctionDataBean> malfunctionData = deviceInfo.getMalfunctionData();
            if (malfunctionData != null) {
                this.malfunctionData = malfunctionData;
            }
            String address = deviceInfo.getAddress();
            if (!TextUtils.isEmpty(address)) {
                this.address = address;
            }
            AlarmInfo alarms = deviceInfo.getAlarms();
            if (alarms != null) {
                if (this.alarms != null) {
                    AlarmInfo.BatteryInfo[] battery = alarms.getBattery();
                    if (battery != null) {
                        this.alarms.setBattery(battery);
                    }
                    AlarmInfo.NotificationInfo notification = alarms.getNotification();
                    if (notification != null) {
                        this.alarms.setNotification(notification);
                    }
                    AlarmInfo.RuleInfo[] rules = alarms.getRules();
                    if (rules != null) {
                        this.alarms.setRules(rules);
                    }
                } else {
                    this.alarms = alarms;
                }

            }
            List<DeviceAlarmsRecord> alarmsRecords = deviceInfo.getAlarmsRecords();
            if (alarmsRecords != null) {
                this.alarmsRecords = alarmsRecords;
            }
            String content = deviceInfo.getContent();
            if (!TextUtils.isEmpty(content)) {
                this.content = content;
            }
            String contact = deviceInfo.getContact();
            if (!TextUtils.isEmpty(contact)) {
                this.contact = contact;
            }
            Integer interval = deviceInfo.getInterval();
            if (interval != null) {
                this.interval = interval;
            }
            List<Double> lonlat = deviceInfo.getLonlat();
            if (lonlat != null && lonlat.size() == 2) {
                this.lonlat = lonlat;
            }
            String name = deviceInfo.getName();
            if (!TextUtils.isEmpty(name)) {
                this.name = name;
            }
            Map<String, SensorStruct> sensoroDetails = deviceInfo.getSensoroDetails();
            if (sensoroDetails != null) {
                this.sensoroDetails = sensoroDetails;
            }
            String[] sensorTypes = deviceInfo.getSensorTypes();
            if (sensorTypes != null) {
                this.sensorTypes = sensorTypes;
            }
            String signal = deviceInfo.getSignal();
            if (!TextUtils.isEmpty(signal)) {
                this.signal = signal;
            }
            List<String> tags = deviceInfo.getTags();
            if (tags != null) {
                this.tags = tags;
            }
            long updatedTime = deviceInfo.getUpdatedTime();
            if (updatedTime != 0) {
                this.updatedTime = updatedTime;
            }
        }
        return this;
    }

    public List<String> getDeployPics() {
        return deployPics;
    }

    public void setDeployPics(List<String> deployPics) {
        this.deployPics = deployPics;
    }

    public Long getDeployTime() {
        return deployTime;
    }

    public void setDeployTime(Long deployTime) {
        this.deployTime = deployTime;
    }

    public DeployControlSettingData getConfig() {
        return config;
    }

    public void setConfig(DeployControlSettingData config) {
        this.config = config;
    }

    public Integer getDemoMode() {
        return demoMode;
    }

    public void setDemoMode(Integer demoMode) {
        this.demoMode = demoMode;
    }

    public String getOwners() {
        return owners;
    }

    public void setOwners(String owners) {
        this.owners = owners;
    }

    private String owners;

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public boolean isEntityNameExist() {
        return entityNameExist;
    }

    public void setEntityNameExist(boolean entityNameExist) {
        this.entityNameExist = entityNameExist;
    }

    public long getRelationTime() {
        return relationTime;
    }

    public void setRelationTime(long relationTime) {
        this.relationTime = relationTime;
    }

    public OtherBean getOther() {
        return other;
    }

    public void setOther(OtherBean other) {
        this.other = other;
    }

    public ErrorBean getError() {
        return error;
    }

    public void setError(ErrorBean error) {
        this.error = error;
    }

    public boolean isSelfCheckStatus() {
        return selfCheckStatus;
    }

    public void setSelfCheckStatus(boolean selfCheckStatus) {
        this.selfCheckStatus = selfCheckStatus;
    }

    public Integer getMalfunctionStatus() {
        return malfunctionStatus;
    }

    public void setMalfunctionStatus(Integer malfunctionStatus) {
        this.malfunctionStatus = malfunctionStatus;
    }

    public String get_updatedTime() {
        return _updatedTime;
    }

    public void set_updatedTime(String _updatedTime) {
        this._updatedTime = _updatedTime;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public String getBlePassword() {
        return blePassword;
    }

    public void setBlePassword(String blePassword) {
        this.blePassword = blePassword;
    }

    public List<?> getHitsRecords() {
        return hitsRecords;
    }

    public void setHitsRecords(List<?> hitsRecords) {
        this.hitsRecords = hitsRecords;
    }

    public List<?> getMalfunctionRecords() {
        return malfunctionRecords;
    }

    public void setMalfunctionRecords(List<?> malfunctionRecords) {
        this.malfunctionRecords = malfunctionRecords;
    }

    public List<Integer> getChannelMask() {
        return channelMask;
    }

    public void setChannelMask(List<Integer> channelMask) {
        this.channelMask = channelMask;
    }

    public String getWxPhone() {
        return wxPhone;
    }

    public void setWxPhone(String wxPhone) {
        this.wxPhone = wxPhone;
    }

    public boolean isNotOwn() {
        return notOwn;
    }

    public void setNotOwn(boolean notOwn) {
        this.notOwn = notOwn;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getDeviceGroup() {
        return deviceGroup;
    }

    public void setDeviceGroup(String deviceGroup) {
        this.deviceGroup = deviceGroup;
    }

//    public DeviceGroup getDeviceGroup() {
//        return deviceGroup;
//    }
//
//    public void setDeviceGroup(DeviceGroup deviceGroup) {
//        this.deviceGroup = deviceGroup;
//    }

    public static class OtherBean implements Serializable {
    }

    public static class DeviceGroup implements Serializable {
        private String _id;
        private String id;
        private String groupName;

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

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }
    }

    public static class ErrorBean implements Serializable {
        /**
         * status : false
         */

        private boolean status;

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }
    }
}

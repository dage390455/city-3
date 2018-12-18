package com.sensoro.smartcity.server.bean;

import java.io.Serializable;
import java.util.List;

public class DeployDeviceInfo implements Serializable {


    private String sn;
    private String owners;
    private String appId;
    private String deviceType;
    private String unionType;
    private MalfunctionDataBean malfunctionData;
    private String name;
    private int interval;
    private String hardwareVersion;
    private long updatedTime;
    private String lastUpdatedTime;
    private String msgId;
    private boolean entityNameExist;
    private long createTime;
    private boolean deployFlag;
    private long relationTime;
    private OtherBean other;
    private String signal;
    private ErrorBean error;
    private boolean selfCheckStatus;
    private int malfunctionStatus;
    private int alarmStatus;
    private int status;
    private AlarmInfo alarms;
    private SensorInfo sensorData;
    private String id;
    private String mergeType;
    private String _updatedTime;
    private String band;
    private String blePassword;
    private List<?> hitsRecords;
    private List<?> malfunctionRecords;
    private List<DeviceAlarmsRecord> alarmsRecords;
    private List<Double> lonlat;
    private List<String> tags;
    private List<String> sensorTypes;
    private List<Integer> channelMask;
    private String wxPhone;
    private boolean notOwn;

    public boolean isNotOwn() {
        return notOwn;
    }

    public void setNotOwn(boolean notOwn) {
        this.notOwn = notOwn;
    }

    public List<Integer> getChannelMask() {
        return channelMask;
    }

    public void setChannelMask(List<Integer> channelMask) {
        this.channelMask = channelMask;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
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

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getUnionType() {
        return unionType;
    }

    public void setUnionType(String unionType) {
        this.unionType = unionType;
    }

    public MalfunctionDataBean getMalfunctionData() {
        return malfunctionData;
    }

    public void setMalfunctionData(MalfunctionDataBean malfunctionData) {
        this.malfunctionData = malfunctionData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(String lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
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

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public boolean isDeployFlag() {
        return deployFlag;
    }

    public void setDeployFlag(boolean deployFlag) {
        this.deployFlag = deployFlag;
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

    public String getSignal() {
        return signal;
    }

    public void setSignal(String signal) {
        this.signal = signal;
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

    public int getMalfunctionStatus() {
        return malfunctionStatus;
    }

    public void setMalfunctionStatus(int malfunctionStatus) {
        this.malfunctionStatus = malfunctionStatus;
    }

    public int getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(int alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public AlarmInfo getAlarms() {
        return alarms;
    }

    public void setAlarms(AlarmInfo alarms) {
        this.alarms = alarms;
    }

    public SensorInfo getSensorData() {
        return sensorData;
    }

    public void setSensorData(SensorInfo sensorData) {
        this.sensorData = sensorData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMergeType() {
        return mergeType;
    }

    public void setMergeType(String mergeType) {
        this.mergeType = mergeType;
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

    public List<DeviceAlarmsRecord> getAlarmsRecords() {
        return alarmsRecords;
    }

    public void setAlarmsRecords(List<DeviceAlarmsRecord> alarmsRecords) {
        this.alarmsRecords = alarmsRecords;
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

    public List<String> getSensorTypes() {
        return sensorTypes;
    }

    public void setSensorTypes(List<String> sensorTypes) {
        this.sensorTypes = sensorTypes;
    }

    public String getWxPhone() {
        return wxPhone;
    }

    public void setWxPhone(String wxPhone) {
        this.wxPhone = wxPhone;
    }

    //TODO 是否一样
    public static class MalfunctionDataBean {
    }

    public static class OtherBean {
    }

    public static class ErrorBean {
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

package com.sensoro.smartcity.model;

import java.util.List;

/**
 * deviceType,unionType映射关系model，后期如果根据接口获取，这个model可弃用，这里主要用于
 * 巡检任务中devieType,unionType关系的映射
 */
public class DeviceTypeMutualModel {

    private List<DeviceInfoBean> deviceInfo;
    private List<SingleInfosBean> singleInfos;
    private List<MergeTypeInfosBean> mergeTypeInfos;

    public List<DeviceInfoBean> getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(List<DeviceInfoBean> deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public List<SingleInfosBean> getSingleInfos() {
        return singleInfos;
    }

    public void setSingleInfos(List<SingleInfosBean> singleInfos) {
        this.singleInfos = singleInfos;
    }

    public List<MergeTypeInfosBean> getMergeTypeInfos() {
        return mergeTypeInfos;
    }

    public void setMergeTypeInfos(List<MergeTypeInfosBean> mergeTypeInfos) {
        this.mergeTypeInfos = mergeTypeInfos;
    }

    public static class DeviceInfoBean {
        /**
         * deviceType : ch4
         * sensorTypes : ["ch4"]
         * unionType : ch4
         * mergeType : natural_gas
         * thresholdSupported : standard
         * intervalSupported : standard
         * alarmReceive : true
         */

        private String deviceType;
        private String unionType;
        private String mergeType;
        private String thresholdSupported;
        private String intervalSupported;
        private boolean alarmReceive;
        private List<String> sensorTypes;

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

        public String getMergeType() {
            return mergeType;
        }

        public void setMergeType(String mergeType) {
            this.mergeType = mergeType;
        }

        public String getThresholdSupported() {
            return thresholdSupported;
        }

        public void setThresholdSupported(String thresholdSupported) {
            this.thresholdSupported = thresholdSupported;
        }

        public String getIntervalSupported() {
            return intervalSupported;
        }

        public void setIntervalSupported(String intervalSupported) {
            this.intervalSupported = intervalSupported;
        }

        public boolean isAlarmReceive() {
            return alarmReceive;
        }

        public void setAlarmReceive(boolean alarmReceive) {
            this.alarmReceive = alarmReceive;
        }

        public List<String> getSensorTypes() {
            return sensorTypes;
        }

        public void setSensorTypes(List<String> sensorTypes) {
            this.sensorTypes = sensorTypes;
        }
    }

    public static class SingleInfosBean {
        /**
         * sesorType : altitude
         * name : 海拔
         * isBool : false
         * unit : m
         * max : 8848
         * min : -500
         * alarm :  连通
         * recovery :  断开
         */

        private String sesorType;
        private String name;
        private boolean isBool;
        private String unit;
        private int max;
        private int min;
        private String alarm;
        private String recovery;

        public String getSesorType() {
            return sesorType;
        }

        public void setSesorType(String sesorType) {
            this.sesorType = sesorType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isIsBool() {
            return isBool;
        }

        public void setIsBool(boolean isBool) {
            this.isBool = isBool;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public String getAlarm() {
            return alarm;
        }

        public void setAlarm(String alarm) {
            this.alarm = alarm;
        }

        public String getRecovery() {
            return recovery;
        }

        public void setRecovery(String recovery) {
            this.recovery = recovery;
        }
    }

    public static class MergeTypeInfosBean {
        /**
         * mergeType : arc
         * name : 电弧探测
         * deviceTypes : ["zcrd_arc"]
         * icon : ammeter
         * image : https://resource-city.sensoro.com/device-icon/ammeter.png
         */

        private String mergeType;
        private String name;
        private String icon;
        private String image;
        private List<String> deviceTypes;

        public String getMergeType() {
            return mergeType;
        }

        public void setMergeType(String mergeType) {
            this.mergeType = mergeType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public List<String> getDeviceTypes() {
            return deviceTypes;
        }

        public void setDeviceTypes(List<String> deviceTypes) {
            this.deviceTypes = deviceTypes;
        }
    }
}

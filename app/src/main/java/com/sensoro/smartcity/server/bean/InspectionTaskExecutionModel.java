package com.sensoro.smartcity.server.bean;

import java.util.List;

public class InspectionTaskExecutionModel {

    /**
     * baseInfo : {"identifier":"XJ201809251209020498","name":"北京市望京soho一期巡检任务","beginTime":1536768000000,"endTime":1538323200000,"startTime":0,"finishTime":0,"status":0}
     * inspectors : [{"id":"5ba5ef9668efebd21a61a063","name":"李仲元","contact":"13051321203"}]
     * deviceTypes : [{"deviceType":"smoke","num":4}]
     * stationStatus : [{"type":"uncheck","num":1},{"type":"normal","num":3},{"type":"abnormal","num":0}]
     */

    private BaseInfoBean baseInfo;
    private List<InspectorsBean> inspectors;
    private List<DeviceTypesBean> deviceTypes;
    private List<DeviceStatusBean> deviceStatus;
    private List<UnionSummaryBean> unionTypes;

    public BaseInfoBean getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(BaseInfoBean baseInfo) {
        this.baseInfo = baseInfo;
    }

    public List<InspectorsBean> getInspectors() {
        return inspectors;
    }

    public void setInspectors(List<InspectorsBean> inspectors) {
        this.inspectors = inspectors;
    }

    public List<DeviceTypesBean> getDeviceTypes() {
        return deviceTypes;
    }

    public void setDeviceTypes(List<DeviceTypesBean> deviceTypes) {
        this.deviceTypes = deviceTypes;
    }

    public List<DeviceStatusBean> getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(List<DeviceStatusBean> deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public static class BaseInfoBean {

        private String identifier;
        private String name;
        private long beginTime;
        private long endTime;
        private int startTime;
        private int finishTime;
        private int status;

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(long beginTime) {
            this.beginTime = beginTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public int getStartTime() {
            return startTime;
        }

        public void setStartTime(int startTime) {
            this.startTime = startTime;
        }

        public int getFinishTime() {
            return finishTime;
        }

        public void setFinishTime(int finishTime) {
            this.finishTime = finishTime;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

    public static class InspectorsBean {
        /**
         * id : 5ba5ef9668efebd21a61a063
         * name : 李仲元
         * contact : 13051321203
         */

        private String id;
        private String name;
        private String contact;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }
    }

    public static class DeviceTypesBean {
        /**
         * deviceType : smoke
         * num : 4
         */

        private String deviceType;
        private int num;

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }
    }

    public static class DeviceStatusBean {
        /**
         * type : uncheck
         * num : 1
         */

        private String type;
        private int num;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }
    }
}

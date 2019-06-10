package com.sensoro.common.server.bean;

import java.io.Serializable;
import java.util.List;

public class InspectionIndexTaskInfo implements Serializable{


    private String identifier;
    private int status;
    private long createdTime;
    private String createdBy;
    private long endTime;
    private long beginTime;
    private String name;
    private String id;
    private List<DeviceSummaryBean> deviceSummary;
    private List<UnionSummaryBean> unionSummary;

    private List<String> inspectorIds;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
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

    public List<DeviceSummaryBean> getDeviceSummary() {
        return deviceSummary;
    }

    public void setDeviceSummary(List<DeviceSummaryBean> deviceSummary) {
        this.deviceSummary = deviceSummary;
    }

    public List<String> getInspectorIds() {
        return inspectorIds;
    }

    public void setInspectorIds(List<String> inspectorIds) {
        this.inspectorIds = inspectorIds;
    }

    public List<UnionSummaryBean> getUnionSummary() {
        return unionSummary;
    }

    public void setUnionSummary(List<UnionSummaryBean> unionSummary) {
        this.unionSummary = unionSummary;
    }

    public static class DeviceSummaryBean implements Serializable{
        /**
         * type : fhsj_smoke
         * num : 1
         */

        private String deviceType;
        private int num;
        private String _id;

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }
    }
}

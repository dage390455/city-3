package com.sensoro.smartcity.server.bean;

import java.io.Serializable;
import java.util.List;

public class InspectionTaskDeviceDetail implements Serializable {
    /**
     * id : 5ba9b3b2f11db9772ee33026
     * name : 一个有节操的传感器
     * taskId : 5ba9b3b2f11db9772ee33021
     * sn : 01921117C6DC0A6A
     * deviceType : smoke
     * status : 0
     * lonlat : [0,0]
     * timecost :
     * tags : ["栋栋真帅","杨哥救我","长寿么么哒"]
     * malfunction : {"tags":[],"remark":"","handle":-1}
     */

    private String id;
    private String name;
    private String taskId;
    private String sn;
    private String deviceType;
    private String unionType;
    private int status;
    private String timecost;
    private MalfunctionBean malfunction;
    private List<Double> lonlat;
    private List<String> tags;

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

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTimecost() {
        return timecost;
    }

    public void setTimecost(String timecost) {
        this.timecost = timecost;
    }

    public MalfunctionBean getMalfunction() {
        return malfunction;
    }

    public void setMalfunction(MalfunctionBean malfunction) {
        this.malfunction = malfunction;
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

    public String getUnionType() {
        return unionType;
    }

    public void setUnionType(String unionType) {
        this.unionType = unionType;
    }

    public static class MalfunctionBean implements Serializable{
        /**
         * tags : []
         * remark :
         * handle : -1
         */

        private String remark;
        private int handle;
        private List<String> tags;

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getHandle() {
            return handle;
        }

        public void setHandle(int handle) {
            this.handle = handle;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }
    }
}

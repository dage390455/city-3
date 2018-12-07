package com.sensoro.smartcity.server.bean;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class InspectionTaskDeviceDetail implements Serializable, Comparable<InspectionTaskDeviceDetail> {

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
    private boolean isNearBy_local = false;
    private double sort_local;

    public boolean isNearBy_local() {
        return isNearBy_local;
    }

    public void setNearBy_local(boolean nearBy_local) {
        isNearBy_local = nearBy_local;
    }


    public double getSort_local() {
        return sort_local;
    }

    public void setSort_local(double sort_local) {
        this.sort_local = sort_local;
    }
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

    @Override
    public int compareTo(@NonNull InspectionTaskDeviceDetail o) {
        if (this.sort_local > o.sort_local) {
            return -1;
        } else if (this.sort_local == o.sort_local) {
            return 0;
        } else {
            return 1;
        }
    }

    public static class MalfunctionBean implements Serializable {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InspectionTaskDeviceDetail that = (InspectionTaskDeviceDetail) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(sn, that.sn);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, sn);
    }
}

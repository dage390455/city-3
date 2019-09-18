package com.sensoro.common.model;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: jack
 * 时  间: 2019-09-18
 * 包  名: com.sensoro.forestfire.model
 * 简  述: <功能简述>
 */
public class ForestGatewayBean implements Serializable {

    /**
     * cigId : 204117050002
     * name : 测试网关
     * status : true
     * userid : 5d149c91ae13e705a90c65a9
     * label : ["测试1","test2"]
     * longitude : 39.9953284
     * latitude : 116.4783978
     * isDeleted : false
     * createTime : 2019-09-12T04:27:07.352Z
     */

    private String cigId;
    private String name;
    private String status;
    private String userid;
    private String longitude;
    private String latitude;
    private boolean isDeleted;
    private String createTime;
    private List<String> label;

    public String getCigId() {
        return cigId;
    }

    public void setCigId(String cigId) {
        this.cigId = cigId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<String> getLabel() {
        return label;
    }

    public void setLabel(List<String> label) {
        this.label = label;
    }
}

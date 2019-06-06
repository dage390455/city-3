package com.sensoro.common.server.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class NamePlateInfo implements Serializable {


    private String createTime;
    private String sn;
    private String cid;
    private String name;
    private String mobilePhone;
    private String id;
    private String _id;
    private String orientationName;
    private String deviceType;
    private boolean deployFlag;

    public int getDevicesCount() {
        return devicesCount;
    }

    public void setDevicesCount(int devicesCount) {
        this.devicesCount = devicesCount;
    }

    private int devicesCount;

    //标识是否已经部署过
    Boolean deployFlag;
    //关联传感器的数量
    Integer devicesCount;

    public String deviceTypeName;
    public String iconUrl;
    public boolean isCheck;

    private ArrayList<String> tags;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Boolean getDeployFlag() {
        return deployFlag;
    }

    public void setDeployFlag(Boolean deployFlag) {
        this.deployFlag = deployFlag;
    }

    public Integer getDevicesCount() {
        return devicesCount;
    }

    public void setDevicesCount(Integer devicesCount) {
        this.devicesCount = devicesCount;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrientationName() {
        return orientationName;
    }

    public void setOrientationName(String orientationName) {
        this.orientationName = orientationName;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

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

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public boolean isDeployFlag() {
        return deployFlag;
    }

    public void setDeployFlag(boolean deployFlag) {
        this.deployFlag = deployFlag;
    }
}

package com.sensoro.common.server.bean;

import com.sensoro.common.model.DeviceNotificationBean;

import java.io.Serializable;
import java.util.List;

public class DeployForestGatewayAddInfo implements Serializable {

    /**
     * status : true
     * label : ["测试1","test2"]
     * notifications : [{"contact":"测试1","content":"13811111111","types":"phone"},{"contact":"测试2","content":"13811111112","types":"phone"}]
     * installationImage : ["https://resource-city.sensoro.com/2BE6075B0BD135B5D268A4C85D32AC25","https://resource-city.sensoro.com/380BE0E55EB4A4270703ED79A0C1415C"]
     * isDeleted : false
     * createTime : 1568964413316
     * _id : 5d847f7771d0bd8350baca6d
     * cigId : 204117050002
     * name : 测试网关2
     * userid : 5b86438092bb4b66f7621a7f
     * location : 张家口A山
     * latitude : 39.9953284
     * longitude : 116.4783978
     * installationLocation : 5号楼105室第二个机柜
     * installationInfo : null
     * id : 5d847f7771d0bd8350baca6d
     */

    private boolean status;
    private boolean isDeleted;
    private long createTime;
//    private String _id;
    private String cigId;
    private String name;
//    private String userid;
    private String location;
    private double latitude;
    private double longitude;
    private String installationLocation;
//    private Object installationInfo;
    private String id;
    private List<String> label;
    private List<DeviceNotificationBean> notifications;
    private List<String> installationImage;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

//    public String get_id() {
//        return _id;
//    }
//
//    public void set_id(String _id) {
//        this._id = _id;
//    }

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

//    public String getUserid() {
//        return userid;
//    }
//
//    public void setUserid(String userid) {
//        this.userid = userid;
//    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getInstallationLocation() {
        return installationLocation;
    }

    public void setInstallationLocation(String installationLocation) {
        this.installationLocation = installationLocation;
    }

//    public Object getInstallationInfo() {
//        return installationInfo;
//    }
//
//    public void setInstallationInfo(Object installationInfo) {
//        this.installationInfo = installationInfo;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getLabel() {
        return label;
    }

    public void setLabel(List<String> label) {
        this.label = label;
    }

    public List<DeviceNotificationBean> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<DeviceNotificationBean> notifications) {
        this.notifications = notifications;
    }

    public List<String> getInstallationImage() {
        return installationImage;
    }

    public void setInstallationImage(List<String> installationImage) {
        this.installationImage = installationImage;
    }

}

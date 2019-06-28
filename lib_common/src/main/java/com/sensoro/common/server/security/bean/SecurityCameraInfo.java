package com.sensoro.common.server.security.bean;

import java.io.Serializable;
import java.util.List;

public class SecurityCameraInfo implements Serializable {

    /**
     * name : 集成研发抓拍机
     * sn : 001C2711A8AF
     * cid : 540409919
     * brand : SENSORO
     * version : V1.0.0
     * type : 抓拍机
     * deviceStatus : 1
     * latitude : 40.017564
     * longitude : 116.503266
     * location : 北京市朝阳区崔各庄镇川渝饭庄12345
     * label : ["望京","SOHO"]
     * contact : {"name":"test","mobilePhone":"13699167277"}
     * installationMode : 101800
     * orientation : 112901
     */

    private String name;
    private String sn;
    private String cid;
    private String brand;
    private String version;
    private String type;
    private String deviceStatus;
    private String latitude;
    private String longitude;
    private String location;
    private List<SecurityContactsInfo> contact;
    private String installationMode;
    private String orientation;
    private List<String> label;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<SecurityContactsInfo> getContact() {
        return contact;
    }

    public void setContact(List<SecurityContactsInfo> contact) {
        this.contact = contact;
    }

    public String getInstallationMode() {
        return installationMode;
    }

    public void setInstallationMode(String installationMode) {
        this.installationMode = installationMode;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public List<String> getLabel() {
        return label;
    }

    public void setLabel(List<String> label) {
        this.label = label;
    }

}

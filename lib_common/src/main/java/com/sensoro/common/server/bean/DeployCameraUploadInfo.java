package com.sensoro.common.server.bean;

import java.io.Serializable;
import java.util.List;

public class DeployCameraUploadInfo implements Serializable {
    private List<String> label;
    private String createTime;
    private String _id;
    private String sn;
    private String cid;
    private String name;
    private String userid;
    private Info info;
    private String mobilePhone;
    private String installationMode;
    private String orientation;
    private String id;

    public void setLabel(List<String> label) {
        this.label = label;
    }

    public List<String> getLabel() {
        return label;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_id() {
        return _id;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getSn() {
        return sn;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCid() {
        return cid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserid() {
        return userid;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public Info getInfo() {
        return info;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setInstallationMode(String installationMode) {
        this.installationMode = installationMode;
    }

    public String getInstallationMode() {
        return installationMode;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    /**
     * Copyright 2019 bejson.com
     */

    /**
     * Auto-generated: 2019-05-21 18:55:58
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */
    public static class Info implements Serializable {

        private String type;
        private String version;
        private String brand;
        private String cid;
        private String sn;
        private boolean platform;
        private String latitude;
        private String longitude;
        private String deviceStatus;
        private String location;
        private List<String> imgUrls;

        public void setType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getVersion() {
            return version;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getBrand() {
            return brand;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public String getCid() {
            return cid;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public String getSn() {
            return sn;
        }

        public void setPlatform(boolean platform) {
            this.platform = platform;
        }

        public boolean getPlatform() {
            return platform;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setDeviceStatus(String deviceStatus) {
            this.deviceStatus = deviceStatus;
        }

        public String getDeviceStatus() {
            return deviceStatus;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getLocation() {
            return location;
        }

        public void setImgUrls(List<String> imgUrls) {
            this.imgUrls = imgUrls;
        }

        public List<String> getImgUrls() {
            return imgUrls;
        }

    }
}

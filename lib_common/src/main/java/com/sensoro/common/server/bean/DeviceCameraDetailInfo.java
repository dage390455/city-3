package com.sensoro.common.server.bean;

import java.util.List;

public class DeviceCameraDetailInfo {

    /**
     * cid : 540409860
     * deviceName : 费家村村委会门口
     * deviceStatus : 1
     * deviceType : 100603
     * id : 72057600540409860
     * latitude : 40.015890000000000
     * longitude : 116.505466000000000
     * lygroupId : 5ca1750f53acb7a97ad4e663
     * manufacturerDeviceId : 540409860
     * manufacturerDeviceType : 103401
     * organizationIds : ["100101000439"]
     * pathId : [119105]
     * placeId : 1200000000000000000
     * sn : 001C2711A6A1
     * hls : https://scpub-api.antelopecloud.cn/cloud/v2/live/540409860.m3u8?client_token=540409860_3356491776_1586754482_329be6468f23f2e032c70cec70fe29c6
     * flv : https://scpub-api.antelopecloud.cn/cloud/v2/live/540409860.flv?client_token=540409860_3356491776_1586754482_329be6468f23f2e032c70cec70fe29c6
     * lastCover : https://scpub-eye.antelopecloud.cn/staticResource/v1/video/getLatestCoverMap/540409860?signature=49393d2ccc4bea5602c863f46f3ebae3&expire=1555576606269
     * camera : {"label":[],"createTime":"2019-04-04T13:07:32.059Z","_id":"5ca605c9c36a4800184bb451","sn":"001C2711A6A1","cid":"540409860","name":"费家村村委会门口","userid":"","info":{"type":"抓拍机","version":"sunellZHY-H1","brand":"sunell","cid":"540409860","sn":"001C2711A6A1","platform":false,"latitude":40.01589,"longitude":116.505466,"deviceStatus":"0"},"mobilePhone":"1627348495969","id":"5ca605c9c36a4800184bb451"}
     */

    private String cid;
    private String deviceName;
    private String deviceStatus;
    private String deviceType;
    private String id;
    private String latitude;
    private String longitude;
    private String lygroupId;
    private String manufacturerDeviceId;
    private String manufacturerDeviceType;
    private String placeId;
    private String sn;
    private String hls;
    private String flv;
    private String lastCover;
    private CameraBean camera;
    private List<String> organizationIds;
    private List<Integer> pathId;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getLygroupId() {
        return lygroupId;
    }

    public void setLygroupId(String lygroupId) {
        this.lygroupId = lygroupId;
    }

    public String getManufacturerDeviceId() {
        return manufacturerDeviceId;
    }

    public void setManufacturerDeviceId(String manufacturerDeviceId) {
        this.manufacturerDeviceId = manufacturerDeviceId;
    }

    public String getManufacturerDeviceType() {
        return manufacturerDeviceType;
    }

    public void setManufacturerDeviceType(String manufacturerDeviceType) {
        this.manufacturerDeviceType = manufacturerDeviceType;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getHls() {
        return hls;
    }

    public void setHls(String hls) {
        this.hls = hls;
    }

    public String getFlv() {
        return flv;
    }

    public void setFlv(String flv) {
        this.flv = flv;
    }

    public String getLastCover() {
        return lastCover;
    }

    public void setLastCover(String lastCover) {
        this.lastCover = lastCover;
    }

    public CameraBean getCamera() {
        return camera;
    }

    public void setCamera(CameraBean camera) {
        this.camera = camera;
    }

    public List<String> getOrganizationIds() {
        return organizationIds;
    }

    public void setOrganizationIds(List<String> organizationIds) {
        this.organizationIds = organizationIds;
    }

    public List<Integer> getPathId() {
        return pathId;
    }

    public void setPathId(List<Integer> pathId) {
        this.pathId = pathId;
    }

    public static class CameraBean {
        /**
         * label : []
         * createTime : 2019-04-04T13:07:32.059Z
         * _id : 5ca605c9c36a4800184bb451
         * sn : 001C2711A6A1
         * cid : 540409860
         * name : 费家村村委会门口
         * userid :
         * info : {"type":"抓拍机","version":"sunellZHY-H1","brand":"sunell","cid":"540409860","sn":"001C2711A6A1","platform":false,"latitude":40.01589,"longitude":116.505466,"deviceStatus":"0"}
         * mobilePhone : 1627348495969
         * id : 5ca605c9c36a4800184bb451
         */

        private String createTime;
        private String _id;
        private String sn;
        private String cid;
        private String name;
        private String userid;
        private InfoBean info;
        private String mobilePhone;
        private String id;
        private List<?> label;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
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

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public InfoBean getInfo() {
            return info;
        }

        public void setInfo(InfoBean info) {
            this.info = info;
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

        public List<?> getLabel() {
            return label;
        }

        public void setLabel(List<?> label) {
            this.label = label;
        }

        public static class InfoBean {
            /**
             * type : 抓拍机
             * version : sunellZHY-H1
             * brand : sunell
             * cid : 540409860
             * sn : 001C2711A6A1
             * platform : false
             * latitude : 40.01589
             * longitude : 116.505466
             * deviceStatus : 0
             */

            private String type;
            private String version;
            private String brand;
            private String cid;
            private String sn;
            private boolean platform;
            private double latitude;
            private double longitude;
            private String deviceStatus;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getVersion() {
                return version;
            }

            public void setVersion(String version) {
                this.version = version;
            }

            public String getBrand() {
                return brand;
            }

            public void setBrand(String brand) {
                this.brand = brand;
            }

            public String getCid() {
                return cid;
            }

            public void setCid(String cid) {
                this.cid = cid;
            }

            public String getSn() {
                return sn;
            }

            public void setSn(String sn) {
                this.sn = sn;
            }

            public boolean isPlatform() {
                return platform;
            }

            public void setPlatform(boolean platform) {
                this.platform = platform;
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

            public String getDeviceStatus() {
                return deviceStatus;
            }

            public void setDeviceStatus(String deviceStatus) {
                this.deviceStatus = deviceStatus;
            }
        }
    }
}

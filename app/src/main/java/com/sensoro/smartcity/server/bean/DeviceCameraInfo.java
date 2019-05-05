package com.sensoro.smartcity.server.bean;

import java.io.Serializable;
import java.util.List;

public class DeviceCameraInfo implements Serializable {

    /**
     * label : []
     * createTime : 2019-04-08T03:25:57.381Z
     * _id : 5caaf4cd5f28d000108fe841
     * sn : 001C2711A980
     * cid : 540409873
     * name : 设备名称-1
     * userid : {"_id":"590c235044aa4369905d455b","updatedTime":1529484942657,"nickname":"DEMO","appId":"50I35FhvOAw9","appKey":"jZuYQtqsQeP1bTVtsI63aBP4cuVKTdJ1Syws0BUrunt","appSecret":"LPMKMqgSfTmXGRwd2vcM6jpPgISZ44aW","addBy":"590bfad00771ed116b1df421","contacts":"15210505093","contactsName":"是","area":"崇文区","manager":{"contacts":"15110041945","name":"高鹏"},"deviceCount":227,"stationCount":8,"syncDeviceStationCountTime":1486199018302,"createdTime":1493967696917,"payInfo":{"sub_mch_id":"13962862021","card_id":"91110105306401130D","name":"赵武阳","mobilePhone":"18601118681","company":"北京升哲科技有限公司","address":"北京市朝阳区望京街10号望京SOHO1号楼B座2801/2807","phone":"400-6863180","province":"HN","city":"HK","business":"ATT","bank":"招商银行股份有限公司北京望京支行","bankNumber":"62030122000696581","fdd_id":"1FD44C56AA4CADD4EFBAE39EBEB25644","isTest":true},"noticeLevel":"senior","isCopySubUserAlarmMsg":true,"isStop":false,"isDeleted":false,"showAllDevice":true,"binding":["5adef07b362c4f2f376b43cb","5af40996d631bb4d0136c715","5bf655ccc0185c78346909c3","5bffb0082cbdbd1e8716c4ef","5c17466ae2177c52d24536a8","5c24a0ee06af1ca6646cbe52","5c6faabbdd6a62a2dc039b7a","5c04f231724d5561d5f7a1c2","5c90509650b3a9466ee478c4"],"isSpecific ":false,"geoFence":[],"character":{"logoUrl1":"https://resource-city.sensoro.com/1542702608103935.png","logoUrl":"https://resource-city.sensoro.com/1543475323312911.jpeg","sms":{"alarm":"1844880","ok":"18448845"},"isApply":false,"domain":"city-pro-test.sensoro.com","shortName":"南京思十亦。","fullName":"南京思十亦电子科技有限公司"},"config":{"businessCount":18,"businessLimit":402},"cases":["angle","cover,level","light","distance","temperature,humidity","smoke","pm2_5,pm10","no2","co2"],"roles":"dealers","province":"北京","city":"北京","region":"mainland China","grants":{},"id":"590c235044aa4369905d455b","phone":"13838383838","depth":3,"addUserEnable":true}
     * info : {"type":"抓拍机","version":"SENSORO","brand":"SENSORO","cid":"540409873","sn":"001C2711A980","platform":false,"latitude":40.016257,"longitude":116.50478,"deviceStatus":"0"}
     * mobilePhone : 18769090237
     * id : 5caaf4cd5f28d000108fe841
     */

    private String createTime;
    private String _id;
    private String sn;
    private String cid;
    private String name;
//    private DeviceCameraUseridBean userid;
    private InfoBean info;
    private String mobilePhone;
    private String id;
    private String orientation;
    private String installationMode;
    private String deviceStatus;//摄像机在线状态


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

//    public DeviceCameraUseridBean getUserid() {
//        return userid;
//    }

//    public void setUserid(DeviceCameraUseridBean userid) {
//        this.userid = userid;
//    }

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

    public static class InfoBean implements Serializable{
        /**
         * type : 抓拍机
         * version : SENSORO
         * brand : SENSORO
         * cid : 540409873
         * sn : 001C2711A980
         * platform : false
         * latitude : 40.016257
         * longitude : 116.50478
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

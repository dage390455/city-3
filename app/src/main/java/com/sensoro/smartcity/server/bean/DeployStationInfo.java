package com.sensoro.smartcity.server.bean;

import java.util.List;

public class DeployStationInfo {
    /**
     * _id : 59cc615d93f92a1e58d3805b
     * appId : 50I35FhvOAw9
     * users : {"_id":"590c235044aa4369905d455b","updatedTime":1529484942657,"nickname":"DEMO",
     * "appId":"50I35FhvOAw9","appKey":"jZuYQtqsQeP1bTVtsI63aBP4cuVKTdJ1Syws0BUrunt",
     * "appSecret":"LPMKMqgSfTmXGRwd2vcM6jpPgISZ44aW","addBy":"590bfad00771ed116b1df421","contacts":"15210505093",
     * "contactsName":"是","createdTime":1493967696917,"noticeLevel":"senior","isCopySubUserAlarmMsg":true,
     * "binding":["5ab366b931e8d506daadc894","5adef07b362c4f2f376b43cb"],"isSpecific":false,"isStop":false,
     * "isDeleted":false,"showAllDevice":true,"geoFence":[],"character":{"logoUrl1":"http://7u2jeb.com1.z0.glb
     * .clouddn.com/bb56c20a864b6f2b6658a1fa2f607d07.png","logoUrl":"http://7u2jeb.com1.z0.glb.clouddn
     * .com/bb56c20a864b6f2b6658a1fa2f607d07.png","sms":{"alarm":1844880,"ok":1844884},"isApply":false,
     * "domain":"city-pro-test.sensoro.com","shortName":"南京思十亦。","fullName":"南京思十亦电子科技有限公司"},
     * "config":{"businessCount":6,"businessLimit":10},"cases":["angle","cover,level","light","distance",
     * "temperature,humidity","smoke","pm2_5,pm10","no2","co2"],"roles":"dealers","province":"北京","city":"北京",
     * "grants":{},"id":"590c235044aa4369905d455b"}
     * sn : 01707E17C681E5AE
     * status : offline
     * normalStatus : 4
     * type : gateway
     * firmwareVersion : 0.7.0
     * hardwareVersion : F5
     * netacm : ethernet
     * updatedTime : 1506566493429
     * lonlatLabel : [0,0]
     * lonlat : [0,0]
     * tags : ["小基站"]
     * id : 59cc615d93f92a1e58d3805b
     */
    private String _id;
    private String appId;
    //    private UsersBean users;
    private String sn;
    private String status;
    private int normalStatus;
    private String type;
    private String firmwareVersion;
    private String hardwareVersion;
    private String netacm;
    private String name;
    private long updatedTime;
    private String id;
    private List<Double> lonlatLabel;
    private List<Double> lonlat;
    private List<String> tags;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNormalStatus() {
        return normalStatus;
    }

    public void setNormalStatus(int normalStatus) {
        this.normalStatus = normalStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getNetacm() {
        return netacm;
    }

    public void setNetacm(String netacm) {
        this.netacm = netacm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Double> getLonlatLabel() {
        return lonlatLabel;
    }

    public void setLonlatLabel(List<Double> lonlatLabel) {
        this.lonlatLabel = lonlatLabel;
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


    @Override
    public String toString() {
        return "DeployStationInfo{" +
                "_id='" + _id + '\'' +
                ", appId='" + appId + '\'' +
                ", sn='" + sn + '\'' +
                ", status='" + status + '\'' +
                ", normalStatus=" + normalStatus +
                ", type='" + type + '\'' +
                ", firmwareVersion='" + firmwareVersion + '\'' +
                ", hardwareVersion='" + hardwareVersion + '\'' +
                ", netacm='" + netacm + '\'' +
                ", name='" + name + '\'' +
                ", updatedTime=" + updatedTime +
                ", id='" + id + '\'' +
                ", lonlatLabel=" + lonlatLabel +
                ", lonlat=" + lonlat +
                ", tags=" + tags +
                '}';
    }
}

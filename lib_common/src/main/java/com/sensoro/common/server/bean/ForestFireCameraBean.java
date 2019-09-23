package com.sensoro.common.server.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: jack
 * 时  间: 2019-09-18
 * 包  名: com.sensoro.forestfire.model
 * 简  述: <功能简述>
 */
public class ForestFireCameraBean implements Serializable {

    /**
     * sn : 001C2710D9CB
     * name : 张家口云顶滑雪场A山山顶
     * label : []
     * createTime : 2019-04-09T15:57:09.425Z
     * _id : 5cacc0d8dbb2ef06c2b2321a
     * userid : cityId
     * state : 1
     * info : {"type":"3","deviceId":"5d6f3c7854996c0ee0e34365","deviceStatus":1,"location":"张家口A山山顶","longitude":"39.9953284","latitude":"116.4783978"}
     * channelIds : [1,2]
     * ptzControl : {"1":false,"2":false}
     * forestCid : {"1":"540672046","2":"540672047"}
     * forestGateway : {"cigId":"204117050002","name":"张家口云顶滑雪场A山网关1"}
     */

    private String sn;
    private String name;
    private long createTime;
    private String _id;
    private String userid;
    private int state;
    private InfoBean info;
    private PtzControlBean ptzControl;
    private ForestCidBean forestCid;
    private ForestGatewayBean forestGateway;
    private List<String> label;
    private List<Integer> channelIds;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public PtzControlBean getPtzControl() {
        return ptzControl;
    }

    public void setPtzControl(PtzControlBean ptzControl) {
        this.ptzControl = ptzControl;
    }

    public ForestCidBean getForestCid() {
        return forestCid;
    }

    public void setForestCid(ForestCidBean forestCid) {
        this.forestCid = forestCid;
    }

    public ForestGatewayBean getForestGateway() {
        return forestGateway;
    }

    public void setForestGateway(ForestGatewayBean forestGateway) {
        this.forestGateway = forestGateway;
    }

    public List<String> getLabel() {
        return label;
    }

    public void setLabel(List<String> label) {
        this.label = label;
    }

    public List<Integer> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<Integer> channelIds) {
        this.channelIds = channelIds;
    }

    public static class InfoBean implements Serializable{
        /**
         * type : 3
         * deviceId : 5d6f3c7854996c0ee0e34365
         * deviceStatus : 1
         * location : 张家口A山山顶
         * longitude : 39.9953284
         * latitude : 116.4783978
         */

        private String type;
        private String deviceId;
        private int deviceStatus;
        private String location;
        private double longitude;
        private double latitude;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public int getDeviceStatus() {
            return deviceStatus;
        }

        public void setDeviceStatus(int deviceStatus) {
            this.deviceStatus = deviceStatus;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }
    }

    public static class PtzControlBean  implements Serializable{
        /**
         * 1 : false
         * 2 : false
         */

        @SerializedName("1")
        private boolean contorl1;
        @SerializedName("2")
        private boolean contorl2;


        public boolean isContorl1() {
            return contorl1;
        }

        public void setContorl1(boolean contorl1) {
            this.contorl1 = contorl1;
        }

        public boolean isContorl2() {
            return contorl2;
        }

        public void setContorl2(boolean contorl2) {
            this.contorl2 = contorl2;
        }
    }

    public static class ForestCidBean  implements Serializable{
        /**
         * 1 : 540672046
         * 2 : 540672047
         */

        @SerializedName("1")
        private String cid1;
        @SerializedName("2")
        private String cid2;

        public String getCid1() {
            return cid1;
        }

        public void setCid1(String cid1) {
            this.cid1 = cid1;
        }

        public String getCid2() {
            return cid2;
        }

        public void setCid2(String cid2) {
            this.cid2 = cid2;
        }
    }

    public static class ForestGatewayBean  implements Serializable{
        /**
         * cigId : 204117050002
         * name : 张家口云顶滑雪场A山网关1
         */

        private String cigId;
        private String name;

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
    }
}

package com.sensoro.common.server.bean;

import com.sensoro.common.model.DeviceNotificationBean;

import java.io.Serializable;
import java.util.List;

public class ForestGatewayDeployInfo implements Serializable {

    /**
     * list : [{"status":true,"label":["测试1","test2"],"notifications":[{"contact":"测试1","content":"13811111111","types":"phone"},{"contact":"测试2","content":"13811111112","types":"phone"}],"installationImage":["https://img.sensoro.com/wangguan1.jpg"],"isDeleted":false,"createTime":1568964413316,"_id":"5d847f7771d0bd8350baca6d","cigId":"204117050002","name":"测试网关2","userid":{"id":"5b86438092bb4b66f7621a7f","nickname":"工厂测试"},"location":"张家口A山","latitude":"39.9953284","longitude":"116.4783978","installationLocation":"5号楼105室第二个机柜","installationInfo":"部署信息","id":"5d847f7771d0bd8350baca6d"}]
     * count : 1
     * titleInfo : {"all":1,"online":1,"offline":0}
     */

    private int count;
    //暂不解析
//    private ForestGatewayDeployInfoTitleInfo titleInfo;
    private List<ForestGatewayDeployInfoListData> list;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

//    public ForestGatewayDeployInfoTitleInfo getTitleInfo() {
//        return titleInfo;
//    }
//
//    public void setTitleInfo(ForestGatewayDeployInfoTitleInfo titleInfo) {
//        this.titleInfo = titleInfo;
//    }

    public List<ForestGatewayDeployInfoListData> getList() {
        return list;
    }

    public void setList(List<ForestGatewayDeployInfoListData> list) {
        this.list = list;
    }

//    public static class ForestGatewayDeployInfoTitleInfo implements Serializable{
//        /**
//         * all : 1
//         * online : 1
//         * offline : 0
//         */
//
//        private int all;
//        private int online;
//        private int offline;
//
//        public int getAll() {
//            return all;
//        }
//
//        public void setAll(int all) {
//            this.all = all;
//        }
//
//        public int getOnline() {
//            return online;
//        }
//
//        public void setOnline(int online) {
//            this.online = online;
//        }
//
//        public int getOffline() {
//            return offline;
//        }
//
//        public void setOffline(int offline) {
//            this.offline = offline;
//        }
//    }

    public static class ForestGatewayDeployInfoListData implements Serializable{
        /**
         * status : true
         * label : ["测试1","test2"]
         * notifications : [{"contact":"测试1","content":"13811111111","types":"phone"},{"contact":"测试2","content":"13811111112","types":"phone"}]
         * installationImage : ["https://img.sensoro.com/wangguan1.jpg"]
         * isDeleted : false
         * createTime : 1568964413316
         * _id : 5d847f7771d0bd8350baca6d
         * cigId : 204117050002
         * name : 测试网关2
         * userid : {"id":"5b86438092bb4b66f7621a7f","nickname":"工厂测试"}
         * location : 张家口A山
         * latitude : 39.9953284
         * longitude : 116.4783978
         * installationLocation : 5号楼105室第二个机柜
         * installationInfo : 部署信息
         * id : 5d847f7771d0bd8350baca6d
         */

        private boolean status;
        private boolean isDeleted;
        private long createTime;
        private String _id;
        private String cigId;
        private String name;
//        private UseridBean userid;
        private String location;
        private double latitude;
        private double longitude;
        private String installationLocation;
        private String installationInfo;
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

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

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

//        public UseridBean getUserid() {
//            return userid;
//        }
//
//        public void setUserid(UseridBean userid) {
//            this.userid = userid;
//        }

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

        public String getInstallationInfo() {
            return installationInfo;
        }

        public void setInstallationInfo(String installationInfo) {
            this.installationInfo = installationInfo;
        }

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

//        public static class UseridBean {
//            /**
//             * id : 5b86438092bb4b66f7621a7f
//             * nickname : 工厂测试
//             */
//
//            private String id;
//            private String nickname;
//
//            public String getId() {
//                return id;
//            }
//
//            public void setId(String id) {
//                this.id = id;
//            }
//
//            public String getNickname() {
//                return nickname;
//            }
//
//            public void setNickname(String nickname) {
//                this.nickname = nickname;
//            }
//        }
    }
}

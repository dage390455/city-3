package com.sensoro.common.server.bean;

import java.io.Serializable;
import java.util.List;

public class DeviceCameraUseridBean implements Serializable {
    /**
     * _id : 590c235044aa4369905d455b
     * updatedTime : 1529484942657
     * nickname : DEMO
     * appId : 50I35FhvOAw9
     * appKey : jZuYQtqsQeP1bTVtsI63aBP4cuVKTdJ1Syws0BUrunt
     * appSecret : LPMKMqgSfTmXGRwd2vcM6jpPgISZ44aW
     * addBy : 590bfad00771ed116b1df421
     * contacts : 15210505093
     * contactsName : 是
     * area : 崇文区
     * manager : {"contacts":"15110041945","name":"高鹏"}
     * deviceCount : 227
     * stationCount : 8
     * syncDeviceStationCountTime : 1486199018302
     * createdTime : 1493967696917
     * payInfo : {"sub_mch_id":"13962862021","card_id":"91110105306401130D","name":"赵武阳","mobilePhone":"18601118681","company":"北京升哲科技有限公司","address":"北京市朝阳区望京街10号望京SOHO1号楼B座2801/2807","phone":"400-6863180","province":"HN","city":"HK","business":"ATT","bank":"招商银行股份有限公司北京望京支行","bankNumber":"62030122000696581","fdd_id":"1FD44C56AA4CADD4EFBAE39EBEB25644","isTest":true}
     * noticeLevel : senior
     * isCopySubUserAlarmMsg : true
     * isStop : false
     * isDeleted : false
     * showAllDevice : true
     * binding : ["5adef07b362c4f2f376b43cb","5af40996d631bb4d0136c715","5bf655ccc0185c78346909c3","5bffb0082cbdbd1e8716c4ef","5c17466ae2177c52d24536a8","5c24a0ee06af1ca6646cbe52","5c6faabbdd6a62a2dc039b7a","5c04f231724d5561d5f7a1c2","5c90509650b3a9466ee478c4"]
     * isSpecific  : false
     * geoFence : []
     * character : {"logoUrl1":"https://resource-city.sensoro.com/1542702608103935.png","logoUrl":"https://resource-city.sensoro.com/1543475323312911.jpeg","sms":{"alarm":"1844880","ok":"18448845"},"isApply":false,"domain":"city-pro-test.sensoro.com","shortName":"南京思十亦。","fullName":"南京思十亦电子科技有限公司"}
     * config : {"businessCount":18,"businessLimit":402}
     * cases : ["angle","cover,level","light","distance","temperature,humidity","smoke","pm2_5,pm10","no2","co2"]
     * roles : dealers
     * province : 北京
     * city : 北京
     * region : mainland China
     * grants : {}
     * id : 590c235044aa4369905d455b
     * phone : 13838383838
     * depth : 3
     * addUserEnable : true
     */

    private String _id;
    private long updatedTime;
    private String nickname;
    private String appId;
    private String appKey;
    private String appSecret;
    private String addBy;
    private String contacts;
    private String contactsName;
    private String area;
    private DeviceCameraUserManagerBean manager;
    private int deviceCount;
    private int stationCount;
    private long syncDeviceStationCountTime;
    private long createdTime;
    private DeviceCameraUserPayInfoBean payInfo;
    private String noticeLevel;
    private boolean isCopySubUserAlarmMsg;
    private boolean isStop;
    private boolean isDeleted;
    private boolean showAllDevice;
    private boolean isSpecific;
    private CharacterBean character;
    private ConfigBean config;
    private String roles;
    private String province;
    private String city;
    private String region;
    private GrantsBean grants;
    private String id;
    private String phone;
    private int depth;
    private boolean addUserEnable;
    private List<String> binding;
    private List<?> geoFence;
    private List<String> cases;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getAddBy() {
        return addBy;
    }

    public void setAddBy(String addBy) {
        this.addBy = addBy;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getContactsName() {
        return contactsName;
    }

    public void setContactsName(String contactsName) {
        this.contactsName = contactsName;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public DeviceCameraUserManagerBean getManager() {
        return manager;
    }

    public void setManager(DeviceCameraUserManagerBean manager) {
        this.manager = manager;
    }

    public int getDeviceCount() {
        return deviceCount;
    }

    public void setDeviceCount(int deviceCount) {
        this.deviceCount = deviceCount;
    }

    public int getStationCount() {
        return stationCount;
    }

    public void setStationCount(int stationCount) {
        this.stationCount = stationCount;
    }

    public long getSyncDeviceStationCountTime() {
        return syncDeviceStationCountTime;
    }

    public void setSyncDeviceStationCountTime(long syncDeviceStationCountTime) {
        this.syncDeviceStationCountTime = syncDeviceStationCountTime;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public DeviceCameraUserPayInfoBean getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(DeviceCameraUserPayInfoBean payInfo) {
        this.payInfo = payInfo;
    }

    public String getNoticeLevel() {
        return noticeLevel;
    }

    public void setNoticeLevel(String noticeLevel) {
        this.noticeLevel = noticeLevel;
    }

    public boolean isIsCopySubUserAlarmMsg() {
        return isCopySubUserAlarmMsg;
    }

    public void setIsCopySubUserAlarmMsg(boolean isCopySubUserAlarmMsg) {
        this.isCopySubUserAlarmMsg = isCopySubUserAlarmMsg;
    }

    public boolean isIsStop() {
        return isStop;
    }

    public void setIsStop(boolean isStop) {
        this.isStop = isStop;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean isShowAllDevice() {
        return showAllDevice;
    }

    public void setShowAllDevice(boolean showAllDevice) {
        this.showAllDevice = showAllDevice;
    }

    public boolean isIsSpecific() {
        return isSpecific;
    }

    public void setIsSpecific(boolean isSpecific) {
        this.isSpecific = isSpecific;
    }

    public CharacterBean getCharacter() {
        return character;
    }

    public void setCharacter(CharacterBean character) {
        this.character = character;
    }

    public ConfigBean getConfig() {
        return config;
    }

    public void setConfig(ConfigBean config) {
        this.config = config;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public GrantsBean getGrants() {
        return grants;
    }

    public void setGrants(GrantsBean grants) {
        this.grants = grants;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isAddUserEnable() {
        return addUserEnable;
    }

    public void setAddUserEnable(boolean addUserEnable) {
        this.addUserEnable = addUserEnable;
    }

    public List<String> getBinding() {
        return binding;
    }

    public void setBinding(List<String> binding) {
        this.binding = binding;
    }

    public List<?> getGeoFence() {
        return geoFence;
    }

    public void setGeoFence(List<?> geoFence) {
        this.geoFence = geoFence;
    }

    public List<String> getCases() {
        return cases;
    }

    public void setCases(List<String> cases) {
        this.cases = cases;
    }


    public static class CharacterBean implements Serializable {
        /**
         * logoUrl1 : https://resource-city.sensoro.com/1542702608103935.png
         * logoUrl : https://resource-city.sensoro.com/1543475323312911.jpeg
         * sms : {"alarm":"1844880","ok":"18448845"}
         * isApply : false
         * domain : city-pro-test.sensoro.com
         * shortName : 南京思十亦。
         * fullName : 南京思十亦电子科技有限公司
         */

        private String logoUrl1;
        private String logoUrl;
        private SmsBean sms;
        private boolean isApply;
        private String domain;
        private String shortName;
        private String fullName;

        public String getLogoUrl1() {
            return logoUrl1;
        }

        public void setLogoUrl1(String logoUrl1) {
            this.logoUrl1 = logoUrl1;
        }

        public String getLogoUrl() {
            return logoUrl;
        }

        public void setLogoUrl(String logoUrl) {
            this.logoUrl = logoUrl;
        }

        public SmsBean getSms() {
            return sms;
        }

        public void setSms(SmsBean sms) {
            this.sms = sms;
        }

        public boolean isIsApply() {
            return isApply;
        }

        public void setIsApply(boolean isApply) {
            this.isApply = isApply;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getShortName() {
            return shortName;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public static class SmsBean {
            /**
             * alarm : 1844880
             * ok : 18448845
             */

            private String alarm;
            private String ok;

            public String getAlarm() {
                return alarm;
            }

            public void setAlarm(String alarm) {
                this.alarm = alarm;
            }

            public String getOk() {
                return ok;
            }

            public void setOk(String ok) {
                this.ok = ok;
            }
        }
    }

    public static class ConfigBean implements Serializable {
    }

    public static class GrantsBean implements Serializable {
    }
}

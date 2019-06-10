package com.sensoro.common.server.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class MalfunctionListInfo implements Serializable {

    private String _id;
    private String malfunctionType;
    private Map<String, MalfunctionDataBean> malfunctionData;
    private String appId;
    private OwnersBean owners;
    private String deviceSN;
    private String deviceName;
    private String deviceType;
    private String unionType;
    private int __v;
    private long createdTime;
    private long updatedTime;
    private boolean isDeleted;
    private DeviceNotificationBean deviceNotification;
    private int malfunctionStatus;
    private String id;
    private String _updatedTime;
    private String _createdTime;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getMalfunctionType() {
        return malfunctionType;
    }

    public void setMalfunctionType(String malfunctionType) {
        this.malfunctionType = malfunctionType;
    }

    public Map<String, MalfunctionDataBean> getMalfunctionData() {
        return malfunctionData;
    }

    public void setMalfunctionData(Map malfunctionData) {
        this.malfunctionData = malfunctionData;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public OwnersBean getOwners() {
        return owners;
    }

    public void setOwners(OwnersBean owners) {
        this.owners = owners;
    }

    public String getDeviceSN() {
        return deviceSN;
    }

    public void setDeviceSN(String deviceSN) {
        this.deviceSN = deviceSN;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getUnionType() {
        return unionType;
    }

    public void setUnionType(String unionType) {
        this.unionType = unionType;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public DeviceNotificationBean getDeviceNotification() {
        return deviceNotification;
    }

    public void setDeviceNotification(DeviceNotificationBean deviceNotification) {
        this.deviceNotification = deviceNotification;
    }

    public int getMalfunctionStatus() {
        return malfunctionStatus;
    }

    public void setMalfunctionStatus(int malfunctionStatus) {
        this.malfunctionStatus = malfunctionStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String get_updatedTime() {
        return _updatedTime;
    }

    public void set_updatedTime(String _updatedTime) {
        this._updatedTime = _updatedTime;
    }

    public String get_createdTime() {
        return _createdTime;
    }

    public void set_createdTime(String _createdTime) {
        this._createdTime = _createdTime;
    }

    public List<RecordsBean> getRecords() {
        return records;
    }

    public void setRecords(List<RecordsBean> records) {
        this.records = records;
    }

    public List<?> getPhoneList() {
        return phoneList;
    }

    public void setPhoneList(List<?> phoneList) {
        this.phoneList = phoneList;
    }

    public List<Double> getDeviceLonlat() {
        return deviceLonlat;
    }

    public void setDeviceLonlat(List<Double> deviceLonlat) {
        this.deviceLonlat = deviceLonlat;
    }

    private List<RecordsBean> records;
    private List<?> phoneList;
    private List<Double> deviceLonlat;

    public static class OwnersBean implements Serializable {

        /**
         * _id : 507f1f77bcf86cd799100001
         * nickname : dealers1
         * appId : appId123890
         * roles : dealers
         * grants : {}
         * id : 507f1f77bcf86cd799100001
         */

        private String _id;
        private String nickname;
        private String appId;
        private String roles;
        private GrantsBean grants;
        private String id;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
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

        public String getRoles() {
            return roles;
        }

        public void setRoles(String roles) {
            this.roles = roles;
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

        public static class GrantsBean implements Serializable {
        }
    }

    public static class DeviceNotificationBean implements Serializable {

        /**
         * types : phone
         */

        private String types;
        private String contact;
        private String content;

        public String getTypes() {
            return types;
        }

        public void setTypes(String types) {
            this.types = types;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class RecordsBean implements Serializable {

        /**
         * _id : 5bcecdd6efa52e6a329b06c8
         * malfunctions : 5bcecdd6efa52e6a329b06c7
         * malfunctionType : circuitShort
         * malfunctionText : 传感器短路
         * __v : 0
         * updatedTime : 1540279766624
         * type : malfunction
         * id : 5bcecdd6efa52e6a329b06c8
         * _updatedTime : 2018-10-23 15:29:26
         */

        private String _id;
        private String malfunctions;
        private String malfunctionType;
        private String malfunctionText;
        private int __v;
        private long updatedTime;
        private String type;
        private String id;
        private String _updatedTime;
        private String status;
        private String malfunctionSubType;
        public String getMalfunctionSubType() {
            return malfunctionSubType;
        }

        public void setMalfunctionSubType(String malfunctionSubType) {
            this.malfunctionSubType = malfunctionSubType;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<Event> getPhoneList() {
            return phoneList;
        }

        public void setPhoneList(List<Event> phoneList) {
            this.phoneList = phoneList;
        }

        private List<Event> phoneList;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getMalfunctions() {
            return malfunctions;
        }

        public void setMalfunctions(String malfunctions) {
            this.malfunctions = malfunctions;
        }

        public String getMalfunctionType() {
            return malfunctionType;
        }

        public void setMalfunctionType(String malfunctionType) {
            this.malfunctionType = malfunctionType;
        }

        public String getMalfunctionText() {
            return malfunctionText;
        }

        public void setMalfunctionText(String malfunctionText) {
            this.malfunctionText = malfunctionText;
        }

        public int get__v() {
            return __v;
        }

        public void set__v(int __v) {
            this.__v = __v;
        }

        public long getUpdatedTime() {
            return updatedTime;
        }

        public void setUpdatedTime(long updatedTime) {
            this.updatedTime = updatedTime;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String get_updatedTime() {
            return _updatedTime;
        }

        public void set_updatedTime(String _updatedTime) {
            this._updatedTime = _updatedTime;
        }

        public static class Event implements Serializable {

            /**
             * source : notification
             * name : nathan
             * level : 0
             * number : 13363294078
             * IS_OVER_LIMIT : false
             * sid : 13363294078
             * count : 1
             * reciveStatus : 0
             * _id : 5bd292a38ea78451461cc7a4
             * retry : 0
             */

            private String source;
            private String name;
            private int level;
            private String number;
            private boolean IS_OVER_LIMIT;
            private String sid;
            private int count;
            private int reciveStatus;
            private String _id;
            private int retry;

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getLevel() {
                return level;
            }

            public void setLevel(int level) {
                this.level = level;
            }

            public String getNumber() {
                return number;
            }

            public void setNumber(String number) {
                this.number = number;
            }

            public boolean isIS_OVER_LIMIT() {
                return IS_OVER_LIMIT;
            }

            public void setIS_OVER_LIMIT(boolean IS_OVER_LIMIT) {
                this.IS_OVER_LIMIT = IS_OVER_LIMIT;
            }

            public String getSid() {
                return sid;
            }

            public void setSid(String sid) {
                this.sid = sid;
            }

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }

            public int getReciveStatus() {
                return reciveStatus;
            }

            public void setReciveStatus(int reciveStatus) {
                this.reciveStatus = reciveStatus;
            }

            public String get_id() {
                return _id;
            }

            public void set_id(String _id) {
                this._id = _id;
            }

            public int getRetry() {
                return retry;
            }

            public void setRetry(int retry) {
                this.retry = retry;
            }
        }
    }

}

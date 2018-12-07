package com.sensoro.smartcity.server.bean;

import java.util.List;

public class InspectionTaskExceptionDeviceModel {

    private String _id;
    private String sn;
    private String taskId;
    private String deviceType;
    private String owners;
    private int __v;
    private String remark;
    private String finishTime;
    private String startTime;
    private int malfunctionHandle;
    private int inService;
    private int status;
    private DeviceBean device;
    private List<ScenesData> imgAndVideo;
    private List<String> malfunctions;

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

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getOwners() {
        return owners;
    }

    public void setOwners(String owners) {
        this.owners = owners;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getMalfunctionHandle() {
        return malfunctionHandle;
    }

    public void setMalfunctionHandle(int malfunctionHandle) {
        this.malfunctionHandle = malfunctionHandle;
    }

    public int getInService() {
        return inService;
    }

    public void setInService(int inService) {
        this.inService = inService;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public DeviceBean getDevice() {
        return device;
    }

    public void setDevice(DeviceBean device) {
        this.device = device;
    }

    public void setImgAndVedio(List<ScenesData> imgAndVedio) {
        this.imgAndVideo = imgAndVedio;
    }

    public List<ScenesData> getImgAndVedio() {
        return imgAndVideo;
    }

    public List<String> getMalfunctions() {
        return malfunctions;
    }

    public void setMalfunctions(List<String> malfunctions) {
        this.malfunctions = malfunctions;
    }

    public static class DeviceBean {
        /**
         * name : 望京SOHO塔1B座2807-035
         * unionType : smoke
         * alarms : {"notification":{"contact":"wang","content":"17813456890","types":"phone"},"mapping":[]}
         * lonlat : [116.482118,39.996104]
         * tags : ["SENSORO"]
         * deviceType : smoke
         */

        private String name;
        private String unionType;
        private AlarmsBean alarms;
        private String deviceType;
        private List<Double> lonlat;
        private List<String> tags;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUnionType() {
            return unionType;
        }

        public void setUnionType(String unionType) {
            this.unionType = unionType;
        }

        public AlarmsBean getAlarms() {
            return alarms;
        }

        public void setAlarms(AlarmsBean alarms) {
            this.alarms = alarms;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
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

        public static class AlarmsBean {
            /**
             * notification : {"contact":"wang","content":"17813456890","types":"phone"}
             * mapping : []
             */

            private NotificationBean notification;
            private List<?> mapping;

            public NotificationBean getNotification() {
                return notification;
            }

            public void setNotification(NotificationBean notification) {
                this.notification = notification;
            }

            public List<?> getMapping() {
                return mapping;
            }

            public void setMapping(List<?> mapping) {
                this.mapping = mapping;
            }

            public static class NotificationBean {
                /**
                 * contact : wang
                 * content : 17813456890
                 * types : phone
                 */

                private String contact;
                private String content;
                private String types;

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

                public String getTypes() {
                    return types;
                }

                public void setTypes(String types) {
                    this.types = types;
                }
            }
        }
    }

    public static class ImgAndVideoBean {
        /**
         * _id : 5ba9f433b428a3795599a180
         * url : https://resource-city.sensoro.com/69CE9436121526FCE5B5084FD64E6972
         * type : image
         */

        private String _id;
        private String url;
        private String type;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}

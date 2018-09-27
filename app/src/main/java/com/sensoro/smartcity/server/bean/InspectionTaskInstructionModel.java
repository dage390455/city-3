package com.sensoro.smartcity.server.bean;

import java.util.List;

public class InspectionTaskInstructionModel {

    /**
     * _id : 5b98f40f7a550e06577f3f4e
     * deviceType : temp_humi_one
     * operator : 5b516460e80bf5ff7638fa2c
     * __v : 0
     * isDelete : false
     * blueTooth : [{"text":"text","title":"title","images":["http://xx.png"]}]
     * data : [{"title":"title","text":"text","_id":"5b98f40f7a550e06577f3f4f","images":["http://xx.png"]}]
     * updatedTime : 1536750607702
     * createdTime : 1536750607702
     */

    private String _id;
    private String deviceType;
    private String operator;
    private int __v;
    private boolean isDelete;
    private long updatedTime;
    private long createdTime;
    private List<BlueToothBean> blueTooth;
    private List<DataBean> data;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }

    public boolean isIsDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public List<BlueToothBean> getBlueTooth() {
        return blueTooth;
    }

    public void setBlueTooth(List<BlueToothBean> blueTooth) {
        this.blueTooth = blueTooth;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class BlueToothBean {
        /**
         * text : text
         * title : title
         * images : ["http://xx.png"]
         */

        private String text;
        private String title;
        private List<String> images;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }
    }

    public static class DataBean {
        /**
         * title : title
         * text : text
         * _id : 5b98f40f7a550e06577f3f4f
         * images : ["http://xx.png"]
         */

        private String title;
        private String text;
        private String _id;
        private List<String> images;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }
    }
}

package com.sensoro.common.model;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: jack
 * 时  间: 2019-09-18
 * 包  名: com.sensoro.forestfire.model
 * 简  述: <功能简述>
 */
public  class ForestFireCameraListInfo implements Serializable {


    /**
     * list : [{"cigId":"204117050002","name":"测试网关","status":"true","userid":"5d149c91ae13e705a90c65a9","label":["测试1","test2"],"longitude":"39.9953284","latitude":"116.4783978","isDeleted":false,"createTime":"2019-09-12T04:27:07.352Z"}]
     * count : 1
     * titleInfo : {"all":1,"online":1,"offline":0}
     */

    private int count;
    private TitleInfoBean titleInfo;
    private List<ForestFireCameraBean> list;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public TitleInfoBean getTitleInfo() {
        return titleInfo;
    }

    public void setTitleInfo(TitleInfoBean titleInfo) {
        this.titleInfo = titleInfo;
    }

    public List<ForestFireCameraBean> getList() {
        return list;
    }

    public void setList(List<ForestFireCameraBean> list) {
        this.list = list;
    }

    public static class TitleInfoBean implements  Serializable{
        /**
         * all : 1
         * online : 1
         * offline : 0
         */

        private int all;
        private int online;
        private int offline;
        private int warning;

        public int getAll() {
            return all;
        }

        public void setAll(int all) {
            this.all = all;
        }

        public int getOnline() {
            return online;
        }

        public void setOnline(int online) {
            this.online = online;
        }

        public int getOffline() {
            return offline;
        }

        public void setOffline(int offline) {
            this.offline = offline;
        }

        public int getWarning() {
            return warning;
        }

        public void setWarning(int warning) {
            this.warning = warning;
        }
    }

}

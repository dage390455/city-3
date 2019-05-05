package com.sensoro.smartcity.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CameraFilterModel {

    /**
     * errcode : 0
     * data : [{"key":"deviceStatus","title":"摄像机状态","list":[{"code":"","name":"全部"},{"code":"1","name":"在线"},{"code":"0","name":"离线"}],"multi":false},{"key":"orientation","title":"镜头朝向","list":[{"id":"1006279","code":"112901","name":"正东朝向","typeCode":"112900","isValid":true},{"id":"1006280","code":"112902","name":"正西朝向","typeCode":"112900","isValid":true},{"id":"1006281","code":"112903","name":"正南朝向","typeCode":"112900","isValid":true},{"id":"1006282","code":"112904","name":"正北朝向","typeCode":"112900","isValid":true},{"id":"1006283","code":"112905","name":"东南朝向","typeCode":"112900","isValid":true},{"id":"1006284","code":"112906","name":"东北朝向","typeCode":"112900","isValid":true},{"id":"1006285","code":"112907","name":"西南朝向","typeCode":"112900","isValid":true},{"id":"1006286","code":"112908","name":"西北朝向","typeCode":"112900","isValid":true},{"id":"1006278","code":"112909","name":"其他","typeCode":"112900","isValid":true}],"multi":true},{"key":"installationMode","title":"安装方式","list":[{"id":"1005616","code":"101801","name":"支架","typeCode":"101800","isValid":true},{"id":"1005617","code":"101802","name":"吊顶","typeCode":"101800","isValid":true},{"id":"1005618","code":"101803","name":"壁装","typeCode":"101800","isValid":true},{"id":"1005619","code":"101804","name":"立杆（大于8米）","typeCode":"101800","isValid":true},{"id":"1005620","code":"101805","name":"立杆（6-8米）","typeCode":"101800","isValid":true},{"id":"1005621","code":"101806","name":"立杆（小于6米）","typeCode":"101800","isValid":true},{"id":"1005622","code":"101807","name":"悬臂托装","typeCode":"101800","isValid":true},{"id":"1005623","code":"101808","name":"悬臂吊装","typeCode":"101800","isValid":true}],"multi":true}]
     */


    /**
     * key : deviceStatus
     * title : 摄像机状态
     * list : [{"code":"","name":"全部"},{"code":"1","name":"在线"},{"code":"0","name":"离线"}]
     * multi : false
     */

    private String key;
    private String title;
    private boolean multi;
    @SerializedName("list")
    private List<ListBean> list;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isMulti() {
        return multi;
    }

    public void setMulti(boolean multi) {
        this.multi = multi;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setListX(List<ListBean> listX) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * code :
         * name : 全部
         */

        private String code;
        private String name;
        private boolean isSelect;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }
    }
}

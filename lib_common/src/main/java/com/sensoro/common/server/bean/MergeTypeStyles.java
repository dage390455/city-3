package com.sensoro.common.server.bean;

import java.util.List;

public class MergeTypeStyles {
    private String id;
    private String name;
    private String image;
    private String icon;
    private boolean isOwn;
    private List<String> deviceTypes;

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    private int resId;


    //部署安装监测时的修复说明
    private String fixSpecificationUrl;

    @Override
    public String toString() {
        return "MergeTypeStyles{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", icon='" + icon + '\'' +
                ", deviceTypes=" + deviceTypes +
                ", resId=" + resId +
                ", isOwn=" + isOwn +
                ", fixSpecificationUrl=" + fixSpecificationUrl +
                '}';
    }

    public boolean isOwn() {
        return isOwn;
    }

    public void setOwn(boolean own) {
        isOwn = own;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<String> getDeviceTypes() {
        return deviceTypes;
    }

    public void setDeviceTypes(List<String> deviceTypes) {
        this.deviceTypes = deviceTypes;
    }

    public String getFixSpecificationUrl() {
        return fixSpecificationUrl;
    }

    public void setFixSpecificationUrl(String fixSpecificationUrl) {
        this.fixSpecificationUrl = fixSpecificationUrl;
    }


}

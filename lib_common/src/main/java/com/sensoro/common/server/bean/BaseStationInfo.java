package com.sensoro.common.server.bean;

import java.io.Serializable;
import java.util.List;

public class BaseStationInfo implements Serializable {
//    : 'appId123890',
//    : '507f1f77bcf86cd799100001',
//    : '40B37F17C673F238',
//    : '光熙门',
//    status: 'normal',
//    normalStatus: 0,
//    type: 'station',
//    firmwareVersion: '1.6.2',
//    hardwareVersion: 'DD',
//    netacm: 'cellular',
//    updatedTime: 1558525715530,
//    lonlatLabel: [Array],
//    lonlat: [Array],
//    : [Array],
//    id: '5ce537131b705bd132833fe2'


    private String appId;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    private String users;
    private String name;
    private String sn;
    private String status;
    private String type;
    private List<String> tags;
}

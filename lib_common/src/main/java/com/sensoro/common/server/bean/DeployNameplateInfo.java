package com.sensoro.common.server.bean;

import java.util.List;

public class DeployNameplateInfo {
    /**
     * name : 1
     * deployFlag : true
     * tags : ["1","2"]
     * deployPics : ["www.baidu.com","www.baidu.com"]
     * lonlat : [15,30]
     * createdTime : 1558946772294
     * updatedTime : 1558946772294
     */

    private int name;
    private boolean deployFlag;
    private long createdTime;
    private long updatedTime;
    private List<String> tags;
    private List<String> deployPics;
    private List<Integer> lonlat;

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public boolean isDeployFlag() {
        return deployFlag;
    }

    public void setDeployFlag(boolean deployFlag) {
        this.deployFlag = deployFlag;
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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getDeployPics() {
        return deployPics;
    }

    public void setDeployPics(List<String> deployPics) {
        this.deployPics = deployPics;
    }

    public List<Integer> getLonlat() {
        return lonlat;
    }

    public void setLonlat(List<Integer> lonlat) {
        this.lonlat = lonlat;
    }
}

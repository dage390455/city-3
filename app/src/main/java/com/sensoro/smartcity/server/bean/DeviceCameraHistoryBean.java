package com.sensoro.smartcity.server.bean;

public class DeviceCameraHistoryBean {

    /**
     * beginTime : 1555581280
     * endTime : 1555581311
     * url : https://scpub-oss1.antelopecloud.cn/records/m3u8_info2/1555581280_1555581311.m3u8?access_token=540409860_3356491776_1586754482_329be6468f23f2e032c70cec70fe29c6&head=1
     */

    private String beginTime;
    private String endTime;
    private String url;

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

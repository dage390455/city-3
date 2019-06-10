package com.sensoro.common.server.bean;

public class DeviceUpdateFirmwareData {

    /**
     * version : 1.1.0
     * fromHVersion : 1.0.0
     * deviceType : test
     * url : https://...
     * band : CN470
     * checksum :
     * createdTime : 1518941975000
     */

    private String version;
    private String fromHVersion;
    private String deviceType;
    private String url;
    private String band;
    private String checksum;
    private long createdTime;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFromHVersion() {
        return fromHVersion;
    }

    public void setFromHVersion(String fromHVersion) {
        this.fromHVersion = fromHVersion;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }
}

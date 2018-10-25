package com.sensoro.smartcity.server.bean;

import java.util.List;

public class DeployDeviceDetail {

    /**
     * sn : 10320117C5A0240B
     * blePassword : 0ff2a23
     * band : SE470
     * channelMask : [0,0,0,0,0,255]
     */

    private String sn;
    private String blePassword;
    private String band;
    private List<Integer> channelMask;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getBlePassword() {
        return blePassword;
    }

    public void setBlePassword(String blePassword) {
        this.blePassword = blePassword;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public List<Integer> getChannelMask() {
        return channelMask;
    }

    public void setChannelMask(List<Integer> channelMask) {
        this.channelMask = channelMask;
    }
}

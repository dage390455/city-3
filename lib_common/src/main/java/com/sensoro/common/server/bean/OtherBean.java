package com.sensoro.common.server.bean;

import java.io.Serializable;

public class OtherBean implements Serializable {
    // "iccid": "89861118253000944283",
//         "imsi": "460113016896570",
//         "imei": "866971033532420",
//         "password": null
    private String iccid;
    private String imsi;
    private String imei;
    private String password;

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

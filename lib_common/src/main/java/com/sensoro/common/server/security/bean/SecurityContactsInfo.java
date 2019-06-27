package com.sensoro.common.server.security.bean;

import java.io.Serializable;

/**
 * 安防设备联系人
 */
public class SecurityContactsInfo implements Serializable {

    /**
     * name : 齐哲
     * mobilePhone : 13888888888
     */

    private String name;
    private String mobilePhone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }
}


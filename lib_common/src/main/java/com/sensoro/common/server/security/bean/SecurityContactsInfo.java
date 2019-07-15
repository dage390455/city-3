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

    private String mobile;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobilePhone() {
        return mobile;
    }

    public void setMobilePhone(String mobile) {
        this.mobile = mobile;
    }
}


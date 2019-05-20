package com.sensoro.smartcity.temp.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class LoginRequest implements Serializable {


    @SerializedName("loginName")
    private String loginName;
    @SerializedName("userPassword")
    private String userPassword;
    @SerializedName("identifyCode")
    private String identifyCode;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getIdentifyCode() {
        return identifyCode;
    }

    public void setIdentifyCode(String identifyCode) {
        this.identifyCode = identifyCode;
    }
}

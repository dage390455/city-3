package com.sensoro.smartcity.temp.entity;

import com.google.gson.annotations.SerializedName;

public class MessageCodeRequest {

    @SerializedName("loginName")
    private String loginName;
    @SerializedName("userPassword")
    private String userPassword;

    public MessageCodeRequest(String loginName, String userPassword) {
        this.loginName = loginName;
        this.userPassword = userPassword;
    }

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
}

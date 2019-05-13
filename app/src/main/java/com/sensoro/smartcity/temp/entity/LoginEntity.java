package com.sensoro.smartcity.temp.entity;

import java.io.Serializable;

public class LoginEntity implements Serializable {


    private int isModifyPassWord;
    private String token;
    private String userId;

    public int getIsModifyPassWord() {
        return isModifyPassWord;
    }

    public void setIsModifyPassWord(int isModifyPassWord) {
        this.isModifyPassWord = isModifyPassWord;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "LoginEntity{" +
                "isModifyPassWord=" + isModifyPassWord +
                ", token='" + token + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}

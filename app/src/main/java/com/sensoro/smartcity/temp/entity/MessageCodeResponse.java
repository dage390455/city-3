package com.sensoro.smartcity.temp.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yexiaokang on 2019/2/16.
 */
public class MessageCodeResponse {

    @SerializedName("mobile")
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}

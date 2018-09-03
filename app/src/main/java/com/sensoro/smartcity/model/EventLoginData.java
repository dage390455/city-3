package com.sensoro.smartcity.model;

import java.io.Serializable;

public final class EventLoginData implements Serializable {
    public String phoneId;
    public String userId;
    public String userName;
    public String phone;
    public String roles;
    public boolean isSupperAccount;
    public boolean hasStation;
    public boolean hasContract;
    public boolean hasScanLogin;

    @Override
    public String toString() {
        return "EventLoginData{" +
                "phoneId='" + phoneId + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", phone='" + phone + '\'' +
                ", roles='" + roles + '\'' +
                ", isSupperAccount=" + isSupperAccount +
                ", hasStation=" + hasStation +
                ", hasContract=" + hasContract +
                ", hasScanLogin=" + hasScanLogin +
                '}';
    }
}

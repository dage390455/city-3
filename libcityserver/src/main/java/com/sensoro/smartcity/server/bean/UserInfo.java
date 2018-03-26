package com.sensoro.smartcity.server.bean;

import java.io.Serializable;

/**
 * Created by sensoro on 17/7/25.
 */

public class UserInfo implements Serializable{

    private String _id;
    private String id;
    private String phone;
    private String nickname;
    private String password;
    private String cases[];
    private String appId;
    private String appKey;
    private String appSecret;
    private String roles;
    private String sessionID;
    private String controllerUid;
    private UserInfo chirldren[];
    private Character character;
    private long createdTime;
    private long updatedTime;
    private boolean isStop;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String[] getCases() {
        return cases;
    }

    public void setCases(String[] cases) {
        this.cases = cases;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserInfo[] getChirldren() {
        return chirldren;
    }

    public void setChirldren(UserInfo[] chirldren) {
        this.chirldren = chirldren;
    }

    public String getControllerUid() {
        return controllerUid;
    }

    public void setControllerUid(String controllerUid) {
        this.controllerUid = controllerUid;
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }
}

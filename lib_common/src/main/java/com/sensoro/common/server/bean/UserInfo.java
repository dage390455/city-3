package com.sensoro.common.server.bean;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by sensoro on 17/7/25.
 */

public class UserInfo implements Serializable {

    private String _id;
    private String id;
    private String contacts;
    private String nickname;
    private String password;
    private String cases[];
    private String appId;
    private String appKey;
    private String appSecret;

    private String isSpecific;
    private String roles;
    private String sessionID;
    private UserInfo chirldren[];
    private Character character;
    private long createdTime;
    private long updatedTime;
    private boolean isStop;
    private Account account;
    private boolean addUserEnable = true;
    private String controllerAid;
    private String token;

    public String getControllerAid() {
        return controllerAid;
    }

    public void setControllerAid(String controllerAid) {
        this.controllerAid = controllerAid;
    }
    public boolean isAddUserEnable() {
        return addUserEnable;
    }

    public void setAddUserEnable(boolean addUserEnable) {
        this.addUserEnable = addUserEnable;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public GrantsInfo getGrants() {
        return grants;
    }

    public void setGrants(GrantsInfo grants) {
        this.grants = grants;
    }

    private GrantsInfo grants;

    public String getIsSpecific() {
        return isSpecific;
    }

    public void setIsSpecific(String isSpecific) {
        this.isSpecific = isSpecific;
    }

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

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
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

    @Override
    public String toString() {
        return "UserInfo{" +
                "_id='" + _id + '\'' +
                ", id='" + id + '\'' +
                ", contacts='" + contacts + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                ", cases=" + Arrays.toString(cases) +
                ", appId='" + appId + '\'' +
                ", appKey='" + appKey + '\'' +
                ", appSecret='" + appSecret + '\'' +
                ", isSpecific='" + isSpecific + '\'' +
                ", roles='" + roles + '\'' +
                ", sessionID='" + sessionID + '\'' +
                ", controllerAid='" + controllerAid + '\'' +
                ", chirldren=" + Arrays.toString(chirldren) +
                ", character=" + character +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                ", isStop=" + isStop +
                '}';
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static class Account implements Serializable {
        private String id;
        private String _id;
        private String contacts;
        private boolean totpEnable;

        public boolean isTotpEnable() {
            return totpEnable;
        }

        public void setTotpEnable(boolean totpEnable) {
            this.totpEnable = totpEnable;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContacts() {
            return contacts;
        }

        public void setContacts(String contacts) {
            this.contacts = contacts;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }
    }
}

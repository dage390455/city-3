package com.sensoro.common.server.security.bean;

import java.io.Serializable;

/**
 * 布控人员信息
 */
public class SecurityDeployPersonInfo implements Serializable {

    /**
     * name : 刘敏
     * gender : 男
     * mobile : 18754623152
     * birthday : 852455
     * description : 阿东分工
     * nationality : 汉
     * identityCardNumber : 410521198704143653
     */

    private String name;
    private String gender;
    private String mobile;
    private String birthday;
    private String description;
    private String nationality;
    private String identityCardNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getIdentityCardNumber() {
        return identityCardNumber;
    }

    public void setIdentityCardNumber(String identityCardNumber) {
        this.identityCardNumber = identityCardNumber;
    }
}
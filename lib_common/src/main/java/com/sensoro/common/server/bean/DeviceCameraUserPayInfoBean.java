package com.sensoro.common.server.bean;

import java.io.Serializable;

public class DeviceCameraUserPayInfoBean implements Serializable {
    /**
     * sub_mch_id : 13962862021
     * card_id : 91110105306401130D
     * name : 赵武阳
     * mobilePhone : 18601118681
     * company : 北京升哲科技有限公司
     * address : 北京市朝阳区望京街10号望京SOHO1号楼B座2801/2807
     * phone : 400-6863180
     * province : HN
     * city : HK
     * business : ATT
     * bank : 招商银行股份有限公司北京望京支行
     * bankNumber : 62030122000696581
     * fdd_id : 1FD44C56AA4CADD4EFBAE39EBEB25644
     * isTest : true
     */

    private String sub_mch_id;
    private String card_id;
    private String name;
    private String mobilePhone;
    private String company;
    private String address;
    private String phone;
    private String province;
    private String city;
    private String business;
    private String bank;
    private String bankNumber;
    private String fdd_id;
    private boolean isTest;

    public String getSub_mch_id() {
        return sub_mch_id;
    }

    public void setSub_mch_id(String sub_mch_id) {
        this.sub_mch_id = sub_mch_id;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }

    public String getFdd_id() {
        return fdd_id;
    }

    public void setFdd_id(String fdd_id) {
        this.fdd_id = fdd_id;
    }

    public boolean isIsTest() {
        return isTest;
    }

    public void setIsTest(boolean isTest) {
        this.isTest = isTest;
    }
}

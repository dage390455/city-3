package com.sensoro.smartcity.server.bean;

import java.io.Serializable;
import java.util.List;

public class ContractAddInfo implements Serializable {

    /**
     * sex : 0
     * contract_type : 1
     * id : 111
     * enterprise_card_id : 无
     * enterprise_register_id : 无
     * customer_name : 无
     * customer_enterprise_name : 无
     * customer_address : 无
     * customer_phone : 13111111111
     * place_type : 企业
     * devices : [{"deviceType":"温度贴片","hardwareVersion":"1","quantity":1,"price":3000},{"deviceType":"烟雾传感器",
     * "hardwareVersion":"1","quantity":1,"price":3000},{"deviceType":"可燃气体传感器","hardwareVersion":"1","quantity":2,
     * "price":3000}]
     * payTimes : 2
     * confirmed : true
     * serviceTime : 2
     * ctid : 0
     * uid : 590c235044aa4369905d455b
     * appId : 50I35FhvOAw9
     * operator : DEMO
     * username : 宁波升哲物联网科技有限公司
     * address : 慈溪市白沙街道文化北路 185 号 (宝洁大厦 14 层)
     * phone : 0574-63590777
     * contract_number : CIXI-ZH-20180731-3
     * updatedAt : 2018-07-31T06:13:12.981Z
     * createdAt : 2018-07-31T06:13:12.981Z
     */

    private int sex;
    private int contract_type;
    private int id;
    private String enterprise_card_id;
    private String enterprise_register_id;
    private String customer_name;
    private String customer_enterprise_name;
    private String customer_address;
    private String customer_phone;
    private String place_type;
    private int payTimes;
    private boolean confirmed;
    private int serviceTime;
    private int ctid;
    private String uid;
    private String appId;
    private String operator;
    private String username;
    private String address;
    private String phone;
    private String contract_number;
    private String updatedAt;
    private String createdAt;
    private List<ContractsTemplateInfo> devices;

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getContract_type() {
        return contract_type;
    }

    public void setContract_type(int contract_type) {
        this.contract_type = contract_type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEnterprise_card_id() {
        return enterprise_card_id;
    }

    public void setEnterprise_card_id(String enterprise_card_id) {
        this.enterprise_card_id = enterprise_card_id;
    }

    public String getEnterprise_register_id() {
        return enterprise_register_id;
    }

    public void setEnterprise_register_id(String enterprise_register_id) {
        this.enterprise_register_id = enterprise_register_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_enterprise_name() {
        return customer_enterprise_name;
    }

    public void setCustomer_enterprise_name(String customer_enterprise_name) {
        this.customer_enterprise_name = customer_enterprise_name;
    }

    public String getCustomer_address() {
        return customer_address;
    }

    public void setCustomer_address(String customer_address) {
        this.customer_address = customer_address;
    }

    public String getCustomer_phone() {
        return customer_phone;
    }

    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }

    public String getPlace_type() {
        return place_type;
    }

    public void setPlace_type(String place_type) {
        this.place_type = place_type;
    }

    public int getPayTimes() {
        return payTimes;
    }

    public void setPayTimes(int payTimes) {
        this.payTimes = payTimes;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }

    public int getCtid() {
        return ctid;
    }

    public void setCtid(int ctid) {
        this.ctid = ctid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getContract_number() {
        return contract_number;
    }

    public void setContract_number(String contract_number) {
        this.contract_number = contract_number;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<ContractsTemplateInfo> getDevices() {
        return devices;
    }

    public void setDevices(List<ContractsTemplateInfo> devices) {
        this.devices = devices;
    }

}

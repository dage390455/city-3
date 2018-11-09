package com.sensoro.smartcity.server.bean;

import java.io.Serializable;
import java.util.List;

public class ContractListInfo implements Serializable {

    /**
     * id : 111
     * created_type : 1
     * card_id : null
     * sex : 0
     * enterprise_card_id : 无
     * enterprise_register_id : 无
     * cid : null
     * customer_name : 无
     * customer_enterprise_name : 无
     * customer_enterprise_validity : null
     * customer_address : 无
     * customer_phone : 13111111111
     * place_type : 企业
     * uid : 590c235044aa4369905d455b
     * appId : 50I35FhvOAw9
     * username : 宁波升哲物联网科技有限公司
     * operator : DEMO
     * address : 慈溪市白沙街道文化北路 185 号 (宝洁大厦 14 层)
     * phone : 0574-63590777
     * contract_type : 1
     * contract_confirm_url : null
     * contract_number : CIXI-ZH-20180731-3
     * ctid : 0
     * devices : [{"deviceType":"温度贴片","hardwareVersion":"1","quantity":1,"price":3000},{"deviceType":"烟雾传感器",
     * "hardwareVersion":"1","quantity":1,"price":3000},{"deviceType":"可燃气体传感器","hardwareVersion":"1","quantity":2,
     * "price":3000}]
     * payTimes : 2
     * confirmed : true
     * serviceTime : 2
     * confirmTime : null
     * createdAt : 2018-07-31T06:13:12.981Z
     * updatedAt : 2018-07-31T06:13:12.981Z
     */

    private int id;
    private int created_type;
    private String card_id;
    private int sex;
    private String enterprise_card_id;
    private String enterprise_register_id;
    private int cid;
    private String customer_name;
    private String customer_enterprise_name;
    private String customer_enterprise_validity;
    private String customer_address;
    private String customer_phone;
    private String place_type;
    private String uid;
    private String appId;
    private String username;
    private String operator;
    private String address;
    private String phone;
    private int contract_type;
    private String contract_confirm_url;
    private String contract_number;
    private String ctid;
    private int payTimes;
    private boolean confirmed;
    private int serviceTime;
    private String confirmTime;
    private String createdAt;
    private String updatedAt;
    private List<ContractsTemplateInfo> devices;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCreated_type() {
        return created_type;
    }

    public void setCreated_type(int created_type) {
        this.created_type = created_type;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
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

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
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

    public String getCustomer_enterprise_validity() {
        return customer_enterprise_validity;
    }

    public void setCustomer_enterprise_validity(String customer_enterprise_validity) {
        this.customer_enterprise_validity = customer_enterprise_validity;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
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

    public int getContract_type() {
        return contract_type;
    }

    public void setContract_type(int contract_type) {
        this.contract_type = contract_type;
    }

    public String getContract_confirm_url() {
        return contract_confirm_url;
    }

    public void setContract_confirm_url(String contract_confirm_url) {
        this.contract_confirm_url = contract_confirm_url;
    }

    public String getContract_number() {
        return contract_number;
    }

    public void setContract_number(String contract_number) {
        this.contract_number = contract_number;
    }

    public String getCtid() {
        return ctid;
    }

    public void setCtid(String ctid) {
        this.ctid = ctid;
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

    public String getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(String confirmTime) {
        this.confirmTime = confirmTime;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ContractsTemplateInfo> getDevices() {
        return devices;
    }

    public void setDevices(List<ContractsTemplateInfo> devices) {
        this.devices = devices;
    }

}

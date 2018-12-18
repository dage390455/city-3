package com.sensoro.smartcity.server.bean;

import java.util.List;

public class MonitorPointOperationTaskResultInfo {

    private String refrence;
    private String deviceType;
    private String scheduleNo;
    private String type;
    private String appId;
    private String operator;
    private String owners;
    private ContentBean content;
    private int total;
    private int complete;
    private int __v;
    private long updatedTime;
    private ContactBean contact;
    private String id;
    private String tasklogTranslation;
    private List<?> unsupportSns;
    private List<?> unexistSns;
    private List<?> rules;
    private List<String> sns;

    public String getRefrence() {
        return refrence;
    }

    public void setRefrence(String refrence) {
        this.refrence = refrence;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getScheduleNo() {
        return scheduleNo;
    }

    public void setScheduleNo(String scheduleNo) {
        this.scheduleNo = scheduleNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getOwners() {
        return owners;
    }

    public void setOwners(String owners) {
        this.owners = owners;
    }

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getComplete() {
        return complete;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public ContactBean getContact() {
        return contact;
    }

    public void setContact(ContactBean contact) {
        this.contact = contact;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTasklogTranslation() {
        return tasklogTranslation;
    }

    public void setTasklogTranslation(String tasklogTranslation) {
        this.tasklogTranslation = tasklogTranslation;
    }

    public List<?> getUnsupportSns() {
        return unsupportSns;
    }

    public void setUnsupportSns(List<?> unsupportSns) {
        this.unsupportSns = unsupportSns;
    }

    public List<?> getUnexistSns() {
        return unexistSns;
    }

    public void setUnexistSns(List<?> unexistSns) {
        this.unexistSns = unexistSns;
    }

    public List<?> getRules() {
        return rules;
    }

    public void setRules(List<?> rules) {
        this.rules = rules;
    }

    public List<String> getSns() {
        return sns;
    }

    public void setSns(List<String> sns) {
        this.sns = sns;
    }

    public static class ContentBean {
        /**
         * cmd : 4
         */

        private int cmd;

        public int getCmd() {
            return cmd;
        }

        public void setCmd(int cmd) {
            this.cmd = cmd;
        }
    }

    public static class ContactBean {
        /**
         * name : 杨志强
         * number : 18611818873
         */

        private String name;
        private String number;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }
    }
}

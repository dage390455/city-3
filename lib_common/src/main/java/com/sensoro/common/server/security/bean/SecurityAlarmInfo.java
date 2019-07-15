package com.sensoro.common.server.security.bean;

import java.io.Serializable;

/**
 * 安防预警信息
 */
public class SecurityAlarmInfo implements Serializable, Comparable<SecurityAlarmInfo> {


    /**
     * id : 16972463580D43448DDC594D9AD65ABD
     * alarmLogId : 613C89AEA44E423899799661FAA9A8F7
     * infoId : 6846440
     * structuredInfojson : null
     * objectMainJson : {"name":"刘敏","gender":"男","mobile":"18754623152","birthday":"852455","description":"阿东分工","nationality":"汉","identityCardNumber":"410521198704143653"}
     * deviceName : 集成研发抓拍机
     * address : 北京市望京SOHO-T1-2807室
     * libId : 101000009813
     * libName : 重点人员布控
     * taskId : B41B4247C6AC47E2BA544B0D5ED2EB00
     * taskName : 测试最新报警
     * sceneUrl : https://jxsr-oss1.antelopecloud.cn/files2/538379219/5bffa4ae201703d30420528a
     * faceRect : 1153,212,66,61
     * score : 80.15703582763672
     * captureTime : 1543480494000
     * alarmTime : 1543507002943
     * isHandle : 0
     * isEffective : 0
     * faceUrl : https://jxsr-oss1.antelopecloud.cn/files
     * imageUrl : https://jxsr-oss1.antelopecloud.cn/files
     * cid : 538379219
     * taskType : 101501
     * alarmType : 1
     * latitude : 30.491367
     * longitude : 114.40996
     * captureId : 1543480494000005
     */

    private String id;
    private String alarmLogId;
    private String infoId;
    private StructuredInfoJson structuredInfojson;//布控人员图片结构化数据.
    private SecurityDeployPersonInfo objectMainJson;
    private String deviceName;
    private String address;
    private String libId;
    private String libName;
    private String taskId;
    private String taskName;
    private String sceneUrl;
    private String faceRect;
    private double score;
    private long captureTime;
    private long alarmTime;
    private int isHandle;
    private int isEffective;
    private String faceUrl;
    private String imageUrl;
    private int cid;
    private int taskType;
    private int alarmType;
    private double latitude;
    private double longitude;
    private String captureId;

    @Override
    public int compareTo(SecurityAlarmInfo o) {
        return 0;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlarmLogId() {
        return alarmLogId;
    }

    public void setAlarmLogId(String alarmLogId) {
        this.alarmLogId = alarmLogId;
    }

    public String getInfoId() {
        return infoId;
    }

    public void setInfoId(String infoId) {
        this.infoId = infoId;
    }

    public StructuredInfoJson getStructuredInfojson() {
        return structuredInfojson;
    }

    public void setStructuredInfojson(StructuredInfoJson structuredInfojson) {
        this.structuredInfojson = structuredInfojson;
    }

    public SecurityDeployPersonInfo getObjectMainJson() {
        return objectMainJson;
    }

    public void setObjectMainJson(SecurityDeployPersonInfo objectMainJson) {
        this.objectMainJson = objectMainJson;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLibId() {
        return libId;
    }

    public void setLibId(String libId) {
        this.libId = libId;
    }

    public String getLibName() {
        return libName;
    }

    public void setLibName(String libName) {
        this.libName = libName;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getSceneUrl() {
        return sceneUrl;
    }

    public void setSceneUrl(String sceneUrl) {
        this.sceneUrl = sceneUrl;
    }

    public String getFaceRect() {
        return faceRect;
    }

    public void setFaceRect(String faceRect) {
        this.faceRect = faceRect;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public long getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(long captureTime) {
        this.captureTime = captureTime;
    }

    public long getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(long alarmTime) {
        this.alarmTime = alarmTime;
    }

    public int getIsHandle() {
        return isHandle;
    }

    public void setIsHandle(int isHandle) {
        this.isHandle = isHandle;
    }

    public int getIsEffective() {
        return isEffective;
    }

    public void setIsEffective(int isEffective) {
        this.isEffective = isEffective;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public int getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(int alarmType) {
        this.alarmType = alarmType;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCaptureId() {
        return captureId;
    }

    public void setCaptureId(String captureId) {
        this.captureId = captureId;
    }

    public static class StructuredInfoJson {
        /**
         * 布控人员图片结构化数据
         * 数据结构为空
         */

    }


}

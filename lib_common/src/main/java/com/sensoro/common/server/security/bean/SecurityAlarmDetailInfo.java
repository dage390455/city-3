package com.sensoro.common.server.security.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 安防预警详情信息
 */
public class SecurityAlarmDetailInfo implements Serializable {


    /**
     * id : 16972463580D43448DDC594D9AD65ABD
     * alarmLogId : 613C89AEA44E423899799661FAA9A8F7
     * infoId : 6846440
     * structuredInfojson : null
     * objectMainJson : {"name":"刘敏","gender":"男","mobile":"18754623152","birthday":"852455","description":"阿东分工","nationality":"汉","identityCardNumber":"410521198704143653"}
     * camera : {"name":"集成研发抓拍机","sn":"001C2711A8AF","cid":"540409919","brand":"SENSORO","version":"V1.0.0","type":"抓拍机","deviceStatus":"1","latitude":"40.017564","longitude":"116.503266","location":"北京市朝阳区崔各庄镇川渝饭庄12345","label":["望京","SOHO"],"contact":{"name":"test","mobilePhone":"13699167277"},"installationMode":"101800","orientation":"112901"}
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
     * operationDetail : null
     * faceUrl : https://jxsr-oss1.antelopecloud.cn/files
     * imageUrl : https://jxsr-oss1.antelopecloud.cn/files
     * cid : 538379219
     * alarmNotifyUserIds : ["101000001028","101000001034"]
     * taskType : 101501
     * alarmType : 1
     * latitude : 30.491367
     * longitude : 114.40996
     * captureId : 1543480494000005
     * personInfoUrl : /api/person/resource/v1/person/getPersonById
     * aid : 5190486531474724667
     * feature : 90rXvRhW3ju2k409tSoGuiZlyLxjmTq8TKCtvQ02pTyDjjQ6NBr3PDbSk7whZ449b137u7d+xTyqpcw8TLIIPEpDCr1BHMG7rZ7xu5u93Dx
     * contacts : [{"name":"齐哲","mobilePhone":"13888888888"},{"name":"巴哲","mobilePhone":"13999999999"}]
     */

    private String id;
    private String alarmLogId;
    private String infoId;
    private Object structuredInfojson;
    private SecurityDeployPersonInfo objectMainJson;
    private SecurityCameraInfo camera;
    private String deviceName;
    private String address;
    private String libId;
    private String libName;
    private String taskId;
    private String taskName;
    private String sceneUrl;
    private String faceRect;
    private double score;
    private String captureTime;
    private String alarmTime;
    private int isHandle;
    private int isEffective;
    private Object operationDetail;
    private String faceUrl;
    private String imageUrl;
    private int cid;
    private int taskType;
    private String alarmType;
    private double latitude;
    private double longitude;
    private String captureId;
    private String personInfoUrl;
    private String aid;
    private String feature;
    private List<String> alarmNotifyUserIds;
    private List<SecurityContactsInfo> contacts;

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

    public Object getStructuredInfojson() {
        return structuredInfojson;
    }

    public void setStructuredInfojson(Object structuredInfojson) {
        this.structuredInfojson = structuredInfojson;
    }

    public SecurityDeployPersonInfo getObjectMainJson() {
        return objectMainJson;
    }

    public void setObjectMainJson(SecurityDeployPersonInfo objectMainJson) {
        this.objectMainJson = objectMainJson;
    }

    public SecurityCameraInfo getCamera() {
        return camera;
    }

    public void setCamera(SecurityCameraInfo camera) {
        this.camera = camera;
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

    public String getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(String captureTime) {
        this.captureTime = captureTime;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
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

    public Object getOperationDetail() {
        return operationDetail;
    }

    public void setOperationDetail(Object operationDetail) {
        this.operationDetail = operationDetail;
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

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
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

    public String getPersonInfoUrl() {
        return personInfoUrl;
    }

    public void setPersonInfoUrl(String personInfoUrl) {
        this.personInfoUrl = personInfoUrl;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public List<String> getAlarmNotifyUserIds() {
        return alarmNotifyUserIds;
    }

    public void setAlarmNotifyUserIds(List<String> alarmNotifyUserIds) {
        this.alarmNotifyUserIds = alarmNotifyUserIds;
    }

    public List<SecurityContactsInfo> getContacts() {
        return contacts;
    }

    public void setContacts(List<SecurityContactsInfo> contacts) {
        this.contacts = contacts;
    }
}

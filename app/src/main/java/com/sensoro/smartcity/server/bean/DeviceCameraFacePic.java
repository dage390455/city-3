package com.sensoro.smartcity.server.bean;

import java.util.List;

public class DeviceCameraFacePic {

    /**
     * id : 1555580885010015006
     * cid : 540409861
     * captureTime : 1555580885000
     * longitude : 116.5040210820253
     * latitude : 40.015634700658815
     * cameraTags : ["72057600540409861","119105","540409861","101801","103401","119401","100603","119503"]
     * sceneUrl : /staticResource/v1/img/cid/540409861/objId/5cb847d620360005102073a5?signature=f5bf87738a3fcea006a0e540061546a3&expire=1555750800000&watermark=53454E534F524F0A323031393034313854313734383330&location=1
     * aid : 5189941173707358336
     * personTags : ["100001","0","112403","100803","112002","106901","119012","112404","112503","112103","112203"]
     * deviceName : 费家村中街79号门前东
     * address : 北京市朝阳区崔各庄镇鸿起来菜馆
     * personInfoUrl : /api/person/resource/v1/person/getPersonById?personId=&aid=5189941173707358336&userId=101000000267&signature=031083fa0d9906bb1d650b425f6198eb&expire=1555753710870
     * faceRect : 144,978,87,84
     * faceUrl : /staticResource/v1/img/cid/540409861/objId/5cb847d620360005102073a5?signature=9451ef6f50a66221e1189447c6a7280f&expire=1555750800000&crop=x_75,y_852,w_226,h_268&watermark=53454E534F524F&location=2
     * faceConfidence : 0.9636986
     * hasBody : false
     */

    private String id;
    private String cid;
    private String captureTime;
    private double longitude;
    private double latitude;
    private String sceneUrl;
    private String aid;
    private String deviceName;
    private String address;
    private String personInfoUrl;
    private String faceRect;
    private String faceUrl;
    private double faceConfidence;
    private boolean hasBody;
    private List<String> cameraTags;
    private List<String> personTags;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(String captureTime) {
        this.captureTime = captureTime;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getSceneUrl() {
        return sceneUrl;
    }

    public void setSceneUrl(String sceneUrl) {
        this.sceneUrl = sceneUrl;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
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

    public String getPersonInfoUrl() {
        return personInfoUrl;
    }

    public void setPersonInfoUrl(String personInfoUrl) {
        this.personInfoUrl = personInfoUrl;
    }

    public String getFaceRect() {
        return faceRect;
    }

    public void setFaceRect(String faceRect) {
        this.faceRect = faceRect;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public double getFaceConfidence() {
        return faceConfidence;
    }

    public void setFaceConfidence(double faceConfidence) {
        this.faceConfidence = faceConfidence;
    }

    public boolean isHasBody() {
        return hasBody;
    }

    public void setHasBody(boolean hasBody) {
        this.hasBody = hasBody;
    }

    public List<String> getCameraTags() {
        return cameraTags;
    }

    public void setCameraTags(List<String> cameraTags) {
        this.cameraTags = cameraTags;
    }

    public List<String> getPersonTags() {
        return personTags;
    }

    public void setPersonTags(List<String> personTags) {
        this.personTags = personTags;
    }
}

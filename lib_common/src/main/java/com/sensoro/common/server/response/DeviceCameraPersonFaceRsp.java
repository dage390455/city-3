package com.sensoro.common.server.response;

import java.io.Serializable;
import java.util.List;

public class DeviceCameraPersonFaceRsp extends ResponseBase{

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        /**
         * id : 1556097276010009002
         * cid : 540409860
         * captureTime : 1556097276000
         * longitude : 116.505466
         * latitude : 40.01589
         * cameraTags : ["119105","72057600540409860","540409860","102406","103401","119401","100603","119503"]
         * sceneUrl : /staticResource/v1/img/cid/540409860/objId/5cc028fd20360004103035a8?signature=9e556e773f63ec1b08a8f02d8555908d&expire=1556276400000&watermark=53454E534F524F0A323031393034323454313930353330&location=1
         * aid : 5189941173707360554
         * personTags : ["100001","106901","112303","112401","112403","112503","112203","0","0","0","0","100803","100001","119012"]
         * deviceName : 费家村村委会门口
         * address : 北京市朝阳区崔各庄乡
         * personInfoUrl : /api/person/resource/v1/person/getPersonById?personId=&aid=5189941173707360554&userId=101000000267&signature=0183857cba358f7ff1f00f5fdb1ccf6a&expire=1556276730573
         * faceRect : 1721,438,76,73
         * faceUrl : /staticResource/v1/img/cid/540409860/objId/5cc028fd20360004103035a8?signature=4acf8f2fb5b3527e9f3c54ce0682ec48&expire=1556276400000&crop=x_1661,y_329,w_197,h_233&watermark=53454E534F524F&location=2
         * faceConfidence : 0.9880144
         * hasBody : true
         * score : 99.99993
         */

        private String id;
        private String cid;
        private String captureTime;
        private double longitude;
        private double latitude;
        private String sceneUrl;
//        private String aid;
        private String deviceName;
        private String address;
//        private String personInfoUrl;
//        private String faceRect;
        private String faceUrl;
        private double faceConfidence;
//        private boolean hasBody;
        private double score;
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

//        public String getAid() {
//            return aid;
//        }
//
//        public void setAid(String aid) {
//            this.aid = aid;
//        }

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

//        public String getPersonInfoUrl() {
//            return personInfoUrl;
//        }
//
//        public void setPersonInfoUrl(String personInfoUrl) {
//            this.personInfoUrl = personInfoUrl;
//        }
//
//        public String getFaceRect() {
//            return faceRect;
//        }
//
//        public void setFaceRect(String faceRect) {
//            this.faceRect = faceRect;
//        }

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

//        public boolean isHasBody() {
//            return hasBody;
//        }
//
//        public void setHasBody(boolean hasBody) {
//            this.hasBody = hasBody;
//        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
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
}

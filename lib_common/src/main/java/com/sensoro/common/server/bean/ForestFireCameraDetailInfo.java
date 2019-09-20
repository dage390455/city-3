package com.sensoro.common.server.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: jack
 * 时  间: 2019-09-19
 * 包  名: com.sensoro.common.server.bean
 * 简  述: <功能简述>
 */
public class ForestFireCameraDetailInfo implements Serializable {


    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean  implements Serializable{
        /**
         * sn : 001C271240DF
         * id : 72057600540409953
         * hls : https://scpub-api.antelopecloud.cn/cloud/v2/live/540409953.m3u8?client_token=540409953_3356491776_1600146480_bf45c5db8382bc3dff73780160709465
         * flv : https://scpub-api.antelopecloud.cn/cloud/v2/live/540409953.flv?client_token=540409953_3356491776_1600146480_bf45c5db8382bc3dff73780160709465&decryption=1
         * lastCover : https://scpub-eye.antelopecloud.cn/staticResource/v1/video/getLatestCoverMap/540409953?signature=8656ca7c56b3b40333dc344404abbafc&expire=1568788908308
         * camera : {"alarm":{"status":0,"updateTime":"2019-09-18T06:38:53.098Z","picturePrintTime":"2019-09-18T06:38:53.098Z","picArr":[]},"label":[],"createTime":1568774188147,"_id":"5d7f3bb960e00544fb65fd12","sn":"001C271240DF","installationMode":null,"orientation":null,"mobilePhone":"15108936424","info":{"type":"抓拍机","version":"V1.0.0","brand":"SENSORO","cid":"540409953","deviceId":"72057600540409953","sn":"001C271240DF","platform":false,"latitude":"39.996894","longitude":"116.480667","location":"北京市朝阳区望京街道望京SOHO中心T1望京SOHO","deviceStatus":"0"},"userid":"5b86438092bb4b66f7621a7f","name":"white3","cid":"540409953","id":"5d7f3bb960e00544fb65fd12"}
         * multiVideoInfo : [{"cid":"540672047","hls":"https://scpub-api.antelopecloud.cn/cloud/v2/live/540672047.m3u8?client_token=540672047_3356491776_1600151639_0f44c7a9e15db4dbab34fc3a950e6afd","flv":"https://scpub-api.antelopecloud.cn/cloud/v2/live/540672047.flv?client_token=540672047_3356491776_1600151639_0f44c7a9e15db4dbab34fc3a950e6afd&decryption=1","lastCover":"https://scpub-eye.antelopecloud.cn/staticResource/v1/video/getLatestCoverMap/540672047?signature=310c5a475c4ee7f7197e3af11f90d052&expire=1568791229046"},{"cid":"540672048","hls":"https://scpub-api.antelopecloud.cn/cloud/v2/live/540672048.m3u8?client_token=540672048_3356491776_1600138489_db11210a990723fea8f00caf55c0e57c","flv":"https://scpub-api.antelopecloud.cn/cloud/v2/live/540672048.flv?client_token=540672048_3356491776_1600138489_db11210a990723fea8f00caf55c0e57c&decryption=1","lastCover":"https://scpub-eye.antelopecloud.cn/staticResource/v1/video/getLatestCoverMap/540672048?signature=1944dcca99b828ef53df5d9603000dfb&expire=1568791229455"}]
         */

        private String sn;
        private String id;
        private String hls;
        private String flv;
        private String lastCover;
        private CameraBean camera;
        private List<MultiVideoInfoBean> multiVideoInfo;

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getHls() {
            return hls;
        }

        public void setHls(String hls) {
            this.hls = hls;
        }

        public String getFlv() {
            return flv;
        }

        public void setFlv(String flv) {
            this.flv = flv;
        }

        public String getLastCover() {
            return lastCover;
        }

        public void setLastCover(String lastCover) {
            this.lastCover = lastCover;
        }

        public CameraBean getCamera() {
            return camera;
        }

        public void setCamera(CameraBean camera) {
            this.camera = camera;
        }

        public List<MultiVideoInfoBean> getMultiVideoInfo() {
            return multiVideoInfo;
        }

        public void setMultiVideoInfo(List<MultiVideoInfoBean> multiVideoInfo) {
            this.multiVideoInfo = multiVideoInfo;
        }
    }

    public static class CameraBean  implements Serializable{
        /**
         * alarm : {"status":0,"updateTime":"2019-09-18T06:38:53.098Z","picturePrintTime":"2019-09-18T06:38:53.098Z","picArr":[]}
         * label : []
         * createTime : 1568774188147
         * _id : 5d7f3bb960e00544fb65fd12
         * sn : 001C271240DF
         * installationMode : null
         * orientation : null
         * mobilePhone : 15108936424
         * info : {"type":"抓拍机","version":"V1.0.0","brand":"SENSORO","cid":"540409953","deviceId":"72057600540409953","sn":"001C271240DF","platform":false,"latitude":"39.996894","longitude":"116.480667","location":"北京市朝阳区望京街道望京SOHO中心T1望京SOHO","deviceStatus":"0"}
         * userid : 5b86438092bb4b66f7621a7f
         * name : white3
         * cid : 540409953
         * id : 5d7f3bb960e00544fb65fd12
         */

        private AlarmBean alarm;
        private long createTime;
        private String _id;
        private String sn;
        private Object installationMode;
        private Object orientation;
        private String mobilePhone;
        private InfoBean info;
        private String userid;
        private String name;
        private String cid;
        private String id;
        private List<?> label;

        public AlarmBean getAlarm() {
            return alarm;
        }

        public void setAlarm(AlarmBean alarm) {
            this.alarm = alarm;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public Object getInstallationMode() {
            return installationMode;
        }

        public void setInstallationMode(Object installationMode) {
            this.installationMode = installationMode;
        }

        public Object getOrientation() {
            return orientation;
        }

        public void setOrientation(Object orientation) {
            this.orientation = orientation;
        }

        public String getMobilePhone() {
            return mobilePhone;
        }

        public void setMobilePhone(String mobilePhone) {
            this.mobilePhone = mobilePhone;
        }

        public InfoBean getInfo() {
            return info;
        }

        public void setInfo(InfoBean info) {
            this.info = info;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCid() {
            return cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<?> getLabel() {
            return label;
        }

        public void setLabel(List<?> label) {
            this.label = label;
        }




    }


    public static class AlarmBean  implements Serializable{
        /**
         * status : 0
         * updateTime : 2019-09-18T06:38:53.098Z
         * picturePrintTime : 2019-09-18T06:38:53.098Z
         * picArr : []
         */

        private int status;
        private String updateTime;
        private String picturePrintTime;
        private List<?> picArr;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getPicturePrintTime() {
            return picturePrintTime;
        }

        public void setPicturePrintTime(String picturePrintTime) {
            this.picturePrintTime = picturePrintTime;
        }

        public List<?> getPicArr() {
            return picArr;
        }

        public void setPicArr(List<?> picArr) {
            this.picArr = picArr;
        }
    }

    public static class InfoBean implements Serializable{
        /**
         * type : 抓拍机
         * version : V1.0.0
         * brand : SENSORO
         * cid : 540409953
         * deviceId : 72057600540409953
         * sn : 001C271240DF
         * platform : false
         * latitude : 39.996894
         * longitude : 116.480667
         * location : 北京市朝阳区望京街道望京SOHO中心T1望京SOHO
         * deviceStatus : 0
         */

        private String type;
        private String version;
        private String brand;
        private String cid;
        private String deviceId;
        private String sn;
        private boolean platform;
        private String latitude;
        private String longitude;
        private String location;
        private String deviceStatus;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getCid() {
            return cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public boolean isPlatform() {
            return platform;
        }

        public void setPlatform(boolean platform) {
            this.platform = platform;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getDeviceStatus() {
            return deviceStatus;
        }

        public void setDeviceStatus(String deviceStatus) {
            this.deviceStatus = deviceStatus;
        }
    }

    public static class MultiVideoInfoBean implements Serializable{
        /**
         * cid : 540672047
         * hls : https://scpub-api.antelopecloud.cn/cloud/v2/live/540672047.m3u8?client_token=540672047_3356491776_1600151639_0f44c7a9e15db4dbab34fc3a950e6afd
         * flv : https://scpub-api.antelopecloud.cn/cloud/v2/live/540672047.flv?client_token=540672047_3356491776_1600151639_0f44c7a9e15db4dbab34fc3a950e6afd&decryption=1
         * lastCover : https://scpub-eye.antelopecloud.cn/staticResource/v1/video/getLatestCoverMap/540672047?signature=310c5a475c4ee7f7197e3af11f90d052&expire=1568791229046
         */

        private String cid;
        private String hls;
        private String flv;
        private String lastCover;

        public String getCid() {
            return cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public String getHls() {
            return hls;
        }

        public void setHls(String hls) {
            this.hls = hls;
        }

        public String getFlv() {
            return flv;
        }

        public void setFlv(String flv) {
            this.flv = flv;
        }

        public String getLastCover() {
            return lastCover;
        }

        public void setLastCover(String lastCover) {
            this.lastCover = lastCover;
        }
    }
}

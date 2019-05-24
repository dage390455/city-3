package com.sensoro.common.server.response;

import java.io.Serializable;
import java.util.List;

public class AlarmCloudVideoRsp extends ResponseBase implements Serializable{

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable{
        /**
         * eventId : 5cdbc6c35c62461a263e2e30
         * medias : [{"createTime":"2019-05-09T16:00:00.000Z","endTime":"2019-05-09T16:00:15.000Z","beginTime":"2019-05-09T15:59:45.000Z","sn":"001C2711A8B3","location":"北京市朝阳区崔各庄镇来缘公寓二部","videoSize":"9.67MB","eventId":"5cdbc6c35c62461a263e2e30","mediaUrl":"https://city-video-cdn.sensoro.com/001C2711A8B3_1557417585_1557417615.mp4?e=1558003894&token=5Bf2KpUYTwT76bN_L1wuOKmiCDQbEOU-Fe4NRb-I:bfnnMkeZWU6aD_iFD0zgeZ1ThBg=","coverUrl":"https://city-video-cdn.sensoro.com/%60001C2711A8B3_1557417585_1557417615.jpeg?e=1558003894&token=5Bf2KpUYTwT76bN_L1wuOKmiCDQbEOU-Fe4NRb-I:vV5kzW6d84Wb9Z34f-YOMEVo0FU="},{"createTime":"2019-05-09T16:00:00.000Z","endTime":"2019-05-09T16:00:15.000Z","beginTime":"2019-05-09T15:59:45.000Z","sn":"001C2711A8AE","location":"北京市朝阳区崔各庄镇来缘公寓二部","videoSize":"9.28MB","eventId":"5cdbc6c35c62461a263e2e30","mediaUrl":"https://city-video-cdn.sensoro.com/001C2711A8AE_1557417585_1557417615.mp4?e=1558003894&token=5Bf2KpUYTwT76bN_L1wuOKmiCDQbEOU-Fe4NRb-I:yyJ1cGw1dloSQbpplHP68wuYuBY=","coverUrl":"https://city-video-cdn.sensoro.com/%60001C2711A8AE_1557417585_1557417615.jpeg?e=1558003894&token=5Bf2KpUYTwT76bN_L1wuOKmiCDQbEOU-Fe4NRb-I:NTzO09onliG62zwBI2B7e7PymKU="},{"createTime":"2019-05-09T16:00:00.000Z","endTime":"2019-05-09T16:00:15.000Z","beginTime":"2019-05-09T15:59:45.000Z","sn":"001C2711A680","location":"北京市朝阳区崔各庄镇北京市朝阳区崔各庄费家村村委会","videoSize":"9.18MB","eventId":"5cdbc6c35c62461a263e2e30","mediaUrl":"https://city-video-cdn.sensoro.com/001C2711A680_1557417585_1557417615.mp4?e=1558003894&token=5Bf2KpUYTwT76bN_L1wuOKmiCDQbEOU-Fe4NRb-I:UkdLJVL-dc0YJZh4pWsR5CIqPTA=","coverUrl":"https://city-video-cdn.sensoro.com/%60001C2711A680_1557417585_1557417615.jpeg?e=1558003894&token=5Bf2KpUYTwT76bN_L1wuOKmiCDQbEOU-Fe4NRb-I:xMWPmV8u7wJLF1VR4Wb-G-Q4WXQ="},{"createTime":"2019-05-09T16:00:00.000Z","endTime":"2019-05-09T16:00:15.000Z","beginTime":"2019-05-09T15:59:45.000Z","sn":"001C2711A77E","location":"北京市朝阳区崔各庄镇鸿起来菜馆","videoSize":"9.39MB","eventId":"5cdbc6c35c62461a263e2e30","mediaUrl":"https://city-video-cdn.sensoro.com/001C2711A77E_1557417585_1557417615.mp4?e=1558003894&token=5Bf2KpUYTwT76bN_L1wuOKmiCDQbEOU-Fe4NRb-I:WR-rqVImiDM1hv9bjgAIiBt6bfw=","coverUrl":"https://city-video-cdn.sensoro.com/%60001C2711A77E_1557417585_1557417615.jpeg?e=1558003894&token=5Bf2KpUYTwT76bN_L1wuOKmiCDQbEOU-Fe4NRb-I:E91iyLqHqlhu45pWpDO8t6iSU4M="},{"createTime":"2019-05-09T16:00:00.000Z","endTime":"2019-05-09T16:00:15.000Z","beginTime":"2019-05-09T15:59:45.000Z","sn":"001C2711A8B6","location":"北京市朝阳区崔各庄镇来缘公寓二部","videoSize":"9.20MB","eventId":"5cdbc6c35c62461a263e2e30","mediaUrl":"https://city-video-cdn.sensoro.com/001C2711A8B6_1557417585_1557417615.mp4?e=1558003894&token=5Bf2KpUYTwT76bN_L1wuOKmiCDQbEOU-Fe4NRb-I:MrYAUkzxctkpXsU7R09EyHZxzgY=","coverUrl":"https://city-video-cdn.sensoro.com/%60001C2711A8B6_1557417585_1557417615.jpeg?e=1558003894&token=5Bf2KpUYTwT76bN_L1wuOKmiCDQbEOU-Fe4NRb-I:wSdhxVAcEmHSl7MjzPVUaAuI8wE="},{"createTime":"2019-05-09T16:00:00.000Z","endTime":"2019-05-09T16:00:15.000Z","beginTime":"2019-05-09T15:59:45.000Z","sn":"001C2711A8B5","location":"北京市朝阳区崔各庄镇来缘公寓二部","videoSize":"9.47MB","eventId":"5cdbc6c35c62461a263e2e30","mediaUrl":"https://city-video-cdn.sensoro.com/001C2711A8B5_1557417585_1557417615.mp4?e=1558003894&token=5Bf2KpUYTwT76bN_L1wuOKmiCDQbEOU-Fe4NRb-I:Ih4BIcm_75bGBBMh7MisetppVJ4=","coverUrl":"https://city-video-cdn.sensoro.com/%60001C2711A8B5_1557417585_1557417615.jpeg?e=1558003894&token=5Bf2KpUYTwT76bN_L1wuOKmiCDQbEOU-Fe4NRb-I:QUBDzG1M-eCE2L1qks5_d8mevk8="},{"createTime":"2019-05-09T16:00:00.000Z","endTime":"2019-05-09T16:00:15.000Z","beginTime":"2019-05-09T15:59:45.000Z","sn":"001C2711A8AF","location":"北京市朝阳区崔各庄镇川渝饭庄","videoSize":"9.13MB","eventId":"5cdbc6c35c62461a263e2e30","mediaUrl":"https://city-video-cdn.sensoro.com/001C2711A8AF_1557417585_1557417615.mp4?e=1558003894&token=5Bf2KpUYTwT76bN_L1wuOKmiCDQbEOU-Fe4NRb-I:7_vpO-YZJAv2lJGGb4OD7p5O_nY=","coverUrl":"https://city-video-cdn.sensoro.com/%60001C2711A8AF_1557417585_1557417615.jpeg?e=1558003894&token=5Bf2KpUYTwT76bN_L1wuOKmiCDQbEOU-Fe4NRb-I:IffpKEg5498wytkkKtteaVVEl0c="}]
         */

        private String eventId;
        private List<MediasBean> medias;

        public String getEventId() {
            return eventId;
        }

        public void setEventId(String eventId) {
            this.eventId = eventId;
        }

        public List<MediasBean> getMedias() {
            return medias;
        }

        public void setMedias(List<MediasBean> medias) {
            this.medias = medias;
        }

        public static class MediasBean implements Serializable {
            /**
             * createTime : 2019-05-09T16:00:00.000Z
             * endTime : 2019-05-09T16:00:15.000Z
             * beginTime : 2019-05-09T15:59:45.000Z
             * sn : 001C2711A8B3
             * location : 北京市朝阳区崔各庄镇来缘公寓二部
             * videoSize : 9.67MB
             * eventId : 5cdbc6c35c62461a263e2e30
             * mediaUrl : https://city-video-cdn.sensoro.com/001C2711A8B3_1557417585_1557417615.mp4?e=1558003894&token=5Bf2KpUYTwT76bN_L1wuOKmiCDQbEOU-Fe4NRb-I:bfnnMkeZWU6aD_iFD0zgeZ1ThBg=
             * coverUrl : https://city-video-cdn.sensoro.com/%60001C2711A8B3_1557417585_1557417615.jpeg?e=1558003894&token=5Bf2KpUYTwT76bN_L1wuOKmiCDQbEOU-Fe4NRb-I:vV5kzW6d84Wb9Z34f-YOMEVo0FU=
             */

            private String createTime;
            private String endTime;
            private String beginTime;
            private String sn;
            private String location;
            private String videoSize;
            private String eventId;
            private String mediaUrl;
            private String coverUrl;

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public String getEndTime() {
                return endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
            }

            public String getBeginTime() {
                return beginTime;
            }

            public void setBeginTime(String beginTime) {
                this.beginTime = beginTime;
            }

            public String getSn() {
                return sn;
            }

            public void setSn(String sn) {
                this.sn = sn;
            }

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }

            public String getVideoSize() {
                return videoSize;
            }

            public void setVideoSize(String videoSize) {
                this.videoSize = videoSize;
            }

            public String getEventId() {
                return eventId;
            }

            public void setEventId(String eventId) {
                this.eventId = eventId;
            }

            public String getMediaUrl() {
                return mediaUrl;
            }

            public void setMediaUrl(String mediaUrl) {
                this.mediaUrl = mediaUrl;
            }

            public String getCoverUrl() {
                return coverUrl;
            }

            public void setCoverUrl(String coverUrl) {
                this.coverUrl = coverUrl;
            }
        }
    }
}

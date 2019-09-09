package com.sensoro.common.server.bean;

import java.io.Serializable;
import java.util.List;

public class SensoroBugData implements Serializable {

    /**
     * data : {"msg":"我是内容，一般很长很长，我是内容，一般很长很长，我是内容，一般很长很长，我是内容，一般很长很长，我是内容，一般很长很长，\n我是内容，一般很长很长，我是内容，一般很长很长，我是内容，一般很长很长，我是内容，一般很长很长，我是内容，一般很长很长，\n我是内容，一般很长很长，我是内容，一般很长很长，我是内容，一般很长很长，","accountName":"","tags":["测试","我是个数组"],"timeStamp":1568024643407}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * msg : 我是内容，一般很长很长，我是内容，一般很长很长，我是内容，一般很长很长，我是内容，一般很长很长，我是内容，一般很长很长，
         * 我是内容，一般很长很长，我是内容，一般很长很长，我是内容，一般很长很长，我是内容，一般很长很长，我是内容，一般很长很长，
         * 我是内容，一般很长很长，我是内容，一般很长很长，我是内容，一般很长很长，
         * accountName :
         * tags : ["测试","我是个数组"]
         * timeStamp : 1568024643407
         */

        private String msg;
        private String accountName;
        private long timeStamp;
        private List<String> tags;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getAccountName() {
            return accountName;
        }

        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }
    }
}

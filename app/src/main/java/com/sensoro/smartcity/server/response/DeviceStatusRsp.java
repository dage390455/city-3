package com.sensoro.smartcity.server.response;

public class DeviceStatusRsp extends ResponseBase{
    /**
     * data : {"status":4}
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
         * status : 4
         */

        private Integer status;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }
    }

    /**
     * status : 4
     */

}

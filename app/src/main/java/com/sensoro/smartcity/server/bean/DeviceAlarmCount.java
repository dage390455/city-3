package com.sensoro.smartcity.server.bean;

import com.google.gson.annotations.SerializedName;
import com.sensoro.smartcity.model.AlarmDeviceCountsBean;

import java.util.List;

public class DeviceAlarmCount {

    /**
     * all : [{"type":"all","total":101,"counts":{"0":0,"1":17,"2":74,"3":10}}]
     * details : [{"type":"smoke","total":23,"counts":{"0":0,"1":11,"2":10,"3":2}}]
     * event : detail
     */

    private String event;
    private List<AllBean> all;
    private List<DetailsBean> details;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public List<AllBean> getAll() {
        return all;
    }

    public void setAll(List<AllBean> all) {
        this.all = all;
    }

    public List<DetailsBean> getDetails() {
        return details;
    }

    public void setDetails(List<DetailsBean> details) {
        this.details = details;
    }

    public static class AllBean {
        /**
         * type : all
         * total : 101
         * counts : {"0":0,"1":17,"2":74,"3":10}
         */

        private String type;
        private int total;
        private AlarmDeviceCountsBean counts;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public AlarmDeviceCountsBean getCounts() {
            return counts;
        }

        public void setCounts(AlarmDeviceCountsBean counts) {
            this.counts = counts;
        }

    }

    public static class DetailsBean {
        /**
         * type : smoke
         * total : 23
         * counts : {"0":0,"1":11,"2":10,"3":2}
         */

        private String type;
        private int total;
        private CountsBeanX counts;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public CountsBeanX getCounts() {
            return counts;
        }

        public void setCounts(CountsBeanX counts) {
            this.counts = counts;
        }

        public static class CountsBeanX {
            /**
             * 0 : 0
             * 1 : 11
             * 2 : 10
             * 3 : 2
             */

            @SerializedName("0")
            private int _$0;
            @SerializedName("1")
            private int _$1;
            @SerializedName("2")
            private int _$2;
            @SerializedName("3")
            private int _$3;

            public int get_$0() {
                return _$0;
            }

            public void set_$0(int _$0) {
                this._$0 = _$0;
            }

            public int get_$1() {
                return _$1;
            }

            public void set_$1(int _$1) {
                this._$1 = _$1;
            }

            public int get_$2() {
                return _$2;
            }

            public void set_$2(int _$2) {
                this._$2 = _$2;
            }

            public int get_$3() {
                return _$3;
            }

            public void set_$3(int _$3) {
                this._$3 = _$3;
            }
        }
    }
}

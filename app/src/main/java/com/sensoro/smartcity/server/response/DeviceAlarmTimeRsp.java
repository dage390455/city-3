package com.sensoro.smartcity.server.response;

/**
 * Created by sensoro on 17/7/26.
 */

public class DeviceAlarmTimeRsp extends ResponseBase {

    private AlarmTime data;//time

    public AlarmTime getData() {
        return data;
    }

    public void setData(AlarmTime data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DeviceAlarmTimeRsp{" +
                "data=" + data +
                '}';
    }

    public class AlarmTime {
        private long timeStamp;

        public long getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
        }
    }
}

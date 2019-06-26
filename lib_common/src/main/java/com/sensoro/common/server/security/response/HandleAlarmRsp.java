package com.sensoro.common.server.security.response;

import com.sensoro.common.server.response.ResponseBase;

import java.io.Serializable;

public class HandleAlarmRsp extends ResponseBase implements Serializable {
    public HandleAlarmData data;

    public HandleAlarmData getData() {
        return data;
    }

    public void setData(HandleAlarmData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HandleAlarmRsp{" +
                "data=" + data +
                '}';
    }

    public static class HandleAlarmData {
        public String id;
    }
}



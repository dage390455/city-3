package com.sensoro.common.server.security.response;

import com.sensoro.common.server.response.ResponseBase;
import com.sensoro.common.server.security.bean.SecurityAlarmDetailInfo;

import java.io.Serializable;

public class SecurityAlarmDetailRsp extends ResponseBase implements Serializable {
    public SecurityAlarmDetailInfo data;

    public SecurityAlarmDetailInfo getData() {
        return data;
    }

    public void setData(SecurityAlarmDetailInfo data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SecurityAlarmDetailRsp{" +
                "data=" + data +
                '}';
    }
}

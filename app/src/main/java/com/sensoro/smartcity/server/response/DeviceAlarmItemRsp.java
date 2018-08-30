package com.sensoro.smartcity.server.response;

import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;

import java.io.Serializable;

/**
 * Created by sensoro on 17/7/26.
 */

public class DeviceAlarmItemRsp extends ResponseBase implements Serializable{

    public DeviceAlarmLogInfo getData() {
        return data;
    }

    public void setData(DeviceAlarmLogInfo data) {
        this.data = data;
    }

    protected DeviceAlarmLogInfo data;

    @Override
    public String toString() {
        return "DeviceAlarmListRsp{" +
                "data=" + data +
                '}';
    }
}

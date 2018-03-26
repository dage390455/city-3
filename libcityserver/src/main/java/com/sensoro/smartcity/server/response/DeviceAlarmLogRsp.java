package com.sensoro.smartcity.server.response;

import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sensoro on 17/7/26.
 */

public class DeviceAlarmLogRsp extends ResponseBase implements Serializable{

    public List<DeviceAlarmLogInfo> getData() {
        return data;
    }

    public void setData(List<DeviceAlarmLogInfo> data) {
        this.data = data;
    }

    protected List<DeviceAlarmLogInfo> data;

    @Override
    public String toString() {
        return "DeviceAlarmLogRsp{" +
                "data=" + data +
                '}';
    }
}

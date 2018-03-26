package com.sensoro.smartcity.server.response;

import com.sensoro.smartcity.server.bean.DeviceTypeCount;

/**
 * Created by sensoro on 17/12/6.
 */

public class DeviceTypeCountRsp extends ResponseBase {

    private DeviceTypeCount data;

    public DeviceTypeCount getData() {
        return data;
    }

    public void setData(DeviceTypeCount data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DeviceTypeCountRsp{" +
                "data=" + data +
                '}';
    }
}

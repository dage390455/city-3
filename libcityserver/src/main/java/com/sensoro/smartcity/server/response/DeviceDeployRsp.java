package com.sensoro.smartcity.server.response;

import com.sensoro.smartcity.server.bean.DeviceInfo;

/**
 * Created by sensoro on 17/7/26.
 */

public class DeviceDeployRsp extends ResponseBase {

    private DeviceInfo data;

    public DeviceInfo getData() {
        return data;
    }

    public void setData(DeviceInfo data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DeviceDeployRsp{" +
                "data=" + data +
                '}';
    }
}

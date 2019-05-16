package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.DeviceInfo;

public class DeployDeviceDetailRsp extends ResponseBase {
    private DeviceInfo data;

    public DeviceInfo getData() {
        return data;
    }

    public void setData(DeviceInfo data) {
        this.data = data;
    }
}

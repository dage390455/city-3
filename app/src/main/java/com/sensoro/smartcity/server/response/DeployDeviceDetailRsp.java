package com.sensoro.smartcity.server.response;

import com.sensoro.smartcity.server.bean.DeployDeviceInfo;

public class DeployDeviceDetailRsp extends ResponseBase {
    private DeployDeviceInfo data;

    public DeployDeviceInfo getData() {
        return data;
    }

    public void setData(DeployDeviceInfo data) {
        this.data = data;
    }
}

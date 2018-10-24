package com.sensoro.smartcity.server.response;

import com.sensoro.smartcity.server.bean.DeployDeviceDetail;
import com.tencent.mm.opensdk.modelbase.BaseResp;

public class DeployDeviceDetailRsp extends ResponseBase {
    private DeployDeviceDetail data;

    public DeployDeviceDetail getData() {
        return data;
    }

    public void setData(DeployDeviceDetail data) {
        this.data = data;
    }
}

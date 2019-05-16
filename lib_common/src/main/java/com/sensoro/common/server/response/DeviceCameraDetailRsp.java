package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.DeviceCameraDetailInfo;

public class DeviceCameraDetailRsp extends ResponseBase {
    public DeviceCameraDetailInfo getData() {
        return data;
    }

    public void setData(DeviceCameraDetailInfo data) {
        this.data = data;
    }

    private DeviceCameraDetailInfo data;
}

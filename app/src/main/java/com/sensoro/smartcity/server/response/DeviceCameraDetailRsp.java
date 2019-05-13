package com.sensoro.smartcity.server.response;

import com.sensoro.smartcity.server.bean.DeviceCameraDetailInfo;

public class DeviceCameraDetailRsp extends ResponseBase {
    public DeviceCameraDetailInfo getData() {
        return data;
    }

    public void setData(DeviceCameraDetailInfo data) {
        this.data = data;
    }

    private DeviceCameraDetailInfo data;
}

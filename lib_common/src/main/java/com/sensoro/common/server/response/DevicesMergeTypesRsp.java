package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.DeviceMergeTypesInfo;

public class DevicesMergeTypesRsp extends ResponseBase {
    public DeviceMergeTypesInfo getData() {
        return data;
    }

    public void setData(DeviceMergeTypesInfo data) {
        this.data = data;
    }

    private DeviceMergeTypesInfo data;
}

package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.DeviceUpdateFirmwareData;

import java.util.List;

public class DeviceUpdateFirmwareDataRsp extends ResponseBase {
    private List<DeviceUpdateFirmwareData> data;

    public List<DeviceUpdateFirmwareData> getData() {
        return data;
    }

    public void setData(List<DeviceUpdateFirmwareData> data) {
        this.data = data;
    }
}

package com.sensoro.smartcity.server.response;

import com.sensoro.smartcity.server.bean.DeviceUpdateFirmwareData;

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

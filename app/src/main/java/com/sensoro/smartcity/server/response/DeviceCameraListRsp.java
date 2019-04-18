package com.sensoro.smartcity.server.response;

import com.sensoro.smartcity.server.bean.DeviceCameraInfo;

import java.io.Serializable;
import java.util.List;

public class DeviceCameraListRsp extends ResponseBase implements Serializable {
    public List<DeviceCameraInfo> getData() {
        return data;
    }

    public void setData(List<DeviceCameraInfo> data) {
        this.data = data;
    }

    private List<DeviceCameraInfo> data;
}

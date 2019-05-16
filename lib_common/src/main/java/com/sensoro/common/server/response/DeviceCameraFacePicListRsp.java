package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.DeviceCameraFacePic;

import java.io.Serializable;
import java.util.List;

public class DeviceCameraFacePicListRsp extends ResponseBase implements Serializable {
    public List<DeviceCameraFacePic> getData() {
        return data;
    }

    public void setData(List<DeviceCameraFacePic> data) {
        this.data = data;
    }

    private List<DeviceCameraFacePic> data;
}

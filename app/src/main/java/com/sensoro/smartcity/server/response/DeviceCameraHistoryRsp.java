package com.sensoro.smartcity.server.response;

import com.sensoro.smartcity.server.bean.DeviceCameraHistoryBean;

import java.util.List;

public class DeviceCameraHistoryRsp extends ResponseBase {
    private List<DeviceCameraHistoryBean> data;

    public List<DeviceCameraHistoryBean> getData() {
        return data;
    }

    public void setData(List<DeviceCameraHistoryBean> data) {
        this.data = data;
    }
}

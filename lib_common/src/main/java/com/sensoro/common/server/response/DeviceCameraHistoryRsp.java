package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.DeviceCameraHistoryBean;

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

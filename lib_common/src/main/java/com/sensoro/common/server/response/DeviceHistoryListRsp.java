package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.DeviceHistoryInfo;

import java.util.List;

/**
 * Created by sensoro on 17/7/26.
 */

public class DeviceHistoryListRsp extends ResponseBase {


    public List<DeviceHistoryInfo> getData() {
        return data;
    }

    public void setData(List<DeviceHistoryInfo> data) {
        this.data = data;
    }

    protected List<DeviceHistoryInfo> data;

    @Override
    public String toString() {
        return "DeviceHistoryListRsp{" +
                "data=" + data +
                '}';
    }
}

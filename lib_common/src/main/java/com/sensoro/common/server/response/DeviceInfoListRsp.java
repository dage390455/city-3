package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.DeviceInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sensoro on 17/7/26.
 */

public class DeviceInfoListRsp extends ResponseBase implements Serializable{

    public List<DeviceInfo> getData() {
        return data;
    }

    public void setData(List<DeviceInfo> data) {
        this.data = data;
    }

    protected List<DeviceInfo> data;

    @Override
    public String toString() {
        return "DeviceInfoListRsp{" +
                "data=" + data +
                '}';
    }

}

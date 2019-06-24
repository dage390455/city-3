package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.CameraWarnInfo;
import com.sensoro.common.server.bean.DeviceAlarmLogInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sensoro on 17/7/26.
 */

public class CameraWarnRsp extends ResponseBase implements Serializable{

    public List<CameraWarnInfo> getData() {
        return data;
    }

    public void setData(List<CameraWarnInfo> data) {
        this.data = data;
    }

    protected List<CameraWarnInfo> data;

    @Override
    public String toString() {
        return "CameraWarnInfo{" +
                "data=" + data +
                '}';
    }
}

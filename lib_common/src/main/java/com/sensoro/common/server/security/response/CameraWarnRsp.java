package com.sensoro.common.server.security.response;

import com.sensoro.common.server.response.ResponseBase;
import com.sensoro.common.server.security.bean.SecurityAlarmInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sensoro on 17/7/26.
 */

public class CameraWarnRsp extends ResponseBase implements Serializable{

    public List<SecurityAlarmInfo> getData() {
        return data;
    }

    public void setData(List<SecurityAlarmInfo> data) {
        this.data = data;
    }

    protected List<SecurityAlarmInfo> data;

    @Override
    public String toString() {
        return "SecurityAlarmInfo{" +
                "data=" + data +
                '}';
    }

}

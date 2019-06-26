package com.sensoro.common.server.security.response;

import com.sensoro.common.server.response.ResponseBase;
import com.sensoro.common.server.security.bean.SecurityAlarmInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sensoro on 17/7/26.
 * 获取安防预警列表
 */

public class SecurityAlarmListRsp extends ResponseBase implements Serializable {

    protected SecurityAlarmListData data;
    public SecurityAlarmListData getData() {
        return data;
    }

    public void setData(SecurityAlarmListData data) {
        this.data = data;
    }


    public static class SecurityAlarmListData {
        int total;
        int offset;
        int limit;
        List<SecurityAlarmInfo> list;
    }

    @Override
    public String toString() {
        return "SecurityAlarmListRsp{" +
                "data=" + data +
                '}';
    }
}

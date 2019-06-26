package com.sensoro.common.server.security.response;

import com.sensoro.common.server.response.ResponseBase;
import com.sensoro.common.server.security.bean.SecurityAlarmEventInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 获取安防预警事件详情时间线
 */
public class SecurityAlarmTimelineRsp extends ResponseBase implements Serializable {
    public SecurityAlarmTimelineData data;

    public SecurityAlarmTimelineData getData() {
        return data;
    }

    public void setData(SecurityAlarmTimelineData data) {
        this.data = data;
    }

    class SecurityAlarmTimelineData {
        public String id;
        public List<SecurityAlarmEventInfo> list;
    }

    @Override
    public String toString() {
        return "SecurityAlarmTimelineRsp{" +
                "data=" + data +
                '}';
    }
}

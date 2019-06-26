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
    public void setList(List<SecurityAlarmInfo> list){
        if(null == this.data){
            this.data = new SecurityAlarmListData();
        }
        this.data.list = list;
        this.data.total = list.size();
    }


    public static class SecurityAlarmListData {
        public int total;
        public int offset;
        public int limit;
        public List<SecurityAlarmInfo> list;
    }

    @Override
    public String toString() {
        return "SecurityAlarmListRsp{" +
                "data=" + data +
                '}';
    }
}

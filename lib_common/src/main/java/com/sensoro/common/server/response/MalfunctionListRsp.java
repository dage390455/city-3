package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.MalfunctionListInfo;

import java.util.List;

public class MalfunctionListRsp extends ResponseBase {
    public List<MalfunctionListInfo> getData() {
        return data;
    }

    public void setData(List<MalfunctionListInfo> data) {
        this.data = data;
    }

    protected List<MalfunctionListInfo> data;

    protected int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "DeviceAlarmLogRsp{" +
                "data=" + data +
                '}';
    }
}

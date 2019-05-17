package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.DeployRecordInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sensoro on 17/7/26.
 */

public class DeployRecordRsp extends ResponseBase implements Serializable{

    protected List<DeployRecordInfo> data;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    protected int count;

    public List<DeployRecordInfo> getData() {
        return data;
    }

    public void setData(List<DeployRecordInfo> data) {
        this.data = data;
    }
    @Override
    public String toString() {
        return "DeviceAlarmLogRsp{" +
                "data=" + data +"count="+count+
                '}';
    }
}

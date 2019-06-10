package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.BaseStationInfo;

import java.io.Serializable;
import java.util.List;

public class BaseStationListRsp extends ResponseBase implements Serializable {
    public List<BaseStationInfo> getData() {
        return data;
    }

    public void setData(List<BaseStationInfo> data) {
        this.data = data;
    }

    private List<BaseStationInfo> data;
}

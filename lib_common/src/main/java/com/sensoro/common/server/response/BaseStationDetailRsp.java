package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.BaseStationDetailModel;

public class BaseStationDetailRsp extends ResponseBase {
    private BaseStationDetailModel data;

    public BaseStationDetailModel getData() {
        return data;
    }

    public void setData(BaseStationDetailModel data) {
        this.data = data;
    }
}

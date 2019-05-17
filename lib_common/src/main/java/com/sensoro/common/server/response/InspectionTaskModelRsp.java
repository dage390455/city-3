package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.InspectionTaskModel;

public class InspectionTaskModelRsp extends ResponseBase{
    private InspectionTaskModel data;

    public InspectionTaskModel getData() {
        return data;
    }

    public void setData(InspectionTaskModel data) {
        this.data = data;
    }
}

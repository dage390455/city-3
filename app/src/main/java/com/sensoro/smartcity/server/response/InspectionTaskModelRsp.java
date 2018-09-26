package com.sensoro.smartcity.server.response;

import com.sensoro.smartcity.server.bean.InspectionTaskModel;

public class InspectionTaskModelRsp extends ResponseBase{
    private InspectionTaskModel data;

    public InspectionTaskModel getData() {
        return data;
    }

    public void setData(InspectionTaskModel data) {
        this.data = data;
    }
}

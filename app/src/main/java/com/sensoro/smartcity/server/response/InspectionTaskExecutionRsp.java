package com.sensoro.smartcity.server.response;

import com.sensoro.smartcity.server.bean.InspectionTaskExecutionModel;

public class InspectionTaskExecutionRsp extends ResponseBase {
    public InspectionTaskExecutionModel getData() {
        return data;
    }

    public void setData(InspectionTaskExecutionModel data) {
        this.data = data;
    }

    private InspectionTaskExecutionModel data;
}

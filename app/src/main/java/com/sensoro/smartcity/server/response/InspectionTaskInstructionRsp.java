package com.sensoro.smartcity.server.response;

import com.sensoro.smartcity.server.bean.InspectionTaskInstructionModel;

public class InspectionTaskInstructionRsp extends ResponseBase{
    private InspectionTaskInstructionModel data;

    public InspectionTaskInstructionModel getData() {
        return data;
    }

    public void setData(InspectionTaskInstructionModel data) {
        this.data = data;
    }
}

package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.InspectionTaskInstructionModel;

public class InspectionTaskInstructionRsp extends ResponseBase{
    private InspectionTaskInstructionModel data;

    public InspectionTaskInstructionModel getData() {
        return data;
    }

    public void setData(InspectionTaskInstructionModel data) {
        this.data = data;
    }
}

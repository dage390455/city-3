package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.InspectionTaskExceptionDeviceModel;


public class InspectionTaskExceptionDeviceRsp extends ResponseBase {
    public InspectionTaskExceptionDeviceModel getData() {
        return data;
    }

    public void setData(InspectionTaskExceptionDeviceModel data) {
        this.data = data;
    }

    private InspectionTaskExceptionDeviceModel data;
}

package com.sensoro.smartcity.server.response;

import com.sensoro.smartcity.server.bean.InspectionTaskExceptionDeviceModel;


public class InspectionTaskExceptionDeviceRsp extends ResponseBase {
    public InspectionTaskExceptionDeviceModel getData() {
        return data;
    }

    public void setData(InspectionTaskExceptionDeviceModel data) {
        this.data = data;
    }

    private InspectionTaskExceptionDeviceModel data;
}

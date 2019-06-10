package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.InspectionTaskDeviceDetailModel;


public class InspectionTaskDeviceDetailRsp extends ResponseBase {
    public InspectionTaskDeviceDetailModel getData() {
        return data;
    }

    public void setData(InspectionTaskDeviceDetailModel data) {
        this.data = data;
    }

    private InspectionTaskDeviceDetailModel data;
}

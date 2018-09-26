package com.sensoro.smartcity.server.response;

import com.sensoro.smartcity.server.bean.InspectionTaskDeviceDetailModel;


public class InspectionTaskDeviceDetailRsp extends ResponseBase {
    public InspectionTaskDeviceDetailModel getData() {
        return data;
    }

    public void setData(InspectionTaskDeviceDetailModel data) {
        this.data = data;
    }

    private InspectionTaskDeviceDetailModel data;
}

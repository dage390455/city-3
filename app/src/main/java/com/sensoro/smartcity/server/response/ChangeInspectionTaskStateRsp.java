package com.sensoro.smartcity.server.response;

import com.sensoro.smartcity.server.bean.ChangeInspectionTaskStateInfo;

public class ChangeInspectionTaskStateRsp extends ResponseBase{
    public ChangeInspectionTaskStateInfo getData() {
        return data;
    }

    public void setData(ChangeInspectionTaskStateInfo data) {
        this.data = data;
    }

    private ChangeInspectionTaskStateInfo data;
}

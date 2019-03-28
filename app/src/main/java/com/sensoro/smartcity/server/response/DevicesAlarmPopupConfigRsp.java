package com.sensoro.smartcity.server.response;

import com.sensoro.smartcity.server.bean.AlarmPopupDataBean;

public class DevicesAlarmPopupConfigRsp extends ResponseBase {
    public AlarmPopupDataBean getData() {
        return data;
    }

    public void setData(AlarmPopupDataBean data) {
        this.data = data;
    }

    private AlarmPopupDataBean data;
}

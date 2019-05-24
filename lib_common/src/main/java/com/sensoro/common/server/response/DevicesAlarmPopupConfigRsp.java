package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.AlarmPopupDataBean;

public class DevicesAlarmPopupConfigRsp extends ResponseBase {
    public AlarmPopupDataBean getData() {
        return data;
    }

    public void setData(AlarmPopupDataBean data) {
        this.data = data;
    }

    private AlarmPopupDataBean data;
}

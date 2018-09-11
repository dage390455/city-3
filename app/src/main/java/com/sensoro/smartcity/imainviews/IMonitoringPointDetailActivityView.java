package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.DeviceInfo;

public interface IMonitoringPointDetailActivityView extends IToast, IProgressDialog, IActivityIntent {
    void setTitleNameTextView(String name);

    void setUpdateTime(String time);

    void setAlarmStateColor(int color);

    void setContractName(String contractName);

    void setContractPhone(String contractPhone);

    void setDeviceLocation(String location);

    void updateDeviceInfoAdapter(DeviceInfo deviceInfo);
}

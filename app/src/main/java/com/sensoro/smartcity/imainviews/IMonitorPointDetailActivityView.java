package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.DeviceInfo;

import java.util.List;

public interface IMonitorPointDetailActivityView extends IToast, IProgressDialog, IActivityIntent {
    void setTitleNameTextView(String name);

    void setUpdateTime(String time);

    void setAlarmStateColor(int color);

    void setContractName(String contractName);

    void setContractPhone(String contractPhone);

    void setDeviceLocation(String location);

    void updateDeviceInfoAdapter(DeviceInfo deviceInfo);

    void setSNText(String sn);

    void updateTags(List<String> list);

    void setBatteryInfo(String battery);

    void setInterval(String interval);

    void setStatusInfo(String statusInfo, int textColor);

    void setContactPhoneIconVisible(boolean isVisible);

}

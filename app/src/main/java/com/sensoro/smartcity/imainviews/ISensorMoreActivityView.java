package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

public interface ISensorMoreActivityView extends IToast, IProgressDialog {
    void setAlarmRecentInfo(String info);

    void setAlarmRecentInfo(int resID);

    void setSNText(String sn);

    void setTypeText(String type);

    void setTagText(String tag);

    void setLongitudeLatitude(String lon, String lat);

    void setAlarmSetting(String alarmSetting);

    void setInterval(String interval);

    void setName(String name);

    void setName(int resId);

    void setStatusInfo(String status, int background);

    void setBatteryInfo(String battery);

    void setPhoneText(String phone);

    void setReportText(String report);
}




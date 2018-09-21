package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.AlarmInfo;

import java.util.List;

public interface IAlarmDetailLogActivityView extends IToast, IProgressDialog, IActivityIntent {
    void setDeviceNameTextView(String name);

    void setCurrentAlarmState(int state, String time);

    void setAlarmCount(String count);

    void updateAlertLogContentAdapter(List<AlarmInfo.RecordInfo> recordInfoList);

    void showAlarmPopupView();

    void dismissAlarmPopupView();

    void setUpdateButtonClickable(boolean canClick);

    void setConfirmText(String text);
}

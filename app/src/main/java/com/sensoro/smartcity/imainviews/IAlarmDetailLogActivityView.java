package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
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
    void setConfirmBg(int resId);
    void setConfirmColor(int resId);

    void setDeviceSn(String deviceSN);
}

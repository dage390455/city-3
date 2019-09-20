package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.model.AlarmPopupModel;

import java.util.List;

public interface IAlarmDetailLogActivityView extends IToast, IProgressDialog, IActivityIntent {
    void setDeviceNameTextView(String name);

    void setCurrentAlarmState(String time);

    void setAlarmCount(String count);

    void updateAlertLogContentAdapter(DeviceAlarmLogInfo deviceAlarmLogInfo);

    void showAlarmPopupView(AlarmPopupModel alarmPopupModel);

    void dismissAlarmPopupView();

    void setUpdateButtonClickable(boolean canClick);

    void setConfirmText(String text);

    void setConfirmBg(int resId);

    void setConfirmColor(int resId);

    void setDeviceSn(String deviceSN);

    void setCameraLiveCount(List<String> liveCount);

    void setLlVideoSizeAndContent(int size,String content);

    void setHistoryLogVisible(boolean visible);

    void setCloseWarnVisible(boolean visible);
}

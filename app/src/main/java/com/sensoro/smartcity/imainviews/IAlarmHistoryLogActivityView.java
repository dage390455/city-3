package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.bean.DeviceAlarmLogInfo;

import java.util.List;

public interface IAlarmHistoryLogActivityView extends IToast, IProgressDialog, IActivityIntent {
    void showCalendar(long startTime, long endTime);

    void updateAlarmListAdapter(List<DeviceAlarmLogInfo> data);

    void onPullRefreshComplete();

    void setDateSelectVisible(boolean isVisible);

    void setDateSelectText(String text);

    void setNoContentVisible(boolean isVisible);
}

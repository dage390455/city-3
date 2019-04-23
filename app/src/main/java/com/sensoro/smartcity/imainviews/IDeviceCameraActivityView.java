package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.DeviceCameraInfo;

import java.util.List;

public interface IDeviceCameraActivityView extends IToast, IProgressDialog, IActivityIntent {
    void showCalendar(long startTime, long endTime);

    void updateDeviceCameraAdapter(List<DeviceCameraInfo> data);

    void onPullRefreshCompleteNoMoreData();

    void onPullRefreshComplete();

    void setDateSelectVisible(boolean isVisible);

    void setDateSelectText(String text);

    void setNoContentVisible(boolean isVisible);
    void setSmartRefreshEnable(boolean enable);
}

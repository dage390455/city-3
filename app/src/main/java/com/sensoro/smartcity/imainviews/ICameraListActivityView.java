package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.smartcity.model.CameraFilterModel;
import com.sensoro.smartcity.server.bean.DeviceCameraInfo;

import java.util.List;

public interface ICameraListActivityView extends IToast, IProgressDialog, IActivityIntent {
    void showCalendar(long startTime, long endTime);

    void updateDeviceCameraAdapter(List<DeviceCameraInfo> data);

    void updateFilterPop(List<CameraFilterModel> data);

    void onPullRefreshCompleteNoMoreData();

    void onPullRefreshComplete();

    void setDateSelectVisible(boolean isVisible);

    void setDateSelectText(String text);

    void setNoContentVisible(boolean isVisible);
    void setSmartRefreshEnable(boolean enable);


    void setSearchClearImvVisible(boolean isVisible);

    void updateSearchHistoryList(List<String> data);

    void setSearchHistoryVisible(boolean isVisible);

    void showHistoryClearDialog();

    void setSearchButtonTextVisible(boolean isVisible);

    void resetRefreshNoMoreData();

    void setToptitleState();
}

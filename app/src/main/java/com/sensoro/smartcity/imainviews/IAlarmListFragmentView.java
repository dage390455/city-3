package com.sensoro.smartcity.imainviews;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.server.response.DeviceAlarmLogRsp;

import java.util.List;

public interface IAlarmListFragmentView extends IProgressDialog, IToast, IActivityIntent {
    void updateAlarmListAdapter(List<DeviceAlarmLogInfo> deviceAlarmLogInfoList);

    void showAlarmPopupView(DeviceAlarmLogInfo deviceAlarmLogInfo);

    boolean isSelectedDateLayoutVisible();

    void setSelectedDateLayoutVisible(boolean isVisible);

    boolean isSearchLayoutVisible();

    void setSearchLayoutVisible(boolean isVisible);

    void setAlarmSearchText(String searchText);

    void onPullRefreshComplete();

    void setSelectedDateSearchText(String searchText);

    PullToRefreshBase.State getPullRefreshState();

    void requestDataByDate(String startDate, String endDate);

    void refreshUIByType(String type);

    void requestDataByDirection(int direction, boolean isForce);

    void refreshUIBySearch(int direction, DeviceAlarmLogRsp deviceAlarmLogRsp, String searchText);
}

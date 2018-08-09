package com.sensoro.smartcity.imainviews;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;

import java.util.List;

public interface IAlarmListFragmentView extends IProgressDialog, IToast, IActivityIntent {
    void updateAlarmListAdapter(List<DeviceAlarmLogInfo> deviceAlarmLogInfoList);

    void showAlarmPopupView();

    void dismissAlarmPopupView();

    boolean isSelectedDateLayoutVisible();

    void setSelectedDateLayoutVisible(boolean isVisible);

    boolean isSearchLayoutVisible();

    void setSearchLayoutVisible(boolean isVisible);

    void setAlarmSearchText(String searchText);

    void onPullRefreshComplete();

    void setSelectedDateSearchText(String searchText);

    PullToRefreshBase.State getPullRefreshState();

    void requestDataByDirection(int direction, boolean isForce);

    void setUpdateButtonClickable(boolean canClick);
}

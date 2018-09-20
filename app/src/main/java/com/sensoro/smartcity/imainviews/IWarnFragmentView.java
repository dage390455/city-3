package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;

import java.util.List;

public interface IWarnFragmentView extends IToast, IActivityIntent, IProgressDialog {
    void updateAlarmListAdapter(List<DeviceAlarmLogInfo> deviceAlarmLogInfoList);

    void showAlarmPopupView();

    void dismissAlarmPopupView();

    void onPullRefreshComplete();

    void onPullRefreshCompleteNoMoreData();

    void setUpdateButtonClickable(boolean canClick);


    void setSelectedDateLayoutVisible(boolean b);

    void setSelectedDateSearchText(String s);

    boolean isSelectedDateLayoutVisible();

    void setSearchButtonTextVisible(boolean isVisible);

    boolean getSearchTextVisible();
}

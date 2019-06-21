package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.model.AlarmPopupModel;
import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.bean.DeviceAlarmLogInfo;

import java.util.List;

public interface IFireWarnFragmentView extends IToast, IActivityIntent, IProgressDialog {
    void cancelSearchData();

    void updateAlarmListAdapter(List<DeviceAlarmLogInfo> deviceAlarmLogInfoList);

    void showAlarmPopupView(AlarmPopupModel alarmPopupModel);

    void dismissAlarmPopupView();

    void onPullRefreshComplete();

    void onPullRefreshCompleteNoMoreData();

    void setUpdateButtonClickable(boolean canClick);


    void setSelectedDateLayoutVisible(boolean b);

    void setSelectedDateSearchText(String s);

    boolean isSelectedDateLayoutVisible();

    void setSearchButtonTextVisible(boolean isVisible);

    boolean getSearchTextVisible();

    void setNoContentVisible(boolean isVisible);

    void setSearchClearImvVisible(boolean isVisible);

    void updateSearchHistoryList(List<String> data);

    void setSearchHistoryVisible(boolean isVisible);

    void showHistoryClearDialog();
}

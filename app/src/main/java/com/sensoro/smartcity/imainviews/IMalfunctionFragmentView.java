package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.MalfunctionListInfo;

import java.util.List;

public interface IMalfunctionFragmentView extends IToast,IProgressDialog,IActivityIntent {
    void cancelSearchState();

    boolean isSelectedDateLayoutVisible();

    void setSelectedDateLayoutVisible(boolean isVisible);

    void setSelectedDateSearchText(String s);

    void onPullRefreshComplete();

    void onPullRefreshCompleteNoMoreData();

    void setSearchButtonTextVisible(boolean b);

    void updateAlarmListAdapter(List<MalfunctionListInfo> mMalfunctionInfoList);

    void UpdateSearchHistoryList(List<String> data);

    void setSearchHistoryVisible(boolean isVisible);

    void setSearchClearImvVisible(boolean isVisible);
}

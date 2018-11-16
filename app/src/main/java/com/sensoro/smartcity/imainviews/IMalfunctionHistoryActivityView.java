package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.MalfunctionListInfo;

import java.util.List;

public interface IMalfunctionHistoryActivityView extends IToast,IProgressDialog,IActivityIntent {
    void onPullRefreshComplete();

    void onPullRefreshCompleteNoMoreData();

    void updateMalfunctionListAdapter(List<MalfunctionListInfo> mMalfunctionInfoList);

    void setNoContentVisible(boolean isVisible);

    void showCalendar(Long startTime, Long endTime);

    void setDateSelectVisible(boolean b);

    void setDateSelectText(String s);
}

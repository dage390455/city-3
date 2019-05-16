package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.bean.MalfunctionListInfo;

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

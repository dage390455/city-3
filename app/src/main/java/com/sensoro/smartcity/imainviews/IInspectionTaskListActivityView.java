package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.bean.InspectionIndexTaskInfo;

import java.util.List;

public interface IInspectionTaskListActivityView extends IToast,IProgressDialog,IActivityIntent{
    void setRlDateEditVisible(boolean isVisible);

    boolean getRlDateEditIsVisible();

    void setSelectedDateSearchText(String time);

    void updateRcContent(List<InspectionIndexTaskInfo> tasks);

    void onPullRefreshCompleted();

    void recycleViewRefreshCompleteNoMoreData();

    void rcSmoothScrollToTop();

    void closeRefreshHeaderOrFooter();

    void setNoContentVisible(boolean isVisible);
}

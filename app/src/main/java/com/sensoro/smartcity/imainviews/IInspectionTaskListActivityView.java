package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.InspectionIndexTaskInfo;
import com.sensoro.smartcity.server.bean.InspectionTaskModel;

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
}

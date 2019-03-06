package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.DeployRecordInfo;

import java.util.List;

public interface IDeployRecordActivityView extends IToast, IProgressDialog, IActivityIntent {
    boolean isSelectedDateLayoutVisible();

    void updateRcContentData(List<DeployRecordInfo> data);

    void onPullRefreshComplete();

    void setSelectedDateLayoutVisible(boolean isVisible);

    void setSelectedDateSearchText(String s);

    String getSearchText();

    void setSearchButtonTextVisible(boolean isVisible);

    boolean getSearchTextVisible();

    void updateSearchHistoryList(List<String> data);

    void setSearchHistoryVisible(boolean isVisible);

    void showHistoryClearDialog();
}

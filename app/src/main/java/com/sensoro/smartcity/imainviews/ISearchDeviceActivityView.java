package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.DeviceInfo;

import java.util.List;

public interface ISearchDeviceActivityView extends IToast, IProgressDialog, IActivityIntent {
    void showListLayout();

    void showGridLayout();

    void returnTop();

    void switchToTypeGrid();

    void switchToTypeList();

    void setSearchHistoryLayoutVisible(boolean isVisible);

    void setRelationLayoutVisible(boolean isVisible);

    void setIndexListLayoutVisible(boolean isVisible);

    void setTipsLinearLayoutVisible(boolean isVisible);

    void filterByStatusWithRequest(int position);

    void filterByTypeWithRequest(int position);

    void recycleViewRefreshComplete();

    void refreshData(List<DeviceInfo> dataList);

    void updateRelationData(List<String> strList);

    void updateSearchHistoryData();
    boolean getSearchDataListVisible();
}

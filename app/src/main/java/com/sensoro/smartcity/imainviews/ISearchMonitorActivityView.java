package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.DeviceInfo;

import java.util.List;

public interface ISearchMonitorActivityView extends IToast, IProgressDialog, IActivityIntent {
    void showListLayout();

    void returnTop();

    void setSearchHistoryLayoutVisible(boolean isVisible);

    void setRelationLayoutVisible(boolean isVisible);

    void setIndexListLayoutVisible(boolean isVisible);

    void recycleViewRefreshComplete();

    void refreshData(List<DeviceInfo> dataList);

    void updateRelationData(List<String> strList);

    void updateSearchHistoryData(List<String> strHistory);

    void setEditText(String text);


    void setTypeView(String typesText);

    void setNoContentVisible(boolean isVisible);

    void hideSoftInput();

    void setHistoryClearBtnVisible(boolean isVisible);

    void showHistoryClearDialog();
}

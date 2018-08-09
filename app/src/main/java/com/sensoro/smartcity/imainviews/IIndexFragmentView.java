package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;

import java.util.List;

public interface IIndexFragmentView extends IToast, IProgressDialog, IActivityIntent {
    void refreshTop(boolean isFirstInit, int alarmCount, int lostCount, int inactiveCount);

    void returnTop();

    void switchToTypeGrid();

    void switchToTypeList();

    void showListLayout();

    void showGridLayout();

    void refreshData(List<DeviceInfo> dataList);

    void refreshCityInfo();

    void showTypePopupView();

    void showStatusPopupView();

    void filterByStatusWithRequest(int position);

    void filterByTypeWithRequest(int position);

    void reFreshDataByDirection(int direction);

    void refreshBySearch(DeviceInfoListRsp infoRspData);

    void requestTopData(final boolean isFirstInit);

    void recycleViewRefreshComplete();

    void playFlipAnimation();

}

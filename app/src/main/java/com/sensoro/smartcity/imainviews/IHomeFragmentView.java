package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.model.HomeTopModel;

import java.util.List;

public interface IHomeFragmentView extends IToast, IProgressDialog, IActivityIntent {
    void updateRcTypeAdapter(List<String> data);

    void setImvAddVisible(boolean isVisible);

    void setImvSearchVisible(boolean isVisible);

    void refreshHeaderData(boolean isFirstInit, List<HomeTopModel> data);

    void returnTop();

    void refreshContentData(boolean isFirstInit,List<HomeTopModel> dataList);

//    void recycleViewRefreshComplete();

//    void recycleViewRefreshCompleteNoMoreData();
//    void recycleViewRefreshComplete();

    void setDetectionPoints(String count);

    void updateSelectDeviceTypePopAndShow(List<String> devicesTypes);

    void setToolbarTitleCount(String text);

    void setToolbarTitleBackgroundColor(int color);
}

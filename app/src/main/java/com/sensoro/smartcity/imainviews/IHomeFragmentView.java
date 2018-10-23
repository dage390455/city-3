package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.model.HomeTopModel;
import com.sensoro.smartcity.server.bean.DeviceInfo;

import java.util.List;

public interface IHomeFragmentView extends IToast, IProgressDialog, IActivityIntent {
    void updateRcTypeAdapter(List<String> data);

    void setImvAddVisible(boolean isVisible);

    void setImvSearchVisible(boolean isVisible);

    void refreshTop(boolean isFirstInit, List<HomeTopModel> data);

    void returnTop();

    void refreshData(List<DeviceInfo> dataList);

    void recycleViewRefreshComplete();

    void recycleViewRefreshCompleteNoMoreData();
//    void recycleViewRefreshComplete();

    void setDetectionPoints(String count);

    void setNoContentVisible(boolean isVisible);

    void updateSelectDeviceTypePopAndShow(List<String> devicesTypes);
    void setToolbarTitleCount(String text);
    void setToolbarTitleBackgroundColor(int color);
}

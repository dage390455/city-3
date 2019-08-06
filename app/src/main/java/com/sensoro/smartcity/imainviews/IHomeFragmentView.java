package com.sensoro.smartcity.imainviews;

import android.view.View;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.bean.DeviceInfo;
import com.sensoro.smartcity.model.HomeTopModel;

import java.util.List;

public interface IHomeFragmentView extends IToast, IProgressDialog, IActivityIntent {
    void updateRcTypeAdapter(List<String> data);

    void setImvAddVisible(boolean isVisible);

    void setImvSearchVisible(boolean isVisible);

    void refreshHeaderData(boolean isFirstInit, List<HomeTopModel> data);

    void returnTop();

    void refreshContentData(boolean isFirstInit, boolean isPageChanged, List<DeviceInfo>  deviceInfoList);

//    void recycleViewRefreshComplete();

//    void recycleViewRefreshCompleteNoMoreData();
//    void recycleViewRefreshComplete();

    void setDetectionPoints(String count);

    void updateSelectDeviceTypePopAndShow(List<String> devicesTypes);

    void setToolbarTitleCount(String text);

    void setToolbarTitleBackgroundColor(int color);

    void setImvTopAddVisible(boolean b);

    void setImvHeaderLeftVisible(boolean isVisible);

    void setImvHeaderRightVisible(boolean isVisible);

    void recycleViewRefreshComplete();

    void recycleViewRefreshCompleteNoMoreData();

    void showAlarmInfoView();

    void dismissAlarmInfoView();

    int getFirstVisibleItemPosition();



    /**
     *  状态变化数据更新过度动画设置
     */
    void  startAnimation(View view,int animResID);

    /**
     *  过滤条件查看窗口
     */
    void updateSelectFilterConditionPopAndShow(List mSortConditionList,String selectedCondition);


}

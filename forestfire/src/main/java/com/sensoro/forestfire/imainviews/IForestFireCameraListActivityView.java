package com.sensoro.forestfire.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.model.CameraFilterModel;
import com.sensoro.forestfire.model.ForestFireCameraBean;

import java.util.List;

/**
 * @Author: jack
 * 时  间: 2019-09-17
 * 包  名: com.sensoro.forestfire.imainviews
 * 简  述: <功能简述>
 */
public interface IForestFireCameraListActivityView extends IToast, IProgressDialog, IActivityIntent {
    void updateDeviceCameraAdapter(List<ForestFireCameraBean> data);

    void onPullRefreshComplete();

    void setNoContentVisible(boolean isVisible);

    void setSmartRefreshEnable(boolean enable);


    void setSearchClearImvVisible(boolean isVisible);

    void updateSearchHistoryList(List<String> data);

    void setSearchHistoryVisible(boolean isVisible);

    void showHistoryClearDialog();

    void setSearchButtonTextVisible(boolean isVisible);

    void setTopTitleState();

    void showCameraListFilterPopupWindow(List<CameraFilterModel> data);

    void dismissCameraListFilterPopupWindow();

    void updateCameraListFilterPopupWindowStatusList(List<CameraFilterModel> list);

    void setCameraListFilterPopupWindowSelectState(boolean hasSelect);
}

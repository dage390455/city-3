package com.sensoro.nameplate.IMainViews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.model.CameraFilterModel;
import com.sensoro.common.server.bean.DeviceCameraInfo;

import java.util.List;

public interface INameplateListActivityView extends IToast, IProgressDialog, IActivityIntent {
    void updateDeviceCameraAdapter(List<DeviceCameraInfo> data);

    void onPullRefreshComplete();

    void setNoContentVisible(boolean isVisible);

    void setSmartRefreshEnable(boolean enable);

    void setSearchClearImvVisible(boolean isVisible);

    void updateSearchHistoryList(List<String> data);

    void setSearchHistoryVisible(boolean isVisible);

    void showHistoryClearDialog();

    void setSearchButtonTextVisible(boolean isVisible);

    void showCameraListFilterPopupWindow(List<CameraFilterModel> data);

    void dismissCameraListFilterPopupWindow();

    void updateCameraListFilterPopupWindowStatusList(List<CameraFilterModel> list);

    void setCameraListFilterPopupWindowSelectState(boolean hasSelect);
}

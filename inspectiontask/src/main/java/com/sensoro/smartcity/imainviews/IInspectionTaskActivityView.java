package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.model.StatusCountModel;
import com.sensoro.common.server.bean.InspectionTaskDeviceDetail;

import java.util.List;

public interface IInspectionTaskActivityView extends IToast, IProgressDialog, IActivityIntent {
    void updateSelectDeviceTypeList(List<String> deviceTypes);

    void updateSelectDeviceStatusList(List<StatusCountModel> data);

    void updateInspectionTaskDeviceItem(List<InspectionTaskDeviceDetail> data);

    boolean getSearchTextVisible();

    void setSearchButtonTextVisible(boolean isVisible);

    void onPullRefreshComplete();

    void setBottomInspectionStateTitle(String finish, String unFinish);

    void showSelectDeviceTypePop();

    void setNoContentVisible(boolean isVisible);

    void showBleTips();

    void hideBleTips();

    void showSelectDeviceStatusPop();

    void UpdateSearchHistoryList(List<String> data);

    void setSearchHistoryVisible(boolean isVisible);

    void setSearchClearImvVisible(boolean isVisible);

    void showHistoryClearDialog();
}

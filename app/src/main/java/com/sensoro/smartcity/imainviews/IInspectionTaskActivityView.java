package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.model.InspectionStatusCountModel;
import com.sensoro.smartcity.server.bean.InspectionTaskDeviceDetail;

import java.util.List;

public interface IInspectionTaskActivityView extends IToast, IProgressDialog, IActivityIntent {
    void updateSelectDeviceTypeList(List<String> deviceTypes);

    void updateSelectDeviceStatusList(List<InspectionStatusCountModel> data);

    void updateInspectionTaskDeviceItem(List<InspectionTaskDeviceDetail> data);

    boolean getSearchTextVisible();

    void setSearchButtonTextVisible(boolean isVisible);

    void onPullRefreshComplete();

    void onPullRefreshCompleteNoMoreData();

    void setBottomInspectionStateTitle(String finish, String unFinish);

    void showSelectDeviceTypePop();

    void setNoContentVisible(boolean isVisible);

    void showBleTips();

    void hideBleTips();

    void showSelectDeviceStatusPop();

    void UpdateSearchHistoryList(List<String> data);

    void setSearchHistoryVisible(boolean isVisible);

    void setSearchClearImvVisible(boolean isVisible);
}

package com.sensoro.nameplate.IMainViews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.bean.NamePlateInfo;
import com.sensoro.nameplate.model.FilterModel;

import java.util.List;

public interface INameplateListActivityView extends IToast, IProgressDialog, IActivityIntent {
    void updateNameplateAdapter(List<NamePlateInfo> data);

    void onPullRefreshComplete();

    void setNoContentVisible(boolean isVisible);

    void setSmartRefreshEnable(boolean enable);

    void setSearchClearImvVisible(boolean isVisible);

    void updateSearchHistoryList(List<String> data);

    void setSearchHistoryVisible(boolean isVisible);

    void showHistoryClearDialog();

    void setSearchButtonTextVisible(boolean isVisible);


    void updateDeleteNamePlateStatus(int pos);

    void updateSelectDeviceStatusList(List<FilterModel> list);
}

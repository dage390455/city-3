package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.smartcity.model.InspectionStatusCountModel;
import com.sensoro.smartcity.server.bean.ContractListInfo;

import java.util.List;

public interface IContractManagerActivityView extends IProgressDialog, IToast, IActivityIntent {
    //    void updateContractListAdapter(List<DeviceAlarmLogInfo> deviceAlarmLogInfoList);
    void onPullRefreshComplete();

//    PullToRefreshBase.State getPullRefreshState();

    void requestDataByDirection(int direction, boolean isFirst);

    void updateContractList(List<ContractListInfo> data);

    void showSmartRefreshNoMoreData();

    void smoothScrollToPosition(int position);

    void closeRefreshHeaderOrFooter();

    void setNoContentVisible(boolean isVisible);

    void UpdateSelectStatusPopList(List<InspectionStatusCountModel> list);

    void showSelectStatusPop();

    void UpdateSelectTypePopList(List<InspectionStatusCountModel> list);

    void showSelectStTypePop();

    boolean getSearchTextVisible();

    void setSearchButtonTextVisible(boolean isVisible);

    void setSearchClearImvVisible(boolean isVisible);

    void setSearchHistoryVisible(boolean isVisible);

    void UpdateSearchHistoryList(List<String> data);

    boolean isSelectedDateLayoutVisible();

    void setSelectedDateLayoutVisible(boolean isVisible);

    void setSelectedDateSearchText(String content);

    void showHistoryClearDialog();

    void setContractMangerAddVisible(boolean isVisible);
}
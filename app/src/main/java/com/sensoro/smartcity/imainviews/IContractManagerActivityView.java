package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
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
}
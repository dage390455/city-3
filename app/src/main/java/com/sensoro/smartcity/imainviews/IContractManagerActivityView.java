package com.sensoro.smartcity.imainviews;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.ContractListInfo;

import java.util.List;

public interface IContractManagerActivityView extends IProgressDialog, IToast, IActivityIntent {
    //    void updateContractListAdapter(List<DeviceAlarmLogInfo> deviceAlarmLogInfoList);
    void onPullRefreshComplete();

    PullToRefreshBase.State getPullRefreshState();

    void requestDataByDirection(int direction, boolean isFirst);

    void updateContractList(List<ContractListInfo> data);
}
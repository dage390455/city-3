package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IToast;

import java.util.List;

public interface IDeployMonitorWeChatRelationActivityView extends IToast, IActivityIntent {
    void setEditText(String text);

    void updateSearchHistoryData(List<String> searchStr);

    void updateTvTitle(String sn);

    void updateSaveStatus(boolean isEnable);

    void showHistoryClearDialog();
}

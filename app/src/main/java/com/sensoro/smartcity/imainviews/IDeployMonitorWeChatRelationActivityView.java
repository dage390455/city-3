package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IToast;

import java.util.List;

public interface IDeployMonitorWeChatRelationActivityView extends IToast, IActivityIntent {
    void setEditText(String text);

    void updateSearchHistoryData(List<String> searchStr);

    void updateTvTitle(String sn);
}

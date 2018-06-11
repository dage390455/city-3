package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

import java.util.List;

public interface ISearchAlarmActivityView extends IToast, IProgressDialog, IActivityIntent {
    void setSearchHistoryLayoutVisible(boolean isVisible);

    void setClearKeywordIvVisible(boolean isVisible);

    void setTipsLinearLayoutVisible(boolean isVisible);

    void updateSearchHistory(List<String> historyKeywords);

    void setText(String searchContent);
}

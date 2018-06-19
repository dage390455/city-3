package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

public interface ISearchMerchantActivityView extends IToast,IProgressDialog,IActivityIntent{
    void setSearchHistoryLayoutVisible(boolean isVisible);
    void updateSearchHistory();
    void setTipsLinearLayoutVisible(boolean isVisible);
    void setClearKeywordIvVisible(boolean isVisible);
    void setEditText(String text);
}

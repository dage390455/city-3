package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.UserInfo;

import java.util.List;

public interface ISearchMerchantActivityView extends IToast, IProgressDialog, IActivityIntent {
    void setSearchHistoryLayoutVisible(boolean isVisible);

    void updateSearchHistory();

    void setTipsLinearLayoutVisible(boolean isVisible);

    void setClearKeywordIvVisible(boolean isVisible);

    void setEditText(String text);

    void updateMerchantInfo(List<UserInfo> users);

    void setCurrentStatusImageViewVisible(boolean isVisible);

    void setLlMerchantItemViewVisible(boolean isVisible);

    void setCurrentNameAndPhone(String name, String phone);


}

package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.UserInfo;

import java.util.List;

public interface IMerchantSwitchActivityView extends IProgressDialog, IActivityIntent, IToast {
    void setCurrentNameAndPhone(String name, String phone);

    void updateAdapterUserInfo(List<UserInfo> data);

    void onPullRefreshComplete();

    void updateSearchHistoryList(List<String> data);

    void setTvBackToMainMerchantVisible(boolean isVisible);
}

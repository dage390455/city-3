package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.smartcity.server.bean.UserInfo;

import java.util.List;

public interface IMerchantSwitchActivityView extends IProgressDialog, IActivityIntent, IToast {
    void setCurrentNameAndPhone(String name, String phone);

    void updateAdapterUserInfo(List<UserInfo> data);

    void onPullRefreshComplete();

    void updateSearchHistoryList(List<String> data);

    void setTvBackToMainMerchantVisible(boolean isVisible);

    void showHistoryClearDialog();
}

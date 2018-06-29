package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

public interface IMainView extends IToast, IProgressDialog, IActivityIntent {
    void setAPPVersionCode(String versionStr);

    void setMenuSelected(int position);

    void showUpdateAppDialog(String log, final String url);

    void showAccountInfo(String name, String phone);

    void setCurrentPagerItem(int position);

    void freshAccountSwitch(int accountType);

    void changeAccount(String useName, String phone, String roles, String isSpecific);

    void updateMainPageAdapterData();
}

package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

import java.util.List;

public interface IMainView extends IToast, IProgressDialog, IActivityIntent {
    void setAPPVersionCode(String versionStr);

    void changeAccount(int accountType, int position);

    void showUpdateAppDialog(String log, final String url);

    void showAccountInfo(String name, String phone);

    void setCurrentItem(int position);

    void freshAccountSwitch(int accountType);

    void setMenuInfoAdapterData(List<String> data);

    void changeAccount(String useName, String phone, String roles, String isSpecific);

    void updateMainPageAdapterData();
}

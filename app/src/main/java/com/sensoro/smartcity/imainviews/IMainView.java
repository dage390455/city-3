package com.sensoro.smartcity.imainviews;

import android.support.v4.app.Fragment;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.model.MenuPageInfo;

import java.util.List;

public interface IMainView extends IToast, IProgressDialog, IActivityIntent {
    void setAPPVersionCode(String versionStr);

    void setMenuSelected(int position);

    void showUpdateAppDialog(String log, final String url);

    void showAccountInfo(String name, String phone);

    void setCurrentPagerItem(int position);

    void updateMenuPager(List<MenuPageInfo> menuPageInfos);

    void changeAccount(String useName, String phone, String roles, boolean isSpecific, boolean isStation, boolean
            hasContract, boolean hasScanLogin);

    void updateMainPageAdapterData(List<Fragment> fragments);

    void openMenu();

    void closeMenu();
}

package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

public interface ILoginView extends IToast, IProgressDialog, IActivityIntent {
    void showAccountName(String name);

    void showAccountPwd(String pwd);

    void setLogButtonState(int which);
}

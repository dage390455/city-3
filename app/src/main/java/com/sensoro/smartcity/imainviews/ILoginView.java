package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;

public interface ILoginView extends IToast, IProgressDialog, IActivityIntent {
    void showAccountName(String name);

    void showAccountPwd(String pwd);

    void setLogButtonState(int which);
}

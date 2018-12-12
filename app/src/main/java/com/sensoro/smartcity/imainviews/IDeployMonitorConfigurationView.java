package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

public interface IDeployMonitorConfigurationView extends IToast,IActivityIntent,IProgressDialog {
    void showBleConfigurationDialog();

    void dismissBleConfigurationDialog();

    void updateBtnRetryStatus();

    void updateBleConfigurationDialogText(String text);

    String getEditTextValue();
}

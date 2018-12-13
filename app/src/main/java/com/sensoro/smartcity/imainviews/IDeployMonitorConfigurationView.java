package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

public interface IDeployMonitorConfigurationView extends IToast,IActivityIntent,IProgressDialog {
    void showBleConfigurationDialog(String message);

    void dismissBleConfigurationDialog();

    void updateBtnStatus(boolean canConfig);

    void updateBleConfigurationDialogText(String text);

    String getEditTextValue();

    void updateBleConfigurationDialogSuccessImv();

    void setTvNearVisible(boolean isVisible);

    boolean hasEditTextContent();
}

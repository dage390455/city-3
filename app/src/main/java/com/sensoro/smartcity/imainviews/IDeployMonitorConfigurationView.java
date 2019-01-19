package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.adapter.model.EarlyWarningthresholdDialogUtilsAdapterModel;
import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

import java.util.ArrayList;

public interface IDeployMonitorConfigurationView extends IToast, IActivityIntent, IProgressDialog {
    void showBleConfigurationDialog(String message);

    void dismissBleConfigurationDialog();

    void updateBtnStatus(boolean canConfig);

    void updateBleConfigurationDialogText(String text);

    void updateBleConfigurationDialogSuccessImv();

    void setTvNearVisible(boolean isVisible);

    boolean hasEditTextContent();

    void setTvEnterValueRange(int minValue, int maxValue);

    void setLlAcDeployConfigurationDiameterVisible(boolean isVisible);

    void showOverCurrentDialog(ArrayList<EarlyWarningthresholdDialogUtilsAdapterModel> overCurrentDataList);
}

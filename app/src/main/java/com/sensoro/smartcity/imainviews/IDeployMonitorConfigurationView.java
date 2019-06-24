package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.adapter.model.EarlyWarningthresholdDialogUtilsAdapterModel;
import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;

import java.util.ArrayList;
import java.util.List;

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

    void setInputCurrentText(String text);

    void setInputDiameterValueText(String text);

    void setInputWireMaterialText(String text);

    void setActualCurrentText(String text);

    void setAcDeployConfigurationTvConfigurationText(String text);

    void showOperationTipLoadingDialog();

    void dismissOperatingLoadingDialog();

    void showErrorTipDialog(String errorMsg);

    void dismissTipDialog();

    void showOperationSuccessToast();

    void setTitleTvSubtitleVisible(boolean isVisible);
    void updatePvCustomOptions(List<String> list);
}


package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.adapter.model.EarlyWarningthresholdDialogUtilsAdapterModel;
import com.sensoro.smartcity.constant.DeployCheckStateEnum;
import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IToast;

import java.util.ArrayList;
import java.util.List;

public interface IDeployMonitorLocalCheckFragmentView extends IToast, IActivityIntent {
    void updatePvCustomOptions(List<String> list);

    void setDeviceSn(String sn);

    void setDeployDeviceType(String type);

    void setDeployDeviceConfigVisible(boolean isVisible);

    void setDeployPosition(boolean hasPosition);

    void setSwitchSpecHintText(String text);

    void setSwitchSpecContentText(String text);

    void setWireMaterialText(String text);

    void setWireDiameterText(String text);

    void setDeployCheckTvConfigurationText(String text);

    void updateBtnStatus(boolean canConfig);

    void showOverCurrentDialog(ArrayList<EarlyWarningthresholdDialogUtilsAdapterModel> overCurrentDataList);

    void setNotOwnVisible(boolean notOwn);

    void showDeployMonitorCheckDialogUtils(int state, boolean hasForce);

    void updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum deployCheckStateEnum, String tipText, boolean hasForce);

    void dismissDeployMonitorCheckDialogUtils();

    void showBleTips();

    void hideBleTips();

    void setDeployLocalCheckTipText(String text);
}

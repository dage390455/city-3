package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.model.DeployContactModel;

import java.util.List;

public interface IDeployMonitorDetailActivityView extends IToast, IProgressDialog, IActivityIntent {
    void updateUploadState(boolean isAvailable);

    void setDeviceSn(String sn);

    //
    void setNameAddressText(String text);

    void setDeployWeChatText(String text);

    void updateContactData(List<DeployContactModel> contacts);

    void updateTagsData(List<String> tagList);

    void refreshSignal(boolean hasStation, String signal, int resSignalId, String locationInfo);

    void setDeployDeviceRlSignalVisible(boolean isVisible);

    void setDeployContactRelativeLayoutVisible(boolean isVisible);

    void setDeployDeviceDetailFixedPointNearVisible(boolean isVisible);

    void setDeployPhotoVisible(boolean isVisible);

    void showUploadProgressDialog(String content, double percent);

    void dismissUploadProgressDialog();

    void showStartUploadProgressDialog();

    void setDeployPhotoText(String text);

    void showWarnDialog(boolean canForceUpload, String tipText, String instruction);

    void updateUploadTvText(String text);

    void showBleConfigDialog();

    void updateBleConfigDialogMessage(String msg);

    void dismissBleConfigDialog();

    void showBleTips();

    void hideBleTips();

    void setNotOwnVisible(boolean isVisible);

    void setDeployDetailArrowWeChatVisible(boolean isVisible);

    void setDeployDetailDeploySettingVisible(boolean isVisible);

    void setDeployDeviceType(String text);

    void setDeployDeviceDetailDeploySetting(String setting);

    void setUploadBtnStatus(boolean isEnable);
}

package com.sensoro.smartcity.imainviews;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;

import java.util.List;

public interface IDeployMonitorDetailActivityView extends IToast, IProgressDialog, IActivityIntent {
    void setDeviceSn(String sn);

    //
    void setNameAddressText(String text);

    void setDeployWeChatText(String text);

    void updateTagsData(List<String> tagList);

    void refreshSignal(boolean hasStation, String signal, @NonNull Drawable drawable, String locationInfo);

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

    void showRetryDialog();

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

    void setDeployLocalCheckTipText(String tipText);

    void setFirstContact(String contact);

    void setTotalContact(int total);
}

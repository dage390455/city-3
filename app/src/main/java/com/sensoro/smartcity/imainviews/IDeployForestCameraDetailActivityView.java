package com.sensoro.smartcity.imainviews;


import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;

import java.util.List;

public interface IDeployForestCameraDetailActivityView extends IToast, IProgressDialog, IActivityIntent {
    void updateUploadState(boolean isAvailable);

    void setDeviceSn(String sn);

    //
    void setNameAddressText(String text);

    void updateTagsData(List<String> tagList);

    void setDeployPhotoVisible(boolean isVisible);

    void showUploadProgressDialog(String content, double percent);

    void dismissUploadProgressDialog();

    void showStartUploadProgressDialog();

    void setDeployPhotoText(String text);

    void updateUploadTvText(String text);

    void showBleConfigDialog();

    void updateBleConfigDialogMessage(String msg);

    void dismissBleConfigDialog();

    void setDeployPosition(boolean hasPosition, String text);

    void setNotOwnVisible(boolean isVisible);

    void setDeployDeviceType(String text);

    void setUploadBtnStatus(boolean isEnable);

    void setDeployInstallationLocation(String location);

    void showRetryDialog();

}

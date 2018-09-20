package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.model.DeployContactModel;

import java.util.List;

public interface IDeployDeviceDetailActivityView extends IToast, IProgressDialog, IActivityIntent {
    void updateUploadState(boolean isAvailable);

    void setDeviceTitleName(String name);

    //
    void setNameAddressText(String text);

    void updateContactData(List<DeployContactModel> contacts);

    void updateTagsData(List<String> tagList);

    void refreshSignal(boolean hasStation,String signal, int resSignalId, String locationInfo);

    void setDeployDeviceRlSignalVisible(boolean isVisible);

    void setDeployContactRelativeLayoutVisible(boolean isVisible);

    void setDeployPhotoVisible(boolean isVisible);

    void showUploadProgressDialog(int currentNum, int count, double percent);

    void dismissUploadProgressDialog();

    void showStartUploadProgressDialog();

    void setDeployPhotoText(String text);

    void showWarnDialog();

    void updateUploadTvText(String text);
}

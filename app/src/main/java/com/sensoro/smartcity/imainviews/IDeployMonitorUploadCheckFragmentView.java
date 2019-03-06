package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.model.DeployContactModel;

import java.util.List;

public interface IDeployMonitorUploadCheckFragmentView extends IActivityIntent ,IToast ,IProgressDialog {
    void setAlarmContactAndPicAndMiniProgramVisible(boolean isVisible);

    void setDeployPhotoVisible(boolean isVisible);

    void setNameAddressText(String nameAndAddress);

    void setDeployDetailArrowWeChatVisible(boolean isVisible);

    void setDeviceSn(String sn);

    void updateUploadTvText(String text);

    void setDeployDeviceType(String type);

    void updateContactData(List<DeployContactModel> list);

    void updateTagsData(List<String> tagList);

    void setUploadBtnStatus(boolean isEnable);

    void setDeployWeChatText(String weChatAccount);

    void setDeployPhotoText(String text);

    void showStartUploadProgressDialog();

    void dismissUploadProgressDialog();

    void showUploadProgressDialog(String content, double percent);
}

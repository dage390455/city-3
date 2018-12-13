package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.DeployRecordInfo;

import java.util.List;

public interface IDeployRecordDetailActivityView extends IToast, IActivityIntent, IProgressDialog {
    void setSNTitle(String sn);

    void setDeviceName(String deviceName);

    void updateTagList(List<String> tags);

    void setDeployTime(String time);

    void seDeployWeChat(String text);

    void setPicCount(String content);

    void updateContactList(List<DeployRecordInfo.NotificationBean> notifications);

    void setPositionStatus(int status);

    void refreshSingle(String signalQuality);

    void setDeployDeviceRecordDeviceType(String text);

    void setDeployDetailDeploySettingVisible(boolean isVisible);

    void setDeployDeviceDetailDeploySetting(boolean setting);
}

package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.bean.ScenesData;

import java.util.ArrayList;
import java.util.List;

public interface IOfflineDeployTaskDetailActivityView extends IToast, IActivityIntent, IProgressDialog {
    void setSNTitle(String sn);

    void setDeviceName(String deviceName);

    void updateTagList(List<String> tags);

    void setDeployTime(String time);

    void seDeployWeChat(String text);

    void updateDeployPic(ArrayList<ScenesData> data);

    void setPositionStatus(int status);

    void refreshSingle(String signalQuality);

    void setDeployDeviceRecordDeviceType(String text);

    void setDeployDetailDeploySettingVisible(boolean isVisible);

    void setForceDeployReason(String reason);

    void setDeployRecordDetailDeployStaff(String text);

    void setDeployDetailConfigInfo(String actual, String trans);

    void setFirstContact(String contact);

    void setTotalContact(int total);
}

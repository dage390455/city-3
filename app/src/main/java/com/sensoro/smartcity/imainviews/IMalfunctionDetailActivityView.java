package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.bean.MalfunctionListInfo;

import java.util.List;

public interface IMalfunctionDetailActivityView extends IToast, IActivityIntent, IProgressDialog {
    void setDeviceNameText(String deviceName);

    void setMalfunctionStatus(int malfunctionStatus, String strTimeToday);

    void updateRcContent(List<MalfunctionListInfo.RecordsBean> records, String malfunctionText);

    void setMalfunctionCount(String count);

    void setMalfunctionDetailConfirmVisible(boolean isVisible);

    void setDeviceSn(String deviceSN);
}

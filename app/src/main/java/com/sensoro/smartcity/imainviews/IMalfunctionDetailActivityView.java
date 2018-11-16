package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.MalfunctionListInfo;

import java.util.List;

public interface IMalfunctionDetailActivityView extends IToast,IActivityIntent,IProgressDialog {
    void setDeviceNameText(String deviceName);

    void setMalfunctionStatus(int malfunctionStatus, String strTimeToday);

    void updateRcContent(List<MalfunctionListInfo.RecordsBean> records);

    void setMalfunctionCount(String count);
}

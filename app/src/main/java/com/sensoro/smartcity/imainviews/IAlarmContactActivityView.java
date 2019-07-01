package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.model.DeployContactModel;

import java.util.ArrayList;

public interface IAlarmContactActivityView extends IToast, IActivityIntent {
//    void setNameAndPhone(String name, String phone);

    void updateHistoryData(ArrayList<String> mHistoryKeywords);

    void updateSaveStatus(boolean isEnable);

    void showHistoryClearDialog();

//    void setName(String name);
//
//    void setPhone(String phone);

    void updateContactData(ArrayList<DeployContactModel> mdContactModelList);

}

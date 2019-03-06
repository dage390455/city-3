package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IToast;

import java.util.ArrayList;

public interface IAlarmContactActivityView extends IToast, IActivityIntent {
    void setNameAndPhone(String name, String phone);

    void updateHistoryData(ArrayList<String> mHistoryKeywords);

    void updateSaveStatus(boolean isEnable);
}

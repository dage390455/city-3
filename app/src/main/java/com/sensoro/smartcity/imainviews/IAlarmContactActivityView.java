package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IToast;

public interface IAlarmContactActivityView extends IToast, IActivityIntent {
    void setNameAndPhone(String name, String phone);
}

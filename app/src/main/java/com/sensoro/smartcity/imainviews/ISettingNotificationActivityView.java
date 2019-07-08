package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;

public interface ISettingNotificationActivityView extends IActivityIntent {
    void setDeviceInChecked(boolean isChecked);

    void setDeviceOutChecked(boolean isChecked);

    void setDeviceInEditContent(String text);

    void setDeviceOutEditContent(String text);
}

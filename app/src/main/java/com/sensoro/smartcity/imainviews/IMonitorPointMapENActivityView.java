package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IToast;

public interface IMonitorPointMapENActivityView extends IToast, IActivityIntent {
    void setPositionCalibrationVisible(boolean isVisible);
}

package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IToast;

public interface IMonitorPointMapActivityView extends IToast, IActivityIntent {
    void setPositionCalibrationVisible(boolean isVisible);
}

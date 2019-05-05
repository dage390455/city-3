package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IToast;

public interface IMonitorPointMapActivityView extends IToast, IActivityIntent {
    void setPositionCalibrationVisible(boolean isVisible);
}

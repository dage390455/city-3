package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IToast;

public interface IMonitorPointMapENActivityView extends IToast, IActivityIntent {
    void setPositionCalibrationVisible(boolean isVisible);
}

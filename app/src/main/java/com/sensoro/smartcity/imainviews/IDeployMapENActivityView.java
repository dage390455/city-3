package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;

public interface IDeployMapENActivityView extends IActivityIntent, IToast, IProgressDialog {
    void refreshSignal(long updateTime, String signal);

    void setSignalVisible(boolean isVisible);

    void setSaveVisible(boolean isVisible);

    void refreshSignal(String signal);

    void setSubtitleVisible(boolean isVisible);
}

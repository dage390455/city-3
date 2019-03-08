package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

public interface IDeployMapENActivityView extends IActivityIntent, IToast, IProgressDialog {
    void refreshSignal(long updateTime, String signal);

    void setSignalVisible(boolean isVisible);

    void setSaveVisible(boolean isVisible);

    void refreshSignal(String signal);

    void setSubtitleVisible(boolean isVisible);
}

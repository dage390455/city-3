package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

public interface IDeployMapActivityView extends IActivityIntent, IToast, IProgressDialog {
    void refreshSignal(long updateTime, String signal);

    void setSignalVisible(boolean isVisble);
}

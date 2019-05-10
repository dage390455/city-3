package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

public interface IAlarmCameraLiveDetailActivityView extends IToast , IProgressDialog {
    void doPlayLive(final String url, String cameraName, final boolean isLive);
}

package com.sensoro.smartcity.imainviews;

import com.google.zxing.client.android.camera.CameraManager;
import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

public interface IPointDeployFragmentView extends IToast,IActivityIntent,IProgressDialog{
    void setCameraManager(CameraManager cameraManager);
    void resetStatusView();
    void setCameraCapture();
    void setStatusInfo(String info);
    boolean isNotVisibleOrResumed();
    void setFlashLightState(boolean isOn);
}

package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.shuyu.gsyvideoplayer.video.CityStandardGSYVideoPlayer;

public interface IMutilCameraView extends IProgressDialog, IToast, IActivityIntent {
    CityStandardGSYVideoPlayer getPlayView();

    void backFromWindowFull();


}

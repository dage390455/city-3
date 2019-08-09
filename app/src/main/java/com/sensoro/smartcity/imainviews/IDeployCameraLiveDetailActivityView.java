package com.sensoro.smartcity.imainviews;

import android.widget.ImageView;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.shuyu.gsyvideoplayer.video.CityStandardGSYVideoPlayer;

import java.util.ArrayList;


public interface IDeployCameraLiveDetailActivityView extends IActivityIntent, IToast, IProgressDialog {

    void playError(String errorMsg);

    void setTitle(String time);

    void doPlayLive(ArrayList<String> urlList, String cameraName);

    ImageView getImageView();

    CityStandardGSYVideoPlayer getPlayView();

    void setVerOrientationUtilEnable(boolean enable);


    void backFromWindowFull();
}

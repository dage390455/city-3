package com.sensoro.smartcity.imainviews;

import android.graphics.drawable.Drawable;

import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.bean.AlarmCameraLiveBean;
import com.shuyu.gsyvideoplayer.video.CityStandardGSYVideoPlayer;

import java.util.ArrayList;

public interface IAlarmCameraLiveDetailActivityView extends IToast, IProgressDialog {
    void doPlayLive(final String url);

    void offlineType(String url, String sn);

    void updateData(ArrayList<AlarmCameraLiveBean> mList);

    void onPullRefreshComplete();

    void setImage(Drawable bitmapDrawable);

    void backFromWindowFull();


    void setVerOrientationUtilEnable(boolean enable);


    CityStandardGSYVideoPlayer getPlayView();

    void onVideoPause();

    void onVideoResume();
}

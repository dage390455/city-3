package com.sensoro.forestfire.imainviews;

import android.graphics.drawable.Drawable;

import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.bean.AlarmCameraLiveBean;
import com.sensoro.common.server.bean.ForestFireCameraDetailInfo;
import com.shuyu.gsyvideoplayer.video.CityStandardGSYVideoPlayer;

import java.util.ArrayList;

public interface IAlarmForestFireCameraLiveDetailActivityView extends IToast, IProgressDialog {
    void doPlayLive(ArrayList<String> urlList);

    void offlineType(String url, String sn);

    void updateData(ArrayList<ForestFireCameraDetailInfo.ListBean> mList);

    void onPullRefreshComplete();

    void setImage(Drawable bitmapDrawable);

    void backFromWindowFull();


    void setVerOrientationUtilEnable(boolean enable);


    CityStandardGSYVideoPlayer getPlayView();

    void onVideoPause();

    void onVideoResume();
}

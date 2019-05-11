package com.sensoro.smartcity.imainviews;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.response.AlarmCameraLiveRsp;

import java.util.ArrayList;

public interface IAlarmCameraLiveDetailActivityView extends IToast , IProgressDialog {
    void doPlayLive(final String url);

    void offlineType(String url, String sn);

    void updateData(ArrayList<AlarmCameraLiveRsp.DataBean> mList);

    void onPullRefreshComplete();

    void setImage(Drawable bitmapDrawable);
}

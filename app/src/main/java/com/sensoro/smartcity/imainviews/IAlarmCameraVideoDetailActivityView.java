package com.sensoro.smartcity.imainviews;

import android.graphics.drawable.Drawable;

import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.smartcity.server.response.AlarmCloudVideoRsp;


import java.util.ArrayList;

public interface IAlarmCameraVideoDetailActivityView extends IToast, IProgressDialog {
    void doPlayLive(final String url);

    void updateData(ArrayList<AlarmCloudVideoRsp.DataBean.MediasBean> mList);

    void onPullRefreshComplete();

    void setImage(Drawable bitmapDrawable);

    void setDownloadStartState(String videoSize);

    void updateDownLoadProgress(int progress, String totalBytesRead, String fileSize);

    void doDownloadFinish();

    void setDownloadErrorState();

    void setPlayVideoTime(String s);
}

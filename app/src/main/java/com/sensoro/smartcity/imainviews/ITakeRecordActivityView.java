package com.sensoro.smartcity.imainviews;

import android.content.Intent;
import android.view.SurfaceHolder;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.yixia.camera.MediaRecorderNative;

public interface ITakeRecordActivityView extends IToast, IProgressDialog, IActivityIntent {
    void showProgressDialog(int progress);

    void dismissProgress();

    void previewVideo(String videoPath);

    SurfaceHolder getFfmpegHolder();

    void startAnim();

    void pausePlayVideo();

    void setFinishResult(int resultCodeRecord, Intent intent);

    void recordFinish();

    void updateStatusProgress(int duration);

    void setFfmpegTouchFocus(MediaRecorderNative mMediaRecorder);

    void rbStartCloseButton();

    void setPlayLooping(boolean isLooping);

    void startPlay();

    void setStartRecordStatus();

    void setRetakeStatus();
}

package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.smartcity.imainviews.ITakeRecordActivityView;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.RecordedButton;
import com.sensoro.common.model.ImageItem;
import com.yixia.camera.MediaRecorderBase;
import com.yixia.camera.MediaRecorderNative;
import com.yixia.camera.VCamera;
import com.yixia.camera.model.MediaObject;
import com.yixia.videoeditor.adapter.UtilityAdapter;

import java.io.Serializable;
import java.util.LinkedList;

import static com.sensoro.smartcity.constant.Constants.RESULT_CODE_RECORD;

public class TakeRecordActivityPresenter extends BasePresenter<ITakeRecordActivityView> implements
        MediaRecorderBase.OnEncodeListener, RecordedButton.OnGestureListener, MediaPlayer.OnPreparedListener{
    public boolean isEncodingFinish = true;
    private MediaRecorderNative mMediaRecorder;
    private MediaObject mMediaObject;
    private boolean recordedOver = true;
    private Activity mActivity;
    private final Handler myHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (!recordedOver) {
                int duration = mMediaObject.getDuration();
                if (duration > 15 * 1000) {
                    recordFinish();
                    return;
                }
                getView().updateStatusProgress(duration);

                myHandler.sendEmptyMessageDelayed(0, 50);


            }
        }
    };
    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        initMediaRecorder();
        getView().setFfmpegTouchFocus(mMediaRecorder);
    }

    /**
     * 初始化录制对象
     */
    private void initMediaRecorder() {

        mMediaRecorder = new MediaRecorderNative();
        mMediaRecorder.setOnEncodeListener(this);
        String key = String.valueOf(System.currentTimeMillis());
        //设置缓存文件夹
        mMediaObject = mMediaRecorder.setOutputDirectory(key, VCamera.getVideoCachePath());
        //设置视频预览源
        mMediaRecorder.setSurfaceHolder(getView().getFfmpegHolder());
        //准备
        mMediaRecorder.prepare();
        //滤波器相关
        UtilityAdapter.freeFilterParser();
        UtilityAdapter.initFilterParser();
    }

    @Override
    public void onDestroy() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stopRecord();
            mMediaRecorder.release();
        }
        myHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onEncodeStart() {
        try {
            LogUtils.logd("Log.i", "onEncodeStart");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void onEncodeProgress(int progress) {
//        if (textView != null) {
//            textView.setText("视频编译中 " + progress + "%");
//        }

        if (isAttachedView()) {
            getView().showProgressDialog(progress);
        }

    }

    /**
     * 视频编辑完成
     */
    @Override
    public void onEncodeComplete() {
        //TODO
        if (isAttachedView()) {
            getView().dismissProgress();
        }

        final String videoPath = mMediaObject.getOutputTempVideoPath();
        if (!TextUtils.isEmpty(videoPath)) {
            if (isAttachedView()) {
                getView().previewVideo(videoPath);
                recordedOver = true;
                getView().startAnim();
            }

        } else {
            if (isAttachedView()) {
                getView().toastShort(mActivity.getString(R.string.video_recording_failed_please_try_again));
                getView().finishAc();
            }
        }
    }


    @Override
    public void onEncodeError() {
        try {
            LogUtils.logd("Log.i", "onEncodeError");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        if (isAttachedView()) {
            getView().toastShort(mActivity.getString(R.string.video_recording_failed_please_try_again));
            getView().finishAc();
        }
    }

    @Override
    public void onLongClick() {
//        mMediaRecorder.startRecord();
//        myHandler.sendEmptyMessageDelayed(0, 50);
    }

    @Override
    public void onClick() {
        //点击按钮
    }

    @Override
    public void onLift() {
        recordedOver = true;
        videoFinish();
    }

    @Override
    public void onOver() {
        recordedOver = true;
        getView().rbStartCloseButton();
        videoFinish();
    }

    private void recordStart() {
        mMediaRecorder.startRecord();
        myHandler.sendEmptyMessageDelayed(0, 50);
        getView().setStartRecordStatus();
        recordedOver = false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (isAttachedView()) {
            getView().setPlayLooping(true);
        }
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAttachedView()) {
                    getView().startPlay();
                }
            }
        }, 50);
    }

    private void recordFinish() {
        recordedOver = true;
        getView().recordFinish();
        videoFinish();
    }

    private void videoFinish() {
        getView().dismissProgress();
        mMediaRecorder.stopRecord();
        //开始合成视频, 异步
        mMediaRecorder.startEncoding();
        isEncodingFinish = false;
    }

    public void doFinish() {
        if (isEncodingFinish) {
            final String videoPath = mMediaObject.getOutputTempVideoPath();
            final String videoThumbPath = WidgetUtil.bitmap2File(WidgetUtil.getVideoThumbnail(videoPath), videoPath);
            final long endTime = mMediaObject.getCurrentPart().endTime;
            try {
                LogUtils.loge("videoThumbPath = " + videoThumbPath);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            //                    initMediaRecorderState();

            getView().pausePlayVideo();

            Intent intent = new Intent();
            final ImageItem imageItem = new ImageItem();
            imageItem.isRecord = true;
            imageItem.addTime = endTime;
            imageItem.path = videoPath;
            imageItem.thumbPath = videoThumbPath;
            imageItem.name = videoPath.substring(videoPath.lastIndexOf("/") + 1);
            intent.putExtra("path_record", (Serializable) imageItem);
            getView().setFinishResult(RESULT_CODE_RECORD, intent);
            getView().finishAc();
        }
    }

    public void setEncodingStatus(boolean isEncoding) {
        isEncodingFinish = isEncoding;
    }

    public void doMediaRecorderStartPreview() {
        mMediaRecorder.startPreview();
    }

    public void doMediaRecorderStopPreview() {
        mMediaRecorder.stopPreview();
    }

    public void reVideo() {
        LinkedList<MediaObject.MediaPart> medaParts = mMediaObject.getMedaParts();
        for (MediaObject.MediaPart part : medaParts) {
            mMediaObject.removePart(part, true);
        }
        mMediaRecorder.startPreview();
    }

    public void clearMediaObject() {
        LinkedList<MediaObject.MediaPart> medaParts = mMediaObject.getMedaParts();
        for (MediaObject.MediaPart part : medaParts) {
            mMediaObject.removePart(part, true);
        }
    }

    public void doRbStart() {
        if (isEncodingFinish) {
            if (recordedOver) {
                recordStart();
            } else {
                if (mMediaObject.getDuration() > 5000) {
                    recordFinish();
                }

            }
        }
    }

    public void doRetake() {
        mMediaRecorder.stopRecord();
//                initMediaRecorderState();
        myHandler.removeCallbacksAndMessages(null);
        recordedOver = true;

        getView().setRetakeStatus();
        LinkedList<MediaObject.MediaPart> medaParts = mMediaObject.getMedaParts();
        for (MediaObject.MediaPart part : medaParts) {
            mMediaObject.removePart(part, true);
        }
        mMediaRecorder.startPreview();
    }
}

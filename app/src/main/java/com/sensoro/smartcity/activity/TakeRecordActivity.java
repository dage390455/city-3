package com.sensoro.smartcity.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.FocusSurfaceView;
import com.sensoro.smartcity.widget.MyVideoView;
import com.sensoro.smartcity.widget.RecordedButton;
import com.sensoro.smartcity.widget.imagepicker.bean.ImageItem;
import com.sensoro.smartcity.widget.toast.SensoroToast;
import com.yixia.camera.MediaRecorderBase;
import com.yixia.camera.MediaRecorderNative;
import com.yixia.camera.VCamera;
import com.yixia.camera.model.MediaObject;
import com.yixia.videoeditor.adapter.UtilityAdapter;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;

import static com.sensoro.smartcity.constant.Constants.RESULT_CODE_RECORD;

public class TakeRecordActivity extends Activity implements MediaRecorderBase.OnEncodeListener, RecordedButton.OnGestureListener, View.OnClickListener, MediaPlayer.OnPreparedListener {

    private static final int REQUEST_KEY = 100;
    private MediaRecorderNative mMediaRecorder;
    private MediaObject mMediaObject;
    private FocusSurfaceView sv_ffmpeg;
    private RecordedButton rb_start;
    private int maxDuration = 15 * 1000;
    private boolean recordedOver;
    private MyVideoView vv_play;
    private ImageView iv_finish;
    private ImageView iv_back;
    private float dp100;
    private TextView tv_hint;
    private float backX = -1;
    private ProgressDialog progressDialog;
    private float lastValue;
    private final Handler myHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (!recordedOver) {
                rb_start.setProgress(mMediaObject.getDuration());
                myHandler.sendEmptyMessageDelayed(0, 50);
                tv_hint.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_take_record);
        //
        sv_ffmpeg = (FocusSurfaceView) findViewById(R.id.sv_ffmpeg);
        rb_start = (RecordedButton) findViewById(R.id.rb_start);
        vv_play = (MyVideoView) findViewById(R.id.vv_play);
        iv_finish = (ImageView) findViewById(R.id.iv_finish);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_hint = (TextView) findViewById(R.id.tv_hint);

        dp100 = getResources().getDimension(R.dimen.dp100);

        initMediaRecorder();
        initProgressDialog();

        sv_ffmpeg.setTouchFocus(mMediaRecorder);

        rb_start.setMax(maxDuration);
        rb_start.setOnGestureListener(this);

        iv_back.setOnClickListener(this);
        iv_finish.setOnClickListener(this);
    }

    public void initProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
//        progressDialog.setProgressNumberFormat("");
        progressDialog.setCancelable(false);
    }

    /**
     * 初始化视频拍摄状态
     */
    private void initMediaRecorderState() {
        vv_play.setVisibility(View.GONE);
        vv_play.pause();

        iv_back.setX(backX);
        iv_finish.setX(backX);

        tv_hint.setVisibility(View.VISIBLE);
        rb_start.setVisibility(View.VISIBLE);
        lastValue = 0;
    }

    private void videoFinish() {
        dismissProgress();
        mMediaRecorder.stopRecord();
        if (mMediaObject.getDuration() <10000) {
//            initMediaRecorderState();
            LinkedList<MediaObject.MediaPart> medaParts = mMediaObject.getMedaParts();
            for (MediaObject.MediaPart part : medaParts) {
                mMediaObject.removePart(part, true);
            }
            mMediaRecorder.startPreview();
            recordedOver = false;
            SensoroToast.INSTANCE.makeText(getString(R.string.record_more_than_ten_seconds),Toast.LENGTH_SHORT).show();
        }else{
            //开始合成视频, 异步
            mMediaRecorder.startEncoding();
        }

    }

    private void startAnim() {

        rb_start.setVisibility(View.GONE);
        ValueAnimator va = ValueAnimator.ofFloat(0, dp100).setDuration(300);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float value = (float) animation.getAnimatedValue();
                float dis = value - lastValue;
                iv_back.setX(iv_back.getX() - dis);
                iv_finish.setX(iv_finish.getX() + dis);
                lastValue = value;
            }
        });
        va.start();
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
        mMediaRecorder.setSurfaceHolder(sv_ffmpeg.getHolder());
        //准备
        mMediaRecorder.prepare();
        //滤波器相关
        UtilityAdapter.freeFilterParser();
        UtilityAdapter.initFilterParser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaRecorder.startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaRecorder.stopPreview();
    }

    @Override
    public void onBackPressed() {
        if (rb_start.getVisibility() != View.VISIBLE) {
            initMediaRecorderState();
            LinkedList<MediaObject.MediaPart> medaParts = mMediaObject.getMedaParts();
            for (MediaObject.MediaPart part : medaParts) {
                mMediaObject.removePart(part, true);
            }
            mMediaRecorder.startPreview();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        if (backX == -1) {
            backX = iv_back.getX();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CODE_RECORD) {
            LinkedList<MediaObject.MediaPart> medaParts = mMediaObject.getMedaParts();
            for (MediaObject.MediaPart part : medaParts) {
                mMediaObject.removePart(part, true);
            }
//            deleteDir(SensoroCityApplication.VIDEO_PATH);
        }
    }

    /**
     * 删除文件夹下所有文件,暂时去掉此类敏感操作，部分rom会拒接导致终止
     */
    public void deleteDir(String dirPath) {

//        File dir = new File(dirPath);
////        if (dir.exists() && dir.isDirectory()) {
////            File[] files = dir.listFiles();
////            for (File f : files) {
////                deleteDir(f.getAbsolutePath());
////            }
////        } else if (dir.exists()) {
////            if (!dir.getAbsolutePath().endsWith(".jpg") && !dir.getAbsolutePath().endsWith("mp4")) {
////                dir.delete();
////            }
////        }
        File dir = new File(dirPath);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File f : files) {
                deleteDir(f.getAbsolutePath());
            }
        } else if (dir.exists()) {
            if (dir.getAbsolutePath().endsWith(".jpg") || dir.getAbsolutePath().endsWith("mp4")) {
                try {
                    LogUtils.loge("视频图片缓存文件路径--->> " + dir.getAbsolutePath());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            } else {
                dir.delete();
            }
        }
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
        showProgressDialog(progress);
    }

    private void showProgressDialog(int progress) {
        if (progressDialog != null) {
            String title = getString(R.string.video_compilation);
            progressDialog.setTitle(title);
            progressDialog.setProgress(progress);
            progressDialog.show();
        }
    }

    private void dismissProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 视频编辑完成
     */
    @Override
    public void onEncodeComplete() {
        //TODO
        dismissProgress();
        final String videoPath = mMediaObject.getOutputTempVideoPath();
        if (!TextUtils.isEmpty(videoPath)) {
            vv_play.setVisibility(View.VISIBLE);
            vv_play.setVideoPath(videoPath);
            vv_play.setOnPreparedListener(this);
            vv_play.start();

            recordedOver = false;
            startAnim();
        } else {
            SensoroToast.INSTANCE.makeText(getString(R.string.video_recording_failed_please_try_again), Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    @Override
    public void onEncodeError() {
        try {
            LogUtils.logd("Log.i", "onEncodeError");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null) {
            progressDialog.cancel();
        }
        if (rb_start != null) {
            rb_start.onDestroy();
        }
        if (vv_play != null) {
            vv_play.release();
        }
        myHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onLongClick() {
        mMediaRecorder.startRecord();
        myHandler.sendEmptyMessageDelayed(0, 50);
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
        rb_start.closeButton();
        videoFinish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_finish:
                final String videoPath = mMediaObject.getOutputTempVideoPath();
                final String videoThumbPath = WidgetUtil.bitmap2File(WidgetUtil.getVideoThumbnail(videoPath), videoPath);
                final long endTime = mMediaObject.getCurrentPart().endTime;
                try {
                    LogUtils.loge("videoThumbPath = " + videoThumbPath);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                //                    initMediaRecorderState();
                if (vv_play != null) {
                    vv_play.pause();
                }
                Intent intent = new Intent();
                final ImageItem imageItem = new ImageItem();
                imageItem.isRecord = true;
                imageItem.addTime = endTime;
                imageItem.path = videoPath;
                imageItem.thumbPath = videoThumbPath;
                imageItem.name = videoPath.substring(videoPath.lastIndexOf("/") + 1);
                intent.putExtra("path_record", (Serializable) imageItem);
                setResult(RESULT_CODE_RECORD, intent);
                finish();
                break;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        vv_play.setLooping(true);
        vv_play.start();
    }

}

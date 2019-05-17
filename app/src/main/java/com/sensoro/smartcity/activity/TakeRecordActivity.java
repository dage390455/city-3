package com.sensoro.smartcity.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.sensoro.smartcity.R;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.smartcity.imainviews.ITakeRecordActivityView;
import com.sensoro.smartcity.presenter.TakeRecordActivityPresenter;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.FocusSurfaceView;
import com.sensoro.smartcity.widget.MyVideoView;
import com.sensoro.smartcity.widget.RecordedButton;
import com.sensoro.common.widgets.SensoroToast;
import com.yixia.camera.MediaRecorderNative;

import java.io.File;
import java.util.Locale;

import static com.sensoro.smartcity.constant.Constants.RESULT_CODE_RECORD;

public class TakeRecordActivity extends BaseActivity<ITakeRecordActivityView, TakeRecordActivityPresenter> implements ITakeRecordActivityView, View.OnClickListener {

    private static final int REQUEST_KEY = 100;
    private FocusSurfaceView sv_ffmpeg;
    private RecordedButton rb_start;
    private int maxDuration = 15 * 1000;
    private MyVideoView vv_play;
    private ImageView iv_finish;
    private ImageView iv_back;
    private float dp100;
    private TextView tv_hint;
    private float backX = -1;
    private ProgressDialog progressDialog;
    private float lastValue;
    private TextView tv_record_time;
    private ImageView imv_back;
    private TextView tv_retake;
    private Point displayPoint;
    private ValueAnimator va;
    public ImmersionBar immersionBar;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_take_record);
        //
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        sv_ffmpeg =  findViewById(R.id.sv_ffmpeg);
        rb_start =  findViewById(R.id.rb_start);
        vv_play =  findViewById(R.id.vv_play);
        iv_finish =  findViewById(R.id.iv_finish);
        iv_back =  findViewById(R.id.iv_back);
        tv_hint =  findViewById(R.id.tv_hint);
        tv_record_time =  findViewById(R.id.tv_record_time);
        imv_back = findViewById(R.id.imv_back);
        tv_retake = findViewById(R.id.tv_retake);

        displayPoint = new Point();
        getWindowManager().getDefaultDisplay().getSize(displayPoint);
        // 返回 确认按钮设置为距离两个边为58dp,view大小是70dp，所以应该减掉是58+35
        dp100 = displayPoint.x / 2 - AppUtils.dp2px(this, 93);

        initProgressDialog();

        rb_start.setMax(maxDuration);
        rb_start.setOnGestureListener(mPresenter);

        rb_start.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        iv_finish.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tv_retake.setOnClickListener(this);
        imv_back.setOnClickListener(this);
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

        iv_back.setX(iv_back.getX() + dp100);
        iv_finish.setX(iv_finish.getX() - dp100);

        rb_start.setVisibility(View.VISIBLE);
        iv_back.setVisibility(View.GONE);
        iv_finish.setVisibility(View.GONE);
        lastValue = 0;
    }



    @Override
    public void startAnim() {
        rb_start.setVisibility(View.GONE);
        iv_finish.setVisibility(View.VISIBLE);
        iv_back.setVisibility(View.VISIBLE);
        va = ValueAnimator.ofFloat(0, dp100).setDuration(300);
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
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mPresenter.setEncodingStatus(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        va.start();
    }

    @Override
    public void pausePlayVideo() {
        if (vv_play != null) {
            vv_play.pause();
        }
    }

    @Override
    public void setFinishResult(int resultCodeRecord, Intent intent) {
        mActivity.setResult(resultCodeRecord,intent);
    }

    @Override
    public void recordFinish() {
        rb_start.closeButton();
        rb_start.setIsRecording(false);
        tv_record_time.setVisibility(View.GONE);
        tv_retake.setVisibility(View.GONE);
    }

    @Override
    public void updateStatusProgress(int duration) {
        rb_start.setProgress(duration);
        tv_hint.setVisibility(duration > 5000 ? View.GONE : View.VISIBLE);
        tv_record_time.setText(String.format(Locale.ROOT, "00:%02d", duration / 1000));
    }

    @Override
    public void setFfmpegTouchFocus(MediaRecorderNative mMediaRecorder) {
        sv_ffmpeg.setTouchFocus(mMediaRecorder);
    }

    @Override
    public void rbStartCloseButton() {
        rb_start.closeButton();
    }

    @Override
    public void setPlayLooping(boolean isLooping) {
        vv_play.setLooping(isLooping);
    }

    @Override
    public void startPlay() {
        vv_play.start();
    }

    @Override
    public void setStartRecordStatus() {
        rb_start.setIsRecording(true);
        tv_record_time.setVisibility(View.VISIBLE);
        tv_retake.setVisibility(View.VISIBLE);
    }

    @Override
    public void setRetakeStatus() {
        rb_start.retake();
        tv_retake.setVisibility(View.GONE);
        tv_record_time.setVisibility(View.GONE);
        tv_hint.setVisibility(View.GONE);
    }

    @Override
    public boolean isActivityOverrideStatusBar() {
        immersionBar = ImmersionBar.with(mActivity);
        immersionBar.transparentStatusBar().init();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.doMediaRecorderStartPreview();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.doMediaRecorderStopPreview();

    }

    @Override
    protected TakeRecordActivityPresenter createPresenter() {
        return new TakeRecordActivityPresenter();
    }

    @Override
    public void onBackPressed() {
        if (rb_start.getVisibility() != View.VISIBLE) {
            initMediaRecorderState();
            mPresenter.reVideo();
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
            mPresenter.clearMediaObject();
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
    public void showProgressDialog(int progress) {
        if (progressDialog != null) {
            String title = getString(R.string.video_compilation);
            progressDialog.setTitle(title);
            progressDialog.setProgress(progress);
            progressDialog.show();
        }
    }

    @Override
    public void dismissProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void previewVideo(String videoPath) {
        vv_play.setVisibility(View.VISIBLE);
        vv_play.setVideoPath(videoPath);
        vv_play.setOnPreparedListener(mPresenter);
        vv_play.start();
    }

    @Override
    public SurfaceHolder getFfmpegHolder() {
        return sv_ffmpeg.getHolder();
    }


    @Override
    protected void onDestroy() {
        if (progressDialog != null) {
            progressDialog.cancel();
        }
        if (va != null) {
            va.cancel();
        }
        if (rb_start != null) {
            rb_start.onDestroy();
        }
        if (vv_play != null) {
            vv_play.release();
        }

        if (immersionBar != null) {
            immersionBar.destroy();
        }
        mPresenter.onDestroy();
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                if (mPresenter.isEncodingFinish) {
                    onBackPressed();
                }

                break;
            case R.id.iv_finish:
                mPresenter.doFinish();
                break;

            case R.id.rb_start:
                mPresenter.doRbStart();

                break;
            case R.id.imv_back:
                finishAc();
                break;
            case R.id.tv_retake:
                mPresenter.doRetake();
                break;
        }
    }


    @Override
    public void startAC(Intent intent) {
        mActivity.startActivity(intent);
    }

    @Override
    public void finishAc() {
        finish();
    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {
        mActivity.startActivityForResult(intent,requestCode);
    }

    @Override
    public void setIntentResult(int resultCode) {

    }


    @Override
    public void setIntentResult(int resultCode, Intent data) {

    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void dismissProgressDialog() {

    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }
}

package com.sensoro.smartcity.cameralibrary;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.utils.FileUtil;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.cameralibrary.listener.ErrorListener;
import com.sensoro.smartcity.cameralibrary.listener.JCameraListener;
import com.sensoro.smartcity.cameralibrary.state.CameraMachine;
import com.sensoro.smartcity.cameralibrary.util.LogUtil;
import com.sensoro.smartcity.cameralibrary.view.CameraView;
import com.sensoro.smartcity.widget.RecordedButton;

import java.util.Locale;


/**
 * =====================================
 * 作    者: 陈嘉桐
 * 版    本：1.0.4
 * 创建日期：2017/4/25
 * 描    述：
 * =====================================
 */
public class JCameraView extends FrameLayout implements CameraInterface.CameraOpenOverCallback, SurfaceHolder
        .Callback, CameraView,
        RecordedButton.OnGestureListener {
//    private static final String TAG = "JCameraView";

    //Camera状态机
    private CameraMachine machine;

    //闪关灯状态
    private static final int TYPE_FLASH_AUTO = 0x021;
    private static final int TYPE_FLASH_ON = 0x022;
    private static final int TYPE_FLASH_OFF = 0x023;
    private int type_flash = TYPE_FLASH_OFF;

    //拍照浏览时候的类型
    public static final int TYPE_PICTURE = 0x001;
    public static final int TYPE_VIDEO = 0x002;
    public static final int TYPE_SHORT = 0x003;
    public static final int TYPE_DEFAULT = 0x004;

    //录制视频比特率
    public static final int MEDIA_QUALITY_HIGH = 20 * 100000;
    public static final int MEDIA_QUALITY_MIDDLE = 16 * 100000;
    public static final int MEDIA_QUALITY_LOW = 12 * 100000;
    public static final int MEDIA_QUALITY_POOR = 8 * 100000;
    public static final int MEDIA_QUALITY_FUNNY = 4 * 100000;
    public static final int MEDIA_QUALITY_DESPAIR = 2 * 100000;
    public static final int MEDIA_QUALITY_SORRY = 1 * 80000;


    public static final int BUTTON_STATE_ONLY_CAPTURE = 0x101;      //只能拍照
    public static final int BUTTON_STATE_ONLY_RECORDER = 0x102;     //只能录像
    public static final int BUTTON_STATE_BOTH = 0x103;              //两者都可以


    //回调监听
    private JCameraListener jCameraLisenter;

    private Context mContext;
    private VideoView mVideoView;
    private FoucsView mFoucsView;
    private MediaPlayer mMediaPlayer;

    private int layout_width;
    private float screenProp = 0f;

    private Bitmap captureBitmap;   //捕获的图片
    private Bitmap firstFrame;      //第一帧图片
    private String videoUrl;        //视频URL


    private RecordedButton rb_start;
    private ImageView iv_finish;
    private ImageView iv_back;
    private TextView tv_retake;

    private float dp100;
    private float backX = -1;
    private float lastValue;
    //缩放梯度
    private int zoomGradient = 0;

    private boolean firstTouch = true;
    private float firstTouchLength = 0;

    private TextView tv_record_time;
    private TextView tv_hint;
    private ValueAnimator va;
    private int mProgress;

    private int maxDuration = 15 * 1000;
    private boolean isRecodding = false;

    private RelativeLayout rl_bottom;


    private final Handler myHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                try {
//录制完成重置
                    if (mProgress > maxDuration) {
                        rb_start.closeButton();
                        rb_start.setIsRecording(false);
                        tv_record_time.setVisibility(View.GONE);
                        tv_retake.setVisibility(View.GONE);

                        machine.stopRecord(false, mProgress);
                        startAnim();
                        return;
                    }
                    mProgress = mProgress + 50;
                    updateStatusProgress(mProgress);
                    sendMessageDelayed(myHandler.obtainMessage(0), 50);
                } catch (Exception e) {
                    SensoroToast.getInstance().makeText("录制错误，请稍后再试", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


    public void updateStatusProgress(int duration) {
        rb_start.setProgress(duration);
        tv_hint.setVisibility(duration > 5000 ? View.GONE : View.VISIBLE);
        tv_record_time.setText(String.format(Locale.ROOT, "00:%02d", duration / 1000));
    }


    public JCameraView(Context context) {
        this(context, null);
    }

    public JCameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        //get AttributeSet
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.JCameraView, defStyleAttr, 0);
        maxDuration = a.getInteger(R.styleable.JCameraView_duration_max, 15 * 1000);       //没设置默认为10s

        a.recycle();
        initData();
        initView();
    }

    private void initData() {
        lastValue = 0;

        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layout_width = outMetrics.widthPixels;
        } else {
            layout_width = outMetrics.widthPixels / 2;
        }
        //缩放梯度
        zoomGradient = (int) (layout_width / 16f);
        LogUtil.i("zoom = " + zoomGradient);
        machine = new CameraMachine(getContext(), this, this);
    }

    private void initView() {
        setWillNotDraw(false);
        View view = LayoutInflater.from(mContext).inflate(R.layout.camera_view, this);


        tv_hint = view.findViewById(R.id.tv_hint);
        tv_record_time = view.findViewById(R.id.tv_record_time);
        rb_start = view.findViewById(R.id.rb_start);
        tv_retake = view.findViewById(R.id.tv_retake);
        rl_bottom = view.findViewById(R.id.rl_bottom);


        rb_start.setMax(maxDuration);


//        vv_play = findViewById(R.id.vv_play);
        iv_finish = view.findViewById(R.id.iv_finish);
        iv_back = view.findViewById(R.id.iv_back);
        mVideoView = (VideoView) view.findViewById(R.id.video_preview);
//        mCaptureLayout.setDuration(duration);
        mFoucsView = (FoucsView) view.findViewById(R.id.fouce_view);
        mVideoView.getHolder().addCallback(this);
        //拍照 录像

        rb_start.setOnGestureListener(this);

        Point displayPoint = new Point();


        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getSize(displayPoint);

        // 返回 确认按钮设置为距离两个边为58dp,view大小是70dp，所以应该减掉是58+35
        dp100 = displayPoint.x / 2 - AppUtils.dp2px(getContext(), 93);


        //开始
        rb_start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

//               判断是否大于5秒可以停止，否则不能点击
                try {
                    if (mProgress > 5 * 1000) {
                        machine.stopRecord(false, mProgress);

                        rb_start.closeButton();
                        rb_start.setIsRecording(false);
                        tv_record_time.setVisibility(View.GONE);
                        tv_retake.setVisibility(View.GONE);
                        startAnim();
                        myHandler.removeCallbacksAndMessages(null);

                        return;
                    }

                    if (!isRecodding) {


                        isRecodding = true;

                        mProgress = 0;
                        myHandler.removeCallbacksAndMessages(null);
                        machine.record(mVideoView.getHolder().getSurface(), screenProp);


                        rb_start.setIsRecording(true);
                        tv_record_time.setVisibility(View.VISIBLE);
                        tv_retake.setVisibility(View.VISIBLE);
                        myHandler.sendEmptyMessageDelayed(0, 50);

                    }
                } catch (Exception e) {
                    SensoroToast.getInstance().makeText("录制错误，请稍后再试", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //确认
        iv_finish.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    machine.confirm();
                } catch (Exception e) {
                    SensoroToast.getInstance().makeText("录制错误，请稍后再试", Toast.LENGTH_SHORT).show();
                } finally {
                    isRecodding = false;
                }
            }
        });


        //重拍文字点击
        tv_retake.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    retake();
                } catch (Exception e) {
                    SensoroToast.getInstance().makeText("录制错误，请稍后再试", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //底部返回按钮
        iv_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    retake();
                    resetAnim();
                } catch (Exception e) {
                    SensoroToast.getInstance().makeText("录制错误，请稍后再试", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    /**
     * 重拍
     */

    public void retake() {

        machine.cancle(mVideoView.getHolder(), screenProp);
        isRecodding = false;
        mProgress = 0;
        rb_start.retake();
        tv_retake.setVisibility(View.GONE);
        tv_record_time.setVisibility(View.GONE);
        tv_hint.setVisibility(View.GONE);

        myHandler.removeCallbacksAndMessages(null);

    }


    /**
     * 底部还原动画
     */
    public void resetAnim() {

        iv_back.setX(iv_back.getX() + dp100);
        iv_finish.setX(iv_finish.getX() - dp100);

        rb_start.setVisibility(View.VISIBLE);
        iv_back.setVisibility(View.GONE);
        iv_finish.setVisibility(View.GONE);
        lastValue = 0;
    }

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
//                mPresenter.setEncodingStatus(true);
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
    public void onWindowFocusChanged(boolean hasFocus) {

        if (backX == -1) {
            backX = iv_back.getX();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float widthSize = mVideoView.getMeasuredWidth();
        float heightSize = mVideoView.getMeasuredHeight();
        if (screenProp == 0) {
            screenProp = heightSize / widthSize;
        }
    }

    @Override
    public void cameraHasOpened() {
        CameraInterface.getInstance().doStartPreview(mVideoView.getHolder(), screenProp);
    }

    //生命周期onResume
    public void onResume() {
        LogUtil.i("JCameraView onResume");
        resetState(TYPE_DEFAULT); //重置状态
        CameraInterface.getInstance().registerSensorManager(mContext);
//        CameraInterface.getInstance().setSwitchView(mSwitchCamera, mFlashLamp);
        machine.start(mVideoView.getHolder(), screenProp);
    }

    //生命周期onPause
    public void onPause() {
        LogUtil.i("JCameraView onPause");
        stopVideo();
        resetState(TYPE_PICTURE);
        CameraInterface.getInstance().isPreview(false);
        CameraInterface.getInstance().unregisterSensorManager(mContext);
    }

    //SurfaceView生命周期
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.i("JCameraView SurfaceCreated");
        new Thread() {
            @Override
            public void run() {
                CameraInterface.getInstance().doOpenCamera(JCameraView.this);
            }
        }.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtil.i("JCameraView SurfaceDestroyed");
        CameraInterface.getInstance().doDestroyCamera();
        myHandler.removeCallbacksAndMessages(null);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getPointerCount() == 1) {
                    //显示对焦指示器
                    setFocusViewWidthAnimation(event.getX(), event.getY());
                }
                if (event.getPointerCount() == 2) {
                    Log.i("CJT", "ACTION_DOWN = " + 2);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    firstTouch = true;
                }
                if (event.getPointerCount() == 2) {
                    //第一个点
                    float point_1_X = event.getX(0);
                    float point_1_Y = event.getY(0);
                    //第二个点
                    float point_2_X = event.getX(1);
                    float point_2_Y = event.getY(1);

                    float result = (float) Math.sqrt(Math.pow(point_1_X - point_2_X, 2) + Math.pow(point_1_Y -
                            point_2_Y, 2));

                    if (firstTouch) {
                        firstTouchLength = result;
                        firstTouch = false;
                    }
                    if ((int) (result - firstTouchLength) / zoomGradient != 0) {
                        firstTouch = true;
                        machine.zoom(result - firstTouchLength, CameraInterface.TYPE_CAPTURE);
                    }
//                    Log.i("CJT", "result = " + (result - firstTouchLength));
                }
                break;
            case MotionEvent.ACTION_UP:
                firstTouch = true;
                break;
        }
        return true;
    }

    //对焦框指示器动画
    private void setFocusViewWidthAnimation(float x, float y) {
        machine.foucs(x, y, new CameraInterface.FocusCallback() {
            @Override
            public void focusSuccess() {
                mFoucsView.setVisibility(INVISIBLE);
            }
        });
    }

    private void updateVideoViewSize(float videoWidth, float videoHeight) {
        if (videoWidth > videoHeight) {
            LayoutParams videoViewParam;
            int height = (int) ((videoHeight / videoWidth) * getWidth());
            videoViewParam = new LayoutParams(LayoutParams.MATCH_PARENT, height);
            videoViewParam.gravity = Gravity.CENTER;
            mVideoView.setLayoutParams(videoViewParam);
        }
    }

    /**************************************************
     * 对外提供的API                     *
     **************************************************/

    public void setSaveVideoPath(String path) {
        CameraInterface.getInstance().setSaveVideoPath(path);
    }


    public void setJCameraLisenter(JCameraListener jCameraLisenter) {
        this.jCameraLisenter = jCameraLisenter;
    }


    private ErrorListener errorLisenter;

    //启动Camera错误回调
    public void setErrorLisenter(ErrorListener errorLisenter) {
        this.errorLisenter = errorLisenter;
        CameraInterface.getInstance().setErrorLinsenter(errorLisenter);
    }


    //设置录制质量
    public void setMediaQuality(int quality) {
        CameraInterface.getInstance().setMediaQuality(quality);
    }

    @Override
    public void resetState(int type) {
        switch (type) {
            case TYPE_VIDEO:
                stopVideo();    //停止播放
                //初始化VideoView
                FileUtil.deleteFile(videoUrl);
                mVideoView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                machine.start(mVideoView.getHolder(), screenProp);
                break;
            case TYPE_PICTURE:
                break;
            case TYPE_SHORT:
                break;
            case TYPE_DEFAULT:
                mVideoView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                break;
        }
    }

    @Override
    public void confirmState(int type) {
        switch (type) {
            case TYPE_VIDEO:
                stopVideo();
//                mVideoView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//                machine.start(mVideoView.getHolder(), screenProp);
                if (jCameraLisenter != null) {
                    jCameraLisenter.recordSuccess(videoUrl, firstFrame, mProgress);
                }
                break;
            case TYPE_SHORT:
                break;
            case TYPE_DEFAULT:
                break;
        }
    }

    @Override
    public void showPicture(Bitmap bitmap, boolean isVertical) {
    }

    @Override
    public void playVideo(Bitmap firstFrame, final String url) {
        videoUrl = url;
        JCameraView.this.firstFrame = firstFrame;
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            } else {
                mMediaPlayer.reset();
            }
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.setSurface(mVideoView.getHolder().getSurface());
            mMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer
                    .OnVideoSizeChangedListener() {
                @Override
                public void
                onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                    updateVideoViewSize(mMediaPlayer.getVideoWidth(), mMediaPlayer
                            .getVideoHeight());
                }
            });
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopVideo() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void setTip(String tip) {

    }


    @Override
    public void startPreviewCallback() {
        LogUtil.i("startPreviewCallback");
        handlerFoucs(mFoucsView.getWidth() / 2, mFoucsView.getHeight() / 2);
    }

    @Override
    public boolean handlerFoucs(float x, float y) {
        if (y > rl_bottom.getTop()) {
            return false;
        }
        mFoucsView.setVisibility(VISIBLE);
        if (x < mFoucsView.getWidth() / 2) {
            x = mFoucsView.getWidth() / 2;
        }
        if (x > layout_width - mFoucsView.getWidth() / 2) {
            x = layout_width - mFoucsView.getWidth() / 2;
        }
        if (y < mFoucsView.getWidth() / 2) {
            y = mFoucsView.getWidth() / 2;
        }
        if (y > rl_bottom.getTop() - mFoucsView.getWidth() / 2) {
            y = rl_bottom.getTop() - mFoucsView.getWidth() / 2;
        }
        mFoucsView.setX(x - mFoucsView.getWidth() / 2);
        mFoucsView.setY(y - mFoucsView.getHeight() / 2);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mFoucsView, "scaleX", 1, 0.6f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mFoucsView, "scaleY", 1, 0.6f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mFoucsView, "alpha", 1f, 0.4f, 1f, 0.4f, 1f, 0.4f, 1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(scaleX).with(scaleY).before(alpha);
        animSet.setDuration(400);
        animSet.start();
        return true;
    }


    @Override
    public void onLongClick() {

    }

    @Override
    public void onClick() {


    }

    @Override
    public void onLift() {
    }

    @Override
    public void onOver() {
    }
}

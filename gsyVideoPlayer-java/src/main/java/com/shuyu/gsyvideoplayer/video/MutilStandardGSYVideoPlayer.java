package com.shuyu.gsyvideoplayer.video;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sensoro.common.base.ContextUtils;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.utils.LogUtils;
import com.sensoro.common.utils.Repause;
import com.sensoro.common.widgets.SelectDialog;
import com.sensoro.smartcity.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.R;
import com.shuyu.gsyvideoplayer.listener.GSYVideoShotListener;
import com.shuyu.gsyvideoplayer.listener.GSYVideoShotSaveListener;
import com.shuyu.gsyvideoplayer.utils.ChangeVideoFormatDialog;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.NetworkUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import moe.codeest.enviews.CityENDownloadView;
import moe.codeest.enviews.ENPlayView;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 多任务播放
 */

public class MutilStandardGSYVideoPlayer extends StandardGSYVideoPlayer implements Repause.Listener {

    /**
     * 自定义横屏时候的封面
     */
    private ImageView mCoverImage;

    public ImageView backMaskTv;
    public TextView swVideoFormatTv;

    public RelativeLayout getMaskLayoutTop() {
        return maskLayoutTop;
    }

    private RelativeLayout maskLayoutTop;

    public TextView maskTitleTv;

    public RelativeLayout getrMobileData() {
        return rMobileData;
    }

    private RelativeLayout rMobileData;


    /**
     * 录播是 View.VISIBLE
     */
    private static int isLive;
    private int currVolume;
    private int lastVolume;
    private static boolean isShowMaskTopBack = true;
    /**
     * 1没网,2移动数据 3加载失败重试 4 播放完成重播 5 离线 6直播 7 录像
     */
    private int cityPlayState;
    /**
     * 切换视频流
     */
    private static ArrayList<String> urlList = new ArrayList<>();
    /**
     * 当前视频格式
     */
    private int currentVideoFormat = ContextUtils.getContext().getSharedPreferences(Constants.PREFERENCE_VIDEO_FORMAT_SETTING_SP, Context.MODE_PRIVATE)
            .getInt(Constants.PREFERENCE_VIDEO_FORMAT_SETTING_KEY, 0);

    /**
     * 切换视频格式，不提示移动数据
     */
    private static boolean isChangeVideoFormat = false;
    private boolean isAudioChecked;

    private Button playAndRetryBtn;
    private LinearLayout layoutBottomControlLl;
    private ImageView maskFaceIv;
    private TextView tiptv;
    private ToggleButton audioIv;
    private ChangeVideoFormatDialog changeVideoFormatDialog = new ChangeVideoFormatDialog();
    private SelectDialog selectDialog;

    //亮度dialog
    protected Dialog mBrightnessDialog;

    //音量dialog
    protected Dialog mVolumeDialog;

    //触摸进度dialog
    protected Dialog mProgressDialog;
    //seekbar和触摸进度dialog
//    protected Dialog mSeekProgressDialog;

    protected TextView seekDialogTv;

    //触摸进度条的progress
    protected ProgressBar mDialogProgressBar;

    //音量进度条的progress
    protected ProgressBar mDialogVolumeProgressBar;

    //亮度文本
    protected TextView mBrightnessDialogTv;

    //触摸移动显示文本
    protected TextView mDialogSeekTime;

    //触摸移动显示全部时间
    protected TextView mDialogTotalTime;

    //触摸移动方向icon
    protected ImageView mDialogIcon;

    protected Drawable mBottomProgressDrawable;

    protected Drawable mBottomShowProgressDrawable;

    protected Drawable mBottomShowProgressThumbDrawable;

    protected Drawable mVolumeProgressDrawable;

    protected Drawable mDialogProgressBarDrawable;

    protected int mDialogProgressHighLightColor = -11;

    protected int mDialogProgressNormalColor = -11;
    private boolean mHideActionBar = false;
    private MyVolumeReceiver mVolumeReceiver;

    private ICityChangeUiVideoPlayerListener iCityChangeUiVideoPlayerListener;

    public void setICityChangeUiVideoPlayerListener(ICityChangeUiVideoPlayerListener listener) {
        this.iCityChangeUiVideoPlayerListener = listener;

    }


    /**
     * 1.5.0开始加入，如果需要不同布局区分功能，需要重载
     */
    public MutilStandardGSYVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public MutilStandardGSYVideoPlayer(Context context) {
        super(context);
    }

    public MutilStandardGSYVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        int dp48 = (int) (context.getResources().getDisplayMetrics().density * 28 + 0.5f);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.standard_view);
            int titleTop = (int) ta.getDimension(R.styleable.standard_view_title_margin_top, dp48);
            ta.recycle();

            RelativeLayout.LayoutParams lpBack = (RelativeLayout.LayoutParams) mBackButton.getLayoutParams();
            lpBack.topMargin = titleTop;
            mBackButton.setLayoutParams(lpBack);

            RelativeLayout.LayoutParams lpTitle = (RelativeLayout.LayoutParams) mTitleTextView.getLayoutParams();
            lpTitle.topMargin = titleTop;
            mTitleTextView.setLayoutParams(lpTitle);

            RelativeLayout.LayoutParams lpState = (RelativeLayout.LayoutParams) mStateTv.getLayoutParams();
            lpState.topMargin = titleTop;
            mStateTv.setLayoutParams(lpState);
            RelativeLayout.LayoutParams mStatEmpty = (RelativeLayout.LayoutParams) mStatEmptyIv.getLayoutParams();
            mStatEmpty.topMargin = titleTop;
            mStatEmptyIv.setLayoutParams(mStatEmpty);

        }

    }


    /**
     * 设置播放urls
     *
     * @param cityURl
     */
    public void setCityURl(ArrayList<String> cityURl, String title) {
        urlList = cityURl;
        if (null != cityURl && cityURl.size() >= currentVideoFormat) {
            isLive = View.INVISIBLE;
            setUp(cityURl.get(currentVideoFormat), false, title);
            mTitleTextView.setText(title);

        }

    }

    public void setHideActionBar(boolean isHide) {
        mHideActionBar = isHide;
    }


    /**
     * 是否显示蒙版返回键
     *
     * @param state
     */
    public void setIsShowBackMaskTv(boolean state) {
        isShowMaskTopBack = state;
        if (getContext() instanceof Activity) {

            if (!GSYVideoManager.isFullState((Activity) getContext())) {
                backMaskTv.setVisibility(state ? VISIBLE : GONE);
            }
        }

    }

    /**
     * 设置蒙版功能
     * 1没网,2移动数据 3加载失败重试 4 播放完成重播 5 离线
     *
     * @param cityState
     */
    public void setCityPlayState(int cityState) {
        dismissProgressDialog();
        cityPlayState = cityState;
        isShowMaskTopBack = true;
        setIsShowMaskTopBack(isShowMaskTopBack);

        switch (cityPlayState) {

            case 1:
                GSYVideoManager.onPause();
                playAndRetryBtn.setText(getResources().getString(R.string.retry));
                tiptv.setText(getResources().getString(R.string.online_tip));
                playAndRetryBtn.setVisibility(VISIBLE);
                rMobileData.setVisibility(VISIBLE);
                rMobileData.setBackgroundResource(R.drawable.camera_detail_mask);
                maskFaceIv.setVisibility(GONE);
                maskLayoutTop.setVisibility(VISIBLE);
                setViewShowState(mBottomContainer, INVISIBLE);
                playAndRetryBtn.setOnClickListener(v -> startPlayLogic());
                break;
            case 2:
                GSYVideoManager.onPause();
                setViewShowState(mBottomContainer, INVISIBLE);

                rMobileData.setVisibility(VISIBLE);
                playAndRetryBtn.setVisibility(VISIBLE);
                playAndRetryBtn.setText(R.string.play);
//                mTitleTextView.setText("");

                rMobileData.setBackgroundColor(Color.TRANSPARENT);


                maskFaceIv.setVisibility(VISIBLE);
//                maskTitleTv.setText(mTitle);
                tiptv.setText(getResources().getString(R.string.mobile_network));
                maskLayoutTop.setVisibility(VISIBLE);
//                playAndRetryBtn.setOnClickListener(v -> {
//                    maskLayoutTop.setVisibility(GONE);
//                    rMobileData.setVisibility(GONE);
//                    cityPlayState = -1;
//                    if (mVideoAllCallBack != null) {
//                        Debuger.printfLog("onClickStartThumb");
//                        mVideoAllCallBack.onClickStartThumb(mOriginUrl, mTitle, MutilStandardGSYVideoPlayer.this);
//                    }
//                    prepareVideo();
//                    startDismissControlViewTimer();
//
//                });

                break;
            case 3:


                rMobileData.setVisibility(VISIBLE);
                playAndRetryBtn.setVisibility(VISIBLE);
                rMobileData.setBackgroundResource(R.drawable.camera_detail_mask);
                maskFaceIv.setVisibility(GONE);
                playAndRetryBtn.setText(R.string.retry);
                tiptv.setText(getResources().getString(R.string.retry_play));
                maskLayoutTop.setVisibility(VISIBLE);
                maskTitleTv.setText(mTitle);
                break;
            case 4:
                //录播才会执行播放完成
                if (isLive == View.VISIBLE) {
                    setViewShowState(mBottomContainer, View.INVISIBLE);

                    tiptv.setText(getResources().getString(R.string.played));
                    playAndRetryBtn.setText(getResources().getString(R.string.replay));
                    playAndRetryBtn.setVisibility(VISIBLE);
                    rMobileData.setVisibility(VISIBLE);
                    maskFaceIv.setVisibility(GONE);
                    rMobileData.setBackground(null);
                    rMobileData.setBackgroundColor(getResources().getColor(R.color.c_66000000));
                    playAndRetryBtn.setOnClickListener(v -> {
                        setIsLive(View.VISIBLE);
                        startPlayLogic();

                    });
                }


                break;
            case 5:

                rMobileData.setVisibility(VISIBLE);
                playAndRetryBtn.setVisibility(VISIBLE);
                playAndRetryBtn.setText(R.string.retry);
                rMobileData.setBackgroundResource(R.drawable.camera_detail_mask);
                maskFaceIv.setVisibility(GONE);

                tiptv.setText(getResources().getString(R.string.cameroffline));
                maskLayoutTop.setVisibility(VISIBLE);
                break;
            case 6:
                break;
            case 7:
                break;

            default:
                rMobileData.setVisibility(View.GONE);
                maskLayoutTop.setVisibility(View.GONE);
                maskFaceIv.setVisibility(GONE);
                mCoverImage.setVisibility(GONE);
                seekDialogTv.setVisibility(INVISIBLE);


                break;
        }

    }


    /**
     * 设置封面
     *
     * @param drawable
     */

    public void setMobileFace(final Drawable drawable) {
        maskFaceIv.setVisibility(VISIBLE);
        if (null != drawable) {
            maskFaceIv.setImageDrawable(drawable);
        } else {
            maskFaceIv.setImageResource(R.drawable.camera_detail_mask);

        }
    }

    /**
     * 底部进度条是否显示，直播不显示
     *
     * @param isLive
     */
    public void setIsLive(int isLive) {
        if (isLive == View.INVISIBLE) {
            cityChangePosition = false;
            //todo 声音按钮

            mStateTv.setText(R.string.live);
            mStateTv.setBackgroundResource(R.drawable.shape_bg_corner_2dp_29c_shadow);
        } else {
            cityChangePosition = true;
            mStateTv.setText(R.string.gsy_video);
            mStateTv.setBackgroundResource(R.drawable.shape_bg_corner_2dp_f48f57_shadow);
        }

        MutilStandardGSYVideoPlayer.isLive = isLive;
        hide();
        updateVideTypeTag();


    }

    /**
     * 蒙版返回箭头是否显示
     */
    public void setIsShowMaskTopBack(boolean isShowMaskTopBack) {

        MutilStandardGSYVideoPlayer.isShowMaskTopBack = isShowMaskTopBack;
        if (!isShowMaskTopBack) {
            backMaskTv.setVisibility(GONE);
        } else {
            backMaskTv.setVisibility(VISIBLE);

        }


    }

    public Button getPlayAndRetryBtn() {
        return playAndRetryBtn;
    }


    private void registerReceiver() {
        mVolumeReceiver = new MyVolumeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        getContext().registerReceiver(mVolumeReceiver, filter);
    }

    @Override
    public void onApplicationResumed() {
        GSYVideoManager.onResume(false);

    }

    @Override
    public void onApplicationPaused() {

        if (mOrientationUtils != null) {
            mOrientationUtils.backToProtVideo();
        }
        if (GSYVideoManager.backFromWindowFull(mContext)) {
            return;
        }
    }

    private class MyVolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("android.media.VOLUME_CHANGED_ACTION".equals(intent.getAction())) {
                currVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (currVolume == 0) {
                    audioIv.setChecked(true);
                    isAudioChecked = true;
                } else {
                    audioIv.setChecked(false);
                    isAudioChecked = false;
                    lastVolume = currVolume;
                }
            }
        }
    }

    /**
     * 继承后重写可替换为你需要的布局
     *
     * @return
     */
    @Override
    public int getLayoutId() {
        return R.layout.city_video_layout_standard;
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mCoverImage = (ImageView) findViewById(R.id.thumbImage);
        VideoOptionModel videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
        List<VideoOptionModel> list = new ArrayList<>();
        list.add(videoOptionModel);
        GSYVideoManager.instance().setOptionModelList(list);
        Repause.registerListener(this);
        boolean needTimeOutOther = GSYVideoManager.instance().isNeedTimeOutOther();
        int timeOut = GSYVideoManager.instance().getTimeOut();
        try {
            LogUtils.loge("GSYVideoManager needTimeOutOther = " + needTimeOutOther + ",timeOut = " + timeOut);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        currVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        lastVolume = currVolume;
        registerReceiver();
        swVideoFormatTv = findViewById(R.id.sw_video_format);
        maskFaceIv = findViewById(R.id.face_iv);
        seekDialogTv = findViewById(R.id.city_seek_dialog_tv);
        layoutBottomControlLl = findViewById(R.id.layout_bottom_control_ll);
        rMobileData = findViewById(R.id.rl_mobile_data);
        playAndRetryBtn = findViewById(R.id.playa_retry_btn);

        backMaskTv = findViewById(R.id.mask_iv_back);
        maskTitleTv = findViewById(R.id.mask_title_tv);
        maskLayoutTop = findViewById(R.id.mask_layout_top);
        tiptv = findViewById(R.id.tip_data_tv);

        playAndRetryBtn.setOnClickListener(v -> {
            maskLayoutTop.setVisibility(GONE);
            rMobileData.setVisibility(GONE);
            cityPlayState = -1;

            if (mVideoAllCallBack != null) {
                Debuger.printfLog("onClickStartThumb");
                mVideoAllCallBack.onClickStartThumb(mOriginUrl, mTitle, MutilStandardGSYVideoPlayer.this);
            }
            prepareVideo();
            startDismissControlViewTimer();

        });
        backMaskTv.setOnClickListener(v -> {
            if (getContext() instanceof Activity) {

                if (GSYVideoManager.isFullState((Activity) getContext())) {


                    if (GSYVideoManager.backFromWindowFull(mContext)) {
                        return;
                    }
                } else {
                    if (getContext() instanceof Activity) {
                        ((Activity) getContext()).finish();
                    }
                }
            }
        });
        audioIv = findViewById(R.id.audio_iv);
        audioIv.setOnCheckedChangeListener((buttonView, isChecked) -> {

            isAudioChecked = isChecked;
            if (isChecked) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            } else {

                postDelayed(() -> mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, lastVolume, 0), 200);

            }
        });

        swVideoFormatTv.setOnClickListener(v -> {
            int orientation = mContext.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                changeVideoFormatDialog = new ChangeVideoFormatDialog();
                changeVideoFormatDialog.showChangeVideoFormatDialog(mContext, currentVideoFormat, position -> changeVideoFormat(position));
            } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {

                List<String> names = new ArrayList<>();
                names.add(mContext.getString(R.string.flv_format));
                names.add(mContext.getString(R.string.hls_format));
                selectDialog = new SelectDialog((Activity) mContext, currentVideoFormat, R.style
                        .transparentFrameWindowStyle,
                        (parent, view, position, id) -> changeVideoFormat(position), names, mContext.getString(R.string.video_format_des));
                selectDialog.setCanceledOnTouchOutside(true);

                selectDialog.show();

            }


        });

        if (mBottomShowProgressDrawable != null) {
            mProgressBar.setProgressDrawable(mBottomProgressDrawable);
        }

        if (mBottomShowProgressThumbDrawable != null) {
            mProgressBar.setThumb(mBottomShowProgressThumbDrawable);
        }


    }


    /**
     * 显示wifi确定框
     */
    @Override
    public void startPlayLogic() {

        setViewShowState(mBottomContainer, INVISIBLE);
        try {
            LogUtils.loge(mUrl);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        maskLayoutTop.setVisibility(GONE);
        rMobileData.setVisibility(GONE);

        maskFaceIv.setVisibility(GONE);

        if (cityPlayState != 1 && cityPlayState != 2) {
            if ((!NetworkUtils.isAvailable(getContext()) || !NetworkUtils.isWifiConnected(getContext()))) {
                if (!NetworkUtils.isAvailable(getContext())) {
                    setCityPlayState(1);
                    return;
                }
                /**
                 * 切换视频格式，不显示移动数据
                 */
                if (!NetworkUtils.isWifiConnected(getContext()) && !isChangeVideoFormat) {
                    setCityPlayState(2);
                    return;
                }
            }
        }
        if (mVideoAllCallBack != null) {
            Debuger.printfLog("onClickStartThumb");
            mVideoAllCallBack.onClickStartThumb(mOriginUrl, mTitle, MutilStandardGSYVideoPlayer.this);
        }
        prepareVideo();
        startDismissControlViewTimer();
        cityPlayState = -1;
        isChangeVideoFormat = false;


    }

    /**
     * 根据是否直播隐藏控件
     */
    public void hide() {
        setViewShowState(mProgressBar, isLive);

        setViewShowState(mTotalTimeTextView, isLive);
        setViewShowState(mCurrentTimeTextView, isLive);

    }

    public void clickCityStartIcon() {
        clickStartIcon();

    }

    /**
     * 显示wifi确定框，如需要自定义继承重写即可
     */
    @Override
    protected void showWifiDialog() {
        if (!NetworkUtils.isAvailable(mContext)) {
            startPlayLogic();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
        builder.setMessage(getResources().getString(R.string.tips_not_wifi));
        builder.setPositiveButton(getResources().getString(R.string.tips_not_wifi_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startPlayLogic();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.tips_not_wifi_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        super.onProgressChanged(seekBar, progress, fromUser);
        if (fromUser) {
            int time = seekBar.getProgress() * getDuration() / 100;
            String seekTime = CommonUtil.stringForTime(time);

            showCityProgressDiallog(seekTime, time);


        }

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        super.onStopTrackingTouch(seekBar);

    }

    /**
     * 触摸显示滑动进度dialog，如需要自定义继承重写即可，记得重写dismissProgressDialog
     */
    @Override
    @SuppressWarnings("ResourceType")
    protected void showProgressDialog(float deltaX, String seekTime,
                                      int seekTimePosition, String totalTime, int totalTimeDuration) {

        showCityProgressDiallog(seekTime, seekTimePosition);

    }

    /**
     * 显示气泡dialog
     *
     * @param seekTime
     */

    private void showCityProgressDiallog(String seekTime, int seekTimePosition) {

        if (seekDialogTv.getVisibility() != View.VISIBLE) {
            seekDialogTv.setVisibility(VISIBLE);
        }
        if (null != seekDialogTv) {
            seekDialogTv.setText(seekTime);
        }


    }

    @Override
    protected void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }


        seekDialogTv.setVisibility(INVISIBLE);

    }

    /**
     * 触摸音量dialog，如需要自定义继承重写即可，记得重写dismissVolumeDialog
     */
    @Override
    protected void showVolumeDialog(float deltaY, int volumePercent) {
        if (mVolumeDialog == null) {
            View localView = LayoutInflater.from(getActivityContext()).inflate(getVolumeLayoutId(), null);
            if (localView.findViewById(getVolumeProgressId()) instanceof ProgressBar) {
                mDialogVolumeProgressBar = ((ProgressBar) localView.findViewById(getVolumeProgressId()));
                if (mVolumeProgressDrawable != null && mDialogVolumeProgressBar != null) {
                    mDialogVolumeProgressBar.setProgressDrawable(mVolumeProgressDrawable);
                }
            }
            mVolumeDialog = new Dialog(getActivityContext(), R.style.video_style_dialog_progress);
            mVolumeDialog.setContentView(localView);
            mVolumeDialog.getWindow().addFlags(8);
            mVolumeDialog.getWindow().addFlags(32);
            mVolumeDialog.getWindow().addFlags(16);
            mVolumeDialog.getWindow().setLayout(-2, -2);
            WindowManager.LayoutParams localLayoutParams = mVolumeDialog.getWindow().getAttributes();
            localLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
            localLayoutParams.width = getWidth();
            localLayoutParams.height = getHeight();
            int location[] = new int[2];
            getLocationOnScreen(location);
            localLayoutParams.x = location[0];
            localLayoutParams.y = location[1];
            mVolumeDialog.getWindow().setAttributes(localLayoutParams);
        }
        if (!mVolumeDialog.isShowing()) {
            mVolumeDialog.show();
        }
        if (mDialogVolumeProgressBar != null) {
            mDialogVolumeProgressBar.setProgress(volumePercent);
        }
    }

    @Override
    protected void dismissVolumeDialog() {
        if (mVolumeDialog != null) {
            mVolumeDialog.dismiss();
            mVolumeDialog = null;
        }
    }


    /**
     * 触摸亮度dialog，如需要自定义继承重写即可，记得重写dismissBrightnessDialog
     */
    @Override
    protected void showBrightnessDialog(float percent) {
        if (mBrightnessDialog == null) {
            View localView = LayoutInflater.from(getActivityContext()).inflate(getBrightnessLayoutId(), null);
            if (localView.findViewById(getBrightnessTextId()) instanceof TextView) {
                mBrightnessDialogTv = (TextView) localView.findViewById(getBrightnessTextId());
            }
            mBrightnessDialog = new Dialog(getActivityContext(), R.style.video_style_dialog_progress);
            mBrightnessDialog.setContentView(localView);
            mBrightnessDialog.getWindow().addFlags(8);
            mBrightnessDialog.getWindow().addFlags(32);
            mBrightnessDialog.getWindow().addFlags(16);
            mBrightnessDialog.getWindow().setLayout(-2, -2);
            WindowManager.LayoutParams localLayoutParams = mBrightnessDialog.getWindow().getAttributes();
            localLayoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
            localLayoutParams.width = getWidth();
            localLayoutParams.height = getHeight();
            int location[] = new int[2];
            getLocationOnScreen(location);
            localLayoutParams.x = location[0];
            localLayoutParams.y = location[1];
            mBrightnessDialog.getWindow().setAttributes(localLayoutParams);
        }
        if (!mBrightnessDialog.isShowing()) {
            mBrightnessDialog.show();
        }
        if (mBrightnessDialogTv != null)
            mBrightnessDialogTv.setText((int) (percent * 100) + "%");
    }


    @Override
    protected void dismissBrightnessDialog() {
        if (mBrightnessDialog != null) {
            mBrightnessDialog.dismiss();
            mBrightnessDialog = null;
        }
    }

    @Override
    protected void cloneParams(GSYBaseVideoPlayer from, GSYBaseVideoPlayer to) {
        super.cloneParams(from, to);
        MutilStandardGSYVideoPlayer sf = (MutilStandardGSYVideoPlayer) from;
        MutilStandardGSYVideoPlayer st = (MutilStandardGSYVideoPlayer) to;
        if (st.mProgressBar != null && sf.mProgressBar != null) {
            st.mProgressBar.setProgress(sf.mProgressBar.getProgress());
            st.mProgressBar.setSecondaryProgress(sf.mProgressBar.getSecondaryProgress());
        }
        if (st.mTotalTimeTextView != null && sf.mTotalTimeTextView != null) {
            st.mTotalTimeTextView.setText(sf.mTotalTimeTextView.getText());
        }
        if (st.mCurrentTimeTextView != null && sf.mCurrentTimeTextView != null) {
            st.mCurrentTimeTextView.setText(sf.mCurrentTimeTextView.getText());
        }
        if (st.mThumbImageViewLayout != null && sf.mThumbImageViewLayout != null) {
            st.mThumbImageViewLayout = sf.mThumbImageViewLayout;
        }
        st.cityPlayState = sf.cityPlayState;
        st.isAudioChecked = sf.isAudioChecked;
    }

    /**
     * 将自定义的效果也设置到全屏
     *
     * @param context
     * @param actionBar 是否有actionBar，有的话需要隐藏
     * @param statusBar 是否有状态bar，有的话需要隐藏
     * @return
     */
    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar,
                                                    boolean statusBar) {
        MutilStandardGSYVideoPlayer gsyBaseVideoPlayer = (MutilStandardGSYVideoPlayer) super.startWindowFullscreen(context, actionBar, statusBar);
        if (gsyBaseVideoPlayer != null) {
            MutilStandardGSYVideoPlayer gsyVideoPlayer = (MutilStandardGSYVideoPlayer) gsyBaseVideoPlayer;
            gsyVideoPlayer.setLockClickListener(mLockClickListener);
            gsyVideoPlayer.setNeedLockFull(isNeedLockFull());
            initFullUI(gsyVideoPlayer);
            //比如你自定义了返回案件，但是因为返回按键底层已经设置了返回事件，所以你需要在这里重新增加的逻辑
            backMaskTv.setVisibility(VISIBLE);
            gsyBaseVideoPlayer.currentVideoFormat = currentVideoFormat;
            gsyBaseVideoPlayer.mThumbImageViewLayout = mThumbImageViewLayout;
            gsyBaseVideoPlayer.cityPlayState = cityPlayState;
            gsyBaseVideoPlayer.isAudioChecked = isAudioChecked;
            setCityPlayState(cityPlayState);
            audioIv.setChecked(isAudioChecked);

        }
        return gsyBaseVideoPlayer;
    }

    @Override
    protected void resolveNormalVideoShow(View oldF, ViewGroup vp, GSYVideoPlayer gsyVideoPlayer) {


        backMaskTv.setVisibility(isShowMaskTopBack ? VISIBLE : GONE);
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer);

        if (gsyVideoPlayer != null) {
            MutilStandardGSYVideoPlayer sampleVideo = (MutilStandardGSYVideoPlayer) gsyVideoPlayer;
            currentVideoFormat = sampleVideo.currentVideoFormat;
            mThumbImageViewLayout = sampleVideo.mThumbImageViewLayout;
            setCityPlayState(cityPlayState);
            audioIv.setChecked(isAudioChecked);


        }


    }

    /********************************各类UI的状态显示*********************************************/

    /**
     * 点击触摸显示和隐藏逻辑
     */
    @Override
    protected void onClickUiToggle() {
        if (mIfCurrentIsFullscreen && mLockCurScreen && mNeedLockFull) {
            setViewShowState(mLockScreen, VISIBLE);
            return;
        }
        if (mCurrentState == CURRENT_STATE_PREPAREING) {
            if (mBottomContainer != null) {
                if (mBottomContainer.getVisibility() == View.VISIBLE) {
                    changeUiToPrepareingClear();
                } else {
                    changeUiToPreparingShow();
                }
            }
        } else if (mCurrentState == CURRENT_STATE_PLAYING) {
            if (mBottomContainer != null) {
                if (mBottomContainer.getVisibility() == View.VISIBLE) {
                    changeUiToPlayingClear();
                } else {
                    changeUiToPlayingShow();
                }
            }
        } else if (mCurrentState == CURRENT_STATE_PAUSE) {
            if (mBottomContainer != null) {
                if (mBottomContainer.getVisibility() == View.VISIBLE) {
                    changeUiToPauseClear();
                } else {
                    changeUiToPauseShow();
                }
            }
        } else if (mCurrentState == CURRENT_STATE_AUTO_COMPLETE) {
            if (mBottomContainer != null) {
                if (mBottomContainer.getVisibility() == View.VISIBLE) {
                    changeUiToCompleteClear();
                } else {
                    changeUiToCompleteShow();
                }
            }
        } else if (mCurrentState == CURRENT_STATE_PLAYING_BUFFERING_START) {
            if (mBottomContainer != null) {
                if (mBottomContainer.getVisibility() == View.VISIBLE) {
                    changeUiToPlayingBufferingClear();
                } else {
                    changeUiToPlayingBufferingShow();
                }
            }
        }
    }

    @Override
    protected void hideAllWidget() {
        setViewShowState(mBottomContainer, INVISIBLE);
        setViewShowState(mTopContainer, INVISIBLE);
        if (mHideActionBar) {
            CommonUtil.hideSupportActionBar(mContext, true, true);
        }
    }


    @Override
    protected void changeUiToNormal() {
        Debuger.printfLog("changeUiToNormal");

        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mBottomContainer, INVISIBLE);
        setViewShowState(mStartButton, VISIBLE);
        setViewShowState(mLoadingProgressBar, INVISIBLE);
        setViewShowState(mThumbImageViewLayout, VISIBLE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);
        if (mHideActionBar) {
            CommonUtil.showSupportActionBar(mContext, true, true);
        }
        updateStartImage();
        if (mLoadingProgressBar instanceof CityENDownloadView) {
            ((CityENDownloadView) mLoadingProgressBar).reset();
        }
    }

    @Override
    protected void changeUiToPreparingShow() {
        if (null != mOrientationUtils) {
            mOrientationUtils.setEnable(false);
        }
        Debuger.printfLog("changeUiToPreparingShow");

        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mBottomContainer, View.INVISIBLE);
//        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(mLoadingProgressBar, VISIBLE);


        setViewShowState(mThumbImageViewLayout, VISIBLE);
        setViewShowState(mLockScreen, GONE);
        if (mHideActionBar) {
            CommonUtil.showSupportActionBar(mContext, true, true);
        }
        if (mLoadingProgressBar instanceof CityENDownloadView) {
            CityENDownloadView enDownloadView = (CityENDownloadView) mLoadingProgressBar;
            ((CityENDownloadView) mLoadingProgressBar).start();
        }
    }

    @Override
    protected void changeUiToPlayingShow() {

        if (null != mOrientationUtils) {
            mOrientationUtils.setEnable(true);

        }
        if (null != iCityChangeUiVideoPlayerListener) {
            iCityChangeUiVideoPlayerListener.OnCityChangeUiToPlayingShow();
        }
        Debuger.printfLog("changeUiToPlayingShow");

        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mBottomContainer, VISIBLE);
        setViewShowState(mStartButton, VISIBLE);
        setViewShowState(mLoadingProgressBar, INVISIBLE);
        setViewShowState(mThumbImageViewLayout, INVISIBLE);


        if (mCoverImage.getVisibility() == VISIBLE) {
            postDelayed(() -> setViewShowState(mCoverImage, INVISIBLE), 300);
        }

        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);
        if (mHideActionBar) {
            CommonUtil.showSupportActionBar(mContext, true, true);
        }
        if (mLoadingProgressBar instanceof CityENDownloadView) {
            ((CityENDownloadView) mLoadingProgressBar).reset();
        }
        updateStartImage();

        hide();
        dismissProgressDialog();
    }

    @Override
    protected void changeUiToPauseShow() {

        Debuger.printfLog("changeUiToPauseShow");

        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mBottomContainer, VISIBLE);
        setViewShowState(mStartButton, VISIBLE);
        setViewShowState(mLoadingProgressBar, INVISIBLE);
        setViewShowState(mThumbImageViewLayout, INVISIBLE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);
        if (mHideActionBar) {
            CommonUtil.showSupportActionBar(mContext, true, true);
        }
        if (mLoadingProgressBar instanceof CityENDownloadView) {
            ((CityENDownloadView) mLoadingProgressBar).reset();
        }
        updateStartImage();
        updatePauseCover();
        hide();
    }

    @Override
    protected void changeUiToPlayingBufferingShow() {
        if (null != iCityChangeUiVideoPlayerListener) {
            iCityChangeUiVideoPlayerListener.OnCityChangeUiToPlayingBufferingShow();
        }
        Debuger.printfLog("changeUiToPlayingBufferingShow");

        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mBottomContainer, VISIBLE);
        setViewShowState(mLoadingProgressBar, VISIBLE);
        setViewShowState(mThumbImageViewLayout, INVISIBLE);
        setViewShowState(mLockScreen, GONE);
        if (mHideActionBar) {
            CommonUtil.showSupportActionBar(mContext, true, true);
        }
        if (mLoadingProgressBar instanceof CityENDownloadView) {
            CityENDownloadView enDownloadView = (CityENDownloadView) mLoadingProgressBar;
            ((CityENDownloadView) mLoadingProgressBar).start();
        }
        hide();

    }

    @Override
    protected void changeUiToCompleteShow() {

        Debuger.printfLog("changeUiToCompleteShow");

        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mBottomContainer, VISIBLE);
        setViewShowState(mStartButton, VISIBLE);
        setViewShowState(mLoadingProgressBar, INVISIBLE);
        setViewShowState(mThumbImageViewLayout, VISIBLE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);
        if (mHideActionBar) {
            CommonUtil.showSupportActionBar(mContext, true, true);
        }
        if (mLoadingProgressBar instanceof CityENDownloadView) {
            ((CityENDownloadView) mLoadingProgressBar).reset();
        }
        updateStartImage();
        hide();

    }

    @Override
    protected void changeUiToError() {
        Debuger.printfLog("changeUiToError");

        setViewShowState(mTopContainer, INVISIBLE);
        setViewShowState(mBottomContainer, INVISIBLE);
        setViewShowState(mStartButton, VISIBLE);
        setViewShowState(mLoadingProgressBar, INVISIBLE);
        setViewShowState(mThumbImageViewLayout, INVISIBLE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);
        if (mHideActionBar) {
            CommonUtil.hideSupportActionBar(mContext, true, true);
        }
        if (mLoadingProgressBar instanceof CityENDownloadView) {
            ((CityENDownloadView) mLoadingProgressBar).reset();
        }
        updateStartImage();
    }

    /**
     * 触摸进度dialog的layoutId
     * 继承后重写可返回自定义
     * 有自定义的实现逻辑可重载showProgressDialog方法
     */
    protected int getProgressDialogLayoutId() {
        return R.layout.video_progress_dialog;
    }

    /**
     * 触摸进度dialog的进度条id
     * 继承后重写可返回自定义，如果没有可返回空
     * 有自定义的实现逻辑可重载showProgressDialog方法
     */
    protected int getProgressDialogProgressId() {
        return R.id.duration_progressbar;
    }

    /**
     * 触摸进度dialog的当前时间文本
     * 继承后重写可返回自定义，如果没有可返回空
     * 有自定义的实现逻辑可重载showProgressDialog方法
     */
    protected int getProgressDialogCurrentDurationTextId() {
        return R.id.tv_current;
    }

    /**
     * 触摸进度dialog全部时间文本
     * 继承后重写可返回自定义，如果没有可返回空
     * 有自定义的实现逻辑可重载showProgressDialog方法
     */
    protected int getProgressDialogAllDurationTextId() {
        return R.id.tv_duration;
    }

    /**
     * 触摸进度dialog的图片id
     * 继承后重写可返回自定义，如果没有可返回空
     * 有自定义的实现逻辑可重载showProgressDialog方法
     */
    protected int getProgressDialogImageId() {
        return R.id.duration_image_tip;
    }

    /**
     * 音量dialog的layoutId
     * 继承后重写可返回自定义
     * 有自定义的实现逻辑可重载showVolumeDialog方法
     */
    protected int getVolumeLayoutId() {
        return R.layout.video_volume_dialog;
    }

    /**
     * 音量dialog的百分比进度条 id
     * 继承后重写可返回自定义，如果没有可返回空
     * 有自定义的实现逻辑可重载showVolumeDialog方法
     */
    protected int getVolumeProgressId() {
        return R.id.volume_progressbar;
    }


    /**
     * 亮度dialog的layoutId
     * 继承后重写可返回自定义
     * 有自定义的实现逻辑可重载showBrightnessDialog方法
     */
    protected int getBrightnessLayoutId() {
        return R.layout.video_brightness;
    }

    /**
     * 亮度dialog的百分比text id
     * 继承后重写可返回自定义，如果没有可返回空
     * 有自定义的实现逻辑可重载showBrightnessDialog方法
     */
    protected int getBrightnessTextId() {
        return R.id.app_video_brightness;
    }

    protected void changeUiToPrepareingClear() {
        Debuger.printfLog("changeUiToPrepareingClear");

        setViewShowState(mTopContainer, INVISIBLE);
        setViewShowState(mBottomContainer, INVISIBLE);
        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(mLoadingProgressBar, INVISIBLE);
        setViewShowState(mThumbImageViewLayout, INVISIBLE);
//        setViewShowState(mBottomProgressBar, INVISIBLE);
        setViewShowState(mLockScreen, GONE);
        if (mHideActionBar) {
            CommonUtil.hideSupportActionBar(mContext, true, true);
        }
        if (mLoadingProgressBar instanceof CityENDownloadView) {
            ((CityENDownloadView) mLoadingProgressBar).reset();
        }
    }

    protected void changeUiToPlayingClear() {
        Debuger.printfLog("changeUiToPlayingClear");
        changeUiToClear();
//        setViewShowState(mBottomProgressBar, VISIBLE);
    }

    protected void changeUiToPauseClear() {
        Debuger.printfLog("changeUiToPauseClear");
        changeUiToClear();
//        setViewShowState(mBottomProgressBar, VISIBLE);
        updatePauseCover();
    }

    protected void changeUiToPlayingBufferingClear() {
        Debuger.printfLog("changeUiToPlayingBufferingClear");

        setViewShowState(mTopContainer, INVISIBLE);
        setViewShowState(mBottomContainer, INVISIBLE);
        setViewShowState(mLoadingProgressBar, VISIBLE);
        setViewShowState(mThumbImageViewLayout, INVISIBLE);
        setViewShowState(mLockScreen, GONE);
        if (mHideActionBar) {
            CommonUtil.hideSupportActionBar(mContext, true, true);
        }
        if (mLoadingProgressBar instanceof CityENDownloadView) {
            ((CityENDownloadView) mLoadingProgressBar).start();
        }
        updateStartImage();
    }

    protected void changeUiToClear() {
        Debuger.printfLog("changeUiToClear");

        setViewShowState(mTopContainer, INVISIBLE);
        setViewShowState(mBottomContainer, INVISIBLE);
        setViewShowState(mLoadingProgressBar, INVISIBLE);
        setViewShowState(mThumbImageViewLayout, INVISIBLE);
        setViewShowState(mLockScreen, GONE);
        if (mHideActionBar) {
            CommonUtil.hideSupportActionBar(mContext, true, true);
        }
        if (mLoadingProgressBar instanceof CityENDownloadView) {
            ((CityENDownloadView) mLoadingProgressBar).reset();
        }
    }

    protected void changeUiToCompleteClear() {
        Debuger.printfLog("changeUiToCompleteClear");

        setViewShowState(mTopContainer, INVISIBLE);
        setViewShowState(mBottomContainer, INVISIBLE);
        setViewShowState(mStartButton, VISIBLE);
        setViewShowState(mLoadingProgressBar, INVISIBLE);
        setViewShowState(mThumbImageViewLayout, VISIBLE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);
        if (mHideActionBar) {
            CommonUtil.hideSupportActionBar(mContext, true, true);
        }
        if (mLoadingProgressBar instanceof CityENDownloadView) {
            ((CityENDownloadView) mLoadingProgressBar).reset();
        }
        updateStartImage();
    }

    /**
     * 定义开始按键显示
     */
    protected void updateStartImage() {
        if (mStartButton instanceof ENPlayView) {
            ENPlayView enPlayView = (ENPlayView) mStartButton;
            enPlayView.setDuration(500);
            if (mCurrentState == CURRENT_STATE_PLAYING) {
                enPlayView.play();
            } else if (mCurrentState == CURRENT_STATE_ERROR) {
                enPlayView.pause();
            } else {
                enPlayView.pause();
            }
        } else if (mStartButton instanceof ImageView) {
            ImageView imageView = (ImageView) mStartButton;
            if (mCurrentState == CURRENT_STATE_PLAYING) {
                imageView.setImageResource(R.drawable.pause);
            } else if (mCurrentState == CURRENT_STATE_ERROR) {
                imageView.setImageResource(R.drawable.pause);
            } else {
                imageView.setImageResource(R.drawable.play);
            }
        }
    }

    /**
     * 全屏的UI逻辑
     */
    private void initFullUI(MutilStandardGSYVideoPlayer standardGSYVideoPlayer) {

        if (mBottomProgressDrawable != null) {
            standardGSYVideoPlayer.setBottomProgressBarDrawable(mBottomProgressDrawable);
        }

        if (mBottomShowProgressDrawable != null && mBottomShowProgressThumbDrawable != null) {
            standardGSYVideoPlayer.setBottomShowProgressBarDrawable(mBottomShowProgressDrawable,
                    mBottomShowProgressThumbDrawable);
        }

        if (mVolumeProgressDrawable != null) {
            standardGSYVideoPlayer.setDialogVolumeProgressBar(mVolumeProgressDrawable);
        }

        if (mDialogProgressBarDrawable != null) {
            standardGSYVideoPlayer.setDialogProgressBar(mDialogProgressBarDrawable);
        }

        if (mDialogProgressHighLightColor >= 0 && mDialogProgressNormalColor >= 0) {
            standardGSYVideoPlayer.setDialogProgressColor(mDialogProgressHighLightColor, mDialogProgressNormalColor);
        }
    }

    /**
     * 底部进度条-弹出的
     */
    public void setBottomShowProgressBarDrawable(Drawable drawable, Drawable thumb) {
        mBottomShowProgressDrawable = drawable;
        mBottomShowProgressThumbDrawable = thumb;
        if (mProgressBar != null) {
            mProgressBar.setProgressDrawable(drawable);
            mProgressBar.setThumb(thumb);
        }
    }

    /**
     * 底部进度条-非弹出
     */
    public void setBottomProgressBarDrawable(Drawable drawable) {
        mBottomProgressDrawable = drawable;
    }

    /**
     * 声音进度条
     */
    public void setDialogVolumeProgressBar(Drawable drawable) {
        mVolumeProgressDrawable = drawable;
    }


    /**
     * 中间进度条
     */
    public void setDialogProgressBar(Drawable drawable) {
        mDialogProgressBarDrawable = drawable;
    }

    /**
     * 中间进度条字体颜色
     */
    public void setDialogProgressColor(int highLightColor, int normalColor) {
        mDialogProgressHighLightColor = highLightColor;
        mDialogProgressNormalColor = normalColor;
    }


    /************************************* 关于截图的 ****************************************/

    /**
     * 获取截图
     */
    public void taskShotPic(GSYVideoShotListener gsyVideoShotListener) {
        this.taskShotPic(gsyVideoShotListener, false);
    }

    /**
     * 获取截图
     *
     * @param high 是否需要高清的
     */
    public void taskShotPic(GSYVideoShotListener gsyVideoShotListener, boolean high) {
        if (getCurrentPlayer().getRenderProxy() != null) {
            getCurrentPlayer().getRenderProxy().taskShotPic(gsyVideoShotListener, high);
        }
    }

    /**
     * 保存截图
     */
    public void saveFrame(final File file, GSYVideoShotSaveListener gsyVideoShotSaveListener) {
        saveFrame(file, false, gsyVideoShotSaveListener);
    }

    /**
     * 保存截图
     *
     * @param high 是否需要高清的
     */
    public void saveFrame(final File file, final boolean high,
                          final GSYVideoShotSaveListener gsyVideoShotSaveListener) {
        if (getCurrentPlayer().getRenderProxy() != null) {
            getCurrentPlayer().getRenderProxy().saveFrame(file, high, gsyVideoShotSaveListener);
        }
    }

    /**
     * 重新开启进度查询以及控制view消失的定时任务
     * 用于解决GSYVideoHelper中通过removeview方式做全屏切换导致的定时任务停止的问题
     * GSYVideoControlView   onDetachedFromWindow（）
     */
    public void restartTimerTask() {
        startProgressTimer();
        startDismissControlViewTimer();
    }

    public void setNoVideo() {
        rMobileData.setVisibility(VISIBLE);
        tiptv.setText(getResources().getString(R.string.no_vido));
        rMobileData.setBackgroundResource(R.drawable.camera_detail_mask);

        playAndRetryBtn.setVisibility(GONE);
        backMaskTv.setVisibility(VISIBLE);
        maskLayoutTop.setVisibility(VISIBLE);

    }


    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        updateVideTypeTag();

        /**
         * 横竖屏切换隐藏dialog
         */
        changeVideoFormatDialog.disMiss();
        if (null != selectDialog) {
            selectDialog.dismiss();
        }


        if (isLive == View.INVISIBLE) {
            cityChangePosition = false;

            mStateTv.setText(getResources().getString(R.string.live));
            mStateTv.setBackgroundResource(R.drawable.shape_bg_corner_2dp_29c_shadow);
        } else {
            cityChangePosition = true;

            mStateTv.setText(getResources().getString(R.string.gsy_video));
            mStateTv.setBackgroundResource(R.drawable.shape_bg_corner_2dp_f48f57_shadow);
        }


        audioIv.setChecked(isAudioChecked);


        setCityPlayState(cityPlayState);
        Toast.makeText(mContext, isAudioChecked + "", Toast.LENGTH_SHORT).show();


        //横竖屏切换，icon大小和间距动态调整

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            int dp20 = (int) (displayMetrics.density * 20 + 0.5f);
            int dp16 = (int) (displayMetrics.density * 16 + 0.5f);
            int dp12 = (int) (displayMetrics.density * 12 + 0.5f);

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) layoutBottomControlLl.getLayoutParams();
            lp.setMargins(dp16, dp12, dp16, dp12);

            layoutBottomControlLl.setLayoutParams(lp);


            LinearLayout.LayoutParams audioIvlayoutParams = (LinearLayout.LayoutParams) audioIv.getLayoutParams();
            LinearLayout.LayoutParams mStartButtonlayoutParams = (LinearLayout.LayoutParams) mStartButton.getLayoutParams();
            LinearLayout.LayoutParams mFullscreenButtonlayoutParams = (LinearLayout.LayoutParams) mFullscreenButton.getLayoutParams();


            audioIvlayoutParams.width = dp20;
            audioIvlayoutParams.height = dp20;
            mStartButtonlayoutParams.width = dp20;
            mStartButtonlayoutParams.height = dp20;
            mFullscreenButtonlayoutParams.width = dp20;
            mFullscreenButtonlayoutParams.height = dp20;


            audioIv.setLayoutParams(audioIvlayoutParams);
            mStartButton.setLayoutParams(mStartButtonlayoutParams);
            mFullscreenButton.setLayoutParams(mFullscreenButtonlayoutParams);
        } else {

            int dp24 = (int) (displayMetrics.density * 24 + 0.5f);


            int dp20 = (int) (displayMetrics.density * 20 + 0.5f);
            int dp16 = (int) (displayMetrics.density * 16 + 0.5f);

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) layoutBottomControlLl.getLayoutParams();
            lp.setMargins(dp20, dp16, dp20, dp16);

            layoutBottomControlLl.setLayoutParams(lp);
//
//
            LinearLayout.LayoutParams audioIvlayoutParams = (LinearLayout.LayoutParams) audioIv.getLayoutParams();
            LinearLayout.LayoutParams mStartButtonlayoutParams = (LinearLayout.LayoutParams) mStartButton.getLayoutParams();
            LinearLayout.LayoutParams mFullscreenButtonlayoutParams = (LinearLayout.LayoutParams) mFullscreenButton.getLayoutParams();


            audioIvlayoutParams.width = dp24;
            audioIvlayoutParams.height = dp24;
            mStartButtonlayoutParams.width = dp24;
            mStartButtonlayoutParams.height = dp24;
            mFullscreenButtonlayoutParams.width = dp24;
            mFullscreenButtonlayoutParams.height = dp24;


            audioIv.setLayoutParams(audioIvlayoutParams);
            mStartButton.setLayoutParams(mStartButtonlayoutParams);
            mFullscreenButton.setLayoutParams(mFullscreenButtonlayoutParams);
        }

    }

    /**
     * 视频格式标签
     */
    private void changeVideoFormat(int pos) {
        if (currentVideoFormat != pos) {
            if ((mCurrentState == GSYVideoPlayer.CURRENT_STATE_PLAYING
                    || mCurrentState == GSYVideoPlayer.CURRENT_STATE_PAUSE)) {

                if (null != iCityChangeUiVideoPlayerListener) {
                    iCityChangeUiVideoPlayerListener.OnchangeVideoFormat();
                }
                isChangeVideoFormat = true;
                String url = urlList.get(pos);
                if (!TextUtils.isEmpty(url)) {
                    /**
                     * 获取最后视频截图loading显示占位图
                     */
                    taskShotPic(bitmap -> {
                        if (null != bitmap) {
                            mCoverImage.setImageBitmap(bitmap);
                            mCoverImage.setVisibility(VISIBLE);
                        }

                    });
                    onVideoPause();
                    getGSYVideoManager().releaseMediaPlayer();
                    cancelProgressTimer();
                    hideAllWidget();
                    changeVideoFormatDialog.disMiss();
                    if (null != selectDialog) {
                        selectDialog.dismiss();
                    }
                    new Handler().postDelayed(() -> {
                        setUp(url, mCache, mCachePath, mTitle);
                        setSeekOnStart(0);
                        startPlayLogic();
                        cancelProgressTimer();
                        hideAllWidget();

                    }, 300);
                    /**
                     * 存储本地记录
                     */
                    ContextUtils.getContext().getSharedPreferences(Constants.PREFERENCE_VIDEO_FORMAT_SETTING_SP, Context.MODE_PRIVATE)
                            .edit().putInt(Constants.PREFERENCE_VIDEO_FORMAT_SETTING_KEY, pos).apply();
                    updateVideTypeTag();
                    currentVideoFormat = pos;
                }
            }
        }


    }


    /**
     * 更新视频格式tag
     */
    public void updateVideTypeTag() {
        currentVideoFormat = ContextUtils.getContext().getSharedPreferences(Constants.PREFERENCE_VIDEO_FORMAT_SETTING_SP, Context.MODE_PRIVATE)
                .getInt(Constants.PREFERENCE_VIDEO_FORMAT_SETTING_KEY, 0);
        if (isLive == View.INVISIBLE) {
            swVideoFormatTv.setVisibility(VISIBLE);

            if (currentVideoFormat == 0) {
                swVideoFormatTv.setText(mContext.getString(R.string.flv_format));
            } else if (currentVideoFormat == 1) {
                swVideoFormatTv.setText(mContext.getString(R.string.hls_format));

            }
        } else {
            swVideoFormatTv.setVisibility(GONE);

        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        try {
            getContext().unregisterReceiver(mVolumeReceiver);
        } catch (IllegalArgumentException e) {

        }
    }

    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();
        //TODO 控制播放状态
        setCityPlayState(4);

        if (GSYVideoManager.backFromWindowFull(mContext)) {
            return;
        }


    }


    @Override
    public void onError(int what, int extra) {
        super.onError(what, extra);

        GSYVideoManager.instance().pause();
        setCityPlayState(3);
    }

    @Override
    public void onVideoPause() {
        super.onVideoPause();

        changeVideoFormatDialog.disMiss();
    }

    @Override
    protected void releasePauseCover() {
        super.releasePauseCover();
    }

    @Override
    protected void releaseVideos() {
        super.releaseVideos();
        Repause.unregisterListener(this);

    }

    @Override
    protected void showPauseCover() {
        super.showPauseCover();
    }


    /**
     * 监听播放中，缓冲,切换视频流
     */
    public interface ICityChangeUiVideoPlayerListener {
        void OnCityChangeUiToPlayingShow();

        void OnchangeVideoFormat();

        void OnCityChangeUiToPlayingBufferingShow();
    }

}

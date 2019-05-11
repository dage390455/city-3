package com.shuyu.gsyvideoplayer.video;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.shuyu.gsyvideoplayer.R;
import com.shuyu.gsyvideoplayer.listener.GSYVideoShotListener;
import com.shuyu.gsyvideoplayer.listener.GSYVideoShotSaveListener;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.NetworkUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.io.File;

import moe.codeest.enviews.CityENDownloadView;
import moe.codeest.enviews.ENPlayView;

/**
 * 标准播放器，继承之后实现一些ui显示效果，如显示／隐藏ui，播放按键等
 * Created by shuyu on 2016/11/11.
 */

public class CityStandardGSYVideoPlayer extends StandardGSYVideoPlayer {

    private ImageView backMaskTv;
    private RelativeLayout maskLayoutTop;

    public TextView maskTitleTv;
    private RelativeLayout rMobileData;

    private static int isLive;
    private static boolean isShowMaskTopBack;
    /**
     * 1没网,2移动数据 3加载失败重试 4 播放完成重播 5 离线 6直播 7 录像
     */
    private static int cityPlayState;
    private static boolean isAudioChecked;


    private Button playAndRetryBtn;
    private ImageView maskFaceIv;

    private TextView tiptv;

    private ToggleButton audioIv;

    //亮度dialog
    protected Dialog mBrightnessDialog;

    //音量dialog
    protected Dialog mVolumeDialog;

    //触摸进度dialog
    protected Dialog mProgressDialog;
    //seekbar和触摸进度dialog
    protected Dialog mSeekProgressDialog;

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

    /**
     * 1.5.0开始加入，如果需要不同布局区分功能，需要重载
     */
    public CityStandardGSYVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public CityStandardGSYVideoPlayer(Context context) {
        super(context);
    }

    public CityStandardGSYVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        int dp48 = (int) (context.getResources().getDisplayMetrics().density * 48 + 0.5f);
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
     * 设置蒙版功能
     * 1没网,2移动数据 3加载失败重试 4 播放完成重播 5 离线
     *
     * @param cityState
     */
    public void setCityPlayState(int cityState) {

        cityPlayState = cityState;


        switch (cityPlayState) {

            case 1:

                tiptv.setText(getResources().getString(R.string.online_tip));
                playAndRetryBtn.setVisibility(GONE);
                rMobileData.setVisibility(VISIBLE);
                rMobileData.setBackgroundResource(R.drawable.camera_detail_mask);

                maskFaceIv.setVisibility(GONE);
                maskLayoutTop.setVisibility(VISIBLE);


                break;
            case 2:

                rMobileData.setVisibility(VISIBLE);
                playAndRetryBtn.setVisibility(VISIBLE);
                playAndRetryBtn.setText(R.string.play);
                rMobileData.setBackgroundColor(Color.TRANSPARENT);


                maskTitleTv.setText(mTitle);

                tiptv.setText(getResources().getString(R.string.mobile_network));
                maskLayoutTop.setVisibility(VISIBLE);
                playAndRetryBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        maskLayoutTop.setVisibility(GONE);
                        rMobileData.setVisibility(GONE);
                        cityPlayState = -1;

                        if (mVideoAllCallBack != null) {
                            Debuger.printfLog("onClickStartThumb");
                            mVideoAllCallBack.onClickStartThumb(mOriginUrl, mTitle, CityStandardGSYVideoPlayer.this);
                        }
                        prepareVideo();
                        startDismissControlViewTimer();

                    }
                });
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

                playAndRetryBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startPlayLogic();
                    }
                });
                break;
            case 4:

                tiptv.setText(getResources().getString(R.string.played));
                playAndRetryBtn.setText(getResources().getString(R.string.replay));
                playAndRetryBtn.setVisibility(VISIBLE);

                rMobileData.setVisibility(VISIBLE);
                maskFaceIv.setVisibility(GONE);

                rMobileData.setBackground(null);

                rMobileData.setBackgroundColor(Color.parseColor("#66000000"));
                playAndRetryBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setIsLive(View.VISIBLE);


                        startPlayLogic();


                    }
                });


                break;
            case 5:

                rMobileData.setVisibility(VISIBLE);
                playAndRetryBtn.setVisibility(VISIBLE);
                playAndRetryBtn.setText(R.string.retry);
                rMobileData.setBackgroundResource(R.drawable.camera_detail_mask);
                maskFaceIv.setVisibility(GONE);

                tiptv.setText(getResources().getString(R.string.offline));
                maskLayoutTop.setVisibility(VISIBLE);
                maskTitleTv.setText(mTitle);
                break;
            case 6:
                break;
            case 7:
                break;

            default:
                rMobileData.setVisibility(View.GONE);
                maskLayoutTop.setVisibility(View.GONE);
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
            mStateTv.setText(R.string.live);
            mStateTv.setBackgroundResource(R.drawable.shape_bg_corner_2dp_29c_shadow);
        } else {
            mStateTv.setText(R.string.gsy_video);
            mStateTv.setBackgroundResource(R.drawable.shape_bg_corner_2dp_f48f57_shadow);
        }

        CityStandardGSYVideoPlayer.isLive = isLive;
        hide();


    }

    /**
     * 蒙版返回箭头是否显示
     */
    public void setIsShowMaskTopBack(boolean isShowMaskTopBack) {

        CityStandardGSYVideoPlayer.isShowMaskTopBack = isShowMaskTopBack;
        if (!isShowMaskTopBack) {
            backMaskTv.setVisibility(GONE);
        }


    }

    public Button getPlayAndRetryBtn() {
        return playAndRetryBtn;
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

        maskFaceIv = findViewById(R.id.face_iv);
        rMobileData = findViewById(R.id.rl_mobile_data);
        playAndRetryBtn = findViewById(R.id.playa_retry_btn);

        backMaskTv = findViewById(R.id.mask_iv_back);
        maskTitleTv = findViewById(R.id.mask_title_tv);
        maskLayoutTop = findViewById(R.id.mask_layout_top);
        tiptv = findViewById(R.id.tip_data_tv);

        backMaskTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getContext() instanceof Activity) {
                    ((Activity) getContext()).finish();
                }
//                getContext().
            }
        });
        audioIv = findViewById(R.id.audio_iv);
        audioIv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                isAudioChecked = isChecked;
                if (isChecked) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                } else {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0);

                }
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

        maskLayoutTop.setVisibility(GONE);
        rMobileData.setVisibility(GONE);

        if ((!NetworkUtils.isAvailable(getContext()) || !NetworkUtils.isWifiConnected(getContext()))) {

            if (!NetworkUtils.isAvailable(getContext())) {
                setCityPlayState(1);

                return;
            }
            if (!NetworkUtils.isWifiConnected(getContext())) {
                setCityPlayState(2);

                return;

            }


        }


        if (mVideoAllCallBack != null) {
            Debuger.printfLog("onClickStartThumb");
            mVideoAllCallBack.onClickStartThumb(mOriginUrl, mTitle, CityStandardGSYVideoPlayer.this);
        }


        prepareVideo();
        startDismissControlViewTimer();
        cityPlayState = -1;


    }

    public void hide() {
        setViewShowState(mProgressBar, isLive);

        setViewShowState(mTotalTimeTextView, isLive);

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

//    private boolean isSeekTouch;
//
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        int id = v.getId();
//        if (id == R.id.progress) {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    isSeekTouch = true;
//
//                    break;
//                case MotionEvent.ACTION_UP:
//                    isSeekTouch = false;
//                    break;
//            }
//        }
//        return super.onTouch(v, event);
//    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        super.onProgressChanged(seekBar, progress, fromUser);
        if (fromUser) {
            int time = seekBar.getProgress() * getDuration() / 100;
            String seekTime = CommonUtil.stringForTime(time);

            showCityProgressDiallog(seekTime);


        }

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        super.onStopTrackingTouch(seekBar);
//        int time = seekBar.getProgress() * getDuration() / 100;
//        String seekTime = CommonUtil.stringForTime(time);
//
//        showCityProgressDiallog(seekTime);

//        if (mSeekProgressDialog == null) {
//            View localView = LayoutInflater.from(getActivityContext()).inflate(R.layout.city_seek_dilog, null);
//
//            seekDialogTv = localView.findViewById(R.id.city_seek_dialog_tv);
//            mSeekProgressDialog = new Dialog(getActivityContext(), R.style.video_style_dialog_progress);
//            mSeekProgressDialog.setContentView(localView);
//            mSeekProgressDialog.getWindow().addFlags(Window.FEATURE_ACTION_BAR);
//            mSeekProgressDialog.getWindow().addFlags(32);
//            mSeekProgressDialog.getWindow().addFlags(16);
//            mSeekProgressDialog.getWindow().setLayout(getWidth(), getHeight());
//            WindowManager.LayoutParams localLayoutParams = mSeekProgressDialog.getWindow().getAttributes();
//            localLayoutParams.gravity = Gravity.TOP;
//            localLayoutParams.width = getWidth();
//            localLayoutParams.height = getHeight();
//            int location[] = new int[2];
//            getLocationOnScreen(location);
//            localLayoutParams.x = location[0];
//            localLayoutParams.y = location[1];
//            mSeekProgressDialog.getWindow().setAttributes(localLayoutParams);
//        }
//        if (!mSeekProgressDialog.isShowing()) {
//            mSeekProgressDialog.show();
//        }
//        if (null != seekDialogTv) {
//            seekDialogTv.setText(seekTime);
//        }

    }

    /**
     * 触摸显示滑动进度dialog，如需要自定义继承重写即可，记得重写dismissProgressDialog
     */
    @Override
    @SuppressWarnings("ResourceType")
    protected void showProgressDialog(float deltaX, String seekTime,
                                      int seekTimePosition, String totalTime, int totalTimeDuration) {

        showCityProgressDiallog(seekTime);
//        if (mProgressDialog == null) {
//            View localView = LayoutInflater.from(getActivityContext()).inflate(getProgressDialogLayoutId(), null);
//            if (localView.findViewById(getProgressDialogProgressId()) instanceof ProgressBar) {
//                mDialogProgressBar = ((ProgressBar) localView.findViewById(getProgressDialogProgressId()));
//                if (mDialogProgressBarDrawable != null) {
//                    mDialogProgressBar.setProgressDrawable(mDialogProgressBarDrawable);
//                }
//            }
//            if (localView.findViewById(getProgressDialogCurrentDurationTextId()) instanceof TextView) {
//                mDialogSeekTime = ((TextView) localView.findViewById(getProgressDialogCurrentDurationTextId()));
//            }
//            if (localView.findViewById(getProgressDialogAllDurationTextId()) instanceof TextView) {
//                mDialogTotalTime = ((TextView) localView.findViewById(getProgressDialogAllDurationTextId()));
//            }
//            if (localView.findViewById(getProgressDialogImageId()) instanceof ImageView) {
//                mDialogIcon = ((ImageView) localView.findViewById(getProgressDialogImageId()));
//            }
//            mProgressDialog = new Dialog(getActivityContext(), R.style.video_style_dialog_progress);
//            mProgressDialog.setContentView(localView);
//            mProgressDialog.getWindow().addFlags(Window.FEATURE_ACTION_BAR);
//            mProgressDialog.getWindow().addFlags(32);
//            mProgressDialog.getWindow().addFlags(16);
//            mProgressDialog.getWindow().setLayout(getWidth(), getHeight());
//            if (mDialogProgressNormalColor != -11 && mDialogTotalTime != null) {
//                mDialogTotalTime.setTextColor(mDialogProgressNormalColor);
//            }
//            if (mDialogProgressHighLightColor != -11 && mDialogSeekTime != null) {
//                mDialogSeekTime.setTextColor(mDialogProgressHighLightColor);
//            }
//            WindowManager.LayoutParams localLayoutParams = mProgressDialog.getWindow().getAttributes();
//            localLayoutParams.gravity = Gravity.TOP;
//            localLayoutParams.width = getWidth();
//            localLayoutParams.height = getHeight();
//            int location[] = new int[2];
//            getLocationOnScreen(location);
//            localLayoutParams.x = location[0];
//            localLayoutParams.y = location[1];
//            mProgressDialog.getWindow().setAttributes(localLayoutParams);
//        }
//        if (!mProgressDialog.isShowing()) {
//            mProgressDialog.show();
//        }
//        if (mDialogSeekTime != null) {
//            mDialogSeekTime.setText(seekTime);
//        }
//        if (mDialogTotalTime != null) {
//            mDialogTotalTime.setText(" / " + totalTime);
//        }
//        if (totalTimeDuration > 0)
//            if (mDialogProgressBar != null) {
//                mDialogProgressBar.setProgress(seekTimePosition * 100 / totalTimeDuration);
//            }
//        if (deltaX > 0) {
//            if (mDialogIcon != null) {
//                mDialogIcon.setBackgroundResource(R.drawable.video_forward_icon);
//            }
//        } else {
//            if (mDialogIcon != null) {
//                mDialogIcon.setBackgroundResource(R.drawable.video_backward_icon);
//            }
//        }

    }

    /**
     * 显示气泡dialog
     *
     * @param seekTime
     */

    private void showCityProgressDiallog(String seekTime) {

        if (mSeekProgressDialog == null) {
            View localView = LayoutInflater.from(getActivityContext()).inflate(R.layout.city_seek_dilog, null);

            seekDialogTv = localView.findViewById(R.id.city_seek_dialog_tv);
            mSeekProgressDialog = new Dialog(getActivityContext(), R.style.video_style_dialog_progress);
            mSeekProgressDialog.setContentView(localView);
            mSeekProgressDialog.getWindow().addFlags(Window.FEATURE_ACTION_BAR);
            mSeekProgressDialog.getWindow().addFlags(32);
            mSeekProgressDialog.getWindow().addFlags(16);
            mSeekProgressDialog.getWindow().setLayout(getWidth(), getHeight());
            WindowManager.LayoutParams localLayoutParams = mSeekProgressDialog.getWindow().getAttributes();
            localLayoutParams.gravity = Gravity.TOP;
            localLayoutParams.width = getWidth();
            localLayoutParams.height = getHeight();
            int location[] = new int[2];
            getLocationOnScreen(location);
            localLayoutParams.x = location[0];
            localLayoutParams.y = location[1];
            mSeekProgressDialog.getWindow().setAttributes(localLayoutParams);
        }
        if (!mSeekProgressDialog.isShowing()) {
            mSeekProgressDialog.show();
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

        if (mSeekProgressDialog != null) {
            mSeekProgressDialog.dismiss();
            mSeekProgressDialog = null;
        }

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
        CityStandardGSYVideoPlayer sf = (CityStandardGSYVideoPlayer) from;
        CityStandardGSYVideoPlayer st = (CityStandardGSYVideoPlayer) to;
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
        GSYBaseVideoPlayer gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar);
        if (gsyBaseVideoPlayer != null) {
            CityStandardGSYVideoPlayer gsyVideoPlayer = (CityStandardGSYVideoPlayer) gsyBaseVideoPlayer;
            gsyVideoPlayer.setLockClickListener(mLockClickListener);
            gsyVideoPlayer.setNeedLockFull(isNeedLockFull());
            initFullUI(gsyVideoPlayer);
            //比如你自定义了返回案件，但是因为返回按键底层已经设置了返回事件，所以你需要在这里重新增加的逻辑
        }
        return gsyBaseVideoPlayer;
    }

    @Override
    protected void resolveNormalVideoShow(View oldF, ViewGroup vp, GSYVideoPlayer gsyVideoPlayer) {
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer);


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

        updateStartImage();
        if (mLoadingProgressBar instanceof CityENDownloadView) {
            ((CityENDownloadView) mLoadingProgressBar).reset();
        }
    }

    @Override
    protected void changeUiToPreparingShow() {
        Debuger.printfLog("changeUiToPreparingShow");

        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mBottomContainer, VISIBLE);
//        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(mLoadingProgressBar, VISIBLE);
        setViewShowState(mThumbImageViewLayout, INVISIBLE);
//        setViewShowState(mBottomProgressBar, INVISIBLE);
        setViewShowState(mLockScreen, GONE);

        if (mLoadingProgressBar instanceof CityENDownloadView) {
            CityENDownloadView enDownloadView = (CityENDownloadView) mLoadingProgressBar;
//            if (enDownloadView.getCurrentState() == ENDownloadView.STATE_PRE) {
            ((CityENDownloadView) mLoadingProgressBar).start();
//            }
        }
    }

    @Override
    protected void changeUiToPlayingShow() {
        Debuger.printfLog("changeUiToPlayingShow");

        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mBottomContainer, VISIBLE);
        setViewShowState(mStartButton, VISIBLE);
        setViewShowState(mLoadingProgressBar, INVISIBLE);
        setViewShowState(mThumbImageViewLayout, INVISIBLE);
//        setViewShowState(mBottomProgressBar, INVISIBLE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);

        if (mLoadingProgressBar instanceof CityENDownloadView) {
            ((CityENDownloadView) mLoadingProgressBar).reset();
        }
        updateStartImage();

        hide();
    }

    @Override
    protected void changeUiToPauseShow() {
        Debuger.printfLog("changeUiToPauseShow");

        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mBottomContainer, VISIBLE);
        setViewShowState(mStartButton, VISIBLE);
        setViewShowState(mLoadingProgressBar, INVISIBLE);
        setViewShowState(mThumbImageViewLayout, INVISIBLE);
//        setViewShowState(mBottomProgressBar, INVISIBLE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);

        if (mLoadingProgressBar instanceof CityENDownloadView) {
            ((CityENDownloadView) mLoadingProgressBar).reset();
        }
        updateStartImage();
        updatePauseCover();
        hide();
    }

    @Override
    protected void changeUiToPlayingBufferingShow() {
        Debuger.printfLog("changeUiToPlayingBufferingShow");

        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mBottomContainer, VISIBLE);
//        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(mLoadingProgressBar, VISIBLE);
        setViewShowState(mThumbImageViewLayout, INVISIBLE);
//        setViewShowState(mBottomProgressBar, INVISIBLE);
        setViewShowState(mLockScreen, GONE);

        if (mLoadingProgressBar instanceof CityENDownloadView) {
            CityENDownloadView enDownloadView = (CityENDownloadView) mLoadingProgressBar;
//            if (enDownloadView.getCurrentState() == ENDownloadView.STATE_PRE) {
            ((CityENDownloadView) mLoadingProgressBar).start();
//            }
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
//        setViewShowState(mBottomProgressBar, INVISIBLE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);

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
//        setViewShowState(mBottomProgressBar, INVISIBLE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);

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
//        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(mLoadingProgressBar, VISIBLE);
        setViewShowState(mThumbImageViewLayout, INVISIBLE);
//        setViewShowState(mBottomProgressBar, VISIBLE);
        setViewShowState(mLockScreen, GONE);

        if (mLoadingProgressBar instanceof CityENDownloadView) {
//            CityENDownloadView enDownloadView = (CityENDownloadView) mLoadingProgressBar;
//            if (enDownloadView.getCurrentState() == ENDownloadView.STATE_PRE) {
            ((CityENDownloadView) mLoadingProgressBar).start();
//            }
        }
        updateStartImage();
    }

    protected void changeUiToClear() {
        Debuger.printfLog("changeUiToClear");

        setViewShowState(mTopContainer, INVISIBLE);
        setViewShowState(mBottomContainer, INVISIBLE);
//        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(mLoadingProgressBar, INVISIBLE);
        setViewShowState(mThumbImageViewLayout, INVISIBLE);
//        setViewShowState(mBottomProgressBar, INVISIBLE);
        setViewShowState(mLockScreen, GONE);

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
//        setViewShowState(mBottomProgressBar, VISIBLE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);

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
    private void initFullUI(CityStandardGSYVideoPlayer standardGSYVideoPlayer) {

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
//        if (mBottomProgressBar != null) {
//            mBottomProgressBar.setProgressDrawable(drawable);
//        }
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
        playAndRetryBtn.setVisibility(GONE);
    }


    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        if (isLive == View.INVISIBLE) {
            mStateTv.setText(getResources().getString(R.string.live));
            mStateTv.setBackgroundResource(R.drawable.shape_bg_corner_2dp_29c_shadow);
        } else {
            mStateTv.setText(getResources().getString(R.string.gsy_video));
            mStateTv.setBackgroundResource(R.drawable.shape_bg_corner_2dp_f48f57_shadow);
        }

        audioIv.setChecked(isAudioChecked);


        setCityPlayState(cityPlayState);


    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }


    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();

        setCityPlayState(4);

    }

    @Override
    public void onError(int what, int extra) {
        super.onError(what, extra);


        setCityPlayState(3);

    }

    @Override
    protected void releaseVideos() {
        super.releaseVideos();

    }
}

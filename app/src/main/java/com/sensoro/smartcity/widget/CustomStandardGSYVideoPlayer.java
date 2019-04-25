package com.sensoro.smartcity.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.model.EventData;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYVideoShotListener;
import com.shuyu.gsyvideoplayer.listener.GSYVideoShotSaveListener;
import com.shuyu.gsyvideoplayer.utils.NetworkUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import static com.sensoro.smartcity.constant.Constants.NetworkInfo;
import static com.sensoro.smartcity.constant.Constants.PREFERENCE_LOGIN_NAME_PWD;

/**
 * 涉及生命横竖屏旋转
 */
public class CustomStandardGSYVideoPlayer extends StandardGSYVideoPlayer {
    private RelativeLayout rMobileData;

    public Button getPlayBtn() {
        return playBtn;
    }


    private Button playBtn;

    public Button getPlayRetryBtn() {
        return playRetryBtn;
    }

    private Button playRetryBtn;
    private TextView tiptv;

    public CustomStandardGSYVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public CustomStandardGSYVideoPlayer(Context context) {
        super(context);
    }

    public CustomStandardGSYVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 加载失败
     */
    public void changeRetryType() {
        rMobileData.setVisibility(VISIBLE);
        playBtn.setVisibility(GONE);
        playRetryBtn.setVisibility(VISIBLE);

        tiptv.setText(getResources().getString(R.string.retry_play));
    }

    /**
     * 移动网络
     */
    public void changeMobileType() {
        GSYVideoManager.onPause();
        rMobileData.setVisibility(VISIBLE);
        playBtn.setVisibility(VISIBLE);
        playRetryBtn.setVisibility(GONE);

        tiptv.setText(getResources().getString(R.string.mobile_network));
    }

    /**
     * 没有网
     */

    public void changeNoDataType() {
        GSYVideoManager.onPause();
        tiptv.setText(getResources().getString(R.string.online_tip));
        playBtn.setVisibility(GONE);
        rMobileData.setVisibility(VISIBLE);
        playRetryBtn.setVisibility(GONE);

    }

    /**
     * 底部进度条是否显示，直播不显示
     *
     * @param islive
     */
    public void changeBottomContainer(int islive) {

        SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_LOGIN_NAME_PWD, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("islive", islive);
        editor.apply();
//        if (islive == VISIBLE) {
//            mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_video_seek_progress));
//            mProgressBar.setThumb(getResources().getDrawable(R.drawable.video_seek_thumb));
//        } else {
//            mProgressBar.setProgressDrawable(null);
//            mProgressBar.setThumb(null);
//        }
        hide();
        invalidate();


    }


    /**
     * 网络改变状态
     *
     * @param eventData
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        if (code == NetworkInfo) {
            int data = (int) eventData.data;

            switch (data) {

                case ConnectivityManager.TYPE_WIFI:
                    if (rMobileData.getVisibility() == VISIBLE) {
                        rMobileData.setVisibility(GONE);
                        GSYVideoManager.onResume();
                    }

                    break;

                case ConnectivityManager.TYPE_MOBILE:

                    changeMobileType();

                    break;

                case -1:
                    changeNoDataType();


                    break;


            }
        }
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        EventBus.getDefault().register(this);


        rMobileData = findViewById(R.id.rl_mobile_data);
        playBtn = findViewById(R.id.play_btn);
        playRetryBtn = findViewById(R.id.playa_retry_btn);

        tiptv = findViewById(R.id.tip_data_tv);


        playBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rMobileData.getVisibility() == VISIBLE) {
                    rMobileData.setVisibility(GONE);
                    GSYVideoManager.onResume();
                }
            }
        });


    }

    @Override
    public int getLayoutId() {
        return R.layout.cus_video_layout_standard;
    }

    @Override
    public void startPlayLogic() {

        super.startPlayLogic();
        if (!NetworkUtils.isAvailable(getContext())) {
            changeNoDataType();
            return;
        }
        if (!NetworkUtils.isWifiConnected(getContext())) {
            changeMobileType();


            return;
        }
        rMobileData.setVisibility(GONE);


    }

    public void hide() {
        SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_LOGIN_NAME_PWD, Context
                .MODE_PRIVATE);
        int islive = sp.getInt("islive", 0);
        setViewShowState(mProgressBar, islive);
//        setViewShowState(mCurrentTimeTextView, islive);

        setViewShowState(mTotalTimeTextView, islive);
    }

    @Override
    protected void showWifiDialog() {
        super.showWifiDialog();


    }

    @Override
    protected void showProgressDialog(float deltaX, String seekTime, int seekTimePosition, String totalTime, int totalTimeDuration) {
        super.showProgressDialog(deltaX, seekTime, seekTimePosition, totalTime, totalTimeDuration);
    }

    @Override
    protected void dismissProgressDialog() {
        super.dismissProgressDialog();
    }

    @Override
    protected void showVolumeDialog(float deltaY, int volumePercent) {
        super.showVolumeDialog(deltaY, volumePercent);
    }

    @Override
    protected void dismissVolumeDialog() {
        super.dismissVolumeDialog();
    }

    @Override
    protected void showBrightnessDialog(float percent) {
        super.showBrightnessDialog(percent);
    }

    @Override
    protected void dismissBrightnessDialog() {
        super.dismissBrightnessDialog();
    }

    @Override
    protected void cloneParams(GSYBaseVideoPlayer from, GSYBaseVideoPlayer to) {
        super.cloneParams(from, to);
    }

    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        return super.startWindowFullscreen(context, actionBar, statusBar);
    }

    @Override
    protected void onClickUiToggle() {
        super.onClickUiToggle();
    }

    @Override
    protected void hideAllWidget() {
        super.hideAllWidget();
    }

    @Override
    protected void changeUiToNormal() {
        super.changeUiToNormal();
    }

    @Override
    protected void changeUiToPreparingShow() {
        super.changeUiToPreparingShow();


    }

    @Override
    protected void changeUiToPlayingShow() {
        super.changeUiToPlayingShow();
        hide();

    }

    @Override
    protected void changeUiToPauseShow() {
        super.changeUiToPauseShow();
        hide();

    }

    @Override
    protected void changeUiToPlayingBufferingShow() {
        super.changeUiToPlayingBufferingShow();
        hide();

    }

    @Override
    protected void changeUiToCompleteShow() {
        super.changeUiToCompleteShow();
        hide();

    }

    @Override
    protected void changeUiToError() {
        super.changeUiToError();
    }

    @Override
    protected int getProgressDialogLayoutId() {
        return super.getProgressDialogLayoutId();
    }

    @Override
    protected int getProgressDialogProgressId() {
        return super.getProgressDialogProgressId();
    }

    @Override
    protected int getProgressDialogCurrentDurationTextId() {
        return super.getProgressDialogCurrentDurationTextId();
    }

    @Override
    protected int getProgressDialogAllDurationTextId() {
        return super.getProgressDialogAllDurationTextId();
    }

    @Override
    protected int getProgressDialogImageId() {
        return super.getProgressDialogImageId();
    }

    @Override
    protected int getVolumeLayoutId() {
        return super.getVolumeLayoutId();
    }

    @Override
    protected int getVolumeProgressId() {
        return super.getVolumeProgressId();
    }

    @Override
    protected int getBrightnessLayoutId() {
        return super.getBrightnessLayoutId();
    }

    @Override
    protected int getBrightnessTextId() {
        return super.getBrightnessTextId();
    }

    @Override
    protected void changeUiToPrepareingClear() {
        super.changeUiToPrepareingClear();
    }

    @Override
    protected void changeUiToPlayingClear() {
        super.changeUiToPlayingClear();
    }

    @Override
    protected void changeUiToPauseClear() {
        super.changeUiToPauseClear();
    }

    @Override
    protected void changeUiToPlayingBufferingClear() {
        super.changeUiToPlayingBufferingClear();
    }

    @Override
    protected void changeUiToClear() {
        super.changeUiToClear();
    }

    @Override
    protected void changeUiToCompleteClear() {
        super.changeUiToCompleteClear();
    }

    @Override
    protected void updateStartImage() {
        super.updateStartImage();
    }

    @Override
    public void setBottomShowProgressBarDrawable(Drawable drawable, Drawable thumb) {
        super.setBottomShowProgressBarDrawable(drawable, thumb);
    }

    @Override
    public void setBottomProgressBarDrawable(Drawable drawable) {
        super.setBottomProgressBarDrawable(drawable);
    }

    @Override
    public void setDialogVolumeProgressBar(Drawable drawable) {
        super.setDialogVolumeProgressBar(drawable);
    }

    @Override
    public void setDialogProgressBar(Drawable drawable) {
        super.setDialogProgressBar(drawable);
    }

    @Override
    public void setDialogProgressColor(int highLightColor, int normalColor) {
        super.setDialogProgressColor(highLightColor, normalColor);
    }

    @Override
    public void taskShotPic(GSYVideoShotListener gsyVideoShotListener) {
        super.taskShotPic(gsyVideoShotListener);
    }

    @Override
    public void taskShotPic(GSYVideoShotListener gsyVideoShotListener, boolean high) {
        super.taskShotPic(gsyVideoShotListener, high);
    }

    @Override
    public void saveFrame(File file, GSYVideoShotSaveListener gsyVideoShotSaveListener) {
        super.saveFrame(file, gsyVideoShotSaveListener);
    }

    @Override
    public void saveFrame(File file, boolean high, GSYVideoShotSaveListener gsyVideoShotSaveListener) {
        super.saveFrame(file, high, gsyVideoShotSaveListener);
    }

    @Override
    public void restartTimerTask() {
        super.restartTimerTask();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        hide(INVISIBLE);

        SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_LOGIN_NAME_PWD, Context
                .MODE_PRIVATE);
        int islive = sp.getInt("islive", 0);
        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向

        hide();
//        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
////            setViewShowState(mProgressBar, INVISIBLE);
//            //横屏
////            hide(INVISIBLE);
//            if (islive == VISIBLE) {
//                mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_video_seek_progress));
//                mProgressBar.setThumb(getResources().getDrawable(R.drawable.video_seek_thumb));
//            } else {
//                mProgressBar.setProgressDrawable(null);
//                mProgressBar.setThumb(null);
//            }
//        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
//            //竖屏
//            if (islive == VISIBLE) {
//                mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_video_seek_progress));
//                mProgressBar.setThumb(getResources().getDrawable(R.drawable.video_seek_thumb));
//            } else {
//                mProgressBar.setProgressDrawable(null);
//                mProgressBar.setThumb(null);
//            }

//        }


    }

//
//    @Nullable
//    @Override
//    protected Parcelable onSaveInstanceState() {
//        super.onSaveInstanceState();
//
//        Bundle bundle = new Bundle();
//        bundle.putInt("islive", islive);
//        return bundle;
//
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Parcelable state) {
//
//        Bundle bundle = (Bundle) state;
//        islive = bundle.getInt("islive");
//        super.onRestoreInstanceState(bundle);
//    }
}

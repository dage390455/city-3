package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.gyf.immersionbar.ImmersionBar;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.utils.LogUtils;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.imainviews.ICameraPersonDetailActivityView;
import com.sensoro.smartcity.presenter.CameraPersonDetailActivityPresenter;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.NetworkUtils;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.CityStandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CameraPersonDetailActivity extends BaseActivity<ICameraPersonDetailActivityView, CameraPersonDetailActivityPresenter>
        implements ICameraPersonDetailActivityView {
    @BindView(R.id.include_imv_title_imv_arrows_left)
    ImageView includeImvTitleImvArrowsLeft;
    @BindView(R.id.include_imv_title_tv_title)
    TextView includeImvTitleTvTitle;
    @BindView(R.id.include_imv_title_imv_subtitle)
    ImageView includeImvTitleImvSubtitle;
    @BindView(R.id.include_imv_title_imv_cl_root)
    ConstraintLayout includeImvTitleImvClRoot;
    @BindView(R.id.view_top_ac_camera_person_detail)
    View viewTopAcCameraPersonDetail;
    @BindView(R.id.gsy_player_ac_camera_person_detailq)
    CityStandardGSYVideoPlayer gsyPlayerAcCameraPersonDetail;
    private OrientationUtils orientationUtils;
    private ImageView imageView;
    private GSYVideoOptionBuilder gsyVideoOption;
    private boolean isPlay;
    private boolean isPause;
    private ProgressUtils mProgressUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_camera_person_detail);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        includeImvTitleImvSubtitle.setVisibility(View.GONE);

        initViewHeight();
        initGsyVideo();


    }

    private void initViewHeight() {
        int resourceId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            int result = this.getResources().getDimensionPixelSize(resourceId);
            ViewGroup.LayoutParams lp = viewTopAcCameraPersonDetail.getLayoutParams();
            lp.height = result;
            viewTopAcCameraPersonDetail.setLayoutParams(lp);
        }
    }

    private void initGsyVideo() {
        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, gsyPlayerAcCameraPersonDetail);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);
//        getCurPlay().getTitleTextView().setVisibility(View.VISIBLE);
//        //设置返回键
//        getCurPlay().getBackButton().setVisibility(View.VISIBLE);

        getCurPlay().setEnlargeImageRes(R.drawable.ic_camera_full_screen);

        getCurPlay().setShrinkImageRes(R.drawable.video_shrink);

        getCurPlay().getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                orientationUtils.resolveByClick();
                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                getCurPlay().startWindowFullscreen(mActivity, true, true);
            }
        });

        getCurPlay().getBackButton().setVisibility(View.INVISIBLE);
        initVideoOption();
    }

    private GSYVideoPlayer getCurPlay() {
        if (gsyPlayerAcCameraPersonDetail.getFullWindowPlayer() != null) {
            return gsyPlayerAcCameraPersonDetail.getFullWindowPlayer();
        }
        return gsyPlayerAcCameraPersonDetail;
    }

    public void initVideoOption() {
        gsyPlayerAcCameraPersonDetail.setIsLive(View.VISIBLE);
        gsyPlayerAcCameraPersonDetail.setICityChangeUiVideoPlayerListener(new CityStandardGSYVideoPlayer.ICityChangeUiVideoPlayerListener() {
            @Override
            public void OnCityChangeUiToPlayingShow() {
                orientationUtils.setEnable(true);

            }

            @Override
            public void OnCityChangeUiToPlayingBufferingShow() {
                orientationUtils.setEnable(false);

            }

            @Override
            public void OnchangeVideoFormat() {
                orientationUtils.setEnable(false);
            }
        });
        //增加封面
        if (imageView == null) {
            imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(R.drawable.camera_detail_mask);
        }


        gsyVideoOption = new GSYVideoOptionBuilder();
        gsyVideoOption.setThumbImageView(imageView)
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setAutoFullWithSize(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
//                .setUrl(url)
                .setCacheWithPlay(false)
//                .setVideoTitle("测试视频")
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        //开始播放了才能旋转和全屏
                        orientationUtils.setEnable(true);
                        isPlay = true;
                        isPause = false;
                    }

                    @Override
                    public void onPlayError(final String url, Object... objects) {
                        gsyPlayerAcCameraPersonDetail.setCityPlayState(3);
                        orientationUtils.setEnable(false);
                        backFromWindowFull();
                        gsyPlayerAcCameraPersonDetail.getPlayAndRetryBtn().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gsyVideoOption.setUrl(url).build(getCurPlay());
                                getCurPlay().startPlayLogic();
                            }
                        });
                    }

                    @Override
                    public void onAutoComplete(String url, Object... objects) {
                        super.onAutoComplete(url, objects);
                        orientationUtils.setEnable(false);
                        backFromWindowFull();


                    }
                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        if (gsyPlayerAcCameraPersonDetail.getCurrentState() != GSYVideoView.CURRENT_STATE_PLAYING) {
                            orientationUtils.setEnable(false);
                        }
                        if (orientationUtils != null) {
                            orientationUtils.backToProtVideo();
                        }
                    }
                }).setLockClickListener(new LockClickListener() {
            @Override
            public void onClick(View view, boolean lock) {
                if (orientationUtils != null) {
                    //配合下方的onConfigurationChanged
                    orientationUtils.setEnable(!lock);
                }
            }
        }).build(getCurPlay());
        getCurPlay().getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                orientationUtils.resolveByClick();

                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                getCurPlay().startWindowFullscreen(mActivity, true, true);
            }
        });
//        getCurPlay().getBackButton().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//        getCurPlay().startPlayLogic();
    }

    @Override
    public boolean setMyCurrentActivityTheme() {
        setTheme(R.style.Theme_AppCompat_Translucent);
        return true;
    }

    @Override
    public boolean setMyCurrentStatusBar() {
        immersionBar = ImmersionBar.with(mActivity);
        immersionBar
                .statusBarColor(R.color.white)
                .statusBarDarkFont(true)
                .init();
        return true;
    }

    @Override
    public void startPlayLogic(final String url1) {
        if ((!NetworkUtils.isAvailable(mActivity) || !NetworkUtils.isWifiConnected(mActivity))) {
            setVerOrientationUtil(false);
        }
        gsyVideoOption.setUrl(url1).build(getCurPlay());
        gsyPlayerAcCameraPersonDetail.setIsLive(View.VISIBLE);
        gsyPlayerAcCameraPersonDetail.setIsShowMaskTopBack(false);
        getCurPlay().startPlayLogic();

    }

    @Override
    public void playError(String errorMsg) {
        orientationUtils.setEnable(false);

        gsyPlayerAcCameraPersonDetail.setCityPlayState(3);
        gsyPlayerAcCameraPersonDetail.getPlayAndRetryBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.doRetry();


            }
        });
    }

    @Override
    public void setTitle(String time) {
        includeImvTitleTvTitle.setText(time);
    }

    @Override
    public ImageView getImageView() {
        return imageView;
    }

    @Override
    public CityStandardGSYVideoPlayer getPlayView() {
        return gsyPlayerAcCameraPersonDetail;
    }

    @Override
    public void setVerOrientationUtil(boolean b) {
        if (!b) {
            isPause = true;
        } else {
            isPause = false;

        }
        if (orientationUtils != null) {
            orientationUtils.setEnable(b);
        }
    }

    @Override
    public void backFromWindowFull() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
        orientationUtils.setEnable(false);
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
    }


    @Override
    protected CameraPersonDetailActivityPresenter createPresenter() {
        return new CameraPersonDetailActivityPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPause = false;
        GSYVideoManager.onResume();
    }

    @Override
    protected void onPause() {

        super.onPause();
        isPause = true;
        GSYVideoManager.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPlay) {
            getCurPlay().release();
        }


        if (orientationUtils != null)
            orientationUtils.releaseListener();

        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
        }


        GSYVideoManager.releaseAllVideos();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause && orientationUtils.isEnable()) {

            try {
                LogUtils.logd("==onConfigurationChanged=222222=====" + gsyPlayerAcCameraPersonDetail.getCurrentState());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            getCurPlay().onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }

    }

    @OnClick(R.id.include_imv_title_imv_arrows_left)
    public void onViewClicked() {
        finishAc();
    }

    @Override
    public void startAC(Intent intent) {

    }

    @Override
    public void finishAc() {
        mActivity.finish();
    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {

    }

    @Override
    public void setIntentResult(int resultCode) {

    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {

    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgressDialog() {
        if (mProgressUtils != null) {
            mProgressUtils.showProgress();
        }
    }

    @Override
    public void dismissProgressDialog() {
        if (mProgressUtils != null) {
            mProgressUtils.dismissProgress();
        }
    }
}

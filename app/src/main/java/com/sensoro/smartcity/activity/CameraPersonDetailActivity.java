package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.ICameraPersonDetailActivityView;
import com.sensoro.smartcity.presenter.CameraPersonDetailActivityPresenter;
import com.sensoro.smartcity.widget.CustomStandardGSYVideoPlayer;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.toast.SensoroToast;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.NetworkUtils;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

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
    @BindView(R.id.gsy_player_ac_camera_person_detail)
    CustomStandardGSYVideoPlayer gsyPlayerAcCameraPersonDetail;
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
        initGsyVideo();


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

        getCurPlay().setShrinkImageRes(R.drawable.ic_camera_full_screen);

        getCurPlay().getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                orientationUtils.resolveByClick();
                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                getCurPlay().startWindowFullscreen(mActivity, true, true);
            }
        });

        initVideoOption();
    }

    private GSYVideoPlayer getCurPlay() {
        if (gsyPlayerAcCameraPersonDetail.getFullWindowPlayer() != null) {
            return gsyPlayerAcCameraPersonDetail.getFullWindowPlayer();
        }
        return gsyPlayerAcCameraPersonDetail;
    }

    public void initVideoOption() {
        gsyPlayerAcCameraPersonDetail.changeBottomContainer(View.INVISIBLE);

        //增加封面
        if (imageView == null) {
            imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(R.mipmap.ic_launcher);
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
                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
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
        setTheme(R.style.Theme_AppCompat_Full);
        return true;
    }

    @Override
    public boolean isActivityOverrideStatusBar() {
        immersionBar = ImmersionBar.with(mActivity);
        immersionBar.transparentStatusBar().init();
        return true;
    }

    @Override
    public void startPlayLogic(final String url1) {
        if (!NetworkUtils.isAvailable(this)) {
            orientationUtils.setEnable(false);
            return;
        }

        if (!NetworkUtils.isWifiConnected(this)) {
            orientationUtils.setEnable(false);
            gsyPlayerAcCameraPersonDetail.getPlayBtn().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gsyVideoOption.setUrl(url1).build(getCurPlay());
                    getCurPlay().startPlayLogic();

                }
            });
            return;
        }
        gsyPlayerAcCameraPersonDetail.changeBottomContainer(View.VISIBLE);

        gsyVideoOption.setUrl(url1).build(getCurPlay());
        getCurPlay().startPlayLogic();
        orientationUtils.setEnable(true);

    }

    @Override
    public void playError(String errorMsg) {
        orientationUtils.setEnable(false);

        gsyPlayerAcCameraPersonDetail.changeRetryType();
        gsyPlayerAcCameraPersonDetail.getPlayRetryBtn().setOnClickListener(new View.OnClickListener() {
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
    protected CameraPersonDetailActivityPresenter createPresenter() {
        return new CameraPersonDetailActivityPresenter();
    }

    @Override
    protected void onResume() {
        getCurPlay().onVideoResume(false);
        super.onResume();
        isPause = false;
    }

    @Override
    protected void onPause() {
        getCurPlay().onVideoPause();
        super.onPause();
        isPause = true;
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
        if (isPlay && !isPause) {
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

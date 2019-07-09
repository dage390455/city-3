package com.sensoro.city_camera.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.gyf.immersionbar.ImmersionBar;
import com.sensoro.city_camera.IMainViews.ISecurityRecordDetailActivityView;
import com.sensoro.city_camera.R;
import com.sensoro.city_camera.R2;
import com.sensoro.city_camera.presenter.SecurityRecordDetailActivityPresenter;
import com.sensoro.city_camera.widget.AiGSYVideoPlayer;
import com.sensoro.city_camera.widget.AiGSYVideoPlayerUtil;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.common.widgets.VideoDownloadDialogUtils;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.NetworkUtils;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.CityStandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author bin.tian
 */
public class SecurityWarnRecordDetailActivity
        extends BaseActivity<ISecurityRecordDetailActivityView, SecurityRecordDetailActivityPresenter>
        implements ISecurityRecordDetailActivityView, AiGSYVideoPlayerUtil.CaptureClickListener,
        AiGSYVideoPlayerUtil.DownloadClickListener, VideoDownloadDialogUtils.TipDialogUtilsClickListener {

    @BindView(R2.id.include_imv_title_imv_arrows_left)
    ImageView includeImvTitleImvArrowsLeft;
    @BindView(R2.id.include_imv_title_tv_title)
    TextView includeImvTitleTvTitle;
    @BindView(R2.id.include_imv_title_imv_cl_root)
    ConstraintLayout includeImvTitleImvClRoot;
    @BindView(R2.id.view_top_ac_camera_person_detail)
    View viewTopAcCameraPersonDetail;
    @BindView(R2.id.gsy_player_ac_camera_person_detailq)
    AiGSYVideoPlayer gsyPlayerAcCameraPersonDetail;

    private OrientationUtils orientationUtils;
    private ImageView imageView;
    private GSYVideoOptionBuilder gsyVideoOption;
    private boolean isPlay;
    private boolean isPause;
    private ProgressUtils mProgressUtils;
    private VideoDownloadDialogUtils mDownloadUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_security_warn_record_detail);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    @Override
    protected SecurityRecordDetailActivityPresenter createPresenter() {
        return new SecurityRecordDetailActivityPresenter();
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());

        initViewHeight();
        initGsyVideo();

        AiGSYVideoPlayerUtil.getInstance().setCaptureClickListener(this);
        AiGSYVideoPlayerUtil.getInstance().setDownloadClickListener(this);

        mDownloadUtils = new VideoDownloadDialogUtils(mActivity);
        mDownloadUtils.setTipDialogUtilsClickListener(this);
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

        //增加封面
        if (imageView == null) {
            imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setImageResource(R.mipmap.ic_launcher);
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
        setTheme(R.style.Theme_AppCompat_Translucent);
        return true;
    }

    @Override
    public boolean isActivityOverrideStatusBar() {
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
            orientationUtils.setEnable(false);
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
    }

    @Override
    public void onVideoResume() {
        onResume();
    }

    @Override
    public void onVideoPause() {
        onPause();
    }

    @Override
    public void capture(File file) {
        gsyPlayerAcCameraPersonDetail.doCapture(file, (success, file1) -> {
            if (success) {
                toastShort(getString(R.string.capture_security_warn_record_success));
                mPresenter.onCaptureFinished(file);
            } else {
                toastShort(getString(R.string.capture_security_warn_record_fail));
            }
        });
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


        if (orientationUtils != null) {
            orientationUtils.releaseListener();
        }

        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
        }

        if (mDownloadUtils != null) {
            mDownloadUtils.destroy();
        }

        GSYVideoManager.releaseAllVideos();
        AiGSYVideoPlayerUtil.getInstance().clearListener();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause && orientationUtils.isEnable()) {
            getCurPlay().onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }

    }

    @OnClick({R2.id.include_imv_title_imv_arrows_left, R2.id.vertical_download_iv, R2.id.vertical_capture_iv})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.include_imv_title_imv_arrows_left) {
            finishAc();
        } else if (i == R.id.vertical_download_iv) {
            mPresenter.showDownloadDialog();
        } else if (i == R.id.vertical_capture_iv) {
            mPresenter.doCapture();
        }

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

    @Override
    public void onCaptureClick() {
        mPresenter.doCapture();
    }

    @Override
    public void onDownloadClick() {
        mPresenter.showDownloadDialog();
    }

    @Override
    public void showDownloadDialog(String videoSize) {
        if (mDownloadUtils != null) {
            mDownloadUtils.show(videoSize);
        }
    }

    @Override
    public void onCancelClick(boolean isCancelDownload) {
        if (mDownloadUtils != null) {
            mDownloadUtils.dismiss();
            if (isCancelDownload) {
                mPresenter.doDownloadCancel();
            }
        }
    }

    @Override
    public void onConfirmClick() {
        mPresenter.doDownload();
    }

    @Override
    public void setDownloadStartState(String videoSize) {
        if (mDownloadUtils.isShowing()) {
            mDownloadUtils.setDownloadStartState(videoSize);
        }
    }

    @Override
    public void updateDownLoadProgress(int progress, String totalBytesRead, String fileSize) {
        if (mDownloadUtils.isShowing()) {
            Log.d("updateDownLoadProgress", "updateDownLoadProgress: " + progress);
            mDownloadUtils.updateDownLoadProgress(progress, totalBytesRead, fileSize);
        }
    }

    @Override
    public void doDownloadFinish() {
        if (mDownloadUtils.isShowing()) {
            mDownloadUtils.doDownloadFinish();
        }
    }

    @Override
    public void setDownloadErrorState() {
        if (mDownloadUtils.isShowing()) {
            mDownloadUtils.setDownloadErrorState();
        }
    }
}

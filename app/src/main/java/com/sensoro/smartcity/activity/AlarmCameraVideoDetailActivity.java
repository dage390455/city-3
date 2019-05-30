package com.sensoro.smartcity.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gyf.barlibrary.ImmersionBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.server.response.AlarmCloudVideoRsp;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.AlarmCameraVideoDetailAdapter;
import com.sensoro.smartcity.imainviews.IAlarmCameraVideoDetailActivityView;
import com.sensoro.smartcity.presenter.AlarmCameraVideoDetailActivityPresenter;
import com.sensoro.smartcity.widget.dialog.VideoDownloadDialogUtils;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.CityStandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AlarmCameraVideoDetailActivity extends BaseActivity<IAlarmCameraVideoDetailActivityView,
        AlarmCameraVideoDetailActivityPresenter> implements IAlarmCameraVideoDetailActivityView, VideoDownloadDialogUtils.TipDialogUtilsClickListener {
    @BindView(R.id.view_top_ac_alarm_camera_video_detail)
    View viewTopAcAlarmCameraVideoDetail;
    @BindView(R.id.include_imv_title_imv_arrows_left)
    ImageView includeImvTitleImvArrowsLeft;
    @BindView(R.id.include_imv_title_tv_title)
    TextView includeImvTitleTvTitle;
    @BindView(R.id.include_imv_title_imv_subtitle)
    ImageView includeImvTitleImvSubtitle;
    @BindView(R.id.gsy_player_ac_alarm_camera_video_detail)
    CityStandardGSYVideoPlayer gsyPlayerAcAlarmCameraVideoDetail;
    @BindView(R.id.tv_time_title_ac_alarm_camera_video_detail)
    TextView tvTimeTitleAcAlarmCameraVideoDetail;
    @BindView(R.id.tv_time_ac_alarm_camera_video_detail)
    TextView tvTimeAcAlarmCameraVideoDetail;
    @BindView(R.id.view_divider_ac_alarm_camera_video_detail)
    View viewDividerAcAlarmCameraVideoDetail;
    @BindView(R.id.no_content)
    ImageView noContent;
    @BindView(R.id.no_content_tip)
    TextView noContentTip;
    @BindView(R.id.ic_no_content)
    LinearLayout icNoContent;
    @BindView(R.id.rv_list_include)
    RecyclerView rvListInclude;
    @BindView(R.id.refreshLayout_include)
    SmartRefreshLayout refreshLayoutInclude;
    @BindView(R.id.return_top_include)
    ImageView returnTopInclude;
    private ProgressUtils mProgressUtils;
    private Animation returnTopAnimation;
    private AlarmCameraVideoDetailAdapter mListAdapter;
    private OrientationUtils orientationUtils;
    private ImageView ivGsyCover;
    private GSYVideoOptionBuilder gsyVideoOption;
    private boolean isPlay;
    private boolean isPause;
    private VideoDownloadDialogUtils mDownloadUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_alarm_camera_video_detail);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());

        mDownloadUtils = new VideoDownloadDialogUtils(mActivity);
        mDownloadUtils.setTipDialogUtilsClickListener(this);


        includeImvTitleTvTitle.setText(mActivity.getString(R.string.alarm_video));
        includeImvTitleImvSubtitle.setVisibility(GONE);

        initSmartRefresh();

        returnTopAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.return_top_in_anim);
        returnTopInclude.setAnimation(returnTopAnimation);
        returnTopInclude.setVisibility(GONE);

        initRvList();

        initViewHeight();

        initGsyPlayer();
    }

    private void initSmartRefresh() {
        refreshLayoutInclude.setEnableAutoLoadMore(false);//开启自动加载功能（非必须）
        refreshLayoutInclude.setEnableLoadMore(false);
        refreshLayoutInclude.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                mPresenter.doRefresh();
            }
        });


    }

    private void initGsyPlayer() {
        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, gsyPlayerAcAlarmCameraVideoDetail);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);
        gsyPlayerAcAlarmCameraVideoDetail.setIsLive(View.INVISIBLE);

        //增加封面
        if (ivGsyCover == null) {
            ivGsyCover = new ImageView(this);
            ivGsyCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setImageResource(R.mipmap.ic_launcher);
        }
        gsyVideoOption = new GSYVideoOptionBuilder();
        gsyVideoOption.setThumbImageView(ivGsyCover)
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setAutoFullWithSize(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
//                .setUrl(url)
                .setCacheWithPlay(false)
//                .setVideoTitle(cameraName)
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPlayError(final String url, Object... objects) {
                        orientationUtils.setEnable(false);

                        backFromWindowFull();
                    }

                    @Override
                    public void onAutoComplete(final String url, Object... objects) {
                        orientationUtils.setEnable(false);

                        backFromWindowFull();
//
                    }

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

        //增加title
        getCurPlay().getTitleTextView().setVisibility(GONE);
        //设置返回键
        getCurPlay().getBackButton().setVisibility(GONE);

        getCurPlay().getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        gsyPlayerAcAlarmCameraVideoDetail.setIsLive(VISIBLE);
        getCurPlay().setEnlargeImageRes(R.drawable.ic_camera_full_screen);

        getCurPlay().setShrinkImageRes(R.drawable.video_shrink);

        getCurPlay().getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                orientationUtils.resolveByClick();
                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                getCurPlay().startWindowFullscreen(AlarmCameraVideoDetailActivity.this, true, true);
            }
        });

        getPlayView().setIsShowBackMaskTv(false);

    }

    private GSYBaseVideoPlayer getCurPlay() {
        if (gsyPlayerAcAlarmCameraVideoDetail.getFullWindowPlayer() != null) {
            return gsyPlayerAcAlarmCameraVideoDetail.getFullWindowPlayer();
        }
        return gsyPlayerAcAlarmCameraVideoDetail;
    }

    private void initViewHeight() {
        int resourceId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            int result = this.getResources().getDimensionPixelSize(resourceId);
            ViewGroup.LayoutParams lp = viewTopAcAlarmCameraVideoDetail.getLayoutParams();
            lp.height = result;
            viewTopAcAlarmCameraVideoDetail.setLayoutParams(lp);
        }
    }

    private void initRvList() {
        mListAdapter = new AlarmCameraVideoDetailAdapter(mActivity);
        mListAdapter.setOnAlarmCameraVideoItemClickListener(new AlarmCameraVideoDetailAdapter.AlarmCameraVideoClickListener() {

            @Override
            public void OnAlarmCameraVideoItemClick(AlarmCloudVideoRsp.DataBean.MediasBean bean) {
                mPresenter.doItemClick(bean);
            }

            @Override
            public void onAlarmCameraVideoDownloadClick(AlarmCloudVideoRsp.DataBean.MediasBean bean) {
                if (mDownloadUtils != null) {
                    mPresenter.setDownloadBean(bean);
                    mDownloadUtils.show(bean.getVideoSize());
                }
            }
        });
        final LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvListInclude.setLayoutManager(manager);
        rvListInclude.setAdapter(mListAdapter);

        rvListInclude.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//

                if (manager.findFirstVisibleItemPosition() > 4) {
                    if (newState == 0) {
                        returnTopInclude.setVisibility(VISIBLE);
                        if (returnTopAnimation != null && returnTopAnimation.hasEnded()) {
                            returnTopInclude.startAnimation(returnTopAnimation);
                        }
                    } else {
                        returnTopInclude.setVisibility(GONE);
                    }
                } else {
                    returnTopInclude.setVisibility(GONE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });

    }

    @Override
    protected AlarmCameraVideoDetailActivityPresenter createPresenter() {
        return new AlarmCameraVideoDetailActivityPresenter();
    }

    @Override
    protected void onPause() {
//        getCurPlay().onVideoPause();
        super.onPause();
        isPause = true;
        GSYVideoManager.onPause();
        Log.d("======", "onPause");


    }

    @Override
    public boolean isActivityOverrideStatusBar() {
        immersionBar = ImmersionBar.with(mActivity);
        immersionBar.statusBarDarkFont(true).statusBarColor(R.color.white).init();
        return true;
    }

    @Override
    public boolean setMyCurrentActivityTheme() {
        setTheme(R.style.Theme_AppCompat_Translucent);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏


        if (isPlay && !isPause && orientationUtils.isEnable()) {
            getCurPlay().onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
    }


    @Override
    public void onPullRefreshComplete() {
        refreshLayoutInclude.finishRefresh();
        refreshLayoutInclude.finishLoadMore();
    }

    @Override
    public void setImage(Drawable resource) {
        if (ivGsyCover != null) {
            ivGsyCover.setImageDrawable(resource);
        } else {
            ivGsyCover = new ImageView(this);
            ivGsyCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ivGsyCover.setImageDrawable(resource);
        }
        gsyPlayerAcAlarmCameraVideoDetail.setMobileFace(resource);

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

    @Override
    public void setPlayVideoTime(String s) {
        tvTimeAcAlarmCameraVideoDetail.setText(s);
    }

    @Override
    public void onVideoPause() {

        onPause();
    }

    @Override
    public void onVideoResume() {

        onResume();

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
    public void setVerOrientationUtil(boolean enable) {


        if (!enable) {
            isPause = true;
        } else {
            isPause = false;

        }
        if (orientationUtils != null) {
            orientationUtils.setEnable(enable);
        }
    }

    @Override
    public CityStandardGSYVideoPlayer getPlayView() {
        return gsyPlayerAcAlarmCameraVideoDetail;
    }

    @Override
    public void updateData(ArrayList<AlarmCloudVideoRsp.DataBean.MediasBean> mList) {
        mListAdapter.updateData(mList);
        setNoContentVisible(mList == null || mList.size() < 1);
    }

    private void setNoContentVisible(boolean isVisible) {
        icNoContent.setVisibility(isVisible ? VISIBLE : GONE);
        rvListInclude.setVisibility(isVisible ? GONE : VISIBLE);
    }

    @Override
    public void doPlayLive(final String url) {
        gsyVideoOption.setUrl(url).build(getCurPlay());
        getCurPlay().startPlayLogic();


    }

    @Override
    public void onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (orientationUtils != null)
            orientationUtils.releaseListener();

        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
        }

        if (mDownloadUtils != null) {
            mDownloadUtils.destory();
        }

        if (isPlay) {
            getCurPlay().release();
        }

        GSYVideoManager.releaseAllVideos();
    }

    @Override
    protected void onResume() {
//        getCurPlay().onVideoResume();
        super.onResume();
        isPause = false;
        GSYVideoManager.onResume();

        Log.d("======", "onResume");

    }

    @OnClick({R.id.include_imv_title_imv_arrows_left, R.id.return_top_include})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_imv_title_imv_arrows_left:
                mActivity.finish();
                break;
            case R.id.return_top_include:
                rvListInclude.smoothScrollToPosition(0);
                returnTopInclude.setVisibility(GONE);
                refreshLayoutInclude.closeHeaderOrFooter();
                break;
        }

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
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_LONG).show();
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


}

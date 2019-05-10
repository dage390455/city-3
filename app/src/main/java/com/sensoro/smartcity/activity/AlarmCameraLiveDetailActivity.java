package com.sensoro.smartcity.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.CameraLiveDetailAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IAlarmCameraLiveDetailActivityView;
import com.sensoro.smartcity.presenter.AlarmCameraLiveDetailActivityPresenter;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.toast.SensoroToast;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.NetworkUtils;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.CityStandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AlarmCameraLiveDetailActivity extends BaseActivity<IAlarmCameraLiveDetailActivityView, AlarmCameraLiveDetailActivityPresenter>
        implements IAlarmCameraLiveDetailActivityView {
    @BindView(R.id.include_imv_title_imv_arrows_left)
    ImageView includeImvTitleImvArrowsLeft;
    @BindView(R.id.include_imv_title_tv_title)
    TextView includeImvTitleTvTitle;
    @BindView(R.id.include_imv_title_imv_subtitle)
    ImageView includeImvTitleImvSubtitle;
    @BindView(R.id.gsy_player_ac_alarm_camera_live_detail)
    CityStandardGSYVideoPlayer gsyPlayerAcAlarmCameraLiveDetail;
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
    private ImageView ivGsyCover;
    private GSYVideoOptionBuilder gsyVideoOption;
    private OrientationUtils orientationUtils;
    private boolean isPlay;
    private boolean isPause;
    private Animation returnTopAnimation;
    private CameraLiveDetailAdapter mListAdapter;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_alarm_camera_live_detail);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        
        includeImvTitleTvTitle.setText(mActivity.getString(R.string.deploy_camera_watch_live));
        includeImvTitleImvSubtitle.setVisibility(View.GONE);

        initSmartRefresh();

        returnTopAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.return_top_in_anim);
        returnTopInclude.setAnimation(returnTopAnimation);
        returnTopInclude.setVisibility(GONE);

        initRvList();

        initGsyPlayer();

    }

    private void initRvList() {
        mListAdapter = new CameraLiveDetailAdapter(mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL);
        rvListInclude.addItemDecoration(dividerItemDecoration);
        rvListInclude.setLayoutManager(manager);
        rvListInclude.setAdapter(mListAdapter);
    }

    private void initSmartRefresh() {
        refreshLayoutInclude.setEnableAutoLoadMore(false);//开启自动加载功能（非必须）
        refreshLayoutInclude.setEnableLoadMore(true);
        refreshLayoutInclude.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
//                mPresenter.doRefresh();
            }
        });
        refreshLayoutInclude.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
//                mPresenter.doLoadMore();
            }
        });
    }

    private void initGsyPlayer() {
        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, gsyPlayerAcAlarmCameraLiveDetail);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);
            gsyPlayerAcAlarmCameraLiveDetail.changeBottomContainer(View.INVISIBLE);

            //增加封面
            if (ivGsyCover == null) {
                ivGsyCover = new ImageView(this);
                ivGsyCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            ivGsyCover.setImageResource(R.mipmap.ic_launcher);
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
//                .setCacheWithPlay(true)
//                .setVideoTitle(cameraName)
                    .setVideoAllCallBack(new GSYSampleCallBack() {
                        @Override
                        public void onPlayError(final String url, Object... objects) {
                            orientationUtils.setEnable(false);

                            gsyPlayerAcAlarmCameraLiveDetail.changeRetryType();
                            gsyPlayerAcAlarmCameraLiveDetail.getPlayRetryBtn().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    gsyVideoOption.setUrl(url).build(getCurPlay());
                                    getCurPlay().startPlayLogic();
                                }
                            });
                        }

                        @Override
                        public void onAutoComplete(final String url, Object... objects) {

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
            getCurPlay().getTitleTextView().setVisibility(VISIBLE);
            //设置返回键
            getCurPlay().getBackButton().setVisibility(VISIBLE);

            getCurPlay().getBackButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            getCurPlay().setEnlargeImageRes(R.drawable.ic_camera_full_screen);

            getCurPlay().setShrinkImageRes(R.drawable.video_shrink);

            getCurPlay().getFullscreenButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //直接横屏
                    orientationUtils.resolveByClick();
                    //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                    getCurPlay().startWindowFullscreen(AlarmCameraLiveDetailActivity.this, true, true);
                }
            });
    }

    private GSYBaseVideoPlayer getCurPlay() {
            if (gsyPlayerAcAlarmCameraLiveDetail.getFullWindowPlayer() != null) {
                return gsyPlayerAcAlarmCameraLiveDetail.getFullWindowPlayer();
            }
            return gsyPlayerAcAlarmCameraLiveDetail;
    }

    @Override
    protected AlarmCameraLiveDetailActivityPresenter createPresenter() {
        return new AlarmCameraLiveDetailActivityPresenter();
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
    protected void onDestroy() {
        super.onDestroy();
        if (orientationUtils != null)
            orientationUtils.releaseListener();

        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
        }

        if (isPlay) {
            getCurPlay().release();
        }

        GSYVideoManager.releaseAllVideos();


    }

    @Override
    protected void onPause() {
        getCurPlay().onVideoPause();
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        getCurPlay().onVideoResume();
        super.onResume();
        isPause = false;
    }

    @Override
    public boolean isActivityOverrideStatusBar() {
        immersionBar = ImmersionBar.with(mActivity);
        immersionBar.transparentBar().init();
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
        if (isPlay && !isPause) {
            getCurPlay().onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
    }

//    @Override
    public void startPlayLogic(final String url1, String title) {


        if (!NetworkUtils.isAvailable(this) || !NetworkUtils.isWifiConnected(this)) {
            orientationUtils.setEnable(false);
            getCurPlay().onVideoPause();

            gsyPlayerAcAlarmCameraLiveDetail.getPlayBtn().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gsyPlayerAcAlarmCameraLiveDetail.changeBottomContainer(VISIBLE);

                    gsyVideoOption.setUrl(url1).build(getCurPlay());
                    getCurPlay().startPlayLogic();


                }
            });

            if (!NetworkUtils.isAvailable(AlarmCameraLiveDetailActivity.this)) {
                gsyPlayerAcAlarmCameraLiveDetail.changeNoDataType();
                return;
            }
            if (!NetworkUtils.isWifiConnected(AlarmCameraLiveDetailActivity.this)) {
                gsyPlayerAcAlarmCameraLiveDetail.changeMobileType();
                return;

            }


        } else {

            gsyVideoOption.setUrl(url1).setVideoTitle(title).build(getCurPlay());
            getCurPlay().startPlayLogic();
            orientationUtils.setEnable(true);
            gsyPlayerAcAlarmCameraLiveDetail.changeBottomContainer(VISIBLE);
        }
    }

//    @Override
    public void offlineType(final String url) {
        orientationUtils.setEnable(false);
//        orientationUtils.setEnable(false);

        gsyPlayerAcAlarmCameraLiveDetail.offlineType();
        gsyPlayerAcAlarmCameraLiveDetail.getPlayRetryBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gsyVideoOption.setUrl(url).build(getCurPlay());
                getCurPlay().startPlayLogic();
            }
        });

    }

//    @Override
    public void playError(final int pos) {
        orientationUtils.setEnable(false);

        gsyPlayerAcAlarmCameraLiveDetail.changeRetryType();
        gsyPlayerAcAlarmCameraLiveDetail.getPlayRetryBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mPresenter.onCameraItemClick(pos);


            }
        });
    }

    @Override
    public void doPlayLive(final String url, String cameraName, final boolean isLive) {
        if (!NetworkUtils.isAvailable(this) || !NetworkUtils.isWifiConnected(this)) {
            orientationUtils.setEnable(false);
            getCurPlay().onVideoPause();
            gsyPlayerAcAlarmCameraLiveDetail.getPlayBtn().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gsyPlayerAcAlarmCameraLiveDetail.changeBottomContainer(isLive ? View.INVISIBLE : VISIBLE);
                    gsyVideoOption.setUrl(url).build(getCurPlay());
                    getCurPlay().startPlayLogic();


                }
            });

            if (!NetworkUtils.isAvailable(AlarmCameraLiveDetailActivity.this)) {
                gsyPlayerAcAlarmCameraLiveDetail.changeNoDataType();
                return;
            }
            if (!NetworkUtils.isWifiConnected(AlarmCameraLiveDetailActivity.this)) {
                gsyPlayerAcAlarmCameraLiveDetail.changeMobileType();
                return;

            }


        } else {
            gsyVideoOption.setUrl(url).setVideoTitle(cameraName).build(getCurPlay());
            gsyPlayerAcAlarmCameraLiveDetail.changeBottomContainer(isLive ? View.INVISIBLE : VISIBLE);
            getCurPlay().startPlayLogic();

        }


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

    @OnClick(R.id.include_imv_title_imv_arrows_left)
    public void onViewClicked(View view) {
        switch (view.getId()){
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
}

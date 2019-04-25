package com.sensoro.smartcity.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.DeviceCameraListAdapter;
import com.sensoro.smartcity.adapter.model.DeviceCameraFacePicListModel;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.ICameraDetailView;
import com.sensoro.smartcity.presenter.CameraDetailPresenter;
import com.sensoro.smartcity.widget.CustomStandardGSYVideoPlayer;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.NetworkUtils;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 简单详情实现模式2
 */
public class CameraDetailActivity extends BaseActivity<ICameraDetailView, CameraDetailPresenter>
        implements ICameraDetailView {

    //    @BindView(R.id.include_text_title_imv_arrows_left)
//    ImageView includeTextTitleImvArrowsLeft;
//    @BindView(R.id.include_text_title_tv_title)
//    TextView includeTextTitleTvTitle;
//    @BindView(R.id.include_text_title_tv_subtitle)
//    TextView includeTextTitleTvSubtitle;
//    @BindView(R.id.include_text_title_divider)
//    View includeTextTitleDivider;
//    @BindView(R.id.include_text_title_cl_root)
//    ConstraintLayout includeTextTitleClRoot;
    @BindView(R.id.detail_player)
    CustomStandardGSYVideoPlayer detailPlayer;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.ll_time)
    LinearLayout llTime;
    @BindView(R.id.rv_device_camera)
    RecyclerView rvDeviceCamera;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.activity_detail_player)
    LinearLayout activityDetailPlayer;

    private boolean isPlay;
    private boolean isPause;
    private ProgressUtils mProgressUtils;
    private OrientationUtils orientationUtils;
    private DeviceCameraListAdapter deviceCameraListAdapter;
    private GSYVideoOptionBuilder gsyVideoOption;
    private ImmersionBar immersionBar;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_simple_detail_player);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);

    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(this).build());
//        includeTextTitleImvArrowsLeft.setColorFilter(mActivity.getResources().getColor(R.color.white));
//        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
//        includeTextTitleTvTitle.setTextColor(Color.WHITE);
//        includeTextTitleClRoot.setBackgroundColor(Color.TRANSPARENT);

        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, detailPlayer);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);
//增加title
        getCurPlay().getTitleTextView().setVisibility(View.VISIBLE);
        //设置返回键
        getCurPlay().getBackButton().setVisibility(View.VISIBLE);
//        initVideoOption();

        initRefreshLayout();

        initRvCameraList();

        getCurPlay().setEnlargeImageRes(R.drawable.ic_camera_full_screen);

        getCurPlay().setShrinkImageRes(R.drawable.ic_camera_full_screen);

        getCurPlay().getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                orientationUtils.resolveByClick();
                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                getCurPlay().startWindowFullscreen(CameraDetailActivity.this, true, true);
            }
        });

    }

    private void initRefreshLayout() {
        refreshLayout.setEnableAutoLoadMore(false);//开启自动加载功能（非必须）
        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                mPresenter.doRefresh();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                mPresenter.doLoadMore();
            }
        });

        //

    }

    @Override
    public void initVideoOption(String url) {
        detailPlayer.layoutBottom.setVisibility(View.INVISIBLE);

        //增加封面
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.mipmap.ic_launcher);

        gsyVideoOption = new GSYVideoOptionBuilder();
        gsyVideoOption.setThumbImageView(imageView)
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setAutoFullWithSize(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setUrl(url)
                .setCacheWithPlay(false)
                .setVideoTitle("测试视频")
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
        getCurPlay().getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getCurPlay().startPlayLogic();
    }

    @Override
    public void updateCameraList(ArrayList<DeviceCameraFacePicListModel> data) {
        if (data != null) {
            deviceCameraListAdapter.updateData(data);
        }
    }

    @Override
    public void startPlayLogic(final String url1) {
        //
        if (!NetworkUtils.isWifiConnected(this) || (!NetworkUtils.isWifiConnected(this))) {

            detailPlayer.playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gsyVideoOption.setUrl(url1).build(getCurPlay());
                    getCurPlay().startPlayLogic();

                }
            });
            return;
        }
        detailPlayer.layoutBottom.setVisibility(View.VISIBLE);

        gsyVideoOption.setUrl(url1).build(getCurPlay());
        getCurPlay().startPlayLogic();
    }

    @Override
    public DeviceCameraFacePicListModel getItemData(int position) {
        return deviceCameraListAdapter.getData().get(position);
    }

    @Override
    public void setDateTime(String time) {
        tvTime.setText(time);
    }

    @Override
    public List<DeviceCameraFacePicListModel> getRvListData() {
        return deviceCameraListAdapter.getData();
    }

    @Override
    public void onPullRefreshComplete() {
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
    }

    @Override
    public void playError(final int pos) {
        detailPlayer.playAgainBtn.setText("Video loading failed, please try again");
        detailPlayer.playAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onCameraItemClick(pos);

            }
        });
    }

    private void initRvCameraList() {
        deviceCameraListAdapter = new DeviceCameraListAdapter(this);
        rvDeviceCamera.setLayoutManager(new LinearLayoutManager(this));
        rvDeviceCamera.setAdapter(deviceCameraListAdapter);
        deviceCameraListAdapter.setOnContentItemClickListener(new DeviceCameraListAdapter.OnDeviceCameraListClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LinearLayoutManager manager = (LinearLayoutManager) rvDeviceCamera.getLayoutManager();
                int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
                View childAt = rvDeviceCamera.getChildAt(position - firstVisibleItemPosition);
                int top = childAt.getTop();
                rvDeviceCamera.smoothScrollBy(0, top);

                mPresenter.onCameraItemClick(position - 1);

            }


            @Override
            public void setOnLiveClick() {

            }
        });
        rvDeviceCamera.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstPosition = manager.findFirstVisibleItemPosition();
                mPresenter.doDateTime(firstPosition);
//                }


            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

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
    protected void onPause() {
        getCurPlay().onVideoPause();
        super.onPause();
        isPause = true;
    }

    @Override
    protected CameraDetailPresenter createPresenter() {
        return new CameraDetailPresenter();
    }

    @Override
    protected void onResume() {
        getCurPlay().onVideoResume(false);
        super.onResume();
        isPause = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPlay) {
            getCurPlay().release();
        }
        if (immersionBar != null) {
            immersionBar.destroy();
        }
        if (orientationUtils != null)
            orientationUtils.releaseListener();

        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            getCurPlay().onConfigurationChanged(this, newConfig, orientationUtils, true, true);
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
    public boolean isActivityOverrideStatusBar() {
        immersionBar = ImmersionBar.with(mActivity);
        immersionBar.transparentStatusBar().init();
        return true;
    }

    private GSYVideoPlayer getCurPlay() {
        if (detailPlayer.getFullWindowPlayer() != null) {
            return detailPlayer.getFullWindowPlayer();
        }
        return detailPlayer;
    }

    @Override
    public boolean setMyCurrentActivityTheme() {
        setTheme(R.style.Theme_AppCompat_Full);
        return true;
    }
    //    @OnClick(R.id.include_text_title_imv_arrows_left)
//    public void onViewClicked() {
//    }
}

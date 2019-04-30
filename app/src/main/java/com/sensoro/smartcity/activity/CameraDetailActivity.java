package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import com.sensoro.smartcity.adapter.CameraDetailListAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.ICameraDetailActivityView;
import com.sensoro.smartcity.presenter.CameraDetailActivityPresenter;
import com.sensoro.smartcity.server.bean.DeviceCameraFacePic;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 简单详情实现模式2
 */
public class CameraDetailActivity extends BaseActivity<ICameraDetailActivityView, CameraDetailActivityPresenter>
        implements ICameraDetailActivityView {


    @BindView(R.id.gsy_player_ac_camera_detail)
    CustomStandardGSYVideoPlayer gsyPlayerAcCameraDetail;
    @BindView(R.id.iv_live_ac_camera_detail)
    ImageView ivLiveAcCameraDetail;
    @BindView(R.id.tv_live_ac_camera_detail)
    TextView tvLiveAcCameraDetail;
    @BindView(R.id.ll_live_ac_camera_detail)
    LinearLayout llLiveAcCameraDetail;
    @BindView(R.id.iv_calendar_ac_camera_detail)
    ImageView ivCalendarAcCameraDetail;
    @BindView(R.id.tv_select_time_ac_camera_detail)
    TextView tvSelectTimeAcCameraDetail;
    @BindView(R.id.iv_time_close_ac_camera_detail)
    ImageView ivTimeCloseAcCameraDetail;
    @BindView(R.id.ll_select_time_ac_camera_detail)
    LinearLayout llSelectTimeAcCameraDetail;
    @BindView(R.id.rv_device_camera_ac_camera_detail)
    RecyclerView rvDeviceCameraAcCameraDetail;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.activity_detail_player)
    LinearLayout activityDetailPlayer;
    private boolean isPlay;
    private boolean isPause;
    private ProgressUtils mProgressUtils;
    private OrientationUtils orientationUtils;
    private CameraDetailListAdapter deviceCameraListAdapter;
    private GSYVideoOptionBuilder gsyVideoOption;
    private ImmersionBar immersionBar;
    private ImageView imageView;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_simple_detail_player);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);

    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(this).build());

        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, gsyPlayerAcCameraDetail);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);
//增加title
        getCurPlay().getTitleTextView().setVisibility(View.VISIBLE);
        //设置返回键
        getCurPlay().getBackButton().setVisibility(View.VISIBLE);
//        initVideoOption();

        ivCalendarAcCameraDetail.setColorFilter(mActivity.getResources().getColor(R.color.c_a6a6a6));

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
        gsyPlayerAcCameraDetail.changeBottomContainer(View.INVISIBLE);

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
    public void updateCameraList(List<DeviceCameraFacePic> data) {
        if (data != null) {
            deviceCameraListAdapter.updateData(data);
        }
    }

    @Override
    public void startPlayLogic(final String url1) {
        if (!NetworkUtils.isAvailable(this)) {
            orientationUtils.setEnable(false);
            return;
        }

        if (!NetworkUtils.isWifiConnected(this)) {
            orientationUtils.setEnable(false);
            gsyPlayerAcCameraDetail.getPlayBtn().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gsyVideoOption.setUrl(url1).build(getCurPlay());
                    getCurPlay().startPlayLogic();

                }
            });
            return;
        }
        gsyPlayerAcCameraDetail.changeBottomContainer(View.VISIBLE);

        gsyVideoOption.setUrl(url1).build(getCurPlay());
        getCurPlay().startPlayLogic();
        orientationUtils.setEnable(true);

    }

    @Override
    public DeviceCameraFacePic getItemData(int position) {
        return deviceCameraListAdapter.getData().get(position);
    }

    @Override
    public void setDateTime(String time) {
        tvSelectTimeAcCameraDetail.setText(time);
    }

    @Override
    public List<DeviceCameraFacePic> getRvListData() {
        return deviceCameraListAdapter.getData();
    }

    @Override
    public void onPullRefreshComplete() {
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
    }

    @Override
    public void setLiveState(boolean isLiveStream) {
        if (isLiveStream) {
            ivLiveAcCameraDetail.setImageResource( R.drawable.camera_live_normal );
            tvLiveAcCameraDetail.setText(mActivity.getString( R.string.camera_living ));
            tvLiveAcCameraDetail.setTextColor(mActivity.getResources().getColor(R.color.c_252525));
            llLiveAcCameraDetail.setBackgroundResource(R.drawable.shape_bg_solid_ee_full_corner_4 );

        }else{
            ivLiveAcCameraDetail.setImageResource(R.drawable.camera_live_rollback);
            tvLiveAcCameraDetail.setText(mActivity.getString( R.string.camera_back_live));
            tvLiveAcCameraDetail.setTextColor(mActivity.getResources().getColor(R.color.white));
            llLiveAcCameraDetail.setBackgroundResource( R.drawable.shape_bg_corner_29c_shadow);

        }
    }

    @Override
    public void setImage(Drawable resource) {
        if (imageView != null) {
            imageView.setImageDrawable(resource);
        } else {
            imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageDrawable(resource);
        }

    }

    @Override
    public void clearClickPosition() {
        if (deviceCameraListAdapter != null) {
            deviceCameraListAdapter.clearClickPosition();
        }
    }

    @Override
    public boolean isSelectedDateLayoutVisible() {
        return ivTimeCloseAcCameraDetail.getVisibility() == View.VISIBLE;
    }

    @Override
    public void setSelectedDateLayoutVisible(boolean isVisible) {
        ivCalendarAcCameraDetail.setColorFilter(mActivity.getResources().
                getColor(isVisible ? R.color.c_252525: R.color.c_a6a6a6));
        ivTimeCloseAcCameraDetail.setVisibility(isVisible ? View.VISIBLE : View.GONE);

        if (isVisible) {
            tvSelectTimeAcCameraDetail.setTextColor(mActivity.getResources().getColor(R.color.c_252525));
        }else{
            tvSelectTimeAcCameraDetail.setTextColor(mActivity.getResources().getColor(R.color.c_a6a6a6));
            tvSelectTimeAcCameraDetail.setText(mActivity.getString(R.string.click_select_date));
        }
    }

    @Override
    public void setSelectedDateSearchText(String s) {
        tvSelectTimeAcCameraDetail.setText(s);
    }

    @Override
    public void onPullRefreshCompleteNoMoreData() {
        refreshLayout.finishLoadMoreWithNoMoreData();
    }

    @Override
    public void playError(final int pos) {
        orientationUtils.setEnable(false);

        gsyPlayerAcCameraDetail.changeRetryType();
        gsyPlayerAcCameraDetail.getPlayRetryBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onCameraItemClick(pos);


            }
        });
    }

    @Override
    public void autoRefresh() {
        if (refreshLayout != null) {
            refreshLayout.autoRefresh();
        }
    }

    @Override
    public ImageView getImageView() {
        return imageView;
    }

    private void initRvCameraList() {
        deviceCameraListAdapter = new CameraDetailListAdapter(this);
        rvDeviceCameraAcCameraDetail.setLayoutManager(new LinearLayoutManager(this));
        rvDeviceCameraAcCameraDetail.setAdapter(deviceCameraListAdapter);

        deviceCameraListAdapter.setOnCameraDetailListClickListener(new CameraDetailListAdapter.CameraDetailListClickListener() {
            @Override
            public void onItemClick(int position) {
                setLiveState(false);
                mPresenter.onCameraItemClick(position);
            }

            @Override
            public void onAvatarClick(int position) {
                mPresenter.doPersonAvatarHistory(position);
            }
        });
//        deviceCameraListAdapter.setOnContentItemClickListener(new DeviceCameraListAdapter.OnDeviceCameraListClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                LinearLayoutManager manager = (LinearLayoutManager) rvDeviceCameraAcCameraDetail.getLayoutManager();
//                int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
//                View childAt = rvDeviceCameraAcCameraDetail.getChildAt(position - firstVisibleItemPosition);
//                int top = childAt.getTop();
//                rvDeviceCameraAcCameraDetail.smoothScrollBy(0, top);
//
//                mPresenter.onCameraItemClick(position - 1);
//
//            }
//
//
//            @Override
//            public void onLiveClick() {
//                mPresenter.doLive();
//            }
//
//            @Override
//            public void onAvatarClick(int modelPosition, int avatarPosition) {
//                mPresenter.doPersonAvatarHistory(modelPosition, avatarPosition);
//            }
//        });
        rvDeviceCameraAcCameraDetail.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
    protected CameraDetailActivityPresenter createPresenter() {
        return new CameraDetailActivityPresenter();
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
        if (gsyPlayerAcCameraDetail.getFullWindowPlayer() != null) {
            return gsyPlayerAcCameraDetail.getFullWindowPlayer();
        }
        return gsyPlayerAcCameraDetail;
    }

    @Override
    public boolean setMyCurrentActivityTheme() {
        setTheme(R.style.Theme_AppCompat_Full);
        return true;
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_LONG).show();
    }

    @OnClick({R.id.ll_select_time_ac_camera_detail, R.id.iv_time_close_ac_camera_detail,R.id.ll_live_ac_camera_detail})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_select_time_ac_camera_detail:
                mPresenter.doCalendar(activityDetailPlayer);
                break;
            case R.id.iv_time_close_ac_camera_detail:
                setSelectedDateLayoutVisible(false);
                mPresenter.doRequestData();
                break;
            case R.id.ll_live_ac_camera_detail:
                clearClickPosition();
                setLiveState(true);
                mPresenter.doLive();
                break;

        }
    }


    @Override
    public void startAC(Intent intent) {
        mActivity.startActivity(intent);
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



}

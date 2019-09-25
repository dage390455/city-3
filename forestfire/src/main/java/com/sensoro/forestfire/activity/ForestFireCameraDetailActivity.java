package com.sensoro.forestfire.activity;

import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.amap.api.maps.TextureMapView;
import com.gyf.immersionbar.ImmersionBar;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.server.bean.ForestFireCameraDetailInfo;
import com.sensoro.common.utils.ScreenUtils;
import com.sensoro.common.widgets.BoldTextView;
import com.sensoro.forestfire.R;
import com.sensoro.forestfire.R2;
import com.sensoro.forestfire.imainviews.IForestFireCameraDetailActivityView;
import com.sensoro.forestfire.presenter.ForestFireCameraDetailActivityPresenter;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.utils.CustomManager;
import com.shuyu.gsyvideoplayer.video.MultiSampleVideo;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author: jack
 * 时  间: 2019-09-17
 * 包  名: com.sensoro.forestfire.activity
 * 简  述: <功能简述:森林防火管理监测点详情>
 */

@Route(path = ARouterConstants.ACTIVITY_FORESTFIRE_CAMERA_DETAIL)
public class ForestFireCameraDetailActivity extends BaseActivity<IForestFireCameraDetailActivityView, ForestFireCameraDetailActivityPresenter>
        implements IForestFireCameraDetailActivityView {


    @BindView(R2.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R2.id.include_text_title_tv_title)
    BoldTextView includeTextTitleTvTitle;
    @BindView(R2.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R2.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R2.id.tv_forest_fire_camera_name)
    TextView tvForestFireCameraName;
    @BindView(R2.id.tv_forest_fire_camera_type)
    TextView tvForestFireCameraType;
    @BindView(R2.id.tv_forest_fire_camera_time)
    TextView tvForestFireCameraTime;

    @BindView(R2.id.tv_forest_fire_camera_detail_device_sn)
    TextView tvForestFireCameraDetailDeviceSn;
    @BindView(R2.id.tv_forest_fire_camera_detail_device_gatway)
    TextView tvForestFireCameraDetailDeviceGatway;
    @BindView(R2.id.tv_location)
    TextView tvLocation;
    @BindView(R2.id.tv_modify)
    TextView tvModify;
    @BindView(R2.id.ll_forest_fire_camera_detail_device_location)
    LinearLayout llForestFireCameraDetailDeviceLocation;
    @BindView(R2.id.textureMapView)
    TextureMapView textureMapView;

    MultiSampleVideo gsyVideoPlayer;
    MultiSampleVideo gsyVideoPlayerImg;


    @BindView(R2.id.view_top_ac_alarm_camera_video_detail)
    View viewTopAcAlarmCameraVideoDetail;
    private boolean isPause;
    private ArrayList<ForestFireCameraDetailInfo.MultiVideoInfoBean> list;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_forest_fire_camera_detail);
        ButterKnife.bind(this);
        textureMapView.onCreate(savedInstanceState);
        initViewHeight();

        gsyVideoPlayer = findViewById(R.id.video_player1);
        gsyVideoPlayerImg = findViewById(R.id.video_img_player2);
        gsyVideoPlayer.getLayoutParams().height= ScreenUtils.getScreenWidth(mActivity)*9/16;
        gsyVideoPlayerImg.getLayoutParams().height= ScreenUtils.getScreenWidth(mActivity)*9/16;

        initPlayer();
        initImgPlayer();
        mPresenter.initData(mActivity);
        mPresenter.initMapSetting(textureMapView);
        mPresenter.initView();
    }


    private void initPlayer() {
        gsyVideoPlayer.setPlayTag("gsyVideoPlayer");
        gsyVideoPlayer.setPlayPosition(0);
        gsyVideoPlayer.setIsLive(View.INVISIBLE);
        gsyVideoPlayer.getTitleTextView().setVisibility(View.GONE);
        gsyVideoPlayer.getBackButton().setVisibility(View.GONE);
        gsyVideoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resolveFullBtn(gsyVideoPlayer);
            }
        });

        gsyVideoPlayer.setRotateViewAuto(false);
        gsyVideoPlayer.setRotateWithSystem(false);
        gsyVideoPlayer.setLockLand(true);
        gsyVideoPlayer.setReleaseWhenLossAudio(false);
        gsyVideoPlayer.setShowFullAnimation(false);
        gsyVideoPlayer.setIsTouchWiget(false);
        gsyVideoPlayer.setNeedLockFull(true);
        gsyVideoPlayer.setIsShowMaskTopBack(false);
        gsyVideoPlayer.setCityPlayState(-1);
    }


    private void initImgPlayer() {
        gsyVideoPlayerImg.setPlayTag("gsyVideoPlayerImg");
        gsyVideoPlayerImg.setPlayPosition(1);
        gsyVideoPlayerImg.setIsLive(View.INVISIBLE);
        gsyVideoPlayerImg.getTitleTextView().setVisibility(View.GONE);
        gsyVideoPlayerImg.getBackButton().setVisibility(View.GONE);
        gsyVideoPlayerImg.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resolveFullBtn(gsyVideoPlayerImg);
            }
        });
        gsyVideoPlayerImg.setRotateViewAuto(false);
        gsyVideoPlayerImg.setRotateWithSystem(false);
        gsyVideoPlayerImg.setLockLand(true);
        gsyVideoPlayerImg.setReleaseWhenLossAudio(false);
        gsyVideoPlayerImg.setShowFullAnimation(false);
        gsyVideoPlayerImg.setIsTouchWiget(false);
        gsyVideoPlayerImg.setNeedLockFull(true);
        gsyVideoPlayerImg.setIsShowMaskTopBack(false);
        gsyVideoPlayerImg.setCityPlayState(-1);


    }

    @Override
    public boolean setMyCurrentStatusBar() {
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
    protected ForestFireCameraDetailActivityPresenter createPresenter() {
        return new ForestFireCameraDetailActivityPresenter();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        textureMapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textureMapView.onDestroy();


        CustomManager.clearAllVideo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        textureMapView.onPause();
        CustomManager.onPauseAll();
        isPause = true;

    }

    @Override
    protected void onResume() {
        super.onResume();
        textureMapView.onResume();
        CustomManager.onResumeAll();
        isPause = false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        textureMapView.onSaveInstanceState(outState);

    }

    @Override
    public void updateTitle(String title) {
        includeTextTitleTvTitle.setText(title);
    }

    @Override
    public void updateCameraName(String name) {
        tvForestFireCameraName.setText(name);
    }

    @Override
    public void updateCameraType(String type) {
        tvForestFireCameraType.setText(type);
    }

    @Override
    public void updateTime(String time) {
        tvForestFireCameraTime.setText(time);
    }


    @Override
    public void updateDeviceSN(String sn) {
        tvForestFireCameraDetailDeviceSn.setText(sn);
    }

    @Override
    public void updateGateway(String gateway) {
        tvForestFireCameraDetailDeviceGatway.setText(gateway);
    }

    @Override
    public void updateLocation(double lon, double lat) {
        DecimalFormat  decimalFormat=new DecimalFormat("#.######");
        tvLocation.setText(decimalFormat.format(lat)+"；"+decimalFormat.format(lon));
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


    @Override
    public void updateData(ArrayList<ForestFireCameraDetailInfo.MultiVideoInfoBean> list) {
        this.list = list;
        playFirst(list);
        playSecond(list);
    }

    /**
     * 可见光播放器
     *
     * @param list
     */
    private void playFirst(ArrayList<ForestFireCameraDetailInfo.MultiVideoInfoBean> list) {
        gsyVideoPlayer.setCityURl(list, "");
        gsyVideoPlayer.startPlayLogic();
        ForestFireCameraDetailInfo.MultiVideoInfoBean videoModel = list.get(0);
        gsyVideoPlayer.mCoverImage.setVisibility(View.VISIBLE);
        gsyVideoPlayer.loadCoverImage(videoModel.getLastCover(), R.drawable.camera_detail_mask);
        String gsyVideoPlayerKey = gsyVideoPlayer.getKey();
        gsyVideoPlayer.setVideoAllCallBack(new GSYSampleCallBack() {

            @Override
            public void onPlayError(final String url, Object... objects) {

                gsyVideoPlayer.setCityPlayState(3);
                CustomManager.backFromWindowFull(mActivity, gsyVideoPlayerKey);
                gsyVideoPlayer.getPlayAndRetryBtn().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gsyVideoPlayer.setCityPlayState(-1);
                        gsyVideoPlayer.setCityURl(list, "");
                        gsyVideoPlayer.startPlayLogic();
                        videoModel.state = -10;
                    }
                });
            }


            @Override
            public void onQuitFullscreen(String url, Object... objects) {
                super.onQuitFullscreen(url, objects);
            }

            @Override
            public void onEnterFullscreen(String url, Object... objects) {
                super.onEnterFullscreen(url, objects);
                gsyVideoPlayer.getCurrentPlayer().getTitleTextView().setText((String) objects[0]);
            }

        });
    }

    /**
     * 热成像播放器
     *
     * @param list
     */
    private void playSecond(ArrayList<ForestFireCameraDetailInfo.MultiVideoInfoBean> list) {
        ForestFireCameraDetailInfo.MultiVideoInfoBean videoModel = list.get(1);
        String gsyVideoPlayerImgKey = gsyVideoPlayerImg.getKey();

        gsyVideoPlayerImg.loadCoverImage(videoModel.getLastCover(), R.drawable.camera_detail_mask);
        gsyVideoPlayerImg.mCoverImage.setVisibility(View.VISIBLE);
        gsyVideoPlayerImg.setVideoAllCallBack(new GSYSampleCallBack() {

            @Override
            public void onPlayError(final String url, Object... objects) {

                gsyVideoPlayerImg.setCityPlayState(3);
                CustomManager.backFromWindowFull(mActivity, gsyVideoPlayerImgKey);
                gsyVideoPlayerImg.getPlayAndRetryBtn().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gsyVideoPlayerImg.setCityPlayState(-1);
                        gsyVideoPlayerImg.setCityURl(list, "");
                        gsyVideoPlayerImg.startPlayLogic();
                        videoModel.state = -10;
                    }
                });
            }


            @Override
            public void onQuitFullscreen(String url, Object... objects) {
                super.onQuitFullscreen(url, objects);
            }

            @Override
            public void onEnterFullscreen(String url, Object... objects) {
                super.onEnterFullscreen(url, objects);
                gsyVideoPlayerImg.getCurrentPlayer().getTitleTextView().setText((String) objects[0]);
            }

        });
        gsyVideoPlayerImg.setCityURl(list, "");
        gsyVideoPlayerImg.startPlayLogic();
    }

    @Override
    public void updataAdapterState(int state) {
        switch (state) {

            case ConnectivityManager.TYPE_WIFI:

//                if (gsyVideoPlayer.getCurrentPlayer().getCurrentState()!= GSYVideoView.CURRENT_STATE_PLAYING) {


                gsyVideoPlayer.setCityPlayState(-1);

                playFirst(list);
//                }


//                if (gsyVideoPlayerImg.getCurrentPlayer().getCurrentState()!= GSYVideoView.CURRENT_STATE_PLAYING) {

                gsyVideoPlayerImg.setCityPlayState(-1);

                playSecond(list);
//                }

                break;

            case ConnectivityManager.TYPE_MOBILE:
                gsyVideoPlayer.setCityPlayState(2);
                CustomManager.onPauseAll();
                CustomManager.backFromWindowFull(mActivity, gsyVideoPlayer.getKey());
                gsyVideoPlayer.getPlayAndRetryBtn().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        gsyVideoPlayer.mCoverImage.setVisibility(View.VISIBLE);

//                        gsyVideoPlayer.loadCoverImage(list.get(0).getLastCover(), R.drawable.camera_detail_mask);
                        gsyVideoPlayer.getMaskLayoutTop().setVisibility(View.GONE);
                        gsyVideoPlayer.getrMobileData().setVisibility(View.GONE);
                        gsyVideoPlayer.setCityURl(list, "");
                        gsyVideoPlayer.startPlayLogic();


                    }
                });


                gsyVideoPlayerImg.setCityPlayState(2);
                CustomManager.backFromWindowFull(mActivity, gsyVideoPlayerImg.getKey());
                gsyVideoPlayerImg.getPlayAndRetryBtn().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        gsyVideoPlayerImg.loadCoverImage(list.get(1).getLastCover(), R.drawable.camera_detail_mask);
//                        gsyVideoPlayerImg.mCoverImage.setVisibility(View.VISIBLE);
                        gsyVideoPlayerImg.getMaskLayoutTop().setVisibility(View.GONE);
                        gsyVideoPlayerImg.getrMobileData().setVisibility(View.GONE);
                        gsyVideoPlayerImg.setCityURl(list, "");
                        gsyVideoPlayerImg.startPlayLogic();


                    }
                });


                break;

            default:

                gsyVideoPlayer.setCityPlayState(1);
                CustomManager.backFromWindowFull(mActivity, gsyVideoPlayer.getKey());
                gsyVideoPlayerImg.setCityPlayState(1);
                CustomManager.backFromWindowFull(mActivity, gsyVideoPlayerImg.getKey());
                CustomManager.onPauseAll();
                break;


        }
    }


    @OnClick({R2.id.include_text_title_imv_arrows_left, R2.id.include_text_title_tv_subtitle, R2.id.ll_forest_fire_camera_detail_device_location})
    public void onViewClicked(View view) {
        int viewID = view.getId();

        if (viewID == R.id.include_text_title_imv_arrows_left) {
            finish();
        } else if (viewID == R.id.include_text_title_tv_subtitle) {
            mPresenter.startHistoryActivity();
        } else if (viewID == R.id.ll_forest_fire_camera_detail_device_location) {
            mPresenter.startLocationActivity();
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {


//            if (listMultilAdapter.getData() != null) {
//                CustomManager.clearAllVideo();
//                videoList.postDelayed(() -> {
//                    listMultilAdapter = new ListMultiNormalAdapter(ForestFireCameraDetailActivity.this);
//                    videoList.setAdapter(listMultilAdapter);
//
//                    updataAdapter(listMultilAdapter.getData());
//                }, 100);
//            }

            CustomManager.backFromWindowFull(mActivity, gsyVideoPlayer.getKey());
            CustomManager.backFromWindowFull(mActivity, gsyVideoPlayerImg.getKey());
            CustomManager.clearAllVideo();
            viewTopAcAlarmCameraVideoDetail.postDelayed(() -> {
                updateData(list);
            }, 100);

        }



    }


    @Override
    public void onBackPressed() {
        if (CustomManager.backFromWindowFull(this, gsyVideoPlayer.getKey())||CustomManager.backFromWindowFull(this, gsyVideoPlayerImg.getKey())) {
            CustomManager.backFromWindowFull(mActivity, gsyVideoPlayer.getKey());
            CustomManager.backFromWindowFull(mActivity, gsyVideoPlayerImg.getKey());
            return;
        }
        super.onBackPressed();
    }

    private void resolveFullBtn(final StandardGSYVideoPlayer standardGSYVideoPlayer) {
        standardGSYVideoPlayer.startWindowFullscreen(mActivity, false, true);
    }


}

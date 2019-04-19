package com.sensoro.smartcity.temp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.gyf.barlibrary.ImmersionBar;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.DeviceCameraListAdapter;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceCameraFacePic;
import com.sensoro.smartcity.server.bean.DeviceCameraHistoryBean;
import com.sensoro.smartcity.server.response.DeviceCameraFacePicListRsp;
import com.sensoro.smartcity.server.response.DeviceCameraHistoryRsp;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * 简单详情实现模式2
 */
public class SimpleDetailActivityMode extends AppCompatActivity {


    StandardGSYVideoPlayer detailPlayer;

    private boolean isPlay;
    private boolean isPause;
    private ProgressUtils mProgressUtils;
    private OrientationUtils orientationUtils;
    private RecyclerView rvDeviceCamera;
    private DeviceCameraListAdapter deviceCameraListAdapter;
    private List<DeviceCameraFacePic> data;
    public ImmersionBar immersionBar;
    private GSYVideoOptionBuilder gsyVideoOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.MyTheme);
        //取消bar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }
//        CustomDensityUtils.SetCustomDensity(this, SensoroCityApplication.getInstance());
        //控制顶部状态栏显示
//        StatusBarCompat.translucentStatusBar(thi®s);
//        StatusBarCompat.setStatusBarIconDark(this,true);
//        boolean darkmode = true;
        immersionBar = ImmersionBar.with(this);
        immersionBar.fitsSystemWindows(true, R.color.white)
                .statusBarColor(R.color.white)
                .statusBarDarkFont(true)
                .init();
        setContentView(R.layout.activity_simple_detail_player);
        Intent intent = getIntent();
        String url = "http://wdquan-space.b0.upaiyun.com/VIDEO/2018/11/22/ae0645396048_hls_time10.m3u8";
        String cid = null;
        if (intent != null) {
            cid = intent.getStringExtra("cid");
            url = intent.getStringExtra("hls");

        }

        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(this).build());
        detailPlayer = (StandardGSYVideoPlayer) findViewById(R.id.detail_player);
        rvDeviceCamera = (RecyclerView) findViewById(R.id.rv_device_camera);
        deviceCameraListAdapter = new DeviceCameraListAdapter(this);
        rvDeviceCamera.setLayoutManager(new LinearLayoutManager(this));
        rvDeviceCamera.setAdapter(deviceCameraListAdapter);

        final String finalCid = cid;
        deviceCameraListAdapter.setOnContentItemClickListener(new DeviceCameraListAdapter.OnDeviceCameraListClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (data != null) {
                    mProgressUtils.showProgress();
                    DeviceCameraFacePic deviceCameraFacePic = data.get(position);
                    String captureTime = deviceCameraFacePic.getCaptureTime();
                    String beginTime = null;
                    String endTime = null;
                    try {
                        long capTime = Long.parseLong(captureTime);
                        capTime = capTime / 1000;
                        beginTime = String.valueOf(capTime - 30);
                        endTime = String.valueOf(capTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    RetrofitServiceHelper.getInstance().getDeviceCameraPlayHistoryAddress(finalCid, beginTime, endTime, null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceCameraHistoryRsp>(null) {
                        @Override
                        public void onCompleted(DeviceCameraHistoryRsp deviceCameraHistoryRsp) {
                            List<DeviceCameraHistoryBean> data = deviceCameraHistoryRsp.getData();
                            if (data != null && data.size() > 0) {
                                DeviceCameraHistoryBean deviceCameraHistoryBean = data.get(0);
                                String url1 = deviceCameraHistoryBean.getUrl();
                                gsyVideoOption.setUrl(url1).build(detailPlayer);
                                detailPlayer.startPlayLogic();
                            }
                            mProgressUtils.dismissProgress();
                        }

                        @Override
                        public void onErrorMsg(int errorCode, String errorMsg) {
                            mProgressUtils.dismissProgress();
                        }
                    });

                }
            }
        });


        //增加封面
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.mipmap.ic_launcher);

        //增加title
        detailPlayer.getTitleTextView().setVisibility(View.GONE);
        detailPlayer.getBackButton().setVisibility(View.GONE);

        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, detailPlayer);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);

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
        }).build(detailPlayer);

        detailPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                orientationUtils.resolveByClick();
                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                detailPlayer.startWindowFullscreen(SimpleDetailActivityMode.this, true, true);
            }
        });
        requestData(cid);
        detailPlayer.startPlayLogic();
    }

    private void requestData(String cid) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add(cid);
        long currentTimeMillis = System.currentTimeMillis();
        String startTime = String.valueOf(currentTimeMillis - 24 * 60 * 60 * 60 * 1000L);
        String endTime = String.valueOf(currentTimeMillis);
        RetrofitServiceHelper.getInstance().getDeviceCameraFaceList(strings, null, 15, startTime, endTime).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceCameraFacePicListRsp>(null) {
            @Override
            public void onCompleted(DeviceCameraFacePicListRsp deviceCameraFacePicListRsp) {
                data = deviceCameraFacePicListRsp.getData();
                deviceCameraListAdapter.updateData(data);
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {

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
        detailPlayer.getCurrentPlayer().onVideoPause();
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        detailPlayer.getCurrentPlayer().onVideoResume(false);
        super.onResume();
        isPause = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPlay) {
            detailPlayer.getCurrentPlayer().release();
        }
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
//            detailPlayer.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
    }

}

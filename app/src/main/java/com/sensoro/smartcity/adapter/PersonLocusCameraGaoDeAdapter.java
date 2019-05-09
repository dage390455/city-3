package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;
import com.sensoro.smartcity.R;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.CityStandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

public class PersonLocusCameraGaoDeAdapter implements AMap.InfoWindowAdapter {
    private final Activity mContext;
    private CityStandardGSYVideoPlayer player;
    private OrientationUtils orientationUtils;
    private GSYVideoOptionBuilder gsyVideoOption;
    private boolean isPlay;
    private boolean isPause;
    private final View view;
    private ImageView imageView;
    private onVideoButtonClickListener mListener;

    public PersonLocusCameraGaoDeAdapter(Activity context) {
        mContext = context;
        view = LayoutInflater.from(mContext).inflate(R.layout.adapter_gao_de_info_window_person_locus, null);
        player = view.findViewById(R.id.detail_player_adapter_gao_de_person_locus);

        initPlayer();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return view;
    }

    private void initPlayer() {
        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(mContext, player);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);

        player.bringToFront();
        getCurPlay().getTitleTextView().setVisibility(View.VISIBLE);
        //设置返回键
        getCurPlay().getBackButton().setVisibility(View.VISIBLE);

        getCurPlay().setEnlargeImageRes(R.drawable.ic_camera_full_screen);

        getCurPlay().setShrinkImageRes(R.drawable.ic_camera_full_screen);

        getCurPlay().getBackButton().setImageResource(R.drawable.video_small_close);

        getCurPlay().getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                orientationUtils.resolveByClick();
                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                getCurPlay().startWindowFullscreen(mContext, true, true);
            }
        });
//        getCurPlay().setBackFromFullScreenListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });

        initVideoOption(null);

    }

    public void initVideoOption(String url) {
        player.setIsLive(View.VISIBLE);

        //增加封面
        if (imageView == null) {
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setImageResource(R.mipmap.ic_launcher);
        }


        gsyVideoOption = new GSYVideoOptionBuilder();
        gsyVideoOption
                .setThumbImageView(imageView)
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setAutoFullWithSize(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
//                .setUrl(url)
                .setCacheWithPlay(true)
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
                getCurPlay().startWindowFullscreen(mContext, true, true);
            }
        });
        getCurPlay().getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mContext.onBackPressed();
                if (mListener != null) {
                    mListener.onCloseClick();
                }

            }
        });
//        getCurPlay().startPlayLogic();
    }

    private GSYVideoPlayer getCurPlay() {
        if (player.getFullWindowPlayer() != null) {
            return player.getFullWindowPlayer();
        }
        return player;
    }

    public void startPlayLogic(final String url1) {
//        if (!NetworkUtils.isAvailable(mContext)) {
//            orientationUtils.setEnable(false);
//            return;
//        }
//
//        if (!NetworkUtils.isWifiConnected(mContext)) {
//            orientationUtils.setEnable(false);
//            player.getPlayAndRetryBtn().setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    gsyVideoOption.setUrl(url1).build(getCurPlay());
//                    getCurPlay().startPlayLogic();
//
//                }
//            });
//            return;
//        }

        gsyVideoOption.setUrl(url1).build(getCurPlay());
        getCurPlay().startPlayLogic();
        orientationUtils.setEnable(true);
        player.setIsLive(View.VISIBLE);
        player.setIsShowMaskTopBack(false);
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    public void destroyGsyVideo() {
        if (isPlay) {
            getCurPlay().release();
        }
        GSYVideoManager.releaseAllVideos();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (isPlay && !isPause) {
            getCurPlay().onConfigurationChanged(mContext, newConfig, orientationUtils, true, true);
        }

    }

    public void onPause() {
        getCurPlay().onVideoPause();
        isPause = true;
    }

    public void onResume() {
//        getCurPlay().onVideoResume(false);
        isPause = false;
    }

    public void setLastCover(BitmapDrawable bitmapDrawable) {
        if (imageView != null) {
            imageView.setImageDrawable(bitmapDrawable);
        } else {
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageDrawable(bitmapDrawable);
        }
    }

    public void setOnCloseClickListener(onVideoButtonClickListener listener) {
        mListener = listener;
    }

    public interface onVideoButtonClickListener {
        void onCloseClick();

        void onFullScreenClick();
    }
}

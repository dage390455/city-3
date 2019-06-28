package com.sensoro.city_camera.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sensoro.city_camera.R;
import com.shuyu.gsyvideoplayer.listener.GSYVideoShotSaveListener;
import com.shuyu.gsyvideoplayer.video.CityStandardGSYVideoPlayer;

import java.io.File;

/**
 * @author : bin.tian
 * date   : 2019-06-27
 */
public class AiGSYVideoPlayer extends CityStandardGSYVideoPlayer {
    private RelativeLayout mRightToolRl;
    private ImageView mCaptureIv;
    private ImageView mDownloadIv;

    private CaptureClickListener mCaptureClickListener;
    private DownloadClickListener mDownloadClickListener;

    public AiGSYVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public AiGSYVideoPlayer(Context context) {
        super(context);
    }

    public AiGSYVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void init(Context context) {
        super.init(context);
        mRightToolRl = findViewById(R.id.right_tool_rl);
        mCaptureIv = findViewById(R.id.capture_iv);
        mDownloadIv = findViewById(R.id.download_iv);

        mCaptureIv.setOnClickListener(v -> capture());
        mDownloadIv.setOnClickListener(v -> download());
    }

    @Override
    public int getLayoutId() {
        return R.layout.ai_video_layout;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mRightToolRl.setVisibility(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? VISIBLE : GONE);
    }

    @Override
    protected void setViewShowState(View view, int visibility) {
        super.setViewShowState(view, visibility);
        if (!mIfCurrentIsFullscreen) {
            mRightToolRl.setVisibility(GONE);
        } else if (view == mBottomContainer) {
            mRightToolRl.setVisibility(visibility);
        }
    }

    public void doCapture(File file, AiVideoCaptureListener aiVideoCaptureListener) {
        saveFrame(file, aiVideoCaptureListener::result);
    }

    private void capture() {
        if (mCaptureClickListener != null) {
            mCaptureClickListener.onCaptureClick();
        }
    }

    private void download() {
        if (mDownloadClickListener != null) {
            mDownloadClickListener.onDownloadClick();
        }
    }

    public interface CaptureClickListener {
        /**
         * 点击截图
         */
        void onCaptureClick();
    }

    public interface DownloadClickListener {
        /**
         * 点击下载录像
         */
        void onDownloadClick();
    }

    public void setCaptureClickListener(CaptureClickListener captureClickListener) {
        mCaptureClickListener = captureClickListener;
    }

    public void setDownloadClickListener(DownloadClickListener downloadClickListener) {
        mDownloadClickListener = downloadClickListener;
    }

    public interface AiVideoCaptureListener extends GSYVideoShotSaveListener{
        @Override
        void result(boolean success, File file);
    }
}

package com.sensoro.city_camera.widget;

/**
 * @author : bin.tian
 * date   : 2019-07-02
 */
public class AiGSYVideoPlayerUtil {
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

    private CaptureClickListener mCaptureClickListener;
    private DownloadClickListener mDownloadClickListener;

    private static AiGSYVideoPlayerUtil mAiGSYVideoPlayerUtil;
    private AiGSYVideoPlayerUtil(){}
    public static AiGSYVideoPlayerUtil getInstance(){
        if (mAiGSYVideoPlayerUtil == null){
            synchronized (AiGSYVideoPlayerUtil.class){
                if (mAiGSYVideoPlayerUtil == null){
                    mAiGSYVideoPlayerUtil = new AiGSYVideoPlayerUtil();
                }
            }
        }
        return mAiGSYVideoPlayerUtil;
    }

    public void setCaptureClickListener(CaptureClickListener captureClickListener) {
        mCaptureClickListener = captureClickListener;
    }

    public void setDownloadClickListener(DownloadClickListener downloadClickListener) {
        mDownloadClickListener = downloadClickListener;
    }

    public void capture() {
        if (mCaptureClickListener != null) {
            mCaptureClickListener.onCaptureClick();
        }
    }

    public void download() {
        if (mDownloadClickListener != null) {
            mDownloadClickListener.onDownloadClick();
        }
    }

    public void clearListener(){
        mCaptureClickListener = null;
        mDownloadClickListener = null;
    }
}

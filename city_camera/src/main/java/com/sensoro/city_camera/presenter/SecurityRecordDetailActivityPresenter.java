package com.sensoro.city_camera.presenter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sensoro.city_camera.IMainViews.ISecurityRecordDetailActivityView;
import com.sensoro.city_camera.R;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.download.DownloadListener;
import com.sensoro.common.server.download.DownloadUtil;
import com.sensoro.common.server.security.bean.SecurityRecord;
import com.sensoro.common.server.security.response.SecurityWarnRecordResp;
import com.sensoro.common.utils.DateUtil;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.shuyu.gsyvideoplayer.video.base.GSYVideoView.CURRENT_STATE_PAUSE;

/**
 * @author bin.tian
 */
public class SecurityRecordDetailActivityPresenter extends BasePresenter<ISecurityRecordDetailActivityView> implements DownloadListener {
    private Activity mActivity;
    private String mSecurityWarnId;
    private SecurityRecord mSecurityRecord;
    private DownloadUtil mDownloadUtil;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        EventBus.getDefault().register(this);
        mSecurityWarnId = mActivity.getIntent().getStringExtra("id");

        requestVideo();
    }

    private void setLastCover(String coverUrl) {
        Glide.with(mActivity).
                load(coverUrl)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))//缓存全尺寸
                .into(getView().getImageView());

    }

    /**
     * 网络改变状态
     *
     * @param eventData
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        if (code == Constants.NetworkInfo) {
            int data = (int) eventData.data;

            switch (data) {

                case ConnectivityManager.TYPE_WIFI:
                    getView().getPlayView().setCityPlayState(-1);
                    getView().setVerOrientationUtil(true);

                    if (getView().getPlayView().getCurrentState() == CURRENT_STATE_PAUSE) {
                        getView().getPlayView().clickCityStartIcon();

                        GSYVideoManager.onResume();

                    } else {
                        doRetry();

                    }

                    break;

                case ConnectivityManager.TYPE_MOBILE:
                    getView().setVerOrientationUtil(false);

                    if (isAttachedView()) {
                        getView().getPlayView().setCityPlayState(2);

                        getView().getPlayView().getPlayAndRetryBtn().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                getView().getPlayView().setCityPlayState(-1);
                                if (getView().getPlayView().getCurrentState() == CURRENT_STATE_PAUSE) {
                                    getView().getPlayView().clickCityStartIcon();
                                    getView().setVerOrientationUtil(true);

                                }
                                GSYVideoManager.onResume(true);


                            }
                        });

                        getView().backFromWindowFull();

                    }

                    break;

                default:
                    if (isAttachedView()) {


                        getView().backFromWindowFull();
                        getView().getPlayView().setCityPlayState(1);
                        getView().setVerOrientationUtil(false);
                    }
                    break;


            }
        } else if (code == Constants.VIDEO_START) {

            getView().setVerOrientationUtil(true);

            if (getView().getPlayView().getCurrentState() == CURRENT_STATE_PAUSE) {
                getView().getPlayView().clickCityStartIcon();

                GSYVideoManager.onResume();

            } else {
                doRetry();

            }

        } else if (code == Constants.VIDEO_STOP) {

            getView().setVerOrientationUtil(false);
            GSYVideoManager.onPause();

            getView().backFromWindowFull();


        }
    }

    private void setTitle(String title) {
        try {
            long l = Long.parseLong(title);
            String time = DateUtil.getStrTime_ymd_hm_ss(l);
            getView().setTitle(time);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            getView().setTitle(mActivity.getString(R.string.person_avatar_video));
        }
    }

    private void requestVideo() {
        getView().showProgressDialog();

        RetrofitServiceHelper
                .getInstance()
                .getSecurityWarnRecord(mSecurityWarnId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<SecurityWarnRecordResp>(null) {
                    @Override
                    public void onCompleted(SecurityWarnRecordResp securityWarnRecordResp) {
                        List<SecurityRecord> recordList = securityWarnRecordResp.data.list;
                        if (recordList != null && !recordList.isEmpty()) {
                            SecurityRecord securityRecord = recordList.get(0);
                            if (securityRecord != null) {
                                mSecurityRecord = securityRecord;
                                setTitle(securityRecord.createTime);
                                setLastCover(securityRecord.coverUrl);
                                if (isAttachedView()) {
                                    getView().startPlayLogic(securityRecord.mediaUrl);
                                }
                            }

                        } else {
                            if (isAttachedView()) {
                                getView().toastShort(mActivity.getString(R.string.obtain_video_fail));
                            }
                        }

                        if (isAttachedView()) {
                            getView().dismissProgressDialog();
                        }
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        if (isAttachedView()) {
                            getView().playError(errorMsg);
                            getView().dismissProgressDialog();
                        }
                    }
                });


    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    public void doRetry() {
        requestVideo();
    }

    public void doCapture() {
        if (mSecurityRecord == null) {
            return;
        }
        String fileName = System.currentTimeMillis() + ".jpeg";
        String[] strings = mSecurityRecord.coverUrl.split("\\?");
        if (strings.length > 0) {
            fileName = strings[0];
            String[] strings1 = fileName.split("/");
            if (strings1.length > 0) {
                fileName = strings1[strings1.length - 1];
            }
        }
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath(), fileName);
        getView().capture(file);
    }

    public void showDownloadDialog() {
        if (mSecurityRecord != null && isAttachedView()) {
            getView().showDownloadDialog(mSecurityRecord.videoSize);
        }
    }

    public void doDownload() {
        if (mSecurityRecord == null) {
            return;
        }
        if (isAttachedView()){
            getView().setDownloadStartState(mSecurityRecord.videoSize);
        }
        String fileName = System.currentTimeMillis() + ".mp4";
        String[] strings = mSecurityRecord.mediaUrl.split("\\?");
        if (strings.length > 0) {
            fileName = strings[0];
            String[] strings1 = fileName.split("/");
            if (strings1.length > 0) {
                fileName = strings1[strings1.length - 1];
            }
        }
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath(), fileName);

        if (mDownloadUtil == null) {
            mDownloadUtil = new DownloadUtil(this);
        }

        mDownloadUtil.downloadFile(mSecurityRecord.mediaUrl, file.getAbsolutePath());
    }

    public void doDownloadCancel() {

    }

    @Override
    public void onFinish(File file) {
        if (isAttachedView()) {
            getView().doDownloadFinish();
        }

        insertVideoToMediaStore(file.getAbsolutePath(), true);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        mActivity.sendBroadcast(intent);
    }

    @Override
    public void onProgress(int progress, String totalBytesRead, String fileSize) {
        if (isAttachedView()) {
            getView().updateDownLoadProgress(progress, totalBytesRead, fileSize);
        }
    }

    @Override
    public void onFailed(String errMsg) {
        if (isAttachedView()) {
            getView().setDownloadErrorState();
        }
    }

    private void insertVideoToMediaStore(String filePath, boolean isVideo) {
        long createTime = System.currentTimeMillis();
        ContentValues values = initCommonContentValues(filePath, createTime);
        values.put(MediaStore.Video.VideoColumns.DATE_TAKEN, createTime);
        if(isVideo){
            values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
        } else {
            values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        }
        mActivity.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
    }

    private ContentValues initCommonContentValues(String filePath, long time) {
        ContentValues values = new ContentValues();
        File saveFile = new File(filePath);
        values.put(MediaStore.MediaColumns.TITLE, saveFile.getName());
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, saveFile.getName());
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, time);
        values.put(MediaStore.MediaColumns.DATE_ADDED, time);
        values.put(MediaStore.MediaColumns.DATA, saveFile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.SIZE, saveFile.length());
        return values;
    }

    public void onCaptureFinished(File file) {
        insertVideoToMediaStore(file.getAbsolutePath(), false);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        mActivity.sendBroadcast(intent);
    }
}

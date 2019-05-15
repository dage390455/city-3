package com.sensoro.smartcity.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IAlarmCameraVideoDetailActivityView;
import com.sensoro.smartcity.server.download.DownloadListener;
import com.sensoro.smartcity.server.download.DownloadUtil;

import java.io.File;

import io.reactivex.annotations.NonNull;

public class AlarmCameraVideoDetailActivityPresenter extends BasePresenter<IAlarmCameraVideoDetailActivityView>
implements DownloadListener{
    private Context mActivity;
    private DownloadUtil mDownloadUtil;
    private String downLoadFilePath;

    @Override
    public void initData(Context context) {
        mActivity = context;
        getView().doPlayLive("http://vjs.zencdn.net/v/oceans.mp4");
    }

    @Override
    public void onDestroy() {

    }

    public void doRefresh() {

    }

    public void doItemClick(int position) {


    }

    public void regainGetCameraState(String sn) {

    }

    public void doDownload() {
        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        downLoadFilePath = dcim.getAbsolutePath() + "/Camera/oceans.mp4";

        getView().setDownloadStartState();
        if (mDownloadUtil == null) {
            mDownloadUtil = new DownloadUtil(this);
        }
        mDownloadUtil.downloadFile("oceans.mp4", downLoadFilePath);


    }
    @Override
    public void onFinish(final File file) {
        if (isAttachedView()) {
            getView().doDownloadFinish();
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(file));
            mActivity.sendBroadcast(intent);
        }
    }

    @Override
    public void onProgress(int progress, String totalBytesRead, String fileSize) {
        if (isAttachedView()) {
            getView().updateDownLoadProgress(progress,totalBytesRead,fileSize);

        }
    }

    @Override
    public void onFailed(String errMsg) {
        if (isAttachedView()) {
            getView().setDownloadErrorState();
        }
    }

    public void doDownloadCancel() {
        if (mDownloadUtil != null) {
            mDownloadUtil.cancelDownload();
        }

        if (!TextUtils.isEmpty(downLoadFilePath)) {
            File file = new File(downLoadFilePath);
            if (file.exists()) {
                boolean delete = file.delete();
            }
        }

    }
}

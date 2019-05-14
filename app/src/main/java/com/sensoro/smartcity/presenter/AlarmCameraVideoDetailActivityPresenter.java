package com.sensoro.smartcity.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IAlarmCameraVideoDetailActivityView;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;

import java.io.File;

public class AlarmCameraVideoDetailActivityPresenter extends BasePresenter<IAlarmCameraVideoDetailActivityView> {
    private Context mActivity;

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
        final String s = dcim.getAbsolutePath() + "/Camera/oceans.mp4";
        getView().setDownloadState();
        RetrofitServiceHelper.getInstance().downloadDeviceFirmwareFile("http://vjs.zencdn.net/v/oceans.mp4", s, new CityObserver<Boolean>(this) {
            @Override
            public void onCompleted(Boolean aBoolean) {
                if (aBoolean) {
                    Log.e("hcs",":::下载完成");
                    Uri localUri = Uri.parse("file://"+ s);
                    Intent localIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                    localIntent.setData(localUri);
                    mActivity.sendBroadcast(localIntent);
                }
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {

            }
        });
    }
}

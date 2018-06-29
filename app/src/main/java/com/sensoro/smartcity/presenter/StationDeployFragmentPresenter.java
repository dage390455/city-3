package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.DeployActivity;
import com.sensoro.smartcity.activity.DeployManualActivity;
import com.sensoro.smartcity.activity.DeployResultActivity;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IStationDeployFragmentView;
import com.sensoro.smartcity.iwidget.IOndestroy;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.response.CityObserver;
import com.sensoro.smartcity.server.response.StationInfo;
import com.sensoro.smartcity.server.response.StationInfoRsp;
import com.sensoro.smartcity.util.LogUtils;

import java.io.IOException;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.Context.VIBRATOR_SERVICE;
import static com.sensoro.smartcity.constant.Constants.EXTRA_DEVICE_INFO;
import static com.sensoro.smartcity.constant.Constants.EXTRA_IS_STATION_DEPLOY;
import static com.sensoro.smartcity.constant.Constants.EXTRA_SENSOR_RESULT;
import static com.sensoro.smartcity.constant.Constants.EXTRA_SENSOR_SN_RESULT;
import static com.sensoro.smartcity.constant.Constants.REQUEST_CODE_STATION_DEPLOY;

public class StationDeployFragmentPresenter extends BasePresenter<IStationDeployFragmentView> implements
        IOndestroy, MediaPlayer.OnErrorListener {
    private Activity mContext;
    private static final float BEEP_VOLUME = 0.10f;
    private MediaPlayer mediaPlayer;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        mediaPlayer = buildMediaPlayer(mContext);
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void openSNTextAc() {
        Intent intent = new Intent(mContext, DeployManualActivity.class);
        intent.putExtra(EXTRA_IS_STATION_DEPLOY, true);
        getView().startACForResult(intent, REQUEST_CODE_STATION_DEPLOY);
    }

    private void scanFinish(final String scanSerialNumber) {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getStationDetail(scanSerialNumber).subscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<StationInfoRsp>() {
            @Override
            public void onCompleted() {
                getView().dismissProgressDialog();
            }

            @Override
            public void onNext(StationInfoRsp stationInfoRsp) {
                refresh(stationInfoRsp);
            }

            @Override
            public void onErrorMsg(String errorMsg) {
                getView().dismissProgressDialog();
                if (errorMsg.equals("4010104")) {
                    freshError(scanSerialNumber);
                } else {
                    getView().toastShort(errorMsg);
                    getView().startScan();
                }


            }
        });
    }

    private void freshError(String scanSN) {
        //
        Intent intent = new Intent();
        intent.setClass(mContext, DeployResultActivity.class);
        intent.putExtra(EXTRA_SENSOR_RESULT, -1);
        intent.putExtra(EXTRA_SENSOR_SN_RESULT, scanSN);
        intent.putExtra(EXTRA_IS_STATION_DEPLOY, true);
        getView().startACForResult(intent, REQUEST_CODE_STATION_DEPLOY);
    }

    //    private void refresh(DeviceInfoListRsp response) {
//        try {
//            Intent intent = new Intent();
//            if (response.getData().size() > 0) {
//                intent.setClass(mContext, DeployActivity.class);
//                intent.putExtra(EXTRA_DEVICE_INFO, response.getData().get(0));
//                intent.putExtra(EXTRA_IS_STATION_DEPLOY, true);
//                intent.putExtra("uid", mContext.getIntent().getStringExtra("uid"));
//                getView().startACForResult(intent, REQUEST_CODE_STATION_DEPLOY);
//            } else {
//                intent.setClass(mContext, DeployResultActivity.class);
//                intent.putExtra(EXTRA_SENSOR_RESULT, -1);
//
//                getView().startACForResult(intent, REQUEST_CODE_STATION_DEPLOY);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    private void refresh(StationInfoRsp stationInfoRsp) {
        try {
            StationInfo stationInfo = stationInfoRsp.getData();
            double[] lonlat = stationInfo.getLonlat();
//        double[] lonlatLabel = stationInfo.getLonlatLabel();
            String name = stationInfo.getName();
            String sn = stationInfo.getSn();
            String[] tags = stationInfo.getTags();

            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setSn(sn);
            deviceInfo.setTags(tags);
            deviceInfo.setLonlat(lonlat);
            if (!TextUtils.isEmpty(name)) {
                deviceInfo.setName(name);
            }
            Intent intent = new Intent();
            intent.setClass(mContext, DeployActivity.class);
            intent.putExtra(EXTRA_DEVICE_INFO, deviceInfo);
            intent.putExtra(EXTRA_IS_STATION_DEPLOY, true);
            intent.putExtra("uid", mContext.getIntent().getStringExtra("uid"));
            getView().startACForResult(intent, REQUEST_CODE_STATION_DEPLOY);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) mContext.getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    private void playVoice() {
        vibrate();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void processResult(String result) {
        playVoice();
        int currentIndex = ((MainActivity) mContext).getSensoroPager().getCurrentItem();
        System.out.println("currentIndex==>" + currentIndex);
        if (currentIndex != 4) {
            return;
        }
        if (TextUtils.isEmpty(result)) {
            getView().toastShort(mContext.getResources().getString(R.string.scan_failed));
            return;
        }
        String scanSerialNumber = parseResultMac(result);
        if (scanSerialNumber == null) {
            getView().toastShort(mContext.getResources().getString(R.string.invalid_qr_code));
        } else {
            scanFinish(scanSerialNumber);
        }
    }

    private String parseResultMac(String result) {

        String serialNumber = null;
        if (result != null) {
            String[] data = null;
            String type = null;
            data = result.split("\\|");
            // if length is 2, it is fault-tolerant hardware.
            type = data[0];
//            if (type.length() == 2) {
//                serialNumber = data[1];
//            } else {
//                serialNumber = data[0].substring(data[0].length() - 12);
//            }
            serialNumber = type;
        }
        return serialNumber;
    }

    private MediaPlayer buildMediaPlayer(Context activity) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try (AssetFileDescriptor file = activity.getResources().openRawResourceFd(R.raw.beep)) {
            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setLooping(false);
            mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
            mediaPlayer.prepare();
            return mediaPlayer;
        } catch (IOException ioe) {
            LogUtils.loge(this, ioe.getMessage());
            mediaPlayer.release();
            return null;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }
}

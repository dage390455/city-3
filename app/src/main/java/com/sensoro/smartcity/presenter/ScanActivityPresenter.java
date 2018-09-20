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
import com.sensoro.smartcity.activity.DeployDeviceDetailActivity;
import com.sensoro.smartcity.activity.DeployManualActivity;
import com.sensoro.smartcity.activity.DeployResultActivityTest;
import com.sensoro.smartcity.activity.ScanLoginResultActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IScanActivityView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.server.response.StationInfo;
import com.sensoro.smartcity.server.response.StationInfoRsp;
import com.sensoro.smartcity.util.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.Context.VIBRATOR_SERVICE;

public class ScanActivityPresenter extends BasePresenter<IScanActivityView> implements IOnCreate, Constants,
        MediaPlayer.OnErrorListener {
    private Activity mContext;
    private static final float BEEP_VOLUME = 0.10f;
    private MediaPlayer mediaPlayer;
    private int scanType = -1;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        scanType = mContext.getIntent().getIntExtra("type", -1);
        mediaPlayer = buildMediaPlayer(mContext);
        updateTitle();
    }

    private void updateTitle() {
        if (scanType != -1) {
            if (Constants.TYPE_SCAN_DEPLOY_DEVICE == scanType) {
                getView().updateTitleText("设备部署");
                getView().updateQrTipText("对准传感器上的二维码，即可自动扫描");
            } else if (Constants.TYPE_SCAN_LOGIN == scanType) {
                getView().updateTitleText("扫码登录");
                getView().updateQrTipText("对准登录用二维码，即可自动扫描");
                getView().setBottomVisible(false);
            }else{
                getView().updateTitleText("设备部署");
                getView().updateQrTipText("对准传感器上的二维码，即可自动扫描");
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        //TODO 可以修改以此种方式传递，方便管理
        int code = eventData.code;
//        Object data = eventData.data;
        if (code == EVENT_DATA_DEPLOY_RESULT_FINISH) {
            getView().finishAc();
        }
//        LogUtils.loge(this, eventData.toString());
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void openSNTextAc() {
        Intent intent = new Intent(mContext, DeployManualActivity.class);
        intent.putExtra(EXTRA_IS_STATION_DEPLOY, false);
        getView().startAC(intent);
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) mContext.getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
    }

    private void playVoice() {
        vibrate();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void processResult(String result) {
        playVoice();
        if (TextUtils.isEmpty(result)) {
            getView().toastShort(mContext.getResources().getString(R.string.scan_failed));
            getView().startScan();
            return;
        }
        if (scanType != -1) {
            if (Constants.TYPE_SCAN_DEPLOY_DEVICE == scanType) {
                String scanSerialNumber = parseResultMac(result);
                if (scanSerialNumber == null) {
                    getView().toastShort(mContext.getResources().getString(R.string.invalid_qr_code));
                    getView().startScan();
                } else {
                    if (scanSerialNumber.length() == 16) {
                        scanDeviceFinish(scanSerialNumber,false);
                    } else {
                        getView().toastShort(mContext.getResources().getString(R.string.invalid_qr_code));
                        getView().startScan();
                    }
                }
            } else if (Constants.TYPE_SCAN_LOGIN == scanType) {
                LogUtils.loge("result = " + result);
                scanLoginFinish(result);
            }else if(Constants.TYPE_SCAN_CHANGE_DEVICE == scanType){
                String scanSerialNumber = parseResultMac(result);
                if (scanSerialNumber == null) {
                    getView().toastShort(mContext.getResources().getString(R.string.invalid_qr_code));
                    getView().startScan();
                } else {
                    if (scanSerialNumber.length() == 16) {
                        scanDeviceFinish(scanSerialNumber,true);
                    } else {
                        getView().toastShort(mContext.getResources().getString(R.string.invalid_qr_code));
                        getView().startScan();
                    }
                }
            }
        }

    }

    private void scanLoginFinish(final String qrcodeId) {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getLoginScanResult(qrcodeId).subscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseBase>(this) {
            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
                getView().startScan();
            }

            @Override
            public void onCompleted(ResponseBase responseBase) {
                if (responseBase.getErrcode() == 0) {
                    try {
                        LogUtils.loge("qrcodeId = " + qrcodeId);
                        Intent intent = new Intent();
                        intent.setClass(mContext, ScanLoginResultActivity.class);
                        intent.putExtra("qrcodeId", qrcodeId);
                        getView().startAC(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    getView().toastShort("请重新扫描后重试");
                    getView().startScan();
                }
                getView().dismissProgressDialog();
            }
        });
    }

    private String parseResultMac(String result) {

        String serialNumber = null;
        if (result != null) {
            String[] data;
            String type;
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

    private void scanDeviceFinish(final String scanSerialNumber, final boolean isChangeDevice) {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getDeviceDetailInfoList(scanSerialNumber.toUpperCase(), null, 1).subscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>(this) {
            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                    getView().toastShort(errorMsg);
                    getView().startScan();
                } else if (errorCode == 4013101 || errorCode == 4000013) {
                    //TODO 控制逻辑
//                    freshError(scanSerialNumber, null, false);
                    scanStationFinish(scanSerialNumber);
                } else {
                    //TODO 控制逻辑
                    freshError(scanSerialNumber, errorMsg, false);
//                    scanStationFinish(scanSerialNumber);
                }
            }

            @Override
            public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {
                try {
                    if (deviceInfoListRsp.getData().size() > 0) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, DeployDeviceDetailActivity.class);
                        intent.putExtra(EXTRA_DEVICE_INFO, deviceInfoListRsp.getData().get(0));
                        intent.putExtra(EXTRA_IS_STATION_DEPLOY, false);
                        intent.putExtra("uid", mContext.getIntent().getStringExtra("uid"));
                        intent.putExtra(EXTRA_IS_CHANGE_DEVICE,isChangeDevice);
                        getView().startAC(intent);
                    } else {
                        scanStationFinish(scanSerialNumber);
//                        freshError(scanSerialNumber, null, false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getView().dismissProgressDialog();
            }
        });
    }

    private void scanStationFinish(final String scanSerialNumber) {
        RetrofitServiceHelper.INSTANCE.getStationDetail(scanSerialNumber.toUpperCase()).subscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<StationInfoRsp>(this) {
            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                    getView().toastShort(errorMsg);
                    getView().startScan();
                } else if (errorCode == 4013101 || errorCode == 4000013) {
                    freshError(scanSerialNumber, null, true);
                } else {
                    freshError(scanSerialNumber, errorMsg, true);
                }
            }

            @Override
            public void onCompleted(StationInfoRsp stationInfoRsp) {
                try {
                    StationInfo stationInfo = stationInfoRsp.getData();
                    double[] lonlat = stationInfo.getLonlat();
//        double[] lonlatLabel = stationInfo.getLonlatLabel();
                    String name = stationInfo.getName();
                    String sn = stationInfo.getSn();
                    String[] tags = stationInfo.getTags();
                    long updatedTime = stationInfo.getUpdatedTime();
                    DeviceInfo deviceInfo = new DeviceInfo();
                    deviceInfo.setSn(sn);
                    deviceInfo.setTags(tags);
                    deviceInfo.setLonlat(lonlat);
                    deviceInfo.setUpdatedTime(updatedTime);
                    if (!TextUtils.isEmpty(name)) {
                        deviceInfo.setName(name);
                    }
                    Intent intent = new Intent();
                    intent.setClass(mContext, DeployDeviceDetailActivity.class);
                    intent.putExtra(EXTRA_DEVICE_INFO, deviceInfo);
                    intent.putExtra(EXTRA_IS_STATION_DEPLOY, true);
                    intent.putExtra("uid", mContext.getIntent().getStringExtra("uid"));
                    getView().startAC(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getView().dismissProgressDialog();
            }
        });
    }

    private void freshError(String scanSN, String errorInfo, boolean isStation) {
        //
        Intent intent = new Intent();
        intent.setClass(mContext, DeployResultActivityTest.class);
        intent.putExtra(EXTRA_SENSOR_RESULT, -1);
        intent.putExtra(EXTRA_SENSOR_SN_RESULT, scanSN);
        intent.putExtra(EXTRA_IS_STATION_DEPLOY, isStation);
        if (!TextUtils.isEmpty(errorInfo)) {
            intent.putExtra(EXTRA_SENSOR_RESULT_ERROR, errorInfo);
        }

        getView().startAC(intent);
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

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }
}

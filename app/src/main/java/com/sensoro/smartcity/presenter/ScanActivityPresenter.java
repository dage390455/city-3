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
import com.sensoro.smartcity.activity.DeployManualActivity;
import com.sensoro.smartcity.activity.DeployMonitorDetailActivity;
import com.sensoro.smartcity.activity.DeployResultActivity;
import com.sensoro.smartcity.activity.InspectionActivity;
import com.sensoro.smartcity.activity.InspectionExceptionDetailActivity;
import com.sensoro.smartcity.activity.ScanLoginResultActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IScanActivityView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.InspectionIndexTaskInfo;
import com.sensoro.smartcity.server.bean.InspectionTaskDeviceDetail;
import com.sensoro.smartcity.server.bean.InspectionTaskDeviceDetailModel;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.InspectionTaskDeviceDetailRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.util.DeployAnalyzerUtils;
import com.sensoro.smartcity.util.LogUtils;
import com.yixia.camera.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.Context.VIBRATOR_SERVICE;

public class ScanActivityPresenter extends BasePresenter<IScanActivityView> implements IOnCreate, Constants,
        MediaPlayer.OnErrorListener {
    private Activity mContext;
    private static final float BEEP_VOLUME = 0.10f;
    private MediaPlayer mediaPlayer;
    private int scanType = -1;
    private InspectionTaskDeviceDetail mDeviceDetail;
    private InspectionIndexTaskInfo mTaskInfo;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        scanType = mContext.getIntent().getIntExtra(EXTRA_SCAN_ORIGIN_TYPE, -1);
        mDeviceDetail = (InspectionTaskDeviceDetail) mContext.getIntent().getSerializableExtra(EXTRA_INSPECTION_DEPLOY_OLD_DEVICE_INFO);
        mTaskInfo = (InspectionIndexTaskInfo) mContext.getIntent().getSerializableExtra(EXTRA_INSPECTION_INDEX_TASK_INFO);
        mediaPlayer = buildMediaPlayer(mContext);
        updateTitle();
    }

    private void updateTitle() {
        switch (scanType) {
            case TYPE_SCAN_DEPLOY_STATION:
            case TYPE_SCAN_DEPLOY_DEVICE:
                //设备部署
                getView().updateTitleText("设备部署");
                getView().updateQrTipText("对准传感器上的二维码，即可自动扫描");
                break;
            case TYPE_SCAN_LOGIN:
                getView().updateTitleText("扫码登录");
                getView().updateQrTipText("对准登录用二维码，即可自动扫描");
                getView().setBottomVisible(false);
                break;
            case TYPE_SCAN_DEPLOY_DEVICE_CHANGE:
                //巡检设备更换
                getView().updateTitleText("设备更换");
                getView().updateQrTipText("对准设备上的二维码，即可自动扫描");
                break;
            case TYPE_SCAN_INSPECTION:
                //扫描巡检设备
                getView().updateTitleText("设备巡检");
                getView().updateQrTipText("对准设备上的二维码，即可自动扫描");
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        //TODO 可以修改以此种方式传递，方便管理
        int code = eventData.code;
//        Object data = eventData.data;
        if (code == EVENT_DATA_DEPLOY_RESULT_FINISH) {
            getView().finishAc();
        } else if (code == EVENT_DATA_DEPLOY_RESULT_CONTINUE) {
            if (TYPE_SCAN_DEPLOY_DEVICE_CHANGE == scanType) {
                getView().finishAc();
            }
        } else if (code == EVENT_DATA_SCAN_LOGIN_SUCCESS) {
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
        intent.putExtra(EXTRA_INSPECTION_INDEX_TASK_INFO, mTaskInfo);
        intent.putExtra(EXTRA_INSPECTION_DEPLOY_OLD_DEVICE_INFO, mDeviceDetail);
        intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, scanType);
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
        switch (scanType) {
            case TYPE_SCAN_DEPLOY_STATION:
                //基站部署
            case TYPE_SCAN_DEPLOY_DEVICE:
                //设备部署
                String scanSerialNumber = parseResultMac(result);
                if (scanSerialNumber == null) {
                    getView().toastShort(mContext.getResources().getString(R.string.invalid_qr_code));
                    getView().startScan();
                } else {
                    if (scanSerialNumber.length() == 16) {
                        scanDeviceFinish(scanSerialNumber);
                    } else {
                        getView().toastShort(mContext.getResources().getString(R.string.invalid_qr_code));
                        getView().startScan();
                    }
                }
                break;
            case TYPE_SCAN_LOGIN:
                //登录
                LogUtils.loge("result = " + result);
                scanLoginFinish(result);
                break;
            case TYPE_SCAN_DEPLOY_DEVICE_CHANGE:
                //巡检设备更换
                String scanSnNewDevice = parseResultMac(result);
                if (scanSnNewDevice == null) {
                    getView().toastShort(mContext.getResources().getString(R.string.invalid_qr_code));
                    getView().startScan();
                } else {
                    if (scanSnNewDevice.length() == 16) {
                        changeDevice(scanSnNewDevice);
                    } else {
                        getView().toastShort(mContext.getResources().getString(R.string.invalid_qr_code));
                        getView().startScan();
                    }
                }
                break;
            case TYPE_SCAN_INSPECTION:
                //扫描巡检设备
                String scanInspectionDevice = parseResultMac(result);
                if (scanInspectionDevice == null) {
                    getView().toastShort(mContext.getResources().getString(R.string.invalid_qr_code));
                    getView().startScan();
                } else {
                    if (scanInspectionDevice.length() == 16) {
                        scanInspectionDevice(scanInspectionDevice);
                    } else {
                        getView().toastShort(mContext.getResources().getString(R.string.invalid_qr_code));
                        getView().startScan();
                    }
                }
                break;
            default:
                break;
        }

    }

    private void scanInspectionDevice(String scanInspectionDevice) {
        getView().showProgressDialog();
        //TODO 暂时处理
        RetrofitServiceHelper.INSTANCE.getInspectionDeviceList(mTaskInfo.getId(), null, scanInspectionDevice.toUpperCase(), null, null, null, null).
                subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<InspectionTaskDeviceDetailRsp>(this) {
            @Override
            public void onCompleted(InspectionTaskDeviceDetailRsp inspectionTaskDeviceDetailRsp) {
                getView().dismissProgressDialog();
                InspectionTaskDeviceDetailModel data = inspectionTaskDeviceDetailRsp.getData();
                List<InspectionTaskDeviceDetail> devices = data.getDevices();
                if (devices != null && devices.size() > 0) {
                    InspectionTaskDeviceDetail deviceDetail = devices.get(0);
                    Intent intent = new Intent();
                    int status = deviceDetail.getStatus();
                    switch (status) {
                        case 0:
                            intent.setClass(mContext, InspectionActivity.class);
                            break;
                        case 1:
                            getView().toastShort("此设备已巡检完毕，且状态正常");
                            getView().startScan();
                            return;
                        case 2:
                            intent.setClass(mContext, InspectionExceptionDetailActivity.class);
                            break;
                    }
                    intent.putExtra(EXTRA_INSPECTION_TASK_ITEM_DEVICE_DETAIL, deviceDetail);
                    getView().startAC(intent);
                } else {
                    getView().toastShort("此设备未在巡检任务中");
                    getView().startScan();
                }

            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
                getView().startScan();
            }
        });
//        RetrofitServiceHelper.INSTANCE.getInspectionDeviceDetail(null, scanInspectionDevice, mTaskInfo.getId(), 1)
//                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<InspectionTaskExceptionDeviceRsp>() {
//            @Override
//            public void onCompleted(InspectionTaskExceptionDeviceRsp response) {
//                getView().dismissProgressDialog();
//                InspectionTaskExceptionDeviceModel taskDevice = response.getData();
//                int status = taskDevice.getStatus();
//                Intent intent = new Intent();
//                String deviceType = taskDevice.getDeviceType();
//                String sn = taskDevice.getSn();
//                String taskId = taskDevice.getTaskId();
//                InspectionTaskExceptionDeviceModel.DeviceBean device = taskDevice.getDevice();
//                String name = device.getName();
//                List<Double> lonlat = device.getLonlat();
//                List<String> tags = device.getTags();
//                String id = taskDevice.get_id();
////                private String name;
////                private String taskId;
////                private String sn;
////                private String deviceType;
////                private int status;
////                private String timecost;
////                private InspectionTaskDeviceDetail.MalfunctionBean malfunction;
////                private List<Double> lonlat;
////                private List<String> tags;
//
//                InspectionTaskDeviceDetail deviceDetail = new InspectionTaskDeviceDetail();
//                deviceDetail.setId(id);
//                deviceDetail.setDeviceType(deviceType);
//                deviceDetail.setLonlat(lonlat);
//                deviceDetail.setSn(sn);
//                deviceDetail.setTaskId(taskId);
//                deviceDetail.setName(name);
//                deviceDetail.setTags(tags);
//                deviceDetail.setStatus(status);
//                switch (status) {
//                    case 0:
//                        intent.setClass(mContext, InspectionActivity.class);
//                        break;
//                    case 1:
//                    case 2:
//                        intent.setClass(mContext, InspectionExceptionDetailActivity.class);
//                        break;
//                }
//                intent.putExtra(EXTRA_INSPECTION_TASK_ITEM_DEVICE_DETAIL, deviceDetail);
//                getView().startAC(intent);
//
//            }
//
//            @Override
//            public void onErrorMsg(int errorCode, String errorMsg) {
//                getView().dismissProgressDialog();
//                getView().toastShort(errorMsg);
//            }
//        });
    }

    private void changeDevice(final String scanSnNewDevice) {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getDeviceDetailInfoList(scanSnNewDevice.toUpperCase(), null, 1).subscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>() {
            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                    getView().toastShort(errorMsg);
                } else if (errorCode == 4013101 || errorCode == 4000013) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, DeployResultActivity.class);
                    intent.putExtra(EXTRA_SENSOR_RESULT, -1);
                    intent.putExtra(EXTRA_SENSOR_SN_RESULT, scanSnNewDevice);
                    intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, TYPE_SCAN_DEPLOY_DEVICE_CHANGE);
                    getView().startAC(intent);
                } else {
                    //TODO 控制逻辑
                    Intent intent = new Intent();
                    intent.setClass(mContext, DeployResultActivity.class);
                    intent.putExtra(EXTRA_SENSOR_RESULT, -1);
                    intent.putExtra(EXTRA_SENSOR_SN_RESULT, scanSnNewDevice);
                    intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, TYPE_SCAN_DEPLOY_DEVICE_CHANGE);
                    intent.putExtra(EXTRA_SENSOR_RESULT_ERROR, errorMsg);
                    getView().startAC(intent);
                }
            }

            @Override
            public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {
                getView().dismissProgressDialog();
                try {
                    if (deviceInfoListRsp.getData().size() > 0) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, DeployMonitorDetailActivity.class);
                        intent.putExtra(EXTRA_DEVICE_INFO, deviceInfoListRsp.getData().get(0));
                        intent.putExtra(EXTRA_INSPECTION_DEPLOY_OLD_DEVICE_INFO, mDeviceDetail);
                        intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, TYPE_SCAN_DEPLOY_DEVICE_CHANGE);
                        intent.putExtra("uid", mContext.getIntent().getStringExtra("uid"));
                        getView().startAC(intent);
                    } else {
                        Intent intent = new Intent();
                        intent.setClass(mContext, DeployResultActivity.class);
                        intent.putExtra(EXTRA_SENSOR_RESULT, -1);
                        intent.putExtra(EXTRA_SENSOR_SN_RESULT, scanSnNewDevice);
                        intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, TYPE_SCAN_DEPLOY_DEVICE_CHANGE);
                        getView().startAC(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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

    private void scanDeviceFinish(final String scanSerialNumber) {
        getView().showProgressDialog();
        DeployAnalyzerUtils.INSTANCE.getDeployAnalyzerResult(scanSerialNumber.toUpperCase(), mContext, new DeployAnalyzerUtils.OnDeployAnalyzerListener() {
            @Override
            public void onSuccess(Intent intent) {
                getView().dismissProgressDialog();
                getView().startAC(intent);
            }

            @Override
            public void onError(int errType, Intent intent, String errMsg) {
                getView().dismissProgressDialog();
                if (intent != null) {
                    getView().startAC(intent);
                } else {
                    getView().toastShort(errMsg);
                }
            }
        });
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

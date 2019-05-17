package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.sensoro.libbleserver.ble.callback.SensoroConnectionCallback;
import com.sensoro.libbleserver.ble.callback.SensoroWriteCallback;
import com.sensoro.libbleserver.ble.connection.SensoroDeviceConnection;
import com.sensoro.libbleserver.ble.constants.CmdType;
import com.sensoro.libbleserver.ble.constants.ResultCode;
import com.sensoro.libbleserver.ble.entity.BLEDevice;
import com.sensoro.libbleserver.ble.entity.SensoroDevice;
import com.sensoro.libbleserver.ble.proto.ProtoMsgTest1U1;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.ISignalCheckActivityView;
import com.sensoro.common.iwidget.IOnStart;
import com.sensoro.smartcity.model.DeployAnalyzerModel;
import com.sensoro.smartcity.model.SignalData;
import com.sensoro.smartcity.callback.BleObserver;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.WidgetUtil;

import java.util.ArrayList;
import java.util.Calendar;

public class SignalCheckActivityPresenter extends BasePresenter<ISignalCheckActivityView>
        implements Constants, Runnable, BLEDeviceListener<BLEDevice>, IOnStart {
    private Activity mActivity;
    private Handler mHandler;
    private boolean bleHasOpen;
    private String bleAddress;
    private boolean isStartSignalCheck = false;
    private SensoroDeviceConnection mConnection;
    private int sendCount;
    private int receiveCount;
    //随机频点
    private int selectedFreq = 0;
    private boolean isConnected = false;
    private DeployAnalyzerModel deployAnalyzerModel;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        deployAnalyzerModel = (DeployAnalyzerModel) mActivity.getIntent().getSerializableExtra(EXTRA_DEPLOY_ANALYZER_MODEL);
        mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(this);
        init();
        BleObserver.getInstance().registerBleObserver(this);

    }

    private void init() {
        getView().setSnText(deployAnalyzerModel.sn);
        String statusText;
        int textColor;
        switch (deployAnalyzerModel.status) {
            case SENSOR_STATUS_ALARM:
                textColor = R.color.c_f34a4a;
                statusText = mActivity.getString(R.string.main_page_warn);
                break;
            case SENSOR_STATUS_NORMAL:
                textColor = R.color.c_1dbb99;
                statusText = mActivity.getString(R.string.normal);
                break;
            case SENSOR_STATUS_LOST:
                textColor = R.color.c_5d5d5d;
                statusText = mActivity.getString(R.string.status_lost);
                break;
            case SENSOR_STATUS_INACTIVE:
                textColor = R.color.c_b6b6b6;
                statusText = mActivity.getString(R.string.status_inactive);
                break;
            case SENSOR_STATUS_MALFUNCTION:
                textColor = R.color.c_fdc83b;
                statusText = mActivity.getString(R.string.status_malfunction);
                break;
            default:
                textColor = R.color.c_1dbb99;
                statusText = mActivity.getString(R.string.normal);
                break;
        }
        getView().setStatus(statusText, textColor);
        getView().setUpdateTime(DateUtil.getStrTime_hms(deployAnalyzerModel.updatedTime));
        String temp;
        if (TextUtils.isEmpty(deployAnalyzerModel.nameAndAddress)) {
            temp = deployAnalyzerModel.sn;
        } else {
            temp = deployAnalyzerModel.nameAndAddress;
        }
        String text = WidgetUtil.getDeviceMainTypeName(deployAnalyzerModel.deviceType) + " " + temp;
        getView().setTypeAndName(text);
        getView().updateTag(deployAnalyzerModel.tagList);
    }

    @Override
    public void onDestroy() {
        BleObserver.getInstance().unregisterBleObserver(this);
        mHandler.removeCallbacksAndMessages(null);
        if (mConnection != null) {
            mConnection.disconnect();
        }
        stopScanService();
    }

    @Override
    public void run() {
        try {
            bleHasOpen = SensoroCityApplication.getInstance().bleDeviceManager.startService();
        } catch (Exception e) {
            e.printStackTrace();
            //TODO 提示
            getView().toastShort(mActivity.getString(R.string.check_ble_status));
        }
        if (!bleHasOpen) {
            bleHasOpen = SensoroCityApplication.getInstance().bleDeviceManager.enEnableBle();
            if (!bleHasOpen) {
                getView().toastShort(mActivity.getString(R.string.check_ble_status));
            }
        }
        mHandler.postDelayed(this, 3000);
    }

    @Override
    public void onNewDevice(BLEDevice bleDevice) {
        try {
            LogUtils.loge(this, bleDevice.getMacAddress() + " " + bleDevice.getSn().equals(deployAnalyzerModel.sn));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        if (bleDevice.getSn().equals(deployAnalyzerModel.sn)) {
            bleAddress = bleDevice.getMacAddress();
            getView().setNearVisible(true);
        }
    }

    @Override
    public void onGoneDevice(BLEDevice bleDevice) {
        if (bleDevice.getSn().equals(deployAnalyzerModel.sn) && !isConnected) {
            bleAddress = null;
            getView().setNearVisible(false);
        }
    }

    @Override
    public void onUpdateDevices(ArrayList<BLEDevice> deviceList) {

    }

    public void doStartOrStop() {
        if (getView().getIsStartSignalCheck()) {
            if (TextUtils.isEmpty(bleAddress)) {
                getView().toastShort(mActivity.getString(R.string.device_not_near));
            } else {
                if (!TextUtils.isEmpty(deployAnalyzerModel.blePassword)) {
                    connectDevice();
                } else {
                    getView().dismissProgressDialog();
                    getView().toastShort(mActivity.getString(R.string.device_not_signal_check));
                    getView().setStartBtnIcon(R.drawable.signal_check_start_btn);
                    isStartSignalCheck = !isStartSignalCheck;
                }

//                getView().setStartBtnIcon(R.drawable.signal_check_stop_btn);
//                getView().showProgressDialog();
//                RetrofitServiceHelper.INSTANCE.getDeployDeviceDetail(deployAnalyzerModel.getSn(), mDeviceInfo.getLonlat()[0], mDeviceInfo.getLonlat()[1])
//                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeployDeviceDetailRsp>() {
//                    @Override
//                    public void onCompleted(DeployDeviceDetailRsp deployDeviceDetailRsp) {
//                        blePassword = deployDeviceDetailRsp.getData().getBlePassword();
//                        //todo delete
////                        blePassword = "hzmBl4;XTD6*[@}I";
//
//
//                    }
//
//                    @Override
//                    public void onErrorMsg(int errorCode, String errorMsg) {
//                        getView().dismissProgressDialog();
////                        getView().updateUploadState(true);
////                        getView().toastShort("获取配置文件失败，请重试 "+errorMsg);
//                        getView().toastShort(mActivity.getString(R.string.device_not_signal_check));
//                        getView().setStartBtnIcon(R.drawable.signal_check_start_btn);
//                    }
//                });
            }


        } else {
            getView().setStartBtnIcon(R.drawable.signal_check_start_btn);
            if (mConnection != null) {
                mConnection.disconnect();
            }

        }

    }

    private void connectDevice() {
        final Runnable connectOvertime = new Runnable() {
            @Override
            public void run() {
                if (isAttachedView()) {
                    if (mConnection != null) {
                        mConnection.disconnect();
                    }
                    if (isAttachedView()) {
                        getView().dismissProgressDialog();
                        getView().setStartBtnIcon(R.drawable.signal_check_start_btn);
                        getView().toastShort(mActivity.getString(R.string.ble_connect_failed));
                        getView().setSubTitleVisible(true);
                    }
                }
            }
        };
        mConnection = new SensoroDeviceConnection(mActivity, bleAddress, true);
        try {
            final SensoroConnectionCallback sensoroConnectionCallback = new SensoroConnectionCallback() {
                @Override
                public void onConnectedSuccess(final BLEDevice bleDevice, int cmd) {
                    isConnected = true;
                    if (isAttachedView()) {
                        mHandler.removeCallbacks(connectOvertime);
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (isAttachedView()) {
                            getView().updateProgressDialogMessage(mActivity.getString(R.string.ble_send_data));
                            getView().showProgressDialog();
                            getView().setLlTestVisible(false);
                            getView().setLlDetailVisible(true);
                            getView().setSubTitleVisible(false);
                            sendDetectionCmd((SensoroDevice) bleDevice);
                        }
                    }
                    sendCount = 0;
                    receiveCount = 0;
                }

                @Override
                public void onConnectedFailure(final int errorCode) {
                    isConnected = false;
                    if (isAttachedView()) {
                        mHandler.removeCallbacks(connectOvertime);
                        getView().dismissProgressDialog();
                        getView().setStartBtnIcon(R.drawable.signal_check_start_btn);
                        getView().toastShort(ResultCode.errCodeToMsg(errorCode));
                        getView().setSubTitleVisible(true);
                    }
                }

                @Override
                public void onDisconnected() {
                    if (isAttachedView()) {
                        mHandler.removeCallbacks(connectOvertime);
                        getView().setSubTitleVisible(true);
//                        getView().toastShort(mActivity.getString(R.string.ble_device_disconnected));
                    }
                }
            };
            mConnection.connect(deployAnalyzerModel.blePassword, sensoroConnectionCallback);
            getView().updateProgressDialogMessage(mActivity.getString(R.string.connecting));
            getView().showProgressDialog();
            mHandler.postDelayed(connectOvertime, 10 * 1000);
//            stopScanService();
        } catch (Exception e) {
            e.printStackTrace();
            getView().dismissProgressDialog();
            getView().toastShort(mActivity.getString(R.string.ble_connect_failed));
        }

    }

    private void sendDetectionCmd(SensoroDevice sensoroDevice) {
        //暂时不写入dr等信息
        final SensoroWriteCallback writeCallback = new SensoroWriteCallback() {
            @Override
            public void onWriteSuccess(final Object o, final int cmd) {
                if (isAttachedView()) {
                    getView().dismissProgressDialog();
                    if (cmd == CmdType.CMD_SIGNAL) {
                        if (o == null) {
                            getView().setStartBtnIcon(R.drawable.signal_check_stop_btn);
                        } else {
                            ProtoMsgTest1U1.MsgTest msgTest = (ProtoMsgTest1U1.MsgTest) o;
                            refresh(msgTest);
                        }
                    }
                }

            }

            @Override
            public void onWriteFailure(final int errorCode, int cmd) {
                if (isAttachedView()) {
                    getView().dismissProgressDialog();
                    getView().setStartBtnIcon(R.drawable.signal_check_start_btn);
                    mConnection.disconnect();
                    getView().toastShort(ResultCode.errCodeToMsg(errorCode));
                }

            }
        };
        mConnection.writeSignalData(selectedFreq, 0, 0, 0, writeCallback);

    }

    private void stopScanService() {
        SensoroCityApplication.getInstance().bleDeviceManager.stopService();
    }

    private void refresh(ProtoMsgTest1U1.MsgTest msgTest) {
        SignalData signalData = new SignalData();
        signalData.setUplinkDR(msgTest.getUplinkDR());
        signalData.setUplinkFreq(msgTest.getUplinkFreq());
        signalData.setUplinkInterval(msgTest.getUplinkInterval());
        signalData.setUplinkRSSI(msgTest.getUplinkRSSI());
        signalData.setUplinkSNR(msgTest.getUplinkSNR());
        signalData.setUplinkTxPower(msgTest.getUplinkTxPower());
        signalData.setDownlinkDR(msgTest.getDownlinkDR());
        signalData.setDownlinkFreq(msgTest.getDownlinkFreq());
        signalData.setDownlinkRSSI(msgTest.getDownlinkRSSI());
        signalData.setDownlinkSNR(msgTest.getDownlinkSNR());
        signalData.setDownlinkTxPower(msgTest.getDownlinkTxPower());
        Calendar calendar = Calendar.getInstance();
        signalData.setDate(DateUtil.getStrTime_ymd_hm_ss(calendar.getTimeInMillis()));

        if (msgTest.getDownlinkSNR() != 0 && msgTest.getDownlinkFreq() != 0 && msgTest.getDownlinkRSSI() != 0 && msgTest.getDownlinkTxPower() != 0) {
            receiveCount++;
        }
        sendCount++;
        float rate = (float) (receiveCount) / sendCount * 100;
        String rateString = String.format("%.1f", rate);
        //

        String text = mActivity.getString(R.string.send) + "：" + sendCount + "  " + mActivity.getString(R.string.receive) + "：" + receiveCount + "  " + mActivity.getString(R.string.success_rate) + "：" + rateString + "%";
        getView().updateSignalStatusText(text);
        getView().updateContentAdapter(signalData);
    }

    public String[] getLoraBandText(Context context) {
        Resources resources = context.getResources();
        //TODO 去掉随机频点
        String band = "";
        if (band.equals(Constants.LORA_BAND_EU433)) {
            return resources.getStringArray(R.array.signal_eu433_band_array);
        } else if (band.equals(Constants.LORA_BAND_EU868)) {
            return resources.getStringArray(R.array.signal_eu868_band_array);
        } else if (band.equals(Constants.LORA_BAND_US915)) {
            return resources.getStringArray(R.array.signal_us915_band_array);
        } else if (band.equals(Constants.LORA_BAND_SE470)) {
            return resources.getStringArray(R.array.signal_se470_band_array);
        } else if (band.equals(Constants.LORA_BAND_SE780)) {
            return resources.getStringArray(R.array.signal_se780_band_array);
        } else if (band.equals(Constants.LORA_BAND_SE433)) {
            return resources.getStringArray(R.array.signal_se433_band_array);
        } else if (band.equals(Constants.LORA_BAND_SE915)) {
            return resources.getStringArray(R.array.signal_se915_band_array);
        } else if (band.equals(Constants.LORA_BAND_AU915)) {
            return resources.getStringArray(R.array.signal_au915_band_array);
        } else if (band.equals(Constants.LORA_BAND_AS923)) {
            return resources.getStringArray(R.array.signal_as923_band_array);
        } else {
            return resources.getStringArray(R.array.signal_se433_band_array);
        }
    }

    @Override
    public void onStart() {
        SensoroCityApplication.getInstance().bleDeviceManager.startScan();
    }

    @Override
    public void onStop() {
        SensoroCityApplication.getInstance().bleDeviceManager.stopScan();
    }
}

package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.sensoro.libbleserver.ble.BLEDevice;
import com.sensoro.libbleserver.ble.CmdType;
import com.sensoro.libbleserver.ble.ResultCode;
import com.sensoro.libbleserver.ble.SensoroConnectionCallback;
import com.sensoro.libbleserver.ble.SensoroDevice;
import com.sensoro.libbleserver.ble.SensoroDeviceConnectionTest;
import com.sensoro.libbleserver.ble.SensoroWriteCallback;
import com.sensoro.libbleserver.ble.proto.ProtoMsgTest1U1;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.ISignalCheckActivityView;
import com.sensoro.smartcity.model.SignalData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.response.DeployDeviceDetailRsp;
import com.sensoro.smartcity.util.BleObserver;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.WidgetUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SignalCheckActivityPresenter extends BasePresenter<ISignalCheckActivityView>
        implements Constants, Runnable, BLEDeviceListener<BLEDevice>, SensoroConnectionCallback, SensoroWriteCallback {
    private Activity mActivity;
    private DeviceInfo mDeviceInfo;
    private Handler mHandler;
    private boolean bleHasOpen;
    private String bleAddress;
    private boolean isStartSignalCheck = false;
    private SensoroDeviceConnectionTest mConnection;
    private String blePassword;
    private int clickCount = 0;
    private boolean isAutoConnect = false;
    private int sendCount;
    private int receiveCount;
    private int selectedFreq = 0;
    private boolean isConnected = false;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        mDeviceInfo = (DeviceInfo) mActivity.getIntent().getSerializableExtra(EXTRA_DEVICE_INFO);
        mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(this);
        init();
        BleObserver.getInstance().registerBleObserver(this);

    }

    private void init() {
        getView().setSnText(mDeviceInfo.getSn());
        String statusText;
        int textColor;
        switch (mDeviceInfo.getStatus()) {
            case SENSOR_STATUS_ALARM:
                textColor = R.color.c_f34a4a;
                statusText = mActivity.getString(R.string.status_alarm);
                break;
            case SENSOR_STATUS_NORMAL:
                textColor = R.color.c_29c093;
                statusText = mActivity.getString(R.string.status_normal);
                break;
            case SENSOR_STATUS_LOST:
                textColor = R.color.c_5d5d5d;
                statusText = mActivity.getString(R.string.status_lost);
                break;
            case SENSOR_STATUS_INACTIVE:
                textColor = R.color.c_b6b6b6;
                statusText = mActivity.getString(R.string.status_inactive);
                break;
            default:
                textColor = R.color.c_29c093;
                statusText = mActivity.getString(R.string.status_normal);
                break;
        }
        getView().setStatus(statusText, textColor);
        getView().setUpdateTime(DateUtil.getStrTime_hms(mDeviceInfo.getUpdatedTime()));
        String temp;
        if (TextUtils.isEmpty(mDeviceInfo.getName())) {
            temp = mDeviceInfo.getSn();
        } else {
            temp = mDeviceInfo.getName();
        }
        String text = WidgetUtil.getDeviceTypeName(mDeviceInfo.getDeviceType()) + " " + temp;
        getView().setTypeAndName(text);
        getView().updateTag(Arrays.asList(mDeviceInfo.getTags()));
    }

    @Override
    public void onDestroy() {
        BleObserver.getInstance().unregisterBleObserver(this);
        stopScanService();
        if (mConnection != null) {
            mConnection.disconnect();
        }
    }

    @Override
    public void run() {
        try {
            bleHasOpen = SensoroCityApplication.getInstance().bleDeviceManager.startService();
        } catch (Exception e) {
            e.printStackTrace();
            //TODO 提示
            getView().toastShort("请检查蓝牙状态");
        }
        if (!bleHasOpen) {
            bleHasOpen = SensoroCityApplication.getInstance().bleDeviceManager.enEnableBle();
            if (!bleHasOpen) {
                getView().toastShort("请检查蓝牙状态");
            }
        }
        mHandler.postDelayed(this, 3000);
    }

    @Override
    public void onNewDevice(BLEDevice bleDevice) {
        LogUtils.loge(this, bleDevice.getMacAddress() + " " + bleDevice.getSn().equals(mDeviceInfo.getSn()));
        if (bleDevice.getSn().equals(mDeviceInfo.getSn())) {
            bleAddress = bleDevice.getMacAddress();
            getView().setNearVisible(true);
        }
    }

    @Override
    public void onGoneDevice(BLEDevice bleDevice) {
        if (bleDevice.getSn().equals(mDeviceInfo.getSn()) && !isConnected) {
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
                getView().setStartBtnIcon(R.drawable.signal_check_stop_btn);
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getDeployDeviceDetail(mDeviceInfo.getSn(), mDeviceInfo.getLonlat()[0], mDeviceInfo.getLonlat()[1])
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeployDeviceDetailRsp>() {
                    @Override
                    public void onCompleted(DeployDeviceDetailRsp deployDeviceDetailRsp) {
                        blePassword = deployDeviceDetailRsp.getData().getBlePassword();
                        //todo delete
//                        blePassword = "hzmBl4;XTD6*[@}I";
                        if (!TextUtils.isEmpty(blePassword)) {
                            connectDevice();

                        } else {
                            getView().dismissProgressDialog();
                            getView().toastShort(mActivity.getString(R.string.device_not_signal_check));
                            getView().setStartBtnIcon(R.drawable.signal_check_start_btn);
                            isStartSignalCheck = !isStartSignalCheck;
                        }

                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
//                        getView().updateUploadState(true);
//                        getView().toastShort("获取配置文件失败，请重试 "+errorMsg);
                        getView().toastShort(mActivity.getString(R.string.device_not_signal_check));
                        getView().setStartBtnIcon(R.drawable.signal_check_start_btn);
                    }
                });
            }


        } else {
            getView().setStartBtnIcon(R.drawable.signal_check_start_btn);
            if (mConnection != null) {
                mConnection.disconnect();
            }

        }

    }

    private void connectDevice() {
        mHandler.removeCallbacksAndMessages(null);
        mConnection = new SensoroDeviceConnectionTest(mActivity, bleAddress, true);
        try {
            mConnection.connect(blePassword, SignalCheckActivityPresenter.this);
            getView().updateProgressDialogMessage(mActivity.getString(R.string.connecting));
//            stopScanService();
        } catch (Exception e) {
            e.printStackTrace();
            getView().dismissProgressDialog();
            getView().toastShort(mActivity.getString(R.string.ble_connect_failed));
        }

    }

    private void sendDetectionCmd(SensoroDevice sensoroDevice) {
        mConnection.writeSignalData(selectedFreq, sensoroDevice.getLoraDr(), sensoroDevice.getLoraTxp(),
                5, this);

    }

    private void stopScanService() {
        SensoroCityApplication.getInstance().bleDeviceManager.stopService();
    }

    @Override
    public void onConnectedSuccess(final BLEDevice bleDevice, int cmd) {
        isConnected = true;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getView().updateProgressDialogMessage(mActivity.getString(R.string.ble_send_data));
                getView().setLlTestVisible(false);
                getView().setLlDetailVisible(true);
                sendCount = 0;
                receiveCount = 0;
                getView().setSubTitleVisible(false);
                sendDetectionCmd((SensoroDevice) bleDevice);
            }
        });


    }

    @Override
    public void onConnectedFailure(final int errorCode) {
        isConnected = false;
        clickCount = 0;
        isAutoConnect = false;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getView().dismissProgressDialog();
                getView().setStartBtnIcon(R.drawable.signal_check_start_btn);
                getView().toastShort(ResultCode.errCodeToMsg(errorCode));
                getView().setSubTitleVisible(true);
            }
        });
    }

    @Override
    public void onDisconnected() {
        getView().setSubTitleVisible(true);
        getView().toastShort(mActivity.getString(R.string.ble_device_disconnected));
    }

    @Override
    public void onWriteSuccess(final Object o, final int cmd) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
        });
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

        if (msgTest.getDownlinkSNR() != 0 && msgTest.getDownlinkFreq() != 0 && msgTest.getDownlinkRSSI() != 0 &&
                msgTest.getDownlinkSNR() != 0 && msgTest.getDownlinkTxPower() != 0) {
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

    @Override
    public void onWriteFailure(final int errorCode, int cmd) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getView().dismissProgressDialog();
                getView().setStartBtnIcon(R.drawable.signal_check_start_btn);
                mConnection.disconnect();

                getView().toastShort(ResultCode.errCodeToMsg(errorCode));
            }
        });
    }

    public String[] getLoraBandText(Context context) {
        Resources resources = context.getResources();
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

}

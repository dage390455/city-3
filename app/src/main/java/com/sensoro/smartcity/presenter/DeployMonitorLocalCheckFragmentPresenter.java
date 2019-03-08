package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.sensoro.libbleserver.ble.callback.SensoroConnectionCallback;
import com.sensoro.libbleserver.ble.callback.SensoroWriteCallback;
import com.sensoro.libbleserver.ble.connection.SensoroDeviceConnection;
import com.sensoro.libbleserver.ble.entity.BLEDevice;
import com.sensoro.libbleserver.ble.entity.SensoroDevice;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.DeployMapActivity;
import com.sensoro.smartcity.activity.DeployMapENActivity;
import com.sensoro.smartcity.activity.DeployMonitorCheckActivity;
import com.sensoro.smartcity.adapter.model.EarlyWarningthresholdDialogUtilsAdapterModel;
import com.sensoro.smartcity.analyzer.DeployConfigurationAnalyzer;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.constant.DeployCheckStateEnum;
import com.sensoro.smartcity.imainviews.IDeployMonitorLocalCheckFragmentView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.iwidget.IOnStart;
import com.sensoro.smartcity.model.DeployAnalyzerModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.bean.DeployControlSettingData;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.BleObserver;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.MyTimer;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.util.WidgetUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;

import static com.sensoro.smartcity.presenter.DeployMonitorCheckActivityPresenter.deployAnalyzerModel;

public class DeployMonitorLocalCheckFragmentPresenter extends BasePresenter<IDeployMonitorLocalCheckFragmentView> implements IOnCreate, Constants, Runnable, BLEDeviceListener<BLEDevice>, IOnStart {
    private Activity mActivity;
    private final ArrayList<String> pickerStrings = new ArrayList<>();
    private ArrayList<EarlyWarningthresholdDialogUtilsAdapterModel> overCurrentDataList;
    private SensoroDeviceConnection sensoroDeviceConnection;
    private Handler mHandler;
    private final HashMap<String, BLEDevice> BLE_DEVICE_SET = new HashMap<>();
    private final Runnable signalTask = new Runnable() {
        @Override
        public void run() {
            freshSignalInfo();
            //信号刷新
            mHandler.postDelayed(signalTask, 2000);
        }
    };

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        mHandler = new Handler(Looper.getMainLooper());
        onCreate();
        init();
        initPickerData();
        initOverCurrentData();
        //基站或白名单不开启蓝牙
        if (deployAnalyzerModel.deployType != TYPE_SCAN_DEPLOY_STATION || deployAnalyzerModel.deployType != TYPE_SCAN_DEPLOY_WHITE_LIST) {
            mHandler.post(this);
        }
        BleObserver.getInstance().registerBleObserver(this);
        mHandler.post(signalTask);
        MyTimer myTimer = new MyTimer(1000, 10 * 1000, new MyTimer.OnMyTimer() {
            @Override
            public void onNext() {
                try {
                    LogUtils.loge("myTimer-->>onNext");
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                try {
                    LogUtils.loge("myTimer-->>onFinish");
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            @Override
            public void onCancel() {

            }
        });
        myTimer.start();
    }

    private void initPickerData() {
        pickerStrings.addAll(Constants.materialValueMap.keySet());
        getView().updatePvCustomOptions(pickerStrings);
    }

    private void initOverCurrentData() {
        overCurrentDataList = new ArrayList<>();
        EarlyWarningthresholdDialogUtilsAdapterModel model = new EarlyWarningthresholdDialogUtilsAdapterModel();
        model.content = mActivity.getString(R.string.over_current_description_one);
        overCurrentDataList.add(model);
        EarlyWarningthresholdDialogUtilsAdapterModel model1 = new EarlyWarningthresholdDialogUtilsAdapterModel();
        model1.content = mActivity.getString(R.string.over_current_description_two);
        overCurrentDataList.add(model1);

    }

    private void init() {
        getView().setNotOwnVisible(deployAnalyzerModel.notOwn);
        getView().setDeviceSn(deployAnalyzerModel.sn);
        //TODO 这是是否要回显位置信息
        getView().updateBtnStatus(canDoOneNextTest());
        getView().setDeployPosition(checkHasLatLng());
        //
        String deviceTypeName = WidgetUtil.getDeviceMainTypeName(deployAnalyzerModel.deviceType);
        //控制界面显示逻辑
        switch (deployAnalyzerModel.deployType) {
            //白名单设备
            case TYPE_SCAN_DEPLOY_WHITE_LIST:
                getView().setDeployDeviceConfigVisible(false);
                getView().setDeployDeviceType(deviceTypeName);
                break;
            //基站
            case TYPE_SCAN_DEPLOY_STATION:
                getView().setDeployDeviceType("基站");
                getView().setDeployDeviceConfigVisible(false);
                break;
            case TYPE_SCAN_DEPLOY_DEVICE:
            case TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
            case TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                //不论更换还是部署都需要安装检测
                getView().setDeployDeviceType(deviceTypeName);
                boolean isFire = DEVICE_CONTROL_DEVICE_TYPES.contains(deployAnalyzerModel.deviceType);
                if (isFire) {
                    //需要安装检测的
                } else {
                    //不需要安装检测
                }
                getView().setDeployDeviceConfigVisible(isFire);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        BleObserver.getInstance().unregisterBleObserver(this);
        mHandler.removeCallbacksAndMessages(null);
        SensoroCityApplication.getInstance().bleDeviceManager.stopService();
        BLE_DEVICE_SET.clear();
        pickerStrings.clear();
    }

    public void showOverCurrentDialog() {
        if (isAttachedView()) {
            getView().showOverCurrentDialog(overCurrentDataList);
        }
    }

    public void doCustomOptionPickerItemSelect(int position) {
        String tx = pickerStrings.get(position);
        if (!TextUtils.isEmpty(tx)) {
            getView().setWireDiameterText(tx);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            case EVENT_DATA_DEPLOY_RESULT_FINISH:
            case EVENT_DATA_DEPLOY_CHANGE_RESULT_CONTINUE:
            case EVENT_DATA_DEPLOY_RESULT_CONTINUE:
                getView().finishAc();
                break;
            case EVENT_DATA_DEPLOY_MAP:
                //地图信息
                if (data instanceof DeployAnalyzerModel) {
                    deployAnalyzerModel = (DeployAnalyzerModel) data;
                    getView().setDeployPosition(deployAnalyzerModel.latLng != null && deployAnalyzerModel.latLng.size() == 2);
                }
                getView().updateBtnStatus(canDoOneNextTest());
                break;
            case EVENT_DATA_SOCKET_DATA_INFO:
                //信号刷新推送
                if (data instanceof DeviceInfo) {
                    DeviceInfo deviceInfo = (DeviceInfo) data;
                    String sn = deviceInfo.getSn();
                    try {
                        if (deployAnalyzerModel.sn.equalsIgnoreCase(sn)) {
                            deployAnalyzerModel.updatedTime = deviceInfo.getUpdatedTime();
                            deployAnalyzerModel.signal = deviceInfo.getSignal();
                            deployAnalyzerModel.status = deviceInfo.getStatus();
                            freshSignalInfo();
//                            getView().toastLong("信号-->>time = " + deployAnalyzerModel.updatedTime + ",signal = " + deployAnalyzerModel.signal);
                            try {
                                LogUtils.loge(this, "部署页刷新信号 -->> deployMapModel.updatedTime = " + deployAnalyzerModel.updatedTime + ",deployMapModel.signal = " + deployAnalyzerModel.signal);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    public void doDeployMap() {
        Intent intent = new Intent();
        if (AppUtils.isChineseLanguage()) {
            intent.setClass(mActivity, DeployMapActivity.class);
        } else {
            intent.setClass(mActivity, DeployMapENActivity.class);
        }
        deployAnalyzerModel.mapSourceType = DEPLOY_MAP_SOURCE_TYPE_DEPLOY_MONITOR_DETAIL;
        intent.putExtra(EXTRA_DEPLOY_ANALYZER_MODEL, deployAnalyzerModel);
        getView().startAC(intent);
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void run() {
        boolean bleHasOpen = SensoroCityApplication.getInstance().bleDeviceManager.isBluetoothEnabled();
        if (bleHasOpen) {
            try {
                bleHasOpen = SensoroCityApplication.getInstance().bleDeviceManager.startService();
            } catch (Exception e) {
                e.printStackTrace();
                getView().showBleTips();
            }
            if (bleHasOpen) {
                getView().hideBleTips();
            } else {
                getView().showBleTips();
            }
        } else {
            getView().showBleTips();
        }
        mHandler.postDelayed(this, 2000);
    }

    @Override
    public void onNewDevice(BLEDevice bleDevice) {
        BLE_DEVICE_SET.put(bleDevice.getSn(), bleDevice);
    }

    @Override
    public void onGoneDevice(BLEDevice bleDevice) {
        BLE_DEVICE_SET.remove(bleDevice.getSn());
    }

    @Override
    public void onUpdateDevices(ArrayList<BLEDevice> deviceList) {
        for (BLEDevice device : deviceList) {
            if (device != null) {
                BLE_DEVICE_SET.put(device.getSn(), device);
            }
        }
    }

    private void connectDevice(final OnConfigInfoObserver onConfigInfoObserver) {
        if (sensoroDeviceConnection != null) {
            sensoroDeviceConnection.disconnect();
        }
        try {
            sensoroDeviceConnection = new SensoroDeviceConnection(mActivity, BLE_DEVICE_SET.get(deployAnalyzerModel.sn).getMacAddress());
            //蓝牙连接回调
            final SensoroConnectionCallback sensoroConnectionCallback = new SensoroConnectionCallback() {
                @Override
                public void onConnectedSuccess(final BLEDevice bleDevice, int cmd) {
                    if (isAttachedView()) {
                        //连接成功后写命令超时
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sensoroDeviceConnection.disconnect();
                                onConfigInfoObserver.onOverTime();
                            }
                        }, 7 * 1000);
                        if (PreferencesHelper.getInstance().getUserData().hasSignalConfig) {
                            //如果需要写频点信息 写入频点信息回调
                            final SensoroWriteCallback SignalWriteCallback = new SensoroWriteCallback() {
                                @Override
                                public void onWriteSuccess(Object o, int cmd) {
                                    if (isAttachedView()) {
                                        //需要写频点信息
                                        if (Constants.DEVICE_CONTROL_DEVICE_TYPES.contains(deployAnalyzerModel.deviceType)) {
                                            if (deployAnalyzerModel.settingData != null) {
                                                SensoroDevice sensoroDevice = DeployConfigurationAnalyzer.configurationData(deployAnalyzerModel.deviceType, (SensoroDevice) bleDevice, deployAnalyzerModel.settingData.getSwitchSpec());
                                                if (sensoroDevice != null) {
                                                    //频点信息写入状态回调
                                                    final SensoroWriteCallback signalAndConfigWriteCallback = new SensoroWriteCallback() {
                                                        @Override
                                                        public void onWriteSuccess(Object o, int cmd) {
                                                            if (isAttachedView()) {
                                                                try {
                                                                    LogUtils.loge("onConnectedSuccess--->>hasSignalConfig writeData05Configuration suc");
                                                                } catch (Throwable throwable) {
                                                                    throwable.printStackTrace();
                                                                }
                                                                sensoroDeviceConnection.disconnect();
                                                                onConfigInfoObserver.onSuccess();
                                                            }
                                                        }

                                                        @Override
                                                        public void onWriteFailure(int errorCode, int cmd) {
                                                            if (isAttachedView()) {
                                                                try {
                                                                    LogUtils.loge("onConnectedSuccess--->>hasSignalConfig writeData05Configuration suc");
                                                                } catch (Throwable throwable) {
                                                                    throwable.printStackTrace();
                                                                }
                                                                sensoroDeviceConnection.disconnect();
                                                                onConfigInfoObserver.onFailed();
                                                            }
                                                        }
                                                    };
                                                    sensoroDeviceConnection.writeData05Configuration(sensoroDevice, signalAndConfigWriteCallback);
                                                } else {
                                                    sensoroDeviceConnection.disconnect();
                                                    onConfigInfoObserver.onFailed();
                                                }

                                            } else {
                                                sensoroDeviceConnection.disconnect();
                                                onConfigInfoObserver.onFailed();
                                            }
                                        } else {
                                            //不需要写入信息直接成功
                                            sensoroDeviceConnection.disconnect();
                                            onConfigInfoObserver.onSuccess();
                                        }
                                    }

                                }

                                @Override
                                public void onWriteFailure(int errorCode, int cmd) {
                                    if (isAttachedView()) {
                                        try {
                                            LogUtils.loge("onConnectedSuccess--->>hasSignalConfig writeData05ChannelMask fal");
                                        } catch (Throwable throwable) {
                                            throwable.printStackTrace();
                                        }
                                        sensoroDeviceConnection.disconnect();
                                        onConfigInfoObserver.onFailed();
                                    }

                                }

                            };
                            sensoroDeviceConnection.writeData05ChannelMask(deployAnalyzerModel.channelMask, SignalWriteCallback);
                        } else {
                            if (Constants.DEVICE_CONTROL_DEVICE_TYPES.contains(deployAnalyzerModel.deviceType)) {
                                //需要写入配置信息
                                try {
                                    LogUtils.loge("onConnectedSuccess--->>contains(deployAnalyzerModel.deviceType)");
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                                if (deployAnalyzerModel.settingData != null) {
                                    SensoroDevice sensoroDevice = DeployConfigurationAnalyzer.configurationData(deployAnalyzerModel.deviceType, (SensoroDevice) bleDevice, deployAnalyzerModel.settingData.getSwitchSpec());
                                    if (sensoroDevice != null) {
                                        //配置信息写入回调
                                        SensoroWriteCallback configWriteCallback = new SensoroWriteCallback() {
                                            @Override
                                            public void onWriteSuccess(Object o, int cmd) {
                                                if (isAttachedView()) {
                                                    try {
                                                        LogUtils.loge("onConnectedSuccess--->>contains(deployAnalyzerModel.deviceType)  writeData05Configuration suc");
                                                    } catch (Throwable throwable) {
                                                        throwable.printStackTrace();
                                                    }
                                                    sensoroDeviceConnection.disconnect();
                                                    onConfigInfoObserver.onSuccess();
                                                }
                                            }

                                            @Override
                                            public void onWriteFailure(int errorCode, int cmd) {
                                                if (isAttachedView()) {
                                                    try {
                                                        LogUtils.loge("onConnectedSuccess--->>contains(deployAnalyzerModel.deviceType)  writeData05Configuration fail");
                                                    } catch (Throwable throwable) {
                                                        throwable.printStackTrace();
                                                    }
                                                    sensoroDeviceConnection.disconnect();
                                                    onConfigInfoObserver.onFailed();
                                                }

                                            }
                                        };
                                        sensoroDeviceConnection.writeData05Configuration(sensoroDevice, configWriteCallback);
                                    } else {
                                        sensoroDeviceConnection.disconnect();
                                        onConfigInfoObserver.onFailed();
                                    }
                                } else {
                                    getView().toastShort(mActivity.getString(R.string.please_set_initial_configuration));
                                    sensoroDeviceConnection.disconnect();
                                    onConfigInfoObserver.onFailed();
                                }
                            } else {
                                //不需要直接成功
                                sensoroDeviceConnection.disconnect();
                                onConfigInfoObserver.onSuccess();
                            }

                        }
                    }
                }

                @Override
                public void onConnectedFailure(int errorCode) {
                    if (isAttachedView()) {
                        onConfigInfoObserver.onFailed();
                    }
                }

                @Override
                public void onDisconnected() {

                }
            };
            sensoroDeviceConnection.connect(deployAnalyzerModel.blePassword, sensoroConnectionCallback);
        } catch (Exception e) {
            e.printStackTrace();
            onConfigInfoObserver.onFailed();
        }
    }

    public interface OnConfigInfoObserver {
        void onSuccess();

        void onFailed();

        void onOverTime();
    }


    /**
     * 检测是否有经纬度
     *
     * @return
     */
    private boolean checkHasLatLng() {
        return deployAnalyzerModel.latLng.size() == 2;
    }

    /**
     * 检查是否进行过初始配置
     *
     * @return
     */
    private boolean checkHasConfig() {
        DeployControlSettingData settingData = deployAnalyzerModel.settingData;
        return settingData != null && settingData.getSwitchSpec() != null && settingData.getWireDiameter() != null && settingData.getWireMaterial() != null;
    }


    private void freshSignalInfo() {
        //刷新信号状态
        String signal_text = null;
        long time_diff = System.currentTimeMillis() - deployAnalyzerModel.updatedTime;
        int resId = 0;
        if (deployAnalyzerModel.signal != null && (time_diff < 2 * 60 * 1000)) {
            switch (deployAnalyzerModel.signal) {
                case "good":
                    signal_text = mActivity.getString(R.string.signal_excellent);
                    resId = R.drawable.shape_signal_good;
                    break;
                case "normal":
                    signal_text = mActivity.getString(R.string.signal_good);
                    resId = R.drawable.shape_signal_normal;
                    break;
                case "bad":
                    signal_text = mActivity.getString(R.string.signal_weak);
                    resId = R.drawable.shape_signal_bad;
                    break;
            }
        } else {
            signal_text = mActivity.getString(R.string.no_signal);
            resId = R.drawable.shape_signal_none;
        }

    }

    /**
     * 检查信号状态
     *
     * @return
     */
    private int checkSignalState() {
        long time_diff = System.currentTimeMillis() - deployAnalyzerModel.updatedTime;
        if (deployAnalyzerModel.signal != null && (time_diff < 2 * 60 * 1000)) {
            switch (deployAnalyzerModel.signal) {
                case "good":
                    return 1;
                case "normal":
                    return 2;
                case "bad":
                    return 3;
            }
        }
        return -1;
    }

    @Override
    public void onStart() {
        SensoroCityApplication.getInstance().bleDeviceManager.startScan();
    }

    @Override
    public void onStop() {
        SensoroCityApplication.getInstance().bleDeviceManager.stopScan();
    }

    /**
     * 重新测试
     */
    public void doCheckDeployTest() {
        doCheckDeployNext();
    }

    /**
     * 跳转下一步
     */
    public void goToNextStep() {
        if (mActivity instanceof DeployMonitorCheckActivity) {
            ((DeployMonitorCheckActivity) mActivity).setDeployMonitorStep(2);
        }
        getView().dismissDeployMonitorCheckDialogUtils();
    }

    /**
     * 点击按钮逻辑
     */
    public void doCheckDeployNext() {
        //是否有强制部署权限
        switch (deployAnalyzerModel.deployType) {
            //白名单设备
            case TYPE_SCAN_DEPLOY_WHITE_LIST:
                //TODO 开始检查操作并更新UI
                getView().showDeployMonitorCheckDialogUtils(1, false);
                checkDeviceIsNearBy(1);
                break;
            //基站
            case TYPE_SCAN_DEPLOY_STATION:
                //TODO 开始检查操作并更新UI
                getView().showDeployMonitorCheckDialogUtils(1, false);
                checkDeviceIsNearBy(1);
                break;
            case TYPE_SCAN_DEPLOY_DEVICE:
            case TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
            case TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                //不论更换还是部署都需要安装检测
                String deviceTypeName = WidgetUtil.getDeviceMainTypeName(deployAnalyzerModel.deviceType);
                getView().setDeployDeviceType(deviceTypeName);
                boolean isFire = DEVICE_CONTROL_DEVICE_TYPES.contains(deployAnalyzerModel.deviceType);
                if (isFire) {
                    //做初始配置检查
                    //TODO 开始检查操作并更新UI
                    getView().showDeployMonitorCheckDialogUtils(3, false);
                    checkDeviceIsNearBy(3);
                } else {
                    if (PreferencesHelper.getInstance().getUserData().hasSignalConfig) {
                        //不做初始配置检查
                        //TODO 开始检查操作并更新UI
                        getView().showDeployMonitorCheckDialogUtils(3, false);
                        checkDeviceIsNearBy(3);
                    } else {
                        //不做初始配置检查
                        //TODO 开始检查操作并更新UI
                        getView().showDeployMonitorCheckDialogUtils(2, false);
                        checkDeviceIsNearBy(2);
                    }

                }
                break;
            default:
                break;
        }

    }

    private final Runnable bleNearbyTaskOverTime = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(bleNearbyTaskOverTime);
            //TODO 蓝牙附近检查超时
        }
    };
    private final Runnable bleNearbyCheckTask = new Runnable() {
        @Override
        public void run() {
            if (BLE_DEVICE_SET.containsKey(deployAnalyzerModel.sn)) {
                mHandler.removeCallbacks(bleNearbyCheckTask);
                //TODO 蓝牙设备在附近
            } else {
                mHandler.postDelayed(bleNearbyCheckTask, 1000);
            }
        }
    };
    private MyTimer myTimer;

    private void checkDeviceIsNearBy(int state) {
        getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_NEARBY_START, "", false);
        switch (state) {
            //一项
            case 1:
                MyTimer.OnMyTimer onSingleMyTimer = new MyTimer.OnMyTimer() {
                    @Override
                    public void onNext() {
                    }

                    @Override
                    public void onFinish() {
                        getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_NEARBY_SUC, "", false);
                        getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_ALL_SUC, "", false);
                    }

                    @Override
                    public void onCancel() {

                    }
                };
                myTimer = new MyTimer(1000, 1000, onSingleMyTimer);
                myTimer.start();
                break;
            //三项
            case 2:
                MyTimer.OnMyTimer onThreeMyTimer = new MyTimer.OnMyTimer() {
                    @Override
                    public void onNext() {
                        if (BLE_DEVICE_SET.containsKey(deployAnalyzerModel.sn)) {
                            myTimer.cancel();
                            getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_NEARBY_SUC, "", false);
                            getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_SIGNAL_START, "", false);
                            MyTimer.OnMyTimer signalMyTimer = new MyTimer.OnMyTimer() {
                                @Override
                                public void onNext() {
                                    int signalState = checkSignalState();
                                    MyTimer.OnMyTimer onStatusMyTimer = new MyTimer.OnMyTimer() {
                                        @Override
                                        public void onNext() {

                                        }

                                        @Override
                                        public void onFinish() {
                                            getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_STATUS_SUC, "", false);
                                            getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_ALL_SUC, "", false);
                                        }

                                        @Override
                                        public void onCancel() {

                                        }
                                    };
                                    switch (signalState) {
                                        case 1:
                                            myTimer.cancel();
                                            getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_SIGNAL_SUC_GOOD, "", false);
                                            getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_STATUS_START, "", false);
                                            myTimer = new MyTimer(1000, 1000, onStatusMyTimer);
                                            myTimer.start();
                                            break;
                                        case 2:
                                            myTimer.cancel();
                                            getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_SIGNAL_SUC_NORMAL, "", false);
                                            getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_STATUS_START, "", false);
                                            myTimer = new MyTimer(1000, 1000, onStatusMyTimer);
                                            myTimer.start();
                                            break;

                                    }
                                }

                                @Override
                                public void onFinish() {
                                    switch (checkSignalState()) {
                                        case -1:
                                            myTimer.cancel();
                                            getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_SIGNAL_FAIL_NONE, "无信号", false);
                                            break;
                                        case 3:
                                            myTimer.cancel();
                                            getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_SIGNAL_FAIL_BAD, "信号差", false);
                                            break;
                                    }
                                }

                                @Override
                                public void onCancel() {

                                }
                            };
                            myTimer = new MyTimer(1000, 10 * 1000, signalMyTimer);
                            myTimer.start();
                        }


                    }

                    @Override
                    public void onFinish() {
                        getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_NEARBY_FAIL, "chaoshi", false);
                    }

                    @Override
                    public void onCancel() {

                    }
                };
                myTimer = new MyTimer(1000, 10 * 1000, onThreeMyTimer);
                myTimer.start();
                break;
            //四项
            case 3:
                MyTimer.OnMyTimer onFourMyTime = new MyTimer.OnMyTimer() {
                    @Override
                    public void onNext() {
                        if (BLE_DEVICE_SET.containsKey(deployAnalyzerModel.sn)) {
                            myTimer.cancel();
                            getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_NEARBY_SUC, "", false);

                        }
                    }

                    @Override
                    public void onFinish() {
                        getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_NEARBY_FAIL, "chaoshi", false);
                    }

                    @Override
                    public void onCancel() {

                    }
                };
                myTimer = new MyTimer(1000, 10 * 1000, onFourMyTime);
                myTimer.start();
                break;
        }
    }

    //频点信息和初始配置信息写入
    private final Runnable bleConfigTaskOverTime = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(bleConfigTask);
            mHandler.removeCallbacks(bleConfigTaskOverTime);
            //TODO 蓝牙附近检查超时
        }
    };
    private final Runnable bleConfigTask = new Runnable() {
        @Override
        public void run() {
            if (BLE_DEVICE_SET.containsKey(deployAnalyzerModel.sn)) {
                mHandler.removeCallbacks(bleConfigTaskOverTime);
                //TODO 蓝牙设备在附近
            } else {
                mHandler.postDelayed(bleNearbyCheckTask, 1000);
            }
        }
    };

    /**
     * 检查配置信息和信号刷新状态
     */
    private void checkDoConfig() {
        mHandler.postDelayed(bleConfigTask, 1000);
        mHandler.postDelayed(bleConfigTaskOverTime, 10 * 1000);
        //由于以1秒间隔不准，所以取其一半来做间隔
        int countTemp = 2;
//此处写的是500的间隔，实际通过countTemp达到1秒间隔的效果
    }


//开始倒计时


    /**
     * 检查按钮是否可以点击
     *
     * @return
     */
    public boolean canDoOneNextTest() {
        switch (deployAnalyzerModel.deployType) {
            //白名单设备
            case TYPE_SCAN_DEPLOY_WHITE_LIST:
                return checkHasLatLng();
            //基站
            case TYPE_SCAN_DEPLOY_STATION:
                return checkHasLatLng();
            case TYPE_SCAN_DEPLOY_DEVICE:
            case TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
            case TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                //不论更换还是部署都需要安装检测
                boolean isFire = DEVICE_CONTROL_DEVICE_TYPES.contains(deployAnalyzerModel.deviceType);
                if (isFire) {
                    //需要安装检测的
                    return checkHasLatLng() && checkHasConfig();
                } else {
                    //不需要安装检测
                    return checkHasLatLng();
                }
        }
        return false;
    }

    /**
     * 跳转配置说明界面
     */
    public void doInstruction() {
        getView().toastShort("span 点击了");
    }
}

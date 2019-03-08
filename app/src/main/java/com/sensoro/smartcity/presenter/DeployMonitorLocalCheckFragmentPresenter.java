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
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.util.WidgetUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;

import static com.sensoro.smartcity.presenter.DeployMonitorCheckActivityPresenter.deployAnalyzerModel;

public class DeployMonitorLocalCheckFragmentPresenter extends BasePresenter<IDeployMonitorLocalCheckFragmentView> implements IOnCreate, Constants, Runnable, BLEDeviceListener<BLEDevice>, SensoroConnectionCallback, IOnStart {
    private Activity mActivity;
    private final ArrayList<String> pickerStrings = new ArrayList<>();
    private ArrayList<EarlyWarningthresholdDialogUtilsAdapterModel> overCurrentDataList;
    private SensoroDeviceConnection sensoroDeviceConnection;
    private Handler mHandler;
    private final HashMap<String, BLEDevice> BLE_DEVICE_SET = new HashMap<>();
    private final Runnable signalTask = new Runnable() {
        @Override
        public void run() {
//            freshSignalInfo();
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
//                            freshSignalInfo();
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
//                getView().showBleTips();
            }
            if (bleHasOpen) {
//                getView().hideBleTips();
            } else {
//                getView().showBleTips();
            }
        } else {
//            getView().showBleTips();
        }
        mHandler.postDelayed(this, 2000);
//        getView().setDeployDeviceDetailFixedPointNearVisible(BLE_DEVICE_SET.containsKey(deployAnalyzerModel.sn));
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

    private void connectDevice() {
//        getView().showBleConfigDialog();
        if (sensoroDeviceConnection != null) {
            sensoroDeviceConnection.disconnect();
        }
        try {
            sensoroDeviceConnection = new SensoroDeviceConnection(mActivity, BLE_DEVICE_SET.get(deployAnalyzerModel.sn).getMacAddress());
            sensoroDeviceConnection.connect(deployAnalyzerModel.blePassword, this);
        } catch (Exception e) {
            e.printStackTrace();
            if (getView() != null) {
//                getView().dismissBleConfigDialog();
//                getView().updateUploadState(true);
                getView().toastShort(mActivity.getString(R.string.ble_connect_failed));
            }

        }
    }

    @Override
    public void onConnectedSuccess(final BLEDevice bleDevice, int cmd) {
        if (isAttachedView()) {
//            getView().updateBleConfigDialogMessage(mContext.getString(R.string.loading_configuration_file));
            if (PreferencesHelper.getInstance().getUserData().hasSignalConfig) {
                try {
                    LogUtils.loge("onConnectedSuccess--->> hasSignalConfig");
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                sensoroDeviceConnection.writeData05ChannelMask(deployAnalyzerModel.channelMask, new SensoroWriteCallback() {
                    @Override
                    public void onWriteSuccess(Object o, int cmd) {
                        if (isAttachedView()) {
                            try {
                                LogUtils.loge("onConnectedSuccess--->> hasSignalConfig writeData05ChannelMask suc");
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                            if (Constants.DEVICE_CONTROL_DEVICE_TYPES.contains(deployAnalyzerModel.deviceType)) {
                                if (deployAnalyzerModel.settingData != null) {
                                    SensoroDevice sensoroDevice = DeployConfigurationAnalyzer.configurationData(deployAnalyzerModel.deviceType, (SensoroDevice) bleDevice, deployAnalyzerModel.settingData.getSwitchSpec());
                                    if (sensoroDevice != null) {
                                        sensoroDeviceConnection.writeData05Configuration(sensoroDevice, new SensoroWriteCallback() {
                                            @Override
                                            public void onWriteSuccess(Object o, int cmd) {
                                                if (isAttachedView()) {
                                                    try {
                                                        LogUtils.loge("onConnectedSuccess--->>hasSignalConfig writeData05Configuration suc");
                                                    } catch (Throwable throwable) {
                                                        throwable.printStackTrace();
                                                    }
//                                                    getView().dismissBleConfigDialog();
//                                                    doUploadImages(deployAnalyzerModel.latLng.get(0), deployAnalyzerModel.latLng.get(1));
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
//                                                    getView().dismissBleConfigDialog();
//                                                    getView().updateUploadState(true);
                                                    getView().toastShort(mActivity.getString(R.string.device_ble_deploy_failed));
                                                    sensoroDeviceConnection.disconnect();
                                                }
                                            }
                                        });
                                    } else {
//                                        getView().dismissBleConfigDialog();
                                        getView().toastShort(mActivity.getString(R.string.deploy_configuration_analyze_data_failed));
                                        sensoroDeviceConnection.disconnect();

                                    }

                                } else {
//                                    getView().dismissBleConfigDialog();
                                    getView().toastShort(mActivity.getString(R.string.please_set_initial_configuration));
                                    sensoroDeviceConnection.disconnect();
                                }
                            } else {
//                                getView().dismissBleConfigDialog();
//                                doUploadImages(deployAnalyzerModel.latLng.get(0), deployAnalyzerModel.latLng.get(1));
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
//                            getView().dismissBleConfigDialog();
//                            getView().updateUploadState(true);
                            getView().toastShort(mActivity.getString(R.string.device_ble_deploy_failed));
                            sensoroDeviceConnection.disconnect();
                        }

                    }

                });
            } else {
                if (Constants.DEVICE_CONTROL_DEVICE_TYPES.contains(deployAnalyzerModel.deviceType)) {
                    try {
                        LogUtils.loge("onConnectedSuccess--->>contains(deployAnalyzerModel.deviceType)");
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    if (deployAnalyzerModel.settingData != null) {
                        SensoroDevice sensoroDevice = DeployConfigurationAnalyzer.configurationData(deployAnalyzerModel.deviceType, (SensoroDevice) bleDevice, deployAnalyzerModel.settingData.getSwitchSpec());
                        if (sensoroDevice != null) {
                            sensoroDeviceConnection.writeData05Configuration(sensoroDevice, new SensoroWriteCallback() {
                                @Override
                                public void onWriteSuccess(Object o, int cmd) {
                                    if (isAttachedView()) {
                                        try {
                                            LogUtils.loge("onConnectedSuccess--->>contains(deployAnalyzerModel.deviceType)  writeData05Configuration suc");
                                        } catch (Throwable throwable) {
                                            throwable.printStackTrace();
                                        }
//                                        getView().dismissBleConfigDialog();
//                                        doUploadImages(deployAnalyzerModel.latLng.get(0), deployAnalyzerModel.latLng.get(1));
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
//                                        getView().dismissBleConfigDialog();
//                                        getView().updateUploadState(true);
                                        getView().toastShort(mActivity.getString(R.string.device_ble_deploy_failed));
                                        sensoroDeviceConnection.disconnect();
                                    }

                                }
                            });
                        } else {
//                            getView().dismissBleConfigDialog();
                            getView().toastShort(mActivity.getString(R.string.deploy_configuration_analyze_data_failed));
                            sensoroDeviceConnection.disconnect();
                        }
                    } else {
//                        getView().dismissBleConfigDialog();
                        getView().toastShort(mActivity.getString(R.string.please_set_initial_configuration));
                        sensoroDeviceConnection.disconnect();
                    }
                } else {
//                    getView().dismissBleConfigDialog();
//                    doUploadImages(deployAnalyzerModel.latLng.get(0), deployAnalyzerModel.latLng.get(1));
                }

            }
        }
    }

    @Override
    public void onConnectedFailure(int errorCode) {
        if (isAttachedView()) {
//            getView().dismissBleConfigDialog();
//            getView().updateUploadState(true);
            getView().toastShort(mActivity.getString(R.string.ble_connect_failed));
        }
    }

    @Override
    public void onDisconnected() {

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
        switch (deployAnalyzerModel.deployType) {
            case TYPE_SCAN_DEPLOY_STATION:
                if (deployAnalyzerModel.latLng.size() != 2) {
//                    getView().refreshSignal(true, signal_text, resId, mActivity.getString(R.string.not_positioned));
                } else {
//                    getView().refreshSignal(true, signal_text, resId, mActivity.getString(R.string.positioned));
                }
                break;
            case TYPE_SCAN_DEPLOY_DEVICE:
            case TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
            case TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                if (deployAnalyzerModel.latLng.size() != 2) {
//                    getView().refreshSignal(false, signal_text, resId, mActivity.getString(R.string.required));
                } else {
//                    getView().refreshSignal(false, signal_text, resId, mActivity.getString(R.string.positioned));
                }
                break;
            case TYPE_SCAN_INSPECTION:
                //扫描巡检设备
                break;
            default:
                break;
        }
    }

    /**
     * 检查信号状态
     *
     * @return
     */
    private boolean checkNeedSignal() {
        long time_diff = System.currentTimeMillis() - deployAnalyzerModel.updatedTime;
        if (deployAnalyzerModel.signal != null && (time_diff < 2 * 60 * 1000)) {
            switch (deployAnalyzerModel.signal) {
                case "good":
                case "normal":
                    return false;
            }
        }
        return true;
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
        DeployMonitorCheckActivityPresenter.deployAnalyzerModel.address = "1234";
        if (mActivity instanceof DeployMonitorCheckActivity) {
            ((DeployMonitorCheckActivity) mActivity).setDeployMonitorStep(2);
        }
        getView().dismissDeployMonitorCheckDialogUtils();
    }

    /**
     * 强制上传直接跳过
     */
    public void doForceUpload() {
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
        boolean hasBadSignalUpload = PreferencesHelper.getInstance().getUserData().hasBadSignalUpload;
        switch (deployAnalyzerModel.deployType) {
            //白名单设备
            case TYPE_SCAN_DEPLOY_WHITE_LIST:
                //TODO 开始检查操作并更新UI
                getView().showDeployMonitorCheckDialogUtils(1, false);
                break;
            //基站
            case TYPE_SCAN_DEPLOY_STATION:
                //TODO 开始检查操作并更新UI
                getView().showDeployMonitorCheckDialogUtils(1, hasBadSignalUpload);
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
                    getView().showDeployMonitorCheckDialogUtils(2, hasBadSignalUpload);
                } else {
                    //不做初始配置检查
                    //TODO 开始检查操作并更新UI
                    getView().showDeployMonitorCheckDialogUtils(3, hasBadSignalUpload);
                }
                break;
            default:
                break;
        }

    }

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

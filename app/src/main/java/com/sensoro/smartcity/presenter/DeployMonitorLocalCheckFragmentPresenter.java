package com.sensoro.smartcity.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.geocoder.RegeocodeRoad;
import com.amap.api.services.geocoder.StreetNumber;
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
import com.sensoro.smartcity.activity.DeployRepairInstructionActivity;
import com.sensoro.smartcity.adapter.model.EarlyWarningthresholdDialogUtilsAdapterModel;
import com.sensoro.smartcity.analyzer.DeployConfigurationAnalyzer;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.constant.DeoloyCheckPointConstants;
import com.sensoro.smartcity.constant.DeployCheckStateEnum;
import com.sensoro.smartcity.imainviews.IDeployMonitorLocalCheckFragmentView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.iwidget.IOnStart;
import com.sensoro.smartcity.model.DeployAnalyzerModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.model.MaterialValueModel;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.RetryWithDelay;
import com.sensoro.smartcity.server.bean.DeployControlSettingData;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.bean.DeviceTypeStyles;
import com.sensoro.smartcity.server.bean.MergeTypeStyles;
import com.sensoro.smartcity.server.response.DeviceStatusRsp;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.BleObserver;
import com.sensoro.smartcity.util.HandlerDeployCheck;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.util.WidgetUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class DeployMonitorLocalCheckFragmentPresenter extends BasePresenter<IDeployMonitorLocalCheckFragmentView> implements IOnCreate, Constants, Runnable, BLEDeviceListener<BLEDevice>, IOnStart {
    private DeployMonitorCheckActivity mActivity;
    private final ArrayList<String> pickerStrings = new ArrayList<>();
    private ArrayList<EarlyWarningthresholdDialogUtilsAdapterModel> overCurrentDataList;
    private SensoroDeviceConnection sensoroDeviceConnection;
    private Handler mHandler;
    private final HashMap<String, BLEDevice> BLE_DEVICE_SET = new HashMap<>();
    private final HandlerDeployCheck checkHandler = new HandlerDeployCheck(Looper.getMainLooper());
    //forceReason: enum ["lonlat", "config", "signalQuality", "status"]
    private String tempForceReason;
    private Integer tempStatus;
    private String tempSignalQuality;
    private DeployAnalyzerModel deployAnalyzerModel;

    @Override
    public void initData(Context context) {
        mActivity = (DeployMonitorCheckActivity) context;
        DeployAnalyzerModel deployAnalyzer = mActivity.getDeployAnalyzerModel();
        if (deployAnalyzer == null) {
            getView().toastLong(mActivity.getString(R.string.unknown));
            return;
        }
        deployAnalyzerModel = deployAnalyzer;
        mHandler = new Handler(Looper.getMainLooper());
        onCreate();
        init();
        initPickerData();
        initOverCurrentData();
        //基站或白名单不开启蓝牙
        if (deployAnalyzerModel.deployType != TYPE_SCAN_DEPLOY_STATION || deployAnalyzerModel.whiteListDeployType != TYPE_SCAN_DEPLOY_WHITE_LIST) {
            mHandler.post(this);
            BleObserver.getInstance().registerBleObserver(this);
        }
        int[] minMaxValue = DeployConfigurationAnalyzer.analyzeDeviceType(deployAnalyzerModel.deviceType);
        if (minMaxValue == null) {
            getView().toastShort(mActivity.getString(R.string.deploy_configuration_analyze_failed));
        } else {
            getView().setSwitchSpecHintText(mActivity.getString(R.string.range) + minMaxValue[0] + "-" + minMaxValue[1]);
        }
        //获取一次临时的位置信息
        GeocodeSearch geocoderSearch = new GeocodeSearch(mActivity);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                try {
                    RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
                    StringBuilder stringBuilder = new StringBuilder();
//        String subLoc = regeocodeAddress.getDistrict();// 区或县或县级市
                    String ts = regeocodeAddress.getTownship();// 乡镇
                    String thf = null;// 道路
                    List<RegeocodeRoad> regeocodeRoads = regeocodeAddress.getRoads();// 道路列表
                    if (regeocodeRoads != null && regeocodeRoads.size() > 0) {
                        RegeocodeRoad regeocodeRoad = regeocodeRoads.get(0);
                        if (regeocodeRoad != null) {
                            thf = regeocodeRoad.getName();
                        }
                    }
                    String subthf = null;// 门牌号
                    StreetNumber streetNumber = regeocodeAddress.getStreetNumber();
                    if (streetNumber != null) {
                        subthf = streetNumber.getNumber();
                    }
//        String fn = regeocodeAddress.getBuilding();// 标志性建筑,当道路为null时显示
//        if (subLoc != null) {
//            stringBuffer.append(subLoc);
//        }
//        if (ts != null) {
//            stringBuffer.append(ts);
//        }
                    if (thf != null) {
                        stringBuilder.append(thf);
                    }
                    if (subthf != null) {
                        stringBuilder.append(subthf);
                    }
                    String address = stringBuilder.toString();
                    if (TextUtils.isEmpty(address)) {
                        address = ts;
                    }
                    if (!TextUtils.isEmpty(address)) {
                        deployAnalyzerModel.address = address;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });
        //默认显示已定位
        deployAnalyzerModel.address = mActivity.getString(R.string.positioned);
        if (deployAnalyzerModel.latLng.size() == 2) {
            LatLonPoint lp = new LatLonPoint(deployAnalyzerModel.latLng.get(1), deployAnalyzerModel.latLng.get(0));
            RegeocodeQuery query = new RegeocodeQuery(lp, 200, GeocodeSearch.AMAP);
            geocoderSearch.getFromLocationAsyn(query);
        }
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
        getView().setDeviceSn(mActivity.getString(R.string.device_number) + deployAnalyzerModel.sn);
        //TODO 这是是否要回显位置信息
        getView().updateBtnStatus(canDoOneNextTest());
        getView().setDeployPosition(checkHasLatLng());
        //
        String deviceTypeName = WidgetUtil.getDeviceMainTypeName(deployAnalyzerModel.deviceType);
        //控制界面显示逻辑
        switch (deployAnalyzerModel.deployType) {
            //基站
            case TYPE_SCAN_DEPLOY_STATION:
                getView().setDeployDeviceType(mActivity.getString(R.string.station));
                getView().setDeployDeviceConfigVisible(false);
                break;
            case TYPE_SCAN_DEPLOY_DEVICE:
            case TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
            case TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                //不论更换还是部署都需要安装检测
                getView().setDeployDeviceType(deviceTypeName);
                switch (deployAnalyzerModel.whiteListDeployType) {
                    //白名单设备
                    case TYPE_SCAN_DEPLOY_WHITE_LIST:
                    case TYPE_SCAN_DEPLOY_WHITE_LIST_HAS_SIGNAL_CONFIG:
                        getView().setDeployDeviceConfigVisible(false);
                        break;
                    default:
                        boolean isFire = DEVICE_CONTROL_DEVICE_TYPES.contains(deployAnalyzerModel.deviceType);
                        getView().setDeployDeviceConfigVisible(isFire);
                        break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        BleObserver.getInstance().unregisterBleObserver(this);
        checkHandler.removeAllMessage();
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
                    getView().setDeployPosition(deployAnalyzerModel.latLng.size() == 2);
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
        final Runnable configOvertime = new Runnable() {
            @Override
            public void run() {
                if (isAttachedView()) {
                    if (sensoroDeviceConnection != null) {
                        sensoroDeviceConnection.disconnect();
                    }
                    if (onConfigInfoObserver != null) {
                        onConfigInfoObserver.onOverTime(mActivity.getString(R.string.init_config_over_time));
                    }
                }
            }
        };
        try {
            sensoroDeviceConnection = new SensoroDeviceConnection(mActivity, BLE_DEVICE_SET.get(deployAnalyzerModel.sn).getMacAddress());
            //蓝牙连接回调
            final SensoroConnectionCallback sensoroConnectionCallback = new SensoroConnectionCallback() {
                @Override
                public void onConnectedSuccess(final BLEDevice bleDevice, int cmd) {
                    if (isAttachedView()) {
                        //连接成功后写命令超时
                        if (PreferencesHelper.getInstance().getUserData().hasSignalConfig) {
                            //如果需要写频点信息 写入频点信息回调
                            final SensoroWriteCallback SignalWriteCallback = new SensoroWriteCallback() {
                                @Override
                                public void onWriteSuccess(Object o, int cmd) {
                                    if (isAttachedView()) {
                                        //需要写频点信息
                                        if (Constants.DEVICE_CONTROL_DEVICE_TYPES.contains(deployAnalyzerModel.deviceType)) {
                                            if (deployAnalyzerModel.settingData != null) {
                                                SensoroDevice sensoroDevice = DeployConfigurationAnalyzer.configurationData(deployAnalyzerModel.deviceType, (SensoroDevice) bleDevice, deployAnalyzerModel.settingData.getSwitchSpec(), deployAnalyzerModel.settingData.getInputValue());
                                                if (sensoroDevice != null) {
                                                    //频点信息写入状态回调
                                                    final SensoroWriteCallback configWriteCallback = new SensoroWriteCallback() {
                                                        @Override
                                                        public void onWriteSuccess(Object o, int cmd) {
                                                            if (isAttachedView()) {
                                                                sensoroDeviceConnection.disconnect();
                                                                mHandler.removeCallbacks(configOvertime);
                                                                onConfigInfoObserver.onSuccess();
                                                            }
                                                        }

                                                        @Override
                                                        public void onWriteFailure(int errorCode, int cmd) {
                                                            if (isAttachedView()) {
                                                                sensoroDeviceConnection.disconnect();
                                                                mHandler.removeCallbacks(configOvertime);
                                                                onConfigInfoObserver.onFailed(mActivity.getString(R.string.ble_init_config_write_failure));
                                                            }
                                                        }
                                                    };
                                                    sensoroDeviceConnection.writeData05Configuration(sensoroDevice, configWriteCallback);
                                                } else {
                                                    sensoroDeviceConnection.disconnect();
                                                    mHandler.removeCallbacks(configOvertime);
                                                    onConfigInfoObserver.onFailed(mActivity.getString(R.string.init_config_not_support_device));
                                                }

                                            } else {
                                                sensoroDeviceConnection.disconnect();
                                                mHandler.removeCallbacks(configOvertime);
                                                onConfigInfoObserver.onFailed(mActivity.getString(R.string.init_config_info_error));
                                            }
                                        } else {
                                            //不需要写入信息直接成功
                                            sensoroDeviceConnection.disconnect();
                                            mHandler.removeCallbacks(configOvertime);
                                            onConfigInfoObserver.onSuccess();
                                        }
                                    }

                                }

                                @Override
                                public void onWriteFailure(int errorCode, int cmd) {
                                    if (isAttachedView()) {
                                        sensoroDeviceConnection.disconnect();
                                        mHandler.removeCallbacks(configOvertime);
                                        onConfigInfoObserver.onFailed(mActivity.getString(R.string.frequency_config_write_failure));
                                    }

                                }

                            };
                            sensoroDeviceConnection.writeData05ChannelMask(deployAnalyzerModel.channelMask, SignalWriteCallback);
                        } else {
                            if (Constants.DEVICE_CONTROL_DEVICE_TYPES.contains(deployAnalyzerModel.deviceType)) {
                                //需要写入配置信息
                                if (deployAnalyzerModel.settingData != null) {
                                    SensoroDevice sensoroDevice = DeployConfigurationAnalyzer.configurationData(deployAnalyzerModel.deviceType, (SensoroDevice) bleDevice, deployAnalyzerModel.settingData.getSwitchSpec(), deployAnalyzerModel.settingData.getInputValue());
                                    if (sensoroDevice != null) {
                                        //配置信息写入回调
                                        SensoroWriteCallback configWriteCallback = new SensoroWriteCallback() {
                                            @Override
                                            public void onWriteSuccess(Object o, int cmd) {
                                                if (isAttachedView()) {
                                                    sensoroDeviceConnection.disconnect();
                                                    mHandler.removeCallbacks(configOvertime);
                                                    onConfigInfoObserver.onSuccess();
                                                }
                                            }

                                            @Override
                                            public void onWriteFailure(int errorCode, int cmd) {
                                                if (isAttachedView()) {
                                                    sensoroDeviceConnection.disconnect();
                                                    mHandler.removeCallbacks(configOvertime);
                                                    onConfigInfoObserver.onFailed(mActivity.getString(R.string.ble_init_config_write_failure));
                                                }

                                            }
                                        };
                                        sensoroDeviceConnection.writeData05Configuration(sensoroDevice, configWriteCallback);
                                    } else {
                                        sensoroDeviceConnection.disconnect();
                                        mHandler.removeCallbacks(configOvertime);
                                        onConfigInfoObserver.onFailed(mActivity.getString(R.string.init_config_not_support_device));
                                    }
                                } else {
                                    getView().toastShort(mActivity.getString(R.string.please_set_initial_configuration));
                                    sensoroDeviceConnection.disconnect();
                                    mHandler.removeCallbacks(configOvertime);
                                    onConfigInfoObserver.onFailed(mActivity.getString(R.string.init_config_info_error));
                                }
                            } else {
                                //不需要直接成功
                                sensoroDeviceConnection.disconnect();
                                mHandler.removeCallbacks(configOvertime);
                                onConfigInfoObserver.onSuccess();
                            }

                        }
                    }
                }

                @Override
                public void onConnectedFailure(int errorCode) {
                    if (isAttachedView()) {
                        mHandler.removeCallbacks(configOvertime);
                        onConfigInfoObserver.onFailed(mActivity.getString(R.string.deploy_check_ble_connect_error));
                    }
                }

                @Override
                public void onDisconnected() {

                }
            };
            sensoroDeviceConnection.connect(deployAnalyzerModel.blePassword, sensoroConnectionCallback);
            mHandler.postDelayed(configOvertime, 15 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.removeCallbacks(configOvertime);
            onConfigInfoObserver.onFailed(mActivity.getString(R.string.unknown_error));
        }
    }

    public void updateCheckTipText() {
        if (TYPE_SCAN_DEPLOY_STATION == deployAnalyzerModel.deployType) {
            getView().setDeployLocalCheckTipText("");
        } else {
            if (DEVICE_CONTROL_DEVICE_TYPES.contains(deployAnalyzerModel.deviceType) || "mantun_fires".equals(deployAnalyzerModel.deviceType)) {
                getView().setDeployLocalCheckTipText(mActivity.getString(R.string.deploy_check_button_tip_is_powered_on));
            } else {
                DeviceTypeStyles configDeviceType = PreferencesHelper.getInstance().getConfigDeviceType(deployAnalyzerModel.deviceType);
                if (configDeviceType != null) {
                    String mergeType = configDeviceType.getMergeType();
                    if ("smoke".equals(mergeType)) {
                        getView().setDeployLocalCheckTipText(mActivity.getString(R.string.deploy_check_button_tip_press_the_sensor));
                        return;
                    }
                }
                getView().setDeployLocalCheckTipText("");
            }
        }

    }

    public void cancelCheckTest() {
        checkHandler.removeAllMessage();

    }

    private void updateConfigSettingData(Integer inputValue, int material, double diameter, int min) {
        deployAnalyzerModel.settingData = new DeployControlSettingData();
        deployAnalyzerModel.settingData.setSwitchSpec(min);
        deployAnalyzerModel.settingData.setWireDiameter(diameter);
        deployAnalyzerModel.settingData.setWireMaterial(material);
        deployAnalyzerModel.settingData.setInputValue(inputValue);
    }

    public void doForceUpload() {
        deployAnalyzerModel.forceReason = tempForceReason;
        deployAnalyzerModel.currentSignalQuality = tempSignalQuality;
        deployAnalyzerModel.currentStatus = tempStatus;
        goToNextStep();
    }

    public String getRepairInstructionUrl() {
        String mergeType = WidgetUtil.handleMergeType(deployAnalyzerModel.deviceType);
        if (TextUtils.isEmpty(mergeType)) {
            return null;
        }
        MergeTypeStyles configMergeType = PreferencesHelper.getInstance().getConfigMergeType(mergeType);
        if (configMergeType == null) {
            return null;
        }
        return configMergeType.getFixSpecificationUrl();
    }

    public void handleCurrentValue(String diameterStr, String materialStr, String enterValueStr) {

        if (!TextUtils.isEmpty(diameterStr) && !mActivity.getString(R.string.deploy_check_please_select).equals(diameterStr) && !TextUtils.isEmpty(materialStr) && !TextUtils.isEmpty(enterValueStr)) {
            try {
                Integer inputValue = Integer.valueOf(enterValueStr);
                int min = inputValue;
                int material = 0;
                int mapValue = inputValue;
                double diameter = Double.parseDouble(diameterStr);
                MaterialValueModel materialValueModel = Constants.materialValueMap.get(diameterStr);
                if (materialValueModel != null) {
                    if (mActivity.getString(R.string.cu).equals(materialStr)) {
                        material = 0;
                        mapValue = materialValueModel.cuValue;
                    } else if (mActivity.getString(R.string.al).equals(materialStr)) {
                        material = 1;
                        mapValue = materialValueModel.alValue;
                    }
                    min = Math.min(inputValue, mapValue);

                    getView().setDeployCheckTvConfigurationText(String.format(Locale.CHINESE, "%dA", min));
                }
                updateConfigSettingData(inputValue, material, diameter, min);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                int[] minMaxValue = DeployConfigurationAnalyzer.analyzeDeviceType(deployAnalyzerModel.deviceType);
                getView().toastShort(mActivity.getString(R.string.enter_the_correct_number_format) + "," + mActivity.getString(R.string.range) + minMaxValue[0] + "-" + minMaxValue[1]);
            }
        } else {
            getView().setDeployCheckTvConfigurationText("-");
        }
        getView().updateBtnStatus(canDoOneNextTest());
    }

    public interface OnConfigInfoObserver {
        void onSuccess();

        void onFailed(String errorMsg);

        void onOverTime(String overTimeMsg);
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
        if (settingData != null) {
            Integer switchSpec = settingData.getSwitchSpec();
            Integer inputValue = settingData.getInputValue();
            return switchSpec != null && settingData.getWireDiameter() != null && settingData.getWireMaterial() != null && inputValue != null;
        }
        return false;
    }

    /**
     * 检查初始配置是否符合逻辑
     *
     * @return
     */
    private boolean checkConfig() {
        DeployControlSettingData settingData = deployAnalyzerModel.settingData;
        if (settingData != null) {
            Integer switchSpec = settingData.getSwitchSpec();
            Integer inputValue = settingData.getInputValue();
            int[] minMaxValue = DeployConfigurationAnalyzer.analyzeDeviceType(deployAnalyzerModel.deviceType);
            if (minMaxValue != null) {
                if (inputValue != null) {
                    if (inputValue >= minMaxValue[0] && inputValue <= minMaxValue[1]) {
                        if (switchSpec != null) {
                            if (switchSpec >= minMaxValue[0]) {
                                return true;
                            } else {
                                getView().toastShort(mActivity.getString(R.string.actual_overcurrent_threshold) + mActivity.getString(R.string.out_of_range) + "," + mActivity.getString(R.string.range) + minMaxValue[0] + "-" + minMaxValue[1]);
                            }
                        }
                    } else {
                        getView().toastShort(mActivity.getString(R.string.empty_open_rated_current_is_out_of_range) + "," + mActivity.getString(R.string.range) + minMaxValue[0] + "-" + minMaxValue[1]);
                    }
                }
            }
        }
        return false;
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
                    return DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_SIGNAL_GOOD;
                case "normal":
                    return DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_SIGNAL_NORMAL;
                case "bad":
                    return DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_SIGNAL_BAD;
            }
        }
        return DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_SIGNAL_NONE;
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
     * 跳转下一步
     */
    public void goToNextStep() {
        if (mActivity instanceof DeployMonitorCheckActivity) {
            ((DeployMonitorCheckActivity) mActivity).setDeployAnalyzerModel(deployAnalyzerModel);
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
            //基站
            case TYPE_SCAN_DEPLOY_STATION:
                // 基站
                //改为直接跳过
                goToNextStep();

//                getView().showDeployMonitorCheckDialogUtils(DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_ORIGIN_STATE_SINGLE, false);
//                checkDeviceIsNearBy(DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_ORIGIN_STATE_SINGLE);
                break;
            case TYPE_SCAN_DEPLOY_DEVICE:
            case TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
            case TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                switch (deployAnalyzerModel.whiteListDeployType) {
                    //白名单设备
                    case TYPE_SCAN_DEPLOY_WHITE_LIST:
                        // 开始检查操作并更新UI
//                        getView().showDeployMonitorCheckDialogUtils(DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_ORIGIN_STATE_SINGLE, false);
//                        checkDeviceIsNearBy(DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_ORIGIN_STATE_SINGLE);
                        //改为直接跳过
                        goToNextStep();
                        break;
                    case TYPE_SCAN_DEPLOY_WHITE_LIST_HAS_SIGNAL_CONFIG:
                        // 开始检查操作并更新UI
                        getView().showDeployMonitorCheckDialogUtils(DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_ORIGIN_STATE_TWO, false);
                        checkDeviceIsNearBy(DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_ORIGIN_STATE_TWO);
                        break;
                    default:
                        //不论更换还是部署都需要安装检测
                        String deviceTypeName = WidgetUtil.getDeviceMainTypeName(deployAnalyzerModel.deviceType);
                        getView().setDeployDeviceType(deviceTypeName);
                        boolean isFire = DEVICE_CONTROL_DEVICE_TYPES.contains(deployAnalyzerModel.deviceType);
                        if (isFire) {
                            //做初始配置检查
                            //开始检查操作并更新UI
                            if (checkConfig()) {
                                getView().showDeployMonitorCheckDialogUtils(DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_ORIGIN_STATE_FOUR, false);
                                checkDeviceIsNearBy(DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_ORIGIN_STATE_FOUR);
                            }
                        } else {
                            if (PreferencesHelper.getInstance().getUserData().hasSignalConfig) {
                                //不做初始配置检查
                                getView().showDeployMonitorCheckDialogUtils(DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_ORIGIN_STATE_FOUR, false);
                                checkDeviceIsNearBy(DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_ORIGIN_STATE_FOUR);
                            } else {
                                //不做初始配置检查
                                getView().showDeployMonitorCheckDialogUtils(DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_ORIGIN_STATE_THREE, false);
                                checkDeviceIsNearBy(DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_ORIGIN_STATE_THREE);
                            }

                        }
                        break;
                }

                break;
            default:
                break;
        }

    }

    private void checkDeviceIsNearBy(int state) {
        getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_NEARBY_START, "", false);
        switch (state) {
            //一项
            case DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_ORIGIN_STATE_SINGLE:
                //一项的时候，检查是否在附近
                checkNearByOne();
                break;
            case DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_ORIGIN_STATE_TWO:
                //二项的时候，检查是否在附近，频点配置
                checkNearByTwo();
                break;
            //三项
            case DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_ORIGIN_STATE_THREE:
                //三项的时候，检查是否在附近
                checkNearbyThree();
                break;
            //四项
            case DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_ORIGIN_STATE_FOUR:
                //四项的时候，检查是否在附近
                checkNearbyFour();
                break;
        }
    }

    private void checkNearbyFour() {
        HandlerDeployCheck.OnMessageDeal threeMsgDeal = new HandlerDeployCheck.OnMessageDeal() {
            @Override
            public void onNext() {
                if (BLE_DEVICE_SET.containsKey(deployAnalyzerModel.sn)) {
                    checkHandler.removeAllMessage();
                    getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_NEARBY_SUC, "", false);
                    getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_CONFIG_START, "", false);

                    connectDevice(new OnConfigInfoObserver() {
                        @Override
                        public void onSuccess() {
                            if (isAttachedView()) {
                                HandlerDeployCheck.OnMessageDeal signalMsgDeal = new HandlerDeployCheck.OnMessageDeal() {
                                    @Override
                                    public void onNext() {
                                        int signalState = checkSignalState();
                                        switch (signalState) {
                                            case DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_SIGNAL_GOOD:
                                                getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_SIGNAL_SUC_GOOD, "", false);
                                                getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_STATUS_START, "", false);
                                                checkHandler.removeAllMessage();
                                                getDeviceRealStatus();
                                                break;
                                            case DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_SIGNAL_NORMAL:
                                                getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_SIGNAL_SUC_NORMAL, "", false);
                                                getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_STATUS_START, "", false);
                                                checkHandler.removeAllMessage();
                                                getDeviceRealStatus();
                                                break;
                                        }

                                    }

                                    @Override
                                    public void onFinish() {
                                        int state = checkSignalState();
                                        switch (state) {
                                            case DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_SIGNAL_NONE:
                                                tempForceReason = "signalQuality";
                                                tempSignalQuality = "none";
                                                getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_SIGNAL_FAIL_NONE, mActivity.getString(R.string.deploy_check_dialog_no_signal), PreferencesHelper.getInstance().getUserData().hasBadSignalUpload);
                                                return;
                                            case DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_SIGNAL_BAD:
                                                tempForceReason = "signalQuality";
                                                tempSignalQuality = "bad";
                                                getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_SIGNAL_FAIL_BAD, mActivity.getString(R.string.deploy_check_dialog_quality_bad_signal), PreferencesHelper.getInstance().getUserData().hasBadSignalUpload);
                                                return;
                                        }
                                        tempForceReason = "signalQuality";
                                        tempSignalQuality = "none";
                                        getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_SIGNAL_FAIL_NONE, mActivity.getString(R.string.deploy_check_dialog_no_signal), PreferencesHelper.getInstance().getUserData().hasBadSignalUpload);
                                    }
                                };
                                checkHandler.init(1000, 10);
                                checkHandler.dealMessage(3, signalMsgDeal);
                                getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_CONFIG_SUC, "", false);
                                getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_SIGNAL_START, "", false);
                            }
                        }

                        @Override
                        public void onFailed(String errorMsg) {
                            if (isAttachedView()) {
                                tempForceReason = "config";
                                getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_CONFIG_FAIL, mActivity.getString(R.string.installation_config_failed) + errorMsg, PreferencesHelper.getInstance().getUserData().hasBadSignalUpload);
                            }

                        }

                        @Override
                        public void onOverTime(String overTimeMsg) {
                            if (isAttachedView()) {
                                tempForceReason = "config";
                                getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_CONFIG_FAIL, mActivity.getString(R.string.installation_config_failed) + overTimeMsg, PreferencesHelper.getInstance().getUserData().hasBadSignalUpload);
                            }
                        }
                    });


                }
            }

            @Override
            public void onFinish() {
                tempForceReason = "lonlat";
                getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_NEARBY_FAIL, mActivity.getString(R.string.installation_test_not_nearby), PreferencesHelper.getInstance().getUserData().hasBadSignalUpload);
            }
        };
        checkHandler.removeAllMessage();
        checkHandler.init(1000, 10);
        checkHandler.dealMessage(2, threeMsgDeal);
    }

    /**
     * 第四步，检测设备状态
     */
    private void getDeviceRealStatus() {
        final long requestTime = System.currentTimeMillis();
        RetrofitServiceHelper.getInstance().getDeviceRealStatus(deployAnalyzerModel.sn).subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(2, 100))
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceStatusRsp>(this) {
            @Override
            public void onCompleted(final DeviceStatusRsp data) {
                long diff = System.currentTimeMillis() - requestTime;
                if (diff > 1000) {
                    updateDeviceStatusDialog(data);
                } else {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateDeviceStatusDialog(data);
                        }
                    }, diff);
                }
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                tempForceReason = null;
                // 获取不到当前状态是否强制上传
                getView().toastShort(errorMsg);
                getView().dismissDeployMonitorCheckDialogUtils();
//                getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_STATUS_FAIL_INTERNET, mActivity.getString(R.string.get_device_status_failed), PreferencesHelper.getInstance().getUserData().hasBadSignalUpload);
            }
        });
    }

    private void updateDeviceStatusDialog(DeviceStatusRsp data) {
        if (data != null && data.getData() != null && data.getData().getStatus() != null) {
            switch (data.getData().getStatus()) {
                case SENSOR_STATUS_ALARM:
                    tempForceReason = "status";
                    tempStatus = data.getData().getStatus();
                    getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_STATUS_FAIL_ALARM,
                            mActivity.getString(R.string.device_is_alarm), PreferencesHelper.getInstance().getUserData().hasBadSignalUpload);
                    break;
                case SENSOR_STATUS_MALFUNCTION:
                    tempForceReason = "status";
                    tempStatus = data.getData().getStatus();
                    getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_STATUS_FAIL_MALFUNCTION, mActivity.getString(R.string.device_is_malfunction), PreferencesHelper.getInstance().getUserData().hasBadSignalUpload);
                    break;
                default:
                    tempForceReason = null;
                    getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_STATUS_SUC, "", false);
                    getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_ALL_SUC, "", false);
                    break;
            }
        } else {
            //状态错误
            tempForceReason = null;
            String errMsg;
            if (AppUtils.isChineseLanguage()) {
                errMsg = "似乎已断开与互联网的连接。";
            } else {
                errMsg = "It seems to have disconnected from the internet.";
            }
            getView().toastShort(errMsg);
            getView().dismissDeployMonitorCheckDialogUtils();
//            getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_STATUS_FAIL_INTERNET, mActivity.getString(R.string.get_device_status_failed), PreferencesHelper.getInstance().getUserData().hasBadSignalUpload);

        }
    }

    private void checkNearbyThree() {
//        HandlerDeployCheck.OnMessageDeal threeMsgDeal = new HandlerDeployCheck.OnMessageDeal() {
//            @Override
//            public void onNext() {
//                if (BLE_DEVICE_SET.containsKey(deployAnalyzerModel.sn)) {
//
//
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                tempForceReason = "lonlat";
//                getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_NEARBY_FAIL, mActivity.getString(R.string.installation_test_not_nearby), PreferencesHelper.getInstance().getUserData().hasBadSignalUpload);
//            }
//        };
//        checkHandler.removeAllMessage();
//        checkHandler.init(1000, 10);
//        checkHandler.dealMessage(2, threeMsgDeal);
        //
        checkHandler.removeAllMessage();
        getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_NEARBY_SUC, "", false);
        getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_SIGNAL_START, "", false);
        HandlerDeployCheck.OnMessageDeal signalMsgDeal = new HandlerDeployCheck.OnMessageDeal() {
            @Override
            public void onNext() {
                int signalState = checkSignalState();
                switch (signalState) {
                    case DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_SIGNAL_GOOD:
                        getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_SIGNAL_SUC_GOOD, "", false);
                        getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_STATUS_START, "", false);
                        checkHandler.removeAllMessage();
                        getDeviceRealStatus();
                        break;
                    case DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_SIGNAL_NORMAL:
                        getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_SIGNAL_SUC_NORMAL, "", false);
                        getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_STATUS_START, "", false);
                        checkHandler.removeAllMessage();
                        getDeviceRealStatus();
                        break;
                }

            }

            @Override
            public void onFinish() {
                int state = checkSignalState();
                switch (state) {
                    case DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_SIGNAL_NONE:
                        tempForceReason = "signalQuality";
                        tempSignalQuality = "none";
                        getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_SIGNAL_FAIL_NONE, mActivity.getString(R.string.deploy_check_dialog_no_signal), PreferencesHelper.getInstance().getUserData().hasBadSignalUpload);
                        return;
                    case DeoloyCheckPointConstants.DEPLOY_CHECK_DIALOG_SIGNAL_BAD:
                        tempForceReason = "signalQuality";
                        tempSignalQuality = "bad";
                        getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_SIGNAL_FAIL_BAD, mActivity.getString(R.string.deploy_check_dialog_quality_bad_signal), PreferencesHelper.getInstance().getUserData().hasBadSignalUpload);
                        return;
                }
                tempForceReason = "signalQuality";
                tempSignalQuality = "none";
                getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_SIGNAL_FAIL_NONE, mActivity.getString(R.string.deploy_check_dialog_no_signal), PreferencesHelper.getInstance().getUserData().hasBadSignalUpload);

            }
        };
        checkHandler.init(1000, 10);
        checkHandler.dealMessage(3, signalMsgDeal);

    }

    private void checkNearByTwo() {
        HandlerDeployCheck.OnMessageDeal twoMsgDeal = new HandlerDeployCheck.OnMessageDeal() {
            @Override
            public void onNext() {
                if (BLE_DEVICE_SET.containsKey(deployAnalyzerModel.sn)) {
                    checkHandler.removeAllMessage();
                    getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_NEARBY_SUC, "", false);
                    getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_CONFIG_START, "", false);
                    connectDevice(new OnConfigInfoObserver() {
                        @Override
                        public void onSuccess() {
                            if (isAttachedView()) {
                                getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_CONFIG_SUC, "", false);
                                getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_ALL_SUC, "", false);
                            }
                        }

                        @Override
                        public void onFailed(String errorMsg) {
                            if (isAttachedView()) {
                                tempForceReason = "config";
                                getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_CONFIG_FAIL, mActivity.getString(R.string.installation_config_failed) + errorMsg, PreferencesHelper.getInstance().getUserData().hasBadSignalUpload);
                            }

                        }

                        @Override
                        public void onOverTime(String overTimeMsg) {
                            if (isAttachedView()) {
                                tempForceReason = "config";
                                getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_CONFIG_FAIL, mActivity.getString(R.string.installation_config_failed) + overTimeMsg, PreferencesHelper.getInstance().getUserData().hasBadSignalUpload);
                            }
                        }
                    });


                }
            }

            @Override
            public void onFinish() {
                tempForceReason = "lonlat";
                getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_NEARBY_FAIL, mActivity.getString(R.string.installation_test_not_nearby), PreferencesHelper.getInstance().getUserData().hasBadSignalUpload);
            }
        };
        checkHandler.removeAllMessage();
        checkHandler.init(1000, 10);
        checkHandler.dealMessage(2, twoMsgDeal);
    }

    private void checkNearByOne() {
        checkHandler.init(1000, 1);
        checkHandler.dealMessage(1, new HandlerDeployCheck.OnMessageDeal() {

            @Override
            public void onNext() {

            }

            @Override
            public void onFinish() {
                tempForceReason = null;
                getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_NEARBY_SUC, "", false);
                getView().updateDeployMonitorCheckDialogUtils(DeployCheckStateEnum.DEVICE_CHECK_ALL_SUC, "", false);
            }

        });
    }

    /**
     * 检查按钮是否可以点击
     *
     * @return
     */
    private boolean canDoOneNextTest() {
        switch (deployAnalyzerModel.deployType) {
            //基站
            case TYPE_SCAN_DEPLOY_STATION:
                return checkHasLatLng();
            case TYPE_SCAN_DEPLOY_DEVICE:
            case TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
            case TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                switch (deployAnalyzerModel.whiteListDeployType) {
                    //白名单设备
                    case TYPE_SCAN_DEPLOY_WHITE_LIST:
                    case TYPE_SCAN_DEPLOY_WHITE_LIST_HAS_SIGNAL_CONFIG:
                        return checkHasLatLng();
                    default:
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

        }
        return false;
    }

    /**
     * 跳转配置说明界面
     *
     * @param repairInstructionUrl
     */
    public void doInstruction(String repairInstructionUrl) {
        Intent intent = new Intent(mActivity, DeployRepairInstructionActivity.class);
//        "https://resource-city.sensoro.com/fix-specification/smoke.html";
        intent.putExtra(Constants.EXTRA_DEPLOY_CHECK_REPAIR_INSTRUCTION_URL, repairInstructionUrl);
        getView().startAC(intent);
    }
}

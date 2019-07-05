package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.base.ContextUtils;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.constant.SearchHistoryTypeConstants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.DeviceAlarmLogInfo;
import com.sensoro.common.server.bean.DeviceInfo;
import com.sensoro.common.server.response.DeviceAlarmLogRsp;
import com.sensoro.common.server.response.DeviceInfoListRsp;
import com.sensoro.common.server.response.DevicesAlarmPopupConfigRsp;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.libbleserver.ble.entity.BLEDevice;
import com.sensoro.libbleserver.ble.entity.IBeacon;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.MonitorPointElectricDetailActivity;
import com.sensoro.smartcity.analyzer.AlarmPopupConfigAnalyzer;
import com.sensoro.smartcity.callback.BleObserver;
import com.sensoro.smartcity.imainviews.INearByDeviceActivityView;
import com.sensoro.smartcity.model.AlarmPopupModel;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.popup.AlarmLogPopUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NearByDevicePresenter extends BasePresenter<INearByDeviceActivityView> implements BLEDeviceListener<BLEDevice> {
    private Activity mActivity;
    private final LinkedHashMap<String, IBeacon> mNearByIBeaconMap = new LinkedHashMap<>();
    private final List<String> mNearByIDevice = new ArrayList<>();

    private String mUuid = "70DC44C3-E2A8-4B22-A2C6-129B41A4BDBC";
    //    private String mUuid="23A01AF0-232A-4518-9C0E-323FB773F5EF";

    private int major = 51050;
    private boolean deviceoutSwstate = true;
    private boolean deviceinSwstate = true;
    private String deviceoutContent = "进入";
    private String deviceinContent = "离开";
    private volatile boolean isNearby = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private final ArrayList<DeviceInfo> deviceInfos = new ArrayList<>();

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;

        getView().showProgressDialog();
        mActivity = (Activity) context;
        refreshSp();
        initBle();

    }

    private void initBle() {
        BleObserver.getInstance().registerBleObserver(this);
        final Runnable bleCheckTask = new Runnable() {
            @Override
            public void run() {
                boolean bleHasOpen = SensoroCityApplication.getInstance().bleDeviceManager.isBluetoothEnabled();
                if (bleHasOpen) {
                    try {
                        bleHasOpen = SensoroCityApplication.getInstance().bleDeviceManager.startService();
                        //
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    SensoroToast.getInstance().makeText("蓝牙未开启", Toast.LENGTH_SHORT).show();
                }
                mHandler.postDelayed(this, 3000);
            }
        };
        mHandler.post(bleCheckTask);
        getView().showProgressDialog();
        final Runnable checkNearbyTask = new Runnable() {
            @Override
            public void run() {
                boolean currentNearby = !mNearByIBeaconMap.isEmpty();
                if (isNearby != currentNearby) {
                    getView().toastLong("进出状态：" + currentNearby);
                    if (currentNearby) {
                        if (deviceinSwstate) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (TextUtils.isEmpty(deviceinContent)) {
                                        SensoroCityApplication.getInstance().mNotificationUtils.sendNotification("进入！！");
                                    } else {
                                        SensoroCityApplication.getInstance().mNotificationUtils.sendNotification(deviceinContent);
                                    }

                                }
                            });
                        }
                    } else {
                        if (deviceoutSwstate) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (TextUtils.isEmpty(deviceinContent)) {
                                        SensoroCityApplication.getInstance().mNotificationUtils.sendNotification("出去！！！");
                                    } else {
                                        SensoroCityApplication.getInstance().mNotificationUtils.sendNotification(deviceoutContent);
                                    }

                                }
                            });
                        }
                    }
                    isNearby = currentNearby;
                }
                mHandler.postDelayed(this, 300);
            }
        };
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getDeviceBriefInfo();
                mHandler.post(checkNearbyTask);
            }
        }, 5000);
    }

    @Override
    public void onGoneDevice(BLEDevice bleDevice) {
        if (bleDevice != null) {
            String sn = bleDevice.getSn();
            if (!TextUtils.isEmpty(sn)) {
                mNearByIDevice.remove(sn);
                IBeacon iBeacon = bleDevice.iBeacon;
                if (iBeacon != null) {
                    String uuid = iBeacon.getUuid();
                    if (mUuid.equals(uuid)) {
                        mNearByIBeaconMap.remove(sn);
                    }
                }
            }

        }
    }

    @Override
    public void onNewDevice(BLEDevice bleDevice) {
        if (bleDevice != null) {
            String sn = bleDevice.getSn();
            if (!TextUtils.isEmpty(sn)) {
                mNearByIDevice.add(sn);
                IBeacon iBeacon = bleDevice.iBeacon;
                if (iBeacon != null) {
                    String uuid = iBeacon.getUuid();
                    if (mUuid.equals(uuid)) {
                        mNearByIBeaconMap.put(sn, iBeacon);
                    }
                }
            }
        }


    }


    @Override
    public void onUpdateDevices(ArrayList<BLEDevice> deviceList) {
        for (BLEDevice bleDevice : deviceList) {
            String sn = bleDevice.getSn();
            if (!TextUtils.isEmpty(sn)) {
                mNearByIDevice.add(sn);
                IBeacon iBeacon = bleDevice.iBeacon;
                if (iBeacon != null) {
                    String uuid = iBeacon.getUuid();
                    if (mUuid.equals(uuid)) {
                        mNearByIBeaconMap.put(sn, iBeacon);
                    }
                }
            }

        }
    }


    public void getDeviceBriefInfo() {
        getView().showProgressDialog();
        deviceInfos.clear();
        RetrofitServiceHelper.getInstance().getDeviceBriefInfoList(mNearByIDevice, 1, null, null, null, null).subscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>(NearByDevicePresenter.this) {
            @Override
            public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {

                if (deviceInfoListRsp.getData() != null && deviceInfoListRsp.getData().size() > 0) {
                    deviceInfos.addAll(deviceInfoListRsp.getData());
                    getView().dismissProgressDialog();
                    getView().onPullRefreshComplete();

                }
                getView().updateAdapter(deviceInfos);
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
                getView().onPullRefreshComplete();

            }
        });
    }


    public void clickAlarmInfo(int position) {
        DeviceInfo deviceInfo = deviceInfos.get(position);
        requestAlarmInfo(deviceInfo);
    }

    private void requestAlarmInfo(DeviceInfo deviceInfo) {
        //
        getView().showProgressDialog();
        RetrofitServiceHelper.getInstance().getDeviceAlarmLogList(1, deviceInfo.getSn(), null, null, null, null, null, null)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>(this) {

            @Override
            public void onCompleted(DeviceAlarmLogRsp deviceAlarmLogRsp) {
                getView().dismissProgressDialog();
                if (deviceAlarmLogRsp.getData().size() == 0) {
                    getView().toastShort(mActivity.getString(R.string.no_alert_log_information_was_obtained));
                } else {
                    DeviceAlarmLogInfo deviceAlarmLogInfo = deviceAlarmLogRsp.getData().get(0);
                    enterAlarmLogPop(deviceAlarmLogInfo);
                }
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }

    private void enterAlarmLogPop(final DeviceAlarmLogInfo deviceAlarmLogInfo) {
        final AlarmLogPopUtils mAlarmLogPop = new AlarmLogPopUtils(mActivity);
        mAlarmLogPop.refreshData(deviceAlarmLogInfo);
        if (PreferencesHelper.getInstance().getAlarmPopupDataBeanCache() == null) {
            RetrofitServiceHelper.getInstance().getDevicesAlarmPopupConfig().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DevicesAlarmPopupConfigRsp>(this) {
                @Override
                public void onCompleted(DevicesAlarmPopupConfigRsp devicesAlarmPopupConfigRsp) {
                    PreferencesHelper.getInstance().saveAlarmPopupDataBeanCache(devicesAlarmPopupConfigRsp.getData());
                    final AlarmPopupModel alarmPopupModel = new AlarmPopupModel();
                    String deviceName = deviceAlarmLogInfo.getDeviceName();
                    if (TextUtils.isEmpty(deviceName)) {
                        alarmPopupModel.title = deviceAlarmLogInfo.getDeviceSN();
                    } else {
                        alarmPopupModel.title = deviceName;
                    }
                    alarmPopupModel.alarmStatus = deviceAlarmLogInfo.getAlarmStatus();
                    alarmPopupModel.updateTime = deviceAlarmLogInfo.getUpdatedTime();
                    alarmPopupModel.mergeType = WidgetUtil.handleMergeType(deviceAlarmLogInfo.getDeviceType());
                    alarmPopupModel.sensorType = deviceAlarmLogInfo.getSensorType();
                    //
                    AlarmPopupConfigAnalyzer.handleAlarmPopupModel(null, alarmPopupModel);
                    mAlarmLogPop.show(alarmPopupModel);
                    getView().dismissProgressDialog();

                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    getView().toastShort(errorMsg);
                    getView().dismissProgressDialog();
                }
            });
        } else {
            final AlarmPopupModel alarmPopupModel = new AlarmPopupModel();
            String deviceName = deviceAlarmLogInfo.getDeviceName();
            if (TextUtils.isEmpty(deviceName)) {
                alarmPopupModel.title = deviceAlarmLogInfo.getDeviceSN();
            } else {
                alarmPopupModel.title = deviceName;
            }
            alarmPopupModel.alarmStatus = deviceAlarmLogInfo.getAlarmStatus();
            alarmPopupModel.updateTime = deviceAlarmLogInfo.getUpdatedTime();
            alarmPopupModel.mergeType = WidgetUtil.handleMergeType(deviceAlarmLogInfo.getDeviceType());
            alarmPopupModel.sensorType = deviceAlarmLogInfo.getSensorType();
            //
            AlarmPopupConfigAnalyzer.handleAlarmPopupModel(null, alarmPopupModel);
            mAlarmLogPop.show(alarmPopupModel);
        }
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        BleObserver.getInstance().unregisterBleObserver(this);
        SensoroCityApplication.getInstance().bleDeviceManager.stopService();
        mNearByIBeaconMap.clear();

    }


    public void refreshSp() {


        SharedPreferences sp = ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_SETTINGNOTIFICATION_NAME, Context
                .MODE_PRIVATE);
        String oldText = sp.getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");
        if (!TextUtils.isEmpty(oldText)) {
            String[] split = oldText.split(",");
            if (!TextUtils.isEmpty(split[0])) {
                deviceinSwstate = Boolean.parseBoolean(split[0]);
            }
            if (!TextUtils.isEmpty(split[1])) {
                deviceoutSwstate = Boolean.parseBoolean(split[1]);
            }
            if (!TextUtils.isEmpty(split[2])) {
                deviceinContent = split[2];
            }
            if (!TextUtils.isEmpty(split[3])) {
                deviceoutContent = split[3];
            }


        }
    }


    public void itemClickStartActivity(int position) {

        Intent intent = new Intent();
        intent.setClass(mActivity, MonitorPointElectricDetailActivity.class);
        intent.putExtra(Constants.EXTRA_DEVICE_INFO, deviceInfos.get(position));
        getView().startAC(intent);

    }
}

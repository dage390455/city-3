package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.model.IbeaconSettingData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.AlarmPopupDataBean;
import com.sensoro.common.server.bean.DeviceAlarmLogInfo;
import com.sensoro.common.server.bean.DeviceInfo;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.libbleserver.ble.entity.BLEDevice;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.MonitorPointElectricDetailActivity;
import com.sensoro.smartcity.activity.SettingNotificationActivity;
import com.sensoro.smartcity.analyzer.AlarmPopupConfigAnalyzer;
import com.sensoro.smartcity.callback.BleObserver;
import com.sensoro.smartcity.imainviews.INearByDeviceActivityView;
import com.sensoro.smartcity.model.AlarmPopupModel;
import com.sensoro.common.utils.WidgetUtil;
import com.sensoro.smartcity.widget.popup.AlarmLogPopUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NearByDevicePresenter extends BasePresenter<INearByDeviceActivityView> implements BLEDeviceListener<BLEDevice>, IOnCreate {
    private Activity mActivity;

    private final List<String> mNearByIDevice = new ArrayList<>();

    //    private String mUuid="23A01AF0-232A-4518-9C0E-323FB773F5EF";

    //    private int major = 51050;
//    private boolean deviceoutSwstate = true;
//    private boolean deviceinSwstate = true;
//    private String deviceoutContent = "进入";
//    private String deviceinContent = "离开";

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private final ArrayList<DeviceInfo> deviceInfos = new ArrayList<>();

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        onCreate();
        mActivity = (Activity) context;
        init();

    }

    private void initBle() {
        getView().showProgressDialog();
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
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getDeviceBriefInfo();
//                mHandler.post(checkNearbyTask);
            }
        }, 5000);
    }

    @Override
    public void onGoneDevice(BLEDevice bleDevice) {
        if (bleDevice != null) {
            String sn = bleDevice.getSn();
            if (!TextUtils.isEmpty(sn)) {
                mNearByIDevice.remove(sn);
//                IBeacon iBeacon = bleDevice.iBeacon;
//                if (iBeacon != null) {
//                    String uuid = iBeacon.getUuid();
//                    if (ibeaconSettingData.currentUUID.equals(uuid)) {
//                        mNearByIBeaconMap.remove(sn);
//                    }
//                }
            }

        }
    }

    @Override
    public void onNewDevice(BLEDevice bleDevice) {
        if (bleDevice != null) {
            String sn = bleDevice.getSn();
            if (!TextUtils.isEmpty(sn)) {
                mNearByIDevice.add(sn);
//                IBeacon iBeacon = bleDevice.iBeacon;
//                if (iBeacon != null) {
//                    String uuid = iBeacon.getUuid();
//                    if (ibeaconSettingData.currentUUID.equals(uuid)) {
//                        mNearByIBeaconMap.put(sn, iBeacon);
//                    }
//                }
            }
        }


    }


    @Override
    public void onUpdateDevices(ArrayList<BLEDevice> deviceList) {
        for (BLEDevice bleDevice : deviceList) {
            String sn = bleDevice.getSn();
            if (!TextUtils.isEmpty(sn)) {
                mNearByIDevice.add(sn);
//                IBeacon iBeacon = bleDevice.iBeacon;
//                if (iBeacon != null) {
//                    String uuid = iBeacon.getUuid();
//                    if (ibeaconSettingData.currentUUID.equals(uuid)) {
//                        mNearByIBeaconMap.put(sn, iBeacon);
//                    }
//                }
            }

        }
    }


    public void getDeviceBriefInfo() {
        getView().showProgressDialog();
        deviceInfos.clear();
        RetrofitServiceHelper.getInstance().getDeviceBriefInfoList(mNearByIDevice, 1, null, null, null, null).subscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<DeviceInfo>>>(NearByDevicePresenter.this) {
            @Override
            public void onCompleted(ResponseResult<List<DeviceInfo>> deviceInfoListRsp) {
                deviceInfos.clear();
                List<DeviceInfo> data = deviceInfoListRsp.getData();
                if (data != null && data.size() > 0) {
                    deviceInfos.addAll(data);
                }
                getView().updateAdapter(deviceInfos);
                getView().dismissProgressDialog();
                getView().onPullRefreshComplete();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                deviceInfos.clear();
                getView().updateAdapter(deviceInfos);
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
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<DeviceAlarmLogInfo>>>(this) {

            @Override
            public void onCompleted(ResponseResult<List<DeviceAlarmLogInfo>> deviceAlarmLogRsp) {
                getView().dismissProgressDialog();
                List<DeviceAlarmLogInfo> data = deviceAlarmLogRsp.getData();
                if (data == null || data.size() == 0) {
                    getView().toastShort(mActivity.getString(R.string.no_alert_log_information_was_obtained));
                } else {
                    DeviceAlarmLogInfo deviceAlarmLogInfo = data.get(0);
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
            RetrofitServiceHelper.getInstance().getDevicesAlarmPopupConfig().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<AlarmPopupDataBean>>(this) {
                @Override
                public void onCompleted(ResponseResult<AlarmPopupDataBean> devicesAlarmPopupConfigRsp) {
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
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
        BleObserver.getInstance().unregisterBleObserver(this);
        SensoroCityApplication.getInstance().bleDeviceManager.stopService();

    }


    public void init() {
        initBle();
    }


    public void itemClickStartActivity(int position) {

        Intent intent = new Intent();
        intent.setClass(mActivity, MonitorPointElectricDetailActivity.class);
        intent.putExtra(Constants.EXTRA_DEVICE_INFO, deviceInfos.get(position));
        getView().startAC(intent);

    }

    public void goNoSetting() {
        Intent intent = new Intent(mActivity, SettingNotificationActivity.class);
        intent.putExtra("ibeaconSettingData", SensoroCityApplication.getInstance().ibeaconSettingData);
        getView().startAC(intent);
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IbeaconSettingData ibeaconSettingData) {
        if (ibeaconSettingData != null) {
            SensoroCityApplication.getInstance().ibeaconSettingData = ibeaconSettingData;
        }
    }
}

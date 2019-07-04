package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.base.ContextUtils;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.constant.SearchHistoryTypeConstants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.manger.ThreadPoolManager;
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
import com.sensoro.smartcity.model.OKBLEBeaconRegion;
import com.sensoro.smartcity.util.NotificationUtils;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.popup.AlarmLogPopUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NearByDevicePresenter extends BasePresenter<INearByDeviceActivityView> implements BLEDeviceListener<BLEDevice> {
    private volatile ArrayList<DeviceInfo> deviceInfos = new ArrayList<DeviceInfo>();
    //    private volatile ArrayList<IBeacon> iBeacons = new ArrayList<IBeacon>();
    private Activity mActivity;
    private ConcurrentHashMap<String, BLEDevice> mNearByDeviceMap = new ConcurrentHashMap<>();
    //    private ConcurrentHashMap<String, DeviceInfo> deviceInfoHashMap = new ConcurrentHashMap<>();
    private NotificationUtils notificationUtils;

    private String mUuid = "70DC44C3-E2A8-4B22-A2C6-129B41A4BDBC";
    //    private String mUuid="23A01AF0-232A-4518-9C0E-323FB773F5EF";

    private int major = 51050;
    private boolean deviceoutSwstate = true;
    private boolean deviceinSwstate = true;
    private String deviceoutContent = "进入";
    private String deviceinContent = "离开";
    private boolean onScanCycleFinish = false;

    private OKBLEBeaconRegion okbleBeaconRegion = OKBLEBeaconRegion.getInstance(mUuid);
    private Map<String, RegionObject> monitoringBeaconRegions = new HashMap<String, RegionObject>();
    private final int regionExitOverTime = 10 * 1000;//退出区域的超时时间，持续regionExitOverTime这么长的时间内没有再次扫描到这个区域，则视为退出区域
    private int monitoringBeaconRegionID = 0;//监控的iBeacon区域的id

    private class RegionObject {
        boolean hasEntered;
        OKBLEBeaconRegion region;
        int regionID;

        public RegionObject(OKBLEBeaconRegion region, int regionID) {
            super();
            this.region = region;
            this.regionID = regionID;
        }
    }


    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String key = (String) msg.obj;
            //收到消息，表示已经持续了一段时间没有扫描到区域内的beacon了，视为退出区域
            if (monitoringBeaconRegions.containsKey(key)) {
                RegionObject regionObject = monitoringBeaconRegions.get(key);
                OKBLEBeaconRegion beaconRegion = regionObject.region;

                regionObject.hasEntered = false;
//                if (regionListener != null) {
//                    if (okbleScanManager.isScanning()) {
//                        regionListener.onExitBeaconRegion(beaconRegion);
//                    }
//                }
                notificationUtils.sendNotification(deviceoutContent);

            }
        }
    };

    public void startMonitoringForRegion(OKBLEBeaconRegion region) {
        String key = region.getIdentifier();
        if (!monitoringBeaconRegions.containsKey(key)) {
            monitoringBeaconRegionID++;

            RegionObject regionObject = new RegionObject(region, monitoringBeaconRegionID);

            monitoringBeaconRegions.put(key, regionObject);
        }
    }

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;

        notificationUtils = new NotificationUtils(context);
        getView().showProgressDialog();
        mActivity = (Activity) context;
        refreshSp();
        registerBleObserver();

    }

    public void registerBleObserver() {
        BleObserver.getInstance().registerBleObserver(this);
        ThreadPoolManager.getInstance().execute(new Runnable() {


            @Override
            public void run() {
                boolean bleHasOpen = SensoroCityApplication.getInstance().bleDeviceManager.isBluetoothEnabled();
                if (bleHasOpen) {
                    try {
                        bleHasOpen = SensoroCityApplication.getInstance().bleDeviceManager.startService();
                        startMonitoringForRegion(okbleBeaconRegion);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getView().dismissProgressDialog();

                            SensoroToast.getInstance().makeText("蓝牙未开启", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });
    }

    public void onRefresh() {
        deviceInfos.clear();

        if (null != mNearByDeviceMap && mNearByDeviceMap.size() > 0) {
            Iterator<Map.Entry<String, BLEDevice>> iterator = mNearByDeviceMap.entrySet().iterator();

            while (iterator.hasNext()) {

                Map.Entry<String, BLEDevice> next = iterator.next();
                getDeviceBriefInfo(next.getValue());

            }
        } else {

            getView().onPullRefreshComplete();
            getView().toastLong("附近暂无设备");
            getView().updateAdapter(deviceInfos);

        }
    }

    @Override
    public void onGoneDevice(BLEDevice bleDevice) {

//        if (bleDevice.iBeacon != null) {
//            IBeacon iBeacon = bleDevice.iBeacon;
//            String uuid = iBeacon.getUuid();
//            if (deviceoutSwstate && onScanCycleFinish) {
//                if (uuid.equals(mUuid) /*&& (iBeacon.getMajor() == major)*/) {
//
//                    notificationUtils.sendNotification(bleDevice.getSn() + deviceoutContent);
//                    mNearByDeviceMap.remove(bleDevice.getSn());
//                }
//            }
//        }

        if (bleDevice.iBeacon != null) {
            IBeacon iBeacon = bleDevice.iBeacon;
            String uuid = iBeacon.getUuid();
            if (uuid.equals(mUuid)) {
//                iBeacons.remove(iBeacon);
            }
        }

    }

    private void handleEnterRegion(RegionObject regionObject) {
        handler.removeMessages(regionObject.regionID);//移除超时时间后回调退出区域的消息
        Message msg = new Message();
        msg.what = regionObject.regionID;
        msg.obj = regionObject.region.getIdentifier();
        handler.sendMessageDelayed(msg, regionExitOverTime);//重新发送一个延时消息，

        if (!regionObject.hasEntered) {
            regionObject.hasEntered = true;
            notificationUtils.sendNotification(deviceinContent);

//            if(regionListener!=null){
//                if(okbleScanManager.isScanning()){
//                    regionListener.onEnterBeaconRegion(regionObject.region);
//                }
//            }
        }
    }

    @Override
    public void onNewDevice(BLEDevice bleDevice) {


        if (bleDevice.iBeacon != null) {
            IBeacon iBeacon = bleDevice.iBeacon;
            String uuid = iBeacon.getUuid();

            if (monitoringBeaconRegions.size() > 0) {
                String key = iBeacon.macAddress;

                if (monitoringBeaconRegions.containsKey(key)) {
                    //如果正在监控这个区域
                    RegionObject regionObject = monitoringBeaconRegions.get(key);
                    handleEnterRegion(regionObject);
                }

            }
//            if (uuid.equals(mUuid)) {
//                for (int i = 0; i < iBeacons.size(); i++) {
//                    String btAddress = iBeacons.get(i).macAddress;
//                    if (btAddress.equals(iBeacon.macAddress)) {
//                        iBeacons.set(i, iBeacon);
//                        break;
//                    }
//                }
//                iBeacons.add(iBeacon);
//            }
        }


//        if (!mNearByDeviceMap.containsKey(bleDevice.getSn())) {
//            if (deviceinSwstate && onScanCycleFinish) {
//                if (bleDevice.iBeacon != null) {
//                    IBeacon iBeacon = bleDevice.iBeacon;
//                    String uuid = iBeacon.getUuid();
//                    if (uuid.equals(mUuid)/* && (iBeacon.getMajor() == major)*/) {
//
//
//                        iBeacons.add(iBeacon);
////                        String content = bleDevice.getSn();
////                        notificationUtils.sendNotification(content + deviceinContent);
//                    }
//
//                }
//            }
//
//
//        }

        if (!mNearByDeviceMap.containsKey(bleDevice.getSn())) {
            mNearByDeviceMap.put(bleDevice.getSn(), bleDevice);
        }

        if (!onScanCycleFinish) {
            getDeviceBriefInfo(bleDevice);
        }


    }


    @Override
    public void onUpdateDevices(ArrayList<BLEDevice> deviceList) {
        for (BLEDevice bleDevice : deviceList) {
            String sn = bleDevice.getSn();
            mNearByDeviceMap.put(sn, bleDevice);
        }
        onScanCycleFinish = true;

    }


    public void getDeviceBriefInfo(BLEDevice bleDevice) {


        RetrofitServiceHelper.getInstance().getDeviceBriefInfoList(1, null, null, null, bleDevice.getSn()).subscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>(NearByDevicePresenter.this) {
            @Override
            public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {

                if (deviceInfoListRsp.getData() != null && deviceInfoListRsp.getData().size() > 0) {
                    deviceInfos.addAll(deviceInfoListRsp.getData());
                    getView().dismissProgressDialog();
                    getView().onPullRefreshComplete();
//                            deviceInfoHashMap.put(bleDevice.getSn(), deviceInfoListRsp.getData().get(0));

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
        BleObserver.getInstance().unregisterBleObserver(this);
        SensoroCityApplication.getInstance().bleDeviceManager.stopService();
        mNearByDeviceMap.clear();
        deviceInfos.clear();

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

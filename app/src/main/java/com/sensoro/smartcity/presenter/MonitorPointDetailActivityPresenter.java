package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.AlarmHistoryLogActivity;
import com.sensoro.smartcity.activity.MonitorMoreActivity;
import com.sensoro.smartcity.activity.MonitorPointDetailActivity;
import com.sensoro.smartcity.activity.MonitorPointMapActivity;
import com.sensoro.smartcity.adapter.model.MonitoringPointRcContentAdapterModel;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.constant.MonitorPointOperationCode;
import com.sensoro.smartcity.imainviews.IMonitorPointDetailActivityView;
import com.sensoro.smartcity.iwidget.IOnStart;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.push.ThreadPoolManager;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceAlarmsRecord;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.bean.DeviceMergeTypesInfo;
import com.sensoro.smartcity.server.bean.MalfunctionDataBean;
import com.sensoro.smartcity.server.bean.MergeTypeStyles;
import com.sensoro.smartcity.server.bean.MonitorPointOperationTaskResultInfo;
import com.sensoro.smartcity.server.bean.SensorStruct;
import com.sensoro.smartcity.server.bean.SensorTypeStyles;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.MonitorPointOperationRequestRsp;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.util.WidgetUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MonitorPointDetailActivityPresenter extends BasePresenter<IMonitorPointDetailActivityView> implements IOnStart, Constants, GeocodeSearch.OnGeocodeSearchListener {
    private Activity mContext;
    private DeviceInfo mDeviceInfo;

    private String content;
    private boolean hasPhoneNumber;
    private String mScheduleNo;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Runnable DeviceTaskOvertime = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(DeviceTaskOvertime);
            mScheduleNo = null;
            getView().dismissOperatingLoadingDialog();
            getView().showErrorTipDialog(mContext.getString(R.string.operation_request_time_out));

        }
    };

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        mDeviceInfo = (DeviceInfo) mContext.getIntent().getSerializableExtra(EXTRA_DEVICE_INFO);
        requestDeviceRecentLog();
    }

    private void freshTopData() {
        String sn = mDeviceInfo.getSn();
        getView().setSNText(sn);
        String typeName = mContext.getString(R.string.power_supply);
        try {
            DeviceMergeTypesInfo.DeviceMergeTypeConfig localDevicesMergeTypes = PreferencesHelper.getInstance().getLocalDevicesMergeTypes().getConfig();
            String mergeType = mDeviceInfo.getMergeType();
            String deviceType = mDeviceInfo.getDeviceType();
            if (TextUtils.isEmpty(mergeType)) {
                mergeType = WidgetUtil.handleMergeType(deviceType);
            }
            Map<String, MergeTypeStyles> mergeTypeMap = localDevicesMergeTypes.getMergeType();
            MergeTypeStyles mergeTypeStyles = mergeTypeMap.get(mergeType);
            typeName = mergeTypeStyles.getName();

        } catch (Exception e) {
            e.printStackTrace();
        }
        getView().setDeviceTypeName(typeName);
        refreshOperationStatus();
        String statusText;
        int textColor;
        switch (mDeviceInfo.getStatus()) {
            case SENSOR_STATUS_ALARM:
                textColor = mContext.getResources().getColor(R.color.c_f34a4a);
                statusText = mContext.getString(R.string.main_page_warm);
                getView().setErasureStatus(true);
                break;
            case SENSOR_STATUS_NORMAL:
                textColor = mContext.getResources().getColor(R.color.c_29c093);
                statusText = mContext.getString(R.string.normal);
                break;
            case SENSOR_STATUS_LOST:
                textColor = mContext.getResources().getColor(R.color.c_5d5d5d);
                statusText = mContext.getString(R.string.status_lost);
                break;
            case SENSOR_STATUS_INACTIVE:
                textColor = mContext.getResources().getColor(R.color.c_b6b6b6);
                statusText = mContext.getString(R.string.status_inactive);
                break;
            case SENSOR_STATUS_MALFUNCTION:
                textColor = mContext.getResources().getColor(R.color.c_fdc83b);
                statusText = mContext.getString(R.string.status_malfunction);
                break;
            default:
                textColor = mContext.getResources().getColor(R.color.c_29c093);
                statusText = mContext.getString(R.string.normal);
                break;
        }
        String name = mDeviceInfo.getName();
        getView().setStatusInfo(statusText, textColor);
        getView().setTitleNameTextView(TextUtils.isEmpty(name) ? sn : name);
        //
        String contact = null;
        String phone = null;
        try {
            contact = mDeviceInfo.getAlarms().getNotification().getContact();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            phone = mDeviceInfo.getAlarms().getNotification().getContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(contact) && TextUtils.isEmpty(phone)) {
            getView().setNoContact();
            hasPhoneNumber = false;
        } else {
            if (TextUtils.isEmpty(contact)) {
                contact = mContext.getString(R.string.not_set);
            }
            hasPhoneNumber = !TextUtils.isEmpty(phone);
            getView().setContactPhoneIconVisible(hasPhoneNumber);
            if (hasPhoneNumber) {
                this.content = phone;
            } else {
                this.content = mContext.getString(R.string.not_set);
            }
            getView().setContractName(contact);
            getView().setContractPhone(content);
        }
        getView().setUpdateTime(DateUtil.getStrTimeToday(mContext, mDeviceInfo.getUpdatedTime(), 0));
        String tags[] = mDeviceInfo.getTags();
        if (tags != null && tags.length > 0) {
            List<String> list = Arrays.asList(tags);
            getView().updateTags(list);

        }
        SensorStruct batteryStruct = mDeviceInfo.getSensoroDetails().get("battery");
        if (batteryStruct != null) {
            String battery = batteryStruct.getValue().toString();
            if (battery.equals("-1.0") || battery.equals("-1")) {
                getView().setBatteryInfo(mContext.getString(R.string.power_supply));
            } else {
                getView().setBatteryInfo(WidgetUtil.subZeroAndDot(battery) + "%");
            }
        }
        int interval = mDeviceInfo.getInterval();
        getView().setInterval(DateUtil.secToTimeBefore(mContext, interval));
    }

    private void refreshOperationStatus() {
        boolean isContains = Constants.DEVICE_CONTROL_DEVICE_TYPES.contains(mDeviceInfo.getDeviceType());
        getView().setDeviceOperationVisible(isContains);
        getView().setErasureStatus(false);
        getView().setResetStatus(false);
        getView().setPsdStatus(true);
        getView().setQueryStatus(true);
        getView().setSelfCheckStatus(true);
        getView().setAirSwitchConfigStatus(true);
        if (isContains) {
            switch (mDeviceInfo.getStatus()) {
                case SENSOR_STATUS_ALARM:
                    getView().setErasureStatus(true);
                    getView().setResetStatus(true);
                    break;
                case SENSOR_STATUS_NORMAL:
                    break;
                case SENSOR_STATUS_LOST:
                case SENSOR_STATUS_INACTIVE:
                    getView().setPsdStatus(false);
                    getView().setQueryStatus(false);
                    getView().setSelfCheckStatus(false);
                    getView().setAirSwitchConfigStatus(false);
                    break;
                case SENSOR_STATUS_MALFUNCTION:
                    break;
                default:
                    break;
            }
        }
    }

    private void freshLocationDeviceInfo() {
        GeocodeSearch geocoderSearch = new GeocodeSearch(mContext);
        geocoderSearch.setOnGeocodeSearchListener(this);
        double[] lonlat = mDeviceInfo.getLonlat();
        try {
            double v = lonlat[1];
            double v1 = lonlat[0];
            if (v == 0 || v1 == 0) {
                getView().setDeviceLocation(mContext.getString(R.string.not_positioned), false);
                getView().setDeviceLocationTextColor(R.color.c_a6a6a6);
                return;
            }
            RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(v, v1), 200, GeocodeSearch.AMAP);
            geocoderSearch.getFromLocationAsyn(query);
        } catch (Exception e) {
            e.printStackTrace();
            getView().setDeviceLocationTextColor(R.color.c_a6a6a6);
            getView().setDeviceLocation(mContext.getString(R.string.not_positioned), false);

        }

    }

    private void requestDeviceRecentLog() {
        String sn = mDeviceInfo.getSn();
        getView().showProgressDialog();
        //合并请求
        RetrofitServiceHelper.INSTANCE.getDeviceDetailInfoList(sn, null, 1).subscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>() {
            @Override
            public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {
                if (deviceInfoListRsp.getData().size() > 0) {
                    mDeviceInfo = deviceInfoListRsp.getData().get(0);
                }
                freshLocationDeviceInfo();
                freshTopData();
                handleDeviceInfoAdapter();
                getView().dismissProgressDialog();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });

    }

    private void handleDeviceInfoAdapter() {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                final ArrayList<MonitoringPointRcContentAdapterModel> uiData = new ArrayList<>();
                if (mDeviceInfo != null) {
                    if (mDeviceInfo.getStatus() == SENSOR_STATUS_MALFUNCTION) {
                        Map<String, MalfunctionDataBean> malfunctionData = mDeviceInfo.getMalfunctionData();
                        Set<String> keySet = malfunctionData.keySet();
                        ArrayList<String> keyList = new ArrayList<>();
                        for (String key : keySet) {
                            if (!keyList.contains(key)) {
                                keyList.add(key);
                            }
                        }
                        Collections.sort(keyList);
                        for (String key : keyList) {
                            MalfunctionDataBean malfunctionDataBean = malfunctionData.get(key);
                            MonitoringPointRcContentAdapterModel monitoringPointRcContentAdapterModel = new MonitoringPointRcContentAdapterModel();
                            monitoringPointRcContentAdapterModel.name = "故障成因";
                            monitoringPointRcContentAdapterModel.statusColorId = R.color.c_fdc83b;
                            monitoringPointRcContentAdapterModel.content = malfunctionDataBean.getDescription();
                            uiData.add(monitoringPointRcContentAdapterModel);
                            LogUtils.loge("故障成因：key = " + key + "value = " + malfunctionDataBean.getDescription());
                        }

                    }
                    //
                    String[] sensorTypes = mDeviceInfo.getSensorTypes();
                    Map<String, SensorStruct> sensoroDetails = mDeviceInfo.getSensoroDetails();
                    Map<String, SensorTypeStyles> sensorTypeMap = PreferencesHelper.getInstance().getLocalDevicesMergeTypes().getConfig().getSensorType();
                    if (sensorTypes != null && sensorTypes.length > 0 && sensorTypeMap != null && sensoroDetails != null) {
                        List<String> sortSensorTypes = Arrays.asList(sensorTypes);
                        for (String type : sortSensorTypes) {
                            if (!TextUtils.isEmpty(type)) {
                                SensorTypeStyles sensorTypeStyles = sensorTypeMap.get(type);
                                if (sensorTypeStyles != null) {
                                    MonitoringPointRcContentAdapterModel monitoringPointRcContentAdapterModel = new MonitoringPointRcContentAdapterModel();
                                    String name = sensorTypeStyles.getName();
                                    if (TextUtils.isEmpty(name)) {
                                        monitoringPointRcContentAdapterModel.name = mContext.getResources().getString(R.string.unknown);
                                    } else {
                                        monitoringPointRcContentAdapterModel.name = name;
                                    }
                                    boolean bool = sensorTypeStyles.isBool();
                                    SensorStruct sensorStruct = sensoroDetails.get(type);
                                    if (sensorStruct != null) {
                                        Object value = sensorStruct.getValue();
                                        if (value != null) {
                                            if (bool) {
                                                if (value instanceof Boolean) {
                                                    String trueMean = sensorTypeStyles.getTrueMean();
                                                    String falseMean = sensorTypeStyles.getFalseMean();
                                                    if ((Boolean) value) {
                                                        if (!TextUtils.isEmpty(trueMean)) {
                                                            monitoringPointRcContentAdapterModel.content = trueMean;
                                                        }
                                                    } else {
                                                        if (!TextUtils.isEmpty(falseMean)) {
                                                            monitoringPointRcContentAdapterModel.content = falseMean;
                                                        }
                                                    }

                                                }
                                            } else {
                                                String unit = sensorTypeStyles.getUnit();
                                                if (!TextUtils.isEmpty(unit)) {
                                                    monitoringPointRcContentAdapterModel.unit = unit;
                                                }
                                                WidgetUtil.judgeIndexSensorType(monitoringPointRcContentAdapterModel, type, value);
                                            }
                                        }
                                    }
                                    int status = mDeviceInfo.getStatus();
                                    switch (status) {
                                        case SENSOR_STATUS_ALARM:
                                            monitoringPointRcContentAdapterModel.statusColorId = R.color.sensoro_alarm;
                                            break;
                                        case SENSOR_STATUS_INACTIVE:
                                            monitoringPointRcContentAdapterModel.statusColorId = R.color.sensoro_inactive;
                                            break;
                                        case SENSOR_STATUS_LOST:
                                            monitoringPointRcContentAdapterModel.statusColorId = R.color.sensoro_lost;
                                            break;
                                        case SENSOR_STATUS_NORMAL:
                                            monitoringPointRcContentAdapterModel.statusColorId = R.color.c_29c093;
                                            break;
                                        case SENSOR_STATUS_MALFUNCTION:
                                            monitoringPointRcContentAdapterModel.statusColorId = R.color.c_fdc83b;
                                            break;
                                        default:
                                            monitoringPointRcContentAdapterModel.statusColorId = R.color.c_29c093;
                                            break;
                                    }
                                    switch (status) {
                                        case SENSOR_STATUS_ALARM:
                                        case SENSOR_STATUS_NORMAL:
                                            List<DeviceAlarmsRecord> alarmsRecords = mDeviceInfo.getAlarmsRecords();
                                            try {
                                                if (alarmsRecords != null) {
                                                    for (DeviceAlarmsRecord deviceAlarmsRecord : alarmsRecords) {
                                                        String sensorTypeStr = deviceAlarmsRecord.getSensorTypes();
                                                        if (type.equalsIgnoreCase(sensorTypeStr)) {
                                                            int alarmStatus = deviceAlarmsRecord.getAlarmStatus();
                                                            switch (alarmStatus) {
                                                                case 1:
                                                                    monitoringPointRcContentAdapterModel.statusColorId = R.color.c_29c093;
                                                                    break;
                                                                case 2:
                                                                    monitoringPointRcContentAdapterModel.statusColorId = R.color.sensoro_alarm;
                                                                    break;
                                                            }
                                                        }
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();

                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                    uiData.add(monitoringPointRcContentAdapterModel);
                                }
                            }

                        }
                    }
                }
                //
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getView().updateDeviceInfoAdapter(uiData);
                    }
                });

            }
        });

    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            case EVENT_DATA_SOCKET_DATA_INFO:
                if (data instanceof DeviceInfo) {
                    final DeviceInfo pushDeviceInfo = (DeviceInfo) data;
                    if (pushDeviceInfo.getSn().equals(mDeviceInfo.getSn())) {
                        if (AppUtils.isActivityTop(mContext, MonitorPointDetailActivity.class)) {
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (getView() != null) {
                                        mDeviceInfo = pushDeviceInfo;
                                        freshLocationDeviceInfo();
                                        freshTopData();
                                        handleDeviceInfoAdapter();
                                    }
                                }
                            });
                        }
                    }
                }
                break;
            case EVENT_DATA_SOCKET_MONITOR_POINT_OPERATION_TASK_RESULT:
                if (data instanceof MonitorPointOperationTaskResultInfo) {
                    MonitorPointOperationTaskResultInfo info = (MonitorPointOperationTaskResultInfo) data;
                    final String scheduleNo = info.getScheduleNo();
                    if (!TextUtils.isEmpty(scheduleNo) && info.getTotal() == info.getComplete()) {
                        String[] split = scheduleNo.split(",");
                        if (split.length > 0) {
                            final String temp = split[0];
                            if (!TextUtils.isEmpty(temp)) {
                                if (AppUtils.isActivityTop(mContext, MonitorPointDetailActivity.class)) {
                                    mContext.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!TextUtils.isEmpty(mScheduleNo) && mScheduleNo.equals(temp)) {
                                                mHandler.removeCallbacks(DeviceTaskOvertime);
                                                getView().dismissOperatingLoadingDialog();
                                                getView().showOperationSuccessToast();
                                            }
                                        }
                                    });
                                }
                            }
                        }

                    }
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
    }


    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        String address = regeocodeResult.getRegeocodeAddress().getFormatAddress();
        LogUtils.loge(this, "onRegeocodeSearched: " + "code = " + i + ",address = " + address);
        if (TextUtils.isEmpty(address)) {
            address = mContext.getString(R.string.unknown_street);
        }
        mDeviceInfo.setAddress(address);
        getView().setDeviceLocation(address, true);
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        LogUtils.loge(this, "onGeocodeSearched: " + "onGeocodeSearched");
    }

    public void doMore() {
        Intent intent = new Intent(mContext, MonitorMoreActivity.class);
        intent.putExtra(EXTRA_SENSOR_SN, mDeviceInfo.getSn());
        getView().startAC(intent);
    }

    public void doNavigation() {
        double[] lonlat = mDeviceInfo.getLonlat();

        double v = lonlat[1];
        double v1 = lonlat[0];
        if (lonlat.length > 1 && v == 0 || v1 == 0) {
            getView().toastShort(mContext.getString(R.string.location_information_not_set));
            return;
        }
        Intent intent = new Intent();
        intent.setClass(mContext, MonitorPointMapActivity.class);
        intent.putExtra(EXTRA_DEVICE_INFO, mDeviceInfo);
        getView().startAC(intent);
    }

    public void doContact() {
        if (hasPhoneNumber) {
            if (TextUtils.isEmpty(content) || mContext.getString(R.string.not_set).equals(content)) {
                getView().toastShort(mContext.getString(R.string.phone_contact_not_set));
                return;
            }
            AppUtils.diallPhone(content, mContext);
        }

    }

    public void doMonitorHistory() {
        String sn = mDeviceInfo.getSn();
        Intent intent = new Intent(mContext, AlarmHistoryLogActivity.class);
        intent.putExtra(EXTRA_SENSOR_SN, sn);
        getView().startAC(intent);
    }

    public void doOperation(int type, String content) {
        String operationType = null;
        Integer switchSpec = null;
        switch (type) {
            case MonitorPointOperationCode.ERASURE:
                operationType = "mute";
                break;
            case MonitorPointOperationCode.RESET:
                operationType = "reset";
                break;
            case MonitorPointOperationCode.PSD:
                operationType = "password";
                break;
            case MonitorPointOperationCode.QUERY:
                operationType = "view";
                break;
            case MonitorPointOperationCode.SELF_CHECK:
                operationType = "check";
                break;
            case MonitorPointOperationCode.AIR_SWITCH_CONFIG:
                operationType = "config";
                Integer integer = null;
                if (TextUtils.isEmpty(content)) {
                    getView().toastShort(mContext.getString(R.string.input_not_null));
                    return;
                }
                try {
                    integer = Integer.valueOf(content);
                    if (integer >= 50 && integer <= 560) {
                        switchSpec = integer;
                    } else {
                        getView().toastShort(mContext.getString(R.string.monitor_point_operation_error_value_range));
                        return;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    getView().toastShort(mContext.getString(R.string.enter_the_correct_number_format));
                    return;
                }

                break;
        }

        requestCmd(operationType, switchSpec);
    }

    private void requestCmd(String operationType, Integer switchSpec) {
        ArrayList<String> sns = new ArrayList<>();
        sns.add(mDeviceInfo.getSn());
        getView().dismissTipDialog();
        getView().showOperationTipLoadingDialog();
        mScheduleNo = null;
        RetrofitServiceHelper.INSTANCE.doMonitorPointOperation(sns, operationType, null, null, switchSpec)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<MonitorPointOperationRequestRsp>() {
            @Override
            public void onCompleted(MonitorPointOperationRequestRsp response) {
                String scheduleNo = response.getScheduleNo();
                if (TextUtils.isEmpty(scheduleNo)) {
                    getView().dismissOperatingLoadingDialog();
                    getView().showErrorTipDialog(mContext.getString(R.string.monitor_point_operation_schedule_no_error));
                } else {
                    String[] split = scheduleNo.split(",");
                    if (split.length > 0) {
                        mScheduleNo = split[0];
                        mHandler.postDelayed(DeviceTaskOvertime, 10 * 1000);
                    } else {
                        getView().dismissOperatingLoadingDialog();
                        getView().showErrorTipDialog(mContext.getString(R.string.monitor_point_operation_schedule_no_error));

                    }
                }
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissOperatingLoadingDialog();
                getView().showErrorTipDialog(errorMsg);
            }
        });

    }
}

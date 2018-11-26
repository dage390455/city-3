package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.constant.MonitorPointOperationCode;
import com.sensoro.smartcity.imainviews.IMonitorPointDetailActivityView;
import com.sensoro.smartcity.iwidget.IOnStart;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.bean.DeviceMergeTypesInfo;
import com.sensoro.smartcity.server.bean.DeviceRecentInfo;
import com.sensoro.smartcity.server.bean.MergeTypeStyles;
import com.sensoro.smartcity.server.bean.MonitorPointOperationTaskResultInfo;
import com.sensoro.smartcity.server.bean.SensorStruct;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.DeviceRecentRsp;
import com.sensoro.smartcity.server.response.MonitorPointOperationRequestRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.util.WidgetUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.igexin.push.util.EncryptUtils.errorMsg;

public class MonitorPointDetailActivityPresenter extends BasePresenter<IMonitorPointDetailActivityView> implements IOnStart, Constants, GeocodeSearch.OnGeocodeSearchListener {
    private Activity mContext;
    private DeviceInfo mDeviceInfo;

    private final List<DeviceRecentInfo> mRecentInfoList = new ArrayList<>();
    private int textColor;
    private String content;
    private boolean hasPhoneNumber;
    private String mScheduleNo;


    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        mDeviceInfo = (DeviceInfo) mContext.getIntent().getSerializableExtra(EXTRA_DEVICE_INFO);
        initCurrentDeviceInfo();
        requestDeviceRecentLog();
    }

    private void freshTopData() {
        refreshOperationStatus();
        String statusText;
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
        String sn = mDeviceInfo.getSn();
        //
        getView().setStatusInfo(statusText, textColor);
        //TODO 显示sn还是姓名等
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
        if(isContains){
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

    private void initCurrentDeviceInfo() {
        getView().setSNText(mDeviceInfo.getSn());
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

//            deviceType.equals()
        } catch (Exception e) {
            e.printStackTrace();
        }
        getView().setDeviceTypeName(typeName);
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
            getView().setDeviceLocation(mContext.getString(R.string.not_positioned), false);

        }

    }

    private void requestDeviceRecentLog() {
        long endTime = mDeviceInfo.getUpdatedTime();
        long startTime = endTime - 2 * 1000 * 60 * 60 * 24;
        String sn = mDeviceInfo.getSn();
        getView().showProgressDialog();
        //合并请求
        Observable<DeviceInfoListRsp> deviceDetailInfoList = RetrofitServiceHelper.INSTANCE.getDeviceDetailInfoList
                (sn, null, 1);
        Observable<DeviceRecentRsp> deviceHistoryList = RetrofitServiceHelper.INSTANCE.getDeviceHistoryList(sn,
                startTime, endTime);
        Observable.merge(deviceDetailInfoList, deviceHistoryList).subscribeOn(Schedulers.io()).doOnNext(new Action1<ResponseBase>() {


            @Override
            public void call(ResponseBase responseBase) {
                if (responseBase instanceof DeviceInfoListRsp) {
                    DeviceInfoListRsp response = (DeviceInfoListRsp) responseBase;
                    if (response.getData().size() > 0) {
                        DeviceInfo deviceInfo = response.getData().get(0);
                        String[] tags = deviceInfo.getTags();
                        mDeviceInfo.setTags(tags);
                    }
                } else if (responseBase instanceof DeviceRecentRsp) {
                    DeviceRecentRsp response = ((DeviceRecentRsp) responseBase);
                    String data = response.getData().toString();
                    try {
//                        String[] sensorTypes = response.getSensorTypes();
                        JSONObject jsonObject = new JSONObject(data);
//                        sensorTypesList = SortUtils.sortSensorTypes(sensorTypes);
                        Iterator<String> iterator = jsonObject.keys();
                        while (iterator.hasNext()) {
                            String keyStr = iterator.next();
                            if (!TextUtils.isEmpty(keyStr)) {
                                JSONObject object = jsonObject.getJSONObject(keyStr);
                                if (object != null) {
                                    DeviceRecentInfo recentInfo = RetrofitServiceHelper.INSTANCE.getGson().fromJson
                                            (object.toString(),
                                                    DeviceRecentInfo.class);
                                    recentInfo.setDate(keyStr);
                                    mRecentInfoList.add(recentInfo);
                                }

                            }
                        }
                        Collections.sort(mRecentInfoList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseBase>(this) {

//            @Override
//            public void onNext(ResponseBase responseBase) {
//                getView().setMapLayoutVisible(true);
//                getView().setMapViewVisible(true);
//                refreshBatteryLayout();
//                refreshKLayout();

//            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }

            @Override
            public void onCompleted(ResponseBase responseBase) {
                freshTopData();
                getView().updateDeviceInfoAdapter(mDeviceInfo);
                getView().dismissProgressDialog();
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
                    DeviceInfo pushDeviceInfo = (DeviceInfo) data;
                    if (pushDeviceInfo.getSn().equalsIgnoreCase(mDeviceInfo.getSn())) {
                        mDeviceInfo = pushDeviceInfo;
                        if (AppUtils.isActivityTop(mContext, MonitorPointDetailActivity.class)) {
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (getView() != null) {
                                        freshTopData();
                                        getView().updateDeviceInfoAdapter(mDeviceInfo);
                                    }

//                        freshStructData();
//                        freshMarker();
                                }
                            });
                        }
                    }
                }
                break;
            case EVENT_DATA_SOCKET_MONITOR_POINT_OPERATION_TASK_RESULT:
                if(data instanceof MonitorPointOperationTaskResultInfo){
                    MonitorPointOperationTaskResultInfo info = (MonitorPointOperationTaskResultInfo) data;
                    final String scheduleNo = info.getScheduleNo();
                    if (!TextUtils.isEmpty(scheduleNo)&& info.getTotal() == info.getComplete()) {
                        String[] split = scheduleNo.split(",");
                        if (split.length>0) {
                            final String temp = split[0];
                            if (!TextUtils.isEmpty(temp)) {
                                if (AppUtils.isActivityTop(mContext,MonitorPointDetailActivity.class)){
                                    mContext.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(!TextUtils.isEmpty(mScheduleNo)&&mScheduleNo.equals(temp)){
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
        mRecentInfoList.clear();
//        if (tempUpBitmap != null) {
//            tempUpBitmap.recycle();
//            tempUpBitmap = null;
//        }
//        if (sensorTypesList != null) {
//            sensorTypesList.clear();
//            sensorTypesList = null;
//        }
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
        getView().setDeviceLocation(mContext.getString(R.string.unknown_street), true);
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

    public void doOperation(int type) {
        String operationType = null;
        switch (type){
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
        }
        ArrayList<String> sns = new ArrayList<>();
        sns.add(mDeviceInfo.getSn());
        RetrofitServiceHelper.INSTANCE.doMonitorPointOperation(sns,operationType,null,null,null)
        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<MonitorPointOperationRequestRsp>() {
            @Override
            public void onCompleted(MonitorPointOperationRequestRsp response) {
                String scheduleNo = response.getScheduleNo();
                if (TextUtils.isEmpty(scheduleNo)) {
                    getView().showErrorTipDialog(mContext.getString(R.string.monitor_point_operation_schedule_no_error));
                }else{
                    String[] split = scheduleNo.split(",");
                    if (split.length>0) {
                        mScheduleNo = split[0];
                    }else{
                        getView().showErrorTipDialog(mContext.getString(R.string.monitor_point_operation_schedule_no_error));

                    }
                }
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().showErrorTipDialog(errorMsg);
            }
        });
    }

    public void clearScheduleNo() {
        mScheduleNo = null;
    }
}

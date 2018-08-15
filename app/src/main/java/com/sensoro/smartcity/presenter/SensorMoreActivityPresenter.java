package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.SensorMoreActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.ISensorMoreActivityView;
import com.sensoro.smartcity.iwidget.IOnStart;
import com.sensoro.smartcity.model.PushData;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.bean.SensorStruct;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.response.DeviceAlarmTimeRsp;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.WidgetUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.sensoro.smartcity.constant.Constants.DEVICE_STATUS_ARRAY;
import static com.sensoro.smartcity.constant.Constants.EXTRA_SENSOR_SN;
import static com.sensoro.smartcity.constant.Constants.SENSOR_STATUS_ALARM;
import static com.sensoro.smartcity.constant.Constants.SENSOR_STATUS_INACTIVE;
import static com.sensoro.smartcity.constant.Constants.SENSOR_STATUS_LOST;
import static com.sensoro.smartcity.constant.Constants.SENSOR_STATUS_NORMAL;

public class SensorMoreActivityPresenter extends BasePresenter<ISensorMoreActivityView> implements IOnStart {
    private Activity mContext;
    private String mSn;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        mSn = mContext.getIntent().getStringExtra(EXTRA_SENSOR_SN);
    }

    public void requestData() {
        getView().showProgressDialog();
        //合并请求
        Observable<DeviceInfoListRsp> deviceDetailInfoList = RetrofitServiceHelper.INSTANCE.getDeviceDetailInfoList
                (mSn, null, 1);
        Observable<DeviceAlarmTimeRsp> deviceAlarmTime = RetrofitServiceHelper.INSTANCE.getDeviceAlarmTime(mSn);
        Observable.merge(deviceDetailInfoList, deviceAlarmTime).subscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseBase>() {


            @Override
            public void onCompleted() {
                getView().dismissProgressDialog();
            }

            @Override
            public void onNext(ResponseBase responseBase) {
                if (responseBase instanceof DeviceInfoListRsp) {
                    refresh((DeviceInfoListRsp) responseBase);
                } else if (responseBase instanceof DeviceAlarmTimeRsp) {
                    DeviceAlarmTimeRsp deviceAlarmTimeRsp = (DeviceAlarmTimeRsp) responseBase;
                    long time = deviceAlarmTimeRsp.getData().getTimeStamp();
                    if (time == -1) {
                        getView().setAlarmRecentInfo(R.string.tips_no_alarm);
                    } else {
                        //修改全部为时间格式
                        getView().setAlarmRecentInfo(DateUtil.getFullDate(time));
                    }
                }
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }

    private void refresh(DeviceInfoListRsp response) {
        try {
            if (response.getData().size() > 0) {
                DeviceInfo deviceInfo = response.getData().get(0);
                getView().setSNText(mSn);
                getView().setTypeText(WidgetUtil.parseSensorTypes(mContext, deviceInfo.getSensorTypes()));

                String tags[] = deviceInfo.getTags();
                if (tags != null && tags.length > 0) {
                    getView().setTags(tags);
                }
                String lon = "" + deviceInfo.getLonlat()[0];
                String lat = "" + deviceInfo.getLonlat()[1];
                getView().setLongitudeLatitude(WidgetUtil.subZeroAndDot(lon), WidgetUtil.subZeroAndDot(lat));
                //
//                deviceInfo.getSensorTypes();
                AlarmInfo.RuleInfo rules[] = deviceInfo.getAlarms().getRules();

                String alarm = null;
                StringBuilder stringBuilder = new StringBuilder();
                for (AlarmInfo.RuleInfo ruleInfo : rules) {
                    String sensorType = ruleInfo.getSensorTypes();
                    alarm = WidgetUtil.getBooleanAlarm(sensorType);
                    if (!TextUtils.isEmpty(alarm)) {
                        stringBuilder.append(alarm);
                        break;
                    }
                }
                if (TextUtils.isEmpty(alarm)) {
                    for (AlarmInfo.RuleInfo ruleInfo : rules) {
                        String sensorType = ruleInfo.getSensorTypes();
                        sensorType = WidgetUtil.getSensorTypeChinese(sensorType);
                        String value = String.valueOf(ruleInfo.getThresholds());
                        value = WidgetUtil.subZeroAndDot(value);
                        String conditionType = ruleInfo.getConditionType();
                        String rule = null;
                        if (conditionType != null) {
                            switch (conditionType) {
                                case "gt":
                                    rule = sensorType + ">" + value;
                                    break;
                                case "lt":
                                    rule = sensorType + "<" + value;
                                    break;
                                case "gte":
                                    rule = sensorType + ">=" + value;
                                    break;
                                case "lte":
                                    rule = sensorType + "<=" + value;
                                    break;
                            }
                            stringBuilder.append(" ").append(rule);
                        }

                    }
                }
                //

                getView().setAlarmSetting(stringBuilder.toString());
                //
                int interval = deviceInfo.getInterval();
                getView().setInterval(DateUtil.secToTimeBefore(interval));
                //

                String name = deviceInfo.getName();
                if (TextUtils.isEmpty(name)) {
                    getView().setName(R.string.unname);
                } else {
                    getView().setName(deviceInfo.getName());
                }
                //
                freshStructData(deviceInfo);
                //
                SensorStruct batteryStruct = deviceInfo.getSensoroDetails().get("battery");
                if (batteryStruct != null) {
                    String battery = batteryStruct.getValue().toString();
                    if (battery.equals("-1.0") || battery.equals("-1")) {
                        getView().setBatteryInfo("电源供电");
                    } else {
                        getView().setBatteryInfo(WidgetUtil.subZeroAndDot(battery) + "%");
                    }
                }
                String content = deviceInfo.getAlarms().getNotification().getContent();
                if (!TextUtils.isEmpty(content)) {
                    getView().setPhoneText(content);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            getView().toastShort(mContext.getResources().getString(R.string.tips_data_error));
        }


    }

    private void freshStructData(DeviceInfo deviceInfo) {
        int statusId = R.drawable.shape_marker_alarm;
        switch (deviceInfo.getStatus()) {
            case SENSOR_STATUS_ALARM://alarm
                statusId = R.drawable.shape_marker_alarm;
                break;
            case SENSOR_STATUS_NORMAL://normal
                statusId = R.drawable.shape_marker_normal;
                break;
            case SENSOR_STATUS_LOST://lost
                statusId = R.drawable.shape_marker_lost;
                break;
            case SENSOR_STATUS_INACTIVE://inactive
                statusId = R.drawable.shape_marker_inactive;

                break;
        }
        getView().setStatusInfo(DEVICE_STATUS_ARRAY[deviceInfo.getStatus()], statusId);
        if (deviceInfo.getUpdatedTime() > 0) {
            getView().setReportText(DateUtil.getFullDate(deviceInfo.getUpdatedTime()));
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PushData data) {
        if (data != null) {
            DeviceInfo tempDeviceInfo = null;
            List<DeviceInfo> deviceInfoList = data.getDeviceInfoList();
            for (DeviceInfo deviceInfo : deviceInfoList) {
                if (mSn.equals(deviceInfo.getSn())) {
                    tempDeviceInfo = deviceInfo;
                    break;
                }
            }
            if (tempDeviceInfo != null && isActivityTop()) {
                freshStructData(tempDeviceInfo);
            }
        }
        if (data != null) {
            LogUtils.loge(this, data.toString());
        }
    }

    private boolean isActivityTop() {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(SensorMoreActivity.class.getName());
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {

    }
}

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
import com.sensoro.smartcity.server.response.CityObserver;
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
    private String sensor_sn;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        sensor_sn = mContext.getIntent().getStringExtra(EXTRA_SENSOR_SN);
    }

    public void requestData() {
        getView().showProgressDialog();
        //合并请求
        Observable<DeviceInfoListRsp> deviceDetailInfoList = RetrofitServiceHelper.INSTANCE.getDeviceDetailInfoList
                (sensor_sn, null, 1);
        Observable<DeviceAlarmTimeRsp> deviceAlarmTime = RetrofitServiceHelper.INSTANCE.getDeviceAlarmTime(sensor_sn);
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
                        getView().setAlarmRecentInfo(DateUtil.getFullParseDate(time));
                    }
                }
            }

            @Override
            public void onErrorMsg(String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }

    private void refresh(DeviceInfoListRsp response) {
        try {
            if (response.getData().size() > 0) {
                DeviceInfo deviceInfo = response.getData().get(0);
//                titleTextView.setEditText(sensor_sn);
                getView().setSNText(sensor_sn);
                getView().setTypeText(parseSensorTypes(deviceInfo.getSensorTypes()));

                String tags[] = deviceInfo.getTags();
//                StringBuffer sb = new StringBuffer();
//                for (int i = 0; i < tags.length; i++) {
//                    if (i > 0) {
//                        sb.append("， " + tags[i]);
//                    } else {
//                        sb.append(tags[i]);
//                    }
//                }
//                getView().setTagText(sb.toString());
                //TODO 可以替换 tag显示方式
                if (tags != null && tags.length > 0) {
                    getView().setTags(tags);
                }
                getView().setLongitudeLatitude("" + deviceInfo.getLonlat()[0], "" + deviceInfo.getLonlat()[1]);
//                lonTextView.setEditText("" + deviceInfo.getLonlat()[0]);
//                lanTextView.setEditText("" + deviceInfo.getLonlat()[1]);
                AlarmInfo.RuleInfo rules[] = deviceInfo.getAlarms().getRules();
                StringBuffer sbRule = new StringBuffer();
                for (AlarmInfo.RuleInfo ruleInfo : rules) {
                    String sensorType = ruleInfo.getSensorTypes();
                    sensorType = WidgetUtil.getSensorTypeChinese(sensorType);
                    float value = ruleInfo.getThresholds();
                    String conditionType = ruleInfo.getConditionType();
                    String rule = null;
                    if (conditionType != null) {
                        if (conditionType.equals("gt")) {
                            rule = sensorType + ">" + value;
                        } else if (conditionType.equals("lt")) {
                            rule = sensorType + "<" + value;
                        } else if (conditionType.equals("gte")) {
                            rule = sensorType + ">=" + value;
                        } else if (conditionType.equals("lte")) {
                            rule = sensorType + "<=" + value;
                        }
                        sbRule.append(" " + rule);
                    }
                }
                getView().setAlarmSetting(sbRule.toString());
                getView().setInterval(deviceInfo.getInterval() + "s");
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
                SensorStruct batteryStruct = deviceInfo.getSensoroDetails().loadData().get("battery");
                if (batteryStruct != null) {
                    String battery = batteryStruct.getValue().toString();
                    if (battery.equals("-1.0") || battery.equals("-1")) {
                        getView().setBatteryInfo("电源供电");
                    } else {
                        getView().setBatteryInfo("" + battery.toString() + "%");
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

    private String parseSensorTypes(String[] sensorTypes) {
        if (sensorTypes.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (String device : sensorTypes) {
                sb.append(device);
            }
            String temp = sb.toString();
            if (temp.contains("temperature")) {
                return "温湿度传感器";
            } else if (temp.contains("cover")) {
                return "井位传感器";
            } else if (temp.contains("pm")) {
                return "PM2.5/PM10传感器";
            } else if (temp.contains("pitch")) {
                return "倾角传感器";
            } else if (temp.contains("latitude")) {
                return "追踪器";
            } else if (temp.contains("temp1")) {
                return "温度";
            } else if (temp.contains("CURRENT")) {
                return "电表";
            } else {
                return mContext.getString(R.string.unknown);
            }
        } else {
            String sensorType = sensorTypes[0];
            if (sensorType.equals("light") || sensorType.equals("temperature")) {
                return "温湿度传感器";
            } else if (sensorType.equals("pitch") || sensorType.equals("roll") || sensorType.equals("yaw")) {
                return "倾角传感器";
            } else if (sensorType.equals("cover") || sensorType.equals("level")) {
                return "井位传感器";
            } else if (sensorType.equals("pm2_5") || sensorType.equals("pm10")) {
                return "PM2.5/PM10传感器";
            } else if (sensorType.equals("ch4")) {
                return "甲烷传感器";
            } else if (sensorType.equals("co")) {
                return "一氧化碳传感器";
            } else if (sensorType.equals("co2")) {
                return "二氧化碳传感器";
            } else if (sensorType.equals("leak")) {
                return "跑冒滴漏传感器";
            } else if (sensorType.equals("smoke")) {
                return "烟雾传感器";
            } else if (sensorType.equals("lpg")) {
                return "液化石油气传感器";
            } else if (sensorType.equals("no2")) {
                return "二氧化氮传感器";
            } else if (sensorType.equals("so2")) {
                return "二氧化硫传感器";
            } else if (sensorType.equals("artificialGas")) {
                return "人工煤气";
            } else if (sensorType.equals("waterPressure")) {
                return "水压传感器";
            } else if (sensorType.equals("magnetic")) {
                return "地磁传感器";
            } else if (sensorType.equals("flame")) {
                return "火焰传感器";
            } else if (sensorType.equalsIgnoreCase("cover")) {
                return "井盖传感器";
            } else if (sensorType.equalsIgnoreCase("level")) {
                return "水位传感器";
            } else if (sensorType.equalsIgnoreCase("drop")) {
                return "滴漏传感器";
            } else if (sensorType.equalsIgnoreCase("smoke")) {
                return "烟感传感器";
            } else if (sensorType.equalsIgnoreCase("altitude")) {
                return "追踪器";
            } else if (sensorType.equalsIgnoreCase("latitude")) {
                return "追踪器";
            } else if (sensorType.equalsIgnoreCase("longitude")) {
                return "追踪器";
            } else if (sensorType.equalsIgnoreCase("alarm")) {
                return "紧急报警器";
            } else if (sensorType.equalsIgnoreCase("distance")) {
                return "距离水位传感器";
            } else {
                return mContext.getString(R.string.unknown);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PushData data) {
        if (data != null) {
            DeviceInfo tempDeviceInfo = null;
            List<DeviceInfo> deviceInfoList = data.getDeviceInfoList();
            for (DeviceInfo deviceInfo : deviceInfoList) {
                if (sensor_sn.equals(deviceInfo.getSn())) {
                    tempDeviceInfo = deviceInfo;
                    break;
                }
            }
            if (tempDeviceInfo != null && isActivityTop()) {
                freshStructData(tempDeviceInfo);
            }
        }
        LogUtils.loge(this, data.toString());
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

    public void onStop() {
        EventBus.getDefault().unregister(this);
    }
}

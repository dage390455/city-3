package com.sensoro.smartcity.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.mobstat.StatService;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.bean.SensorStruct;
import com.sensoro.smartcity.server.response.DeviceAlarmTimeRsp;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.widget.statusbar.StatusBarCompat;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sensoro.smartcity.constant.Constants.DEVICE_STATUS_ARRAY;
import static com.sensoro.smartcity.constant.Constants.EXTRA_SENSOR_SN;
import static com.sensoro.smartcity.constant.Constants.SENSOR_STATUS_ALARM;
import static com.sensoro.smartcity.constant.Constants.SENSOR_STATUS_INACTIVE;
import static com.sensoro.smartcity.constant.Constants.SENSOR_STATUS_LOST;
import static com.sensoro.smartcity.constant.Constants.SENSOR_STATUS_NORMAL;

/**
 * Created by sensoro on 17/7/31.
 */

public class SensorMoreActivity extends BaseActivity {

    @BindView(R.id.sensor_more_back)
    ImageView backImageView;
    @BindView(R.id.sensor_more_title_sn)
    TextView titleTextView;
    @BindView(R.id.sensor_more_tv_sn)
    TextView snTextView;
    @BindView(R.id.sensor_more_tv_name)
    TextView nameTextView;
    @BindView(R.id.sensor_more_tv_type)
    TextView typeTextView;
    @BindView(R.id.sensor_more_tv_status)
    TextView statusTextView;
    @BindView(R.id.sensor_more_tv_tag)
    TextView tagTextView;
    @BindView(R.id.sensor_more_tv_battery)
    TextView batteryTextView;
    @BindView(R.id.sensor_more_tv_lon)
    TextView lonTextView;
    @BindView(R.id.sensor_more_tv_lan)
    TextView lanTextView;
    @BindView(R.id.sensor_more_tv_phone)
    TextView phoneTextView;
    @BindView(R.id.sensor_more_tv_report)
    TextView reportTextView;
    @BindView(R.id.sensor_more_tv_interval)
    TextView intervalTextView;
    @BindView(R.id.sensor_more_tv_alarm_setting)
    TextView alarmSettingTextView;
    @BindView(R.id.sensor_more_tv_alarm_recent)
    TextView alarmRecentTextView;

    private String sensor_sn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_more);
        ButterKnife.bind(this);
        requestData();
        StatusBarCompat.setStatusBarColor(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    protected boolean isNeedSlide() {
        return true;
    }

    private void refresh(DeviceInfoListRsp response) {
        try {
            if (response.getData().size() > 0) {
                DeviceInfo deviceInfo = response.getData().get(0);
                titleTextView.setText(sensor_sn);
                snTextView.setText(sensor_sn);
                String name = deviceInfo.getName();
                if (name == null || name.equals("")) {
                    nameTextView.setText(R.string.unname);
                } else {
                    nameTextView.setText(deviceInfo.getName());
                }
                typeTextView.setText(parseSensorTypes(deviceInfo.getSensorTypes()));
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
                statusTextView.setText(DEVICE_STATUS_ARRAY[deviceInfo.getStatus()]);
                statusTextView.setBackground(getResources().getDrawable(statusId));
                String tags[] = deviceInfo.getTags();
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < tags.length; i++) {
                    sb.append(" " + tags[i]);
                }
                tagTextView.setText(sb.toString());
                SensorStruct batteryStruct = deviceInfo.getSensoroDetails().loadData().get("battery");
                if (batteryStruct != null) {
                    String battery = batteryStruct.getValue().toString();
                    if (battery.equals("-1.0") || battery.equals("-1")) {
                        batteryTextView.setText("电源供电");
                    } else {
                        batteryTextView.setText("" + battery.toString() + "%");
                    }

                }

                lonTextView.setText("" + deviceInfo.getLonlat()[0]);
                lanTextView.setText("" + deviceInfo.getLonlat()[1]);
                String content = deviceInfo.getAlarms().getNotification().getContent();
                if (content != null) {
                    phoneTextView.setText("" + content);
                }
                if (deviceInfo.getUpdatedTime() > 0) {
                    reportTextView.setText("" + DateUtil.getFullDate(deviceInfo.getUpdatedTime()));
                }
                intervalTextView.setText("" + deviceInfo.getInterval() + "s");
                AlarmInfo.RuleInfo rules[] = deviceInfo.getAlarms().getRules();
                StringBuffer sbRule = new StringBuffer();
                for (int i = 0; i < rules.length; i++) {
                    AlarmInfo.RuleInfo ruleInfo = rules[i];
                    String sensorType = ruleInfo.getSensorTypes();
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
                alarmSettingTextView.setText(sbRule.toString());
            }
            requestDataWithRecentAlarm();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.tips_data_error, Toast.LENGTH_SHORT).show();
        }


    }

    private String parseSensorTypes(String[] sensorTypes) {
        if (sensorTypes.length > 1) {
            if (sensorTypes.toString().contains("temperature")) {
                return "温湿度传感器";
            } else if (sensorTypes.toString().contains("cover")) {
                return "井位传感器";
            } else if (sensorTypes.toString().contains("pm")) {
                return "PM2.5/PM10传感器";
            } else if (sensorTypes.toString().contains("pitch")) {
                return "倾角传感器";
            } else if (sensorTypes.toString().contains("latitude")) {
                return "追踪器";
            } else {
                return getString(R.string.unknown);
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
                return getString(R.string.unknown);
            }
        }
    }

    private void requestData() {
        sensor_sn = this.getIntent().getStringExtra(EXTRA_SENSOR_SN);
        SensoroCityApplication sensoroCityApplication = (SensoroCityApplication) getApplication();
        sensoroCityApplication.smartCityServer.getDeviceDetailInfoList(sensor_sn, null, 1, new Response
                .Listener<DeviceInfoListRsp>() {
            @Override
            public void onResponse(DeviceInfoListRsp response) {
                refresh(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError.networkResponse != null) {
                    String reason = new String(volleyError.networkResponse.data);
                    try {
                        JSONObject jsonObject = new JSONObject(reason);
                        Toast.makeText(getApplicationContext(), jsonObject.getString("errmsg"), Toast.LENGTH_SHORT)
                                .show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {

                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.tips_network_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void requestDataWithRecentAlarm() {
        SensoroCityApplication sensoroCityApplication = (SensoroCityApplication) getApplication();
        sensoroCityApplication.smartCityServer.getDeviceAlarmTime(snTextView.getText().toString(), new Response
                .Listener<DeviceAlarmTimeRsp>() {
            @Override
            public void onResponse(DeviceAlarmTimeRsp response) {
                long time = response.getData().getTimeStamp();
                if (time == -1) {
                    alarmRecentTextView.setText(R.string.tips_no_alarm);
                } else {
                    alarmRecentTextView.setText(DateUtil.getFullParseDate(time));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError.networkResponse != null) {
                    String reason = new String(volleyError.networkResponse.data);
                    try {
                        JSONObject jsonObject = new JSONObject(reason);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {

                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.tips_network_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @OnClick(R.id.sensor_more_back)
    public void back() {
        this.finish();
    }
}

package com.sensoro.smartcity.factory;

import android.content.Context;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.model.MonitoringPointRcContentAdapterModel;
import com.sensoro.smartcity.model.Elect3DetailModel;
import com.sensoro.smartcity.server.bean.DeviceAlarmsRecord;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.bean.DisplayOptionsBean;
import com.sensoro.smartcity.server.bean.SensorStruct;
import com.sensoro.smartcity.server.bean.SensorTypeStyles;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.util.WidgetUtil;

import java.util.List;
import java.util.Map;

import static com.sensoro.smartcity.constant.Constants.SENSOR_STATUS_ALARM;
import static com.sensoro.smartcity.constant.Constants.SENSOR_STATUS_INACTIVE;
import static com.sensoro.smartcity.constant.Constants.SENSOR_STATUS_LOST;
import static com.sensoro.smartcity.constant.Constants.SENSOR_STATUS_MALFUNCTION;
import static com.sensoro.smartcity.constant.Constants.SENSOR_STATUS_NORMAL;

public class MonitorPointModelsFactory {

    public static Elect3DetailModel createElect3DetailModel(DeviceInfo deviceInfo, int index, DisplayOptionsBean.SpecialBean.DataBean dataBean, Map<String, SensorStruct> sensoroDetails) {
        if (deviceInfo != null && dataBean != null && sensoroDetails != null) {
            String type = dataBean.getType();
            if ("sensorType".equals(type)) {
                String sensoroType = dataBean.getValue();
                SensorTypeStyles sensorTypeStyles = PreferencesHelper.getInstance().getConfigSensorType(sensoroType);
                if (sensorTypeStyles != null) {
                    Elect3DetailModel elect3DetailModel = new Elect3DetailModel();
                    elect3DetailModel.index = index;
                    int status = deviceInfo.getStatus();
                    switch (status) {
                        case SENSOR_STATUS_ALARM:
                            elect3DetailModel.backgroundColor = R.color.c_fde4e4;
                            elect3DetailModel.textColor = R.color.c_922c2c;
                            break;
                        case SENSOR_STATUS_INACTIVE:
                            elect3DetailModel.backgroundColor = R.color.c_f4f4f4;
                            elect3DetailModel.textColor = R.color.c_5d5d5d;
                            break;
                        case SENSOR_STATUS_LOST:
                            elect3DetailModel.backgroundColor = R.color.c_f4f4f4;
                            elect3DetailModel.textColor = R.color.c_b6b6b6;
                            break;
                        case SENSOR_STATUS_NORMAL:
                            elect3DetailModel.backgroundColor = R.color.c_dff6ef;
                            elect3DetailModel.textColor = R.color.c_197358;
                            break;
                        case SENSOR_STATUS_MALFUNCTION:
                            elect3DetailModel.backgroundColor = R.color.c_fff7e2;
                            elect3DetailModel.textColor = R.color.c_987823;
                            break;
                        default:
                            elect3DetailModel.backgroundColor = R.color.c_dff6ef;
                            elect3DetailModel.textColor = R.color.c_197358;
                            break;
                    }
                    //针对预警特殊处理
                    if (SENSOR_STATUS_ALARM == status) {
                        elect3DetailModel.backgroundColor = R.color.c_dff6ef;
                        elect3DetailModel.textColor = R.color.c_197358;
                        List<DeviceAlarmsRecord> alarmsRecords = deviceInfo.getAlarmsRecords();
                        if (alarmsRecords != null) {
                            for (DeviceAlarmsRecord deviceAlarmsRecord : alarmsRecords) {
                                String sensorTypeStr = deviceAlarmsRecord.getSensorTypes();
                                if (sensoroType.equalsIgnoreCase(sensorTypeStr)) {
                                    int alarmStatus = deviceAlarmsRecord.getAlarmStatus();
                                    switch (alarmStatus) {
                                        case 1:
                                            elect3DetailModel.backgroundColor = R.color.c_dff6ef;
                                            elect3DetailModel.textColor = R.color.c_197358;
                                            break;
                                        case 2:
                                            elect3DetailModel.backgroundColor = R.color.c_fde4e4;
                                            elect3DetailModel.textColor = R.color.c_922c2c;
                                            break;
                                    }
                                }
                            }
                        }
                    }
                    boolean bool = sensorTypeStyles.isBool();
                    SensorStruct sensorStruct = sensoroDetails.get(sensoroType);
                    if (sensorStruct != null) {
                        Object value = sensorStruct.getValue();
                        if (value != null) {
                            if (bool) {
                                if (value instanceof Boolean) {
                                    String trueMean = sensorTypeStyles.getTrueMean();
                                    String falseMean = sensorTypeStyles.getFalseMean();
                                    if ((Boolean) value) {
                                        if (!TextUtils.isEmpty(trueMean)) {
                                            elect3DetailModel.text = trueMean;
                                        }
                                    } else {
                                        if (!TextUtils.isEmpty(falseMean)) {
                                            elect3DetailModel.text = falseMean;
                                        }
                                    }

                                }
                            } else {
                                String unit = sensorTypeStyles.getUnit();
                                WidgetUtil.judgeIndexSensorType(elect3DetailModel, sensoroType, value, unit);
                            }
                        }
                    } else {
                        elect3DetailModel.text = "-";
                    }
                    return elect3DetailModel;
                }
            }

        }
        return null;
    }

    public static MonitoringPointRcContentAdapterModel createMonitoringPointRcContentAdapterModel(Context context, DeviceInfo deviceInfo, Map<String, SensorStruct> sensoroDetails, String sensoroType) {
        if (context != null && deviceInfo != null && sensoroDetails != null) {
            SensorStruct sensorStruct = sensoroDetails.get(sensoroType);
            // 只在有数据时进行显示
            SensorTypeStyles sensorTypeStyles = PreferencesHelper.getInstance().getConfigSensorType(sensoroType);
            if (sensorTypeStyles != null) {
                MonitoringPointRcContentAdapterModel monitoringPointRcContentAdapterModel = new MonitoringPointRcContentAdapterModel();
                String name = sensorTypeStyles.getName();
                if (TextUtils.isEmpty(name)) {
                    monitoringPointRcContentAdapterModel.name = context.getResources().getString(R.string.unknown);
                } else {
                    monitoringPointRcContentAdapterModel.name = name;
                }
                int status = deviceInfo.getStatus();
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
                //针对预警特殊处理
                if (SENSOR_STATUS_ALARM == status) {
                    monitoringPointRcContentAdapterModel.statusColorId = R.color.c_29c093;
                    List<DeviceAlarmsRecord> alarmsRecords = deviceInfo.getAlarmsRecords();
                    if (alarmsRecords != null) {
                        for (DeviceAlarmsRecord deviceAlarmsRecord : alarmsRecords) {
                            String sensorTypeStr = deviceAlarmsRecord.getSensorTypes();
                            if (sensoroType.equalsIgnoreCase(sensorTypeStr)) {
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
                }
                boolean bool = sensorTypeStyles.isBool();
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
                            WidgetUtil.judgeIndexSensorType(monitoringPointRcContentAdapterModel, sensoroType, value);
                        }
                    }
                }
                return monitoringPointRcContentAdapterModel;
            }
        }
        return null;
    }

    public static Elect3DetailModel createElect3NameModel(Context context, int index, DisplayOptionsBean.SpecialBean.DataBean dataBean) {
        if (context != null && dataBean != null) {
            String type = dataBean.getType();
            if ("label".equals(type)) {
                Elect3DetailModel elect3DetailModel = new Elect3DetailModel();
                elect3DetailModel.index = index;
                String name = dataBean.getName();
                if (TextUtils.isEmpty(name)) {
                    name = context.getString(R.string.unknown);
                }
                elect3DetailModel.text = name;
                return elect3DetailModel;
            }
        }
        return null;
    }
}

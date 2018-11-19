package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.DeviceAlarmsRecord;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.bean.DeviceMergeTypesInfo;
import com.sensoro.smartcity.server.bean.SensorStruct;
import com.sensoro.smartcity.server.bean.SensorTypeStyles;
import com.sensoro.smartcity.util.DpUtils;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.util.WidgetUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MonitoringPointRcContentAdapter extends RecyclerView.Adapter
        <MonitoringPointRcContentAdapter.MonitoringPointRcContentHolder> implements Constants {
    private final Context mContext;
    private DeviceInfo mDeviceInfo;
    private DeviceMergeTypesInfo.DeviceMergeTypeConfig typeConfig;

    public MonitoringPointRcContentAdapter(Context context) {
        mContext = context;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        mDeviceInfo = deviceInfo;
        typeConfig = PreferencesHelper.getInstance().getLocalDevicesMergeTypes().getConfig();
    }

    @Override
    public MonitoringPointRcContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_monitoring_point_content, parent, false);
        return new MonitoringPointRcContentHolder(view);
    }

    @Override
    public void onBindViewHolder(MonitoringPointRcContentHolder holder, int position) {
        if (mDeviceInfo != null) {
            String[] sensorTypes = mDeviceInfo.getSensorTypes();
//            if (position == sensorTypes.length) {
//                SensorStruct batteryStruct = mDeviceInfo.getSensoroDetails().get("battery");
//                if (batteryStruct != null) {
//                    holder.itemMonitoringPointContentTvName.setText("电量");
//                    String battery = batteryStruct.getValue().toString();
//                    if (battery.equals("-1.0") || battery.equals("-1")) {
//                        holder.itemMonitoringPointContentTvContent.setText("电源供电");
//                        holder.itemMonitoringPointContentTvUnit.setVisibility(View.GONE);
//                    } else {
//                        String batteryValue = WidgetUtil.subZeroAndDot(battery);
//                        holder.itemMonitoringPointContentTvContent.setText(batteryValue);
//                        holder.itemMonitoringPointContentTvUnit.setText("%");
//                        if (Integer.valueOf(batteryValue) < 10) {
//                            holder.itemMonitoringPointContentTvContent.setTextColor(mContext.getResources().getColor(R.color.sensoro_alarm));
//                            holder.itemMonitoringPointContentTvUnit.setTextColor(mContext.getResources().getColor(R.color.sensoro_alarm));
//                        }
//                        holder.itemMonitoringPointContentTvUnit.setText("%");
//
//                    }
//                }
//                return;
//            }
            //
            int color;
            int status = mDeviceInfo.getStatus();
            switch (status) {
                case SENSOR_STATUS_ALARM:
                    color = R.color.sensoro_alarm;
                    break;
                case SENSOR_STATUS_INACTIVE:
                    color = R.color.sensoro_inactive;
                    break;
                case SENSOR_STATUS_LOST:
                    color = R.color.sensoro_lost;
                    break;
                case SENSOR_STATUS_NORMAL:
                    color = R.color.c_29c093;
                    break;
                default:
                    color = R.color.c_29c093;
                    break;
            }
            //
//            holder.itemMonitoringPointContentTvName.setTextColor(mContext.getResources().getColor(color));
            holder.itemMonitoringPointContentTvContent.setTextColor(mContext.getResources().getColor(color));
            holder.itemMonitoringPointContentTvUnit.setTextColor(mContext.getResources().getColor(color));

            Map<String, SensorTypeStyles> sensorTypeMap = typeConfig.getSensorType();
            List<String> sortSensorTypes = Arrays.asList(sensorTypes);
            Map<String, SensorStruct> sensoroDetails = mDeviceInfo.getSensoroDetails();
            if (sensorTypeMap != null && sensoroDetails != null && sortSensorTypes.size() > 0) {
                String type = sortSensorTypes.get(position);
                if (!TextUtils.isEmpty(type)) {
                    SensorTypeStyles sensorTypeStyles = sensorTypeMap.get(type);
                    if (sensorTypeStyles != null) {
                        String name = sensorTypeStyles.getName();
                        if (TextUtils.isEmpty(name)) {
                            holder.itemMonitoringPointContentTvName.setText(R.string.unknown);
                        } else {
                            holder.itemMonitoringPointContentTvName.setText(name);
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
                                                holder.itemMonitoringPointContentTvContent.setText(trueMean);
                                            } else {
                                                WidgetUtil.judgeIndexSensorType(holder.itemMonitoringPointContentTvContent, type,
                                                        true, sensorStruct);
                                            }
                                        } else {
                                            if (!TextUtils.isEmpty(falseMean)) {
                                                holder.itemMonitoringPointContentTvContent.setText(falseMean);
                                            } else {
                                                WidgetUtil.judgeIndexSensorType(holder.itemMonitoringPointContentTvContent, type,
                                                        true, sensorStruct);
                                            }
                                        }

                                    }
                                    holder.itemMonitoringPointContentTvUnit.setVisibility(View.GONE);
                                } else {
                                    String unit = sensorTypeStyles.getUnit();
                                    if (!TextUtils.isEmpty(unit)) {
                                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.itemMonitoringPointContentTvUnit.getLayoutParams();
                                        if ("°".equals(unit)) {
                                            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                            layoutParams.bottomMargin = 0;
                                            holder.itemMonitoringPointContentTvUnit.setLayoutParams(layoutParams);
                                        } else {
                                            layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
                                            layoutParams.bottomMargin = DpUtils.dp2px(mContext, 15);
                                            holder.itemMonitoringPointContentTvUnit.setLayoutParams(layoutParams);
                                        }
                                        holder.itemMonitoringPointContentTvUnit.setText(unit);
                                    }
                                    WidgetUtil.judgeIndexSensorType(holder.itemMonitoringPointContentTvContent, type,
                                            false, sensorStruct);
                                }
                            }
                        }
                    }
                    handleStatus(status, holder, type);
                }

            }
            //


        }
    }

    private void handleStatus(int status, MonitoringPointRcContentHolder holder, String sensoroType) {
        //只针对预警和正常的状态
        switch (status) {
            case SENSOR_STATUS_ALARM:
            case SENSOR_STATUS_NORMAL:
                List<DeviceAlarmsRecord> alarmsRecords = mDeviceInfo.getAlarmsRecords();
                try {
                    if (alarmsRecords != null) {
                        for (DeviceAlarmsRecord deviceAlarmsRecord : alarmsRecords) {
                            String sensorTypeStr = deviceAlarmsRecord.getSensorTypes();
                            if (sensoroType.equalsIgnoreCase(sensorTypeStr)) {
                                int alarmStatus = deviceAlarmsRecord.getAlarmStatus();
                                switch (alarmStatus) {
                                    case 1:
                                        holder.itemMonitoringPointContentTvContent.setTextColor(mContext.getResources().getColor(R.color.c_29c093));
                                        holder.itemMonitoringPointContentTvUnit.setTextColor(mContext.getResources().getColor(R.color.c_29c093));
                                        break;
                                    case 2:
                                        holder.itemMonitoringPointContentTvContent.setTextColor(mContext.getResources().getColor(R.color.sensoro_alarm));
                                        holder.itemMonitoringPointContentTvUnit.setTextColor(mContext.getResources().getColor(R.color.sensoro_alarm));
                                        break;
                                }
                                return;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
                break;
            case SENSOR_STATUS_INACTIVE:
                break;
            case SENSOR_STATUS_LOST:
                break;
            default:
                break;
        }

    }

    @Override
    public int getItemCount() {
        if (mDeviceInfo == null || mDeviceInfo.getSensorTypes() == null) {
            return 0;
        }
        return mDeviceInfo.getSensorTypes().length;
    }

    class MonitoringPointRcContentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_monitoring_point_content_tv_name)
        TextView itemMonitoringPointContentTvName;
        @BindView(R.id.item_monitoring_point_content_tv_content)
        TextView itemMonitoringPointContentTvContent;
        @BindView(R.id.item_monitoring_point_content_tv_unit)
        TextView itemMonitoringPointContentTvUnit;

        MonitoringPointRcContentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

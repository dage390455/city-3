package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.bean.SensorStruct;
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

    public MonitoringPointRcContentAdapter(Context context) {
        mContext = context;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        mDeviceInfo = deviceInfo;
    }

    @Override
    public MonitoringPointRcContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_monitoring_point_content, parent, false);
        return new MonitoringPointRcContentHolder(view);
    }

    @Override
    public void onBindViewHolder(MonitoringPointRcContentHolder holder, int position) {
        if (mDeviceInfo != null) {
            Map<String, SensorStruct> sensoroDetails = mDeviceInfo.getSensoroDetails();
            String[] sensorTypes = mDeviceInfo.getSensorTypes();
            List<String> sortSensorTypes = Arrays.asList(sensorTypes);
            if (sensoroDetails != null && sortSensorTypes.size() > 0) {
                String type = sortSensorTypes.get(position);
                if (!TextUtils.isEmpty(type)) {
                    String sensorTypeChinese = WidgetUtil.getSensorTypeSingleChinese(type);
                    holder.itemMonitoringPointContentTvName.setText(sensorTypeChinese);
                    SensorStruct sensorStruct = sensoroDetails.get(type);
                    if (sensorStruct != null) {
                        WidgetUtil.judgeIndexSensorType(holder.itemMonitoringPointContentTvContent, holder.itemMonitoringPointContentTvUnit, type,
                                sensorStruct);
                    }
                }
            }

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

        }
    }

    @Override
    public int getItemCount() {
        if (mDeviceInfo == null) {
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

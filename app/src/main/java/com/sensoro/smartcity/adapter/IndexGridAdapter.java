package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.bean.SensorStruct;
import com.sensoro.smartcity.util.SortUtils;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroAlarmView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Jack on 2016/9/16.
 */

public class IndexGridAdapter extends RecyclerView.Adapter<IndexGridAdapter.IndexListViewHolder> implements Constants {

    private Context mContext;
    private final List<DeviceInfo> mList = new ArrayList<>();

    private RecycleViewItemClickListener itemClickListener;

    public IndexGridAdapter(Context context, RecycleViewItemClickListener itemClickListener) {
        this.mContext = context;
        this.itemClickListener = itemClickListener;
    }

    public void setData(List<DeviceInfo> list) {
        this.mList.clear();
        //去除动画效果
//        notifyItemRangeRemoved(1, mList.size());
        this.mList.addAll(list);
//        notifyItemRangeChanged(1, list.size());
    }

    public List<DeviceInfo> getData() {
        return mList;
    }

    @Override
    public IndexListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_index_grid, parent, false);
        return new IndexListViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(IndexListViewHolder holder, int position) {
        DeviceInfo deviceInfo = mList.get(position);
        //
        String deviceInfoName = deviceInfo.getName();
        if (TextUtils.isEmpty(deviceInfoName)) {
            holder.item_name.setText(deviceInfo.getSn());
        } else {
            holder.item_name.setText(deviceInfoName);
        }
        //
        int status = deviceInfo.getStatus();
        Drawable drawable = null;
        int color = 0;
        switch (status) {
            case SENSOR_STATUS_ALARM:
                holder.item_iv_status.setVisibility(View.INVISIBLE);
                holder.item_alarm_view.setVisibility(View.VISIBLE);
                drawable = mContext.getResources().getDrawable(R.drawable.shape_status_alarm);
                drawable.setBounds(0, 0, drawable != null ? drawable.getMinimumWidth() : 0, drawable
                        .getMinimumHeight());
                color = R.color.sensoro_alarm;
                setDataInfo(holder, deviceInfo, color);
                break;
            case SENSOR_STATUS_INACTIVE:
                color = R.color.sensoro_inactive;
                holder.item_alarm_view.setVisibility(View.GONE);
                drawable = mContext.getResources().getDrawable(R.drawable.shape_status_inactive);
                drawable.setBounds(0, 0, drawable != null ? drawable.getMinimumWidth() : 0, drawable
                        .getMinimumHeight());
                holder.item_value1.setText(mContext.getString(R.string.status_inactive));
                holder.item_value1.setTextColor(mContext.getResources().getColor(color));
                //
                holder.item_unit1.setVisibility(GONE);
                holder.item_value2.setVisibility(GONE);
                holder.item_unit2.setVisibility(GONE);
                break;
            case SENSOR_STATUS_LOST:
                color = R.color.sensoro_lost;
                holder.item_alarm_view.setVisibility(View.GONE);
                drawable = mContext.getResources().getDrawable(R.drawable.shape_status_lost);
                drawable.setBounds(0, 0, drawable != null ? drawable.getMinimumWidth() : 0, drawable
                        .getMinimumHeight());
                holder.item_value1.setText(mContext.getString(R.string.status_lost));
                holder.item_value1.setTextColor(mContext.getResources().getColor(color));
                //
                holder.item_unit1.setVisibility(GONE);
                holder.item_value2.setVisibility(GONE);
                holder.item_unit2.setVisibility(GONE);
                break;
            case SENSOR_STATUS_NORMAL:
                color = R.color.sensoro_normal;
                holder.item_alarm_view.setVisibility(View.GONE);
                drawable = mContext.getResources().getDrawable(R.drawable.shape_status_normal);
                drawable.setBounds(0, 0, drawable != null ? drawable.getMinimumWidth() : 0, drawable
                        .getMinimumHeight());
                setDataInfo(holder, deviceInfo, color);
                break;
            default:
                holder.item_alarm_view.setVisibility(View.GONE);
                holder.item_iv_status.setVisibility(View.INVISIBLE);
                break;
        }
        holder.item_iv_status.setImageDrawable(drawable);
        //
        holder.item_name.setTextColor(mContext.getResources().getColor(color));

    }

    private void setDataInfo(IndexListViewHolder holder, DeviceInfo deviceInfo, int color) {
        Map<String, SensorStruct> sensoroDetails = deviceInfo.getSensoroDetails();
        String[] sensorTypes = deviceInfo.getSensorTypes();
        //
        List<String> sortSensorTypes = SortUtils.sortSensorTypes(sensorTypes);
        if (sensoroDetails != null && sortSensorTypes.size() > 0) {
            String sensorType1;
            SensorStruct sensorStruct1;
            String sensorType2;
            SensorStruct sensorStruct2;
            if (sortSensorTypes.size() > 1) {
                //两条数据
                sensorType1 = sortSensorTypes.get(0);
                sensorStruct1 = sensoroDetails.get(sensorType1);
                //第一条数据
                if (sensorStruct1 == null) {
                    holder.item_value2.setText("");
                    holder.item_unit2.setVisibility(GONE);
                } else {
                    holder.item_value2.setVisibility(VISIBLE);
                    holder.item_unit2.setVisibility(VISIBLE);
                    holder.item_value2.setTextColor(mContext.getResources().getColor(color));
                    holder.item_unit2.setTextColor(mContext.getResources().getColor(color));
                    WidgetUtil.judgeIndexSensorType(holder.item_value2, holder.item_unit2, sensorType1,
                            sensorStruct1);
                }
                sensorType2 = sortSensorTypes.get(1);
                sensorStruct2 = sensoroDetails.get(sensorType2);
                //第二条数据
                if (sensorStruct2 == null) {
                    holder.item_value1.setText("");
                    holder.item_unit1.setVisibility(GONE);
                } else {
                    holder.item_value1.setVisibility(VISIBLE);
                    holder.item_unit1.setVisibility(VISIBLE);
                    holder.item_value1.setTextColor(mContext.getResources().getColor(color));
                    holder.item_unit1.setTextColor(mContext.getResources().getColor(color));
                    WidgetUtil.judgeIndexSensorType(holder.item_value1, holder.item_unit1, sensorType2,
                            sensorStruct2);
                }

            } else {
                sensorType1 = sortSensorTypes.get(0);
                sensorStruct1 = sensoroDetails.get(sensorType1);
                //只有一条数据
                if (sensorStruct1 != null) {
                    holder.item_value1.setVisibility(VISIBLE);
                    holder.item_unit1.setVisibility(VISIBLE);
                    holder.item_value1.setTextColor(mContext.getResources().getColor(color));
                    holder.item_unit1.setTextColor(mContext.getResources().getColor(color));
                    WidgetUtil.judgeIndexSensorType(holder.item_value1, holder.item_unit1,
                            sensorType1, sensorStruct1);
                } else {
                    holder.item_value1.setText("");
                    holder.item_unit1.setVisibility(GONE);
                }
            }
            if (sensorType1 != null) {
                WidgetUtil.judgeSensorType(mContext, holder.item_iv_type, sensorType1);
            }
            holder.item_iv_status.setVisibility(View.VISIBLE);

        } else {
            holder.item_alarm_view.setVisibility(View.GONE);
            holder.item_iv_status.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    static class IndexListViewHolder extends RecyclerView.ViewHolder {
        final TextView item_name;
        final ImageView item_iv_status;
        final ImageView item_iv_type;
        final SensoroAlarmView item_alarm_view;
        final TextView item_value1;
        final TextView item_unit1;
        final TextView item_value2;
        final TextView item_unit2;
        final RecycleViewItemClickListener itemClickListener;

        IndexListViewHolder(View itemView, RecycleViewItemClickListener itemClickListener) {
            super(itemView);
            this.item_name = (TextView) itemView.findViewById(R.id.item_grid_tv_name);
            this.item_iv_type = (ImageView) itemView.findViewById(R.id.item_grid_icon);
            this.item_alarm_view = (SensoroAlarmView) itemView.findViewById(R.id.item_grid_sensor_call);
            this.item_iv_status = (ImageView) itemView.findViewById(R.id.item_grid_iv_status);
            this.item_value1 = (TextView) itemView.findViewById(R.id.item_grid_value1);
            this.item_unit1 = (TextView) itemView.findViewById(R.id.item_grid_unit1);
            this.item_value2 = (TextView) itemView.findViewById(R.id.item_grid_value2);
            this.item_unit2 = (TextView) itemView.findViewById(R.id.item_grid_unit2);
            this.itemClickListener = itemClickListener;
            itemView.setOnClickListener(onItemClickListener);
        }

        View.OnClickListener onItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, getAdapterPosition());
                }
            }
        };
    }

}

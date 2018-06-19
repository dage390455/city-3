package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.bean.SensorDetailInfo;
import com.sensoro.smartcity.server.bean.SensorStruct;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.AutoSplitTextView;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroAlarmView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Jack on 2016/9/16.
 */

public class IndexGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Constants {

    private Context mContext;
    private List<DeviceInfo> mList = new ArrayList<>();

    RecycleViewItemClickListener itemClickListener;

    public IndexGridAdapter(Context context, RecycleViewItemClickListener itemClickListener) {
        this.mContext = context;
        this.itemClickListener = itemClickListener;
    }

    public void setData(List<DeviceInfo> list) {
        this.mList.clear();
        notifyItemRangeRemoved(1, mList.size());
        this.mList.addAll(list);
        notifyItemRangeChanged(1, list.size());
    }

    public List<DeviceInfo> getData() {
        return mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_index_grid, parent, false);
        return new IndexListViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder tempholder, int position) {
        if (mList == null) {
            return;
        }

        IndexListViewHolder holder = (IndexListViewHolder) tempholder;

        DeviceInfo deviceInfo = mList.get(position);

        SensorDetailInfo sensorDetailInfo = deviceInfo.getSensoroDetails();
        String[] sensorTypes = deviceInfo.getSensorTypes();
        Arrays.sort(sensorTypes);
        if (sensorDetailInfo != null && sensorTypes.length > 0) {
//            holder.item_value2.setEditText("");
//            holder.item_unit2.setEditText("");
            HashMap<String, SensorStruct> stringSensorStructHashMap = sensorDetailInfo.loadData();
            if (sensorTypes.length > 1) {
                //两条数据
                String sensorType1 = sensorTypes[0];
                SensorStruct sensorStruct1 = stringSensorStructHashMap.get(sensorType1);
                //第一条数据
                if (sensorStruct1 == null) {
                    holder.item_value2.setText("");
                    holder.item_unit2.setVisibility(GONE);
                } else {
                    WidgetUtil.judgeIndexSensorType(mContext, holder.item_value2, holder.item_unit2, sensorType1,
                            sensorStruct1);
                }
                String sensorType2 = sensorTypes[1];
                SensorStruct sensorStruct2 = stringSensorStructHashMap.get(sensorType2);
                //第二条数据
                if (sensorStruct2 == null) {
                    holder.item_value1.setText("");
                    holder.item_unit1.setVisibility(GONE);
                } else {
                    WidgetUtil.judgeIndexSensorType(mContext, holder.item_value1, holder.item_unit1, sensorType2,
                            sensorStruct2);
                }
                if (sensorType1 != null) {
                    WidgetUtil.judgeSensorType(mContext, holder.item_iv_type, sensorType1);
                }
            } else {
                String sensorType1 = sensorTypes[0];
                SensorStruct sensorStruct1 = stringSensorStructHashMap.get(sensorType1);
                //只有一条数据
                if (sensorStruct1 != null) {
                    WidgetUtil.judgeSensorType(mContext, holder.item_iv_type, holder.item_value1, holder.item_unit1,
                            sensorType1, sensorStruct1.getValue(), sensorStruct1.getUnit());
                } else {
                    if (sensorType1 != null) {
                        WidgetUtil.judgeSensorType(mContext, holder.item_iv_type, sensorType1);
                    }
                    holder.item_value1.setText("");
                    holder.item_unit1.setVisibility(GONE);
                }
            }
            Drawable drawable = null;
            holder.item_unit1.setVisibility(VISIBLE);
            holder.item_value2.setVisibility(VISIBLE);
            holder.item_unit2.setVisibility(VISIBLE);
            holder.item_iv_status.setVisibility(View.VISIBLE);
            switch (deviceInfo.getStatus()) {
                case SENSOR_STATUS_ALARM:
                    holder.item_iv_status.setVisibility(View.INVISIBLE);
                    holder.item_alarm_view.setVisibility(View.VISIBLE);
                    drawable = mContext.getResources().getDrawable(R.drawable.shape_status_alarm);
                    drawable.setBounds(0, 0, drawable != null ? drawable.getMinimumWidth() : 0, drawable
                            .getMinimumHeight());
                    break;
                case SENSOR_STATUS_INACTIVE:
                    holder.item_alarm_view.setVisibility(View.GONE);
                    drawable = mContext.getResources().getDrawable(R.drawable.shape_status_inactive);
                    drawable.setBounds(0, 0, drawable != null ? drawable.getMinimumWidth() : 0, drawable
                            .getMinimumHeight());
                    holder.item_value1.setText(mContext.getString(R.string.status_inactive));
                    holder.item_unit1.setVisibility(GONE);
                    holder.item_value2.setVisibility(GONE);
                    holder.item_unit2.setVisibility(GONE);
                    break;
                case SENSOR_STATUS_LOST:
                    holder.item_alarm_view.setVisibility(View.GONE);
                    drawable = mContext.getResources().getDrawable(R.drawable.shape_status_lost);
                    drawable.setBounds(0, 0, drawable != null ? drawable.getMinimumWidth() : 0, drawable
                            .getMinimumHeight());
                    holder.item_value1.setText(mContext.getString(R.string.status_lost));
                    holder.item_unit1.setVisibility(GONE);
                    holder.item_value2.setVisibility(GONE);
                    holder.item_unit2.setVisibility(GONE);
                    break;
                case SENSOR_STATUS_NORMAL:
                    holder.item_alarm_view.setVisibility(View.GONE);
                    drawable = mContext.getResources().getDrawable(R.drawable.shape_status_normal);
                    drawable.setBounds(0, 0, drawable != null ? drawable.getMinimumWidth() : 0, drawable
                            .getMinimumHeight());
                    break;
                default:
                    holder.item_alarm_view.setVisibility(View.GONE);
                    holder.item_iv_status.setVisibility(View.INVISIBLE);
                    break;
            }
            holder.item_iv_status.setImageDrawable(drawable);
            int color = 0;
            switch (deviceInfo.getStatus()) {
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
                    color = R.color.sensoro_normal;
                    break;
            }
            holder.item_name.setColor(mContext.getResources().getColor(color));
            holder.item_value1.setTextColor(mContext.getResources().getColor(color));
            holder.item_unit1.setTextColor(mContext.getResources().getColor(color));
            holder.item_value2.setTextColor(mContext.getResources().getColor(color));
            holder.item_unit2.setTextColor(mContext.getResources().getColor(color));
            if (deviceInfo.getName() == null) {
                holder.item_name.setText(deviceInfo.getSn());
            } else {
                if (deviceInfo.getName().equals("")) {
                    holder.item_name.setText(deviceInfo.getSn());
                } else {
                    String name = deviceInfo.getName();
                    holder.item_name.setText(name);
                }
            }
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

    class IndexListViewHolder extends RecyclerView.ViewHolder {
        AutoSplitTextView item_name;
        ImageView item_iv_status;
        ImageView item_iv_type;
        SensoroAlarmView item_alarm_view;
        TextView item_value1;
        TextView item_unit1;
        TextView item_value2;
        TextView item_unit2;
        RecycleViewItemClickListener itemClickListener;

        public IndexListViewHolder(View itemView, RecycleViewItemClickListener itemClickListener) {
            super(itemView);
            this.item_name = (AutoSplitTextView) itemView.findViewById(R.id.item_grid_tv_name);
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

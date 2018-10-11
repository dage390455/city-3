package com.sensoro.smartcity.adapter;

import android.content.Context;
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
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainHomeFragRcContentAdapter extends RecyclerView.Adapter<MainHomeFragRcContentAdapter.MyViewHolder> implements Constants {
    private final Context mContext;
    private final List<DeviceInfo> mList = new ArrayList<>();
    private RecycleViewItemClickListener itemClickListener;
    private OnItemAlarmInfoClickListener onItemAlarmInfoClickListener;

    public interface OnItemAlarmInfoClickListener {
        void onAlarmInfoClick(View v, int position);
    }

    public void setOnItemAlarmInfoClickListener(OnItemAlarmInfoClickListener onItemAlarmInfoClickListener) {
        this.onItemAlarmInfoClickListener = onItemAlarmInfoClickListener;
    }

    public MainHomeFragRcContentAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<DeviceInfo> list) {
        this.mList.clear();
        //去除动画效果
//        notifyItemRangeRemoved(1, list.size());
        this.mList.addAll(list);
//        notifyItemRangeInserted(1, list.size());
    }

    public void setOnItemClickLisenter(RecycleViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public List<DeviceInfo> getData() {
        return mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_main_home_rc_content, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        DeviceInfo deviceInfo = mList.get(position);
        //
        String deviceInfoName = deviceInfo.getName();
        if (TextUtils.isEmpty(deviceInfoName)) {
            holder.mainRcContentTvLocation.setText(deviceInfo.getSn());
        } else {
            holder.mainRcContentTvLocation.setText(deviceInfoName);
        }
        holder.mainRcContentTvTime.setText(DateUtil.getHourFormatDate(deviceInfo.getUpdatedTime()));
        //
        int status = deviceInfo.getStatus();
        //
        String[] sensorTypes = deviceInfo.getSensorTypes();
        if (sensorTypes != null && sensorTypes.length > 0) {
            String sensorType = sensorTypes[0];
            if (!TextUtils.isEmpty(sensorType)) {
                WidgetUtil.judgeSensorTypeNew(holder.mainRcContentImvIcon, sensorType);
            }
        }
        int color = 0;
        switch (status) {
            case SENSOR_STATUS_ALARM:
                color = R.color.c_f34a4a;
                holder.ivItemAlarm.setVisibility(View.VISIBLE);
                if (onItemAlarmInfoClickListener != null) {
                    holder.ivItemAlarm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onItemAlarmInfoClickListener.onAlarmInfoClick(v, position);
                        }
                    });
                }
                break;
            case SENSOR_STATUS_INACTIVE:
                color = R.color.c_b6b6b6;
                holder.ivItemAlarm.setVisibility(View.GONE);
                break;
            case SENSOR_STATUS_LOST:
                color = R.color.c_5d5d5d;
                holder.ivItemAlarm.setVisibility(View.GONE);
                break;
            case SENSOR_STATUS_NORMAL:
                color = R.color.c_29c093;
                holder.ivItemAlarm.setVisibility(View.GONE);
                break;
            default:
                holder.ivItemAlarm.setVisibility(View.GONE);
                break;
        }
        holder.mainRcContentImvIcon.setColorFilter(mContext.getResources().getColor(color));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.main_rc_content_imv_icon)
        ImageView mainRcContentImvIcon;
        @BindView(R.id.iv_item_alarm)
        ImageView ivItemAlarm;
        @BindView(R.id.main_rc_content_tv_location)
        TextView mainRcContentTvLocation;
        @BindView(R.id.main_rc_content_tv_time)
        TextView mainRcContentTvTime;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

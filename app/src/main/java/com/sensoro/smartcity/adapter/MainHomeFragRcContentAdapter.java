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
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainHomeFragRcContentAdapter extends RecyclerView.Adapter<MainHomeFragRcContentAdapter.MyViewHolder> implements Constants {
    private final Context mContext;
    private final List<DeviceInfo> mList = new ArrayList<>();
    private RecycleViewItemClickListener itemClickListener;

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
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        DeviceInfo deviceInfo = mList.get(position);
        //
        String deviceInfoName = deviceInfo.getName();
        if (TextUtils.isEmpty(deviceInfoName)) {
            holder.mainRcContentTvLocation.setText(deviceInfo.getSn());
        } else {
            holder.mainRcContentTvLocation.setText(deviceInfoName);
        }
        holder.mainRcContentTvTime.setText(DateUtil.getFullParseDate(deviceInfo.getUpdatedTime()));
        //
        int status = deviceInfo.getStatus();
        //
        Drawable drawable = null;
        int color = 0;
        switch (status) {
            case SENSOR_STATUS_ALARM:
                color = R.color.sensoro_alarm;
//                holder.item_iv_status.setVisibility(View.INVISIBLE);
//                holder.item_alarm_view.setVisibility(View.VISIBLE);
//                drawable = mContext.getResources().getDrawable(R.drawable.shape_status_alarm);
//                drawable.setBounds(0, 0, drawable != null ? drawable.getMinimumWidth() : 0, drawable
//                        .getMinimumHeight());
                break;
            case SENSOR_STATUS_INACTIVE:
                color = R.color.sensoro_inactive;
//                holder.item_alarm_view.setVisibility(View.GONE);
//                drawable = mContext.getResources().getDrawable(R.drawable.shape_status_inactive);
//                drawable.setBounds(0, 0, drawable != null ? drawable.getMinimumWidth() : 0, drawable
//                        .getMinimumHeight());
//                holder.item_value1.setText(mContext.getString(R.string.status_inactive));
//                holder.item_value1.setTextColor(mContext.getResources().getColor(color));
//                holder.item_unit1.setVisibility(GONE);
//                holder.item_value2.setVisibility(GONE);
//                holder.item_unit2.setVisibility(GONE);
                break;
            case SENSOR_STATUS_LOST:
                color = R.color.sensoro_lost;
//                holder.item_alarm_view.setVisibility(View.GONE);
//                drawable = mContext.getResources().getDrawable(R.drawable.shape_status_lost);
//                drawable.setBounds(0, 0, drawable != null ? drawable.getMinimumWidth() : 0, drawable
//                        .getMinimumHeight());
//                holder.item_value1.setText(mContext.getString(R.string.status_lost));
//                holder.item_value1.setTextColor(mContext.getResources().getColor(color));
//                holder.item_unit1.setVisibility(GONE);
//                holder.item_value2.setVisibility(GONE);
//                holder.item_unit2.setVisibility(GONE);
                break;
            case SENSOR_STATUS_NORMAL:
                color = R.color.sensoro_normal;
//                holder.item_alarm_view.setVisibility(View.GONE);
//                drawable = mContext.getResources().getDrawable(R.drawable.shape_status_normal);
//                drawable.setBounds(0, 0, drawable != null ? drawable.getMinimumWidth() : 0, drawable
//                        .getMinimumHeight());
//                setData(holder, deviceInfo, color);
                break;
            default:
//                holder.item_alarm_view.setVisibility(View.GONE);
//                holder.item_iv_status.setVisibility(View.INVISIBLE);
                break;
        }
//        holder.item_iv_status.setImageDrawable(drawable);
        //
        holder.mainRcContentTvLocation.setTextColor(mContext.getResources().getColor(color));
        holder.mainRcContentTvTime.setTextColor(mContext.getResources().getColor(color));
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

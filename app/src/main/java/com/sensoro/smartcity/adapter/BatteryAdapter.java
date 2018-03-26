package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.server.bean.DeviceRecentInfo;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by fangping on 2016/7/7.
 */

public class BatteryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<DeviceRecentInfo> mList = new ArrayList<>();
    RecycleViewItemClickListener itemClickListener;

    public BatteryAdapter(Context context, RecycleViewItemClickListener itemClickListener) {
        this.mContext = context;
        this.itemClickListener = itemClickListener;
    }

    public void setData(List<DeviceRecentInfo> list) {
        this.mList.clear();
        this.mList.addAll(list);
    }

    public List<DeviceRecentInfo> getData() {
        return mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recent_battery, parent, false);

        return new BatteryViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (mList == null) {
            return;
        }
        DeviceRecentInfo deviceRecentInfo = mList.get(position);
        BatteryViewHolder batteryViewHolder = (BatteryViewHolder) holder;
        Date date = DateUtil.yearStringToDate(deviceRecentInfo.getDate());
        batteryViewHolder.dateTextView.setText(DateUtil.getMonthDate(date.getTime()));
        if (deviceRecentInfo.getBatteryAvg() != null) {
            if (deviceRecentInfo.getBatteryAvg() == -1) {
                batteryViewHolder.batteryImageView.setVisibility(View.GONE);
            } else {
                batteryViewHolder.batteryImageView.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int src_height = mContext.getResources().getDimensionPixelSize(R.dimen.y680);
                layoutParams.height = (int)(src_height * (deviceRecentInfo.getBatteryAvg() / 100f));
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                batteryViewHolder.batteryImageView.setLayoutParams(layoutParams);
            }
        } else {
            batteryViewHolder.batteryImageView.setVisibility(View.GONE);
        }


        if (mList.size() < 4) {
            LinearLayout.LayoutParams batteryLayoutParams = new LinearLayout.LayoutParams(mContext.getResources().getDimensionPixelSize(R.dimen.x200), mContext.getResources().getDimensionPixelSize(R.dimen.y680));
            batteryViewHolder.batteryLayout.setLayoutParams(batteryLayoutParams);
        } else {
            LinearLayout.LayoutParams batteryLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mContext.getResources().getDimensionPixelSize(R.dimen.y680));
            batteryViewHolder.batteryLayout.setLayoutParams(batteryLayoutParams);
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class BatteryViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        ImageView batteryImageView;
        RelativeLayout batteryLayout;
        RecycleViewItemClickListener itemClickListener;

        public BatteryViewHolder(View itemView, RecycleViewItemClickListener itemClickListener) {
            super(itemView);
            this.dateTextView = (TextView) itemView.findViewById(R.id.item_battery_date);
            this.batteryImageView = (ImageView) itemView.findViewById(R.id.item_battery_value);
            this.batteryLayout = (RelativeLayout) itemView.findViewById(R.id.item_battery_layout);
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

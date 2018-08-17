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

public class BatteryAdapter extends RecyclerView.Adapter<BatteryAdapter.BatteryViewHolder> {

    private Context mContext;
    private final List<DeviceRecentInfo> mList = new ArrayList<>();
    private RecycleViewItemClickListener itemClickListener;

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
    public BatteryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recent_battery, parent, false);

        return new BatteryViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(BatteryViewHolder holder, int position) {
        DeviceRecentInfo deviceRecentInfo = mList.get(position);
        Date date = DateUtil.yearStringToDate(deviceRecentInfo.getDate());
        holder.dateTextView.setText(DateUtil.getMonthDate(date.getTime()));

        if (deviceRecentInfo.getBatteryAvg() != null) {
            if (deviceRecentInfo.getBatteryAvg() == -1) {
                holder.batteryImageView.setVisibility(View.GONE);
            } else {
                holder.batteryImageView.setVisibility(View.VISIBLE);
                //
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams
                        .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int src_height = mContext.getResources().getDimensionPixelSize(R.dimen.y680);
                layoutParams.height = (int) (src_height * (deviceRecentInfo.getBatteryAvg() / 100f));
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                holder.batteryImageView.setLayoutParams(layoutParams);
            }
        } else {
            holder.batteryImageView.setVisibility(View.GONE);
        }


        if (mList.size() < 4) {
            LinearLayout.LayoutParams batteryLayoutParams = new LinearLayout.LayoutParams(mContext.getResources()
                    .getDimensionPixelSize(R.dimen.x200), mContext.getResources().getDimensionPixelSize(R.dimen.y680));
            holder.batteryLayout.setLayoutParams(batteryLayoutParams);
        } else {
            LinearLayout.LayoutParams batteryLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, mContext.getResources().getDimensionPixelSize(R.dimen.y680));
            holder.batteryLayout.setLayoutParams(batteryLayoutParams);
        }
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class BatteryViewHolder extends RecyclerView.ViewHolder {
        final TextView dateTextView;
        final ImageView batteryImageView;
        final RelativeLayout batteryLayout;
        final RecycleViewItemClickListener itemClickListener;

        BatteryViewHolder(View itemView, RecycleViewItemClickListener itemClickListener) {
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

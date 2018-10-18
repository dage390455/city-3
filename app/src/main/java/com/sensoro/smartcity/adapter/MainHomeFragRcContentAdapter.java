package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.DiffUtils.HomeContentListAdapterDiff;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.push.ThreadPoolManager;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.bean.DeviceMergeTypesInfo;
import com.sensoro.smartcity.server.bean.DeviceTypeStyles;
import com.sensoro.smartcity.server.bean.MergeTypeStyles;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainHomeFragRcContentAdapter extends RecyclerView.Adapter<MainHomeFragRcContentAdapter.MyViewHolder> implements Constants {
    private final Activity mContext;
    private final List<DeviceInfo> mList = new ArrayList<>();
    private RecycleViewItemClickListener itemClickListener;
    private OnItemAlarmInfoClickListener onItemAlarmInfoClickListener;
    private DeviceMergeTypesInfo.DeviceMergeTypeConfig deviceMergeTypeConfig;

    public interface OnItemAlarmInfoClickListener {
        void onAlarmInfoClick(View v, int position);
    }

    public void setOnItemAlarmInfoClickListener(OnItemAlarmInfoClickListener onItemAlarmInfoClickListener) {
        this.onItemAlarmInfoClickListener = onItemAlarmInfoClickListener;
    }

    public MainHomeFragRcContentAdapter(Activity context) {
        mContext = context;
    }

    public void updateData(final List<DeviceInfo> list) {
        deviceMergeTypeConfig = PreferencesHelper.getInstance().getLocalDevicesMergeTypes().getConfig();
        //
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                HomeContentListAdapterDiff homeContentListAdapterDiff = new HomeContentListAdapterDiff(mList, list);
                final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(homeContentListAdapterDiff, true);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        diffResult.dispatchUpdatesTo(MainHomeFragRcContentAdapter.this);
                        mList.clear();
                        mList.addAll(list);
                    }
                });


            }
        });
    }

    public void setOnItemClickListener(RecycleViewItemClickListener itemClickListener) {
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
        setBottomVisible(holder, position);
        DeviceInfo deviceInfo = mList.get(position);
        //
        String deviceInfoName = deviceInfo.getName();
        String sn = deviceInfo.getSn();
        setContentName(holder, deviceInfoName, sn);
        long updatedTime = deviceInfo.getUpdatedTime();
        setContentTime(holder, updatedTime);
        //
        int status = deviceInfo.getStatus();
        String deviceType = deviceInfo.getDeviceType();

        setContentStatus(holder, position, status, deviceType);

        setListener(holder, position);
    }

    private void setBottomVisible(MyViewHolder holder, int position) {
        if (getItemCount() - 1 == position) {
            holder.lineBottom.setVisibility(View.INVISIBLE);
        } else {
            holder.lineBottom.setVisibility(View.VISIBLE);
        }
    }

    private void setListener(MyViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    private void setContentStatus(final MyViewHolder holder, final int position, int status, String deviceType) {
        String image = null;
        try {
            DeviceTypeStyles deviceTypeStyles = deviceMergeTypeConfig.getDeviceType().get(deviceType);
            MergeTypeStyles mergeTypeStyles = deviceMergeTypeConfig.getMergeType().get(deviceTypeStyles.getMergeType());
            image = mergeTypeStyles.getImage();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.loge("MainHomeFragRcContentAdapter ----->>>deviceType = " + deviceType);
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
        final int colorResId = mContext.getResources().getColor(color);
        Glide.with(mContext)                             //配置上下文
                .load(image)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.mainRcContentImvIcon.setImageDrawable(resource);
                        holder.mainRcContentImvIcon.setColorFilter(colorResId);
                        return true;
                    }
                }).centerCrop().into(holder.mainRcContentImvIcon);
    }

    private void setContentTime(MyViewHolder holder, long updatedTime) {
        holder.mainRcContentTvTime.setText(DateUtil.getHourFormatDate(updatedTime));
    }

    private void setContentName(MyViewHolder holder, String deviceInfoName, String sn) {
        if (TextUtils.isEmpty(deviceInfoName)) {
            holder.mainRcContentTvLocation.setText(sn);
        } else {
            holder.mainRcContentTvLocation.setText(deviceInfoName);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            setBottomVisible(holder, position);
            DeviceInfo deviceInfo = mList.get(position);
            HashMap map = (HashMap) payloads.get(0);
            LogUtils.loge(this, "----------------->>>>" + map.toString());
            Integer status = (Integer) map.get("status");
            if (status != null) {
                LogUtils.loge(this, "status change -->> " + status);
                setContentStatus(holder, position, status, deviceInfo.getDeviceType());
            }
            Long updateTime = (Long) map.get("updateTime");
            if (updateTime != null) {
                LogUtils.loge(this, "updateTime change -->> " + updateTime);
                setContentTime(holder, updateTime);
            }
            String name = (String) map.get("name");
            if (!TextUtils.isEmpty(name)) {
                LogUtils.loge(this, "updateTime name -->> " + name);
                setContentName(holder, name, deviceInfo.getSn());
            }
            setListener(holder, position);
        }
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
        @BindView(R.id.line_bottom)
        View lineBottom;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

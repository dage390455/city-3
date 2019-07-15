package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.server.bean.DeviceInfo;
import com.sensoro.common.server.bean.MergeTypeStyles;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.WidgetUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainHomeFragRcContentAdapter extends RecyclerView.Adapter<MainHomeFragRcContentAdapter.MyViewHolder> implements Constants {
    private final Activity mContext;
    private final List<DeviceInfo> mList = new ArrayList<>();
    private OnContentItemClickListener onContentItemClickListener;

    public interface OnContentItemClickListener {
        void onAlarmInfoClick(View v, int position);

        void onItemClick(View view, int position);
    }

    public void setOnContentItemClickListener(OnContentItemClickListener onContentItemClickListener) {
        this.onContentItemClickListener = onContentItemClickListener;
    }

    public MainHomeFragRcContentAdapter(Activity context) {
        mContext = context;
    }

    public void updateData(final List<DeviceInfo> list) {
        //
//        ThreadPoolManager.getInstance().execute(new Runnable() {
//            @Override
//            public void run() {
//                HomeContentListAdapterDiff homeContentListAdapterDiff = new HomeContentListAdapterDiff(mList, list);
//                final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(homeContentListAdapterDiff, false);
//                mContext.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        diffResult.dispatchUpdatesTo(MainHomeFragRcContentAdapter.this);
//                        mList.clear();
//                        mList.addAll(list);
//                    }
//                });
//
//
//            }
//        });
//        ThreadPoolManager.getInstance().execute(new Runnable() {
//            @Override
//            public void run() {
//                TopListAdapterDiff indexListAdapterDiff = new TopListAdapterDiff(mData, data);
//                final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(indexListAdapterDiff, true);
//                mContext.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        diffResult.dispatchUpdatesTo(new ListUpdateCallback() {
//                            @Override
//                            public void onInserted(final int position, final int count) {
//                                LogUtils.loge("updateData-----onInserted-->>position = " + position + ", count = " + count);
//                                notifyItemRangeInserted(position, count);
//                            }
//
//                            @Override
//                            public void onRemoved(final int position, final int count) {
//                                notifyItemRangeRemoved(position, count);
//
//                            }
//
//                            @Override
//                            public void onMoved(final int fromPosition, final int toPosition) {
//                                notifyItemMoved(fromPosition, toPosition);
//                            }
//
//                            @Override
//                            public void onChanged(final int position, final int count, final Object payload) {
//                                notifyItemRangeChanged(position, count, payload);
//                            }
//                        });
////                        mData.clear();
////                        mData.addAll(data);
//                    }
//                });
//
//            }
//        });
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addData(List<DeviceInfo> list) {
        mList.addAll(list);
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
        String mergeType = deviceInfo.getMergeType();
        if (TextUtils.isEmpty(mergeType)) {
            mergeType = WidgetUtil.handleMergeType(deviceInfo.getDeviceType());
        }
        setContentStatus(holder, position, status, mergeType);
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
                if (onContentItemClickListener != null) {
                    onContentItemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    private void setContentStatus(final MyViewHolder holder, final int position, int status, String mergeType) {
        String image = null;
        MergeTypeStyles mergeTypeStyles = PreferencesHelper.getInstance().getConfigMergeType(mergeType);
        if (mergeTypeStyles != null) {
            image = mergeTypeStyles.getImage();
        }
        int color = 0;
        switch (status) {
            case SENSOR_STATUS_ALARM:
                color = R.color.c_f34a4a;
                holder.ivItemAlarm.setVisibility(View.VISIBLE);
                if (onContentItemClickListener != null) {
                    holder.ivItemAlarm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onContentItemClickListener.onAlarmInfoClick(v, position);
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
                color = R.color.c_1dbb99;
                holder.ivItemAlarm.setVisibility(View.GONE);
                break;
            case SENSOR_STATUS_MALFUNCTION:
                color = R.color.c_fdc83b;
                holder.ivItemAlarm.setVisibility(View.GONE);
                break;
            default:
                holder.ivItemAlarm.setVisibility(View.GONE);
                break;
        }
        final int colorResId = mContext.getResources().getColor(color);

        if ("smoke".equalsIgnoreCase(mergeType) && status == SENSOR_STATUS_ALARM) {
            holder.mainRcContentImvIcon.setImageResource(R.drawable.smoke_alarm_down);
            holder.mainRcContentImvIcon.setColorFilter(colorResId);
        } else {
            Glide.with(mContext)                             //配置上下文
                    .load(image).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).centerCrop())    //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.mainRcContentImvIcon.setImageDrawable(resource);
                            holder.mainRcContentImvIcon.setColorFilter(colorResId);
                            return true;
                        }
                    }).into(holder.mainRcContentImvIcon);
        }

    }

    private void setContentTime(MyViewHolder holder, long updatedTime) {
        if (updatedTime != 0) {
            holder.mainRcContentTvTime.setText(DateUtil.getStrTimeTodayByDevice(mContext, updatedTime));
        } else {
            holder.mainRcContentTvTime.setText("-");
        }
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
            try {
                LogUtils.loge(this, "----------------->>>>" + map.toString());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            Integer status = (Integer) map.get("status");
            if (status != null) {
                try {
                    LogUtils.loge(this, "status change -->> " + status);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                setContentStatus(holder, position, status, deviceInfo.getDeviceType());
            }
            Long updateTime = (Long) map.get("updateTime");
            if (updateTime != null) {
                try {
                    LogUtils.loge(this, "updateTime change -->> " + updateTime);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                setContentTime(holder, updateTime);
            }
            String name = (String) map.get("name");
            if (!TextUtils.isEmpty(name)) {
                try {
                    LogUtils.loge(this, "updateTime name -->> " + name);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
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
            ivItemAlarm.setColorFilter(mContext.getResources().getColor(R.color.c_f34a4a));
        }
    }
}

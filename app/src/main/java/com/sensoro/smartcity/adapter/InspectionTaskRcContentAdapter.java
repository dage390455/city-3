package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.server.bean.InspectionTaskDeviceDetail;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.WidgetUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InspectionTaskRcContentAdapter extends RecyclerView.Adapter<InspectionTaskRcContentAdapter.InspectionTaskRcContentHolder> {
    private final Activity mContext;

    private InspectionTaskRcItemClickListener listener;

    public InspectionTaskRcContentAdapter(Activity context) {
        mContext = context;
    }

    private final List<InspectionTaskDeviceDetail> mDevices = new ArrayList<>();

    @Override
    public InspectionTaskRcContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_inspection_task_content, parent, false);
        return new InspectionTaskRcContentHolder(view);
    }

    public void updateDevices(final List<InspectionTaskDeviceDetail> devices) {
        //TODO 采用动态刷新 数据错位，暂时放弃
//        ThreadPoolManager.getInstance().execute(new Runnable() {
//            @Override
//            public void run() {
//                InspectionTaskContentAdapterDiff inspectionTaskContentAdapterDiff = new InspectionTaskContentAdapterDiff(mDevices, devices);
//                final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(inspectionTaskContentAdapterDiff, true);
//                mContext.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        diffResult.dispatchUpdatesTo(InspectionTaskRcContentAdapter.this);
//                        mDevices.clear();
//                        mDevices.addAll(devices);
//                    }
//                });
//
//
//            }
//        });
        mDevices.clear();
        mDevices.addAll(devices);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(InspectionTaskRcContentHolder holder, final int position) {
        InspectionTaskDeviceDetail deviceDetail = mDevices.get(position);
        String name = deviceDetail.getName();
        setName(holder, name);
        String sn = deviceDetail.getSn();
        boolean nearBy_local = deviceDetail.isNearBy_local();
        setIsNearBy(holder, nearBy_local);
        int status = deviceDetail.getStatus();
        String deviceType = deviceDetail.getDeviceType();
        setSnType(holder, sn, deviceType);
        setStatus(holder, status);
        setListener(holder, position);

    }

    @Override
    public void onBindViewHolder(@NonNull InspectionTaskRcContentHolder holder, int position, @NonNull List<Object> payloads) {
        LogUtils.loge(this, "onBindViewHolder-->>> payloads.size = " + payloads.size());
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            HashMap map = (HashMap) payloads.get(0);
            Integer status = (Integer) map.get("status");
            if (status != null) {
                LogUtils.loge(this, "status change -->> " + status);
                setStatus(holder, status);
            }
            Boolean bNear = (Boolean) map.get("bNear");
            if (bNear != null) {
                setIsNearBy(holder, bNear);
            }
            String name = (String) map.get("name");
            if (!TextUtils.isEmpty(name)) {
                setName(holder, name);
            }
            LogUtils.loge(this, "onBindViewHolder-->>> status = " + status + ",bNear = " + bNear + ",name = " + name);
            setListener(holder, position);
        }
    }

    private void setListener(InspectionTaskRcContentHolder holder, final int position) {
        holder.itemAdapterInspectionTaskTvInspection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    //第二个参数为state 目前先用position，根据需要改
                    listener.onInspectionTaskInspectionClick(position, mDevices.get(position).getStatus());
                }
            }
        });

        holder.itemAdapterInspectionTaskTvNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onInspectionTaskNavigationClick(position);
            }
        });
    }

    private void setStatus(InspectionTaskRcContentHolder holder, int status) {
        switch (status) {
            case 0:
                //未巡检调用的函数跟其他的不一样，我们不一样，每个人都有不同的境遇
                setState(holder);
                holder.itemAdapterInspectionTaskTvInspection.setVisibility(View.VISIBLE);
                holder.itemAdapterInspectionTaskTvInspection.setText("巡检");
                break;
            case 1:
                setState(holder, R.color.c_29c093, "巡检正常");
                holder.itemAdapterInspectionTaskTvInspection.setVisibility(View.GONE);
                break;
            case 2:
                setState(holder, R.color.c_ff8d34, "巡检异常");
                holder.itemAdapterInspectionTaskTvInspection.setVisibility(View.VISIBLE);
                holder.itemAdapterInspectionTaskTvInspection.setText("详情");
                holder.itemAdapterInspectionTaskTvInspection.setTextColor(mContext.getResources().getColor(R.color.c_252525));
                holder.itemAdapterInspectionTaskTvInspection.setBackgroundResource(R.drawable.shape_bg_solid_fa_stroke_df_corner_2dp);
                break;
        }
    }

    private void setSnType(InspectionTaskRcContentHolder holder, String sn, String deviceType) {
        String inspectionDeviceName = WidgetUtil.getInspectionDeviceName(deviceType);
        holder.itemAdapterInspectionTaskTvSn.setText(inspectionDeviceName + " " + sn);
    }

    private void setName(InspectionTaskRcContentHolder holder, String name) {
        holder.itemAdapterInspectionTaskTvName.setText(name);
    }

    private void setIsNearBy(InspectionTaskRcContentHolder holder, boolean nearBy_local) {
        if (nearBy_local) {
            holder.itemAdapterInspectionTaskTvNear.setVisibility(View.VISIBLE);
        } else {
            holder.itemAdapterInspectionTaskTvNear.setVisibility(View.INVISIBLE);
        }
    }

    private void setState(InspectionTaskRcContentHolder holder, @ColorRes int colorId, String text) {
        int color = mContext.getResources().getColor(colorId);
        holder.itemAdapterInspectionTaskTvState.setText(text);
        holder.itemAdapterInspectionTaskTvState.setTextColor(color);
        GradientDrawable gd = (GradientDrawable) mContext.getResources().getDrawable(R.drawable.shape_small_oval_29c);
        gd.setColor(color);
        gd.setBounds(0, 0, gd.getMinimumWidth(), gd.getMinimumHeight());
        holder.itemAdapterInspectionTaskTvState.setCompoundDrawables(gd, null, null, null);

    }

    /**
     * 未巡检独有的函数
     */
    private void setState(InspectionTaskRcContentHolder holder) {
        Resources resources = mContext.getResources();
        holder.itemAdapterInspectionTaskTvState.setText("未巡检");
        holder.itemAdapterInspectionTaskTvState.setTextColor(resources.getColor(R.color.c_a6a6a6));
        GradientDrawable gd = (GradientDrawable) resources.getDrawable(R.drawable.shape_small_oval_29c);
        gd.setColor(resources.getColor(R.color.c_626262));
        gd.setBounds(0, 0, gd.getMinimumWidth(), gd.getMinimumHeight());
        holder.itemAdapterInspectionTaskTvState.setCompoundDrawables(gd, null, null, null);

    }

    public void setOnRecycleViewItemClickListener(InspectionTaskRcItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    class InspectionTaskRcContentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_adapter_inspection_task_tv_name)
        TextView itemAdapterInspectionTaskTvName;
        @BindView(R.id.item_adapter_inspection_task_tv_near)
        TextView itemAdapterInspectionTaskTvNear;
        @BindView(R.id.item_adapter_inspection_task_tv_state)
        TextView itemAdapterInspectionTaskTvState;
        @BindView(R.id.item_adapter_inspection_task_ll_top)
        LinearLayout itemAdapterInspectionTaskLlTop;
        @BindView(R.id.item_adapter_inspection_task_tv_sn)
        TextView itemAdapterInspectionTaskTvSn;
        @BindView(R.id.item_adapter_inspection_task_view)
        View itemAdapterInspectionTaskView;
        @BindView(R.id.item_adapter_inspection_task_tv_inspection)
        TextView itemAdapterInspectionTaskTvInspection;
        @BindView(R.id.item_adapter_inspection_task_tv_navigation)
        TextView itemAdapterInspectionTaskTvNavigation;

        InspectionTaskRcContentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface InspectionTaskRcItemClickListener {
        void onInspectionTaskInspectionClick(int position, int status);

        void onInspectionTaskNavigationClick(int position);
    }
}

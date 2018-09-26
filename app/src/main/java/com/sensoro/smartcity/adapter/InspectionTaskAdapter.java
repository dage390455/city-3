package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorRes;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.InspectionIndexTaskInfo;
import com.sensoro.smartcity.server.bean.InspectionTaskModel;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class InspectionTaskAdapter extends RecyclerView.Adapter<InspectionTaskAdapter.InspectionTaskHolder> {
    private final Context mContext;
    private RecycleViewItemClickListener listener;
    private List<InspectionIndexTaskInfo> mTasks = new ArrayList<>();

    public InspectionTaskAdapter(Context context) {
        mContext = context;
    }

    @Override
    public InspectionTaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_inspection_task, parent, false);
        return new InspectionTaskHolder(view);
    }

    @Override
    public void onBindViewHolder(InspectionTaskHolder holder, final int position) {
        InspectionIndexTaskInfo tasksBean = mTasks.get(position);

        holder.itemInspectionAdapterTvTitle.setText(tasksBean.getName());
        holder.itemInspectionAdapterTvTime.setText(DateUtil.getDateByOtherFormat(tasksBean.getBeginTime())+" - "+DateUtil.getDateByOtherFormat(tasksBean.getEndTime()));

//        switch (tasksBean.getStatus()){
//            case 0:
//                setTvState(holder, R.color.c_8058a5, "待执行");
//                break;
//            case 1:
//                setTvState(holder, R.color.c_3aa7f0, "执行中");
//                break;
//            case 2:
//                setTvState(holder, R.color.c_ff8d34, "超时未完成");
//                break;
//            case 3:
//                setTvState(holder, R.color.c_29c093, "已完成");
//                break;
//            case 4:
//                setTvState(holder, R.color.c_a6a6a6, "超时完成");
//                break;
//        }
        setTvState(holder,Constants.INSPECTION_STATUS_COLORS[tasksBean.getStatus()],Constants.INSPECTION_STATUS_TEXTS[tasksBean.getStatus()]);
        holder.itemInspectionAdapterClRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(v,position);
                }
            }
        });

    }

    public void setOnRecycleViewItemClickListener(RecycleViewItemClickListener listener){
        this.listener = listener;
    }

    private void setTvState(InspectionTaskHolder holder, @ColorRes int color, String text) {
//        GradientDrawable compoundDrawables = (GradientDrawable) holder.itemInspectionAdapterImvState.getBackground();
//        compoundDrawables.setColor(mContext.getResources().getColor(color));
//
//        holder.itemInspectionAdapterTvState.setText(text);
//        holder.itemInspectionAdapterTvState.setTextColor(mContext.getResources().getColor(color));
        WidgetUtil.changeTvState(mContext,holder.itemInspectionAdapterTvState,color,text);

    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    public void updateTaskList(List<InspectionIndexTaskInfo> tasks) {
        mTasks.clear();
        mTasks.addAll(tasks);
        notifyDataSetChanged();
    }

    public InspectionIndexTaskInfo getItem(int position) {
        return mTasks.get(position);
    }

    class InspectionTaskHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_inspection_adapter_tv_title)
        TextView itemInspectionAdapterTvTitle;
        @BindView(R.id.item_inspection_adapter_tv_time)
        TextView itemInspectionAdapterTvTime;
        @BindView(R.id.item_inspection_adapter_imv_arrows)
        ImageView itemInspectionAdapterImvArrows;
        @BindView(R.id.item_inspection_adapter_tv_state)
        TextView itemInspectionAdapterTvState;
        @BindView(R.id.item_inspection_adapter_cl_root)
        ConstraintLayout itemInspectionAdapterClRoot;

        InspectionTaskHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

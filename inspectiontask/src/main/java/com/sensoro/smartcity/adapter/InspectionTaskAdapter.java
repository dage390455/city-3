package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.common.server.bean.InspectionIndexTaskInfo;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.inspectiontask.R;
import com.sensoro.common.utils.WidgetUtil;
import com.sensoro.inspectiontask.R2;
import com.sensoro.smartcity.constant.InspectionConstant;

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
        holder.itemInspectionAdapterTvTime.setText(DateUtil.getDateByOtherFormatPoint(tasksBean.getBeginTime())+" - "+DateUtil.getDateByOtherFormatPoint(tasksBean.getEndTime()));

        //防止status 后台瞎给 造成崩溃，如status 给个6，索引越界
        try {
            setTvState(holder, InspectionConstant.INSPECTION_STATUS_COLORS[tasksBean.getStatus()],mContext.getString(InspectionConstant.INSPECTION_STATUS_TEXTS[tasksBean.getStatus()]));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        @BindView(R2.id.item_inspection_adapter_tv_title)
        TextView itemInspectionAdapterTvTitle;
        @BindView(R2.id.item_inspection_adapter_tv_time)
        TextView itemInspectionAdapterTvTime;
        @BindView(R2.id.item_inspection_adapter_imv_arrows)
        ImageView itemInspectionAdapterImvArrows;
        @BindView(R2.id.item_inspection_adapter_tv_state)
        TextView itemInspectionAdapterTvState;
        @BindView(R2.id.item_inspection_adapter_cl_root)
        ConstraintLayout itemInspectionAdapterClRoot;

        InspectionTaskHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

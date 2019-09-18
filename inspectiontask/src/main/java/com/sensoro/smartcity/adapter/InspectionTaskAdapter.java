package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.common.server.bean.InspectionIndexTaskInfo;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.common.utils.WidgetUtil;
import com.sensoro.inspectiontask.R;
import com.sensoro.inspectiontask.R2;
import com.sensoro.smartcity.constant.InspectionConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sensoro.smartcity.constant.InspectionConstant.TASK_STATUS_EXCING;
import static com.sensoro.smartcity.constant.InspectionConstant.TASK_STATUS_PEDNING_EXC;


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

        setTvState(holder, tasksBean.getStatus());

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

    private void setTvState(InspectionTaskHolder holder, int status) {
        int color=R.color.c_8058a5;
        String text=mContext.getString(R.string.inspection_status_text_pending_execution);
        switch (status) {
            case InspectionConstant.TASK_STATUS_PEDNING_EXC:
                color=R.color.c_8058a5;
                text=mContext.getString(R.string.inspection_status_text_pending_execution);
                break;
            case InspectionConstant.TASK_STATUS_EXCING:
                color=R.color.c_3aa7f0;
                text=mContext.getString(R.string.inspection_status_text_executing);
                break;
            case InspectionConstant.TASK_STATUS_TIMEOUE_UNDONE:
                color=R.color.c_ff8d34;
                text=mContext.getString(R.string.inspection_status_text_timeout_not_completed);
                break;
            case InspectionConstant.TASK_STATUS_DONE:
                color=R.color.c_1dbb99;
                text=mContext.getString(R.string.inspection_status_text_completed);
                break;
            case InspectionConstant.TASK_STATUS_TIMEOUE_DONE:
                color=R.color.c_a6a6a6;
                text=mContext.getString(R.string.inspection_status_text_timeout_completed);
                break;
        }

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

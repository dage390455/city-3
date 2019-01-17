package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.model.TaskOptionModel;

import java.util.ArrayList;
import java.util.List;

public class MonitorDetailOperationAdapter extends RecyclerView.Adapter<MonitorDetailOperationAdapter.MonitorDetailOperationHolder> {
    private Context mContext;
    private final List<TaskOptionModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private OnMonitorDetailOperationAdapterListener listener;

    public void setOnMonitorDetailOperationAdatperListener(OnMonitorDetailOperationAdapterListener listener) {
        this.listener = listener;
    }

    public void updateMonitorDetailOperations(List<TaskOptionModel> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public List<TaskOptionModel> getImages() {
        return mData;
    }

    public MonitorDetailOperationAdapter(Context mContext) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public MonitorDetailOperationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MonitorDetailOperationHolder(mInflater.inflate(R.layout.item_monitoring_point_operation, parent, false));
    }

    @Override
    public void onBindViewHolder(final MonitorDetailOperationHolder holder, final int position) {
        final TaskOptionModel taskOptionModel = mData.get(position);
        holder.ivMonitoringPointOperation.setImageResource(taskOptionModel.drawableResId);
        holder.tvMonitoringPointOperation.setText(taskOptionModel.contentResId);
        holder.tvMonitoringPointOperation.setTextColor(mContext.getResources().getColor(taskOptionModel.textColorResId));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    if (taskOptionModel.clickable) {
                        listener.onClickOperation(holder.itemView, position, taskOptionModel);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MonitorDetailOperationHolder extends RecyclerView.ViewHolder {

        ImageView ivMonitoringPointOperation;
        TextView tvMonitoringPointOperation;

        MonitorDetailOperationHolder(View itemView) {
            super(itemView);
            ivMonitoringPointOperation = (ImageView) itemView.findViewById(R.id.item_iv_monitoring_point_operation);
            tvMonitoringPointOperation = (TextView) itemView.findViewById(R.id.item_tv_monitoring_point_operation);
        }

    }

    public interface OnMonitorDetailOperationAdapterListener {
        void onClickOperation(View view, int position, TaskOptionModel taskOptionModel);
    }
}
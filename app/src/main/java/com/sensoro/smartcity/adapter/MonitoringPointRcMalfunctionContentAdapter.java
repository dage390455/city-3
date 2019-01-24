package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.model.MonitoringPointRcContentAdapterModel;
import com.sensoro.smartcity.base.IMyBaseRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MonitoringPointRcMalfunctionContentAdapter extends RecyclerView.Adapter
        <MonitoringPointRcMalfunctionContentAdapter.MonitoringPointRcContentHolder> implements IMyBaseRecyclerView<MonitoringPointRcContentAdapterModel> {
    private final Context mContext;
    private final List<MonitoringPointRcContentAdapterModel> data = new ArrayList<>();

    public MonitoringPointRcMalfunctionContentAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MonitoringPointRcContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_monitoring_point_malfunction_content, parent, false);
        return new MonitoringPointRcContentHolder(view);
    }

    @Override
    public void onBindViewHolder(MonitoringPointRcContentHolder holder, int position) {
        MonitoringPointRcContentAdapterModel monitoringPointRcContentAdapterModel = data.get(position);
//        holder.itemMonitoringPointContentTvContent.setTextColor(mContext.getResources().getColor(monitoringPointRcContentAdapterModel.statusColorId));
        if (TextUtils.isEmpty(monitoringPointRcContentAdapterModel.name)) {
            holder.itemMonitoringPointContentTvName.setText(R.string.unknown);
        } else {
            holder.itemMonitoringPointContentTvName.setText(monitoringPointRcContentAdapterModel.name);
        }
        if (!TextUtils.isEmpty(monitoringPointRcContentAdapterModel.content)) {
            holder.itemMonitoringPointContentTvContent.setText(monitoringPointRcContentAdapterModel.content);
            holder.itemMonitoringPointContentTvContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void updateAdapter(List<MonitoringPointRcContentAdapterModel> list) {
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public List<MonitoringPointRcContentAdapterModel> getAdapterData() {
        return null;
    }

    class MonitoringPointRcContentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_monitoring_point_malfunction_content_tv_name)
        TextView itemMonitoringPointContentTvName;
        @BindView(R.id.item_monitoring_point_malfunction_content_tv_content)
        TextView itemMonitoringPointContentTvContent;

        MonitoringPointRcContentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

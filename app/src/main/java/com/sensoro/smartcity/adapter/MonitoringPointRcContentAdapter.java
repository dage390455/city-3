package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.smartcity.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MonitoringPointRcContentAdapter extends RecyclerView.Adapter
        <MonitoringPointRcContentAdapter.MonitoringPointRcContentHolder> {
    private final Context mContext;

    public MonitoringPointRcContentAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MonitoringPointRcContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_monitoring_point_content, parent, false);
        return new MonitoringPointRcContentHolder(view);
    }

    @Override
    public void onBindViewHolder(MonitoringPointRcContentHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 8;
    }

    class MonitoringPointRcContentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_monitoring_point_content_tv_name)
        TextView itemMonitoringPointContentTvName;
        @BindView(R.id.item_monitoring_point_content_tv_content)
        TextView itemMonitoringPointContentTvContent;
        public MonitoringPointRcContentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}

package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sensoro.smartcity.R;

public class DeployDeviceTagHistoryTagAdapter extends RecyclerView.Adapter<DeployDeviceTagHistoryTagAdapter.DeployDeviceTagHistoryTagHolder>{
    private final Context mContext;

    public DeployDeviceTagHistoryTagAdapter(Context context) {
        mContext = context;
    }

    @Override
    public DeployDeviceTagHistoryTagHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_deploy_device_history_tag, parent, false);
        return new DeployDeviceTagHistoryTagHolder(view);
    }

    @Override
    public void onBindViewHolder(DeployDeviceTagHistoryTagHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    class DeployDeviceTagHistoryTagHolder extends RecyclerView.ViewHolder{

        public DeployDeviceTagHistoryTagHolder(View itemView) {
            super(itemView);
        }
    }
}

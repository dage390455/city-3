package com.sensoro.smartcity.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.common.model.DeployContactModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeployDeviceDetailAlarmContactAdapter extends RecyclerView.
        Adapter<DeployDeviceDetailAlarmContactAdapter.DeployDeviceDetailAlarmContactHolder> {
    private final Context mContext;
    private final List<DeployContactModel> deployContactModels = new ArrayList<>();

    public DeployDeviceDetailAlarmContactAdapter(Context context) {
        mContext = context;
    }

    public void updateDeployContactModels(List<DeployContactModel> contactModels) {
        deployContactModels.clear();
        deployContactModels.addAll(contactModels);
        notifyDataSetChanged();
    }

    @Override
    public DeployDeviceDetailAlarmContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_deploy_device_alarm_contact,
                parent, false);
        return new DeployDeviceDetailAlarmContactHolder(view);
    }

    @Override
    public void onBindViewHolder(DeployDeviceDetailAlarmContactHolder holder, int position) {
        String name = deployContactModels.get(position).name;
        String phone = deployContactModels.get(position).phone;
        holder.itemDeployDeviceAlarmContact.setText(name + " : " + phone);
    }

    @Override
    public int getItemCount() {
        return deployContactModels.size();
    }

    class DeployDeviceDetailAlarmContactHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_deploy_device_alarm_contact)
        TextView itemDeployDeviceAlarmContact;

        DeployDeviceDetailAlarmContactHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

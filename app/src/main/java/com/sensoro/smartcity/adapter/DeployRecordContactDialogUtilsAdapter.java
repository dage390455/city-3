package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.model.DeviceNotificationBean;
import com.sensoro.smartcity.R;

import java.util.ArrayList;
import java.util.List;


public class DeployRecordContactDialogUtilsAdapter extends RecyclerView.Adapter<DeployRecordContactDialogUtilsAdapter
        .DeployRecordContactDialogUtilsHolder> {

    private Context mContext;
    private final List<DeviceNotificationBean> mList = new ArrayList<>();

    public DeployRecordContactDialogUtilsAdapter(Context context) {
        this.mContext = context;
    }

    public void updateList(List<DeviceNotificationBean> list) {
        this.mList.clear();
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    public List<DeviceNotificationBean> getData() {
        return mList;
    }

    @Override
    public DeployRecordContactDialogUtilsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_deploy_record_contact, parent, false);

        return new DeployRecordContactDialogUtilsHolder(view);
    }

    @Override
    public void onBindViewHolder(DeployRecordContactDialogUtilsHolder holder, int position) {
        DeviceNotificationBean deviceNotificationBean = mList.get(position);
        String contact = deviceNotificationBean.getContact();
        String content = deviceNotificationBean.getContent();
        if (!TextUtils.isEmpty(content)) {
            holder.tvDeployRecordContact.setText(contact + "(" + content + ")");
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class DeployRecordContactDialogUtilsHolder extends RecyclerView.ViewHolder {
        final TextView tvDeployRecordContact;

        DeployRecordContactDialogUtilsHolder(View itemView) {
            super(itemView);
            tvDeployRecordContact = (TextView) itemView.findViewById(R.id.tv_deploy_record_contact);
        }
    }
}

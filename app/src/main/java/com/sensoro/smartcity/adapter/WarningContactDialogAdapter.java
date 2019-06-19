package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.model.DeviceNotificationBean;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;

import java.util.ArrayList;
import java.util.List;

public class WarningContactDialogAdapter extends RecyclerView.Adapter<WarningContactDialogAdapter.AssociationSensorDialogViewHolder> {
    private final Context mContext;
    private List<DeviceNotificationBean> mList = new ArrayList<>();

    public WarningContactDialogAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public AssociationSensorDialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_warning_contact, parent, false);
        return new AssociationSensorDialogViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull AssociationSensorDialogViewHolder holder, int position) {
        DeviceNotificationBean deviceNotificationBean = mList.get(position);
        String content = deviceNotificationBean.getContent();
        holder.tvPhone.setText(content);
        holder.tvName.setText(deviceNotificationBean.getContact());

        holder.llWarningContactoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(content)) {
                    SensoroToast.getInstance().makeText(mContext, mContext.getString(R.string.no_find_contact_phone_number), Toast.LENGTH_SHORT).show();
                } else {
                    AppUtils.diallPhone(content, (Activity) mContext);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateData(List<DeviceNotificationBean> data) {
        mList.clear();
        mList.addAll(data);
        notifyDataSetChanged();
    }

    class AssociationSensorDialogViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvPhone;
        private LinearLayout llWarningContactoot;

        public AssociationSensorDialogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_warning_contact_name);
            tvPhone = itemView.findViewById(R.id.tv_warning_contact_phone);
            llWarningContactoot = itemView.findViewById(R.id.ll_warning_contact);
        }
    }
}

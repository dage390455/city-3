package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.server.bean.AlarmInfo;
import com.sensoro.smartcity.R;

import java.util.ArrayList;
import java.util.List;

public class WarningContactDialogAdapter extends RecyclerView.Adapter<WarningContactDialogAdapter.AssociationSensorDialogViewHolder> {
    private final Context mContext;
    private ArrayList<AlarmInfo.NotificationInfo> mList = new ArrayList<>();

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
//        NamePlateInfo model = mList.get(position);
//
//
//        sb.append(model.deviceTypeName).append(mContext.getString(R.string.monitor))
//                .append("(").append(TextUtils.isEmpty(model.getName()) ? model.getSn() : model.getName()).append(")");
//
//
//        holder.tvPhone.setText(sb.toString());
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public void updateData(List<AlarmInfo.NotificationInfo> data) {
        mList.clear();
        mList.addAll(data);
        notifyDataSetChanged();
    }

    class AssociationSensorDialogViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvPhone;

        public AssociationSensorDialogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_warning_contact_name);
            tvPhone = itemView.findViewById(R.id.tv_warning_contact_phone);
        }
    }
}

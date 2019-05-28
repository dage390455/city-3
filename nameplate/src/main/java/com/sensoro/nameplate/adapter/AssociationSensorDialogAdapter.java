package com.sensoro.nameplate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.nameplate.R;
import com.sensoro.nameplate.model.AddSensorFromListModel;

import java.util.ArrayList;
import java.util.List;

public class AssociationSensorDialogAdapter extends RecyclerView.Adapter<AssociationSensorDialogAdapter.AssociationSensorDialogViewHolder> {
    private final Context mContext;
    private ArrayList<AddSensorFromListModel> mList = new ArrayList<AddSensorFromListModel>();

    public AssociationSensorDialogAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public AssociationSensorDialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_associate_sensor_dialog, parent, false);
        return new AssociationSensorDialogViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull AssociationSensorDialogViewHolder holder, int position) {
        StringBuilder sb = new StringBuilder();
        AddSensorFromListModel model = mList.get(position);
        sb.append(model.deviceTypeName).append(mContext.getString(R.string.monitor))
                .append("(").append(model.name).append(")");
        holder.tvContent.setText(sb.toString());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateData(List<AddSensorFromListModel> data){
        mList.clear();
        mList.addAll(data);
        notifyDataSetChanged();
    }

    class AssociationSensorDialogViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvContent;

        public AssociationSensorDialogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_content_item_adapter_associate_sensor_dialog);
        }
    }
}

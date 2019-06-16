package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.server.bean.NamePlateInfo;
import com.sensoro.smartcity.R;

import java.util.ArrayList;
import java.util.List;

public class WarningPhoneMsgDialogAdapter extends RecyclerView.Adapter<WarningPhoneMsgDialogAdapter.AssociationSensorDialogViewHolder> {
    private final Context mContext;
    private ArrayList<NamePlateInfo> mList = new ArrayList<>();

    public WarningPhoneMsgDialogAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public AssociationSensorDialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_warning_phone_msg, parent, false);
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


        holder.tvState.setText("title==" + position);


//        Random random = new Random();
//        random.nextInt(position);

        holder.llPhoneContent.removeAllViews();
        for (int i = 0; i < position; i++) {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_warning_phone_msg_content, null);
            TextView textView = inflate.findViewById(R.id.tv_warning_contact_phone_msg);
            textView.setText("詹姆斯(18689721456)" + position + "==" + i);
            holder.llPhoneContent.addView(textView);

        }


    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public void updateData(List<NamePlateInfo> data) {
        mList.clear();
        mList.addAll(data);
        notifyDataSetChanged();
    }

    class AssociationSensorDialogViewHolder extends RecyclerView.ViewHolder {
        private TextView tvState;
        private LinearLayout llPhoneContent;

        public AssociationSensorDialogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvState = itemView.findViewById(R.id.tv_warning_contact_phone_msg_state);
            llPhoneContent = itemView.findViewById(R.id.ll_warning_contact_phone_msg_content);
        }
    }
}

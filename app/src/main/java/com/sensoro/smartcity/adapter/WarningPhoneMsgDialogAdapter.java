package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.server.bean.AlarmInfo;
import com.sensoro.smartcity.R;

import java.util.List;

public class WarningPhoneMsgDialogAdapter extends RecyclerView.Adapter<WarningPhoneMsgDialogAdapter.AssociationSensorDialogViewHolder> {
    private final Context mContext;
    private List[] mList = new List[]{};

    private int type;

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
        List stautus = mList[position];
        if (null != stautus && stautus.size() > 0) {

            holder.tvState.setVisibility(View.VISIBLE);
            holder.llPhoneContent.setVisibility(View.VISIBLE);
            holder.llPhoneContent.removeAllViews();

            for (int i = 0; i < stautus.size(); i++) {
                AlarmInfo.RecordInfo.Event event = (AlarmInfo.RecordInfo.Event) stautus.get(i);
                String number = event.getNumber();
                String name = event.getName();
                View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_warning_phone_msg_content, null);
                TextView textView = inflate.findViewById(R.id.tv_warning_contact_phone_msg);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(" ").append(name).append("(").append(number).append(")");
                textView.setText(stringBuilder.toString());
                holder.llPhoneContent.addView(textView);

            }


            if (type == 0) {
                switch (((AlarmInfo.RecordInfo.Event) stautus.get(0)).getReciveStatus()) {
                    case 0:
                        holder.tvState.setText(mContext.getString(R.string.telephone_call));
                        holder.tvState.setTextColor(mContext.getResources().getColor(R.color.c_37B0E9));
                        break;
                    case 1:
                        holder.tvState.setText(mContext.getString(R.string.telephone_answer_success));
                        holder.tvState.setTextColor(mContext.getResources().getColor(R.color.c_1dbb99));
                        break;
                    case 2:
                        holder.tvState.setText(mContext.getString(R.string.telephone_answer_failed));
                        holder.tvState.setTextColor(mContext.getResources().getColor(R.color.color_alarm_pup_red));

                        break;
                    default:
                        holder.tvState.setText(mContext.getString(R.string.telephone_answer_unknow));
                        holder.tvState.setTextColor(mContext.getResources().getColor(R.color.c_6D5EAC));

                        break;
                }
            } else if (type == 1) {
                switch (((AlarmInfo.RecordInfo.Event) stautus.get(0)).getReciveStatus()) {
                    case 0:
                        holder.tvState.setText(mContext.getString(R.string.sms_sending));
                        holder.tvState.setTextColor(mContext.getResources().getColor(R.color.c_37B0E9));


                        break;
                    case 1:
                        holder.tvState.setText(mContext.getString(R.string.sms_received_successfully));
                        holder.tvState.setTextColor(mContext.getResources().getColor(R.color.c_1dbb99));

                        break;
                    case 2:
                        holder.tvState.setText(mContext.getString(R.string.sms_received_failed));
                        holder.tvState.setTextColor(mContext.getResources().getColor(R.color.color_alarm_pup_red));

                        break;
                    default:

                        holder.tvState.setText(mContext.getString(R.string.sms_received_unknow));
                        holder.tvState.setTextColor(mContext.getResources().getColor(R.color.c_6D5EAC));

                        break;
                }
            }
        } else {
            holder.tvState.setVisibility(View.GONE);
            holder.llPhoneContent.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return mList.length;
    }

    public void updateData(int type, List[] data) {
        mList = data;
        this.type = type;
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

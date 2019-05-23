package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.model.AddSensorFromListModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddSensorListAdapter extends RecyclerView.Adapter<AddSensorListAdapter.AddSensorListAdapterViewHolder> {
    private final Context mContext;
    ArrayList<AddSensorFromListModel> mList = new ArrayList<>();
    private OnSensorListCheckListener mListener;

    public AddSensorListAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public AddSensorListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_add_sensor_from_list, parent, false);
        final AddSensorListAdapterViewHolder holder = new AddSensorListAdapterViewHolder(inflate);
        holder.clRootItemAdapterAddSensorList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer position = (Integer) holder.clRootItemAdapterAddSensorList.getTag();
                if (mListener != null) {
                    mListener.onChecked(position);
                }
                notifyItemChanged(position);

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AddSensorListAdapterViewHolder holder, int position) {
        holder.clRootItemAdapterAddSensorList.setTag(position);
        AddSensorFromListModel model = mList.get(position);

        holder.tvNameItemAdapterAddSensorList.setText(model.name);
        holder.tvDeviceNameItemAdapterAddSensorList.setText(model.deviceTypeName);
        holder.tvDeviceSnItemAdapterAddSensorList.setText(model.sn);

        Glide.with(mContext)
                .load(model.iconUrl)
                .thumbnail(0.1f)
                .error(R.drawable.ic_default_image)
                .placeholder(R.drawable.ic_default_image)
                .into(holder.ivIconItemAdapterAddSensorList);
        boolean isCheck = model.isCheck;
        holder.ivStatusItemAdapterAddSensorList.setImageResource(model.isCheck ? R.drawable.radio_btn_checked : R.drawable.radio_btn_unchecked);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateData(ArrayList<AddSensorFromListModel> data) {
        mList.clear();
        mList.addAll(data);
        notifyDataSetChanged();
    }

    public void setOnSensorListCheckListener(OnSensorListCheckListener listener){
        mListener = listener;
    }

    class AddSensorListAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_status_item_adapter_add_sensor_list)
        ImageView ivStatusItemAdapterAddSensorList;
        @BindView(R.id.tv_name_item_adapter_add_sensor_list)
        TextView tvNameItemAdapterAddSensorList;
        @BindView(R.id.iv_icon_item_adapter_add_sensor_list)
        ImageView ivIconItemAdapterAddSensorList;
        @BindView(R.id.tv_device_name_item_adapter_add_sensor_list)
        TextView tvDeviceNameItemAdapterAddSensorList;
        @BindView(R.id.tv_device_sn_item_adapter_add_sensor_list)
        TextView tvDeviceSnItemAdapterAddSensorList;
        @BindView(R.id.cl_root_item_adapter_add_sensor_list)
        ConstraintLayout clRootItemAdapterAddSensorList;
        public AddSensorListAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }
    }

    public interface OnSensorListCheckListener{
        void onChecked(int position);
    }
}

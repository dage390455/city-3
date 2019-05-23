package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sensoro.smartcity.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddSensorListAdapter extends RecyclerView.Adapter<AddSensorListAdapter.AddSensorListAdapterViewHolder> {
    private final Context mContext;


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
                notifyItemChanged(position);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AddSensorListAdapterViewHolder holder, int position) {
        holder.clRootItemAdapterAddSensorList.setTag(position);
        holder.tvNameItemAdapterAddSensorList.setText("费家村小卖铺");
        holder.tvDeviceNameItemAdapterAddSensorList.setText("烟雾");
        holder.tvDeviceSnItemAdapterAddSensorList.setText("1234567890123456");

        Glide.with(mContext)
                .load("")
                .thumbnail(0.1f)
                .error(R.drawable.ic_default_image)
                .placeholder(R.drawable.ic_default_image)
                .into(holder.ivIconItemAdapterAddSensorList);

        holder.ivStatusItemAdapterAddSensorList.setImageResource(R.drawable.radio_btn_checked);
    }

    @Override
    public int getItemCount() {
        return 3;
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
}

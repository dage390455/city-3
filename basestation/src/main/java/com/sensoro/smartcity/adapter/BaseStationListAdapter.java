package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.basestation.R;
import com.sensoro.basestation.R2;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.model.CameraFilterModel;
import com.sensoro.common.server.bean.BaseStationInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseStationListAdapter extends RecyclerView.Adapter<BaseStationListAdapter.DeviceCameraContentHolder> implements Constants {


    private Context mContext;
    private OnDeviceCameraContentClickListener listener;
    private final List<BaseStationInfo> mData = new ArrayList<>();
    private List<CameraFilterModel.ListBean> types;

    public BaseStationListAdapter(Context context) {
        mContext = context;
    }

    public void setOnAlarmHistoryLogConfirmListener(OnDeviceCameraContentClickListener onDeviceCameraContentClickListener) {
        listener = onDeviceCameraContentClickListener;
    }

    public void updateAdapter(List<BaseStationInfo> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setaseStationType(List<CameraFilterModel.ListBean> data) {

        types = data;

    }

    public List<BaseStationInfo> getData() {
        return mData;
    }

    public interface OnDeviceCameraContentClickListener {
        void onItemClick(View v, int position);
    }

    @Override
    public DeviceCameraContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_base_station, parent, false);
        return new DeviceCameraContentHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final DeviceCameraContentHolder holder, final int position) {

        BaseStationInfo deviceCameraInfo = mData.get(position);
        if (deviceCameraInfo != null) {
            String sn = deviceCameraInfo.getSn();
            //
            String name = deviceCameraInfo.getName();
            String type = deviceCameraInfo.getTypeName();
            if (TextUtils.isEmpty(name)) {
                name = sn;
            }
            if (!TextUtils.isEmpty(type)) {
                holder.itemDeviceCameraTvDeviceName.setText(type + " ");

            }
            holder.itemDeviceCameraTvDeviceName.append(name);
            if (!TextUtils.isEmpty(sn)) {
                holder.itemDeviceCameraTvId.setText(sn);
            } else {
                holder.itemDeviceCameraTvId.setText("");

            }


            List<String> tags = deviceCameraInfo.getTags();
            if (null != tags && tags.size() > 0) {
                holder.itemDeviceCameraLlDetail.setVisibility(View.VISIBLE);
                holder.itemDeviceCameraLlDetail.removeAllViews();
                for (String tag : tags) {
                    View view = LayoutInflater.from(mContext).inflate(R.layout.item_basestation_tag_item, null);
                    TextView textView = view.findViewById(R.id.item_basestation_tag_tv);
                    textView.setText(tag);
                    holder.itemDeviceCameraLlDetail.addView(view);
                }
            } else {
                holder.itemDeviceCameraLlDetail.setVisibility(View.GONE);
            }

            String status = deviceCameraInfo.getStatus();
            if ("offline".equals(status)) {
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.item_basestation_offline);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                holder.itemDeviceCameraTvOnlinestate.setText(mContext.getResources().getString(R.string.offline));
                holder.itemDeviceCameraTvOnlinestate.setTextColor(mContext.getResources().getColor(R.color.c_5d5d5d));
                holder.itemDeviceCameraTvOnlinestate.setCompoundDrawables(drawable, null, null, null);

            } else if ("inactive".equals(status)) {
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.item_inactive);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                holder.itemDeviceCameraTvOnlinestate.setCompoundDrawables(drawable, null, null, null);
                holder.itemDeviceCameraTvOnlinestate.setText(mContext.getResources().getString(R.string.inactive));
                holder.itemDeviceCameraTvOnlinestate.setTextColor(mContext.getResources().getColor(R.color.c_a6a6a6));
            } else if ("timeout".equals(status)) {
                //TODO 颜色定义
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.shape_small_oval_6d5eac);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                holder.itemDeviceCameraTvOnlinestate.setCompoundDrawables(drawable, null, null, null);
                holder.itemDeviceCameraTvOnlinestate.setText(mContext.getResources().getString(R.string.time_out));
                holder.itemDeviceCameraTvOnlinestate.setTextColor(mContext.getResources().getColor(R.color.c_6D5EAC));
            } else {
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.item_device_online);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                holder.itemDeviceCameraTvOnlinestate.setCompoundDrawables(drawable, null, null, null);
                holder.itemDeviceCameraTvOnlinestate.setText(mContext.getResources().getString(R.string.normal));
                holder.itemDeviceCameraTvOnlinestate.setTextColor(mContext.getResources().getColor(R.color.c_1dbb99));

            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(v, position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class DeviceCameraContentHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.item_device_camera_tv_device_name)
        TextView itemDeviceCameraTvDeviceName;
        @BindView(R2.id.item_device_camera_tv_onlinestate)
        TextView itemDeviceCameraTvOnlinestate;
        @BindView(R2.id.item_device_camera_tv_id)
        TextView itemDeviceCameraTvId;
        @BindView(R2.id.item_basestation_tv_type)
        TextView item_basestation_tv_type;
        @BindView(R2.id.item_device_camera_ll_detail)
        LinearLayout itemDeviceCameraLlDetail;

        DeviceCameraContentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

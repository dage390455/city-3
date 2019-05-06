package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.DeviceCameraInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceCameraContentAdapter extends RecyclerView.Adapter<DeviceCameraContentAdapter.DeviceCameraContentHolder> implements Constants {


    private Context mContext;
    private OnDeviceCameraContentClickListener listener;
    private final List<DeviceCameraInfo> mData = new ArrayList<>();

    public DeviceCameraContentAdapter(Context context) {
        mContext = context;
    }

    public void setOnAlarmHistoryLogConfirmListener(OnDeviceCameraContentClickListener onDeviceCameraContentClickListener) {
        listener = onDeviceCameraContentClickListener;
    }

    public void updateAdapter(List<DeviceCameraInfo> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public List<DeviceCameraInfo> getData() {
        return mData;
    }

    public interface OnDeviceCameraContentClickListener {
        void onItemClick(View v, int position);
    }

    @Override
    public DeviceCameraContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_device_camera, parent, false);
        return new DeviceCameraContentHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final DeviceCameraContentHolder holder, final int position) {

        DeviceCameraInfo deviceCameraInfo = mData.get(position);
        if (deviceCameraInfo != null) {
            //
            String name = deviceCameraInfo.getName();
            if (TextUtils.isEmpty(name)) {
                name = mContext.getString(R.string.unknown);
            }
//            holder.tvAlarmHistoryLogContent.setText(DateUtil.getStrTimeToday(mContext, alarmLogInfo.getCreatedTime(), 0) + mContext.getString(R.string.occur_alarmed));
            holder.itemDeviceCameraTvDeviceName.setText(name);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(v, position);
                }
            }
        });


        holder.itemDeviceCameraTvId.setText(deviceCameraInfo.getSn());
        if (!TextUtils.isEmpty(deviceCameraInfo.getOrientationName())) {
            holder.orientationTv.setVisibility(View.VISIBLE);
            holder.orientationTv.setText(deviceCameraInfo.getOrientationName());
            holder.orientationTv.setText(deviceCameraInfo.getOrientationName());

        } else {
            holder.orientationTv.setVisibility(View.GONE);

        }
        if (!TextUtils.isEmpty(deviceCameraInfo.getInstallationModeName())) {
            holder.installationmodeTv.setVisibility(View.VISIBLE);
            holder.installationmodeTv.setText(deviceCameraInfo.getInstallationModeName());
        } else {
            holder.installationmodeTv.setVisibility(View.GONE);

        }

        if (deviceCameraInfo.getDeviceStatus().equals("1")) {


            Drawable drawable = mContext.getResources().getDrawable(R.drawable.item_device_online);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.itemDeviceCameraTvOnlinestate.setCompoundDrawables(drawable, null, null, null);
            holder.itemDeviceCameraTvOnlinestate.setText("在线");
            holder.itemDeviceCameraTvOnlinestate.setTextColor(Color.parseColor("#1DBB99"));
        } else {
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.item_device_offline);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());

            holder.itemDeviceCameraTvOnlinestate.setText("离线");
            holder.itemDeviceCameraTvOnlinestate.setTextColor(Color.parseColor("#A6A6A6"));
            holder.itemDeviceCameraTvOnlinestate.setCompoundDrawables(drawable, null, null, null);


        }
//        AlarmInfo.RecordInfo[] recordInfoArray = alarmLogInfo.getRecords();
//        boolean isAlarm = false;
//        for (AlarmInfo.RecordInfo recordInfo : recordInfoArray) {
////                AlarmInfo.RecordInfo.Event[] event = recordInfo.getPhoneList();
//            String type = recordInfo.getType();
//            if ("alarm".equals(type)) {


//                break;
//            }
//        }
        //
//        holder.tvAlarmHistoryLogContent.setText("今天-------->>>>" + position);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class DeviceCameraContentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_device_camera_tv_device_name)
        TextView itemDeviceCameraTvDeviceName;
        @BindView(R.id.item_device_camera_tv_onlinestate)
        TextView itemDeviceCameraTvOnlinestate;
        @BindView(R.id.item_device_camera_tv_id)
        TextView itemDeviceCameraTvId;
        @BindView(R.id.item_device_camera_ll_detail)
        LinearLayout itemDeviceCameraLlDetail;
        @BindView(R.id.orientation_tv)
        TextView orientationTv;
        @BindView(R.id.installationmode_tv)
        TextView installationmodeTv;

        DeviceCameraContentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

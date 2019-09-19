package com.sensoro.forestfire.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.constant.Constants;
import com.sensoro.common.server.bean.ForestFireCameraBean;
import com.sensoro.forestfire.Constants.ForestFireConstans;
import com.sensoro.forestfire.R;
import com.sensoro.forestfire.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * @Author: jack
 * 时  间: 2019-09-17
 * 包  名: com.sensoro.forestfire.activity
 * 简  述: <功能简述:森林防火管理摄像头列表适配器>
 */
public class ForestFireCameraListAdapter extends RecyclerView.Adapter<ForestFireCameraListAdapter.DeviceCameraContentHolder> implements Constants {


    private Context mContext;
    private OnDeviceCameraContentClickListener listener;
    private final List<ForestFireCameraBean> mData = new ArrayList<>();

    public ForestFireCameraListAdapter(Context context) {
        mContext = context;
    }

    public void setOnAlarmHistoryLogConfirmListener(OnDeviceCameraContentClickListener onDeviceCameraContentClickListener) {
        listener = onDeviceCameraContentClickListener;
    }

    public void updateAdapter(List<ForestFireCameraBean> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public List<ForestFireCameraBean> getData() {
        return mData;
    }

    public interface OnDeviceCameraContentClickListener {
        void onItemClick(View v, int position);
    }

    @Override
    public DeviceCameraContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_forestfire_cameralist, parent, false);
        return new DeviceCameraContentHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final DeviceCameraContentHolder holder, final int position) {

        ForestFireCameraBean mForestFireCameraBean = mData.get(position);
        if (mForestFireCameraBean != null) {
            //
            String name = mForestFireCameraBean.getName();
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

        holder.itemDeviceCameraTvId.setText(mForestFireCameraBean.getSn());

        if (mForestFireCameraBean.getInfo()!=null&&mForestFireCameraBean.getInfo().getDeviceStatus()== ForestFireConstans.FOREST_STATE_ONLINE) {
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.item_device_online);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.itemDeviceCameraTvOnlinestate.setCompoundDrawables(drawable, null, null, null);
            holder.itemDeviceCameraTvOnlinestate.setText(mContext.getString(R.string.online));
            holder.itemDeviceCameraTvOnlinestate.setTextColor(mContext.getResources().getColor(R.color.c_1dbb99));
        } else {
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.item_device_offline);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());

            holder.itemDeviceCameraTvOnlinestate.setText(mContext.getString(R.string.offline));
            holder.itemDeviceCameraTvOnlinestate.setTextColor(mContext.getResources().getColor(R.color.c_a6a6a6));
            holder.itemDeviceCameraTvOnlinestate.setCompoundDrawables(drawable, null, null, null);
        }
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

        DeviceCameraContentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

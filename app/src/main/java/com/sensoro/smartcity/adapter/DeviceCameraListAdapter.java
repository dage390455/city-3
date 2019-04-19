package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.DeviceCameraFacePic;
import com.sensoro.smartcity.server.bean.MergeTypeStyles;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.PreferencesHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceCameraListAdapter extends RecyclerView.Adapter<DeviceCameraListAdapter.MyViewHolder> implements Constants {
    private final Activity mContext;
    private final List<DeviceCameraFacePic> mList = new ArrayList<>();
    private OnDeviceCameraListClickListener onDeviceCameraListClickListener;

    public interface OnDeviceCameraListClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnContentItemClickListener(OnDeviceCameraListClickListener onDeviceCameraListClickListener) {
        this.onDeviceCameraListClickListener = onDeviceCameraListClickListener;
    }

    public DeviceCameraListAdapter(Activity context) {
        mContext = context;
    }

    public void updateData(final List<DeviceCameraFacePic> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addData(List<DeviceCameraFacePic> list) {
        mList.addAll(list);
    }

    public List<DeviceCameraFacePic> getData() {
        return mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_device_camera_list_adapter, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        setBottomVisible(holder, position);
        DeviceCameraFacePic deviceCameraFacePic = mList.get(position);
        String faceUrl = deviceCameraFacePic.getFaceUrl();
        String deviceName = deviceCameraFacePic.getDeviceName();
        String sceneUrl = deviceCameraFacePic.getSceneUrl();
        String captureTime = deviceCameraFacePic.getCaptureTime();
        String address = deviceCameraFacePic.getAddress();
        String baseUrl = "https://scpub-eye.antelopecloud.cn";
//        holder.mainRcContentImvIcon
//        Glide.with(mContext)                             //配置上下文
//                .load(sceneUrl)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                .centerCrop().into(holder.mainRcContentImvIcon);
        Glide.with(mContext)                             //配置上下文
                .load(baseUrl + faceUrl)
//                    .thumbnail(0.01f)//设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                .error(R.drawable.deploy_pic_placeholder)           //设置错误图片
                .placeholder(R.drawable.ic_default_image)    //设置占位图片
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                .into(holder.mainRcContentImvIcon);
        try {
            long time = Long.parseLong(captureTime);
            holder.mainRcContentTvTime.setText(DateUtil.getStrTimeTodayByDevice(mContext, time));
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.mainRcContentTvLocation.setText(address);

        //
        setListener(holder, position);
    }

    private void setBottomVisible(MyViewHolder holder, int position) {
        if (getItemCount() - 1 == position) {
            holder.lineBottom.setVisibility(View.INVISIBLE);
        } else {
            holder.lineBottom.setVisibility(View.VISIBLE);
        }
    }

    private void setListener(MyViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeviceCameraListClickListener != null) {
                    onDeviceCameraListClickListener.onItemClick(v, position);
                }
            }
        });
    }

    private void setContentStatus(final MyViewHolder holder, final int position, int status, String mergeType) {
        String image = null;
        MergeTypeStyles mergeTypeStyles = PreferencesHelper.getInstance().getConfigMergeType(mergeType);
        if (mergeTypeStyles != null) {
            image = mergeTypeStyles.getImage();
        }
        int color = 0;
        final int colorResId = mContext.getResources().getColor(color);
        if ("smoke".equalsIgnoreCase(mergeType) && status == SENSOR_STATUS_ALARM) {
            holder.mainRcContentImvIcon.setImageResource(R.drawable.smoke_alarm_down);
            holder.mainRcContentImvIcon.setColorFilter(colorResId);
        } else {
            Glide.with(mContext)                             //配置上下文
                    .load(image)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.mainRcContentImvIcon.setImageDrawable(resource);
                            holder.mainRcContentImvIcon.setColorFilter(colorResId);
                            return true;
                        }
                    }).centerCrop().into(holder.mainRcContentImvIcon);
        }

    }

    private void setContentTime(MyViewHolder holder, long updatedTime) {
        if (updatedTime != 0) {
            holder.mainRcContentTvTime.setText(DateUtil.getStrTimeTodayByDevice(mContext, updatedTime));
        } else {
            holder.mainRcContentTvTime.setText("-");
        }
    }

    private void setContentName(MyViewHolder holder, String deviceInfoName, String sn) {
        if (TextUtils.isEmpty(deviceInfoName)) {
            holder.mainRcContentTvLocation.setText(sn);
        } else {
            holder.mainRcContentTvLocation.setText(deviceInfoName);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.main_rc_content_imv_icon)
        ImageView mainRcContentImvIcon;
        @BindView(R.id.main_rc_content_tv_location)
        TextView mainRcContentTvLocation;
        @BindView(R.id.main_rc_content_tv_time)
        TextView mainRcContentTvTime;
        @BindView(R.id.line_bottom)
        View lineBottom;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

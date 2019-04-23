package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
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
import com.sensoro.smartcity.adapter.model.DeviceCameraFacePicListModel;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.DeviceCameraFacePic;
import com.sensoro.smartcity.server.bean.MergeTypeStyles;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.ImageFactory;
import com.sensoro.smartcity.util.PreferencesHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.Nullable;

public class DeviceCameraListAdapter extends RecyclerView.Adapter<DeviceCameraListAdapter.MyViewHolder> implements Constants {
    private final Activity mContext;
    private final List<DeviceCameraFacePicListModel> mList = new ArrayList<>();
    private OnDeviceCameraListClickListener onDeviceCameraListClickListener;
    private Integer mClickPosition = -1;

    public interface OnDeviceCameraListClickListener {
        void onItemClick(View view, int position);

        void setOnLiveClick();
    }

    public void setOnContentItemClickListener(OnDeviceCameraListClickListener onDeviceCameraListClickListener) {
        this.onDeviceCameraListClickListener = onDeviceCameraListClickListener;
    }

    public DeviceCameraListAdapter(Activity context) {
        mContext = context;
    }

    public void updateData(final ArrayList<DeviceCameraFacePicListModel> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

//    public void addData(List<DeviceCameraFacePic> list) {
//        mList.addAll(list);
//    }

    public List<DeviceCameraFacePicListModel> getData() {
        return mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final MyViewHolder holder;
        if (viewType == 1 || viewType == 2) {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_camera_list_top, parent, false);
            holder = new MyViewHolder(inflate);
            holder.clLiveStream.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickPosition = (Integer) holder.clPicture.getTag();
                    if (onDeviceCameraListClickListener != null) {
                        onDeviceCameraListClickListener.setOnLiveClick();
                    }
                }
            });
        } else {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_device_camera_list_adapter, parent, false);
            holder = new MyViewHolder(inflate);
            holder.clPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer position = (Integer) holder.clPicture.getTag();
                    mClickPosition = position;
                    if (onDeviceCameraListClickListener != null) {
                        onDeviceCameraListClickListener.onItemClick(v, position);
                    }

                }
            });
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (getItemViewType(position) == 1) {
            holder.clLiveStream.setTag(position);
            holder.tvTimeListTop.setVisibility(View.INVISIBLE);
            holder.imvRing.setVisibility(View.VISIBLE);
            holder.tvLiveStream.setVisibility(View.VISIBLE);
            holder.clLiveStream.setBackgroundColor(mContext.getResources().
                    getColor(mClickPosition != -1 && mClickPosition == position ? R.color.c_fafafa : R.color.white));
        } else if(getItemViewType(position) == 2 ){
           holder.clLiveStream.setTag(position);
           holder.tvTimeListTop.setVisibility(View.VISIBLE);
           holder.imvRing.setVisibility(View.INVISIBLE);
           holder.tvLiveStream.setVisibility(View.INVISIBLE);
            DeviceCameraFacePicListModel model = mList.get(position-1);
            holder.tvTimeListTop.setText(model.time);

        }  else{
            holder.clPicture.setTag(position);
            holder.clPicture.setBackgroundColor(mContext.getResources().
                    getColor(mClickPosition != -1 && mClickPosition == position ? R.color.c_fafafa : R.color.white));
            DeviceCameraFacePicListModel model = mList.get(position-1);
            String captureTime = model.pics.get(0).getCaptureTime();
            String strTime_hm = DateUtil.getStrTime_hm(captureTime);
            //
            holder.tvTimeItemCamera.setText(strTime_hm);
            CameraDetailAvatarAdapter avatarAdapter = new CameraDetailAvatarAdapter(mContext);
            GridLayoutManager manager = new GridLayoutManager(mContext, 4);
            holder.rvPicture.setLayoutManager(manager);
            holder.rvPicture.setAdapter(avatarAdapter);

            avatarAdapter.updateData(model.pics);


        }
//        setBottomVisible(holder, position);
//        DeviceCameraFacePic deviceCameraFacePic = mList.get(position);
//        String faceUrl = deviceCameraFacePic.getFaceUrl();
//        String deviceName = deviceCameraFacePic.getDeviceName();
//        String sceneUrl = deviceCameraFacePic.getSceneUrl();
//        String captureTime = deviceCameraFacePic.getCaptureTime();
//        String address = deviceCameraFacePic.getAddress();
//        String baseUrl = "https://scpub-eye.antelopecloud.cn";
////        holder.mainRcContentImvIcon
////        Glide.with(mContext)                             //配置上下文
////                .load(sceneUrl)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
////                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
////                .centerCrop().into(holder.mainRcContentImvIcon);
//        Glide.with(mContext)                             //配置上下文
//                .load(baseUrl + faceUrl)
////                    .thumbnail(0.01f)//设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
//                .error(R.drawable.deploy_pic_placeholder)           //设置错误图片
//                .placeholder(R.drawable.ic_default_image)    //设置占位图片
//                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
//                .into(holder.mainRcContentImvIcon);
//        try {
//            long time = Long.parseLong(captureTime);
//            holder.mainRcContentTvTime.setText(DateUtil.getStrTimeTodayByDevice(mContext, time));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        holder.mainRcContentTvLocation.setText(address);
//
//        //
//        setListener(holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 1;
        }
        DeviceCameraFacePicListModel model = mList.get(position - 1);
        if (model.pics == null) {
            return 2;
        }

        return 3;
    }

    private void setBottomVisible(MyViewHolder holder, int position) {
//        if (getItemCount() - 1 == position) {
//            holder.lineBottom.setVisibility(View.INVISIBLE);
//        } else {
//            holder.lineBottom.setVisibility(View.VISIBLE);
//        }
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

//    private void setContentStatus(final MyViewHolder holder, final int position, int status, String mergeType) {
//        String image = null;
//        MergeTypeStyles mergeTypeStyles = PreferencesHelper.getInstance().getConfigMergeType(mergeType);
//        if (mergeTypeStyles != null) {
//            image = mergeTypeStyles.getImage();
//        }
//        int color = 0;
//        final int colorResId = mContext.getResources().getColor(color);
//        if ("smoke".equalsIgnoreCase(mergeType) && status == SENSOR_STATUS_ALARM) {
//            holder.mainRcContentImvIcon.setImageResource(R.drawable.smoke_alarm_down);
//            holder.mainRcContentImvIcon.setColorFilter(colorResId);
//        } else {
//            Glide.with(mContext)                             //配置上下文
//                    .load(image)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
//                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                    .listener(new RequestListener<String, GlideDrawable>() {
//                        @Override
//                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                            holder.mainRcContentImvIcon.setImageDrawable(resource);
//                            holder.mainRcContentImvIcon.setColorFilter(colorResId);
//                            return true;
//                        }
//                    }).centerCrop().into(holder.mainRcContentImvIcon);
//        }
//
//    }
//
//    private void setContentTime(MyViewHolder holder, long updatedTime) {
//        if (updatedTime != 0) {
//            holder.mainRcContentTvTime.setText(DateUtil.getStrTimeTodayByDevice(mContext, updatedTime));
//        } else {
//            holder.mainRcContentTvTime.setText("-");
//        }
//    }
//
//    private void setContentName(MyViewHolder holder, String deviceInfoName, String sn) {
//        if (TextUtils.isEmpty(deviceInfoName)) {
//            holder.mainRcContentTvLocation.setText(sn);
//        } else {
//            holder.mainRcContentTvLocation.setText(deviceInfoName);
//        }
//    }

    @Override
    public int getItemCount() {
        return mList.size() == 0 ? 0 : mList.size()+1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @Nullable
        @BindView(R.id.imv_ring)
        ImageView imvRing;
        @Nullable
        @BindView(R.id.tv_live_stream)
        TextView tvLiveStream;
        @Nullable
        @BindView(R.id.cl_live_stream)
        ConstraintLayout clLiveStream;
        @Nullable
        @BindView(R.id.tv_time_item_camera)
        TextView tvTimeItemCamera;
        @Nullable
        @BindView(R.id.tv_time_list_top)
        TextView tvTimeListTop;
        @Nullable
        @BindView(R.id.view_item_camera)
        View viewItemCamera;
        @Nullable
        @BindView(R.id.rv_picture)
        RecyclerView rvPicture;
        @Nullable
        @BindView(R.id.view_below)
        View viewBelow;
        @Nullable
        @BindView(R.id.view_above)
        View viewAbove;
        @Nullable
        @BindView(R.id.cl_picture)
        ConstraintLayout clPicture;


        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sensoro.common.server.bean.AlarmCameraLiveBean;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.smartcity.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmCameraLiveDetailAdapter extends RecyclerView.Adapter<AlarmCameraLiveDetailAdapter.CameraLiveDetailViewHolder> {
    private final Context mContext;

    List<AlarmCameraLiveBean> mList = new ArrayList();
    private AlarmCameraLiveItemClickListener mListener;
    private Integer mClickPosition = 0;
    private final Drawable onLineDrawable;
    private final Drawable offLineDrawable;
    private final int dp4;
    private RoundedBitmapDrawable roundedBitmapDrawable;

    public AlarmCameraLiveDetailAdapter(Context context) {
        mContext = context;
        onLineDrawable = mContext.getResources().getDrawable(R.drawable.shape_oval_1dbb99_6dp);
        onLineDrawable.setBounds(0, 0, onLineDrawable.getMinimumWidth(), onLineDrawable.getMinimumHeight());

        offLineDrawable = mContext.getResources().getDrawable(R.drawable.shape_oval_b6b6_6dp);
        offLineDrawable.setBounds(0, 0, offLineDrawable.getMinimumWidth(), offLineDrawable.getMinimumHeight());

        dp4 = AppUtils.dp2px(context, 4);

        getRoundPlaceholder();

    }

    private void getRoundPlaceholder() {
        Bitmap src = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.camera_placeholder);
        roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), src);
        roundedBitmapDrawable.setCornerRadius(dp4);
        roundedBitmapDrawable.setAntiAlias(true);

    }

    @NonNull
    @Override
    public CameraLiveDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_alarm_camera_live_detail, parent, false);
        final CameraLiveDetailViewHolder holder = new CameraLiveDetailViewHolder(inflate);
        holder.clRootItemAdapterAlarmCameraLiveDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer position = (Integer) holder.clRootItemAdapterAlarmCameraLiveDetail.getTag();
                int temp = mClickPosition;
                mClickPosition = position;

                notifyItemChanged(temp);
                notifyItemChanged(mClickPosition);

                if (mListener != null) {
                    mListener.OnAlarmCameraLiveItemClick(position);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CameraLiveDetailViewHolder holder, int position) {
        holder.clRootItemAdapterAlarmCameraLiveDetail.setTag(position);
        if (mClickPosition == position) {
            holder.clRootItemAdapterAlarmCameraLiveDetail.setBackgroundColor(mContext.getResources().getColor(R.color.c_eeeeee));
            holder.ivLiveItemAdapterAlarmCameraLiveDetail.setVisibility(View.GONE);
            holder.tvWatchStateItemAdapterAlarmCameraLiveDetail.setVisibility(View.VISIBLE);
        } else {
            holder.clRootItemAdapterAlarmCameraLiveDetail.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            holder.ivLiveItemAdapterAlarmCameraLiveDetail.setVisibility(View.VISIBLE);
            holder.tvWatchStateItemAdapterAlarmCameraLiveDetail.setVisibility(View.GONE);
        }

        AlarmCameraLiveBean dataBean = mList.get(position);
        if (dataBean != null) {
            Glide.with(mContext)
                    .load(dataBean.getLastCover())
                    .apply(new RequestOptions().error(R.drawable.camera_placeholder)
//                    .bitmapTransform(new GlideRoundTransform(mContext,dp4))
                            .placeholder(R.drawable.camera_placeholder))
                    .into(holder.ivPicItemAdapterAlarmCameraLiveDetail);
            AlarmCameraLiveBean.CameraBean camera = dataBean.getCamera();
            if (camera != null) {
                String name = camera.getName();
                if (TextUtils.isEmpty(name)) {
                    name = camera.getSn();
                }
                holder.tvNameItemAdapterAlarmCameraLiveDetail.setText(name);

                AlarmCameraLiveBean.CameraBean.InfoBean info = camera.getInfo();
                if (info != null) {
                    String deviceStatus = info.getDeviceStatus();
                    setDeviceCameraStatus(holder, !TextUtils.isEmpty(deviceStatus) && "0".equals(deviceStatus));
                } else {
                    setDeviceCameraStatus(holder, true);
                }

            } else {
                setDeviceCameraStatus(holder, true);
            }


        }


    }

    private void setDeviceCameraStatus(CameraLiveDetailViewHolder holder, boolean isOffline) {
        if (isOffline) {
            holder.tvStatusItemAdapterAlarmCameraLiveDetail.setText(mContext.getString(R.string.deploy_camera_status_offline));
            holder.tvStatusItemAdapterAlarmCameraLiveDetail.setTextColor(mContext.getResources().getColor(R.color.c_a6a6a6));
            holder.tvStatusItemAdapterAlarmCameraLiveDetail.setCompoundDrawables(offLineDrawable, null, null, null);
        } else {
            holder.tvStatusItemAdapterAlarmCameraLiveDetail.setText(mContext.getString(R.string.deploy_camera_status_online));
            holder.tvStatusItemAdapterAlarmCameraLiveDetail.setTextColor(mContext.getResources().getColor(R.color.c_1dbb99));
            holder.tvStatusItemAdapterAlarmCameraLiveDetail.setCompoundDrawables(onLineDrawable, null, null, null);
        }
    }

    public void updateData(List<AlarmCameraLiveBean> data) {
        mList.clear();
        mList.addAll(data);
        mClickPosition = 0;
        notifyDataSetChanged();
    }

    public void setOnAlarmCameraLiveItemClickListener(AlarmCameraLiveItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface AlarmCameraLiveItemClickListener {
        void OnAlarmCameraLiveItemClick(int position);
    }

    class CameraLiveDetailViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_pic_item_adapter_alarm_camera_live_detail)
        ImageView ivPicItemAdapterAlarmCameraLiveDetail;
        @BindView(R.id.iv_live_item_adapter_alarm_camera_live_detail)
        ImageView ivLiveItemAdapterAlarmCameraLiveDetail;
        @BindView(R.id.tv_watch_state_item_adapter_alarm_camera_live_detail)
        TextView tvWatchStateItemAdapterAlarmCameraLiveDetail;
        @BindView(R.id.tv_name_item_adapter_alarm_camera_live_detail)
        TextView tvNameItemAdapterAlarmCameraLiveDetail;
        @BindView(R.id.tv_status_item_adapter_alarm_camera_live_detail)
        TextView tvStatusItemAdapterAlarmCameraLiveDetail;
        @BindView(R.id.cl_root_item_adapter_alarm_camera_live_detail)
        ConstraintLayout clRootItemAdapterAlarmCameraLiveDetail;

        public CameraLiveDetailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

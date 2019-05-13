package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.server.response.AlarmCameraLiveRsp;
import com.sensoro.smartcity.util.AppUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmCameraVideoDetailAdapter extends RecyclerView.Adapter<AlarmCameraVideoDetailAdapter.CameraLiveDetailViewHolder> {
    private final Context mContext;

    List<AlarmCameraLiveRsp.DataBean> mList = new ArrayList();
    private AlarmCameraVideoClickListener mListener;
    private Integer mClickPosition = 0;
    private final int dp4;
    private RoundedBitmapDrawable roundedBitmapDrawable;

    public AlarmCameraVideoDetailAdapter(Context context) {
        mContext = context;

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
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_alarm_camera_video_detail, parent, false);
        final CameraLiveDetailViewHolder holder = new CameraLiveDetailViewHolder(inflate);
        holder.clRootItemAdapterAlarmCameraVideoDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer position = (Integer) holder.clRootItemAdapterAlarmCameraVideoDetail.getTag();
                int temp = mClickPosition;
                mClickPosition = position;

                notifyItemChanged(temp);
                notifyItemChanged(mClickPosition);

                if (mListener != null) {
                    mListener.OnAlarmCameraVideoItemClick(position);
                }
            }
        });

        holder.ivDownloadItemAdapterAlarmCameraVideoDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer position = (Integer) holder.ivDownloadItemAdapterAlarmCameraVideoDetail.getTag();
                if (mListener !=  null) {
                    mListener.onAlarmCameraVideoDownloadClick();
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CameraLiveDetailViewHolder holder, int position) {
        holder.clRootItemAdapterAlarmCameraVideoDetail.setTag(position);
        holder.ivDownloadItemAdapterAlarmCameraVideoDetail.setTag(position);
        if (mClickPosition == position) {
            holder.clRootItemAdapterAlarmCameraVideoDetail.setBackgroundColor(mContext.getResources().getColor(R.color.c_eeeeee));
            holder.ivVideoItemAdapterAlarmCameraVideoDetail.setVisibility(View.GONE);
            holder.tvWatchStateItemAdapterAlarmCameraVideoDetail.setVisibility(View.VISIBLE);
        } else {
            holder.clRootItemAdapterAlarmCameraVideoDetail.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            holder.ivVideoItemAdapterAlarmCameraVideoDetail.setVisibility(View.VISIBLE);
            holder.tvWatchStateItemAdapterAlarmCameraVideoDetail.setVisibility(View.GONE);
        }

//        AlarmCameraLiveRsp.DataBean dataBean = mList.get(position);
//        if (dataBean != null) {
//            Glide.with(mContext)
//                    .load(dataBean.getLastCover())
//                    .error(R.drawable.camera_placeholder)
////                    .bitmapTransform(new GlideRoundTransform(mContext,dp4))
//                    .placeholder(R.drawable.camera_placeholder)
//                    .into(holder.ivPicItemAdapterAlarmCameraVideoDetail);
//            holder.tvNameItemAdapterAlarmCameraVideoDetail.setText(dataBean.getDeviceName());
//
//        }


    }

    public void updateData(List<AlarmCameraLiveRsp.DataBean> data) {
        mList.clear();
        mList.addAll(data);
        mClickPosition = 0;
        notifyDataSetChanged();
    }

    public void setOnAlarmCameraVideoItemClickListener(AlarmCameraVideoClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mList.size()+1;
    }

    public interface AlarmCameraVideoClickListener {
        void OnAlarmCameraVideoItemClick(int position);

        void onAlarmCameraVideoDownloadClick();
    }

    class CameraLiveDetailViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_pic_item_adapter_alarm_camera_video_detail)
        ImageView ivPicItemAdapterAlarmCameraVideoDetail;
        @BindView(R.id.iv_video_item_adapter_alarm_camera_video_detail)
        ImageView ivVideoItemAdapterAlarmCameraVideoDetail;
        @BindView(R.id.tv_watch_state_item_adapter_alarm_camera_video_detail)
        TextView tvWatchStateItemAdapterAlarmCameraVideoDetail;
        @BindView(R.id.cv_pic_item_adapter_alarm_camera_video_detail)
        CardView cvPicItemAdapterAlarmCameraVideoDetail;
        @BindView(R.id.tv_name_item_adapter_alarm_camera_video_detail)
        TextView tvNameItemAdapterAlarmCameraVideoDetail;
        @BindView(R.id.iv_download_item_adapter_alarm_camera_video_detail)
        ImageView ivDownloadItemAdapterAlarmCameraVideoDetail;
        @BindView(R.id.cl_root_item_adapter_alarm_camera_video_detail)
        ConstraintLayout clRootItemAdapterAlarmCameraVideoDetail;
        public CameraLiveDetailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

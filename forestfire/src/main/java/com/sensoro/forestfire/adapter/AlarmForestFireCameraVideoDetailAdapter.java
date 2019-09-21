package com.sensoro.forestfire.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sensoro.common.server.bean.AlarmCloudVideoBean;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.forestfire.R;
import com.sensoro.forestfire.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmForestFireCameraVideoDetailAdapter extends RecyclerView.Adapter<AlarmForestFireCameraVideoDetailAdapter.CameraLiveDetailViewHolder> {
    private final Context mContext;

    List<AlarmCloudVideoBean.MediasBean> mList = new ArrayList();
    private AlarmCameraVideoClickListener mListener;
    private Integer mClickPosition = 0;
    private final int dp4;
    private RoundedBitmapDrawable roundedBitmapDrawable;

    public AlarmForestFireCameraVideoDetailAdapter(Context context) {
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
                    mListener.OnAlarmCameraVideoItemClick(mList.get(position));
                }
            }
        });

        holder.ivDownloadItemAdapterAlarmCameraVideoDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer position = (Integer) holder.ivDownloadItemAdapterAlarmCameraVideoDetail.getTag();
                if (mListener != null) {
                    mListener.onAlarmCameraVideoDownloadClick(mList.get(position));
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

        AlarmCloudVideoBean.MediasBean dataBean = mList.get(position);
        if (dataBean != null) {
            Glide.with(mContext)
                    .load(dataBean.getCoverUrl())
                    .apply(new RequestOptions().error(R.drawable.camera_placeholder).placeholder(R.drawable.camera_placeholder))

//                    .bitmapTransform(new GlideRoundTransform(mContext,dp4))
                    .into(holder.ivPicItemAdapterAlarmCameraVideoDetail);
            holder.tvNameItemAdapterAlarmCameraVideoDetail.setText(dataBean.getLocation());

        }


    }

    public void updateData(List<AlarmCloudVideoBean.MediasBean> data) {
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
        return mList.size();
    }

    public interface AlarmCameraVideoClickListener {
        void OnAlarmCameraVideoItemClick(AlarmCloudVideoBean.MediasBean bean);

        void onAlarmCameraVideoDownloadClick(AlarmCloudVideoBean.MediasBean bean);
    }

    class CameraLiveDetailViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.iv_pic_item_adapter_alarm_camera_video_detail)
        ImageView ivPicItemAdapterAlarmCameraVideoDetail;
        @BindView(R2.id.iv_video_item_adapter_alarm_camera_video_detail)
        ImageView ivVideoItemAdapterAlarmCameraVideoDetail;
        @BindView(R2.id.tv_watch_state_item_adapter_alarm_camera_video_detail)
        TextView tvWatchStateItemAdapterAlarmCameraVideoDetail;
        @BindView(R2.id.cv_pic_item_adapter_alarm_camera_video_detail)
        CardView cvPicItemAdapterAlarmCameraVideoDetail;
        @BindView(R2.id.tv_name_item_adapter_alarm_camera_video_detail)
        TextView tvNameItemAdapterAlarmCameraVideoDetail;
        @BindView(R2.id.iv_download_item_adapter_alarm_camera_video_detail)
        ImageView ivDownloadItemAdapterAlarmCameraVideoDetail;
        @BindView(R2.id.cl_root_item_adapter_alarm_camera_video_detail)
        ConstraintLayout clRootItemAdapterAlarmCameraVideoDetail;

        public CameraLiveDetailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

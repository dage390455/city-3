package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.DeviceCameraFacePic;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.widget.GlideRoundTransform;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class CameraDetailListAdapter extends RecyclerView.Adapter<CameraDetailListAdapter.CameraDetailListViewHolder> {

    private final Context mContext;
    private final List<DeviceCameraFacePic> mList = new ArrayList<>();
    private CameraDetailListClickListener mListener;
    private int mClickPosition = -1;

    public CameraDetailListAdapter(Context context) {
        mContext = context;
    }


    @NonNull
    @Override
    public CameraDetailListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_camera_detail_list, parent, false);
        final CameraDetailListViewHolder holder = new CameraDetailListViewHolder(inflate);
        holder.clRootItemAdapterCameraDetailList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int temp = mClickPosition;
                mClickPosition = (int) holder.clRootItemAdapterCameraDetailList.getTag();
                notifyItemChanged(temp);
                notifyItemChanged(mClickPosition);
                if (mListener != null) {
                    mListener.onItemClick(mClickPosition);
                }
            }
        });

        holder.flAvatarItemAdapterCameraDetailList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onAvatarClick((Integer) holder.flAvatarItemAdapterCameraDetailList.getTag());
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CameraDetailListViewHolder holder, int position) {
        holder.clRootItemAdapterCameraDetailList.setTag(position);
        holder.flAvatarItemAdapterCameraDetailList.setTag(position);

        holder.viewAboveItemAdapterCameraDetailList.setVisibility(position <= 0 ? View.GONE : View.VISIBLE);
        holder.viewBelowItemAdapterCameraDetailList.setVisibility(position >= getItemCount()-1 ? View.GONE : View.VISIBLE);

        holder.clRootItemAdapterCameraDetailList.setBackgroundResource(mClickPosition == position ? R.drawable.shape_bg_solid_ee_full_corner_4 : R.drawable.shape_bg_solid_white_bottom_left_right_corner);

        DeviceCameraFacePic model = mList.get(position);
        Glide.with(mContext)                             //配置上下文
                .load(Constants.CAMERA_BASE_URL + model.getFaceUrl())
                .bitmapTransform(new GlideRoundTransform(mContext))
//                    .thumbnail(0.01f)//设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                .error(R.drawable.deploy_pic_placeholder)           //设置错误图片
                .placeholder(R.drawable.ic_default_cround_image)//设置占位图片
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                .into(holder.ivAvatarItemAdapterCameraDetailList);

        String strTime_hm;
        try {
            long l = Long.parseLong(model.getCaptureTime());
            strTime_hm = DateUtil.getStrTime_MM_dd_hms(l);
        } catch (Exception e) {
            e.printStackTrace();
            strTime_hm  = mContext.getString(R.string.unknown);
        }
        holder.tvTimeItemAdapterCameraDetailList.setText(strTime_hm);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateData(final List<DeviceCameraFacePic> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public List<DeviceCameraFacePic> getData() {
        return mList;
    }

    public void setOnCameraDetailListClickListener(CameraDetailListClickListener listener){
        mListener = listener;
    }

    public void clearClickPosition() {
        int temp = mClickPosition;
        mClickPosition = -1;
        notifyItemChanged(temp);
    }

    class CameraDetailListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_avatar_item_adapter_camera_detail_list)
        ImageView ivAvatarItemAdapterCameraDetailList;
        @BindView(R.id.fl_avatar_item_adapter_camera_detail_list)
        FrameLayout flAvatarItemAdapterCameraDetailList;
        @BindView(R.id.view_above_item_adapter_camera_detail_list)
        View viewAboveItemAdapterCameraDetailList;
        @BindView(R.id.view_below_item_adapter_camera_detail_list)
        View viewBelowItemAdapterCameraDetailList;
        @BindView(R.id.tv_time_item_adapter_camera_detail_list)
        TextView tvTimeItemAdapterCameraDetailList;
        @BindView(R.id.cl_root_item_adapter_camera_detail_list)
        ConstraintLayout clRootItemAdapterCameraDetailList;

        public CameraDetailListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface CameraDetailListClickListener {
        void onItemClick(int position);

        void onAvatarClick(int position);
    }
}

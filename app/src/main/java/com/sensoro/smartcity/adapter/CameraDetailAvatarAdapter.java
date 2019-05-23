package com.sensoro.smartcity.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.common.server.bean.DeviceCameraFacePic;
import com.sensoro.smartcity.widget.GlideRoundTransform;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

class CameraDetailAvatarAdapter extends RecyclerView.Adapter<CameraDetailAvatarAdapter.CameraDetailAvatarViewHolder> {
    private final Context mContext;
    private ArrayList<DeviceCameraFacePic> mList = new ArrayList<>();
    private OnAvatarClickListener mListener;

    public CameraDetailAvatarAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public CameraDetailAvatarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_camera_detail_avater, parent, false);
        CameraDetailAvatarViewHolder holder = new CameraDetailAvatarViewHolder(inflate);
        holder.cLAvatarItemAdapterCameraDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    int position = (int) v.getTag();
                    mListener.onAvatar(position);
                }
            }
        });
        return holder;
    }

    public void setOnAvatarClickListener(OnAvatarClickListener listener){
        mListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull final CameraDetailAvatarViewHolder holder, int position) {

        holder.cLAvatarItemAdapterCameraDetail.setTag(position);
        DeviceCameraFacePic pic = mList.get(position);

        Glide.with(mContext)                             //配置上下文
                .load(Constants.CAMERA_BASE_URL + pic.getFaceUrl())
                .bitmapTransform(new GlideRoundTransform(mContext))
//                    .thumbnail(0.01f)//设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                .error(R.drawable.deploy_pic_placeholder)           //设置错误图片
                .placeholder(R.drawable.ic_default_cround_image)//设置占位图片
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                .into(holder.imvAvatarItemAdapterCameraDetail);


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateData(ArrayList<DeviceCameraFacePic> list){
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    class CameraDetailAvatarViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imv_avatar_item_adapter_camera_detail)
        ImageView imvAvatarItemAdapterCameraDetail;
        @BindView(R.id.cl_avatar_item_adapter_camera_detail)
        ConstraintLayout cLAvatarItemAdapterCameraDetail;

        public CameraDetailAvatarViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    interface OnAvatarClickListener{
        void onAvatar(int position);
    }
}

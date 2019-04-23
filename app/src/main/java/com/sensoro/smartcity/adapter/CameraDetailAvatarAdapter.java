package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.server.bean.DeviceCameraFacePic;
import com.sensoro.smartcity.widget.GlideRoundTransform;
import com.yixia.camera.util.Log;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

class CameraDetailAvatarAdapter extends RecyclerView.Adapter<CameraDetailAvatarAdapter.CameraDetailAvatarViewHolder> {
    private final Context mContext;
    private ArrayList<DeviceCameraFacePic> mList = new ArrayList<>();

    public CameraDetailAvatarAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public CameraDetailAvatarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_camera_detail_avater, parent, false);
        return new CameraDetailAvatarViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull final CameraDetailAvatarViewHolder holder, int position) {
        DeviceCameraFacePic pic = mList.get(position);
        String baseUrl = "https://scpub-eye.antelopecloud.cn";
        Glide.with(mContext)                             //配置上下文
                .load(baseUrl + pic.getFaceUrl())
                .bitmapTransform(new GlideRoundTransform(mContext))
//                    .thumbnail(0.01f)//设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                .error(R.drawable.deploy_pic_placeholder)           //设置错误图片
                .placeholder(R.drawable.ic_default_image)//设置占位图片
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

        public CameraDetailAvatarViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

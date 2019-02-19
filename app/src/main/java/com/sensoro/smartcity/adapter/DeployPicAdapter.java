package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.model.DeployPicModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeployPicAdapter extends RecyclerView.Adapter<DeployPicAdapter.DeployPicHolder> {
    private final Context mContext;
    private List<DeployPicModel> list = new ArrayList<>();
    private DeployPicClickListener listener;

    public DeployPicAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public DeployPicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_deploy_pic, parent,false);
        return new DeployPicHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DeployPicHolder holder, int position) {
        DeployPicModel model = list.get(position);
        holder.itemAdapterDeployPicTvTitle.setText(TextUtils.isEmpty(model.title) ? mContext.getString(R.string.unknown) : model.title);
        holder.itemAdapterDeployPicTvContent.setText(TextUtils.isEmpty(model.content) ? mContext.getString(R.string.unknown) : model.title);

//        if (TextUtils.isEmpty(model.exampleUrl)) {
//            holder.itemAdapterDeployPicImvExample.setVisibility(View.INVISIBLE);
//        }else{
//            holder.itemAdapterDeployPicImvExample.setVisibility(View.VISIBLE);
//            Glide.with(mContext)                             //配置上下文
//                    .load(model.exampleUrl)
//                    .thumbnail(0.01f)//设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
////                    .error(R.drawable.ic_default_image)           //设置错误图片
////                    .placeholder(R.drawable.ic_default_image)    //设置占位图片
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
//                    .into(holder.itemAdapterDeployPicImvExample);
//        }

        if (model.photoItem == null) {
            holder.itemAdapterDeployPicRlTakePhoto.setBackgroundResource(R.drawable.shape_bg_solid_ff_stroke_df_corner_2dp);
            holder.itemAdapterDeployPicTvTakePhoto.setVisibility(View.VISIBLE);
            holder.itemAdapterDeployPicImvPhoto.setVisibility(View.INVISIBLE);
            holder.itemAdapterDeployPicImvDeletePhoto.setVisibility(View.GONE);
        }else{
            holder.itemAdapterDeployPicRlTakePhoto.setBackgroundResource(0);
            holder.itemAdapterDeployPicTvTakePhoto.setVisibility(View.GONE);
            holder.itemAdapterDeployPicImvPhoto.setVisibility(View.VISIBLE);
            holder.itemAdapterDeployPicImvDeletePhoto.setVisibility(View.VISIBLE);
            Glide.with(mContext)                             //配置上下文
                    .load(model.photoItem.path)
                    .thumbnail(0.01f)//设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .error(R.drawable.ic_default_image)           //设置错误图片
                    .placeholder(R.drawable.ic_default_image)    //设置占位图片
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                    .into(holder.itemAdapterDeployPicImvPhoto);
        }

        holder.itemAdapterDeployPicTvTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onTakePhotoClick(holder.getAdapterPosition());
                }

            }
        });

        holder.itemAdapterDeployPicImvDeletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDeletePhotoClick(holder.getAdapterPosition());
                }
            }
        });

        holder.itemAdapterDeployPicImvPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onPreviewPhoto(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateData(List<DeployPicModel> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    public void setDeployPicClickListener(DeployPicClickListener listener){
        this.listener = listener;
    }


    class DeployPicHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_adapter_deploy_pic_tv_title)
        TextView itemAdapterDeployPicTvTitle;
        @BindView(R.id.item_adapter_deploy_pic_tv_content)
        TextView itemAdapterDeployPicTvContent;
        @BindView(R.id.item_adapter_deploy_pic_imv_example)
        ImageView itemAdapterDeployPicImvExample;
        @BindView(R.id.item_adapter_deploy_pic_imv_photo)
        ImageView itemAdapterDeployPicImvPhoto;
        @BindView(R.id.item_adapter_deploy_pic_tv_take_photo)
        TextView itemAdapterDeployPicTvTakePhoto;
        @BindView(R.id.item_adapter_deploy_pic_cl_imv)
        ConstraintLayout itemAdapterDeployPicRlImv;
        @BindView(R.id.item_adapter_deploy_pic_rl_take_photo)
        RelativeLayout itemAdapterDeployPicRlTakePhoto;
        @BindView(R.id.item_adapter_deploy_pic_imv_delete_pic)
        ImageView itemAdapterDeployPicImvDeletePhoto;
        public DeployPicHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface DeployPicClickListener{
        void onTakePhotoClick(int position);

        void onDeletePhotoClick(int position);

        void onPreviewPhoto(int position);
    }
}

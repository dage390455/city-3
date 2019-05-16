package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sensoro.smartcity.R;
import com.sensoro.common.server.bean.ScenesData;

import java.util.ArrayList;
import java.util.List;

public class MonitorDeployDetailPhotoAdapter extends RecyclerView.Adapter<MonitorDeployDetailPhotoAdapter.AlarmDetailViewHolder> {
    private Context mContext;
    private final List<ScenesData> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private OnRecyclerViewItemClickListener listener;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    public void updateImages(List<ScenesData> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public List<ScenesData> getImages() {
        return mData;
    }

    public MonitorDeployDetailPhotoAdapter(Context mContext) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public AlarmDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AlarmDetailViewHolder(mInflater.inflate(R.layout.list_item_monitor_photo_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(AlarmDetailViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class AlarmDetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivPicAlarm;
        private ImageView iv_record;
        private int clickPosition;

        AlarmDetailViewHolder(View itemView) {
            super(itemView);
            ivPicAlarm = (ImageView) itemView.findViewById(R.id.iv_pic_alarm);
            iv_record = (ImageView) itemView.findViewById(R.id.iv_record);
        }

        public void bind(int position) {
            //设置条目的点击事件
            itemView.setOnClickListener(this);
            //根据条目位置设置图片
            ScenesData scenesData = mData.get(position);
            boolean isVideo = "video".equalsIgnoreCase(scenesData.type);
            if (isVideo) {
                Glide.with(mContext)                             //配置上下文
                        .load(scenesData.thumbUrl)
                        .thumbnail(0.01f)//设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                        .error(R.drawable.ic_default_image)           //设置错误图片
                        .placeholder(R.drawable.ic_default_image)    //设置占位图片
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                        .into(ivPicAlarm);
            } else {
                Glide.with(mContext)                             //配置上下文
                        .load(scenesData.url)
                        .thumbnail(0.01f)//设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                        .error(R.drawable.ic_default_image)           //设置错误图片
                        .placeholder(R.drawable.ic_default_image)    //设置占位图片
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                        .into(ivPicAlarm);
            }
            iv_record.setVisibility(isVideo ? View.VISIBLE : View.GONE);
//            ivPicAlarm.setImageResource(R.drawable.ic_default_image);

//            ImagePicker.getInstance().getImageLoader().displayImage((Activity) mContext, url, ivPicAlarm, 0, 0);
            clickPosition = position;
        }

        @Override
        public void onClick(View v) {
//            switch (v.getId()){
//                case R.id.iv_img:
            if (listener != null) listener.onItemClick(v, clickPosition);
//                    break;
//                case R.id.image_delete:
//
//                    break;
//            }

        }
    }

//    public interface OnPhotoDeleteListener {
//        void onDeletePhote(View view, int position);
//    }
}
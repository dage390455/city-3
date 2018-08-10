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

import java.util.ArrayList;
import java.util.List;

public class AlarmDetailPhotoAdapter extends RecyclerView.Adapter<AlarmDetailPhotoAdapter.AlarmDetailViewHolder> {
    private Context mContext;
    private final List<String> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private OnRecyclerViewItemClickListener listener;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    public void setImages(List<String> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public List<String> getImages() {
        return mData;
    }

    public AlarmDetailPhotoAdapter(Context mContext) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public AlarmDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AlarmDetailViewHolder(mInflater.inflate(R.layout.list_item_alarm_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(AlarmDetailViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class AlarmDetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivPicAlarm;
        private int clickPosition;

        public AlarmDetailViewHolder(View itemView) {
            super(itemView);
            ivPicAlarm = (ImageView) itemView.findViewById(R.id.iv_pic_alarm);
        }

        public void bind(int position) {
            //设置条目的点击事件
            itemView.setOnClickListener(this);
            //根据条目位置设置图片
            String url = mData.get(position);
//            ivPicAlarm.setImageResource(R.drawable.ic_default_image);
            Glide.with(mContext)                             //配置上下文
                    .load(url)
                    .thumbnail(0.01f)//设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .error(R.drawable.ic_default_image)           //设置错误图片
                    .placeholder(R.drawable.ic_default_image)    //设置占位图片
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                    .into(ivPicAlarm);
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
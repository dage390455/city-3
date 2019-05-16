package com.sensoro.smartcity.widget.imagepicker.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.common.model.ImageItem;

import java.util.List;

/**
 * ================================================
 * 作    者：ikkong （ikkong@163.com），修改 jeasonlzy（廖子尧）
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：
 * 修订历史：微信图片选择的Adapter, 感谢 ikkong 的提交
 * ================================================
 */
public class ImageBottomAdapter extends RecyclerView.Adapter<ImageBottomAdapter.SelectedPicViewHolder> {
    private Context mContext;
    private List<ImageItem> mData;
    private LayoutInflater mInflater;
    private OnRecyclerViewItemClickListener listener;
    private int currentPositon;
    private boolean isJustDisplay;

    public void setIsJustDisplay(boolean isJustDisplay) {
        this.isJustDisplay = isJustDisplay;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setSelect(int position) {
        currentPositon = position;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    public void setImages(List<ImageItem> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public List<ImageItem> getImages() {
        return mData;
    }

    public ImageBottomAdapter(Context mContext, List<ImageItem> data) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        setImages(data);
    }

    @Override
    public SelectedPicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SelectedPicViewHolder(mInflater.inflate(R.layout.item_bottom_image, parent, false));
    }

    @Override
    public void onBindViewHolder(SelectedPicViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class SelectedPicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView iv_img;
        private ImageView iv_img_bg;
        private int clickPosition;

        public SelectedPicViewHolder(View itemView) {
            super(itemView);
            iv_img = (ImageView) itemView.findViewById(R.id.iv_img);
            iv_img_bg = (ImageView) itemView.findViewById(R.id.iv_img_bg);
        }

        public void bind(int position) {
            //设置条目的点击事件
            itemView.setOnClickListener(this);

//            image_delete.setOnClickListener(this);
            //根据条目位置设置图片
            ImageItem item = mData.get(position);
//            image_delete.setVisibility(View.VISIBLE);
            if(isJustDisplay){
                ImagePicker.getInstance().getImageLoader().displayImagePreview((Activity) mContext,item.path,iv_img,0,0);
            }else{
                if (item.isRecord) {
                    ImagePicker.getInstance().getImageLoader().displayImage((Activity) mContext, item.thumbPath, iv_img, 0, 0);
                } else {
                    ImagePicker.getInstance().getImageLoader().displayImage((Activity) mContext, item.path, iv_img, 0, 0);
                }
            }

            clickPosition = position;
            iv_img_bg.setVisibility(currentPositon == position ? View.VISIBLE : View.GONE);
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
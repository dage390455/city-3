package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.widget.popup.SensoroPopupAlarmView;

import java.util.ArrayList;
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
public class ImagePickerAdapter extends RecyclerView.Adapter<ImagePickerAdapter.SelectedPicViewHolder> {
    private int maxImgCount;
    private Context mContext;
    private List<ImageItem> mData;
    private LayoutInflater mInflater;
    private OnRecyclerViewItemClickListener listener;
    private boolean isAdded;   //是否额外添加了最后一个图片

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    public void setImages(List<ImageItem> data) {
        mData = new ArrayList<>(data);
        if (getItemCount() < maxImgCount) {
            mData.add(new ImageItem());
            isAdded = true;
        } else {
            isAdded = false;
        }
        notifyDataSetChanged();
    }

//    public void removeImage(int position) {
////        mData = new ArrayList<>(data);
//        ImageItem imageItem = mData.get(position);
//        Iterator<ImageItem> iterator = mData.iterator();
//        while (iterator.hasNext()) {
//            ImageItem next = iterator.next();
//            if (next.equals(imageItem)) {
//                iterator.remove();
//                break;
//            }
//        }
//        if (getItemCount() < maxImgCount) {
//            if (isAdded){
//                notifyItemRemoved(position-1);
//            }else {
//                mData.add(new ImageItem());
//                isAdded = true;
//                notifyItemRemoved(position-1);
//            }
//        }else {
//            notifyItemRemoved(position);
//        }
//
////        if (isAdded) {
////            if (getItemCount() < maxImgCount) {
////                mData.add(new ImageItem());
////                isAdded = true;
////                notifyItemRemoved(position - 1);
//////            notifyItemRemoved(position - 1);
////            } else {
////                isAdded = false;
////                notifyItemRemoved(position);
//////            notifyItemRemoved(position);
////            }
////        } else {
////            notifyItemRemoved(position);
////        }
//
//
//    }

    public List<ImageItem> getImages() {
        //由于图片未选满时，最后一张显示添加图片，因此这个方法返回真正的已选图片
        if (isAdded) return new ArrayList<>(mData.subList(0, mData.size() - 1));
        else return mData;
    }

    public ImagePickerAdapter(Context mContext, List<ImageItem> data, int maxImgCount) {
        this.mContext = mContext;
        this.maxImgCount = maxImgCount;
        this.mInflater = LayoutInflater.from(mContext);
        setImages(data);
    }

    @Override
    public SelectedPicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SelectedPicViewHolder(mInflater.inflate(R.layout.list_item_image, parent, false));
    }

    @Override
    public void onBindViewHolder(SelectedPicViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class SelectedPicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView iv_img;
        private ImageView image_delete;
        private LinearLayout ll_add;
        private int clickPosition;

        SelectedPicViewHolder(View itemView) {
            super(itemView);
            iv_img = (ImageView) itemView.findViewById(R.id.iv_img);
            image_delete = (ImageView) itemView.findViewById(R.id.image_delete);
            ll_add = (LinearLayout) itemView.findViewById(R.id.ll_add);
        }

        public void bind(int position) {
            //设置条目的点击事件
            itemView.setOnClickListener(this);
            image_delete.setOnClickListener(this);
            //根据条目位置设置图片
            ImageItem item = mData.get(position);
            if (isAdded && position == getItemCount() - 1) {
                ll_add.setVisibility(View.VISIBLE);
                iv_img.setVisibility(View.GONE);
//                iv_img.setImageResource(R.drawable.selector_image_add);
                clickPosition = SensoroPopupAlarmView.IMAGE_ITEM_ADD;
                image_delete.setVisibility(View.GONE);
            } else {
                iv_img.setVisibility(View.VISIBLE);
                ll_add.setVisibility(View.GONE);
                image_delete.setVisibility(View.VISIBLE);
                ImagePicker.getInstance().getImageLoader().displayImage((Activity) mContext, item.path, iv_img, 0, 0);
                clickPosition = position;
            }
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
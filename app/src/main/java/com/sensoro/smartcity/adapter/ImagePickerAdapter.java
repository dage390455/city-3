package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.widget.imagepicker.bean.ImageItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.sensoro.smartcity.constant.Constants.IMAGE_ITEM_ADD;

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
    private int maxImgCount = 9;
    private Context mContext;
    private List<ImageItem> mData;
    private LayoutInflater mInflater;
    private OnRecyclerViewItemClickListener listener;
    private boolean isAdded;   //是否额外添加了最后一个图片
    private String tipText;
    private boolean isJustDisplay = false;

    public void setJustDisplay(boolean isJustDisplay) {
        this.isJustDisplay = isJustDisplay;
    }
//    private boolean canVideo;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    public void setImages(List<ImageItem> data) {
        mData = new ArrayList<>(data);
        if(!isJustDisplay){
            if (getItemCount() < maxImgCount) {
                mData.add(new ImageItem());
                isAdded = true;
            } else {
                isAdded = false;
            }
        }


        notifyDataSetChanged();
    }

//    public void canVideo(boolean canVideo) {
//        this.canVideo = canVideo;
//    }
//    public void removeImage(int position) {
////        mData = new ArrayList<>(data);
//        ImageItem imageItem = mData.getInstance(position);
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
        if(isJustDisplay){
            return mData;
        }else if (isAdded) {
            return new ArrayList<>(mData.subList(0, mData.size() - 1));
        } else{
            return mData;
        }
    }

    public ImagePickerAdapter(Context mContext, List<ImageItem> data) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        setImages(data);
    }

    public void setMaxImgCount(int maxImgCount) {
        this.maxImgCount = maxImgCount;
    }

    @Override
    public SelectedPicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SelectedPicViewHolder(mInflater.inflate(R.layout.list_item_image, parent, false));
    }

    @Override
    public void onBindViewHolder(SelectedPicViewHolder holder, int position) {
        if (isJustDisplay) {
            holder.itemView.setOnClickListener(holder);
            holder.image_delete.setVisibility(View.GONE);
            ImageItem item = mData.get(position);
            if (item != null) {
                holder.iv_record_play.setVisibility(item.isRecord ? View.VISIBLE : View.GONE);
            }
            Glide.with((Activity) mContext)                             //配置上下文
                    .load(item.path)
                    .error(R.drawable.ic_default_image)           //设置错误图片
                    .placeholder(R.drawable.ic_default_image)//设置占位图片
                    .thumbnail(0.01f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                    .into(holder.iv_img);

        }else{
            holder.bind(position);
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setAddTipText(String tipText) {
        this.tipText = tipText;
    }

    class SelectedPicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView iv_record_play;
        private ImageView iv_img;
        private ImageView image_delete;
        private LinearLayout ll_add;
        private TextView tv_add_content;
        private int clickPosition;

        SelectedPicViewHolder(View itemView) {
            super(itemView);
            iv_img = (ImageView) itemView.findViewById(R.id.iv_img);
            iv_record_play = (ImageView) itemView.findViewById(R.id.iv_record_play);
            image_delete = (ImageView) itemView.findViewById(R.id.image_delete);
            ll_add = (LinearLayout) itemView.findViewById(R.id.ll_add);
            tv_add_content = itemView.findViewById(R.id.tv_add_content);
        }

        public void bind(int position) {
//            if (canVideo) {
//                if (isAdded) {
//                    if (getItemCount() > 1) {
//                        tv_add_content.setText("照片");
//                    } else {
//                        tv_add_content.setText("照片/视频");
//                    }
//                }
//            }
            if (!TextUtils.isEmpty(tipText)) {
                tv_add_content.setText(tipText);
            }
            //设置条目的点击事件
            itemView.setOnClickListener(this);
            image_delete.setOnClickListener(this);
            //根据条目位置设置图片
            ImageItem item = mData.get(position);
            if (isAdded && position == getItemCount() - 1) {
                ll_add.setVisibility(View.VISIBLE);
                iv_img.setVisibility(View.GONE);
//                iv_img.setImageResource(R.drawable.selector_image_add);
                clickPosition = IMAGE_ITEM_ADD;
                image_delete.setVisibility(View.GONE);
                iv_record_play.setVisibility(View.GONE);
            } else {
                iv_img.setVisibility(View.VISIBLE);
                ll_add.setVisibility(View.GONE);
                image_delete.setVisibility(View.VISIBLE);
                if (mData != null) {
                    ImageItem imageItem = mData.get(position);
                    if (imageItem != null) {
                        iv_record_play.setVisibility(imageItem.isRecord ? View.VISIBLE : View.GONE);
                    }
                }
                //替换压缩0.01
                if (item.isRecord) {
                    Glide.with((Activity) mContext)                             //配置上下文
                            .load(Uri.fromFile(new File(item.thumbPath)))    //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                            .error(R.drawable.ic_default_image)           //设置错误图片
                            .placeholder(R.drawable.ic_default_image)//设置占位图片
                            .thumbnail(0.01f)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                            .into(iv_img);
//                ImagePicker.getInstance().getImageLoader().displayImage(, , iv_img, 0, 0);
                } else {
                    Glide.with((Activity) mContext)                             //配置上下文
                            .load(Uri.fromFile(new File(item.path)))    //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                            .error(R.drawable.ic_default_image)           //设置错误图片
                            .placeholder(R.drawable.ic_default_image)//设置占位图片
                            .thumbnail(0.01f)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                            .into(iv_img);
//                ImagePicker.getInstance().getImageLoader().displayImage(, , iv_img, 0, 0);
                }
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
package com.sensoro.smartcity.widget.imagepicker.adapter;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.widget.toast.SensoroToast;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.smartcity.widget.imagepicker.bean.ImageItem;
import com.sensoro.smartcity.widget.imagepicker.ui.ImageBaseActivity;
import com.sensoro.smartcity.widget.imagepicker.ui.ImageGridActivity;
import com.sensoro.smartcity.widget.imagepicker.util.Utils;
import com.sensoro.smartcity.widget.imagepicker.view.SuperCheckBox;

import java.util.ArrayList;
import java.util.List;

/**
 * 加载相册图片的RecyclerView适配器
 */

public class ImageRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {


    private static final int ITEM_TYPE_CAMERA = 0;  //第一个条目是相机
    private static final int ITEM_TYPE_NORMAL = 1;  //第一个条目不是相机
    private ImagePicker imagePicker;
    private Activity mActivity;
    private ArrayList<ImageItem> images;       //当前需要显示的所有的图片数据
    private ArrayList<ImageItem> mSelectedImages; //全局保存的已经选中的图片数据
    private boolean isShowCamera;         //是否显示拍照按钮
    private int mImageSize;               //每个条目的大小
    private LayoutInflater mInflater;
    private OnImageItemClickListener listener;   //图片被点击的监听

    public void setOnImageItemClickListener(OnImageItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnImageItemClickListener {
        void onImageItemClick(View view, ImageItem imageItem, int position);
    }

    public void refreshData(ArrayList<ImageItem> images) {
        if (images == null || images.size() == 0) this.images = new ArrayList<>();
        else this.images = images;
        notifyDataSetChanged();
    }

    /**
     * 构造方法
     */
    public ImageRecyclerAdapter(Activity activity, ArrayList<ImageItem> images) {
        this.mActivity = activity;
        if (images == null || images.size() == 0) this.images = new ArrayList<>();
        else this.images = images;

        mImageSize = Utils.getImageItemWidth(mActivity);
        imagePicker = ImagePicker.getInstance();
        isShowCamera = imagePicker.isShowCamera();
        mSelectedImages = imagePicker.getSelectedImages();
        mInflater = LayoutInflater.from(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CAMERA) {
            return new CameraViewHolder(mInflater.inflate(R.layout.adapter_camera_item, parent, false));
        }
        return new ImageViewHolder(mInflater.inflate(R.layout.adapter_image_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof CameraViewHolder) {
            ((CameraViewHolder) holder).bindCamera();
        } else if (holder instanceof ImageViewHolder) {
            ((ImageViewHolder) holder).bind(position, true);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        //修复局部刷新闪烁问题
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            if (holder instanceof CameraViewHolder) {
                ((CameraViewHolder) holder).bindCamera();
            } else if (holder instanceof ImageViewHolder) {
                ((ImageViewHolder) holder).bind(position, false);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowCamera) return position == 0 ? ITEM_TYPE_CAMERA : ITEM_TYPE_NORMAL;
        return ITEM_TYPE_NORMAL;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return isShowCamera ? images.size() + 1 : images.size();
    }

    public ImageItem getItem(int position) {
        if (isShowCamera) {
            if (position == 0) return null;
            return images.get(position - 1);
        } else {
            return images.get(position);
        }
    }

    class ImageViewHolder extends ViewHolder {

        final View rootView;
        final ImageView ivThumb;
        final View mask;
        final View checkView;
        final SuperCheckBox cbCheck;
        //替换图标方案
        final ImageView circleSelect;

        ImageViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            ivThumb = (ImageView) itemView.findViewById(R.id.iv_thumb);
            mask = itemView.findViewById(R.id.mask);
            mask.setVisibility(View.GONE);
            checkView = itemView.findViewById(R.id.checkView);
            cbCheck = (SuperCheckBox) itemView.findViewById(R.id.cb_check);
            circleSelect = itemView.findViewById(R.id.circle_select);
//            circleSelect.setColorFilter(mActivity.getResources().getColor(R.color.ip_color_accent));
            itemView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize));
            //让图片是个正方形
        }

        void bind(final int position, boolean needReLoad) {
            final ImageItem imageItem = getItem(position);
            ivThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //修改逻辑
                    cbCheck.setChecked(!cbCheck.isChecked());
                    int selectLimit = imagePicker.getSelectLimit();
                    if (cbCheck.isChecked() && mSelectedImages.size() >= selectLimit) {
                        SensoroToast.getInstance().makeText(mActivity.getString(R.string
                                .ip_select_limit, selectLimit), Toast.LENGTH_SHORT).show();
                        cbCheck.setChecked(false);
//                        mask.setVisibility(View.GONE);
                        circleSelect.setVisibility(View.GONE);
                    } else {
                        imagePicker.addSelectedImageItem(position, imageItem, cbCheck.isChecked());
//                        mask.setVisibility(View.VISIBLE);
                        circleSelect.setVisibility(View.VISIBLE);
                    }
                    //
                    if (listener != null) listener.onImageItemClick(rootView, imageItem, position);
                }
            });
            checkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cbCheck.setChecked(!cbCheck.isChecked());
                    int selectLimit = imagePicker.getSelectLimit();
                    if (cbCheck.isChecked() && mSelectedImages.size() >= selectLimit) {
                        SensoroToast.getInstance().makeText(mActivity.getApplicationContext(), mActivity.getString(R.string
                                .ip_select_limit, selectLimit), Toast.LENGTH_SHORT).show();
                        cbCheck.setChecked(false);
//                        mask.setVisibility(View.GONE);
                        circleSelect.setVisibility(View.GONE);
                    } else {
                        imagePicker.addSelectedImageItem(position, imageItem, cbCheck.isChecked());
//                        mask.setVisibility(View.VISIBLE);
                        circleSelect.setVisibility(View.VISIBLE);
                    }
                }
            });
            //根据是否多选，显示或隐藏checkbox
            if (imagePicker.isMultiMode()) {
                cbCheck.setVisibility(View.VISIBLE);
                boolean checked = mSelectedImages.contains(imageItem);
                if (checked) {
//                    mask.setVisibility(View.VISIBLE);
                    cbCheck.setChecked(true);
                } else {
//                    mask.setVisibility(View.GONE);
                    cbCheck.setChecked(false);
                }
                circleSelect.setVisibility(cbCheck.isChecked() ? View.VISIBLE : View.GONE);
            } else {
                cbCheck.setVisibility(View.GONE);
            }
            //显示图片
            if (needReLoad) {
                if (imageItem.isRecord) {
                    imagePicker.getImageLoader().displayImage(mActivity, imageItem.thumbPath, ivThumb, mImageSize, mImageSize);
                } else {
                    imagePicker.getImageLoader().displayImage(mActivity, imageItem.path, ivThumb, mImageSize, mImageSize);
                }

            }
        }

    }

    class CameraViewHolder extends ViewHolder {

        final View mItemView;

        CameraViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
        }

        void bindCamera() {
            mItemView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize));
            //让图片是个正方形
            mItemView.setTag(null);
            mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!((ImageBaseActivity) mActivity).checkPermission(Manifest.permission.CAMERA)) {
                        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA},
                                ImageGridActivity.REQUEST_PERMISSION_CAMERA);
                    } else {
                        imagePicker.takePicture(mActivity, ImagePicker.REQUEST_CODE_TAKE);
                    }
                }
            });
        }
    }
}

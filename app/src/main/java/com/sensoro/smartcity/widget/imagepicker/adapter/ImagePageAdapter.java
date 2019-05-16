package com.sensoro.smartcity.widget.imagepicker.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.sensoro.smartcity.R;
import com.sensoro.common.utils.DpUtils;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.smartcity.widget.imagepicker.bean.ImageItem;
import com.sensoro.smartcity.widget.imagepicker.util.Utils;

import java.io.File;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ImagePageAdapter extends PagerAdapter {

    private int screenWidth;
    private int screenHeight;
    private ImagePicker imagePicker;
    private ArrayList<ImageItem> images = new ArrayList<>();
    private Activity mActivity;
    public PhotoViewClickListener listener;
    private VideoViewClickListener videoViewClicklistener;
    private boolean isJustDisplay = false;

    public ImagePageAdapter(Activity activity, ArrayList<ImageItem> images) {
        this.mActivity = activity;
        this.images = images;

        DisplayMetrics dm = Utils.getScreenPix(activity);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        imagePicker = ImagePicker.getInstance();
    }

    public void setData(ArrayList<ImageItem> images) {
        this.images = images;
    }

    public void setPhotoViewClickListener(PhotoViewClickListener listener) {
        this.listener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageItem imageItem = images.get(position);
        FrameLayout frameLayout = new FrameLayout(mActivity);
        PhotoView photoView = new PhotoView(mActivity);
        photoView.setSaveEnabled(true);
        frameLayout.addView(photoView);
        //
        if (imageItem.isRecord) {
            ImageView imageView = new ImageView(mActivity);
            imageView.setImageResource(R.mipmap.item_icon_play);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(DpUtils.dp2px(mActivity, 50), DpUtils.dp2px(mActivity, 50));
            layoutParams.gravity = Gravity.CENTER;
            imageView.setLayoutParams(layoutParams);
            frameLayout.addView(imageView);
            photoView.setScale(1);
            photoView.setSaveEnabled(false);
            photoView.setZoomable(false);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (videoViewClicklistener != null) {
                        videoViewClicklistener.onVideoClickListener(v, position);
                    }
                }
            };
            imageView.setOnClickListener(onClickListener);
            photoView.setOnClickListener(onClickListener);
        } else {
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    if (listener != null) listener.OnPhotoTapListener(view, x, y);
                }
            });
        }
        //TODO 区分
        if (imageItem.fromUrl) {
            if (imageItem.isRecord) {
                imagePicker.getImageLoader().displayImagePreview(mActivity, imageItem.thumbPath, photoView, screenWidth,
                        screenHeight);
            } else {
                imagePicker.getImageLoader().displayImagePreview(mActivity, imageItem.path, photoView, screenWidth,
                        screenHeight);
            }
        } else {
            if (imageItem.isRecord) {
                imagePicker.getImageLoader().displayImagePreview(mActivity, new File(imageItem.thumbPath), photoView,
                        screenWidth, screenHeight);
            } else {
                imagePicker.getImageLoader().displayImagePreview(mActivity, new File(imageItem.path), photoView,
                        screenWidth, screenHeight);
            }
        }

        container.addView(frameLayout);
        return frameLayout;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setIsJustDisplay(boolean isJustDisplay) {
        this.isJustDisplay = isJustDisplay;
    }

    public void setVideoViewClickListener(VideoViewClickListener videoViewClicklistener) {
        this.videoViewClicklistener = videoViewClicklistener;
    }

    public interface PhotoViewClickListener {
        void OnPhotoTapListener(View view, float v, float v1);
    }

    public interface VideoViewClickListener {
        void onVideoClickListener(View view, int position);
    }
}

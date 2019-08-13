package com.sensoro.smartcity.widget.imagepicker.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sensoro.common.model.ImageItem;
import com.sensoro.imagepicker.R;
import com.sensoro.smartcity.widget.imagepicker.DataHolder;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.smartcity.widget.imagepicker.adapter.ImagePageAdapter;
import com.sensoro.smartcity.widget.imagepicker.util.Utils;
import com.sensoro.smartcity.widget.imagepicker.view.ViewPagerFixed;

import java.util.ArrayList;

//import com.sensoro.smartcity.activity.VideoPlayActivity;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：
 * 修订历史：图片预览的基类
 * ================================================
 */
public abstract class ImagePreviewBaseActivity extends ImageBaseActivity {

    protected ImagePicker imagePicker;
    protected ArrayList<ImageItem> mImageItems;      //跳转进ImagePreviewFragment的图片文件夹
    protected int mCurrentPosition = 0;              //跳转进ImagePreviewFragment时的序号，第几个图片
    protected TextView mTitleCount;                  //显示当前图片的位置  例如  5/31
    protected ArrayList<ImageItem> selectedImages;   //所有已经选中的图片
    protected View content;
    protected View topBar;
    protected ViewPagerFixed mViewPager;
    protected ImagePageAdapter mAdapter;
    protected boolean isFromItems = false;
    public boolean isJustDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        mCurrentPosition = getIntent().getIntExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, 0);
        isFromItems = getIntent().getBooleanExtra(ImagePicker.EXTRA_FROM_ITEMS, false);
        //该extra 在city的constants里面，无法直接获取，所以直接写了
        isJustDisplay = getIntent().getBooleanExtra("extra_just_display_pic", false);


        if (isFromItems) {
            // 据说这样会导致大量图片崩溃
            mImageItems = (ArrayList<ImageItem>) getIntent().getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
        } else {
            // 下面采用弱引用会导致预览崩溃
            mImageItems = (ArrayList<ImageItem>) DataHolder.getInstance().retrieve(DataHolder.DH_CURRENT_IMAGE_FOLDER_ITEMS);
        }

        imagePicker = ImagePicker.getInstance();
        selectedImages = imagePicker.getSelectedImages();

        //初始化控件
        content = findViewById(R.id.content);

        //因为状态栏透明后，布局整体会上移，所以给头部加上状态栏的margin值，保证头部不会被覆盖
        topBar = findViewById(R.id.top_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) topBar.getLayoutParams();
            params.topMargin = Utils.getStatusHeight(this);
            topBar.setLayoutParams(params);
        }
        topBar.findViewById(R.id.btn_ok).setVisibility(View.GONE);
        topBar.findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTitleCount = (TextView) findViewById(R.id.tv_des);


        mViewPager = (ViewPagerFixed) findViewById(R.id.viewpager);
        mAdapter = new ImagePageAdapter(this, mImageItems);
        mAdapter.setIsJustDisplay(isJustDisplay);
        mAdapter.setPhotoViewClickListener(new ImagePageAdapter.PhotoViewClickListener() {
            @Override
            public void OnPhotoTapListener(View view, float v, float v1) {
                onImageSingleTap();
            }
        });
        mAdapter.setVideoViewClickListener(new ImagePageAdapter.VideoViewClickListener() {
            @Override
            public void onVideoClickListener(View view, int position) {
                try {
//                    ImageItem imageItem = mImageItems.get(position);
//                    Intent intent = new Intent();
//                    intent.setClass(ImagePreviewBaseActivity.this, VideoPlayActivity.class);
//                    intent.putExtra("path_record", (Serializable) imageItem);
//                    intent.putExtra("video_del", true);
//                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentPosition, false);

        //初始化当前页面的状态
        mTitleCount.setText(getString(R.string.ip_preview_image_count, mCurrentPosition + 1, mImageItems.size()));
    }

    /**
     * 单击时，隐藏头和尾
     */
    public abstract void onImageSingleTap();
}
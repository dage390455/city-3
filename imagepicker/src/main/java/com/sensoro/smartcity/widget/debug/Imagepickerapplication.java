package com.sensoro.smartcity.widget.debug;

import com.sensoro.common.base.BaseApplication;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.smartcity.widget.imagepicker.view.CropImageView;
import com.sensoro.smartcity.widget.popup.GlideImageLoader;

public  class Imagepickerapplication extends BaseApplication{

    public static Imagepickerapplication sInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        initImagePicker();
    }

    @Override
    protected void onMyApplicationResumed() {

    }

    @Override
    protected void onMyApplicationPaused() {

    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(false);
        //显示拍照按钮
        //TODO 去掉裁剪
        imagePicker.setCrop(false);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(9);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

}

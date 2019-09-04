package com.sensoro.common.imagepicker;

import android.app.Activity;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sensoro.common.R;
import com.sensoro.common.imagepicker.loader.ImageLoader;

import java.io.File;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class GlideImageLoader implements ImageLoader {

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {

        Glide.with(activity)                             //配置上下文
                .load(Uri.fromFile(new File(path)))
                .apply(new RequestOptions().error(R.drawable.ic_default_image).placeholder(R.drawable.ic_default_image).diskCacheStrategy(DiskCacheStrategy.ALL))//设置图片路径(fix #8,文件名包含%符号 无法识别和显示)

                //设置错误图片
                //设置占位图片
                //缓存全尺寸
                .into(imageView);
    }

    @Override
    public void displayImagePreview(Activity activity, Object path, ImageView imageView, int width, int height) {
        if (path instanceof File) {
            File file = (File) path;
            Glide.with(activity)                             //配置上下文
                    .load(Uri.fromFile(file))
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))//设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    //缓存全尺寸
                    .into(imageView);
        } else if (path instanceof String) {
            Glide.with(activity)                             //配置上下文
                    .load(path)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))//设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    //缓存全尺寸
                    .into(imageView);
        }

    }

    @Override
    public void clearMemoryCache() {

    }
}

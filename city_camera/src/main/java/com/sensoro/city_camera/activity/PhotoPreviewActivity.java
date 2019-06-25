package com.sensoro.city_camera.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ortiz.touchview.TouchImageView;
import com.sensoro.city_camera.R;
import com.sensoro.city_camera.widget.ExtendedViewPager;
import com.sensoro.common.manger.ThreadPoolManager;
import com.sensoro.common.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhotoPreviewActivity extends AppCompatActivity {

    private List<String> urlList = new ArrayList<>();
    public static final String EXTRA_KEY_POSITION = "extra_key_position";
    public static final String EXTRA_KEY_URLS = "extra_key_urls";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.c_383838));

        findViewById(R.id.back_iv).setOnClickListener(v -> finish());

        Intent intent = getIntent();
        urlList.addAll(intent.getStringArrayListExtra(EXTRA_KEY_URLS));

        TouchImageAdapter adapter = new TouchImageAdapter();
        final ExtendedViewPager viewPager = findViewById(R.id.image_viewpager);
        viewPager.setAdapter(adapter);

        int position = intent.getIntExtra(EXTRA_KEY_POSITION, 0);
        viewPager.setCurrentItem(position);


        findViewById(R.id.download_iv).setOnClickListener(v -> {
            String url = urlList.get(viewPager.getCurrentItem());
            if(!TextUtils.isEmpty(url)){
             downloadImage(url);
            }
        });
    }

    private void downloadImage(String url){
        Glide.with(this)
                .load(url)
                .asBitmap()
                .toBytes()
                .into(new SimpleTarget<byte[]>() {
                    @Override
                    public void onResourceReady(byte[] bytes, GlideAnimation<? super byte[]> glideAnimation) {
                        // 下载成功回调函数
                        // 数据处理方法，保存bytes到文件 FileUtil.copy(file, bytes);
                        String[] split = url.split("/");
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath(), split[split.length-1]);
                        ThreadPoolManager.getInstance().execute(() -> {
                            FileUtil.copy(file.getAbsolutePath(), bytes);
                            runOnUiThread(() -> Toast.makeText(PhotoPreviewActivity.this, getString(R.string.toast_image_download_success), Toast.LENGTH_SHORT).show());
                        });
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        // 下载失败回调
                        Toast.makeText(PhotoPreviewActivity.this, getString(R.string.toast_image_download_failed), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    class TouchImageAdapter extends PagerAdapter {


        @Override
        public int getCount() {
            return urlList.size();
        }

        @NonNull
        @Override
        public View instantiateItem(@NonNull ViewGroup container, int position) {
            TouchImageView img = new TouchImageView(container.getContext());
            Glide.with(container.getContext()).load(urlList.get(position)).into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    img.setImageDrawable(resource);
                }
            });
            container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            return img;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

    }
}

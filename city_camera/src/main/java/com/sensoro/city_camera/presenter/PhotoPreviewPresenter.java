package com.sensoro.city_camera.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.sensoro.city_camera.IMainViews.IPhotoPreviewView;
import com.sensoro.city_camera.R;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.manger.ThreadPoolManager;
import com.sensoro.common.server.security.bean.SecurityAlarmDetailInfo;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.common.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author : bin.tian
 * date   : 2019-06-28
 */
public class PhotoPreviewPresenter extends BasePresenter<IPhotoPreviewView> {
    public static final String EXTRA_KEY_POSITION = "extra_key_position";
    public static final String EXTRA_KEY_SECURITY_INFO = "extra_key_security_info";
    private Activity mActivity;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        Intent intent = mActivity.getIntent();
        SecurityAlarmDetailInfo securityAlarmDetailInfo = (SecurityAlarmDetailInfo) intent.getSerializableExtra(EXTRA_KEY_SECURITY_INFO);

        ArrayList<String> imageUrls = new ArrayList<>(1);
        String imageUrl = securityAlarmDetailInfo.getSceneUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            imageUrls.add(imageUrl);
        }

        getView().updatePhotoList(imageUrls, intent.getIntExtra(EXTRA_KEY_POSITION, 0));

        getView().updatePhotoInfo(securityAlarmDetailInfo.getAlarmType(),
                securityAlarmDetailInfo.getTaskName(),
                securityAlarmDetailInfo.getCamera().getName() + "  " + DateUtil.getStrTimeToday(mActivity, securityAlarmDetailInfo.getAlarmTime(), 0));
    }


    @Override
    public void onDestroy() {

    }

    public void doBack() {
        getView().finishAc();
    }

    public void doDownload(String url) {
        downloadImage(url);
    }

    private void downloadImage(String url) {
        Glide.with(mActivity)
                .downloadOnly()
                .load(url)
                .addListener(new RequestListener<File>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                        mActivity.runOnUiThread(() -> getView().toastShort(mActivity.getString(R.string.toast_image_download_failed)));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                        ThreadPoolManager.getInstance().execute(() -> {
                            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath(), System.currentTimeMillis()+".jpg");
                            FileUtil.copy(resource, file);
                            mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                    Uri.fromFile(new File(file.getPath()))));
                            mActivity.runOnUiThread(() -> getView().toastShort(mActivity.getString(R.string.toast_image_download_success)));
                        });
                        return false;
                    }
                }).submit();
    }
}

package com.sensoro.city_camera.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
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

        ArrayList<String> imageUrls = new ArrayList<>(2);
        String imageUrl = securityAlarmDetailInfo.getImageUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            imageUrls.add(imageUrl);
        }
        String faceUrl = securityAlarmDetailInfo.getFaceUrl();
        if (!TextUtils.isEmpty(faceUrl)) {
            imageUrls.add(faceUrl);
        }

        getView().updatePhotoList(imageUrls, intent.getIntExtra(EXTRA_KEY_POSITION, 0));

        getView().updatePhotoInfo(securityAlarmDetailInfo.getAlarmType(),
                securityAlarmDetailInfo.getTaskName(),
                securityAlarmDetailInfo.getCamera().getName() + "  " + DateUtil.getStrTimeToday(mActivity, Long.parseLong(securityAlarmDetailInfo.getAlarmTime()), 0));
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
                .asBitmap()
                .load(url)
                .into(new FutureTarget<Bitmap>() {
                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {

                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        getView().toastShort(mActivity.getString(R.string.toast_image_download_failed));
                    }

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // 下载成功回调函数
                        // 数据处理方法，保存bytes到文件 FileUtil.copy(file, bytes);
                        String[] split = url.split("/");
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath(), split[split.length - 1]);
                        ThreadPoolManager.getInstance().execute(() -> {
                            boolean b = FileUtil.saveImageToGallery(file, resource, mActivity);
                            if (b) {
                                mActivity.runOnUiThread(() -> getView().toastShort(mActivity.getString(R.string.toast_image_download_success)));
                            } else {
                                mActivity.runOnUiThread(() -> getView().toastShort(mActivity.getString(R.string.toast_image_download_failed)));
                            }

                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }

                    @Override
                    public void getSize(@NonNull SizeReadyCallback cb) {

                    }

                    @Override
                    public void removeCallback(@NonNull SizeReadyCallback cb) {

                    }

                    @Override
                    public void setRequest(@Nullable Request request) {

                    }

                    @Nullable
                    @Override
                    public Request getRequest() {
                        return null;
                    }

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onStop() {

                    }

                    @Override
                    public void onDestroy() {

                    }

                    @Override
                    public boolean cancel(boolean mayInterruptIfRunning) {
                        return false;
                    }

                    @Override
                    public boolean isCancelled() {
                        return false;
                    }

                    @Override
                    public boolean isDone() {
                        return false;
                    }

                    @Override
                    public Bitmap get() throws ExecutionException, InterruptedException {
                        return null;
                    }

                    @Override
                    public Bitmap get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
                        return null;
                    }
                });
    }
}

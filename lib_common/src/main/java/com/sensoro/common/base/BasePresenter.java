package com.sensoro.common.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sensoro.common.R;
import com.sensoro.common.iwidget.IOnDestroy;

import java.lang.ref.WeakReference;

/**
 * @author DDONG
 * @date 2018/2/4 0004
 */

public abstract class BasePresenter<V> implements IOnDestroy {
    private WeakReference<V> mViewRef;

    /**
     * 这里采用弱引用
     *
     * @param view
     */
    public void attachView(V view) {
        mViewRef = new WeakReference<>(view);
    }

    /**
     * 获取当前的View
     *
     * @return
     */
    protected V getView() {
        if (mViewRef != null) {
            return mViewRef.get();
        }
        return null;
    }

    /**
     * 查看View是否存在
     *
     * @return
     */
    public boolean isAttachedView() {
        return mViewRef != null && mViewRef.get() != null;
    }

    /**
     * 解除View
     */
    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
    }

    public abstract void initData(Context context);

    /**
     * @param path
     * @param bundle
     * @param activity
     */
    public void startActivity(String path, Bundle bundle, @NonNull Activity activity) {
        ARouter.getInstance().build(path).with(bundle).withTransition(R.anim.slide_left, R.anim.slide_out)
                .navigation(activity);
    }

    /**
     * 适用于arouter的postcard
     *
     * @param postcard
     * @param activity
     */
    public void startActivity(@NonNull Postcard postcard, @NonNull Activity activity) {
        postcard.withTransition(R.anim.slide_left, R.anim.slide_out)
                .navigation(activity);
    }

    /**
     * @param path
     * @param bundle
     * @param activity
     * @param requestCode
     */
    public void startActivityForResult(String path, Bundle bundle, @NonNull Activity activity, int requestCode) {
        ARouter.getInstance().build(path).with(bundle).withTransition(R.anim.slide_left, R.anim.slide_out)
                .navigation(activity, requestCode);
    }

    /**
     * @param postcard
     * @param activity
     * @param requestCode
     */
    public void startActivityForResult(@NonNull Postcard postcard, @NonNull Activity activity, int requestCode) {
        postcard.withTransition(R.anim.slide_left, R.anim.slide_out)
                .navigation(activity, requestCode);
    }

    /**
     * @param activity
     * @return
     */
    public Bundle getBundle(@NonNull Activity activity) {
        Intent intent = activity.getIntent();
        if (intent != null) {
            return intent.getExtras();
        }
        return null;
    }

    /**
     * 判断是否有bundle，目前可以判断是否是阿里arouter来源
     *
     * @param activity
     * @return
     */
    public boolean hasBundle(@NonNull Activity activity) {
        return getBundle(activity) != null;
    }

    /**
     * 这里采用 instanceof 合适（适用于bundle）
     *
     * @param activity
     * @param key
     * @return
     */
    public Object getBundleValue(@NonNull Activity activity, @NonNull String key) {
        Intent intent = activity.getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                return extras.get(key);
            }
        }
        return null;
    }

}

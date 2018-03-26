package com.sensoro.smartcity.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.Method;

/**
 * Created by fangping on 2016/7/21.
 */

public abstract class BaseActivity extends AppCompatActivity {

    View decorView;
    int screenWidth;//屏宽
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        verifyStoragePermissions();
        decorView=getWindow().getDecorView();
        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth=metrics.widthPixels;
    }

    protected abstract boolean isNeedSlide();

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("", "BaseActivity onActivityResult===>");
    }

    public boolean checkDeviceHasNavigationBar() {
        boolean hasNavigationBar = false;
        Resources rs = getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;
    }

    public void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }


    float startX, startY, endX, endY, distanceX, distanceY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (isNeedSlide()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    startY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    endX = event.getX();
                    endY = event.getY();
                    distanceX = endX - startX;
                    distanceY = Math.abs(endY - startY);
                    //1.判断手势右滑  2.横向滑动的距离要大于竖向滑动的距离
                    if (endX - startX > 0 && distanceY < distanceX) {
                        decorView.setX(distanceX);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    endX = event.getX();
                    distanceX = endX - startX;
                    endY = event.getY();
                    distanceY = Math.abs(endY - startY);
                    //1.判断手势右滑  2.横向滑动的距离要大于竖向滑动的距离 3.横向滑动距离大于屏幕三分之一才能finish
                    if (endX - startX > 0 && distanceY < distanceX && distanceX > screenWidth / 3) {
                        moveOn(distanceX);
                    }
                    //1.判断手势右滑  2.横向滑动的距离要大于竖向滑动的距离 但是横向滑动距离不够则返回原位置
                    else if (endX - startX > 0 && distanceY < distanceX) {
                        backOrigin(distanceX);
                    } else {
                        decorView.setX(0);
                    }
                    break;
            }
        }

        return super.dispatchTouchEvent(event);
    }


    /**
     * 返回原点
     *
     * @param distanceX 横向滑动距离
     */
    private void backOrigin(float distanceX) {
        ObjectAnimator.ofFloat(decorView, "X", distanceX, 0).setDuration(300).start();
    }

    /**
     * 划出屏幕
     *
     * @param distanceX 横向滑动距离
     */
    private void moveOn(float distanceX) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(distanceX, screenWidth);
        valueAnimator.setDuration(300);
        valueAnimator.start();

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                decorView.setX((Float) animation.getAnimatedValue());
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}

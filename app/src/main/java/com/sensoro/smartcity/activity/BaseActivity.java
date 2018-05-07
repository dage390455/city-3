package com.sensoro.smartcity.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.sensoro.smartcity.SensoroCityApplication;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by fangping on 2016/7/21.
 */

public abstract class BaseActivity extends AppCompatActivity {

    View decorView;
    int screenWidth;//屏宽
    private static final int REQUEST_EXTERNAL_STORAGE = 0x12;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private ValueAnimator mValueAnimator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
//        verifyStoragePermissions();
        decorView = getWindow().getDecorView();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
    }

    protected abstract boolean isNeedSlide();

    @Override
    protected void onResume() {
        NotificationManagerCompat manager = NotificationManagerCompat.from(SensoroCityApplication.getInstance());
        boolean isOpened = manager.areNotificationsEnabled();
        if (!isNotificationEnabled(this)&&!isOpened) {
            showRationaleDialog();
        }
        super.onResume();
    }

    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    /**
     * 检查通知权限
     *
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    private boolean isNotificationEnabled(Context context) {

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
        /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 弹出声明的 Dialog
     */
    private void showRationaleDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("通知中包含了重要报警信息，请前往设置，打开的通知选项。")
                .setPositiveButton("前往设置",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //去设置界面
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", SensoroCityApplication.getInstance()
                                        .getPackageName(), null);
                                intent.setData(uri);
                                dialog.dismiss();
                                startActivity(intent);
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
            mValueAnimator = null;
        }
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

    /**
     * 重写方法 滑动退出ac
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //滑动来退出activity
//        if (isNeedSlide()) {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    startX = event.getX();
//                    startY = event.getY();
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    endX = event.getX();
//                    endY = event.getY();
//                    distanceX = endX - startX;
//                    distanceY = Math.abs(endY - startY);
//                    //1.判断手势右滑  2.横向滑动的距离要大于竖向滑动的距离
//                    if (endX - startX > 0 && distanceY < distanceX) {
//                        decorView.setX(distanceX);
//                    }
//                    break;
//                case MotionEvent.ACTION_UP:
//                    endX = event.getX();
//                    distanceX = endX - startX;
//                    endY = event.getY();
//                    distanceY = Math.abs(endY - startY);
//                    //1.判断手势右滑  2.横向滑动的距离要大于竖向滑动的距离 3.横向滑动距离大于屏幕三分之一才能finish
//                    if (endX - startX > 0 && distanceY < distanceX && distanceX > screenWidth / 3) {
//                        moveOn(distanceX);
//                    }
//                    //1.判断手势右滑  2.横向滑动的距离要大于竖向滑动的距离 但是横向滑动距离不够则返回原位置
//                    else if (endX - startX > 0 && distanceY < distanceX) {
//                        backOrigin(distanceX);
//                    } else {
//                        decorView.setX(0);
//                    }
//                    break;
//            }
//        }

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
        mValueAnimator = ValueAnimator.ofFloat(distanceX, screenWidth);
        mValueAnimator.setDuration(300);


        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                decorView.setX((Float) animation.getAnimatedValue());
            }
        });

        mValueAnimator.addListener(new Animator.AnimatorListener() {
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
        mValueAnimator.start();
    }

}

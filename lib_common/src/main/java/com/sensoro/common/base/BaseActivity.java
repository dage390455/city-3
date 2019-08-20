package com.sensoro.common.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.baidu.mobstat.StatService;
import com.gyf.immersionbar.ImmersionBar;
import com.sensoro.common.BuildConfig;
import com.sensoro.common.R;
import com.sensoro.common.manger.ActivityTaskManager;
import com.sensoro.common.utils.LogUtils;
import com.sensoro.common.widgets.PermissionDialogUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.lang.reflect.Method;

/**
 * @author JL-DDONG
 * @date 2018/2/4 0004
 */

public abstract class BaseActivity<V, P extends BasePresenter<V>> extends AppCompatActivity {
    //    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
//    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Runnable notificationsTask = new Runnable() {
        @Override
        public void run() {
            AndPermission.with(BaseActivity.this).notification().permission().rationale(new Rationale<Void>() {
                @Override
                public void showRationale(Context context, Void data, RequestExecutor executor) {
                    try {
                        LogUtils.loge("permission no : showRationale");
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    showRationaleDialog(executor);
                }
            }).onGranted(new Action<Void>() {
                @Override
                public void onAction(Void data) {
                    try {
                        LogUtils.loge("permission no : onGranted");
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }).onDenied(new Action<Void>() {
                @Override
                public void onAction(Void data) {
                    try {
                        LogUtils.loge("permission no : onDenied");
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    showRationaleDialog(null);
                }
            }).start();
        }
    };
    /**
     * 代理者
     */
    protected P mPresenter;
    /**
     * 主AC
     */
    protected BaseActivity mActivity;
    public ImmersionBar immersionBar;
    private PermissionDialogUtils permissionDialogUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!setMyCurrentActivityOrientation()) {
            super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED //锁屏状态下显示
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD //解锁
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON //保持屏幕长亮
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON); //打开屏幕
        //
        mPresenter = createPresenter();
        mPresenter.attachView((V) this);
        V view = mPresenter.getView();
        if (view instanceof BaseActivity) {
            mActivity = (BaseActivity) view;
        } else {
            try {
                LogUtils.loge(this, "当前View转换异常！");
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            mActivity = this;
        }
        if (!setMyCurrentActivityTheme()) {
            setTheme(R.style.MyTheme);
        }
        //取消bar
        ActionBar supportActionBar = mActivity.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }
//        CustomDensityUtils.SetCustomDensity(this, ContextUtils.getContext());
        //控制顶部状态栏显示
//        StatusBarCompat.translucentStatusBar(thi®s);
//        StatusBarCompat.setStatusBarIconDark(this,true);
//        boolean darkmode = true;
        onCreateInit(savedInstanceState);
        StatService.setDebugOn(BuildConfig.DEBUG);
        ARouter.getInstance().inject(this);
        ActivityTaskManager.getInstance().pushActivity(this);
    }

    /**
     * 处理顶部的StatusBar
     */
    private void handleStatusBar() {
        if (!isActivityOverrideStatusBar()) {
            immersionBar = ImmersionBar.with(this);
            immersionBar.fitsSystemWindows(true, R.color.white)
                    .statusBarColor(R.color.white)
                    .statusBarDarkFont(true)
                    .init();
        }
    }

    /**
     * activity 需要自己设置statusbar 重写该函数，并在该函数内实现
     *
     * @return
     */
    public boolean isActivityOverrideStatusBar() {
        return false;
    }

    public boolean setMyCurrentActivityTheme() {
        return false;
    }

    public boolean setMyCurrentActivityOrientation() {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        StatService.start(mActivity);
    }

    /**
     * 抽象方法初始化View
     */
    protected abstract void onCreateInit(Bundle savedInstanceState);

    private boolean isDestroyed = false;

    private void destroy() {
        if (isDestroyed) {
            return;
        }
        mHandler.removeCallbacksAndMessages(null);
        SensoroToast.getInstance().cancelToast();
        if (permissionDialogUtils != null) {
            permissionDialogUtils.destroy();
        }
        mPresenter.onDestroy();
        mPresenter.detachView();
//        if (immersionBar != null) {
//            immersionBar.destroy();
//        }
        ActivityTaskManager.getInstance().popActivity(this);
        isDestroyed = true;
    }

    @Override
    protected void onDestroy() {
        destroy();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleStatusBar();
        mHandler.postDelayed(notificationsTask, 1000);
        StatService.onResume(mActivity);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(notificationsTask);
        StatService.onPause(mActivity);
        if (isFinishing()) {
            destroy();
        }
    }

    /**
     * 创建代理者
     *
     * @return
     */
    protected abstract P createPresenter();


//    /**
//     * 检查通知权限
//     *
//     * @param context
//     * @return
//     */
//    @SuppressLint("NewApi")
//    private boolean isNotificationEnabled(Context context) {
//
//        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
//        ApplicationInfo appInfo = context.getApplicationInfo();
//        String pkg = context.getApplicationContext().getPackageName();
//        int uid = appInfo.uid;
//
//        Class appOpsClass = null;
//        /* Context.APP_OPS_MANAGER */
//        try {
//            appOpsClass = Class.forName(AppOpsManager.class.getName());
//            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
//                    String.class);
//            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
//
//            int value = (Integer) opPostNotificationValue.get(Integer.class);
//            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
//
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }


    /**
     * 弹出声明的 Dialog
     *
     * @param executor
     */
    private void showRationaleDialog(RequestExecutor executor) {
        if (permissionDialogUtils == null) {
            permissionDialogUtils = new PermissionDialogUtils(mActivity);
        }
        permissionDialogUtils.setTipMessageText(mActivity.getString(R.string.notification_prompt))
                .setTipCacnleText(mActivity.getString(R.string.cancel), mActivity.getResources().getColor(R.color.c_a6a6a6))
                .setTipConfirmText(mActivity.getString(R.string.go_setting), mActivity.getResources().getColor(R.color.colorAccent))
                .show(new PermissionDialogUtils.TipDialogUtilsClickListener() {
                    @Override
                    public void onCancelClick() {
                        if (permissionDialogUtils != null) {
                            permissionDialogUtils.dismiss();
                        }
                        if (executor != null) {
                            executor.cancel();
                        }

                    }

                    @Override
                    public void onConfirmClick() {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", ContextUtils.getContext()
                                .getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                        if (permissionDialogUtils != null) {
                            permissionDialogUtils.dismiss();
                        }
                        if (executor != null) {
                            executor.execute();
                        }
                    }
                });
    }

    public boolean checkDeviceHasNavigationBar() {
        boolean hasNavigationBar = false;
        Resources rs = mActivity.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("getInstance", String.class);
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_out, R.anim.slide_right);
        ActivityTaskManager.getInstance().popActivity(this);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_left, R.anim.slide_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_left, R.anim.slide_out);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}

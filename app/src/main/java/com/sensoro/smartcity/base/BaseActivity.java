package com.sensoro.smartcity.base;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.baidu.mobstat.StatService;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.SensoroToast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author JL-DDONG
 * @date 2018/2/4 0004
 */

public abstract class BaseActivity<V, P extends BasePresenter<V>> extends AppCompatActivity {
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
    /**
     * 代理者
     */
    protected P mPresenter;
    /**
     * 主AC
     */
    protected BaseActivity mActivity;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.MyTheme);
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        mPresenter.attachView((V) this);
        V view = mPresenter.getView();
        if (view instanceof BaseActivity) {
            mActivity = (BaseActivity) view;
        } else {
            LogUtils.loge(this, "当前View转换异常！");
            mActivity = this;
        }
        //取消bar
        ActionBar supportActionBar = mActivity.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }
//        CustomDensityUtils.SetCustomDensity(this, SensoroCityApplication.getInstance());
        //控制顶部状态栏显示
//        StatusBarCompat.setStatusBarColor(this);
        onCreateInit(savedInstanceState);
        StatService.setDebugOn(true);
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


    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        mPresenter.detachView();
        SensoroToast.INSTANCE.cancelToast();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        final NotificationManagerCompat manager = NotificationManagerCompat.from(mActivity);
        boolean isOpened = manager.areNotificationsEnabled();
        if (!isNotificationEnabled(mActivity) && !isOpened) {
            showRationaleDialog();
        }
        super.onResume();
        StatService.onResume(mActivity);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(mActivity);
    }

    /**
     * 创建代理者
     *
     * @return
     */
    protected abstract P createPresenter();


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
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
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

    public boolean checkDeviceHasNavigationBar() {
        boolean hasNavigationBar = false;
        Resources rs = mActivity.getResources();
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
}

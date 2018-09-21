package com.sensoro.smartcity.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Toast;

import com.sensoro.smartcity.widget.SensoroToast;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.lang.ref.SoftReference;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static android.content.Context.ACTIVITY_SERVICE;

public final class RxPermissionUtils {

    private volatile OnRxPermissionsResultObserve mListener;
    private SoftReference<FragmentActivity> reference;
    private final RxPermissions rxPermissions;
    private Disposable disposable;
    private String permissions;

    public RxPermissionUtils(FragmentActivity activity) {
        reference = new SoftReference<>(activity);
        rxPermissions = new RxPermissions(reference.get());
    }

    private static final int REQUEST_CODE_PERMISSION = 0x778;

    //请求权限
    //申请单个或者多个权限,不在乎是否不再询问和哪个权限申请失败，只要有一个失败就执行失败操作：
    public void requestRxPermissions(final String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return;
        }
        if (mListener == null) {
            throw new NullPointerException("请先注册监听!");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            disposable = rxPermissions.request(permissions).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Boolean granted) throws Exception {
                    LogUtils.loge("granted = "+granted);
                    if (granted) {
                        if (mListener != null) {
                            mListener.onRxPermissionGranted();
                        }
                    } else {
                        showRationaleDialog(true, permissions);
                    }
                }
            });
        } else {
            if (mListener != null) {
                mListener.onRxPermissionGranted();
            }
        }

    }

    public interface OnRxPermissionsResultObserve {
        void onRxPermissionGranted();
    }

    //申请多个权限，在乎是否不再询问和哪个权限申请失败：
    public void requestSingleRxPermission(final String permission) {
        this.permissions = permission;
        if (permission == null) {
            return;
        }
        if (mListener == null) {
            throw new NullPointerException("请先注册监听!");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            disposable = rxPermissions.requestEach(permission).subscribe(new Consumer<Permission>() {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Permission permission) throws Exception {
                    if (permission.granted) {
                        if (mListener != null) {
                            mListener.onRxPermissionGranted();
                        }
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        showRationaleDialog(permission, true);
                    } else {
                        // 拒绝权限请求,并不再询问
                        // 可以提醒用户进入设置界面去设置权限
//                        Toast.makeText(mActivity, "已拒绝权限" + permission.name + "并不再询问", Toast
//                                .LENGTH_SHORT).show();
                        showPermissionSettingDialog(true);
                    }
                }
            });
        } else {
            if (mListener != null) {
                mListener.onRxPermissionGranted();
            }
        }


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //如果需要跳转系统设置页后返回自动再次检查和执行业务 如果不需要则不需要重写onActivityResult
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (TextUtils.isEmpty(permissions)) {
                requestSingleRxPermission(permissions);
            }

        }
    }

    private boolean hasActivity() {
        return reference.get() != null;
    }

    /**
     * 弹出声明的 Dialog
     *
     * @param permissions
     */
    private void showRationaleDialog(final boolean needFinish, final String... permissions) {
        if (hasActivity()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(reference.get());
            builder.setTitle("提示")
                    .setMessage("为了应用可以正常使用，请您点击确认申请权限。")
                    .setPositiveButton("确认",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestRxPermissions(permissions);
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (needFinish) {
                                        SensoroToast.INSTANCE.makeText(reference.get(), "需要权限！请重新打开应用", Toast
                                                .LENGTH_SHORT).show();
                                        reference.get().finish();
                                    }
                                }
                            })
                    .setCancelable(false)
                    .show();
        }

    }

    private void showRationaleDialog(final Permission permission, final boolean needFinish) {
        if (hasActivity()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(reference.get());
            builder.setTitle("提示")
                    .setMessage("为了应用可以正常使用，请您点击确认申请权限。")
                    .setPositiveButton("确认",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestSingleRxPermission(permission.name);
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (needFinish) {
                                        SensoroToast.INSTANCE.makeText(reference.get(), "需要权限！请重新打开应用", Toast
                                                .LENGTH_SHORT).show();
                                        reference.get().finish();
                                    }
                                }
                            })
                    .setCancelable(false)
                    .show();
        }

    }


    /**
     * 手动开启权限弹窗
     */
    private void showPermissionSettingDialog(final boolean needFinish) {
        if (hasActivity()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(reference.get());
            builder.setTitle("提示")
                    .setMessage("必要的权限被拒绝")
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent in = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", reference.get().getPackageName(), null);
                            in.setData(uri);
                            reference.get().startActivityForResult(in, REQUEST_CODE_PERMISSION);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            if (needFinish) {
                                restart(reference.get());
                            }
                        }
                    })
                    .setCancelable(false)
                    .show();
        }

    }

    /**
     * 通过任务管理器杀死进程
     * 需添加权限 {@code <uses-permission android:name="android.permission.RESTART_PACKAGES"/>}</p>
     *
     * @param context
     */
    private static void restart(Context context) {
        int currentVersion = Build.VERSION.SDK_INT;
        if (currentVersion > Build.VERSION_CODES.ECLAIR_MR1) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startMain);
            System.exit(0);
        } else {// android2.1
            ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            am.restartPackage(context.getPackageName());
        }
    }


    public void registerObserver(OnRxPermissionsResultObserve onRxPermissionsResultObserve) {
        mListener = onRxPermissionsResultObserve;
    }

    public void unregisterObserver(OnRxPermissionsResultObserve onRxPermissionsResultObserve) {
        if (onRxPermissionsResultObserve != mListener) {
            throw new IllegalArgumentException("注册对象不一致！");
        }
        if (mListener != null) {
            mListener = null;
        }
        if (reference != null) {
            reference.clear();
            reference = null;
        }
        if (disposable != null) {
            disposable.dispose();
        }
    }

}

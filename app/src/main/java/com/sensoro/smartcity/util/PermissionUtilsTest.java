package com.sensoro.smartcity.util;

import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class PermissionUtilsTest {

    private final FragmentActivity mActivity;

    public PermissionUtilsTest(FragmentActivity activity) {
        mActivity = activity;
    }

    //请求权限
    //申请单个或者多个权限,不在乎是否不再询问和哪个权限申请失败，只要有一个失败就执行失败操作：
    private void requestRxPermissions(String... permissions) {
        RxPermissions rxPermissions = new RxPermissions(mActivity);
        Disposable subscribe = rxPermissions.request(permissions).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean granted) throws Exception {
                if (granted) {
                    Toast.makeText(mActivity, "已获取权限", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, "已拒绝一个或以上权限", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //申请多个权限，在乎是否不再询问和哪个权限申请失败：
    private void requestEachRxPermission(String... permissions) {
        RxPermissions rxPermissions = new RxPermissions(mActivity);
        rxPermissions.requestEach(permissions).subscribe(new Consumer<Permission>() {
            @Override
            public void accept(@NonNull Permission permission) throws Exception {
                if (permission.granted) {
                    Toast.makeText(mActivity, "已获取权限" + permission.name, Toast.LENGTH_SHORT).show();
                } else if (permission.shouldShowRequestPermissionRationale) {
                    //拒绝权限请求
                    Toast.makeText(mActivity, "已拒绝权限" + permission.name, Toast.LENGTH_SHORT).show();
                } else {
                    // 拒绝权限请求,并不再询问
                    // 可以提醒用户进入设置界面去设置权限
                    Toast.makeText(mActivity, "已拒绝权限" + permission.name + "并不再询问", Toast
                            .LENGTH_SHORT).show();
                }
            }
        });

    }


}

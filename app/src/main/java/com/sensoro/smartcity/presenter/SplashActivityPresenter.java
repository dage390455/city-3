package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;

import com.igexin.sdk.PushManager;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.LoginActivity;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.ISplashActivityView;
import com.sensoro.smartcity.iwidget.IOnStart;
import com.sensoro.smartcity.model.EventLoginData;
import com.sensoro.smartcity.push.SensoroPushIntentService;
import com.sensoro.smartcity.push.SensoroPushService;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.MyPermissionManager;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.widget.dialog.PermissionDialogUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.util.List;

public class SplashActivityPresenter extends BasePresenter<ISplashActivityView> implements Constants, IOnStart {
    private Activity mContext;
    private final Handler handler = new Handler();

    private PermissionDialogUtils permissionDialogUtils;
    private final String[] requestPermissions = {Permission.READ_PHONE_STATE, Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION, Permission.WRITE_EXTERNAL_STORAGE, Permission.WRITE_CONTACTS, Permission.CAMERA, Permission.RECORD_AUDIO, Permission.CALL_PHONE};

    @Override
    public void initData(Context context) {
//        try {
//            LogUtils.loge(mContext.getActionBar().getCustomView().toString());
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
        mContext = (Activity) context;
        permissionDialogUtils = new PermissionDialogUtils(mContext);
//        getView().startAC(new Intent(mContext, DeployMonitorCheckActivity.class));
//        getView().finishAc();
    }

    private void checkLoginState() {
        try {
            RetrofitServiceHelper.INSTANCE.getBaseUrlType();
            String sessionID = RetrofitServiceHelper.INSTANCE.getSessionId();
            try {
                LogUtils.loge("sessionID = " + sessionID);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            if (TextUtils.isEmpty(sessionID)) {
                openLogin();
                return;
            }
            EventLoginData userData = PreferencesHelper.getInstance().getUserData();
            if (TextUtils.isEmpty(userData.phoneId) || TextUtils.isEmpty(userData.userId)) {
                openLogin();
                return;
            }
            openMain(userData);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                LogUtils.loge("login Exception : " + e.getMessage());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            openLogin();
        }

    }

    //没有登录跳转登录界面
    private void openLogin() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginIntent = new Intent();
                loginIntent.setClass(mContext, LoginActivity.class);
                getView().startAC(loginIntent);
                getView().finishAc();
            }
        }, 500);

    }

    private void openMain(final EventLoginData eventLoginData) {
        // 提前加载数据
        PreferencesHelper.getInstance().getLocalDevicesMergeTypes();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent();
                mainIntent.setClass(mContext, MainActivity.class);
                mainIntent.putExtra(EXTRA_EVENT_LOGIN_DATA, eventLoginData);
                getView().startAC(mainIntent);
                getView().finishAc();
            }
        }, 500);


    }

    private void initPushSDK() {
        PushManager.getInstance().initialize(SensoroCityApplication.getInstance(), SensoroPushService.class);
        // 注册 intentService 后 PushDemoReceiver 无效, sdk 会使用 DemoIntentService 传递数据,
        // AndroidManifest 对应保留一个即可(如果注册 DemoIntentService, 可以去掉 PushDemoReceiver, 如果注册了
        // IntentService, 必须在 AndroidManifest 中声明)
        PushManager.getInstance().registerPushIntentService(SensoroCityApplication.getInstance(),
                SensoroPushIntentService.class);
        if (PushManager.getInstance().getClientid(SensoroCityApplication.getInstance()) == null) {
            PushManager.getInstance().turnOffPush(SensoroCityApplication.getInstance());
        } else {
            PushManager.getInstance().turnOnPush(SensoroCityApplication.getInstance());
        }
    }

    @Override
    public void onDestroy() {
        try {
            LogUtils.loge("SplashActivityPresenter onDestroy ");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        handler.removeCallbacksAndMessages(null);
    }

    private void requestPermissions(final String[] permissions) {
        AndPermission.with(mContext).runtime()
                .permission(permissions)
                .rationale(new Rationale<List<String>>() {
                    @Override
                    public void showRationale(Context context, List<String> data, final RequestExecutor executor) {
                        // 重新授权的提示
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String str : data) {
                            stringBuilder.append(str).append(",");
                        }
                        try {
                            LogUtils.loge("权限列表：" + stringBuilder.toString());
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                        if (isAttachedView()) {
                            permissionDialogUtils.setTipMessageText(mContext.getString(R.string.permission_descript)).setTipConfirmText(mContext.getString(R.string.reauthorization), mContext.getResources().getColor(R.color.colorAccent)).show(new PermissionDialogUtils.TipDialogUtilsClickListener() {
                                @Override
                                public void onCancelClick() {
                                    if (isAttachedView()) {
                                        executor.cancel();
                                        permissionDialogUtils.dismiss();
                                        MyPermissionManager.restart(mContext);
                                    }
                                }

                                @Override
                                public void onConfirmClick() {
                                    if (isAttachedView()) {
                                        executor.execute();
                                        permissionDialogUtils.dismiss();
                                    }
                                }
                            });
                        }
                    }
                })
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        // 用户同意授权
                        if (isAttachedView()) {
                            initPushSDK();
                            checkLoginState();
                            try {
                                LogUtils.loge("SplashActivityPresenter 进入界面 ");
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        // 用户拒绝权限，提示用户授权
                        if (isAttachedView()) {
                            if (AndPermission.hasAlwaysDeniedPermission(mContext, permissions)) {
                                // 如果用户勾选了禁止重复提醒，需要提示用户去到APP权限设置页面开启权限
                                String permissionTips = MyPermissionManager.getPermissionTips(data);
                                permissionDialogUtils.setTipConfirmText(mContext.getString(R.string.go_setting), mContext.getResources().getColor(R.color.c_f34a4a)).setTipMessageText(permissionTips + mContext.getString(R.string.permission_check)).show(new PermissionDialogUtils.TipDialogUtilsClickListener() {
                                    @Override
                                    public void onCancelClick() {
                                        if (isAttachedView()) {
                                            permissionDialogUtils.dismiss();
                                            MyPermissionManager.restart(mContext);
                                        }
                                    }

                                    @Override
                                    public void onConfirmClick() {
                                        if (isAttachedView()) {
                                            permissionDialogUtils.dismiss();
                                            MyPermissionManager.startAppSetting(mContext);
                                        }
                                    }
                                });
                            } else {
                                requestPermissions(data.toArray(new String[data.size()]));
                            }
                        }


                    }
                })
                .start();

    }

    @Override
    public void onStart() {
        requestPermissions(requestPermissions);
    }

    @Override
    public void onStop() {

    }
}

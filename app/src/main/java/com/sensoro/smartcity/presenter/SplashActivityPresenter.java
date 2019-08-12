package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;

import com.igexin.sdk.PushManager;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.iwidget.IOnStart;
import com.sensoro.common.manger.RxApiManager;
import com.sensoro.common.model.EventData;
import com.sensoro.common.model.EventLoginData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.RetryWithDelay;
import com.sensoro.common.server.bean.UserInfo;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.utils.MyPermissionManager;
import com.sensoro.common.widgets.PermissionDialogUtils;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.LoginActivity;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.factory.UserPermissionFactory;
import com.sensoro.smartcity.imainviews.ISplashActivityView;
import com.sensoro.smartcity.push.SensoroPushIntentService;
import com.sensoro.smartcity.push.SensoroPushService;
import com.sensoro.smartcity.util.LogUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SplashActivityPresenter extends BasePresenter<ISplashActivityView> implements IOnStart, IOnCreate {
    private Activity mContext;
    private final Handler handler = new Handler();

    private PermissionDialogUtils permissionDialogUtils;
    private final String[] requestPermissions = {Permission.READ_PHONE_STATE, Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION, Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE, Permission.WRITE_CONTACTS, Permission.READ_CONTACTS, Permission.CAMERA, Permission.RECORD_AUDIO, Permission.CALL_PHONE};

    @Override
    public void initData(Context context) {
//        try {
//            LogUtils.loge(mContext.getActionBar().getCustomView().toString());
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
        mContext = (Activity) context;
        permissionDialogUtils = new PermissionDialogUtils(mContext);
//        getView().startAC(new Intent(mContext, SecurityRisksActivity.class));
//        getView().finishAc();
        onCreate();

    }

    private void checkLoginState() {
        try {
            RetrofitServiceHelper.getInstance().getBaseUrlType();
            String sessionID = RetrofitServiceHelper.getInstance().getSessionId();
            String sessionToken = RetrofitServiceHelper.getInstance().getSessionToken();
            try {
                LogUtils.loge("sessionID = " + sessionID + ",token = " + sessionToken);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            //检验版本信息
            int saveVersionCode = PreferencesHelper.getInstance().getSaveVersionCode();
            if (saveVersionCode == -1) {
                openLogin();
                return;
            }
            int currentVersionCode = AppUtils.getVersionCode(mContext);
            if (currentVersionCode > saveVersionCode) {
                openLogin();
                return;
            }
            // 两个都为空时需要重新登录
            if (TextUtils.isEmpty(sessionToken) && TextUtils.isEmpty(sessionID)) {
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
        //
        final long requestTime = System.currentTimeMillis();
        final Runnable overTime = new Runnable() {
            @Override
            public void run() {
                //登录失败 3秒内没有登录进去 就认为超时直接进入登录界面
                RxApiManager.getInstance().cancelAll();
                Intent loginIntent = new Intent();
                loginIntent.setClass(mContext, LoginActivity.class);
                getView().startAC(loginIntent);
                getView().finishAc();
            }
        };
        handler.postDelayed(overTime, 3000);
        RetrofitServiceHelper.getInstance().getPermissionChangeInfo().retryWhen(new RetryWithDelay(2, 100)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<UserInfo>>(this) {
            @Override
            public void onCompleted(ResponseResult<UserInfo> loginRsp) {
                handler.removeCallbacks(overTime);
                UserInfo userInfo = loginRsp.getData();
                EventLoginData loginData = UserPermissionFactory.createLoginData(userInfo, eventLoginData.phoneId);

                //
                long diff = System.currentTimeMillis() - requestTime;
                try {
                    LogUtils.loge("diff = " + diff);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                if (diff >= 500) {
                    Intent mainIntent = new Intent();
                    mainIntent.setClass(mContext, MainActivity.class);
                    mainIntent.putExtra(Constants.EXTRA_EVENT_LOGIN_DATA, loginData);
                    getView().startAC(mainIntent);
                    getView().finishAc();
                } else {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent mainIntent = new Intent();
                            mainIntent.setClass(mContext, MainActivity.class);
                            mainIntent.putExtra(Constants.EXTRA_EVENT_LOGIN_DATA, loginData);
                            getView().startAC(mainIntent);
                            getView().finishAc();
                        }
                    }, 500 - diff);
                }

            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                //网络出错直接跳到登录界面并吐丝
                //
                handler.removeCallbacks(overTime);
                long diff = System.currentTimeMillis() - requestTime;
                getView().toastShort(errorMsg);
                if (diff >= 500) {
                    Intent loginIntent = new Intent();
                    loginIntent.setClass(mContext, LoginActivity.class);
                    getView().startAC(loginIntent);
                    getView().finishAc();
                } else {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent loginIntent = new Intent();
                            loginIntent.setClass(mContext, LoginActivity.class);
                            getView().startAC(loginIntent);
                            getView().finishAc();
                        }
                    }, 500 - diff);
                }
            }
        });


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
        EventBus.getDefault().unregister(this);
        try {
            LogUtils.loge("SplashActivityPresenter onDestroy ");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        handler.removeCallbacksAndMessages(null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        switch (code) {
            case Constants.EVENT_DATA_SESSION_ID_OVERTIME:
                Intent loginIntent = new Intent();
                loginIntent.setClass(mContext, LoginActivity.class);
                getView().startAC(loginIntent);
                getView().finishAc();
                break;
        }
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

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }
}

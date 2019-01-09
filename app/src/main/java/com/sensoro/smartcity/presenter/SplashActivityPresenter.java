package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;

import com.igexin.sdk.PushManager;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.LoginActivity;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.ISplashActivityView;
import com.sensoro.smartcity.model.EventLoginData;
import com.sensoro.smartcity.push.SensoroPushIntentService;
import com.sensoro.smartcity.push.SensoroPushService;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;

public class SplashActivityPresenter extends BasePresenter<ISplashActivityView> implements Constants {
    private Activity mContext;
    private final Handler handler = new Handler();

    @Override
    public void initData(Context context) {
        try {
            LogUtils.loge(mContext.getActionBar().getCustomView().toString());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        mContext = (Activity) context;
        // 逻辑判断

        initPushSDK();
        checkLoginState();
        try {
            LogUtils.loge("SplashActivityPresenter create ");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void checkLoginState() {
//        Intent intent = new Intent(mContext, ContractPreviewActivity.class);
//        getView().startAC(intent);
//        getView().finishAc();
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
}

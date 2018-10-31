package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        //TODO 逻辑判断
        initPushSDK();
        checkLoginState();
        LogUtils.loge("SplashActivityPresenter create ");
    }

    private void checkLoginState() {
        try {
            RetrofitServiceHelper.INSTANCE.getBaseUrlType();
            String sessionID = RetrofitServiceHelper.INSTANCE.getSessionId();
            LogUtils.loge("sessionID = " + sessionID);
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
            LogUtils.loge("login Exception : " + e.getMessage());
            openLogin();
        }

    }

    //没有登录跳转登录界面
    private void openLogin() {
        Intent loginIntent = new Intent();
        loginIntent.setClass(mContext, LoginActivity.class);
        getView().startAC(loginIntent);
        getView().finishAc();
    }

    private void openMain(EventLoginData eventLoginData) {
        Intent mainIntent = new Intent();
        mainIntent.setClass(mContext, MainActivity.class);
        mainIntent.putExtra(EXTRA_EVENT_LOGIN_DATA, eventLoginData);
        getView().startAC(mainIntent);
        getView().finishAc();
    }

    private void initPushSDK() {
        PushManager.getInstance().initialize(SensoroCityApplication.getInstance(), SensoroPushService.class);
        // 注册 intentService 后 PushDemoReceiver 无效, sdk 会使用 DemoIntentService 传递数据,
        // AndroidManifest 对应保留一个即可(如果注册 DemoIntentService, 可以去掉 PushDemoReceiver, 如果注册了
        // IntentService, 必须在 AndroidManifest 中声明)
        PushManager.getInstance().registerPushIntentService(SensoroCityApplication.getInstance(),
                SensoroPushIntentService
                        .class);
        if (PushManager.getInstance().getClientid(SensoroCityApplication.getInstance()) == null) {
            PushManager.getInstance().turnOffPush(SensoroCityApplication.getInstance());
        } else {
            PushManager.getInstance().turnOnPush(SensoroCityApplication.getInstance());
        }
    }

    @Override
    public void onDestroy() {
        LogUtils.loge("SplashActivityPresenter onDestroy ");
    }
}

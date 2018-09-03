package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
            SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_SPLASH_LOGIN_DATA, Context
                    .MODE_PRIVATE);
            String phoneId = sp.getString(EXTRA_PHONE_ID, null);
            String userId = sp.getString(EXTRA_USER_ID, null);
            LogUtils.loge("phoneId = " + phoneId + ",userId = " + userId);
            if (TextUtils.isEmpty(phoneId) || TextUtils.isEmpty(userId)) {
                openLogin();
                return;
            }
            String userName = sp.getString(EXTRA_USER_NAME, null);
            String phone = sp.getString(EXTRA_PHONE, null);
            String roles = sp.getString(EXTRA_USER_ROLES, null);
            boolean isSupperAccount = sp.getBoolean(EXTRA_IS_SPECIFIC, false);
            boolean hasStation = sp.getBoolean(EXTRA_GRANTS_HAS_STATION, false);
            boolean hasContract = sp.getBoolean(EXTRA_GRANTS_HAS_CONTRACT, false);
            boolean hasScanLogin = sp.getBoolean(EXTRA_GRANTS_HAS_SCAN_LOGIN, false);
            final EventLoginData eventLoginData = new EventLoginData();
            eventLoginData.phoneId = phoneId;
            eventLoginData.userId = userId;
            eventLoginData.userName = userName;
            eventLoginData.phone = phone;
            eventLoginData.roles = roles;
            eventLoginData.isSupperAccount = isSupperAccount;
            eventLoginData.hasStation = hasStation;
            eventLoginData.hasContract = hasContract;
            eventLoginData.hasScanLogin = hasScanLogin;
            openMain(eventLoginData);
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
        mainIntent.putExtra("eventLoginData", eventLoginData);
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

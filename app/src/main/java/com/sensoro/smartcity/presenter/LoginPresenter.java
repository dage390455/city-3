package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.igexin.sdk.PushManager;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.model.EventData;
import com.sensoro.common.model.EventLoginData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.DeviceMergeTypesInfo;
import com.sensoro.common.server.bean.DeviceTypeStyles;
import com.sensoro.common.server.bean.MergeTypeStyles;
import com.sensoro.common.server.bean.SensorTypeStyles;
import com.sensoro.common.server.bean.UserInfo;
import com.sensoro.common.server.response.DevicesMergeTypesRsp;
import com.sensoro.common.server.response.LoginRsp;
import com.sensoro.smartcity.BuildConfig;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.AuthActivity;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.common.constant.Constants;
import com.sensoro.smartcity.factory.UserPermissionFactory;
import com.sensoro.smartcity.imainviews.ILoginView;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.LogUtils;
import com.tencent.bugly.beta.Beta;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class LoginPresenter extends BasePresenter<ILoginView> implements IOnCreate {
    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        Beta.checkUpgrade(false, false);
        readLoginData();
        initSeverUrl();
        ArrayList<String> nameList = new ArrayList<>();
        nameList.add(mContext.getString(R.string.sensoro_number));
        ArrayList<String> numberList = new ArrayList<>();
        numberList.add("(0570)2296646");
        numberList.add("(010)53876304");
        AppUtils.addToPhoneContact(mContext, nameList, numberList);
    }

    private void readLoginData() {
        String name = PreferencesHelper.getInstance().getLoginNamePwd().get(Constants.PREFERENCE_KEY_NAME);
        String pwd = PreferencesHelper.getInstance().getLoginNamePwd().get(Constants.PREFERENCE_KEY_PASSWORD);
        if (!TextUtils.isEmpty(name)) {
            getView().showAccountName(name);
        }
        if (!TextUtils.isEmpty(pwd)) {
            getView().showAccountPwd(pwd);
        }
    }


    private void initSeverUrl() {
        //去除从用户安装渠道设置登录环境
//        try {
//            ApplicationInfo appInfo = mContext.getPackageManager()
//                    .getApplicationInfo(mContext.getPackageName(),
//                            PackageManager.GET_META_DATA);
//            String msg = appInfo.metaData.getString("InstallChannel");
//            if (msg.equalsIgnoreCase("Mocha")) {
//                RetrofitServiceHelper.INSTANCE.saveBaseUrlType(true);
//            } else if (msg.equalsIgnoreCase("Master")) {
//                RetrofitServiceHelper.INSTANCE.saveBaseUrlType(false);
//            } else {
//                RetrofitServiceHelper.INSTANCE.saveBaseUrlType(true);
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
        getView().setLogButtonState(RetrofitServiceHelper.getInstance().getBaseUrlType());
//        SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_SCOPE, Context
//                .MODE_PRIVATE);
//        int urlType = 0;
//        try {
//            urlType = sp.getInt(PREFERENCE_KEY_URL, 0);
//            RetrofitServiceHelper.INSTANCE.saveBaseUrlType(urlType);
//            getView().setLogButtonState(urlType);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


    }

    public void saveScopeData(int which) {
        if (5 == which) {
            getView().showMyBaseUrlDialog(PreferencesHelper.getInstance().getMyBaseUrl());
        } else {
            RetrofitServiceHelper.getInstance().saveBaseUrlType(which);
            getView().setLogButtonState(which);
        }


    }

    public void login(final String account, final String pwd) {
        if (TextUtils.isEmpty(account)) {
            getView().toastShort(mContext.getResources().getString(R.string.tips_username_empty));
        } else if (TextUtils.isEmpty(pwd)) {
            getView().toastShort(mContext.getResources().getString(R.string.tips_login_pwd_empty));
        } else {
            final String phoneId = PushManager.getInstance().getClientid(SensoroCityApplication.getInstance());
            try {
                LogUtils.loge(this, "------phoneId = " + phoneId);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            getView().showProgressDialog();
            //
            RetrofitServiceHelper.getInstance().login(account, pwd, phoneId).subscribeOn
                    (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<LoginRsp>(this) {
                @Override
                public void onCompleted(LoginRsp loginRsp) {
                    String sessionID = loginRsp.getData().getSessionID();
                    String token = loginRsp.getData().getToken();
                    RetrofitServiceHelper.getInstance().saveSessionId(sessionID,token);
                    PreferencesHelper.getInstance().saveLoginNamePwd(account, pwd);
                    //
                    UserInfo userInfo = loginRsp.getData();
                    EventLoginData loginData = UserPermissionFactory.createLoginData(userInfo, phoneId);
                    if (loginData.needAuth) {
                        openNextActivity(loginData);
                        return;
                    }
                    getMergeType(loginData);
                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    getView().dismissProgressDialog();
                    getView().toastShort(errorMsg);
                }
            });
        }

    }

    private void getMergeType(final EventLoginData eventLoginData) {
        RetrofitServiceHelper.getInstance().getDevicesMergeTypes().subscribeOn(Schedulers.io()).doOnNext(new Consumer<DevicesMergeTypesRsp>() {
            @Override
            public void accept(DevicesMergeTypesRsp devicesMergeTypesRsp) throws Exception {
                DeviceMergeTypesInfo data = devicesMergeTypesRsp.getData();
                PreferencesHelper.getInstance().saveLocalDevicesMergeTypes(data);
                //测试信息
                if (BuildConfig.DEBUG) {
                    DeviceMergeTypesInfo.DeviceMergeTypeConfig config = data.getConfig();
                    Map<String, DeviceTypeStyles> deviceType = config.getDeviceType();
                    for (Map.Entry<String, DeviceTypeStyles> next : deviceType.entrySet()) {
                        String key = next.getKey();
                        DeviceTypeStyles value = next.getValue();
                        try {
                            LogUtils.loge("getDevicesMergeTypes---DeviceTypeStyles>> " + key + "," + value.toString());
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                    Map<String, MergeTypeStyles> mergeType = config.getMergeType();
                    for (Map.Entry<String, MergeTypeStyles> next : mergeType.entrySet()) {
                        String key = next.getKey();
                        MergeTypeStyles value = next.getValue();
                        try {
                            LogUtils.loge("getDevicesMergeTypes---MergeTypeStyles>> " + key + "," + value.toString());
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                    Map<String, SensorTypeStyles> sensorType = config.getSensorType();
                    for (Map.Entry<String, SensorTypeStyles> next : sensorType.entrySet()) {
                        String key = next.getKey();
                        SensorTypeStyles value = next.getValue();
                        try {
                            LogUtils.loge("getDevicesMergeTypes---SensorTypeStyles>> " + key + "," + value.toString());
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                    try {
                        LogUtils.loge("getDevicesMergeTypes--->> " + deviceType.size() + "," + mergeType.size() + "," + sensorType.size());
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }

        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DevicesMergeTypesRsp>(LoginPresenter.this) {
            @Override
            public void onCompleted(DevicesMergeTypesRsp devicesMergeTypesRsp) {
                openNextActivity(eventLoginData);
                try {
                    LogUtils.loge("DevicesMergeTypesRsp ....." + eventLoginData.toString());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }


            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }

    private void openNextActivity(EventLoginData eventLoginData) {
        Intent mainIntent = new Intent();
        mainIntent.putExtra(Constants.EXTRA_EVENT_LOGIN_DATA, eventLoginData);
        if (eventLoginData.needAuth) {
            mainIntent.setClass(mContext, AuthActivity.class);
            getView().startAC(mainIntent);
        } else {
            mainIntent.setClass(mContext, MainActivity.class);
            getView().startAC(mainIntent);
            getView().finishAc();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
//        Object data = eventData.data;
        switch (code) {
            case Constants.EVENT_DATA_CANCEL_AUTH:
                getView().dismissProgressDialog();
                break;
            case Constants.EVENT_DATA_AUTH_SUC:
                getView().dismissProgressDialog();
                getView().finishAc();
                break;
        }
//        LogUtils.loge(this, eventData.toString());
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }


    public void doSaveMyBaseUrl(String text) {
        PreferencesHelper.getInstance().saveMyBaseUrl(text);
        RetrofitServiceHelper.getInstance().saveBaseUrlType(5);
        getView().setLogButtonState(5);
        getView().dismissLoginDialog();
    }
}

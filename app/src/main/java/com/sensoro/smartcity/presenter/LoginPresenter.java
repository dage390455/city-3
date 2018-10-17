package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.igexin.sdk.PushManager;
import com.sensoro.smartcity.BuildConfig;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.AuthActivity;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.factory.MenuPageFactory;
import com.sensoro.smartcity.imainviews.ILoginView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.model.EventLoginData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceMergeTypesInfo;
import com.sensoro.smartcity.server.bean.DeviceTypeStyles;
import com.sensoro.smartcity.server.bean.GrantsInfo;
import com.sensoro.smartcity.server.bean.MergeTypeStyles;
import com.sensoro.smartcity.server.bean.SensorTypeStyles;
import com.sensoro.smartcity.server.bean.UserInfo;
import com.sensoro.smartcity.server.response.DevicesMergeTypesRsp;
import com.sensoro.smartcity.server.response.LoginRsp;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class LoginPresenter extends BasePresenter<ILoginView> implements Constants, IOnCreate {
    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        readLoginData();
        initSeverUrl();
    }

    private void readLoginData() {
        String name = PreferencesHelper.getInstance().getLoginNamePwd().get(PREFERENCE_KEY_NAME);
        String pwd = PreferencesHelper.getInstance().getLoginNamePwd().get(PREFERENCE_KEY_PASSWORD);
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
        getView().setLogButtonState(RetrofitServiceHelper.INSTANCE.getBaseUrlType());
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
        RetrofitServiceHelper.INSTANCE.saveBaseUrlType(which);
        getView().setLogButtonState(which);

    }

    public void login(final String account, final String pwd) {
        if (TextUtils.isEmpty(account)) {
            getView().toastShort(mContext.getResources().getString(R.string.tips_username_empty));
        } else if (TextUtils.isEmpty(pwd)) {
            getView().toastShort(mContext.getResources().getString(R.string.tips_login_pwd_empty));
        } else {
            final String phoneId = PushManager.getInstance().getClientid(SensoroCityApplication.getInstance());
            LogUtils.loge(this,"------phoneId = "+phoneId);
            getView().showProgressDialog();
            //
            RetrofitServiceHelper.INSTANCE.login(account, pwd, phoneId).subscribeOn
                    (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<LoginRsp>(this) {
                @Override
                public void onCompleted(LoginRsp loginRsp) {
                    String sessionID = loginRsp.getData().getSessionID();
                    RetrofitServiceHelper.INSTANCE.saveSessionId(sessionID);
                    PreferencesHelper.getInstance().saveLoginNamePwd(account, pwd);
                    //
                    UserInfo userInfo = loginRsp.getData();
                    final EventLoginData eventLoginData = new EventLoginData();
                    GrantsInfo grants = userInfo.getGrants();
                    //
                    eventLoginData.userId = userInfo.get_id();
                    eventLoginData.userName = userInfo.getNickname();
                    eventLoginData.phone = userInfo.getContacts();
                    eventLoginData.phoneId = phoneId;
                    LogUtils.loge("logPresenter", "phoneId = " + phoneId);
                    //TODO 处理Character信息
//                      mCharacter = userInfo.getCharacter();
                    String roles = userInfo.getRoles();
                    eventLoginData.roles = roles;
                    String isSpecific = userInfo.getIsSpecific();
                    eventLoginData.isSupperAccount = MenuPageFactory.getIsSupperAccount(isSpecific);
                    eventLoginData.hasStation = MenuPageFactory.getHasStationDeploy(grants);
                    eventLoginData.hasContract = MenuPageFactory.getHasContract(grants);
                    eventLoginData.hasScanLogin = MenuPageFactory.getHasScanLogin(grants);
                    eventLoginData.hasSubMerchant = MenuPageFactory.getHasSubMerchant(roles, isSpecific);
                    eventLoginData.hasInspection = MenuPageFactory.getHasInspection(grants);
                    //
                    UserInfo.Account account1 = userInfo.getAccount();
                    if (account1 != null) {
                        String id = account1.getId();
                        boolean totpEnable = account1.isTotpEnable();
                        LogUtils.loge("id = " + id + ",totpEnable = " + totpEnable);
                        if (totpEnable) {
                            eventLoginData.needAuth = true;
                            openMain(eventLoginData);
                            return;
                        }
                    }
                    getMergeType(eventLoginData);
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
        RetrofitServiceHelper.INSTANCE.getDevicesMergeTypes().subscribeOn(Schedulers.io()).doOnNext(new Action1<DevicesMergeTypesRsp>() {
            @Override
            public void call(DevicesMergeTypesRsp devicesMergeTypesRsp) {
                DeviceMergeTypesInfo data = devicesMergeTypesRsp.getData();
                PreferencesHelper.getInstance().saveLocalDevicesMergeTypes(data);
                //测试信息
                if (BuildConfig.DEBUG) {
                    DeviceMergeTypesInfo.DeviceMergeTypeConfig config = data.getConfig();
                    Map<String, DeviceTypeStyles> deviceType = config.getDeviceType();
                    for (Map.Entry<String, DeviceTypeStyles> next : deviceType.entrySet()) {
                        String key = next.getKey();
                        DeviceTypeStyles value = next.getValue();
                        LogUtils.loge("getDevicesMergeTypes---DeviceTypeStyles>> " + key + "," + value.toString());
                    }
                    Map<String, MergeTypeStyles> mergeType = config.getMergeType();
                    for (Map.Entry<String, MergeTypeStyles> next : mergeType.entrySet()) {
                        String key = next.getKey();
                        MergeTypeStyles value = next.getValue();
                        LogUtils.loge("getDevicesMergeTypes---MergeTypeStyles>> " + key + "," + value.toString());
                    }
                    Map<String, SensorTypeStyles> sensorType = config.getSensorType();
                    for (Map.Entry<String, SensorTypeStyles> next : sensorType.entrySet()) {
                        String key = next.getKey();
                        SensorTypeStyles value = next.getValue();
                        LogUtils.loge("getDevicesMergeTypes---SensorTypeStyles>> " + key + "," + value.toString());
                    }
                    LogUtils.loge("getDevicesMergeTypes--->> " + deviceType.size() + "," + mergeType.size() + "," + sensorType.size());
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DevicesMergeTypesRsp>(LoginPresenter.this) {
            @Override
            public void onCompleted(DevicesMergeTypesRsp devicesMergeTypesRsp) {
                openMain(eventLoginData);
                LogUtils.loge("DevicesMergeTypesRsp ....." + eventLoginData.toString());
            }


            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }

    private void openMain(EventLoginData eventLoginData) {
        Intent mainIntent = new Intent();
        mainIntent.putExtra(EXTRA_EVENT_LOGIN_DATA, eventLoginData);
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
        //TODO 可以修改以此种方式传递，方便管理
        int code = eventData.code;
//        Object data = eventData.data;
        if (code == EVENT_DATA_CANCEL_AUTH) {
            getView().dismissProgressDialog();
        } else if (code == EVENT_DATA_AUTH_SUC) {
            getView().dismissProgressDialog();
            getView().finishAc();
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


}

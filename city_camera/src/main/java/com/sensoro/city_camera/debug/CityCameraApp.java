package com.sensoro.city_camera.debug;

import com.sensoro.common.base.BaseApplication;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.UserInfo;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.utils.LogUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CityCameraApp extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        login();
    }

    @Override
    protected void onMyApplicationResumed() {

    }

    @Override
    protected void onMyApplicationPaused() {

    }

    private void login() {
        PreferencesHelper.getInstance().getSessionId();
        PreferencesHelper.getInstance().getSessionToken();
        PreferencesHelper.getInstance().saveMyBaseUrl("city-dev");
        RetrofitServiceHelper.getInstance().saveBaseUrlType(5);
        RetrofitServiceHelper.getInstance().getBaseUrlType();
        RetrofitServiceHelper.getInstance().login("15110041945", "aa1111", "").subscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<UserInfo>>(null) {
            @Override
            public void onCompleted(ResponseResult<UserInfo> loginRsp) {
                String sessionID = loginRsp.getData().getSessionID();
                String token = loginRsp.getData().getToken();
                RetrofitServiceHelper.getInstance().saveSessionId(sessionID, token);
                //
//                UserInfo userInfo = loginRsp.getData();
//                EventLoginData loginData = UserPermissionFactory.createLoginData(userInfo, phoneId);
//                if (loginData.needAuth) {
//                    openNextActivity(loginData);
//                    return;
//                }
//                getMergeType(loginData);
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                try {
                    LogUtils.loge(errorMsg);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
//                getView().dismissProgressDialog();
//                getView().toastShort(errorMsg);
            }
        });
    }
}

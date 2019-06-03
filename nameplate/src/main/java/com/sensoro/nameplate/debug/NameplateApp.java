package com.sensoro.nameplate.debug;

import com.sensoro.common.base.BaseApplication;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.response.LoginRsp;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NameplateApp extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        login();
    }

    private void login() {
        RetrofitServiceHelper.getInstance().login("15110041945", "aa1111", "").subscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<LoginRsp>(null) {
            @Override
            public void onCompleted(LoginRsp loginRsp) {
                String sessionID = loginRsp.getData().getSessionID();
                RetrofitServiceHelper.getInstance().saveSessionId(sessionID);
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
//                getView().dismissProgressDialog();
//                getView().toastShort(errorMsg);
            }
        });
    }
}

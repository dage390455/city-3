package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IAuthActivityViewTest;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.model.EventLoginData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.response.AuthRsp;

import org.greenrobot.eventbus.EventBus;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AuthActivityPresenterTest extends BasePresenter<IAuthActivityViewTest> implements Constants {
    private Activity mContext;
    private EventLoginData mEventLoginData;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        mEventLoginData = (EventLoginData) mContext.getIntent().getSerializableExtra("eventLoginData");
    }

    @Override
    public void onDestroy() {

    }

    public void doAuthCheck(String code) {
            doSecondAuth(code);
    }

    private void doSecondAuth(String code) {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.doubleCheck(code).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<AuthRsp>(this) {

            @Override
            public void onCompleted(AuthRsp authRsp) {
                if (authRsp.isData()) {
                    saveLoginDataOpenMain(mEventLoginData);
                } else {
                    getView().dismissProgressDialog();
                    getView().toastShort("二次验证失败");
                }
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().toastShort(errorMsg);
                getView().dismissProgressDialog();
            }
        });
    }

    private void saveLoginDataOpenMain(EventLoginData eventLoginData) {
        //
        EventData eventData = new EventData();
        eventData.code = EVENT_DATA_AUTH_SUC;
        EventBus.getDefault().post(eventData);
        Intent mainIntent = new Intent();
        mainIntent.setClass(mContext, MainActivity.class);
        mainIntent.putExtra("eventLoginData", eventLoginData);
        getView().startAC(mainIntent);
        getView().finishAc();
    }

    public void close() {
        EventData eventData = new EventData();
        eventData.code = EVENT_DATA_CANCEL_AUTH;
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }
}

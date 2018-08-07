package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IScanLoginResultActivityView;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.response.ResponseBase;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ScanLoginResultActivityPresenter extends BasePresenter<IScanLoginResultActivityView> {
    private String qrcodeId;
    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        qrcodeId = mContext.getIntent().getStringExtra("qrcodeId");
    }

    public void doLogin() {
        if (!TextUtils.isEmpty(qrcodeId)) {
            getView().showProgressDialog();
            RetrofitServiceHelper.INSTANCE.scanLoginIn(qrcodeId).subscribeOn(Schedulers.io()).observeOn
                    (AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseBase>() {
                @Override
                public void onCompleted() {
                    getView().dismissProgressDialog();
                    getView().finishAc();
                }

                @Override
                public void onNext(ResponseBase responseBase) {
                    if (responseBase.getErrcode() == 0) {
//                        getView().toastShort("登录成功");
                    } else {
                        getView().toastShort("登录失败请重新扫描");
                    }
                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    getView().dismissProgressDialog();
                    getView().toastShort(errorMsg);
                }
            });
        }
    }

    public void doCancel() {
        if (!TextUtils.isEmpty(qrcodeId)) {
            getView().showProgressDialog();
            RetrofitServiceHelper.INSTANCE.scanLoginCancel(qrcodeId).subscribeOn(Schedulers.io()).observeOn
                    (AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseBase>() {
                @Override
                public void onCompleted() {
                    getView().dismissProgressDialog();
                    getView().finishAc();
                }

                @Override
                public void onNext(ResponseBase responseBase) {
//                    if (responseBase.getErrcode() == 0) {
//                        getView().toastShort("登录成功");
//                    } else {
//                        getView().toastShort("登录失败请重新扫描");
//                        getView().finishAc();
//                    }
                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    getView().dismissProgressDialog();
                    getView().toastShort(errorMsg);
                }
            });
        }
    }

    @Override
    public void onDestroy() {

    }
}

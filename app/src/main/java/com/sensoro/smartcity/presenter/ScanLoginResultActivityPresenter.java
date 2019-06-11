package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.smartcity.imainviews.IScanLoginResultActivityView;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.response.ResponseBase;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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
            RetrofitServiceHelper.getInstance().scanLoginIn(qrcodeId).subscribeOn(Schedulers.io()).observeOn
                    (AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseBase>(this) {
                @Override
                public void onCompleted(ResponseBase responseBase) {
                    if (responseBase.getErrcode() == 0) {
                        EventData eventData = new EventData();
                        eventData.code = Constants.EVENT_DATA_SCAN_LOGIN_SUCCESS;
                        EventBus.getDefault().post(eventData);
                    } else {
                        getView().toastShort(mContext.getString(R.string.login_failed_please_rescan));
                    }
                    getView().dismissProgressDialog();
                    getView().finishAc();
                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    getView().dismissProgressDialog();
                    getView().toastShort(errorMsg);
                    getView().finishAc();
                }
            });
        }
    }

    public void doCancel() {
        if (!TextUtils.isEmpty(qrcodeId)) {
            getView().showProgressDialog();
            RetrofitServiceHelper.getInstance().scanLoginCancel(qrcodeId).subscribeOn(Schedulers.io()).observeOn
                    (AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseBase>(this) {

                @Override
                public void onCompleted(ResponseBase responseBase) {
                    //                    if (responseBase.getErrcode() == 0) {
//                        getView().toastShort("登录成功");
//                    } else {
//                        getView().toastShort("登录失败请重新扫描");
//                        getView().finishAc();
//                    }
                    getView().dismissProgressDialog();
                    getView().finishAc();
                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    getView().dismissProgressDialog();
                    getView().toastShort(errorMsg);
                    getView().finishAc();
                }
            });
        }
    }

    @Override
    public void onDestroy() {

    }
}

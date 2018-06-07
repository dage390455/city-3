package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.DeployActivity;
import com.sensoro.smartcity.activity.DeployResultActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployManualActivityView;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.response.CityObserver;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DeployManualActivityPresenter extends BasePresenter<IDeployManualActivityView> implements Constants {
    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
    }

    public void clickClose() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CONTAINS_DATA, false);
        getView().setIntentResult(RESULT_CODE_DEPLOY, intent);
        getView().finishAc();
    }

    public void clickNext(String text) {
        if (!TextUtils.isEmpty(text) && text.length() == 16) {
//            Intent intent = new Intent(this, DeployActivity.class);
//            intent.putExtra(EXTRA_SENSOR_SN, contentEditText.getText().toString().toUpperCase());
//            startActivity(intent);
            requestData(text);
        } else {
            getView().toastShort("请输入正确的SN,SN为16个字符");
        }
    }

    private void requestData(String scanSerialNumber) {

        if (TextUtils.isEmpty(scanSerialNumber)) {
            getView().toastShort(mContext.getResources().getString(R.string.invalid_qr_code));
        } else {
            getView().showProgressDialog();
            RetrofitServiceHelper.INSTANCE.getDeviceDetailInfoList(scanSerialNumber.toUpperCase(), null, 1)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>() {


                @Override
                public void onCompleted() {
                    getView().dismissProgressDialog();
                }

                @Override
                public void onNext(DeviceInfoListRsp deviceInfoListRsp) {
                    refresh(deviceInfoListRsp);
                }

                @Override
                public void onErrorMsg(String errorMsg) {
                    getView().dismissProgressDialog();
                    getView().toastShort(errorMsg);
                }
            });

        }
    }

    private void refresh(DeviceInfoListRsp response) {
        try {
            Intent intent = new Intent();
            if (response.getData().size() > 0) {

                intent.setClass(mContext, DeployActivity.class);
                intent.putExtra(EXTRA_DEVICE_INFO, response.getData().get(0));
                intent.putExtra("uid", mContext.getIntent().getStringExtra("uid"));
                getView().startACForResult(intent, REQUEST_CODE_DEPLOY);
            } else {
                intent.setClass(mContext, DeployResultActivity.class);
                intent.putExtra(EXTRA_SENSOR_RESULT, -1);
                getView().startACForResult(intent, REQUEST_CODE_DEPLOY);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        //数据回传
        if (resultCode == RESULT_CODE_MAP) {
            getView().setIntentResult(RESULT_CODE_MAP, data);
        }
        getView().finishAc();
    }
}

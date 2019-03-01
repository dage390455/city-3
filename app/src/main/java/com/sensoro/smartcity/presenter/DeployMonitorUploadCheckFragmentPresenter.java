package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.smartcity.activity.DeployMonitorNameAddressActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IDeployMonitorUploadCheckFragmentView;

import static com.sensoro.smartcity.constant.Constants.EXTRA_DEPLOY_ORIGIN_NAME_ADDRESS;
import static com.sensoro.smartcity.constant.Constants.EXTRA_DEPLOY_TO_SN;
import static com.sensoro.smartcity.constant.Constants.EXTRA_DEPLOY_TYPE;
import static com.sensoro.smartcity.constant.Constants.EXTRA_SETTING_NAME_ADDRESS;

public class DeployMonitorUploadCheckFragmentPresenter extends BasePresenter<IDeployMonitorUploadCheckFragmentView> {
    private Activity mActivity;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
    }

    @Override
    public void onDestroy() {

    }

    public void doNameAddress() {
        Intent intent = new Intent(mActivity, DeployMonitorNameAddressActivity.class);
        if (!TextUtils.isEmpty(deployAnalyzerModel.nameAndAddress)) {
            intent.putExtra(EXTRA_SETTING_NAME_ADDRESS, deployAnalyzerModel.nameAndAddress);
        }
        intent.putExtra(EXTRA_DEPLOY_TO_SN, deployAnalyzerModel.sn);
        intent.putExtra(EXTRA_DEPLOY_TYPE, deployAnalyzerModel.deployType);
        if (!TextUtils.isEmpty(originName)) {
            intent.putExtra(EXTRA_DEPLOY_ORIGIN_NAME_ADDRESS, originName);
        }
        intent.putExtra(EXTRA_DEPLOY_TYPE, deployAnalyzerModel.deployType);
        getView().startAC(intent);
    }
}

package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IDeployRecordConfigCommonElectActivityView;

public class DeployRecordConfigCommonElectActivityPresenter extends BasePresenter<IDeployRecordConfigCommonElectActivityView> {
    private Activity mActivity;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        refreshUI();
    }


    private void refreshUI() {
    }


    @Override
    public void onDestroy() {

    }
}

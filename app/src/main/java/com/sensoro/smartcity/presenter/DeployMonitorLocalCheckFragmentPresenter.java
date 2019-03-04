package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;

import com.sensoro.smartcity.activity.DeployMonitorCheckActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IDeployMonitorLocalCheckFragmentView;

public class DeployMonitorLocalCheckFragmentPresenter extends BasePresenter<IDeployMonitorLocalCheckFragmentView> {
    private Activity mActivity;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
    }

    @Override
    public void onDestroy() {

    }

    public void doUploadDeployMonitorInfo() {
        DeployMonitorCheckActivityPresenter.deployAnalyzerModel.address = "1234";
        if (mActivity instanceof DeployMonitorCheckActivity) {
            ((DeployMonitorCheckActivity) mActivity).setDeployMonitorStep(2);
        }
    }
}

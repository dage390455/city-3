package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;

import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IDeployMonitorCheckActivityView;

public class DeployMonitorCheckActivityPresenter extends BasePresenter<IDeployMonitorCheckActivityView> {
    private Activity mActivity;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        getView().showDeployMonitorLocalCheckFragment();
    }

    @Override
    public void onDestroy() {

    }
}

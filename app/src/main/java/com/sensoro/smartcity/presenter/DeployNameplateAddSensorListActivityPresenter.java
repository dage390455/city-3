package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;

import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IDeployNameplateAddSensorListActivityView;

public class DeployNameplateAddSensorListActivityPresenter extends BasePresenter<IDeployNameplateAddSensorListActivityView> {
    private Activity mActivity;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
    }

    @Override
    public void onDestroy() {

    }
}

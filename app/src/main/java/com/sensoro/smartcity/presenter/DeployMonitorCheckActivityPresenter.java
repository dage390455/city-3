package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;

import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployMonitorCheckActivityView;
import com.sensoro.smartcity.model.DeployAnalyzerModel;

public class DeployMonitorCheckActivityPresenter extends BasePresenter<IDeployMonitorCheckActivityView> implements Constants {
    private Activity mActivity;
    public volatile static DeployAnalyzerModel deployAnalyzerModel;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        deployAnalyzerModel = (DeployAnalyzerModel) mActivity.getIntent().getSerializableExtra(EXTRA_DEPLOY_ANALYZER_MODEL);
        getView().setDeployMonitorStep(1);
        deployAnalyzerModel =new DeployAnalyzerModel();
    }

    @Override
    public void onDestroy() {
        deployAnalyzerModel = null;
    }

}

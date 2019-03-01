package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployMonitorCheckActivityView;
import com.sensoro.smartcity.model.DeployAnalyzerModel;

public class DeployMonitorCheckActivityPresenter extends BasePresenter<IDeployMonitorCheckActivityView> implements Constants {
    private Activity mActivity;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        DeployAnalyzerModel deployAnalyzerModel = (DeployAnalyzerModel) mActivity.getIntent().getSerializableExtra(EXTRA_DEPLOY_ANALYZER_MODEL);
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_DEPLOY_ANALYZER_MODEL, deployAnalyzerModel);
        getView().deployMonitorLocalCheckFragmentSetArguments(bundle);
        getView().deployMonitorUploadCheckFragmentSetArguments(bundle);
    }

    @Override
    public void onDestroy() {

    }
}

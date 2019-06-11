package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployMonitorCheckActivityView;
import com.sensoro.common.model.DeployAnalyzerModel;

import java.io.Serializable;

public class DeployMonitorCheckActivityPresenter extends BasePresenter<IDeployMonitorCheckActivityView>  {
    private Activity mActivity;
    public volatile DeployAnalyzerModel deployAnalyzerModel;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        deployAnalyzerModel = (DeployAnalyzerModel) mActivity.getIntent().getSerializableExtra(Constants.EXTRA_DEPLOY_ANALYZER_MODEL);
    }

    @Override
    public void onDestroy() {
        deployAnalyzerModel = null;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Serializable savedInstanceStateSerializable = savedInstanceState.getSerializable(Constants.EXTRA_DEPLOY_ANALYZER_MODEL);
            if (savedInstanceStateSerializable instanceof DeployAnalyzerModel) {
                deployAnalyzerModel = (DeployAnalyzerModel) savedInstanceStateSerializable;
            }
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        if (deployAnalyzerModel != null) {
            outState.putSerializable(Constants.EXTRA_DEPLOY_ANALYZER_MODEL, deployAnalyzerModel);
        }
    }
}

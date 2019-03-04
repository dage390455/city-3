package com.sensoro.smartcity.fragment;

import android.app.Activity;
import android.content.Context;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IDeployMonitorUploadCheckFragmentView;
import com.sensoro.smartcity.presenter.DeployMonitorCheckActivityPresenter;
import com.sensoro.smartcity.presenter.DeployMonitorUploadCheckFragmentPresenter;
import com.sensoro.smartcity.util.LogUtils;

public class DeployMonitorUploadCheckFragment extends BaseFragment<IDeployMonitorUploadCheckFragmentView, DeployMonitorUploadCheckFragmentPresenter> implements IDeployMonitorUploadCheckFragmentView {
    private Activity mActivity;

    @Override
    protected void initData(Context activity) {
        mActivity = (Activity) activity;
    }

    @Override
    protected int initRootViewId() {
        return R.layout.fragment_deploy_monitor_upload_check;
    }

    @Override
    protected DeployMonitorUploadCheckFragmentPresenter createPresenter() {
        return new DeployMonitorUploadCheckFragmentPresenter();
    }

    @Override
    public void onFragmentStart() {

        try {
            LogUtils.loge("---->>>" + DeployMonitorCheckActivityPresenter.deployAnalyzerModel.address);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void onFragmentStop() {

    }
}

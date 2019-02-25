package com.sensoro.smartcity.fragment;

import android.content.Context;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IDeployMonitorUploadCheckFragmentView;
import com.sensoro.smartcity.presenter.DeployMonitorUploadCheckFragmentPresenter;

public class DeployMonitorUploadCheckFragment extends BaseFragment<IDeployMonitorUploadCheckFragmentView, DeployMonitorUploadCheckFragmentPresenter> implements IDeployMonitorUploadCheckFragmentView {
    @Override
    protected void initData(Context activity) {

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

    }

    @Override
    public void onFragmentStop() {

    }
}

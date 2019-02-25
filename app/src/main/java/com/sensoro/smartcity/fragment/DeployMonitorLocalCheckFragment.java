package com.sensoro.smartcity.fragment;

import android.content.Context;

import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IDeployMonitorLocalCheckFragmentView;
import com.sensoro.smartcity.presenter.DeployMonitorLocalCheckFragmentPresenter;

public class DeployMonitorLocalCheckFragment extends BaseFragment<IDeployMonitorLocalCheckFragmentView, DeployMonitorLocalCheckFragmentPresenter> implements IDeployMonitorLocalCheckFragmentView {
    @Override
    protected void initData(Context activity) {

    }

    @Override
    protected int initRootViewId() {
        return com.sensoro.smartcity.R.layout.fragment_deploy_monitor_local_check;
    }

    @Override
    protected DeployMonitorLocalCheckFragmentPresenter createPresenter() {
        return new DeployMonitorLocalCheckFragmentPresenter();
    }

    @Override
    public void onFragmentStart() {

    }

    @Override
    public void onFragmentStop() {

    }
}

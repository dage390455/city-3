package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IDeployMonitorLocalCheckFragmentView;
import com.sensoro.smartcity.presenter.DeployMonitorLocalCheckFragmentPresenter;

import butterknife.BindView;
import butterknife.OnClick;

public class DeployMonitorLocalCheckFragment extends BaseFragment<IDeployMonitorLocalCheckFragmentView, DeployMonitorLocalCheckFragmentPresenter> implements IDeployMonitorLocalCheckFragmentView {
    @BindView(R.id.tv_test_check)
    TextView tvTestCheck;

    @Override
    protected void initData(Context activity) {
        mPresenter.initData(activity);
    }

    @Override
    protected int initRootViewId() {
        return R.layout.fragment_deploy_monitor_local_check;
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

    @OnClick(R.id.tv_test_check)
    public void onViewClicked() {
        mPresenter.doUploadDeployMonitorInfo();
    }
}

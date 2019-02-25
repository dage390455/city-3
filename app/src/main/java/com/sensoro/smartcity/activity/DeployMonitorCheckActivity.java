package com.sensoro.smartcity.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.fragment.DeployMonitorLocalCheckFragment;
import com.sensoro.smartcity.fragment.DeployMonitorUploadCheckFragment;
import com.sensoro.smartcity.imainviews.IDeployMonitorCheckActivityView;
import com.sensoro.smartcity.presenter.DeployMonitorCheckActivityPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeployMonitorCheckActivity extends BaseActivity<IDeployMonitorCheckActivityView, DeployMonitorCheckActivityPresenter> implements IDeployMonitorCheckActivityView {
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_divider)
    View includeLineDivider;
    @BindView(R.id.fl_deploy_check_root)
    FrameLayout flDeployCheckRoot;
    @BindView(R.id.tv_deploy_monitor_local_check)
    TextView tvDeployMonitorLocalCheck;
    @BindView(R.id.tv_deploy_monitor_upload_check)
    TextView tvDeployMonitorUploadCheck;
    private DeployMonitorLocalCheckFragment mDeployMonitorLocalCheckFragment;
    private DeployMonitorUploadCheckFragment mDeployMonitorUploadCheckFragment;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deploy_monitor_check);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        includeTextTitleTvTitle.setText(mActivity.getString(R.string.device_deployment));
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        includeLineDivider.setVisibility(View.GONE);
        mDeployMonitorLocalCheckFragment = new DeployMonitorLocalCheckFragment();
        mDeployMonitorUploadCheckFragment = new DeployMonitorUploadCheckFragment();

    }

    @Override
    protected DeployMonitorCheckActivityPresenter createPresenter() {
        return new DeployMonitorCheckActivityPresenter();
    }

    @Override
    public void showDeployMonitorLocalCheckFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mDeployMonitorLocalCheckFragment.isAdded()) {
            fragmentTransaction.hide(mDeployMonitorUploadCheckFragment).show(mDeployMonitorLocalCheckFragment).commit();
        } else {
            fragmentTransaction.add(R.id.fl_deploy_check_root, mDeployMonitorLocalCheckFragment).hide(mDeployMonitorUploadCheckFragment).show(mDeployMonitorLocalCheckFragment).commit();
        }
    }

    @Override
    public void showDeployMonitorUploadCheckFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mDeployMonitorUploadCheckFragment.isAdded()) {
            fragmentTransaction.hide(mDeployMonitorLocalCheckFragment).show(mDeployMonitorUploadCheckFragment).commit();
        } else {
            fragmentTransaction.add(R.id.fl_deploy_check_root, mDeployMonitorUploadCheckFragment).hide(mDeployMonitorLocalCheckFragment).show(mDeployMonitorUploadCheckFragment).commit();
        }
    }

    @Override
    public void deployMonitorLocalCheckFragmentSetArguments(Bundle bundle) {
        mDeployMonitorLocalCheckFragment.setArguments(bundle);
    }

    @Override
    public void deployMonitorUploadCheckFragmentSetArguments(Bundle bundle) {
        mDeployMonitorUploadCheckFragment.setArguments(bundle);
    }

    @OnClick({R.id.tv_deploy_monitor_local_check, R.id.tv_deploy_monitor_upload_check})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_deploy_monitor_local_check:
                showDeployMonitorLocalCheckFragment();
                break;
            case R.id.tv_deploy_monitor_upload_check:
                showDeployMonitorUploadCheckFragment();
                break;
        }
    }
}

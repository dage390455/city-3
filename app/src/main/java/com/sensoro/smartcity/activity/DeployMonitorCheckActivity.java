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
    @BindView(R.id.tv_include_title_left_text)
    TextView tvIncludeTitleLeftText;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_divider)
    View includeLineDivider;
    @BindView(R.id.fl_deploy_check_root)
    FrameLayout flDeployCheckRoot;
    @BindView(R.id.tv_deploy_check_circle_step_1)
    TextView tvDeployCheckCircleStep1;
    @BindView(R.id.tv_deploy_check_title_step_1)
    TextView tvDeployCheckTitleStep1;
    @BindView(R.id.tv_deploy_check_circle_step_2)
    TextView tvDeployCheckCircleStep2;
    @BindView(R.id.tv_deploy_check_title_step_2)
    TextView tvDeployCheckTitleStep2;
    @BindView(R.id.tv_deploy_check_circle_step_3)
    TextView tvDeployCheckCircleStep3;
    @BindView(R.id.tv_deploy_check_title_step_3)
    TextView tvDeployCheckTitleStep3;
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

    @Override
    public void setDeployMonitorStep(int step) {
        switch (step) {
            case 1:
                //安装检测
                tvDeployCheckCircleStep1.setBackgroundResource(R.drawable.shape_deploy_check_select);
                tvDeployCheckCircleStep1.setTextColor(mActivity.getResources().getColor(R.color.white));
                tvDeployCheckTitleStep1.setTextColor(mActivity.getResources().getColor(R.color.c_252525));
                tvDeployCheckCircleStep2.setBackgroundResource(R.drawable.shape_deploy_check_un_select);
                tvDeployCheckCircleStep2.setTextColor(mActivity.getResources().getColor(R.color.c_a6a6a6));
                tvDeployCheckTitleStep2.setTextColor(mActivity.getResources().getColor(R.color.c_a6a6a6));
                tvDeployCheckCircleStep3.setBackgroundResource(R.drawable.shape_deploy_check_un_select);
                tvDeployCheckCircleStep3.setTextColor(mActivity.getResources().getColor(R.color.c_a6a6a6));
                tvDeployCheckTitleStep3.setTextColor(mActivity.getResources().getColor(R.color.c_a6a6a6));
                tvIncludeTitleLeftText.setVisibility(View.GONE);
                showDeployMonitorLocalCheckFragment();
                break;
            case 2:
                //完善信息
                tvDeployCheckCircleStep1.setBackgroundResource(R.drawable.shape_deploy_check_select);
                tvDeployCheckCircleStep1.setTextColor(mActivity.getResources().getColor(R.color.white));
                tvDeployCheckTitleStep1.setTextColor(mActivity.getResources().getColor(R.color.c_252525));
                tvDeployCheckCircleStep2.setBackgroundResource(R.drawable.shape_deploy_check_select);
                tvDeployCheckCircleStep2.setTextColor(mActivity.getResources().getColor(R.color.white));
                tvDeployCheckTitleStep2.setTextColor(mActivity.getResources().getColor(R.color.c_252525));
                tvDeployCheckCircleStep3.setBackgroundResource(R.drawable.shape_deploy_check_un_select);
                tvDeployCheckCircleStep3.setTextColor(mActivity.getResources().getColor(R.color.c_a6a6a6));
                tvDeployCheckTitleStep3.setTextColor(mActivity.getResources().getColor(R.color.c_a6a6a6));
                showDeployMonitorUploadCheckFragment();
                tvIncludeTitleLeftText.setVisibility(View.VISIBLE);
                break;
            case 3:
                //部署结果
                tvDeployCheckCircleStep1.setBackgroundResource(R.drawable.shape_deploy_check_select);
                tvDeployCheckCircleStep1.setTextColor(mActivity.getResources().getColor(R.color.white));
                tvDeployCheckTitleStep1.setTextColor(mActivity.getResources().getColor(R.color.c_252525));
                tvDeployCheckCircleStep2.setBackgroundResource(R.drawable.shape_deploy_check_select);
                tvDeployCheckCircleStep2.setTextColor(mActivity.getResources().getColor(R.color.white));
                tvDeployCheckTitleStep2.setTextColor(mActivity.getResources().getColor(R.color.c_252525));
                tvDeployCheckCircleStep3.setBackgroundResource(R.drawable.shape_deploy_check_select);
                tvDeployCheckCircleStep3.setTextColor(mActivity.getResources().getColor(R.color.white));
                tvDeployCheckTitleStep3.setTextColor(mActivity.getResources().getColor(R.color.c_252525));
                tvIncludeTitleLeftText.setVisibility(View.VISIBLE);
                break;
            default:
                index = 1;
                return;
        }
        index = step;
    }

    private int index = 1;

    @OnClick({R.id.tv_include_title_left_text, R.id.include_text_title_imv_arrows_left})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
            case R.id.tv_include_title_left_text:
                doPreStep();
                break;
        }
    }

    private void doPreStep() {
        if (index == 1) {
            finish();
        } else if (index == 2) {
            index--;
            setDeployMonitorStep(index);
        } else {
            index = 1;
            setDeployMonitorStep(index);
        }
    }

    @Override
    public void onBackPressed() {
        doPreStep();
    }
}

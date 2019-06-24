package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.widgets.BoldTextView;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.imainviews.IDeployRecordConfigCommonElectActivityView;
import com.sensoro.smartcity.presenter.DeployRecordConfigCommonElectActivityPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = ARouterConstants.ACTIVITY_DEPLOY_RECORD_CONFIG_COMMON_ELECT_ACTIVITY)
public class DeployRecordConfigCommonElectActivity extends BaseActivity<IDeployRecordConfigCommonElectActivityView, DeployRecordConfigCommonElectActivityPresenter>
        implements IDeployRecordConfigCommonElectActivityView {


    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    BoldTextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R.id.include_text_title_cl_root)
    ConstraintLayout includeTextTitleClRoot;
    @BindView(R.id.tv_config_air_rated_current)
    TextView tvConfigAirRatedCurrent;
    @BindView(R.id.tv_config_material)
    TextView tvConfigMaterial;
    @BindView(R.id.tv_config_diameter)
    TextView tvConfigDiameter;
    @BindView(R.id.tv_config_actual_over_current_threshold)
    TextView tvConfigActualOverCurrentThreshold;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.actvity_deploy_record_config_common_elect);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        includeTextTitleTvTitle.setVisibility(View.VISIBLE);
        includeTextTitleTvTitle.setText(R.string.deploy_detail);
    }


    @Override
    protected DeployRecordConfigCommonElectActivityPresenter createPresenter() {
        return new DeployRecordConfigCommonElectActivityPresenter();
    }

    @Override
    public void startAC(Intent intent) {
        mActivity.startActivity(intent);
    }

    @Override
    public void finishAc() {
        finish();
    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {
        mActivity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void setIntentResult(int resultCode) {

    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {

    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void dismissProgressDialog() {

    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setConfigAirRatedCurrentValue(String value) {
        tvConfigAirRatedCurrent.setText(value);
    }

    @Override
    public void setConfigMaterial(String material) {
        tvConfigMaterial.setText(material);
    }

    @Override
    public void setConfigDiameter(String diameter) {
        tvConfigDiameter.setText(diameter);
    }


    @Override
    public void setConfigActualOverCurrentThreshold(String value) {
        tvConfigActualOverCurrentThreshold.setText(value);
    }


    @OnClick(R.id.include_text_title_imv_arrows_left)
    public void onViewClicked() {
        finishAc();
    }
}
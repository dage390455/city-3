package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.widgets.BoldTextView;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.imainviews.IDeployRecordConfigThreePhaseElectActivityView;
import com.sensoro.smartcity.presenter.DeployRecordConfigThreePhaseElectActivityPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeployRecordConfigThreePhaseElectActivity extends BaseActivity<IDeployRecordConfigThreePhaseElectActivityView, DeployRecordConfigThreePhaseElectActivityPresenter>
        implements IDeployRecordConfigThreePhaseElectActivityView {


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
    @BindView(R.id.tv_config_input)
    TextView tvConfigInput;
    @BindView(R.id.tv_config_output)
    TextView tvConfigOutput;
    @BindView(R.id.tv_config_actual_over_current_threshold)
    TextView tvConfigActualOverCurrentThreshold;
    @BindView(R.id.tv_config_recommend_trans)
    TextView tvConfigRecommendTrans;
    @BindView(R.id.tv_config_actual_trans)
    TextView tvConfigActualTrans;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.actvity_deploy_record_config_three_phase_elect);
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
    protected DeployRecordConfigThreePhaseElectActivityPresenter createPresenter() {
        return new DeployRecordConfigThreePhaseElectActivityPresenter();
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
    public void setConfigInput(String input) {
        tvConfigInput.setText(input);
    }

    @Override
    public void setConfigOutput(String output) {
        tvConfigOutput.setText(output);
    }

    @Override
    public void setConfigActualOverCurrentThreshold(String value) {
        tvConfigActualOverCurrentThreshold.setText(value);
    }

    @Override
    public void setConfigRecommendTrans(String value) {
        tvConfigRecommendTrans.setText(value);
    }

    @Override
    public void setConfigActualTrans(String value) {
        tvConfigActualTrans.setText(value);
    }
}
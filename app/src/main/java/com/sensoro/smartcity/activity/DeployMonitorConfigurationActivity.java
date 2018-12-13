package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IDeployMonitorConfigurationView;
import com.sensoro.smartcity.presenter.DeployMonitorConfigurationPresenter;
import com.sensoro.smartcity.widget.dialog.BleConfigurationDialogUtils;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeployMonitorConfigurationActivity extends BaseActivity<IDeployMonitorConfigurationView, DeployMonitorConfigurationPresenter>
        implements IDeployMonitorConfigurationView {
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R.id.ac_deploy_configuration_et_enter)
    EditText acDeployConfigurationEtEnter;
    @BindView(R.id.ac_deploy_configuration_et_root)
    LinearLayout acDeployConfigurationEtRoot;
    @BindView(R.id.ac_deploy_configuration_tv_enter_tip)
    TextView acDeployConfigurationTvEnterTip;
    @BindView(R.id.ac_deploy_configuration_tv_configuration)
    TextView acDeployConfigurationTvConfiguration;
    @BindView(R.id.ac_deploy_configuration_tv_near)
    TextView acDeployConfigurationTvNear;
    private BleConfigurationDialogUtils bleConfigDialog;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deploy_configuration);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);

    }

    private void initView() {
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        includeTextTitleTvTitle.setText(mActivity.getString(R.string.initial_configuration));
        acDeployConfigurationEtEnter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateBtnStatus(s.toString().length() > 0 && acDeployConfigurationTvNear.getVisibility() == View.VISIBLE);
            }
        });
        bleConfigDialog = new BleConfigurationDialogUtils(mActivity, mActivity.getString(R.string.connecting));
    }


    @Override
    protected DeployMonitorConfigurationPresenter createPresenter() {
        return new DeployMonitorConfigurationPresenter();
    }

    @Override
    public void startAC(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void finishAc() {
        finish();
    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {

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
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }


    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.ac_deploy_configuration_tv_configuration})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ac_deploy_configuration_tv_configuration:
                mPresenter.doConfiguration();
                break;
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
                break;
        }
    }

    @Override
    public void showBleConfigurationDialog(String message) {
        bleConfigDialog.updateTvText(message);
        bleConfigDialog.show();
    }

    @Override
    public void dismissBleConfigurationDialog() {
        bleConfigDialog.dismiss();
    }

    @Override
    public void updateBtnStatus(boolean canConfig) {
        acDeployConfigurationTvConfiguration.setEnabled(canConfig);
        acDeployConfigurationTvConfiguration.setBackgroundResource(canConfig ? R.drawable.shape_bg_corner_29c_shadow : R.drawable.shape_bg_solid_df_corner);

    }

    @Override
    public void updateBleConfigurationDialogText(String text) {
        bleConfigDialog.updateTvText(text);
    }

    @Override
    public String getEditTextValue() {
        return acDeployConfigurationEtEnter.getText().toString();
    }

    @Override
    public void updateBleConfigurationDialogSuccessImv() {
        bleConfigDialog.showSuccessImv();
    }

    @Override
    public void setTvNearVisible(boolean isVisible) {
        acDeployConfigurationTvNear.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }


    @Override
    public boolean hasEditTextContent() {
        return acDeployConfigurationEtEnter.getText().toString().length() > 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bleConfigDialog.dismiss();
    }
}

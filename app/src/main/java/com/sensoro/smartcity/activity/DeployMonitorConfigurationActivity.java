package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnDismissListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectChangeListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.model.EarlyWarningthresholdDialogUtilsAdapterModel;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployMonitorConfigurationView;
import com.sensoro.smartcity.model.MaterialValueModel;
import com.sensoro.smartcity.presenter.DeployMonitorConfigurationPresenter;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.widget.dialog.BleConfigurationDialogUtils;
import com.sensoro.smartcity.widget.dialog.EarlyWarningThresholdDialogUtils;
import com.sensoro.smartcity.widget.dialog.MonitorPointOperatingDialogUtil;
import com.sensoro.smartcity.widget.dialog.TipOperationDialogUtils;
import com.sensoro.smartcity.widget.popup.SelectDialog;
import com.sensoro.smartcity.widget.toast.SensoroSuccessToast;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    @BindView(R.id.ll_ac_deploy_configuration_diameter)
    LinearLayout llAcDeployConfigurationDiameter;
    @BindView(R.id.ll_wire_material)
    LinearLayout ll_wire_material;
    @BindView(R.id.ac_deploy_configuration_tv_wire_material)
    TextView acDeployConfigurationTvWireMaterial;
    @BindView(R.id.ll_wire_diameter)
    LinearLayout llWireDiameter;
    @BindView(R.id.ac_deploy_configuration_et_root)
    LinearLayout acDeployConfigurationEtRoot;
    @BindView(R.id.ac_deploy_configuration_tv_enter_tip)
    TextView acDeployConfigurationTvEnterTip;
    @BindView(R.id.ac_deploy_configuration_tv_configuration)
    TextView acDeployConfigurationTvConfiguration;
    @BindView(R.id.ll_current_info)
    LinearLayout llCurrentInfo;
    @BindView(R.id.ac_deploy_configuration_tv_near)
    TextView acDeployConfigurationTvNear;
    @BindView(R.id.tv_current_value)
    TextView tvCurrentValue;
    @BindView(R.id.ac_deploy_configuration_tv_diameter)
    TextView acDeployConfigurationTvDiameter;

    private final List<String> marterials = new ArrayList<>();
    private BleConfigurationDialogUtils bleConfigDialog;
    private OptionsPickerView pvCustomOptions;
    private EarlyWarningThresholdDialogUtils overCurrentDialog;
    private MonitorPointOperatingDialogUtil mOperatingUtil;
    private TipOperationDialogUtils mTipUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deploy_configuration);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initOperatingDialog() {
        mOperatingUtil = new MonitorPointOperatingDialogUtil(mActivity, false);
    }

    private void initTipDialog() {
        mTipUtils = new TipOperationDialogUtils(mActivity, false);
//        mTipUtils.setTipDialogUtilsClickListener(this);
    }

    private void initView() {
        includeTextTitleTvSubtitle.setText(R.string.cancel);
        initTipDialog();
        initOperatingDialog();
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        includeTextTitleTvTitle.setText(mActivity.getString(R.string.initial_configuration));
        initCustomOptionPicker();
        acDeployConfigurationEtEnter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getCurrentValue();
            }
        });
        acDeployConfigurationTvDiameter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getCurrentValue();
            }
        });
        acDeployConfigurationTvWireMaterial.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getCurrentValue();
            }
        });
        bleConfigDialog = new BleConfigurationDialogUtils(mActivity, mActivity.getString(R.string.connecting));
        overCurrentDialog = new EarlyWarningThresholdDialogUtils(mActivity, mActivity.getString(R.string.over_current));

        marterials.add(getString(R.string.cu));
        marterials.add(getString(R.string.al));
    }

    private void getCurrentValue() {
        String diameter = acDeployConfigurationTvDiameter.getText().toString();
        String material = acDeployConfigurationTvWireMaterial.getText().toString();
        String enterValue = acDeployConfigurationEtEnter.getText().toString();
        if (!TextUtils.isEmpty(diameter) && !TextUtils.isEmpty(material) && !TextUtils.isEmpty(enterValue)) {
            try {
                Integer integer = Integer.valueOf(enterValue);
                int in = integer;
                MaterialValueModel materialValueModel = Constants.materialValueMap.get(diameter);
                if (materialValueModel != null) {
                    if (getString(R.string.cu).equals(material)) {
                        in = materialValueModel.cuValue;
                    } else if (getString(R.string.al).equals(material)) {
                        in = materialValueModel.alValue;
                    }
                    int min = Math.min(integer, in);
                    tvCurrentValue.setText(String.format(Locale.CHINESE, "%dA", min));
                    updateBtnStatus(true);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                toastShort(getString(R.string.enter_the_correct_number_format));
                updateBtnStatus(false);
            }
        } else {
            tvCurrentValue.setText("");
            updateBtnStatus(false);
        }
    }

    private void initCustomOptionPicker() {//条件选择器初始化，自定义布局
        final ArrayList<String> strings = new ArrayList<>();
        strings.addAll(Constants.materialValueMap.keySet());
        final String[] arr = {"16"};
        pvCustomOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = strings.get(options1);
                acDeployConfigurationTvDiameter.setText(tx);
            }
        }).setTitleText(mActivity.getString(R.string.wire_diameter))
                .setContentTextSize(23)//设置滚轮文字大小
                .setDividerColor(mActivity.getResources().getColor(R.color.c_e7e7e7))//设置分割线的颜色
                .setSelectOptions(6)//默认选中项
                .setCancelColor(mActivity.getResources().getColor(R.color.transparent))
                .setSubmitColor(mActivity.getResources().getColor(R.color.transparent))
                .setBgColor(mActivity.getResources().getColor(R.color.c_f4f4f4))
                .setTitleBgColor(mActivity.getResources().getColor(R.color.c_f4f4f4))
                .setTitleColor(mActivity.getResources().getColor(R.color.c_252525))
                .setTextColorCenter(mActivity.getResources().getColor(R.color.c_252525))
                .setTextColorOut(mActivity.getResources().getColor(R.color.c_a6a6a6))
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setOnCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        acDeployConfigurationTvDiameter.setText(arr[0]);
                    }
                })
                .setOutSideCancelable(true)
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
                        String s = strings.get(options1);
                        arr[0] = s;
                    }
                })
                .setCyclic(true, true, true)
                .build();
        pvCustomOptions.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(Object o) {
                acDeployConfigurationTvDiameter.setText(arr[0]);
            }
        });
        pvCustomOptions.setPicker(strings);//二级选择器

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


    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.include_text_title_tv_subtitle, R.id.ac_deploy_configuration_tv_configuration, R.id.ll_wire_material, R.id.ll_wire_diameter, R.id.ll_current_info})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ac_deploy_configuration_tv_configuration:
                mPresenter.doConfiguration(acDeployConfigurationTvWireMaterial.getText().toString(), acDeployConfigurationTvDiameter.getText().toString(), tvCurrentValue.getText().toString());
                break;
            case R.id.include_text_title_imv_arrows_left:
            case R.id.include_text_title_tv_subtitle:
                finishAc();
                break;
            case R.id.ll_wire_material:
                AppUtils.showDialog(mActivity, new SelectDialog.SelectDialogListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        setMaterial(marterials.get(position));
                    }
                }, marterials);
                break;
            case R.id.ll_wire_diameter:
                pvCustomOptions.show(); //弹出自定义条件选择器
                break;
            case R.id.ll_current_info:
                mPresenter.showOverCurrentDialog();
                break;
        }
    }

    private void setMaterial(String material) {
        acDeployConfigurationTvWireMaterial.setText(material);
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
    public void setTvEnterValueRange(int minValue, int maxValue) {
        String string = String.format(Locale.CHINESE, "%s%d-%d", mActivity.getString(R.string.deploy_configuration_enter_tip), minValue, maxValue);
        acDeployConfigurationTvEnterTip.setText(string);
    }

    @Override
    public void setLlAcDeployConfigurationDiameterVisible(boolean isVisible) {
        llAcDeployConfigurationDiameter.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showOverCurrentDialog(ArrayList<EarlyWarningthresholdDialogUtilsAdapterModel> overCurrentDataList) {
        if (overCurrentDialog != null) {
            overCurrentDialog.show(overCurrentDataList);
        }
    }

    @Override
    public void setInputCurrentText(String text) {
        if (text != null) {
            acDeployConfigurationEtEnter.setText(text);
            acDeployConfigurationEtEnter.setSelection(text.length());
        }

    }

    @Override
    public void setInputDiameterValueText(String text) {
        acDeployConfigurationTvDiameter.setText(text);
    }

    @Override
    public void setInputWireMaterialText(String text) {
        acDeployConfigurationTvWireMaterial.setText(text);
    }

    @Override
    public void setActualCurrentText(String text) {
        tvCurrentValue.setText(text);
    }

    @Override
    public void setAcDeployConfigurationTvConfigurationText(String text) {
        acDeployConfigurationTvConfiguration.setText(text);
    }

    @Override
    public void showOperationTipLoadingDialog() {
        if (mOperatingUtil != null) {
            mOperatingUtil.setTipText(mActivity.getString(R.string.configuring));
            mOperatingUtil.show();
        }
    }

    @Override
    public void dismissOperatingLoadingDialog() {
        if (mOperatingUtil != null) {
            mOperatingUtil.dismiss();
        }
    }

    @Override
    public void showErrorTipDialog(String errorMsg) {
        if (mTipUtils.isShowing()) {
            mTipUtils.setTipMessageText(errorMsg);
            return;
        }
        mTipUtils.setTipEtRootVisible(false);
        mTipUtils.setTipTitleText(mActivity.getString(R.string.request_failed));
        mTipUtils.setTipMessageText(errorMsg);
        mTipUtils.setTipCancelText(mActivity.getString(R.string.back), mActivity.getResources().getColor(R.color.c_252525));
        mTipUtils.setTipConfirmVisible(false);
        mTipUtils.show();
    }

    @Override
    public void dismissTipDialog() {
        if (mTipUtils != null) {
            mTipUtils.dismiss();
        }
    }

    @Override
    public void showOperationSuccessToast() {
        SensoroSuccessToast.INSTANCE.showToast(mActivity, Toast.LENGTH_SHORT);
    }

    @Override
    public void setTitleImvArrowsLeftVisible(boolean isVisible) {
        includeTextTitleImvArrowsLeft.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void setTitleTvSubtitleVisible(boolean isVisible) {
        includeTextTitleTvSubtitle.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        if (bleConfigDialog != null) {
            bleConfigDialog.onDestroy();
        }

        if (overCurrentDialog != null) {
            overCurrentDialog.destory();
        }
        if (mOperatingUtil != null) {
            mOperatingUtil.destroy();
        }
        if (mTipUtils != null) {
            mTipUtils.destroy();
        }
        super.onDestroy();

    }
}

package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployMonitorLocalCheckFragmentView;
import com.sensoro.smartcity.model.MaterialValueModel;
import com.sensoro.smartcity.presenter.DeployMonitorLocalCheckFragmentPresenter;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.widget.dialog.EarlyWarningThresholdDialogUtils;
import com.sensoro.smartcity.widget.popup.SelectDialog;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

public class DeployMonitorLocalCheckFragment extends BaseFragment<IDeployMonitorLocalCheckFragmentView, DeployMonitorLocalCheckFragmentPresenter> implements IDeployMonitorLocalCheckFragmentView {

    @BindView(R.id.tv_fg_deploy_local_button)
    TextView tvFgDeployLocalButton;
    @BindView(R.id.tv_fg_deploy_local_check_tip)
    TextView tvFgDeployLocalCheckTip;
    @BindView(R.id.tv_fg_deploy_local_check_device_sn)
    TextView tvFgDeployLocalCheckDeviceSn;
    @BindView(R.id.tv_fg_deploy_local_check_device_type)
    TextView tvFgDeployLocalCheckDeviceType;
    @BindView(R.id.tv_fg_deploy_local_check_location)
    TextView tvFgDeployLocalCheckLocation;
    @BindView(R.id.ll_fg_deploy_local_check_location)
    LinearLayout llFgDeployLocalCheckLocation;
    @BindView(R.id.et_fg_deploy_local_check_switch_spec)
    EditText etFgDeployLocalCheckSwitchSpec;
    @BindView(R.id.ll_fg_deploy_local_check_switch_spec)
    LinearLayout llFgDeployLocalCheckSwitchSpec;
    @BindView(R.id.tv_fg_deploy_local_check_wire_material)
    TextView tvFgDeployLocalCheckWireMaterial;
    @BindView(R.id.iv_fg_deploy_local_check_wire_material)
    ImageView ivFgDeployLocalCheckWireMaterial;
    @BindView(R.id.ll_fg_deploy_local_check_wire_material)
    LinearLayout llFgDeployLocalCheckWireMaterial;
    @BindView(R.id.tv_fg_deploy_local_check_wire_diameter)
    TextView tvFgDeployLocalCheckWireDiameter;
    @BindView(R.id.iv_fg_deploy_local_check_wire_diameter)
    ImageView ivFgDeployLocalCheckWireDiameter;
    @BindView(R.id.ll_fg_deploy_local_check_wire_diameter)
    LinearLayout llFgDeployLocalCheckWireDiameter;
    @BindView(R.id.tv_deploy_local_check_current_info)
    TextView tvDeployLocalCheckCurrentInfo;
    @BindView(R.id.iv_deploy_local_check_current_info)
    ImageView ivDeployLocalCheckCurrentInfo;
    @BindView(R.id.tv_deploy_local_check_current_value)
    TextView tvDeployLocalCheckCurrentValue;
    @BindView(R.id.ll_deploy_local_check_current_info)
    LinearLayout llDeployLocalCheckCurrentInfo;
    @BindView(R.id.ll_fg_deploy_local_check_config)
    LinearLayout llFgDeployLocalCheckConfig;
    @BindView(R.id.fl_deploy_local_check_not_own)
    FrameLayout flDeployLocalCheckNotOwn;
    private EarlyWarningThresholdDialogUtils overCurrentDialog;
    private OptionsPickerView pvCustomOptions;
    private final List<String> materials = new ArrayList<>();

    @Override
    protected void initData(Context activity) {
        initView();
        mPresenter.initData(activity);
    }

    private void initView() {
        materials.add(getString(R.string.cu));
        materials.add(getString(R.string.al));
        overCurrentDialog = new EarlyWarningThresholdDialogUtils(mRootFragment.getActivity(), mRootFragment.getString(R.string.over_current));
        initCustomOptionPicker();
        etFgDeployLocalCheckSwitchSpec.addTextChangedListener(new TextWatcher() {
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
        tvFgDeployLocalCheckWireMaterial.addTextChangedListener(new TextWatcher() {
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
        tvFgDeployLocalCheckWireDiameter.addTextChangedListener(new TextWatcher() {
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
    }

    private void initCustomOptionPicker() {//条件选择器初始化，自定义布局
        final int[] arr = {6};
        pvCustomOptions = new OptionsPickerBuilder(mRootFragment.getActivity(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                arr[0] = options1;
                mPresenter.doCustomOptionPickerItemSelect(options1);
            }
        }).setTitleText(mRootFragment.getString(R.string.wire_diameter))
                .setContentTextSize(23)//设置滚轮文字大小
                .setDividerColor(mRootFragment.getResources().getColor(R.color.c_e7e7e7))//设置分割线的颜色
                .setSelectOptions(6)//默认选中项
                .setCancelColor(mRootFragment.getResources().getColor(R.color.transparent))
                .setSubmitColor(mRootFragment.getResources().getColor(R.color.transparent))
                .setBgColor(mRootFragment.getResources().getColor(R.color.c_f4f4f4))
                .setTitleBgColor(mRootFragment.getResources().getColor(R.color.c_f4f4f4))
                .setTitleColor(mRootFragment.getResources().getColor(R.color.c_252525))
                .setTextColorCenter(mRootFragment.getResources().getColor(R.color.c_252525))
                .setTextColorOut(mRootFragment.getResources().getColor(R.color.c_a6a6a6))
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setOnCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.doCustomOptionPickerItemSelect(arr[0]);
                    }
                })
                .setOutSideCancelable(true)
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
                        arr[0] = options1;
                    }
                })
                .setCyclic(true, true, true)
                .build();
        pvCustomOptions.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(Object o) {
                mPresenter.doCustomOptionPickerItemSelect(arr[0]);
            }
        });
    }

    @Override
    public void updatePvCustomOptions(List<String> list) {
        if (pvCustomOptions != null && list != null) {
            pvCustomOptions.setPicker(list);
        }
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
    public void setDeviceSn(String sn) {
        tvFgDeployLocalCheckDeviceSn.setText(sn);
    }

    @Override
    public void setDeployDeviceType(String type) {
        tvFgDeployLocalCheckDeviceType.setText(type);
    }

    @Override
    public void setDeployDeviceConfigVisible(boolean isVisible) {
        llFgDeployLocalCheckConfig.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setDeployPosition(boolean hasPosition) {
        if (hasPosition) {
            tvFgDeployLocalCheckLocation.setTextColor(mRootFragment.getResources().getColor(R.color.c_252525));
            tvFgDeployLocalCheckLocation.setText(mRootFragment.getString(R.string.positioned));
        } else {
            tvFgDeployLocalCheckLocation.setTextColor(mRootFragment.getResources().getColor(R.color.c_a6a6a6));
            tvFgDeployLocalCheckLocation.setText(mRootFragment.getString(R.string.required));
        }

    }

    @Override
    public void setSwitchSpecHintText(String text) {
        etFgDeployLocalCheckSwitchSpec.setHint(text);
    }

    @Override
    public void setSwitchSpecContentText(String text) {
        etFgDeployLocalCheckSwitchSpec.setText(text);
    }

    @Override
    public void setWireMaterialText(String text) {
        if (TextUtils.isEmpty(text)) {
            tvFgDeployLocalCheckWireMaterial.setTextColor(mRootFragment.getResources().getColor(R.color.c_a6a6a6));
            tvFgDeployLocalCheckWireMaterial.setText(mRootFragment.getText(R.string.deploy_check_please_select));
        } else {
            tvFgDeployLocalCheckWireMaterial.setTextColor(mRootFragment.getResources().getColor(R.color.c_252525));
            tvFgDeployLocalCheckWireMaterial.setText(text);
        }

    }

    @Override
    public void setWireDiameterText(String text) {
        if (TextUtils.isEmpty(text)) {
            tvFgDeployLocalCheckWireDiameter.setTextColor(mRootFragment.getResources().getColor(R.color.c_a6a6a6));
            tvFgDeployLocalCheckWireDiameter.setText(mRootFragment.getText(R.string.deploy_check_please_select));
        } else {
            tvFgDeployLocalCheckWireDiameter.setTextColor(mRootFragment.getResources().getColor(R.color.c_252525));
            tvFgDeployLocalCheckWireDiameter.setText(text);
        }
    }

    @Override
    public void setDeployCheckTvConfigurationText(String text) {
        tvDeployLocalCheckCurrentValue.setText(text);
    }

    private void getCurrentValue() {
        String diameter = tvFgDeployLocalCheckWireDiameter.getText().toString();
        String material = tvFgDeployLocalCheckWireMaterial.getText().toString();
        String enterValue = etFgDeployLocalCheckSwitchSpec.getText().toString();
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
                    tvDeployLocalCheckCurrentValue.setText(String.format(Locale.CHINESE, "%dA", min));
                    updateBtnStatus(true);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                toastShort(getString(R.string.enter_the_correct_number_format));
                updateBtnStatus(false);
            }
        } else {
            tvDeployLocalCheckCurrentValue.setText("");
            updateBtnStatus(false);
        }
    }

    @Override
    public void updateBtnStatus(boolean canConfig) {
        tvFgDeployLocalButton.setEnabled(canConfig);
        tvFgDeployLocalButton.setBackgroundResource(canConfig ? R.drawable.shape_bg_corner_29c_shadow : R.drawable.shape_bg_solid_df_corner);

    }

    @Override
    public void onFragmentStart() {

    }

    @Override
    public void onFragmentStop() {

    }

    @Override
    public void showOverCurrentDialog(ArrayList<EarlyWarningthresholdDialogUtilsAdapterModel> overCurrentDataList) {
        if (overCurrentDialog != null) {
            overCurrentDialog.show(overCurrentDataList);
        }
    }

    @OnClick({R.id.tv_fg_deploy_local_button, R.id.ll_fg_deploy_local_check_location, R.id.ll_fg_deploy_local_check_switch_spec, R.id.ll_fg_deploy_local_check_wire_material, R.id.ll_fg_deploy_local_check_wire_diameter, R.id.iv_deploy_local_check_current_info})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_fg_deploy_local_button:
                break;
            case R.id.ll_fg_deploy_local_check_location:
                break;
            case R.id.ll_fg_deploy_local_check_switch_spec:
                break;
            case R.id.ll_fg_deploy_local_check_wire_material:
                AppUtils.showDialog(mRootFragment.getActivity(), new SelectDialog.SelectDialogListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String text = materials.get(position);
                        setWireMaterialText(text);
                    }
                }, materials);
                break;
            case R.id.ll_fg_deploy_local_check_wire_diameter:
                pvCustomOptions.show(); //弹出自定义条件选择器
                break;
            case R.id.iv_deploy_local_check_current_info:
                mPresenter.showOverCurrentDialog();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        if (overCurrentDialog != null) {
            overCurrentDialog.destory();
        }
        super.onDestroyView();
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }
}

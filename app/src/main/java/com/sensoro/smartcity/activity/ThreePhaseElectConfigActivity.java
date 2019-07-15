package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnDismissListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.common.widgets.TipOperationDialogUtils;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.WireMaterialDiameterAdapter;
import com.sensoro.smartcity.imainviews.IThreePhaseElectConfigActivityView;
import com.sensoro.smartcity.model.WireMaterialDiameterModel;
import com.sensoro.smartcity.presenter.ThreePhaseElectConfigActivityPresenter;
import com.sensoro.smartcity.widget.dialog.MonitorPointOperatingDialogUtil;
import com.sensoro.smartcity.widget.toast.SensoroSuccessToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = ARouterConstants.ACTIVITY_THREE_PHASE_ELECT_CONFIG_ACTIVITY)
public class ThreePhaseElectConfigActivity extends BaseActivity<IThreePhaseElectConfigActivityView, ThreePhaseElectConfigActivityPresenter>
        implements IThreePhaseElectConfigActivityView {

    @BindView(R.id.include_text_title_tv_cancel)
    TextView includeImvTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeImvTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R.id.et_input_rated_current_ac_wire_material_diameter1)
    EditText etInputRatedCurrentAcWireMaterialDiameter1;
    @BindView(R.id.ll_input_rated_current_ac_wire_material_diameter1)
    RelativeLayout llInputRatedCurrentAcWireMaterialDiameter1;
    @BindView(R.id.rv_in_line_ac_wire_material_diameter)
    RecyclerView rvInLineAcWireMaterialDiameter;
    @BindView(R.id.rv_out_line_ac_wire_material_diameter)
    RecyclerView rvOutLineAcWireMaterialDiameter;
    @BindView(R.id.nsv_ac_wire_material_diameter)
    NestedScrollView nsvViewAcWireMaterialDiameter;
    @BindView(R.id.ll_root_ac_wire_material_diameter)
    LinearLayout llRootAcWireMaterialDiameter;
    @BindView(R.id.tv_in_line_add_ac_wire_material_diameter)
    TextView tvInLineAddAcWireMaterialDiameter;
    @BindView(R.id.fl_in_line_add_ac_wire_material_diameter)
    FrameLayout flInLineAddAcWireMaterialDiameter;
    @BindView(R.id.tv_out_line_add_ac_wire_material_diameter)
    TextView tvOutLineAddAcWireMaterialDiameter;
    @BindView(R.id.fl_out_line_add_ac_wire_material_diameter)
    FrameLayout flOutLineAddAcWireMaterialDiameter;
    @BindView(R.id.tv_actual_value)
    TextView tvActualValue;
    @BindView(R.id.tv_three_phase_elect_config_current_range)
    TextView tThreePhaseElectConfigCurrentRange;
    private WireMaterialDiameterAdapter mInLineAdapter;
    private WireMaterialDiameterAdapter mOutLineAdapter;
    private OptionsPickerView<String> pvCustomOptions;
    private TextView mTvTitlePicker;
    private Drawable mDetailDownDrawable;
    private Drawable mDetailUpDrawable;
    private Drawable addBlackDrawable;
    private Drawable addWhiteDrawable;
    private MonitorPointOperatingDialogUtil mOperatingUtil;
    private TipOperationDialogUtils mTipUtils;
    private TextView mTvComplete;
    private TextView mTvDelete;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_three_phase_elect_config);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        includeImvTitleTvTitle.setText(mActivity.getString(R.string.monitor_point_detail_air_switch_config));
        includeImvTitleTvTitle.getPaint().setFakeBoldText(true);
        includeTextTitleTvSubtitle.setText(R.string.save);
        includeTextTitleTvSubtitle.setTextColor(getResources().getColor(R.color.c_1DBB99));
        mOperatingUtil = new MonitorPointOperatingDialogUtil(mActivity, false);
        mTipUtils = new TipOperationDialogUtils(mActivity, false);
        initRcInline();

        initRcOutline();

        initCustomOptionPicker();

        initEtListener();

//        AppUtils.getInputSoftStatus(llRootAcWireMaterialDiameter, new AppUtils.InputSoftStatusListener() {
//
//            @Override
//            public void onKeyBoardClose() {
//                mPresenter.handleRecommendTransformer();
//            }
//
//            @Override
//            public void onKeyBoardOpen() {
//            }
//        });

        addBlackDrawable = mActivity.getDrawable(R.drawable.wire_add);
        if (addBlackDrawable != null) {
            addBlackDrawable.setBounds(0, 0, addBlackDrawable.getMinimumWidth(), addBlackDrawable.getMinimumHeight());
        }
        addWhiteDrawable = mActivity.getDrawable(R.drawable.wire_add_white);
        if (addWhiteDrawable != null) {
            addWhiteDrawable.setBounds(0, 0, addWhiteDrawable.getMinimumWidth(), addWhiteDrawable.getMinimumHeight());
        }
    }

    private void initEtListener() {

        etInputRatedCurrentAcWireMaterialDiameter1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String s1 = s.toString();
                if (!TextUtils.isEmpty(s1)) {
                    mPresenter.handleRecommendTransformer();
                } else {
                    setActualCurrentValue(null);
                }
            }
        });

        etInputRatedCurrentAcWireMaterialDiameter1.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    AppUtils.dismissInputMethodManager(mActivity, etInputRatedCurrentAcWireMaterialDiameter1, true);
                    return true;
                }

                return false;
            }
        });
    }

    private void initCustomOptionPicker() {//条件选择器初始化，自定义布局
        boolean[] bs = {false};
        pvCustomOptions = new OptionsPickerBuilder(mActivity, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                if (pvCustomOptions.isShowing()) {
                    mPresenter.doSelectComplete(options1, options2, options3 + 1);
                }
            }
        }).setTitleText(mActivity.getString(R.string.diameter))
                .setLayoutRes(R.layout.item_picker_calculator, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        mTvDelete = v.findViewById(R.id.tv_delete_item_picker_calculator);
                        mTvComplete = v.findViewById(R.id.tv_complete_item_picker_calculator);
                        LinearLayout llSubtitle = v.findViewById(R.id.ll_subtitle_item_picker_calculator);
                        mTvTitlePicker = v.findViewById(R.id.tv_title_item_picker_calculator);
                        mTvDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPresenter.doDeleteGroup();
                                mTvDelete.setClickable(false);
                            }
                        });

                        mTvComplete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions.returnData();
                                mTvComplete.setClickable(false);
                            }
                        });
                        llSubtitle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });


                    }
                })
                .setContentTextSize(20)//设置滚轮文字大小
                .setDividerColor(mActivity.getResources().getColor(R.color.c_dfdfdf))//设置分割线的颜色
                .setSelectOptions(0, 6, 0)//默认选中项
                .setBgColor(mActivity.getResources().getColor(R.color.white))
                .setTitleBgColor(mActivity.getResources().getColor(R.color.c_f4f4f4))
                .setTitleColor(mActivity.getResources().getColor(R.color.c_252525))
                .setTextColorCenter(mActivity.getResources().getColor(R.color.c_252525))
                .setTextColorOut(mActivity.getResources().getColor(R.color.c_a6a6a6))
                .setOutSideColor(mActivity.getResources().getColor(R.color.c_B3000000))
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isCenterLabel(false)//是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setOutSideCancelable(true)
                .setCyclic(false, false, false)
                .setLineSpacingMultiplier(2.0f)
                .build();
        pvCustomOptions.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(Object o) {
                mPresenter.onPickerViewDismiss();
            }
        });

    }

    private void initRcInline() {
        mInLineAdapter = new WireMaterialDiameterAdapter(mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mInLineAdapter.setOnItemClickListener(new WireMaterialDiameterAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position, boolean isAction) {
                AppUtils.dismissInputMethodManager(mActivity, etInputRatedCurrentAcWireMaterialDiameter1);
                mPresenter.doInLineItemClick(position, isAction);
            }
        });
        rvInLineAcWireMaterialDiameter.setLayoutManager(manager);
        rvInLineAcWireMaterialDiameter.setAdapter(mInLineAdapter);
    }

    private void initRcOutline() {
        mOutLineAdapter = new WireMaterialDiameterAdapter(mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mOutLineAdapter.setOnItemClickListener(new WireMaterialDiameterAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position, boolean isAction) {
                AppUtils.dismissInputMethodManager(mActivity, etInputRatedCurrentAcWireMaterialDiameter1);
                mPresenter.doOutLineItemClick(position, isAction);
            }
        });
        rvOutLineAcWireMaterialDiameter.setLayoutManager(manager);
        rvOutLineAcWireMaterialDiameter.setAdapter(mOutLineAdapter);
    }

    @Override
    protected ThreePhaseElectConfigActivityPresenter createPresenter() {
        return new ThreePhaseElectConfigActivityPresenter();
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
    public void startAC(Intent intent) {

    }

    @Override
    public void finishAc() {
        mActivity.finish();
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
    public void updateInLineData(ArrayList<WireMaterialDiameterModel> mInLineList) {
        //个数大于5个不能点击
        setInLineAddStatus(mInLineList.size() < 5);
        mInLineAdapter.updateData(mInLineList);
    }

    @Override
    public void updateOutLineData(ArrayList<WireMaterialDiameterModel> mOutLineList) {
        //个数大于5个不能点击
        setOutLineAddStatus(mOutLineList.size() < 5);
        mOutLineAdapter.updateData(mOutLineList);
    }

    private void setOutLineAddStatus(boolean isClickable) {
        if (isClickable) {
            tvOutLineAddAcWireMaterialDiameter.setCompoundDrawables(addBlackDrawable, null, null, null);
            tvOutLineAddAcWireMaterialDiameter.setTextColor(mActivity.getResources().getColor(R.color.c_252525));
            flOutLineAddAcWireMaterialDiameter.setClickable(true);
            flOutLineAddAcWireMaterialDiameter.setBackground(mActivity.getResources().getDrawable(R.drawable.select_wire_diameter_stroke_fafa_ee));
        } else {
            tvOutLineAddAcWireMaterialDiameter.setCompoundDrawables(addWhiteDrawable, null, null, null);
            tvOutLineAddAcWireMaterialDiameter.setTextColor(mActivity.getResources().getColor(R.color.white));
            flOutLineAddAcWireMaterialDiameter.setBackground(mActivity.getResources().getDrawable(R.drawable.shape_bg_solid_df_corner_2));
            flOutLineAddAcWireMaterialDiameter.setClickable(false);
        }
    }

    private void setInLineAddStatus(boolean isClickable) {
        if (isClickable) {
            tvInLineAddAcWireMaterialDiameter.setCompoundDrawables(addBlackDrawable, null, null, null);
            tvInLineAddAcWireMaterialDiameter.setTextColor(mActivity.getResources().getColor(R.color.c_252525));
            flInLineAddAcWireMaterialDiameter.setClickable(true);
            flInLineAddAcWireMaterialDiameter.setBackground(mActivity.getResources().getDrawable(R.drawable.select_wire_diameter_stroke_fafa_ee));
        } else {
            tvInLineAddAcWireMaterialDiameter.setCompoundDrawables(addWhiteDrawable, null, null, null);
            tvInLineAddAcWireMaterialDiameter.setTextColor(mActivity.getResources().getColor(R.color.white));
            flInLineAddAcWireMaterialDiameter.setBackground(mActivity.getResources().getDrawable(R.drawable.shape_bg_solid_df_corner_2));
            flInLineAddAcWireMaterialDiameter.setClickable(false);
        }
    }

    @Override
    public void updatePvCustomOptions(List<String> materials, List<String> pickerStrings, List<String> counts) {
        if (pvCustomOptions != null && pickerStrings != null && materials != null && counts != null) {
            pvCustomOptions.setNPicker(materials, pickerStrings, counts);
        }

    }

    @Override
    public void showPickerView() {
        if (pvCustomOptions != null) {
            mTvComplete.setClickable(true);
            mTvDelete.setClickable(true);
            pvCustomOptions.show();
        }
    }

    @Override
    public void setPickerViewSelectOptions(int material, int diameter, int count) {
        pvCustomOptions.setSelectOptions(material, diameter, count);
    }

    @Override
    public void dismissPickerView() {
        if (pvCustomOptions != null) {
            pvCustomOptions.dismiss();
        }
    }

    @Override
    public String getEtInputText() {
        return etInputRatedCurrentAcWireMaterialDiameter1.getText().toString();
    }

    @Override
    public void setPickerTitle(String title) {
        mTvTitlePicker.setText(title);
    }

    @Override
    public void setActualCurrentValue(Integer value) {
        if (null == value) {
            tvActualValue.setText("-");
        } else {
            tvActualValue.setText(value + "A");
        }

    }

    @Override
    public void setInputRated(String value) {
        if (value != null) {
            etInputRatedCurrentAcWireMaterialDiameter1.setText(value);
            etInputRatedCurrentAcWireMaterialDiameter1.setSelection(value.length());
        }

    }

    @Override
    public void setSubtitleText(String text) {
        includeTextTitleTvSubtitle.setText(text);
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
    public void showOperationSuccessToast() {
        SensoroSuccessToast.getInstance().showToast(mActivity, Toast.LENGTH_SHORT);
    }

    @Override
    public void dismissTipDialog() {
        if (mTipUtils != null) {
            mTipUtils.dismiss();
        }
    }

    @Override
    public void showOperationTipLoadingDialog() {
        if (mOperatingUtil != null) {
            mOperatingUtil.setTipText(mActivity.getString(R.string.configuring));
            mOperatingUtil.show();
        }
    }

    @Override
    public void setTvEnterValueRange(int minValue, int maxValue) {
        String string = mActivity.getString(R.string.deploy_configuration_enter_tip);
        tThreePhaseElectConfigCurrentRange.setText(string + "/A (" + minValue + "-" + maxValue + ")");
    }


    @OnClick({R.id.include_text_title_tv_cancel, R.id.ll_input_rated_current_ac_wire_material_diameter1,
            R.id.fl_in_line_add_ac_wire_material_diameter, R.id.fl_out_line_add_ac_wire_material_diameter, R.id.include_text_title_tv_subtitle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_tv_cancel:
                AppUtils.dismissInputMethodManager(mActivity, etInputRatedCurrentAcWireMaterialDiameter1);
                finishAc();
                break;
//            case R.id.ll_input_rated_current_ac_wire_material_diameter1:
//                etInputRatedCurrentAcWireMaterialDiameter1.setCursorVisible(true);
//                etInputRatedCurrentAcWireMaterialDiameter1.requestFocus();
//                etInputRatedCurrentAcWireMaterialDiameter1.setFocusableInTouchMode(true);
//                etInputRatedCurrentAcWireMaterialDiameter1.performClick();
//                break;
//            case R.id.tv_recommend_transformer_ac_wire_material_diameter:
//                AppUtils.dismissInputMethodManager(mActivity, etInputRatedCurrentAcWireMaterialDiameter1, true);
//                mPresenter.handleRecommendTransformer();
//                scrollBottom();
//                break;
            case R.id.fl_detail_ac_wire_material_diameter:
                doDetailClick();
                break;
            case R.id.fl_in_line_add_ac_wire_material_diameter:
                AppUtils.dismissInputMethodManager(mActivity, etInputRatedCurrentAcWireMaterialDiameter1, true);
                llInputRatedCurrentAcWireMaterialDiameter1.requestFocus();
                llInputRatedCurrentAcWireMaterialDiameter1.setFocusableInTouchMode(true);
                mPresenter.doAddInLine();
                break;
            case R.id.fl_out_line_add_ac_wire_material_diameter:
                AppUtils.dismissInputMethodManager(mActivity, etInputRatedCurrentAcWireMaterialDiameter1, true);
                llInputRatedCurrentAcWireMaterialDiameter1.requestFocus();
                llInputRatedCurrentAcWireMaterialDiameter1.setFocusableInTouchMode(true);
                mPresenter.doAddOutLine();
                break;
            case R.id.include_text_title_tv_subtitle:
                //保存
                mPresenter.doSave();
                break;
        }
    }

    private void scrollBottom() {
        nsvViewAcWireMaterialDiameter.post(new Runnable() {
            @Override
            public void run() {
                nsvViewAcWireMaterialDiameter.fullScroll(NestedScrollView.FOCUS_DOWN);
            }
        });
    }

    private void doDetailClick() {
//        if (clDetailAcWireMaterialDiameter.getVisibility() == View.VISIBLE) {
//
//            if (mDetailDownDrawable == null) {
//                mDetailDownDrawable = mActivity.getDrawable(R.drawable.arrow_down_elect);
//                if (mDetailDownDrawable != null) {
//                    mDetailDownDrawable.setBounds(0, 0, mDetailDownDrawable.getMinimumWidth(), mDetailDownDrawable.getMinimumHeight());
//                }
//            }
//            tvLookDetailAcWireMaterialDiameter.setCompoundDrawables(null, null, mDetailDownDrawable, null);
//            clDetailAcWireMaterialDiameter.setVisibility(View.GONE);
//            tvLookDetailAcWireMaterialDiameter.setText(mActivity.getString(R.string.look_detail));
//        } else {
//            if (mDetailUpDrawable == null) {
//                mDetailUpDrawable = mActivity.getDrawable(R.drawable.arrow_up_elect);
//                if (mDetailUpDrawable != null) {
//                    mDetailUpDrawable.setBounds(0, 0, mDetailUpDrawable.getMinimumWidth(), mDetailUpDrawable.getMinimumHeight());
//                }
//            }
//            tvLookDetailAcWireMaterialDiameter.setCompoundDrawables(null, null, mDetailUpDrawable, null);
//            clDetailAcWireMaterialDiameter.setVisibility(View.VISIBLE);
//            tvLookDetailAcWireMaterialDiameter.setText(mActivity.getString(R.string.collapse));
//        }
        scrollBottom();
    }

    @Override
    protected void onDestroy() {
        if (mTipUtils != null) {
            mTipUtils.destroy();
        }
        if (mOperatingUtil != null) {
            mOperatingUtil.destroy();
        }
        if (pvCustomOptions != null) {
            pvCustomOptions.dismiss();
        }
        super.onDestroy();
    }
}

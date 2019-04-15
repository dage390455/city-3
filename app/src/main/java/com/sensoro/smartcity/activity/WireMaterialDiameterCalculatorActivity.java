package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnDismissListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.WireMaterialDiameterAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IWireMaterialDiameterCalculatorView;
import com.sensoro.smartcity.model.WireMaterialDiameterModel;
import com.sensoro.smartcity.presenter.WireMaterialDiameterCalculatorPresenter;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WireMaterialDiameterCalculatorActivity extends BaseActivity<IWireMaterialDiameterCalculatorView, WireMaterialDiameterCalculatorPresenter>
        implements IWireMaterialDiameterCalculatorView {

    @BindView(R.id.include_imv_title_imv_arrows_left)
    ImageView includeImvTitleImvArrowsLeft;
    @BindView(R.id.include_imv_title_tv_title)
    TextView includeImvTitleTvTitle;
    @BindView(R.id.include_imv_title_imv_subtitle)
    ImageView includeImvTitleImvSubtitle;
    @BindView(R.id.et_input_rated_current_ac_wire_material_diameter1)
    EditText etInputRatedCurrentAcWireMaterialDiameter1;
    @BindView(R.id.ll_input_rated_current_ac_wire_material_diameter1)
    LinearLayout llInputRatedCurrentAcWireMaterialDiameter1;
    @BindView(R.id.rv_in_line_ac_wire_material_diameter)
    RecyclerView rvInLineAcWireMaterialDiameter;
    @BindView(R.id.rv_out_line_ac_wire_material_diameter)
    RecyclerView rvOutLineAcWireMaterialDiameter;
    @BindView(R.id.tv_recommend_transformer_ac_wire_material_diameter)
    TextView tvRecommendTransformerAcWireMaterialDiameter;
    @BindView(R.id.tv_current_transformer_ac_wire_material_diameter)
    TextView tvCurrentTransformerAcWireMaterialDiameter;
    @BindView(R.id.tv_leakage_current_ac_wire_material_diameter)
    TextView tvLeakageCurrentAcWireMaterialDiameter;
    @BindView(R.id.tv_electrical_fire_ac_wire_material_diameter)
    TextView tvElectricalFireAcWireMaterialDiameter;
    @BindView(R.id.tv_electrical_fire_value_ac_wire_material_diameter)
    TextView tvElectricalFireValueAcWireMaterialDiameter;
    @BindView(R.id.tv_in_line_total_ac_wire_material_diameter)
    TextView tvInLineTotalAcWireMaterialDiameter;
    @BindView(R.id.tv_in_line_total_value_ac_wire_material_diameter)
    TextView tvInLineTotalValueAcWireMaterialDiameter;
    @BindView(R.id.tv_out_line_total_ac_wire_material_diameter)
    TextView tvOutLineTotalAcWireMaterialDiameter;
    @BindView(R.id.tv_out_line_total_value_ac_wire_material_diameter)
    TextView tvOutLineTotalValueAcWireMaterialDiameter;
    @BindView(R.id.view_divider1_ac_wire_material_diameter)
    View viewDivider1AcWireMaterialDiameter;
    @BindView(R.id.tv_actual_over_current_threshold_ac_wire_material_diameter)
    TextView tvActualOverCurrentThresholdAcWireMaterialDiameter;
    @BindView(R.id.tv_actual_over_current_threshold_value_ac_wire_material_diameter)
    TextView tvActualOverCurrentThresholdValueAcWireMaterialDiameter;
    @BindView(R.id.tv_rule_description_ac_wire_material_diameter)
    TextView tvRuleDescriptionAcWireMaterialDiameter;
    @BindView(R.id.cl_detail_ac_wire_material_diameter)
    ConstraintLayout clDetailAcWireMaterialDiameter;
    @BindView(R.id.tv_look_detail_ac_wire_material_diameter)
    TextView tvLookDetailAcWireMaterialDiameter;
    @BindView(R.id.nsv_ac_wire_material_diameter)
    NestedScrollView nsvViewAcWireMaterialDiameter;
    @BindView(R.id.ll_match_result_ac_wire_material_diameter)
    LinearLayout llMatchResultAcWireMaterialDiameter;
    @BindView(R.id.fl_detail_ac_wire_material_diameter)
    FrameLayout flDetailAcWireMaterialDiameter;
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
    private WireMaterialDiameterAdapter mInLineAdapter;
    private WireMaterialDiameterAdapter mOutLineAdapter;
    private OptionsPickerView<String> pvCustomOptions;
    private TextView mTvTitlePicker;
    private Drawable mDetailDownDrawable;
    private Drawable mDetailUpDrawable;
    private Drawable addBlackDrawable;
    private Drawable addWhiteDrawable;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_wire_material_diameter);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        includeImvTitleTvTitle.setText(mActivity.getString(R.string.wire_material_diameter_calculator));
//        includeImvTitleTvTitle.getPaint().
        includeImvTitleTvTitle.getPaint().setFakeBoldText(true);
        includeImvTitleImvSubtitle.setVisibility(View.GONE);

        initRcInline();

        initRcOutline();

        initCustomOptionPicker();

        initEtListener();

        AppUtils.getInputSoftStatus(llRootAcWireMaterialDiameter, new AppUtils.InputSoftStatusListener() {

            @Override
            public void onKeyBoardClose() {
                mPresenter.checkRecommendTransformer();
            }

            @Override
            public void onKeyBoardOpen() {
                setResultVisible(false);
            }
        });

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
                    try {
                        int i = Integer.parseInt(s1);
                        if (i > 560 || i <= 0) {
                            toastShort(String.format(Locale.ROOT, "%s%s", mActivity.getString(R.string.leakage_current_transformer), "1-560"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
        pvCustomOptions = new OptionsPickerBuilder(mActivity, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                mPresenter.doSelectComplete(options1, options2, options3 + 1);
            }
        }).setTitleText(mActivity.getString(R.string.diameter))
                .setLayoutRes(R.layout.item_picker_calculator, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        TextView tvDelete = v.findViewById(R.id.tv_delete_item_picker_calculator);
                        TextView tvComplete = v.findViewById(R.id.tv_complete_item_picker_calculator);
                        LinearLayout llSubtitle = v.findViewById(R.id.ll_subtitle_item_picker_calculator);
                        mTvTitlePicker = v.findViewById(R.id.tv_title_item_picker_calculator);
                        tvDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPresenter.doDeleteGroup();
                            }
                        });

                        tvComplete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions.returnData();

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
                mPresenter.doOutLineItemClick(position, isAction);
            }
        });
        rvOutLineAcWireMaterialDiameter.setLayoutManager(manager);
        rvOutLineAcWireMaterialDiameter.setAdapter(mOutLineAdapter);
    }

    @Override
    protected WireMaterialDiameterCalculatorPresenter createPresenter() {
        return new WireMaterialDiameterCalculatorPresenter();
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
    public void setRecommendBtnStatus(boolean isClickable) {
        tvRecommendTransformerAcWireMaterialDiameter.setClickable(isClickable);
        tvRecommendTransformerAcWireMaterialDiameter.setBackground(mActivity.getDrawable(isClickable ? R.drawable.shape_bg_corner_4_29c_shadow : R.drawable.shape_bg_solid_df_corner));
    }

    @Override
    public void setRatedCurrentTransformer(String ratedCurrent) {
        tvCurrentTransformerAcWireMaterialDiameter.setText(ratedCurrent);
    }

    @Override
    public void setLeakageCurrentTransformer(String leakage) {
        tvLeakageCurrentAcWireMaterialDiameter.setText(leakage);
    }

    @Override
    public void setResultVisible(boolean isVisible) {
        llMatchResultAcWireMaterialDiameter.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        if (!isVisible) {
            clDetailAcWireMaterialDiameter.setVisibility(View.GONE);
            tvLookDetailAcWireMaterialDiameter.setText(mActivity.getString(R.string.look_detail));
        }

    }

    @Override
    public void setOutLineTotalCurrentValue(int value) {
        tvOutLineTotalValueAcWireMaterialDiameter.setText(String.format(Locale.ROOT, "%dA", value));
    }

    @Override
    public void setInLineTotalCurrentValue(int value) {
        tvInLineTotalValueAcWireMaterialDiameter.setText(String.format(Locale.ROOT, "%dA", value));
    }

    @Override
    public void setAirRatedCurrentValue(int ratedCurrent) {
        tvElectricalFireValueAcWireMaterialDiameter.setText(String.format(Locale.ROOT, "%dA", ratedCurrent));
    }

    @Override
    public void setActualCurrentValue(int actualRatedCurrent) {
        tvActualOverCurrentThresholdValueAcWireMaterialDiameter.setText(String.format(Locale.ROOT, "%dA", actualRatedCurrent));
    }


    @OnClick({R.id.include_imv_title_imv_arrows_left, R.id.ll_input_rated_current_ac_wire_material_diameter1,
            R.id.tv_recommend_transformer_ac_wire_material_diameter, R.id.fl_detail_ac_wire_material_diameter,
            R.id.fl_in_line_add_ac_wire_material_diameter, R.id.fl_out_line_add_ac_wire_material_diameter})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_imv_title_imv_arrows_left:
                AppUtils.dismissInputMethodManager(mActivity, etInputRatedCurrentAcWireMaterialDiameter1);
                finishAc();
                break;
            case R.id.ll_input_rated_current_ac_wire_material_diameter1:
                etInputRatedCurrentAcWireMaterialDiameter1.setCursorVisible(true);
                etInputRatedCurrentAcWireMaterialDiameter1.requestFocus();
                etInputRatedCurrentAcWireMaterialDiameter1.setFocusableInTouchMode(true);
                AppUtils.openInputMethodManager(mActivity, etInputRatedCurrentAcWireMaterialDiameter1);
//                etInputRatedCurrentAcWireMaterialDiameter1.performClick();
                break;
            case R.id.tv_recommend_transformer_ac_wire_material_diameter:
                mPresenter.doRecommendTransformer();
                scrollBottom();
                break;
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
        if (clDetailAcWireMaterialDiameter.getVisibility() == View.VISIBLE) {

            if (mDetailDownDrawable == null) {
                mDetailDownDrawable = mActivity.getDrawable(R.drawable.arrow_down_elect);
                if (mDetailDownDrawable != null) {
                    mDetailDownDrawable.setBounds(0, 0, mDetailDownDrawable.getMinimumWidth(), mDetailDownDrawable.getMinimumHeight());
                }
            }
            tvLookDetailAcWireMaterialDiameter.setCompoundDrawables(null, null, mDetailDownDrawable, null);
            clDetailAcWireMaterialDiameter.setVisibility(View.GONE);
            tvLookDetailAcWireMaterialDiameter.setText(mActivity.getString(R.string.look_detail));
        } else {
            if (mDetailUpDrawable == null) {
                mDetailUpDrawable = mActivity.getDrawable(R.drawable.arrow_up_elect);
                if (mDetailUpDrawable != null) {
                    mDetailUpDrawable.setBounds(0, 0, mDetailUpDrawable.getMinimumWidth(), mDetailUpDrawable.getMinimumHeight());
                }
            }
            tvLookDetailAcWireMaterialDiameter.setCompoundDrawables(null, null, mDetailUpDrawable, null);
            clDetailAcWireMaterialDiameter.setVisibility(View.VISIBLE);
            tvLookDetailAcWireMaterialDiameter.setText(mActivity.getString(R.string.collapse));
        }
        scrollBottom();
    }

    @Override
    protected void onDestroy() {
        if (pvCustomOptions != null) {
            pvCustomOptions.dismiss();
        }
        super.onDestroy();
    }
}

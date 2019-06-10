package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.ContractTemplateAdapter;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IContractServiceActivityView;
import com.sensoro.smartcity.presenter.ContractServiceActivityPresenter;
import com.sensoro.common.server.bean.ContractsTemplateInfo;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.common.widgets.SelectDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContractServiceActivity extends BaseActivity<IContractServiceActivityView,
        ContractServiceActivityPresenter> implements IContractServiceActivityView {
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.tv_contract_service_line1)
    TextView tvContractServiceLine1;
    @BindView(R.id.et_contract_service_line1)
    EditText etContractServiceLine1;
    @BindView(R.id.iv_contract_service_line1)
    ImageView ivContractServiceLine1;
    @BindView(R.id.ll_contract_service_line1)
    LinearLayout llContractServiceLine1;
    //新加电话号码必选
    @BindView(R.id.ll_contract_service_phone)
    LinearLayout llContractServicePhone;
    @BindView(R.id.et_contract_service_phone)
    EditText etContractServicePhone;
    //
    @BindView(R.id.tv_contract_service_line2)
    TextView tvContractServiceLine2;
    @BindView(R.id.et_contract_service_line2)
    EditText etContractServiceLine2;
    @BindView(R.id.tv_show_contract_service_line2)
    TextView tvShowContractService_line2;
    @BindView(R.id.iv_contract_service_line2)
    ImageView ivContractServiceLine2;
    @BindView(R.id.ll_contract_service_line2)
    LinearLayout llContractServiceLine2;

    @BindView(R.id.tv_contract_service_line3)
    TextView tvContractServiceLine3;
    @BindView(R.id.et_contract_service_line3)
    EditText etContractServiceLine3;
    @BindView(R.id.iv_contract_service_line3)
    ImageView ivContractServiceLine3;
    @BindView(R.id.ll_contract_service_line3)
    LinearLayout llContractServiceLine3;
    @BindView(R.id.tv_contract_service_line4)
    TextView tvContractServiceLine4;
    @BindView(R.id.et_contract_service_line4)
    EditText etContractServiceLine4;
    @BindView(R.id.iv_contract_service_line4)
    ImageView ivContractServiceLine4;
    @BindView(R.id.ll_contract_service_line4)
    LinearLayout llContractServiceLine4;
    @BindView(R.id.tv_contract_service_line5)
    TextView tvContractServiceLine5;
    @BindView(R.id.et_contract_service_line5)
    EditText etContractServiceLine5;
    @BindView(R.id.iv_contract_service_line5)
    ImageView ivContractServiceLine5;
    @BindView(R.id.ll_contract_service_line5)
    LinearLayout llContractServiceLine5;
    @BindView(R.id.tv_contract_service_line6)
    TextView tvContractServiceLine6;
    @BindView(R.id.et_contract_service_line6)
    EditText etContractServiceLine6;
    @BindView(R.id.iv_contract_service_line6)
    ImageView ivContractServiceLine6;
    @BindView(R.id.ll_contract_service_line6)
    LinearLayout llContractServiceLine6;
    //
    @BindView(R.id.tv_contract_service_place_type)
    TextView tvContractServicePlace;
    @BindView(R.id.ll_contract_service_place_type)
    LinearLayout llContractServicePlace;
    //
    @BindView(R.id.ll_contract_service_layout)
    LinearLayout llContractServiceLayout;

    @BindView(R.id.iv_contract_age_del)
    ImageView ivContractAgeDel;
    @BindView(R.id.et_contract_age)
    EditText etContractAge;
    @BindView(R.id.iv_contract_age_add)
    ImageView ivContractAgeAdd;
    @BindView(R.id.iv_contract_age_first_del)
    ImageView ivContractAgeFirstDel;
    @BindView(R.id.et_contract_age_first)
    EditText etContractAgeFirst;
    @BindView(R.id.iv_contract_age_first_add)
    ImageView ivContractAgeFirstAdd;
    @BindView(R.id.iv_contract_age_period_del)
    ImageView ivContractAgePeriodDel;
    @BindView(R.id.et_contract_age_period)
    EditText etContractAgePeriod;
    @BindView(R.id.iv_contract_age_period_add)
    ImageView getIvContractAgePeriodAdd;
    @BindView(R.id.rv_sensor_count)
    RecyclerView rvSensorCount;
    @BindView(R.id.bt_next)
    Button btNext;
    @BindView(R.id.iv_line_phone)
    ImageView ivLinePhone;
    @BindView(R.id.iv_line2)
    ImageView ivLine2;
    @BindView(R.id.iv_line3)
    ImageView ivLine3;
    @BindView(R.id.iv_line4)
    ImageView ivLine4;
    @BindView(R.id.iv_line5)
    ImageView ivLine5;
    @BindView(R.id.iv_line6)
    ImageView ivLine6;
    @BindView(R.id.iv_line_place)
    ImageView ivLinePlace;
    private ContractTemplateAdapter contractTemplateAdapter;
    private ProgressUtils mProgressUtils;
    private final List<String> names = new ArrayList<>();
    private final List<String> sexList = new ArrayList<>();

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_contract_service);
        ButterKnife.bind(mActivity);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        names.add(mActivity.getString(R.string.community));
        names.add(mActivity.getString(R.string.rental_house));
        names.add(mActivity.getString(R.string.factory));
        names.add(mActivity.getString(R.string.resident_workshop));
        names.add(mActivity.getString(R.string.warehouse));
        names.add(mActivity.getString(R.string.shop_storefront));
        names.add(mActivity.getString(R.string.the_mall));
        names.add(mActivity.getString(R.string.the_ohter));
        //
        sexList.add(mActivity.getString(R.string.male));
        sexList.add(mActivity.getString(R.string.female));
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        includeTextTitleTvTitle.setText(mActivity.getString(R.string.contract_info_title));
        includeTextTitleTvSubtitle.setText(mActivity.getString(R.string.contract_service_retake));
        contractTemplateAdapter = new ContractTemplateAdapter(mActivity);
        rvSensorCount.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, true));
        rvSensorCount.setAdapter(contractTemplateAdapter);
        rvSensorCount.setNestedScrollingEnabled(false);
        etContractAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                handleAgeText(text, etContractAge);
            }
        });
        etContractAgeFirst.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                handleAgeText(text, etContractAgeFirst);
            }
        });
        etContractAgePeriod.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                handleAgeText(text, etContractAgePeriod);
            }
        });
        String contractAge = etContractAge.getText().toString();
        handleAgeText(contractAge, etContractAge);
        String contractAgeFirst = etContractAgeFirst.getText().toString();
        handleAgeText(contractAgeFirst, etContractAgeFirst);
        String contractAgePeriod = etContractAgePeriod.getText().toString();
        handleAgeText(contractAgePeriod, etContractAgePeriod);
    }

    private void handleAgeText(String text, EditText editText) {
        if (!TextUtils.isEmpty(text)) {
            try {
                int i = Integer.parseInt(text);
                if (i > 0) {
                    editText.setTextColor(mActivity.getResources().getColor(R.color.c_1dbb99));
                } else {
                    editText.setTextColor(mActivity.getResources().getColor(R.color.c_252525));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected ContractServiceActivityPresenter createPresenter() {
        return new ContractServiceActivityPresenter();
    }

    @Override
    protected void onDestroy() {
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
        names.clear();
        sexList.clear();
        super.onDestroy();
    }

    @Override
    public void showContentText(int originType,int type, String line1, String phone, String line2, String line3, String line4,
                                String line5,
                                String line6, String place, int service_life, int service_life_first, int service_life_period) {
        showContentText(type, line1, phone, line2, line3, line4,
                line5, line6, place);

        if(originType == Constants.CONTRACT_ORIGIN_TYPE_EDIT){
            includeTextTitleTvSubtitle.setVisibility(View.GONE);
        }
        tvContractServicePlace.setText(place);
        etContractAge.setText(String.valueOf(service_life));
        etContractAge.setSelection(etContractAge.getText().toString().length());
        etContractAgeFirst.setText(String.valueOf(service_life_first));
        etContractAgeFirst.setSelection(etContractAge.getText().toString().length());
        etContractAgePeriod.setText(String.valueOf(service_life_period));
        etContractAgePeriod.setSelection(etContractAge.getText().toString().length());

    }

    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> items) {
        SelectDialog dialog = new SelectDialog(mActivity, R.style
                .transparentFrameWindowStyle,
                listener, items);
        if (!mActivity.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    @Override
    public void updateContractTemplateAdapterInfo(ArrayList<ContractsTemplateInfo> data) {
        contractTemplateAdapter.updateList(data);
    }

    @Override
    public void showContentText(int serviceType, String line1, String phone, String line2, String line3, String line4, String line5, String line6, String place) {
        switch (serviceType) {
            case 1:
                etContractServiceLine1.setText(line1);
                etContractServiceLine1.setSelection(line1.length());
                //
                etContractServicePhone.setText(phone);
                etContractServicePhone.setSelection(phone.length());

                //
                etContractServiceLine2.setText(line2);
                etContractServiceLine2.setSelection(line2.length());
                //
                etContractServiceLine3.setText(line3);
                etContractServiceLine3.setSelection(line3.length());
                //
                etContractServiceLine4.setText(line4);
                etContractServiceLine4.setSelection(line4.length());
                //
                etContractServiceLine5.setText(line5);
                etContractServiceLine5.setSelection(line5.length());
                //
                etContractServiceLine6.setText(line6);
                etContractServiceLine6.setSelection(line6.length());
                //
                includeTextTitleTvSubtitle.setVisibility(View.VISIBLE);
                break;
            case 2:
                tvContractServiceLine1.setText(R.string.name);
                etContractServiceLine1.setText(line1);
                etContractServiceLine1.setSelection(line1.length());
                //
                etContractServicePhone.setText(phone);
                etContractServicePhone.setSelection(phone.length());
                //
                tvContractServiceLine2.setText(R.string.sexs);
                ivContractServiceLine2.setVisibility(View.VISIBLE);
                tvShowContractService_line2.setVisibility(View.VISIBLE);
                tvShowContractService_line2.setText(line2);
                etContractServiceLine2.setVisibility(View.GONE);
                llContractServiceLine2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(new SelectDialog.SelectDialogListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                tvShowContractService_line2.setText(sexList.get(position));
                            }
                        }, sexList);
                    }
                });
                //
                tvContractServiceLine3.setText(R.string.identification_number);
                etContractServiceLine3.setText(line3);
                etContractServiceLine3.setSelection(line3.length());
                //
                tvContractServiceLine4.setText(R.string.address);
                etContractServiceLine4.setText(line4);
                etContractServiceLine4.setSelection(line4.length());
                //
                ivLine5.setVisibility(View.GONE);
                llContractServiceLine5.setVisibility(View.GONE);
                //
                ivLine6.setVisibility(View.GONE);
                llContractServiceLine6.setVisibility(View.GONE);
                //
                includeTextTitleTvSubtitle.setVisibility(View.VISIBLE);
                break;
            case 3:
                tvContractServiceLine1.setText(R.string.party_a_customer_name);
                if (line1 != null) {
                    etContractServiceLine1.setText(line1);
                    etContractServiceLine1.setSelection(line1.length());
                    etContractServiceLine1.requestFocus();
                }

                //
                tvContractServiceLine2.setText(R.string.owners_name);
                if (line2 != null) {
                    etContractServiceLine2.setText(line2);
                    etContractServiceLine2.setSelection(line2.length());
                }

                //
                tvContractServiceLine3.setText(R.string.phone_num);
                if (phone != null) {
                    etContractServiceLine3.setText(phone);
                    etContractServiceLine3.setSelection(phone.length());
                }

                //
                tvContractServiceLine4.setText(R.string.identification_number);
                if (line3 != null) {
                    etContractServiceLine4.setText(line3);
                    etContractServiceLine4.setSelection(line3.length());
                }


                //已经存在电话 不显示
                ivLinePhone.setVisibility(View.GONE);
                llContractServicePhone.setVisibility(View.GONE);
                //
                tvContractServiceLine5.setText(R.string.address);
                if (line4 != null) {
                    etContractServiceLine5.setText(line4);
                    etContractServiceLine5.setSelection(line4.length());
                }
                //


                //
                ivLine6.setVisibility(View.GONE);
                llContractServiceLine6.setVisibility(View.GONE);
                //
                includeTextTitleTvSubtitle.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    public void setBtnNextText(String content) {
        btNext.setText(content);
    }

    @Override
    public String getPhoneNumber(int createType) {
        // createTy是3的时候，phone number 跟1 2 不是一个控件，所以这里，用switch 以备扩展
        switch (createType){
            case 1:
            case 2:
                return etContractServicePhone.getText().toString();

        }
        return "";
    }


    @OnClick({R.id.include_text_title_imv_arrows_left,R.id.include_text_title_tv_subtitle, R.id.iv_contract_service_line1, R
            .id.iv_contract_service_line2, R.id.iv_contract_service_line3, R.id.iv_contract_service_line4, R.id
            .iv_contract_service_line5, R.id.iv_contract_service_line6, R.id.ll_contract_service_place_type, R.id
            .iv_contract_age_del, R.id.iv_contract_age_add, R.id.bt_next, R.id.iv_contract_age_first_del, R.id.iv_contract_age_first_add
            , R.id.iv_contract_age_period_del, R.id.iv_contract_age_period_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
                break;
            case R.id.include_text_title_tv_subtitle:
                mPresenter.retake();
                break;
            case R.id.iv_contract_service_line1:
                break;
            case R.id.iv_contract_service_line2:
                break;
            case R.id.iv_contract_service_line3:
                break;
            case R.id.iv_contract_service_line4:
                break;
            case R.id.iv_contract_service_line5:
                break;
            case R.id.iv_contract_service_line6:
                break;
            case R.id.ll_contract_service_place_type:
                showDialog(new SelectDialog.SelectDialogListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        tvContractServicePlace.setText(names.get(position));
                    }
                }, names);
                break;
            case R.id.iv_contract_age_del:
                String contractAgeDel = etContractAge.getText().toString();
                if (!TextUtils.isEmpty(contractAgeDel)) {
                    int i = Integer.parseInt(contractAgeDel);
                    if (i > 1) {
                        i--;
                        etContractAge.setText(i + "");
                    }
                } else {
                    etContractAge.setText(1 + "");
                }
                break;
            case R.id.iv_contract_age_add:
                String contractAgeAdd = etContractAge.getText().toString();
                if (!TextUtils.isEmpty(contractAgeAdd)) {
                    int i = Integer.parseInt(contractAgeAdd);
                    if (i >= 1) {
                        i++;
                        etContractAge.setText(i + "");
                    }
                } else {
                    etContractAge.setText(1 + "");
                }
                break;
            //
            case R.id.iv_contract_age_first_del:
                String contractAgeFirstDel = etContractAgeFirst.getText().toString();
                if (!TextUtils.isEmpty(contractAgeFirstDel)) {
                    int i = Integer.parseInt(contractAgeFirstDel);
                    if (i > 1) {
                        i--;
                        etContractAgeFirst.setText(i + "");
                    }
                } else {
                    etContractAgeFirst.setText(1 + "");
                }
                break;
            case R.id.iv_contract_age_first_add:
                String contractAgeAddFirst = etContractAgeFirst.getText().toString();
                if (!TextUtils.isEmpty(contractAgeAddFirst)) {
                    int i = Integer.parseInt(contractAgeAddFirst);
                    if (i >= 1) {
                        i++;
                        etContractAgeFirst.setText(i + "");
                    }
                } else {
                    etContractAgeFirst.setText(1 + "");
                }
                break;
            case R.id.iv_contract_age_period_del:
                String contractAgeDelPeriod = etContractAgePeriod.getText().toString();
                if (!TextUtils.isEmpty(contractAgeDelPeriod)) {
                    int i = Integer.parseInt(contractAgeDelPeriod);
                    if (i > 1) {
                        i--;
                        etContractAgePeriod.setText(i + "");
                    }
                } else {
                    etContractAgePeriod.setText(1 + "");
                }
                break;
            case R.id.iv_contract_age_period_add:
                String contractAgeAddPeriod = etContractAgePeriod.getText().toString();
                if (!TextUtils.isEmpty(contractAgeAddPeriod)) {
                    int i = Integer.parseInt(contractAgeAddPeriod);
                    if (i >= 1) {
                        i++;
                        etContractAgePeriod.setText(i + "");
                    }
                } else {
                    etContractAgePeriod.setText(1 + "");
                }
                break;
            case R.id.bt_next:
                String line1 = etContractServiceLine1.getText().toString();
                String line2 = etContractServiceLine2.getText().toString();
                //身份证性别tv
                String sex = tvShowContractService_line2.getText().toString();

                String line3 = etContractServiceLine3.getText().toString();
                String line4 = etContractServiceLine4.getText().toString();
                String line5 = etContractServiceLine5.getText().toString();
                String line6 = etContractServiceLine6.getText().toString();
                String phone = etContractServicePhone.getText().toString();
                String contractAgeStr = etContractAge.getText().toString();
                String contractAgeFirstStr = etContractAgeFirst.getText().toString();
                String contractAgePeriodStr = etContractAgePeriod.getText().toString();
                String placeType = tvContractServicePlace.getText().toString();
                ArrayList<ContractsTemplateInfo> data = contractTemplateAdapter.getData();
                mPresenter.startToNext(line1, phone, line2, line3, line4, line5, line6, contractAgeStr, contractAgeFirstStr, contractAgePeriodStr, placeType, sex, data);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.handleResult(requestCode, resultCode, data);
    }

    @Override
    public void startAC(Intent intent) {
        mActivity.startActivity(intent);
    }

    @Override
    public void finishAc() {
        mActivity.finish();
    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {
        mActivity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void setIntentResult(int resultCode) {
        mActivity.setResult(resultCode);
    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {
        mActivity.setResult(resultCode, data);
    }

    @Override
    public void showProgressDialog() {
        mProgressUtils.showProgress();
    }

    @Override
    public void dismissProgressDialog() {
        mProgressUtils.dismissProgress();
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }
}

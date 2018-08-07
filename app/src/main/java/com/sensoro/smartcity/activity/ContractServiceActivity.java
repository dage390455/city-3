package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.ContractTemplateAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IContractServiceActivityView;
import com.sensoro.smartcity.presenter.ContractServiceActivityPresenter;
import com.sensoro.smartcity.server.bean.ContractsTemplateInfo;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroToast;
import com.sensoro.smartcity.widget.popup.SelectDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContractServiceActivity extends BaseActivity<IContractServiceActivityView,
        ContractServiceActivityPresenter> implements IContractServiceActivityView {

    @BindView(R.id.iv_contract_service_back)
    ImageView ivContractServiceBack;
    @BindView(R.id.contract_service_title)
    TextView contractServiceTitle;
    @BindView(R.id.tv_contract_service_title_retake)
    TextView tvContractServiceTitleRetake;
    @BindView(R.id.contract_service_title_layout)
    RelativeLayout contractServiceTitleLayout;
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
    @BindView(R.id.rv_sensor_count)
    RecyclerView rvSensorCount;
    @BindView(R.id.bt_next)
    Button btNext;
    @BindView(R.id.iv_line1)
    ImageView ivLine1;
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
    private final List<String> sexs = new ArrayList<>();

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_contract_service);
        ButterKnife.bind(mActivity);
        initView();
        mPrestener.initData(mActivity);
    }

    private void initView() {
        names.add("老旧小区");
        names.add("工厂");
        names.add("居民作坊");
        names.add("仓库");
        names.add("商铺店面");
        names.add("商场");
        names.add("其他");
        //
        sexs.add("男");
        sexs.add("女");
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        contractTemplateAdapter = new ContractTemplateAdapter(mActivity);
        rvSensorCount.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, true));
        rvSensorCount.setAdapter(contractTemplateAdapter);
        rvSensorCount.setNestedScrollingEnabled(false);
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
        sexs.clear();
        super.onDestroy();
    }

    @Override
    public void showContentText(int type, String line1, String phone, String line2, String line3, String line4,
                                String line5,
                                String line6, int place) {
        switch (type) {
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
                tvContractServiceTitleRetake.setVisibility(View.VISIBLE);
                break;
            case 2:
                tvContractServiceLine1.setText("姓名");
                etContractServiceLine1.setText(line1);
                etContractServiceLine1.setSelection(line1.length());
                //
                etContractServicePhone.setText(phone);
                etContractServicePhone.setSelection(phone.length());
                //
                tvContractServiceLine2.setText("性别");
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
                                tvShowContractService_line2.setText(sexs.get(position));
                            }
                        }, sexs);
                    }
                });
                //
                tvContractServiceLine3.setText("身份证号码");
                etContractServiceLine3.setText(line3);
                etContractServiceLine3.setSelection(line3.length());
                //
                tvContractServiceLine4.setText("住址");
                etContractServiceLine4.setText(line4);
                etContractServiceLine4.setSelection(line4.length());
                //
                ivLine5.setVisibility(View.GONE);
                llContractServiceLine5.setVisibility(View.GONE);
                //
                ivLine6.setVisibility(View.GONE);
                llContractServiceLine6.setVisibility(View.GONE);
                //
                tvContractServiceTitleRetake.setVisibility(View.VISIBLE);
                break;
            case 3:
                tvContractServiceLine1.setText("甲方（客户名称）");
                etContractServiceLine1.requestFocus();
                //
                tvContractServiceLine2.setText("业主姓名");
                //
                tvContractServiceLine3.setText("手机号");
                //
                tvContractServiceLine4.setText("住址");
                //已经存在电话 不显示
                ivLinePhone.setVisibility(View.GONE);
                llContractServicePhone.setVisibility(View.GONE);
                //
                ivLine5.setVisibility(View.GONE);
                llContractServiceLine5.setVisibility(View.GONE);
                //
                ivLine6.setVisibility(View.GONE);
                llContractServiceLine6.setVisibility(View.GONE);
                //
                tvContractServiceTitleRetake.setVisibility(View.GONE);
                break;
            default:
                break;
        }

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
    public void updateContractTemplateAdapterInfo(List<ContractsTemplateInfo> data) {
        contractTemplateAdapter.setData(data);
        contractTemplateAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.iv_contract_service_back, R.id.tv_contract_service_title_retake, R.id.iv_contract_service_line1, R
            .id.iv_contract_service_line2, R.id.iv_contract_service_line3, R.id.iv_contract_service_line4, R.id
            .iv_contract_service_line5, R.id.iv_contract_service_line6, R.id.ll_contract_service_place_type, R.id
            .iv_contract_age_del, R.id.iv_contract_age_add, R.id.bt_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_contract_service_back:
                finishAc();
                break;
            case R.id.tv_contract_service_title_retake:
//                finishAc();
                mPrestener.retake();
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
                    if (i > 0) {
                        i--;
                        etContractAge.setText(i + "");
                    }
                } else {
                    etContractAge.setText(0 + "");
                }
                break;
            case R.id.iv_contract_age_add:
                String contractAgeAdd = etContractAge.getText().toString();
                if (!TextUtils.isEmpty(contractAgeAdd)) {
                    int i = Integer.parseInt(contractAgeAdd);
                    if (i >= 0) {
                        i++;
                        etContractAge.setText(i + "");
                    }
                } else {
                    etContractAge.setText(0 + "");
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
                String line7 = etContractAge.getText().toString();
                String placeType = tvContractServicePlace.getText().toString();

                mPrestener.startToNext(line1, phone, line2, line3, line4, line5, line6, line7, placeType, sex);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPrestener.handleResult(requestCode, resultCode, data);
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
    public void setIntentResult(int requestCode) {
        mActivity.setResult(requestCode);
    }

    @Override
    public void setIntentResult(int requestCode, Intent data) {
        mActivity.setResult(requestCode, data);
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
        SensoroToast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }
}

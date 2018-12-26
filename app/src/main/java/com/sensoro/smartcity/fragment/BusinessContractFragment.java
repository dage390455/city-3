package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.ContractEditorActivity;
import com.sensoro.smartcity.adapter.ContractTemplateAdapter;
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IBusinessContractView;
import com.sensoro.smartcity.presenter.BusinessContractPresenter;
import com.sensoro.smartcity.server.bean.ContractsTemplateInfo;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.popup.SelectDialog;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class BusinessContractFragment extends BaseFragment<IBusinessContractView, BusinessContractPresenter>
        implements IBusinessContractView {

    @BindView(R.id.fg_business_contract_et_business_merchant_name)
    EditText fgBusinessContractEtBusinessMerchantName;
    @BindView(R.id.fg_business_contract_imv_business_merchant_name)
    ImageView fgBusinessContractImvBusinessMerchantName;
    @BindView(R.id.fg_business_contract_et_owner_name)
    EditText fgBusinessContractEtOwnerName;
    @BindView(R.id.fg_business_contract_et_contact_info)
    EditText fgBusinessContractEtContactInfo;
    @BindView(R.id.fg_business_contract_et_social_credit_code)
    EditText fgBusinessContractEtSocialCreditCode;
    @BindView(R.id.fg_business_contract_et_register_address)
    EditText fgBusinessContractEtRegisterAddress;
    @BindView(R.id.fg_business_contract_tv_site_nature)
    TextView fgBusinessContractTvSiteNature;
    @BindView(R.id.fg_business_contract_ll_site_nature)
    LinearLayout fgBusinessContractLlSiteNature;
    @BindView(R.id.fg_business_contract_rc_device)
    RecyclerView fgBusinessContractRcDevice;
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
    ImageView ivContractAgePeriodAdd;
    @BindView(R.id.fg_business_contract_tv_submit)
    TextView fgBusinessContractTvSybmit;
    private ProgressUtils mProgressUtils;
    private ContractTemplateAdapter contractTemplateAdapter;
    private ArrayList<String> sites = new ArrayList<>();

    @Override
    protected void initData(Context activity) {
        initView();
        mPresenter.initData(activity);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mRootFragment.getActivity()).build());
        contractTemplateAdapter = new ContractTemplateAdapter(mRootFragment.getActivity());
        fgBusinessContractRcDevice.setLayoutManager(new LinearLayoutManager(mRootFragment.getActivity(), LinearLayoutManager.VERTICAL, true));
        fgBusinessContractRcDevice.setAdapter(contractTemplateAdapter);
        fgBusinessContractRcDevice.setNestedScrollingEnabled(false);

        String contractAge = etContractAge.getText().toString();
        handleAgeText(contractAge, etContractAge);
        String contractAgeFirst = etContractAgeFirst.getText().toString();
        handleAgeText(contractAgeFirst, etContractAgeFirst);
        String contractAgePeriod = etContractAgePeriod.getText().toString();
        handleAgeText(contractAgePeriod, etContractAgePeriod);

        sites.add(mRootFragment.getActivity().getString(R.string.community));
        sites.add(mRootFragment.getActivity().getString(R.string.rental_house));
        sites.add(mRootFragment.getActivity().getString(R.string.factory));
        sites.add(mRootFragment.getActivity().getString(R.string.resident_workshop));
        sites.add(mRootFragment.getActivity().getString(R.string.warehouse));
        sites.add(mRootFragment.getActivity().getString(R.string.shop_storefront));
        sites.add(mRootFragment.getActivity().getString(R.string.the_mall));
        sites.add(mRootFragment.getActivity().getString(R.string.the_ohter));

        addEtTextChange(etContractAge);
        addEtTextChange(etContractAgeFirst);
        addEtTextChange(etContractAgePeriod);

    }

    private void addEtTextChange(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                handleAgeText(text, editText);
            }
        });
    }
    private void handleAgeText(String text, EditText editText) {
        if (!TextUtils.isEmpty(text)) {
            try {
                int i = Integer.parseInt(text);
                if (i > 0) {
                    editText.setTextColor(mRootFragment.getResources().getColor(R.color.c_29c093));
                } else {
                    editText.setTextColor(mRootFragment.getResources().getColor(R.color.c_252525));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected int initRootViewId() {
        return R.layout.fragment_business_contract;
    }

    @Override
    protected BusinessContractPresenter createPresenter() {
        return new BusinessContractPresenter();
    }

    @Override
    public void startAC(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void finishAc() {
        mRootFragment.getActivity().finish();
    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {
        startActivityForResult(intent,requestCode);
    }

    @Override
    public void setIntentResult(int resultCode) {

    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {

    }

    @Override
    public void onFragmentStart() {

    }

    @Override
    public void onFragmentStop() {

    }

    @Override
    public void onDestroyView() {
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
        super.onDestroyView();
    }

    @Override
    public void showProgressDialog() {
        if (mProgressUtils != null) {
            mProgressUtils.showProgress();
        }
    }

    @Override
    public void dismissProgressDialog() {
        if (mProgressUtils != null) {
            mProgressUtils.dismissProgress();
        }
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }



    @OnClick({R.id.fg_business_contract_imv_business_merchant_name, R.id.fg_business_contract_ll_site_nature,
            R.id.iv_contract_age_del, R.id.iv_contract_age_add, R.id.iv_contract_age_first_del, R.id.iv_contract_age_first_add,
            R.id.iv_contract_age_period_del, R.id.iv_contract_age_period_add,R.id.fg_business_contract_tv_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fg_business_contract_imv_business_merchant_name:
                mPresenter.doTakePhoto();
                break;
            case R.id.fg_business_contract_ll_site_nature:
                AppUtils.showDialog(mRootFragment.getActivity(),new SelectDialog.SelectDialogListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        fgBusinessContractTvSiteNature.setText(sites.get(position));
                    }
                }, sites);
                break;
            case R.id.iv_contract_age_del:
                contractAgeAddOrSubtract(etContractAge,false);
                break;
            case R.id.iv_contract_age_add:
                contractAgeAddOrSubtract(etContractAge,true);
                break;
            case R.id.iv_contract_age_first_del:
                contractAgeAddOrSubtract(etContractAgeFirst,false);
                break;
            case R.id.iv_contract_age_first_add:
                contractAgeAddOrSubtract(etContractAgeFirst,true);
                break;
            case R.id.iv_contract_age_period_del:
                contractAgeAddOrSubtract(etContractAgePeriod,false);
                break;
            case R.id.iv_contract_age_period_add:
                contractAgeAddOrSubtract(etContractAgePeriod,true);
                break;
            case R.id.fg_business_contract_tv_submit:
                String enterpriseName = fgBusinessContractEtBusinessMerchantName.getText().toString();
                String customerName = fgBusinessContractEtOwnerName.getText().toString();
                String customerPhone = fgBusinessContractEtContactInfo.getText().toString();
                String enterpriseCardId = fgBusinessContractEtSocialCreditCode.getText().toString();
                String customerAddress = fgBusinessContractEtRegisterAddress.getText().toString();
                String placeType = fgBusinessContractTvSiteNature.getText().toString();

                String contractAgeStr = etContractAge.getText().toString();
                String contractAgeFirstStr = etContractAgeFirst.getText().toString();
                String contractAgePeriodStr = etContractAgePeriod.getText().toString();
                ArrayList<ContractsTemplateInfo> data = contractTemplateAdapter.getData();
                mPresenter.doSubmit(enterpriseName,customerName,customerPhone,enterpriseCardId,customerAddress,placeType,
                        contractAgeStr, contractAgeFirstStr, contractAgePeriodStr, data);
                break;
        }
    }

    private void contractAgeAddOrSubtract(EditText editText,boolean isAdd) {
        String contractAgeAdd = editText.getText().toString();
        if (!TextUtils.isEmpty(contractAgeAdd)) {
            int i = Integer.parseInt(contractAgeAdd);
            if(isAdd){
                if (i >= 1) {
                    i++;
                }
            }else{
                if (i > 1) {
                    i--;
                }
            }
            editText.setText(String.valueOf(i));

        } else {
            editText.setText(String.valueOf(1));
        }
    }

    @Override
    public void updateContractTemplateAdapterInfo(ArrayList<ContractsTemplateInfo> data) {
        contractTemplateAdapter.updateList(data);
    }

    @Override
    public void setBusinessMerchantName(String enterpriseName) {
        fgBusinessContractEtBusinessMerchantName.setText(enterpriseName);
    }

    @Override
    public void setOwnerName(String customerName) {
        fgBusinessContractEtOwnerName.setText(customerName);
    }

    @Override
    public void setRegisterAddress(String customerAddress) {
        fgBusinessContractEtRegisterAddress.setText(customerAddress);
    }

    @Override
    public void setSocialCreatedId(String enterpriseCardId) {
        fgBusinessContractEtSocialCreditCode.setText(enterpriseCardId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.handActivityResult(requestCode, resultCode, data);
    }

    public void doCreateContract() {
        mPresenter.doCreateContract();
    }
}

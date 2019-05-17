package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.ContractTemplateAdapter;
import com.sensoro.common.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IPersonalContractView;
import com.sensoro.smartcity.presenter.PersonalContractPresenter;
import com.sensoro.common.server.bean.ContractsTemplateInfo;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.smartcity.widget.popup.SelectDialog;
import com.sensoro.smartcity.widget.toast.SensoroSuccessToast;
import com.sensoro.common.widgets.SensoroToast;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class PersonalContractFragment extends BaseFragment<IPersonalContractView, PersonalContractPresenter>
        implements IPersonalContractView {
    @BindView(R.id.fg_personal_contract_et_part_a)
    EditText fgPersonalContractEtPartA;
    @BindView(R.id.fg_personal_contract_et_owner_name)
    EditText fgPersonalContractEtOwnerName;
    @BindView(R.id.fg_personal_contract_imv_owner_name)
    ImageView fgPersonalContractImvOwnerName;
    @BindView(R.id.fg_personal_contract_et_contact_info)
    EditText fgPersonalContractEtContactInfo;
    @BindView(R.id.fg_personal_contract_et_id_card)
    EditText fgPersonalContractEtIdCard;
    @BindView(R.id.fg_personal_contract_et_home_address)
    EditText fgPersonalContractEtHomeAddress;
    @BindView(R.id.fg_personal_contract_tv_site_nature)
    TextView fgPersonalContractTvSiteNature;
    @BindView(R.id.fg_personal_contract_ll_site_nature)
    LinearLayout fgPersonalContractLlSiteNature;
    @BindView(R.id.fg_personal_contract_rc_device)
    RecyclerView fgPersonalContractRcDevice;
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
    @BindView(R.id.fg_personal_contract_tv_submit)
    TextView fgPersonalContractTvSubmit;
    private ProgressUtils mProgressUtils;
    private ContractTemplateAdapter contractTemplateAdapter;
    private ArrayList<String> sites = new ArrayList<>();


    @Override
    protected void initData(Context activity) {
        initView();
        mPresenter.initData(activity, getArguments());
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mRootFragment.getActivity()).build());
        contractTemplateAdapter = new ContractTemplateAdapter(mRootFragment.getActivity());
        fgPersonalContractRcDevice.setLayoutManager(new LinearLayoutManager(mRootFragment.getActivity(), LinearLayoutManager.VERTICAL, true));
        fgPersonalContractRcDevice.setAdapter(contractTemplateAdapter);
        fgPersonalContractRcDevice.setNestedScrollingEnabled(false);

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
                    editText.setTextColor(mRootFragment.getResources().getColor(R.color.c_1dbb99));
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
        return R.layout.fragment_personal_contract;
    }

    @Override
    protected PersonalContractPresenter createPresenter() {
        return new PersonalContractPresenter();
    }

    @Override
    public void startAC(Intent intent) {
        Objects.requireNonNull(mRootFragment.getActivity()).startActivity(intent);
    }

    @Override
    public void finishAc() {
        Objects.requireNonNull(mRootFragment.getActivity()).finish();
    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {
        Objects.requireNonNull(mRootFragment.getActivity()).startActivityForResult(intent, requestCode);
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
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }


    @OnClick({R.id.fg_personal_contract_imv_owner_name, R.id.fg_personal_contract_ll_site_nature,
            R.id.iv_contract_age_del, R.id.iv_contract_age_add, R.id.iv_contract_age_first_del, R.id.iv_contract_age_first_add,
            R.id.iv_contract_age_period_del, R.id.iv_contract_age_period_add, R.id.fg_personal_contract_tv_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fg_personal_contract_imv_owner_name:
                mPresenter.doTakePhoto();
                break;
            case R.id.fg_personal_contract_ll_site_nature:
                AppUtils.showDialog(mRootFragment.getActivity(), new SelectDialog.SelectDialogListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        setSiteNature(sites.get(position));
                    }
                }, sites);
                break;
            case R.id.iv_contract_age_del:
                contractAgeAddOrSubtract(etContractAge, false);
                break;
            case R.id.iv_contract_age_add:
                contractAgeAddOrSubtract(etContractAge, true);
                break;
            case R.id.iv_contract_age_first_del:
                contractAgeAddOrSubtract(etContractAgeFirst, false);
                break;
            case R.id.iv_contract_age_first_add:
                contractAgeAddOrSubtract(etContractAgeFirst, true);
                break;
            case R.id.iv_contract_age_period_del:
                contractAgeAddOrSubtract(etContractAgePeriod, false);
                break;
            case R.id.iv_contract_age_period_add:
                contractAgeAddOrSubtract(etContractAgePeriod, true);
                break;
            case R.id.fg_personal_contract_tv_submit:
                String partA = fgPersonalContractEtPartA.getText().toString();
                String ownerName = fgPersonalContractEtOwnerName.getText().toString();
                String contactInfo = fgPersonalContractEtContactInfo.getText().toString();
                String idCard = fgPersonalContractEtIdCard.getText().toString();
                String homeAddress = fgPersonalContractEtHomeAddress.getText().toString();
                String siteNature = fgPersonalContractTvSiteNature.getText().toString();

                String contractAgeStr = etContractAge.getText().toString();
                String contractAgeFirstStr = etContractAgeFirst.getText().toString();
                String contractAgePeriodStr = etContractAgePeriod.getText().toString();
                ArrayList<ContractsTemplateInfo> data = contractTemplateAdapter.getData();
                mPresenter.doSubmit(partA, ownerName, contactInfo, idCard, homeAddress, siteNature, contractAgeStr, contractAgeFirstStr, contractAgePeriodStr, data);
                break;
        }
    }

    private void contractAgeAddOrSubtract(EditText editText, boolean isAdd) {
        String contractAgeAdd = editText.getText().toString();
        if (!TextUtils.isEmpty(contractAgeAdd)) {
            int i = Integer.parseInt(contractAgeAdd);
            if (isAdd) {
                if (i >= 1) {
                    i++;
                }
            } else {
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
    public void setOwnerName(String name) {
        fgPersonalContractEtOwnerName.setText(name);
    }

    @Override
    public void setIdCardNumber(String idNumber) {
        fgPersonalContractEtIdCard.setText(idNumber);
    }

    @Override
    public void setHomeAddress(String address) {
        fgPersonalContractEtHomeAddress.setText(address);
    }

    @Override
    public void setPartAName(String customerEnterpriseName) {
        fgPersonalContractEtPartA.setText(customerEnterpriseName);
    }

    @Override
    public void setContactNumber(String contactNumber) {
        fgPersonalContractEtContactInfo.setText(contactNumber);
    }

    @Override
    public void setSiteNature(String placeType) {
        fgPersonalContractTvSiteNature.setText(placeType);
    }

    @Override
    public ArrayList<ContractsTemplateInfo> getContractTemplateList() {
        return contractTemplateAdapter.getData();
    }

    @Override
    public void setServeAge(String serverAge) {
        etContractAge.setText(serverAge);
    }

    @Override
    public void setFirstAge(String firstAge) {
        etContractAgeFirst.setText(firstAge);
    }

    @Override
    public void setPeriodAge(String periodAge) {
        etContractAgePeriod.setText(periodAge);
    }

    @Override
    public void setTvSubmitText(String text) {
        fgPersonalContractTvSubmit.setText(text);
    }

    @Override
    public void showSaveSuccessToast() {
        SensoroSuccessToast.getInstance().showToast(mRootFragment.getActivity(), Toast.LENGTH_SHORT, mRootFragment.getString(R.string.save_success));
    }

    @Override
    public void cancelSuccessToast() {
        SensoroSuccessToast.getInstance().cancelToast();
    }

    @Override
    public void onDestroy() {
        SensoroSuccessToast.getInstance().cancelToast();
        super.onDestroy();
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

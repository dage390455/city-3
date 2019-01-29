package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.ContractTemplateShowAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IContractDetailView;
import com.sensoro.smartcity.presenter.ContractDetailPresenter;
import com.sensoro.smartcity.server.bean.ContractsTemplateInfo;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContractDetailActivity extends BaseActivity<IContractDetailView, ContractDetailPresenter>
        implements IContractDetailView {

    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R.id.include_text_title_cl_root)
    ConstraintLayout includeTextTitleClRoot;
    @BindView(R.id.ac_contract_detail_tv_part_a_enterprise)
    TextView acContractDetailTvPartAEnterprise;
    @BindView(R.id.ac_contract_detail_tv_part_a)
    TextView acContractDetailTvPartA;
    @BindView(R.id.ac_contract_detail_tv_owner_customer_name)
    TextView acContractDetailTvOwnerCustomerName;
    @BindView(R.id.ac_contract_detail_tv_owner_name)
    TextView acContractDetailTvOwnerName;
    @BindView(R.id.ac_contract_detail_tv_contact_info)
    TextView acContractDetailTvContactInfo;
    @BindView(R.id.ac_contract_detail_tv_id_card_enterprise_id)
    TextView acContractDetailTvIdCardEnterpriseId;
    @BindView(R.id.ac_contract_detail_tv_id_card)
    TextView acContractDetailTvIdCard;
    @BindView(R.id.ac_contract_detail_tv_home_register_address)
    TextView acContractDetailTvHomeRegisterAddress;
    @BindView(R.id.ac_contract_detail_tv_home_address)
    TextView acContractDetailTvHomeAddress;
    @BindView(R.id.ac_contract_detail_tv_site_nature)
    TextView acContractDetailTvSiteNature;
    @BindView(R.id.ac_contract_detail_rc_device)
    RecyclerView acContractDetailRcDevice;
    @BindView(R.id.ac_contract_detail_serve_life)
    TextView acContractDetailServeLife;
    @BindView(R.id.ac_contract_detail_first_age)
    TextView acContractDetailFirstAge;
    @BindView(R.id.ac_contract_detail_period_age)
    TextView acContractDetailPeriodAge;
    @BindView(R.id.ac_contract_detail_tv_contract_preview)
    TextView acContractDetailTvContractPreview;
    @BindView(R.id.ac_contract_detail_tv_create_qr_code)
    TextView acContractDetailTvCreateQrCode;
    @BindView(R.id.ac_contract_detail_tv_contract_number)
    TextView acContractDetailTvContractNumber;
    @BindView(R.id.ac_contract_detail_tv_contract_status)
    TextView acContractDetailTvContractStatus;
    @BindView(R.id.ac_contract_detail_imv_pay)
    ImageView acContractDetailImvPay;
    @BindView(R.id.ac_contract_detail_tv_contract_time)
    TextView acContractDetailTvContractTime;
    @BindView(R.id.ac_contract_detail_tv_contract_more)
    TextView acContractDetailTvContractMore;
    @BindView(R.id.ac_contract_detail_ll_contract_look_qr_code)
    LinearLayout acContractDetailLlContractLookQrCode;
    @BindView(R.id.ac_contract_detail_tv_contract_create_time)
    TextView acContractDetailTvContractCreateTime;
    @BindView(R.id.ac_contract_detail_tv_contract_pay_time)
    TextView acContractDetailTvContractPayTime;
    @BindView(R.id.ac_contract_detail_view_contract_pay_time)
    View acContractDetailViewContractPayTime;
    @BindView(R.id.ac_contract_detail_ll_expand)
    LinearLayout acContractDetailLlExpand;
    private ProgressUtils mProgressUtils;
    private ContractTemplateShowAdapter contractTemplateShowAdapter;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_contract_detail);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        includeTextTitleTvTitle.setText(mActivity.getString(R.string.contract_detail));
        includeTextTitleTvSubtitle.setText(mActivity.getString(R.string.title_edit));
        includeTextTitleTvSubtitle.setTextColor(mActivity.getResources().getColor(R.color.c_29c093));
        includeTextTitleTvSubtitle.setVisibility(View.GONE);

        initRCDevices();
    }

    private void initRCDevices() {
        contractTemplateShowAdapter = new ContractTemplateShowAdapter(mActivity);
        acContractDetailRcDevice.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, true));
        acContractDetailRcDevice.setAdapter(contractTemplateShowAdapter);
        acContractDetailRcDevice.setNestedScrollingEnabled(false);
    }

    @Override
    protected ContractDetailPresenter createPresenter() {
        return new ContractDetailPresenter();
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

    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {

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
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_LONG).show();
    }


    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.ac_contract_detail_tv_contract_preview, R.id.ac_contract_detail_tv_create_qr_code
            , R.id.include_text_title_tv_subtitle, R.id.ac_contract_detail_tv_contract_more, R.id.ac_contract_detail_ll_contract_look_qr_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
                break;
            case R.id.include_text_title_tv_subtitle:
                mPresenter.doEditContract();
                break;
            case R.id.ac_contract_detail_tv_contract_preview:
                mPresenter.doPreviewActivity();
                break;
            case R.id.ac_contract_detail_tv_create_qr_code:
            case R.id.ac_contract_detail_ll_contract_look_qr_code:
                mPresenter.doViewContractQrCode();
                break;
            case R.id.ac_contract_detail_tv_contract_more:
                doMore();
                break;
        }
    }

    private void doMore() {
        if (acContractDetailLlExpand.getVisibility() == View.VISIBLE) {
            Drawable drawable = getResources().getDrawable(R.drawable.contract_expand_down);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            acContractDetailTvContractMore.setCompoundDrawables(null, null, drawable, null);
            acContractDetailLlExpand.setVisibility(View.GONE);
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.contract_expand_up);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            acContractDetailTvContractMore.setCompoundDrawables(null, null, drawable, null);
            acContractDetailLlExpand.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setSignStatus(boolean isSigned) {
        includeTextTitleTvSubtitle.setVisibility(isSigned ? View.GONE : View.VISIBLE);

        acContractDetailTvContractStatus.setText(isSigned ? R.string.signed : R.string.not_signed);
        acContractDetailTvContractStatus.setTextColor(isSigned ? getResources().getColor(R.color.c_29c093) :
                getResources().getColor(R.color.c_ff8d34));
        acContractDetailLlContractLookQrCode.setVisibility(isSigned ? View.GONE : View.VISIBLE);
        acContractDetailTvContractMore.setVisibility(isSigned ? View.VISIBLE : View.GONE);

        acContractDetailTvContractPreview.setVisibility(isSigned ? View.GONE : View.VISIBLE);
        acContractDetailTvCreateQrCode.setText(mActivity.getString(isSigned ? R.string.view_signed_contract : R.string.view_contract_qr_code));
    }

    @Override
    public void setCustomerEnterpriseName(String customerEnterpriseName) {
        acContractDetailTvPartA.setText(customerEnterpriseName);
    }

    @Override
    public void setCustomerName(String customerName) {
        acContractDetailTvOwnerName.setText(customerName);
    }

    @Override
    public void setCustomerPhone(String customerPhone) {
        acContractDetailTvContactInfo.setText(customerPhone);
    }

    @Override
    public void setCustomerAddress(String customerAddress) {
        acContractDetailTvHomeAddress.setText(customerAddress);
    }

    @Override
    public void setPlaceType(String placeType) {
        acContractDetailTvSiteNature.setText(placeType);
    }

    @Override
    public void setCardIdOrEnterpriseId(String cardOrEnterpriseId) {
        acContractDetailTvIdCard.setText(cardOrEnterpriseId);
    }

    @Override
    public void setTipText(int contractType) {
        switch (contractType) {
            case 1:
                acContractDetailTvPartAEnterprise.setText(mActivity.getString(R.string.business_merchant_name));
                acContractDetailTvOwnerCustomerName.setText(mActivity.getString(R.string.legal_name));
                acContractDetailTvIdCardEnterpriseId.setText(mActivity.getString(R.string.social_credit_code));
                acContractDetailTvHomeRegisterAddress.setText(mActivity.getString(R.string.register_address));
                break;
            case 2:
                acContractDetailTvPartAEnterprise.setText(mActivity.getString(R.string.party_a_customer_name));
                acContractDetailTvOwnerCustomerName.setText(mActivity.getString(R.string.owners_name));
                acContractDetailTvIdCardEnterpriseId.setText(mActivity.getString(R.string.identification_number));
                acContractDetailTvHomeRegisterAddress.setText(mActivity.getString(R.string.home_address));
                break;
        }
    }

    @Override
    public void setContractCreateTime(String createdAt) {
        acContractDetailTvContractCreateTime.setText(createdAt);
    }


    @Override
    public void updateContractTemplateAdapterInfo(List<ContractsTemplateInfo> devices) {
        contractTemplateShowAdapter.updateList(devices);
    }

    @Override
    public void setServerAge(String serverAge) {
        acContractDetailServeLife.setText(serverAge);
    }

    @Override
    public void setPeriodAge(String periodAge) {
        acContractDetailPeriodAge.setText(periodAge);
    }

    @Override
    public void setFirstAge(String firstAge) {
        acContractDetailFirstAge.setText(firstAge);
    }

    @Override
    public void setContractTime(String time) {
        acContractDetailTvContractTime.setText(time);
    }

    @Override
    public void setContractNumber(String contractNumber) {
        acContractDetailTvContractNumber.setText(contractNumber);
    }

    @Override
    public void setContractOrder(boolean isSuccess, String payTime) {
        acContractDetailViewContractPayTime.setVisibility(isSuccess ? View.VISIBLE : View.GONE);
        acContractDetailTvContractPayTime.setVisibility(isSuccess ? View.VISIBLE : View.GONE);
        acContractDetailImvPay.setVisibility(isSuccess ? View.VISIBLE : View.GONE);
        if (!TextUtils.isEmpty(payTime)) {
            acContractDetailTvContractPayTime.setText(payTime);
        }
    }

}

package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IContractDetailView;
import com.sensoro.smartcity.presenter.ContractDetailPresenter;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.toast.SensoroToast;

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
    @BindView(R.id.fg_personal_contract_tv_site_nature)
    TextView fgPersonalContractTvSiteNature;
    @BindView(R.id.fg_personal_contract_rc_device)
    RecyclerView fgPersonalContractRcDevice;
    @BindView(R.id.ac_contract_detail_sign_status)
    TextView acContractDetailSignStatus;
    @BindView(R.id.ac_contract_detail_serve_life)
    TextView acContractDetailServeLife;
    @BindView(R.id.ac_contract_detail_first_age)
    TextView acContractDetailFirstAge;
    @BindView(R.id.ac_contract_detail_period_age)
    TextView acContractDetailPeriodAge;
    @BindView(R.id.ac_contract_detail_created_time)
    TextView acContractDetailCreatedTime;
    @BindView(R.id.ac_contract_detail_sign_time)
    TextView acContractDetailSignTime;
    @BindView(R.id.ac_contract_detail_tv_contract_preview)
    TextView acContractDetailTvContractPreview;
    @BindView(R.id.ac_contract_detail_tv_create_qr_code)
    TextView acContractDetailTvCreateQrCode;
    private ProgressUtils mProgressUtils;

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
    }

    @Override
    protected ContractDetailPresenter createPresenter() {
        return new ContractDetailPresenter();
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
        startActivityForResult(intent, requestCode);
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
    ,R.id.include_text_title_tv_subtitle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
                break;
            case R.id.include_text_title_tv_subtitle:
                break;
            case R.id.ac_contract_detail_tv_contract_preview:
                break;
            case R.id.ac_contract_detail_tv_create_qr_code:
                break;
        }
    }
}

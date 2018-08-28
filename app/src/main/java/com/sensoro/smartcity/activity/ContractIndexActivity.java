package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IContractIndexActivityView;
import com.sensoro.smartcity.presenter.ContractIndexActivityPresenter;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContractIndexActivity extends BaseActivity<IContractIndexActivityView, ContractIndexActivityPresenter>
        implements IContractIndexActivityView {
    @BindView(R.id.contract_index_back)
    ImageView contractIndexBack;
    @BindView(R.id.contract_index_name_license_rl)
    RelativeLayout contractIndexNameLicenseRl;
    @BindView(R.id.contract_index_person_rl)
    RelativeLayout contractIndexPersonRl;
    @BindView(R.id.contract_index_manual_rl)
    RelativeLayout contractIndexManualRl;
    private ProgressUtils mProgressUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_contract_index);
        ButterKnife.bind(mActivity);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
    }

    @Override
    protected void onDestroy() {
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
        super.onDestroy();
    }

    @Override
    protected ContractIndexActivityPresenter createPresenter() {
        return new ContractIndexActivityPresenter();
    }

    @OnClick({R.id.contract_index_back, R.id.contract_index_name_license_rl, R.id.contract_index_person_rl, R.id
            .contract_index_manual_rl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.contract_index_back:
                finishAc();
                break;
            case R.id.contract_index_name_license_rl:
                mPresenter.startLicense();
                break;
            case R.id.contract_index_person_rl:
                mPresenter.startPerson();
                break;
            case R.id.contract_index_manual_rl:
                mPresenter.startManual();
                break;
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.handActivityResult(requestCode, resultCode, data);
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
        SensoroToast.INSTANCE.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }
}

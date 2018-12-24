package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IContractCreationView;
import com.sensoro.smartcity.presenter.ContractCreationPresenter;

public class ContractCreationActivity extends BaseActivity<IContractCreationView,ContractCreationPresenter>
implements IContractCreationView{
    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_contract_creation);

        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {

    }

    @Override
    protected ContractCreationPresenter createPresenter() {
        return new ContractCreationPresenter();
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
        startActivityForResult(intent,requestCode);
    }

    @Override
    public void setIntentResult(int resultCode) {

    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {

    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void dismissProgressDialog() {

    }

    @Override
    public void toastShort(String msg) {

    }

    @Override
    public void toastLong(String msg) {

    }
}

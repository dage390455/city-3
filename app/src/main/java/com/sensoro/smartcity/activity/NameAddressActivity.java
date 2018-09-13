package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.NameAddressHistoryAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.INameAddressActivityView;
import com.sensoro.smartcity.presenter.NameAddressActivityPresenter;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NameAddressActivity extends BaseActivity<INameAddressActivityView, NameAddressActivityPresenter>
        implements INameAddressActivityView {
    @BindView(R.id.ac_name_address_et)
    EditText acNameAddressEt;
    @BindView(R.id.ac_nam_address_ll)
    LinearLayout acNamAddressLl;
    @BindView(R.id.ac_nam_address_tv_history)
    TextView acNamAddressTvHistory;
    @BindView(R.id.ac_nam_address_rc_history)
    RecyclerView acNamAddressRcHistory;
    @BindView(R.id.ac_nam_address_tv_save)
    TextView acNamAddressTvSave;
    private NameAddressHistoryAdapter mHistoryAdapter;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_name_address);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        initRcHistory();
    }

    private void initRcHistory() {
        mHistoryAdapter = new NameAddressHistoryAdapter(mActivity);
        SensoroLinearLayoutManager manager = new SensoroLinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        acNamAddressRcHistory.setLayoutManager(manager);
        acNamAddressRcHistory.setAdapter(mHistoryAdapter);
    }

    @Override
    protected NameAddressActivityPresenter createPresenter() {
        return new NameAddressActivityPresenter();
    }

    @Override
    public void startAC(Intent intent) {

    }

    @Override
    public void finishAc() {

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


    @OnClick(R.id.ac_nam_address_tv_save)
    public void onViewClicked() {
    }
}

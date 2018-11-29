package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IScanLoginResultActivityView;
import com.sensoro.smartcity.presenter.ScanLoginResultActivityPresenter;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScanLoginResultActivity extends BaseActivity<IScanLoginResultActivityView,
        ScanLoginResultActivityPresenter> implements IScanLoginResultActivityView {
    private ProgressUtils mProgressUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_scan_result);
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
    protected ScanLoginResultActivityPresenter createPresenter() {
        return new ScanLoginResultActivityPresenter();
    }

    @OnClick({R.id.scan_login, R.id.scan_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.scan_login:
                mPresenter.doLogin();
                break;
            case R.id.scan_cancel:
                mPresenter.doCancel();
                break;
        }
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

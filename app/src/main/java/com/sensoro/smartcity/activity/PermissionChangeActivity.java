package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.immersionbar.ImmersionBar;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.imainviews.IPermissionChangeActivityView;
import com.sensoro.smartcity.presenter.PermissionChangeActivityPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PermissionChangeActivity extends BaseActivity<IPermissionChangeActivityView, PermissionChangeActivityPresenter> implements IPermissionChangeActivityView {

    @BindView(R.id.tv_permission_title)
    TextView tvPermissionTitle;
    @BindView(R.id.tv_permission_message)
    TextView tvPermissionMessage;
    @BindView(R.id.dialog_tip_tv_cancel)
    TextView dialogTipTvCancel;
    @BindView(R.id.dialog_tip_tv_confirm)
    TextView dialogTipTvConfirm;
    private ProgressUtils mProgressUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_permission_change);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
    }

    @Override
    public boolean setMyCurrentActivityTheme() {
        setTheme(R.style.Theme_AppCompat_Translucent);
        return true;
    }

    @Override
    public boolean isActivityOverrideStatusBar() {
        immersionBar = ImmersionBar.with(mActivity);
        immersionBar
                .transparentStatusBar()
                .statusBarDarkFont(true)
                .init();
        return true;
    }

    @Override
    protected PermissionChangeActivityPresenter createPresenter() {
        return new PermissionChangeActivityPresenter();
    }

    @OnClick({R.id.dialog_tip_tv_cancel, R.id.dialog_tip_tv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dialog_tip_tv_cancel:
                mPresenter.doGetNewPermission();
                break;
            case R.id.dialog_tip_tv_confirm:
                mPresenter.doReLogin();
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
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_LONG).show();
    }
}

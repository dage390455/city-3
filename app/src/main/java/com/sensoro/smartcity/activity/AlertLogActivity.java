package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IAlertLogActivityView;
import com.sensoro.smartcity.presenter.AlertLogActivityPresenter;

public class AlertLogActivity extends BaseActivity<IAlertLogActivityView,AlertLogActivityPresenter> implements
IAlertLogActivityView
{
    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.layout_alert_log_activity);
    }

    @Override
    protected AlertLogActivityPresenter createPresenter() {
        return null;
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
}

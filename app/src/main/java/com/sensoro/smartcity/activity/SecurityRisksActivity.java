package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.ISecurityRisksActivityView;
import com.sensoro.smartcity.presenter.SecurityRisksPresenter;
import com.sensoro.smartcity.widget.toast.SensoroToast;

public class SecurityRisksActivity extends BaseActivity<ISecurityRisksActivityView, SecurityRisksPresenter> implements ISecurityRisksActivityView{
    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_security_risks);
    }

    @Override
    protected SecurityRisksPresenter createPresenter() {
        return new SecurityRisksPresenter();
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
        mActivity.startActivityForResult(intent,requestCode);
    }

    @Override
    public void setIntentResult(int resultCode) {

    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {

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

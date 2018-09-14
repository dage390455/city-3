package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IAuthActivityViewTest;
import com.sensoro.smartcity.presenter.AuthActivityPresenterTest;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.NumKeyboard;
import com.sensoro.smartcity.widget.NumberKeyboardView;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by sensoro on 17/6/30.
 */

public class AuthActivityTest extends BaseActivity<IAuthActivityViewTest, AuthActivityPresenterTest> implements IAuthActivityViewTest,
        Constants,NumKeyboard.OnKeyPressListener {


    @BindView(R.id.ac_auth_tv_Verification)
    TextView acAuthTvVerification;
    @BindView(R.id.ac_auth_keyboard)
    NumKeyboard acAuthKeyboard;
    private int textCount;
    private ProgressUtils mProgressUtils;
    private StringBuilder mInputCode;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_auth_test);
        ButterKnife.bind(mActivity);
        initView();

    }

    private void initView() {
        mInputCode = new StringBuilder();
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        acAuthKeyboard.setOnKeyPressListener(this);
        mPresenter.initData(mActivity);
    }

    @Override
    protected AuthActivityPresenterTest createPresenter() {
        return new AuthActivityPresenterTest();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mPresenter.close();
        }
        return super.onKeyDown(keyCode, event);
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
        SensoroToast.INSTANCE.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }

    @Override
    protected void onDestroy() {
        mProgressUtils.destroyProgress();
        LogUtils.loge("onDestroy");
        super.onDestroy();
    }


    @Override
    public void onInertKey(String text) {
        if(mInputCode.length()<6){
            mInputCode.append(text);
            acAuthTvVerification.setText(mInputCode.toString());
            if(mInputCode.length()==6){
                mPresenter.doAuthCheck(mInputCode.toString());
            }

        }

    }

    @Override
    public void onDeleteKey() {
        if (mInputCode.length()>0) {
            mInputCode.deleteCharAt(mInputCode.length()-1);
            acAuthTvVerification.setText(mInputCode.toString());
        }

    }

    @Override
    public void onClearKey(String s) {
        mInputCode.delete(0,mInputCode.length());
        acAuthTvVerification.setText(mInputCode.toString());
    }
}

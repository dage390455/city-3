package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.constant.Constants;
import com.sensoro.smartcity.imainviews.IAuthActivityView;
import com.sensoro.smartcity.presenter.AuthActivityPresenter;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.NumKeyboard;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by sensoro on 17/6/30.
 */

public class AuthActivity extends BaseActivity<IAuthActivityView, AuthActivityPresenter> implements IAuthActivityView,
        Constants, NumKeyboard.OnKeyPressListener {


    @BindView(R.id.ac_auth_tv_Verification)
    TextView acAuthTvVerification;
    @BindView(R.id.ac_auth_keyboard)
    NumKeyboard acAuthKeyboard;
    @BindView(R.id.ac_auth_imv_finish)
    ImageView acAuthImvFinish;
    @BindView(R.id.ac_auth_imv_status)
    ImageView acAuthImvStatus;
    @BindView(R.id.ac_auth_tv_num1)
    TextView acAuthTvNum1;
    @BindView(R.id.ac_auth_tv_num2)
    TextView acAuthTvNum2;
    @BindView(R.id.ac_auth_tv_num3)
    TextView acAuthTvNum3;
    @BindView(R.id.ac_auth_tv_num4)
    TextView acAuthTvNum4;
    @BindView(R.id.ac_auth_tv_num5)
    TextView acAuthTvNum5;
    @BindView(R.id.ac_auth_tv_num6)
    TextView acAuthTvNum6;
    private int textCount;
    private ProgressUtils mProgressUtils;
    private StringBuilder mInputCode;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(mActivity);
        initView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    private void initView() {
        mInputCode = new StringBuilder();
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        acAuthKeyboard.setOnKeyPressListener(this);
        mPresenter.initData(mActivity);
    }

    @Override
    protected AuthActivityPresenter createPresenter() {
        return new AuthActivityPresenter();
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
        SensoroToast.getInstance().makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }

    @Override
    protected void onDestroy() {
        mProgressUtils.destroyProgress();
        try {
            LogUtils.loge("onDestroy");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        super.onDestroy();
    }


    @Override
    public void onInertKey(String text) {
        if (mInputCode.length() < 6) {
            switch (mInputCode.length()) {
                case 0:
                    acAuthTvNum1.setText(text);
                    break;
                case 1:
                    acAuthTvNum2.setText(text);
                    break;
                case 2:
                    acAuthTvNum3.setText(text);
                    break;
                case 3:
                    acAuthTvNum4.setText(text);
                    break;
                case 4:
                    acAuthTvNum5.setText(text);
                    break;
                case 5:
                    acAuthTvNum6.setText(text);
                    break;

            }
            mInputCode.append(text);
//            acAuthTvVerification.setText(mInputCode.toString());
            if (mInputCode.length() == 6) {
                mPresenter.doAuthCheck(mInputCode.toString());
            }

        }

    }

    @Override
    public void onDeleteKey() {
        if (mInputCode.length() > 0) {
            switch (mInputCode.length() - 1) {
                case 0:
                    acAuthTvNum1.setText("");
                    break;
                case 1:
                    acAuthTvNum2.setText("");
                    break;
                case 2:
                    acAuthTvNum3.setText("");
                    break;
                case 3:
                    acAuthTvNum4.setText("");
                    break;
                case 4:
                    acAuthTvNum5.setText("");
                    break;
                case 5:
                    acAuthTvNum6.setText("");
                    break;

            }
            mInputCode.deleteCharAt(mInputCode.length() - 1);
//            acAuthTvVerification.setText(mInputCode.toString());
        }
        if (mInputCode.length() == 0) {
            updateImvStatus(true);
        }

    }

    @Override
    public void onClearKey(String s) {
        mInputCode.delete(0, mInputCode.length());
        acAuthTvNum1.setText("");
        acAuthTvNum2.setText("");
        acAuthTvNum3.setText("");
        acAuthTvNum4.setText("");
        acAuthTvNum5.setText("");
        acAuthTvNum6.setText("");
//        acAuthTvVerification.setText(mInputCode.toString());
        updateImvStatus(true);
    }


    @OnClick(R.id.ac_auth_imv_finish)
    public void onViewClicked() {
        mPresenter.close();
    }

    @Override
    public void updateImvStatus(boolean isSuccess) {
        acAuthImvStatus.setImageResource(isSuccess ? R.drawable.deploy_succeed : R.drawable.deploy_fail);
    }

    @Override
    public void autoFillCode(List<String> codes) {
        if (codes != null && codes.size() == 6) {
            mInputCode.delete(0, mInputCode.length());
            acAuthTvNum1.setText(codes.get(0));
            acAuthTvNum2.setText(codes.get(1));
            acAuthTvNum3.setText(codes.get(2));
            acAuthTvNum4.setText(codes.get(3));
            acAuthTvNum5.setText(codes.get(4));
            acAuthTvNum6.setText(codes.get(5));
            mInputCode.append(codes.get(0));
            mInputCode.append(codes.get(1));
            mInputCode.append(codes.get(2));
            mInputCode.append(codes.get(3));
            mInputCode.append(codes.get(4));
            mInputCode.append(codes.get(5));
        }
    }
}

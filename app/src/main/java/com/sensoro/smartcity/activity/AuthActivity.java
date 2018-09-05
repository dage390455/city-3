package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IAuthActivityView;
import com.sensoro.smartcity.presenter.AuthActivityPresenter;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.NumberKeyboardView;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by sensoro on 17/6/30.
 */

public class AuthActivity extends BaseActivity<IAuthActivityView, AuthActivityPresenter> implements IAuthActivityView, Constants, NumberKeyboardView.OnNumberClickListener {

    @BindView(R.id.auth_keyboard)
    NumberKeyboardView mNumberKeyBoard;
    @BindView(R.id.auth_btn1)
    Button button1;
    @BindView(R.id.auth_btn2)
    Button button2;
    @BindView(R.id.auth_btn3)
    Button button3;
    @BindView(R.id.auth_btn4)
    Button button4;
    @BindView(R.id.auth_btn5)
    Button button5;
    @BindView(R.id.auth_btn6)
    Button button6;
    @BindView(R.id.auth_forward)
    Button forwardBtn;
    private int textCount;
    private ProgressUtils mProgressUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(mActivity);
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        mNumberKeyBoard.setOnNumberClickListener(this);
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
    public void onNumberReturn(String number) {
        if (textCount < 6) {
//            authText.append(number);
            switch (textCount) {
                case 0:
                    button1.setText(number);
                    break;
                case 1:
                    button2.setText(number);
                    break;
                case 2:
                    button3.setText(number);
                    break;
                case 3:
                    button4.setText(number);
                    break;
                case 4:
                    button5.setText(number);
                    break;
                case 5:
                    button6.setText(number);
                    break;
            }
            textCount++;
        }
    }


    @Override
    public void onNumberDelete() {
        if (textCount > 0) {
            switch (textCount) {
                case 1:
                    button1.setText("");
                    break;
                case 2:
                    button2.setText("");
                    break;
                case 3:
                    button3.setText("");
                    break;
                case 4:
                    button4.setText("");
                    break;
                case 5:
                    button5.setText("");
                    break;
                case 6:
                    button6.setText("");
                    break;
            }

            textCount--;

        }
    }

    @OnClick(R.id.auth_iv_close)
    public void close() {
        mPresenter.close();
    }

    @OnClick(R.id.auth_forward)
    public void forward() {
        String code = button1.getText().toString() + button2.getText().toString() + button3.getText().toString() + button4.getText().toString() + button5.getText().toString() + button6.getText().toString();
        mPresenter.doAuthCheck(textCount, code);
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
}

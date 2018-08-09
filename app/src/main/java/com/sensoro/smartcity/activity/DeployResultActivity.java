package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IDeployResultActivityView;
import com.sensoro.smartcity.presenter.DeployResultActivityPresenter;
import com.sensoro.smartcity.widget.SensoroToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sensoro on 17/8/4.
 */

public class DeployResultActivity extends BaseActivity<IDeployResultActivityView, DeployResultActivityPresenter>
        implements IDeployResultActivityView {

    @BindView(R.id.deploy_result_tip_tv)
    TextView tipsTextView;
    @BindView(R.id.deploy_result_error)
    TextView deployResultError;
    @BindView(R.id.deploy_result_sn_tv)
    TextView snTextView;
    @BindView(R.id.deploy_result_name_tv)
    TextView nameTextView;
    @BindView(R.id.deploy_result_content_tv)
    TextView contentTextView;
    @BindView(R.id.deploy_result_status_tv)
    TextView statusTextView;
    @BindView(R.id.deploy_result_signal_tv)
    TextView signalTextView;
    @BindView(R.id.deploy_result_update_tv)
    TextView updateTextView;
    @BindView(R.id.deploy_result_iv)
    ImageView resultImageView;
    @BindView(R.id.deploy_result_address_tv)
    TextView deployResultAddressTv;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deploy_result);
        ButterKnife.bind(mActivity);
        mPrestener.initData(mActivity);
    }


    @Override
    protected DeployResultActivityPresenter createPresenter() {
        return new DeployResultActivityPresenter();
    }


    @Override
    public void refreshSignal(long updateTime, String signal) {
        String signal_text = null;
        long time_diff = System.currentTimeMillis() - updateTime;
        if (signal != null && (time_diff < 300000)) {
            switch (signal) {
                case "good":
                    signal_text = "信号质量：优";
                    signalTextView.setBackground(getResources().getDrawable(R.drawable.shape_signal_good));
                    break;
                case "normal":
                    signal_text = "信号质量：良";
                    signalTextView.setBackground(getResources().getDrawable(R.drawable.shape_signal_normal));
                    break;
                case "bad":
                    signal_text = "信号质量：差";
                    signalTextView.setBackground(getResources().getDrawable(R.drawable.shape_signal_bad));
                    break;
            }
        } else {
            signal_text = "无信号";
            signalTextView.setBackground(getResources().getDrawable(R.drawable.shape_signal_none));
        }
        signalTextView.setText(signal_text);
    }

    @Override
    public void setResultImageView(int resId) {
        resultImageView.setImageResource(resId);
    }

    @Override
    public void setTipsTextView(String text) {
        tipsTextView.setText(text);
    }

    @Override
    public void setSnTextView(String sn) {
        snTextView.setText(sn);
    }

    @Override
    public void setNameTextView(String name) {
        nameTextView.setText(name);
    }


    @Override
    public void setContentTextView(String content) {
        contentTextView.setText(content);
    }

    @Override
    public void setStatusTextView(String status) {
        statusTextView.setText(status);
    }

    @Override
    public void setUpdateTextView(String update) {
        updateTextView.setText(update);
    }

    @Override
    public void setAddressTextView(String address) {
        deployResultAddressTv.setText(address);
    }

    @Override
    public void setDeployResultErrorInfo(String errorInfo) {
        deployResultError.setVisibility(View.VISIBLE);
        deployResultError.setText(errorInfo);
    }

    @Override
    public void setUpdateTextViewVisible(boolean isVisible) {
        updateTextView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setContactAndSignalVisible(boolean isVisible) {
//        contactTextView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        contentTextView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        signalTextView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }


    @OnClick(R.id.deploy_result_back)
    public void back() {
        finishAc();
    }

    @OnClick(R.id.deploy_result_continue_btn)
    public void gotoContinue() {
        mPrestener.gotoContinue();
    }

    @OnClick(R.id.deploy_result_back_home)
    public void backHome() {
        mPrestener.backHome();
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

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
}

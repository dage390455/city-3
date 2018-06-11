package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IDeployManualActivityView;
import com.sensoro.smartcity.presenter.DeployManualActivityPresenter;
import com.sensoro.smartcity.widget.ProgressUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sensoro on 17/11/7.
 */

public class DeployManualActivity extends BaseActivity<IDeployManualActivityView, DeployManualActivityPresenter>
        implements IDeployManualActivityView,
        TextWatcher {


    @BindView(R.id.deploy_manual_close)
    ImageView closeImageView;
    @BindView(R.id.deploy_clear_iv)
    ImageView clearImageView;
    @BindView(R.id.deploy_manual_et)
    EditText contentEditText;
    @BindView(R.id.deploy_manual_btn)
    Button nextButton;
    private ProgressUtils mProgressUtils;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deploy_manual);
        ButterKnife.bind(mActivity);
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        contentEditText.addTextChangedListener(this);
        mPrestener.initData(mActivity);
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
    protected DeployManualActivityPresenter createPresenter() {
        return new DeployManualActivityPresenter();
    }

    @OnClick(R.id.deploy_manual_close)
    public void close() {
        mPrestener.clickClose();
    }

    @OnClick(R.id.deploy_manual_btn)
    public void next() {
        String text = contentEditText.getText().toString();
        mPrestener.clickNext(text);

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            close();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPrestener.handleActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        if (s.length() == 0) {
            clearImageView.setVisibility(View.GONE);
            nextButton.setBackground(getResources().getDrawable(R.drawable.shape_button_normal));
        } else {
            clearImageView.setVisibility(View.VISIBLE);
            nextButton.setBackground(getResources().getDrawable(R.drawable.shape_button));
        }
    }

    @OnClick(R.id.deploy_clear_iv)
    public void clear() {
        contentEditText.getText().clear();
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
        mActivity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void setIntentResult(int requestCode) {

    }

    @Override
    public void setIntentResult(int requestCode, Intent data) {
        mActivity.setResult(requestCode, data);
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
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }
}

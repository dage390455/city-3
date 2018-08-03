package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IContractResultActivityView;
import com.sensoro.smartcity.presenter.ContractResultActivityPresenter;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContractResultActivity extends BaseActivity<IContractResultActivityView,
        ContractResultActivityPresenter> implements IContractResultActivityView {
    @BindView(R.id.iv_result)
    ImageView ivResult;
    @BindView(R.id.bt_share)
    Button btShare;
    @BindView(R.id.iv_contract_result_back)
    ImageView ivContractResultBack;
    @BindView(R.id.tv_result_info)
    TextView tvResultInfo;
    @BindView(R.id.tv_contract_info_title_finish)
    TextView tvContractInfoTitleFinish;
    private ProgressUtils mProgressUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_contract_result);
        ButterKnife.bind(mActivity);
        initView();
        mPrestener.initData(mActivity);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
    }

    @Override
    protected ContractResultActivityPresenter createPresenter() {
        return new ContractResultActivityPresenter();
    }

    @OnClick({R.id.bt_share, R.id.iv_contract_result_back, R.id.tv_contract_info_title_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_share:
                mPrestener.sharePic();
                break;
            case R.id.iv_contract_result_back:
                break;
            case R.id.tv_contract_info_title_finish:
                mPrestener.finish();
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mPrestener.finish();
        return super.onKeyDown(keyCode,event);
    }

    @Override
    protected void onDestroy() {
        mPrestener.onDestroy();
        super.onDestroy();
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        ivResult.setImageBitmap(bitmap);
    }

    @Override
    public void setTextResultInfo(String text) {
        tvResultInfo.setText(text);
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
    public void setIntentResult(int requestCode) {
        mActivity.setResult(requestCode);
    }

    @Override
    public void setIntentResult(int requestCode, Intent data) {
        mActivity.setResult(requestCode, data);
    }
}

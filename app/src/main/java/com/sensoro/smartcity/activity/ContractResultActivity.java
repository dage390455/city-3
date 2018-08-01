package com.sensoro.smartcity.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
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

    @OnClick(R.id.bt_share)
    public void onViewClicked() {
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        ivResult.setImageBitmap(bitmap);
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }
}

package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.contractmanager.R;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.contractmanager.R2;
import com.sensoro.smartcity.imainviews.IContractResultActivityView;
import com.sensoro.smartcity.presenter.ContractResultActivityPresenter;
import com.sensoro.common.widgets.SensoroToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContractResultActivity extends BaseActivity<IContractResultActivityView,
        ContractResultActivityPresenter> implements IContractResultActivityView {
    @BindView(R2.id.iv_result)
    ImageView ivResult;
    @BindView(R2.id.bt_share)
    Button btShare;
    @BindView(R2.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R2.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R2.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R2.id.include_text_title_divider)
    View includeTextTitleViewDivider;
    @BindView(R2.id.tv_result_info)
    TextView tvResultInfo;
//    private ProgressUtils mProgressUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_contract_result);
        ButterKnife.bind(mActivity);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
//        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        includeTextTitleImvArrowsLeft.setImageResource(R.drawable.title_close);
        includeTextTitleTvTitle.setText(mActivity.getString(R.string.contract_info_contract_qr_code));
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        includeTextTitleViewDivider.setVisibility(View.GONE);
    }

    @Override
    protected ContractResultActivityPresenter createPresenter() {
        return new ContractResultActivityPresenter();
    }


    @OnClick({R2.id.bt_share, R2.id.include_text_title_imv_arrows_left})
    public void onViewClicked(View view) {

        int viewID=view.getId();
        if(viewID==R.id.bt_share){
            mPresenter.sharePic();
        }else if(viewID==R.id.include_text_title_imv_arrows_left){
            mPresenter.finish();
        }
    }

    @Override
    public void onBackPressed() {
        mPresenter.finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
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
        SensoroToast.getInstance().makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
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
        mActivity.setResult(resultCode);
    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {
        mActivity.setResult(resultCode, data);
    }
}

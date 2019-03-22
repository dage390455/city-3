package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.SecurityRisksContentAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.ISecurityRisksActivityView;
import com.sensoro.smartcity.presenter.SecurityRisksPresenter;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SecurityRisksActivity extends BaseActivity<ISecurityRisksActivityView, SecurityRisksPresenter> implements ISecurityRisksActivityView {
    @BindView(R.id.include_text_title_tv_cancel)
    TextView includeTextTitleTvCancel;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R.id.rc_content_ac_security_risk)
    RecyclerView rcContentAcSecurityRisk;
    @BindView(R.id.guideline1)
    Guideline guideline1;
    @BindView(R.id.tv_name_ac_security_risks)
    TextView tvNameAcSecurityRisks;
    @BindView(R.id.view_tag_ac_security_risks)
    View viewTagAcSecurityRisks;
    @BindView(R.id.iv_close_ac_security_risks)
    ImageView ivCloseAcSecurityRisks;
    @BindView(R.id.tv_manger_ac_security_risks)
    TextView tvMangerAcSecurityRisks;
    @BindView(R.id.rv_tag_ac_security_risks)
    RecyclerView rvTagAcSecurityRisks;
    @BindView(R.id.cl_tag_ac_security_risks)
    ConstraintLayout clTagAcSecurityRisks;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_security_risks);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        includeTextTitleTvTitle.setText(mActivity.getString(R.string.security_risks));
        includeTextTitleTvSubtitle.setText(mActivity.getString(R.string.save));
        includeTextTitleTvSubtitle.setTextColor(mActivity.getResources().getColor(R.color.c_29c093));

        initContentAdapter();

    }

    private void initContentAdapter() {
        SecurityRisksContentAdapter securityRisksContentAdapter = new SecurityRisksContentAdapter();
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
        mActivity.startActivityForResult(intent, requestCode);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation

    }

    @OnClick({R.id.include_text_title_tv_cancel, R.id.include_text_title_tv_subtitle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_tv_cancel:
                break;
            case R.id.include_text_title_tv_subtitle:
                break;
        }
    }
}

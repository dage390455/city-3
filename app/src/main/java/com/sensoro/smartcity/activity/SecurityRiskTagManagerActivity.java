package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.SecurityRisksTagAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.ISecurityRiskTagManagerView;
import com.sensoro.smartcity.presenter.SecurityRiskTagManagerPresenter;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SecurityRiskTagManagerActivity extends BaseActivity<ISecurityRiskTagManagerView, SecurityRiskTagManagerPresenter>
        implements ISecurityRiskTagManagerView {
    @BindView(R.id.include_text_title_tv_cancel)
    TextView includeTextTitleTvCancel;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R.id.include_text_title_cl_root)
    ConstraintLayout includeTextTitleClRoot;
    @BindView(R.id.rv_location_ac_security_risk_tag_manager)
    RecyclerView rvLocationAcSecurityRiskTagManager;
    @BindView(R.id.rv_behavior_ac_security_risk_tag_manager)
    RecyclerView rvBehaviorAcSecurityRiskTagManager;
    private SecurityRisksTagAdapter mBehaviorAdapter;
    private SecurityRisksTagAdapter mLocationAdapter;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_security_risks_tag_manager);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        includeTextTitleTvSubtitle.setText(mActivity.getString(R.string.save));
        includeTextTitleTvSubtitle.setTextColor(mActivity.getResources().getColor(R.color.c_29c093));

        initLocationAdapter();

        initBehaviorAdapter();
    }

    private void initBehaviorAdapter() {
        mBehaviorAdapter = new SecurityRisksTagAdapter(mActivity,1);
        mBehaviorAdapter.setOnSecurityRisksTagClickListener(new SecurityRisksTagAdapter.SecurityRisksTagClickListener() {
            @Override
            public void onDelItemClick(String tag) {
                mPresenter.doBehaviorTagDel(tag);
            }
        });
        SensoroLinearLayoutManager manager = new SensoroLinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvBehaviorAcSecurityRiskTagManager.setLayoutManager(manager);
        rvBehaviorAcSecurityRiskTagManager.setAdapter(mBehaviorAdapter);
//        securityRisksTagAdapter.updateData(model.action);
    }

    private void initLocationAdapter() {
        mLocationAdapter = new SecurityRisksTagAdapter(mActivity,1);
        mLocationAdapter.setOnSecurityRisksTagClickListener(new SecurityRisksTagAdapter.SecurityRisksTagClickListener() {
            @Override
            public void onDelItemClick(String tag) {
                mPresenter.doLocationTagDel(tag);
            }
        });
        SensoroLinearLayoutManager manager = new SensoroLinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvLocationAcSecurityRiskTagManager.setLayoutManager(manager);
        rvLocationAcSecurityRiskTagManager.setAdapter(mLocationAdapter);
    }

    @Override
    protected SecurityRiskTagManagerPresenter createPresenter() {
        return new SecurityRiskTagManagerPresenter();
    }



    @OnClick({R.id.include_text_title_tv_cancel, R.id.include_text_title_tv_subtitle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_tv_cancel:
                finishAc();
                break;
            case R.id.include_text_title_tv_subtitle:
                mPresenter.doSave();
                break;
        }
    }

    @Override
    public void updateLocationAdapter(ArrayList<String> locationData) {
        mLocationAdapter.updateData(locationData);
    }

    @Override
    public void updateBehaviorAdapter(ArrayList<String> behaviorData) {
        mBehaviorAdapter.updateData(behaviorData);
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

package com.sensoro.smartcity.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IDeployDeviceDetailActivityView;
import com.sensoro.smartcity.presenter.DeployDeviceDetailActivityPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeployDeviceDetailActivity extends BaseActivity<IDeployDeviceDetailActivityView, DeployDeviceDetailActivityPresenter> {
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.ac_deploy_device_detail_et_location)
    EditText acDeployDeviceDetailEtLocation;
    @BindView(R.id.ac_deploy_device_detail_ll_tag)
    LinearLayout acDeployDeviceDetailLlTag;
    @BindView(R.id.ac_deploy_device_detail_et_alert_contact)
    EditText acDeployDeviceDetailEtAlertContact;
    @BindView(R.id.ac_deploy_device_detail_et_alert_phone)
    EditText acDeployDeviceDetailEtAlertPhone;
    @BindView(R.id.ac_deploy_device_detail_ll_deploy_pic)
    LinearLayout acDeployDeviceDetailLlDeployPic;
    @BindView(R.id.ac_deploy_device_detail_ll_fixed_point)
    LinearLayout acDeployDeviceDetailLlFixedPoint;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.actvity_deploy_device_detail);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        includeTextTitleTvTitle.setText("01A01117C6F");
    }

    @Override
    protected DeployDeviceDetailActivityPresenter createPresenter() {
        return new DeployDeviceDetailActivityPresenter();
    }


}

package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.MonitoringPointRcContentAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IMonitoringPointDetailActivityView;
import com.sensoro.smartcity.presenter.MonitoringPointDetailActivityPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MonitoringPointDetailActivity extends BaseActivity<IMonitoringPointDetailActivityView,
        MonitoringPointDetailActivityPresenter> implements IMonitoringPointDetailActivityView {
    @BindView(R.id.include_imv_title_imv_arrows_left)
    ImageView includeImvTitleImvArrowsLeft;
    @BindView(R.id.include_imv_title_tv_title)
    TextView includeImvTitleTvTitle;
    @BindView(R.id.include_imv_title_imv_subtitle)
    ImageView includeImvTitleImvSubtitle;
    @BindView(R.id.ac_monitoring_point_tv_name)
    TextView acMonitoringPointTvName;
    @BindView(R.id.ac_monitoring_point_imv_detail)
    ImageView acMonitoringPointImvDetail;
    @BindView(R.id.ac_monitoring_point_tv_type_time)
    TextView acMonitoringPointTvTypeTime;
    @BindView(R.id.ac_monitoring_point_rc_content)
    RecyclerView acMonitoringPointRcContent;
    @BindView(R.id.ac_monitoring_point_tv_alert_contact)
    TextView acMonitoringPointTvAlertContact;
    @BindView(R.id.ac_monitoring_point_tv_alert_contact_name)
    TextView acMonitoringPointTvAlertContactName;
    @BindView(R.id.ac_monitoring_point_tv_alert_contact_phone)
    TextView acMonitoringPointTvAlertContactPhone;
    @BindView(R.id.ac_monitoring_point_imv_phone)
    ImageView acMonitoringPointImvPhone;
    @BindView(R.id.ac_monitoring_point_tv_location_navigation)
    TextView acMonitoringPointTvLocationNavigation;
    @BindView(R.id.ac_monitoring_point_tv_location)
    TextView acMonitoringPointTvLocation;
    @BindView(R.id.ac_monitoring_point_imv_location)
    ImageView acMonitoringPointImvLocation;
    @BindView(R.id.ac_monitoring_point_tv_operation)
    TextView acMonitoringPointTvOperation;
    @BindView(R.id.ac_monitoring_point_tv_alarm_sound)
    TextView acMonitoringPointTvAlarmSound;
    @BindView(R.id.ac_monitoring_point_tv_hardware_upgrade)
    TextView acMonitoringPointTvHardwareUpgrade;
    private MonitoringPointRcContentAdapter mContentAdapter;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_monitoring_point_detail);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        includeImvTitleTvTitle.setText("监控点详情");
        includeImvTitleImvSubtitle.setVisibility(View.GONE);
        mContentAdapter = new MonitoringPointRcContentAdapter(mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL);
        acMonitoringPointRcContent.setLayoutManager(manager);
        acMonitoringPointRcContent.addItemDecoration(dividerItemDecoration);
        acMonitoringPointRcContent.setAdapter(mContentAdapter);



    }

    @Override
    protected MonitoringPointDetailActivityPresenter createPresenter() {
        return new MonitoringPointDetailActivityPresenter();
    }

    @Override
    public void startAC(Intent intent) {

    }

    @Override
    public void finishAc() {

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

    }

    @Override
    public void dismissProgressDialog() {

    }

    @Override
    public void toastShort(String msg) {

    }

    @Override
    public void toastLong(String msg) {

    }

}

package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.MonitoringPointRcContentAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IMonitoringPointDetailActivityView;
import com.sensoro.smartcity.presenter.MonitoringPointDetailActivityPresenter;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.util.DpUtils;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroToast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MonitoringPointDetailActivity extends BaseActivity<IMonitoringPointDetailActivityView,
        MonitoringPointDetailActivityPresenter> implements IMonitoringPointDetailActivityView, View.OnClickListener {
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
    @BindView(R.id.ac_monitoring_point_cl_alert_contact)
    ConstraintLayout acMonitoringPointClAlertContact;
    @BindView(R.id.ac_monitoring_point_cl_location_navigation)
    ConstraintLayout acMonitoringPointClLocationNavigation;
    private MonitoringPointRcContentAdapter mContentAdapter;
    private ProgressUtils mProgressUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_monitoring_point_detail);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        includeImvTitleTvTitle.setText("监控点详情");
        includeImvTitleImvSubtitle.setImageResource(R.mipmap.ic_detail);
        includeImvTitleImvSubtitle.setPadding(0, DpUtils.dp2px(mActivity, 20), 0, DpUtils.dp2px(mActivity, 20));
        mContentAdapter = new MonitoringPointRcContentAdapter(mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL);
        acMonitoringPointRcContent.setLayoutManager(manager);
        acMonitoringPointRcContent.addItemDecoration(dividerItemDecoration);
        acMonitoringPointRcContent.setAdapter(mContentAdapter);
        acMonitoringPointImvLocation.setOnClickListener(this);
        includeImvTitleImvSubtitle.setOnClickListener(this);
        acMonitoringPointClAlertContact.setOnClickListener(this);
        acMonitoringPointClLocationNavigation.setOnClickListener(this);
        acMonitoringPointImvDetail.setOnClickListener(this);


    }

    @Override
    protected MonitoringPointDetailActivityPresenter createPresenter() {
        return new MonitoringPointDetailActivityPresenter();
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
    public void setTitleNameTextView(String name) {
        acMonitoringPointTvName.setText(name);
    }

    @Override
    public void setUpdateTime(String time) {
        acMonitoringPointTvTypeTime.setText(time);
    }

    @Override
    public void setAlarmStateColor(int color) {
        acMonitoringPointTvName.setTextColor(color);
        acMonitoringPointTvTypeTime.setTextColor(color);
    }

    @Override
    public void setContractName(String contractName) {
        acMonitoringPointTvAlertContactName.setText(contractName);
    }

    @Override
    public void setContractPhone(String contractPhone) {
        acMonitoringPointTvAlertContactPhone.setText(contractPhone);
    }

    @Override
    public void setDeviceLocation(String location) {
        acMonitoringPointTvLocation.setText(location);
    }

    @Override
    public void updateDeviceInfoAdapter(DeviceInfo deviceInfo) {
        mContentAdapter.setDeviceInfo(deviceInfo);
        mContentAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
//        if (mAlarmPopupView != null) {
//            mAlarmPopupView.onDestroyPop();
//        }
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.include_imv_title_imv_subtitle:
                mPresenter.doMore();
                break;
            case R.id.ac_monitoring_point_cl_alert_contact:
//               toastShort("预警联系人");
                mPresenter.doContact();
                break;
            case R.id.ac_monitoring_point_imv_location:
            case R.id.ac_monitoring_point_cl_location_navigation:
//                toastShort("位置导航");
                mPresenter.doNavigation();
                break;
            case R.id.ac_monitoring_point_imv_detail:
                mPresenter.doMonitorHistory();
                break;

        }
    }

}

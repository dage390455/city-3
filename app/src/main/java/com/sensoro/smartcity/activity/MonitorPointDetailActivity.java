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
import com.sensoro.smartcity.adapter.TagAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IMonitorPointDetailActivityView;
import com.sensoro.smartcity.presenter.MonitorPointDetailActivityPresenter;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SensoroToast;
import com.sensoro.smartcity.widget.SpacesItemDecoration;
import com.sensoro.smartcity.widget.TouchRecycleView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MonitorPointDetailActivity extends BaseActivity<IMonitorPointDetailActivityView,
        MonitorPointDetailActivityPresenter> implements IMonitorPointDetailActivityView, View.OnClickListener {
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeImvTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeImvTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
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
    @BindView(R.id.monitor_detail_tv_sn)
    TextView monitorDetailTvSn;
    @BindView(R.id.monitor_detail_rc_tag)
    TouchRecycleView monitorDetailRcTag;
    @BindView(R.id.monitor_detail_tv_battery)
    TextView monitorDetailTvBattery;
    @BindView(R.id.monitor_detail_tv_interval)
    TextView monitorDetailTvInterval;
    @BindView(R.id.ac_monitoring_point_tv_status)
    TextView acMonitoringPointTvStatus;
    @BindView(R.id.ac_monitoring_point_view)
    View acMonitoringPointView;
    @BindView(R.id.ac_monitoring_point_imv_phone_view)
    View acMonitoringPointImvPhoneView;
    @BindView(R.id.ac_monitoring_point_tv_device_type)
    TextView acMonitoringPointTvDeviceType;

    private MonitoringPointRcContentAdapter mContentAdapter;
    private TagAdapter mTagAdapter;
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
        includeImvTitleTvTitle.setText(R.string.monitoring_point_details);
        includeTextTitleTvSubtitle.setText(R.string.alert_log);
        //
        mTagAdapter = new TagAdapter(mActivity, R.color.c_252525, R.color.c_dfdfdf);
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mActivity, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        int spacingInPixels = mActivity.getResources().getDimensionPixelSize(R.dimen.x10);
        monitorDetailRcTag.setIntercept(true);
        monitorDetailRcTag.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
        monitorDetailRcTag.setLayoutManager(layoutManager);
        monitorDetailRcTag.setAdapter(mTagAdapter);
        //
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
        includeTextTitleTvSubtitle.setOnClickListener(this);
        acMonitoringPointImvLocation.setOnClickListener(this);
        acMonitoringPointClAlertContact.setOnClickListener(this);
        acMonitoringPointClLocationNavigation.setOnClickListener(this);
        acMonitoringPointImvDetail.setOnClickListener(this);
        includeImvTitleImvArrowsLeft.setOnClickListener(this);


    }

    @Override
    protected MonitorPointDetailActivityPresenter createPresenter() {
        return new MonitorPointDetailActivityPresenter();
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
    public void setDeviceLocation(String location, boolean isArrowsRight) {
        acMonitoringPointTvLocation.setText(location);
        acMonitoringPointImvLocation.setVisibility(isArrowsRight ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateDeviceInfoAdapter(DeviceInfo deviceInfo) {
        mContentAdapter.setDeviceInfo(deviceInfo);
        mContentAdapter.notifyDataSetChanged();
    }

    @Override
    public void setSNText(String sn) {
        monitorDetailTvSn.setText(sn);
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
            case R.id.include_text_title_tv_subtitle:
                mPresenter.doMonitorHistory();
                break;
            case R.id.ac_monitoring_point_cl_alert_contact:
                mPresenter.doContact();
                break;
            case R.id.ac_monitoring_point_imv_location:
            case R.id.ac_monitoring_point_cl_location_navigation:
                mPresenter.doNavigation();
                break;
            case R.id.ac_monitoring_point_imv_detail:
                //已删除
                break;
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
                break;
        }
    }

    @Override
    public void updateTags(List<String> list) {
        mTagAdapter.updateTags(list);
    }

    @Override
    public void setBatteryInfo(String battery) {
        monitorDetailTvBattery.setText(battery);
    }

    @Override
    public void setInterval(String interval) {
        monitorDetailTvInterval.setText(interval);
    }

    @Override
    public void setStatusInfo(String statusInfo, int textColor) {
        acMonitoringPointTvStatus.setText(statusInfo);
        acMonitoringPointTvStatus.setTextColor(textColor);
    }

    @Override
    public void setContactPhoneIconVisible(boolean isVisible) {
        acMonitoringPointImvPhone.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void setNoContact() {
        acMonitoringPointTvAlertContactName.setText(R.string.no_contact_added);
        acMonitoringPointTvAlertContactName.setTextColor(mActivity.getResources().getColor(R.color.c_a6a6a6));
        acMonitoringPointView.setVisibility(View.GONE);
        acMonitoringPointTvAlertContactPhone.setVisibility(View.GONE);
        acMonitoringPointImvPhone.setVisibility(View.GONE);
        acMonitoringPointImvPhoneView.setVisibility(View.GONE);

    }

    @Override
    public void setDeviceLocationTextColor(int color) {
        acMonitoringPointTvLocation.setTextColor(mActivity.getResources().getColor(color));
    }

    @Override
    public void setDeviceTypeName(String typeName) {
        acMonitoringPointTvDeviceType.setText(typeName);
    }
}

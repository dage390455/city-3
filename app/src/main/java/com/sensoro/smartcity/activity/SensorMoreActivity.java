package com.sensoro.smartcity.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.ISensorMoreActivityView;
import com.sensoro.smartcity.presenter.SensorMoreActivityPresenter;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mabbas007.tagsedittext.TagsEditText;

/**
 * Created by sensoro on 17/7/31.
 */

public class SensorMoreActivity extends BaseActivity<ISensorMoreActivityView, SensorMoreActivityPresenter> implements
        ISensorMoreActivityView {

    @BindView(R.id.sensor_more_back)
    ImageView backImageView;
    @BindView(R.id.sensor_more_title_sn)
    TextView titleTextView;
    @BindView(R.id.sensor_more_tv_sn)
    TextView snTextView;
    @BindView(R.id.sensor_more_tv_name)
    TextView nameTextView;
    @BindView(R.id.sensor_more_tv_type)
    TextView typeTextView;
    @BindView(R.id.sensor_more_tv_status)
    TextView statusTextView;
    @BindView(R.id.sensor_more_sensoro_tag)
    TagsEditText tagSensoroView;
    @BindView(R.id.sensor_more_tv_tag)
    TextView tagTextView;
    @BindView(R.id.sensor_more_tv_battery)
    TextView batteryTextView;
    @BindView(R.id.sensor_more_tv_lon)
    TextView lonTextView;
    @BindView(R.id.sensor_more_tv_lan)
    TextView lanTextView;
    @BindView(R.id.sensor_more_tv_phone)
    TextView phoneTextView;
    @BindView(R.id.sensor_more_tv_report)
    TextView reportTextView;
    @BindView(R.id.sensor_more_tv_interval)
    TextView intervalTextView;
    @BindView(R.id.sensor_more_tv_alarm_setting)
    TextView alarmSettingTextView;
    @BindView(R.id.sensor_more_tv_alarm_recent)
    TextView alarmRecentTextView;
    private ProgressUtils mProgressUtils;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_sensor_more);
        ButterKnife.bind(mActivity);
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        mPresenter.initData(mActivity);
        tagSensoroView.setTagClickable(false);
    }


    @Override
    protected SensorMoreActivityPresenter createPresenter() {
        return new SensorMoreActivityPresenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.requestData();
        mPresenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    @OnClick(R.id.sensor_more_back)
    public void back() {
        mActivity.finish();
    }

    @Override
    protected void onDestroy() {
        mProgressUtils.destroyProgress();
        super.onDestroy();
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
        SensoroToast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }

    @Override
    public void setAlarmRecentInfo(String info) {
        alarmRecentTextView.setText(info);
    }

    @Override
    public void setAlarmRecentInfo(int resID) {
        alarmRecentTextView.setText(resID);
    }

    @Override
    public void setSNText(String sn) {
        snTextView.setText(sn);
    }

    @Override
    public void setTypeText(String type) {
        typeTextView.setText(type);
    }

    @Override
    public void setTagText(String tag) {
        tagTextView.setText(tag);
    }

    @Override
    public void setLongitudeLatitude(String lon, String lat) {
        lonTextView.setText(lon);
        lanTextView.setText(lat);
    }

    @Override
    public void setAlarmSetting(String alarmSetting) {
        alarmSettingTextView.setText(alarmSetting);
    }

    @Override
    public void setInterval(String interval) {
        intervalTextView.setText(interval);
    }

    @Override
    public void setName(String name) {
        nameTextView.setText(name);
    }

    @Override
    public void setName(int resId) {
        nameTextView.setText(resId);
    }

    @Override
    public void setStatusInfo(String status, int background) {
        statusTextView.setText(status);
        statusTextView.setBackground(mActivity.getResources().getDrawable(background));
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        tagSensoroView.onSaveInstanceState();
    }

    @Override
    public void setBatteryInfo(String battery) {
        batteryTextView.setText(battery);
    }

    @Override
    public void setPhoneText(String phone) {
        phoneTextView.setText(phone);
    }

    @Override
    public void setReportText(String report) {
        reportTextView.setText(report);
    }

    @Override
    public void setTags(String[] tags) {
        tagSensoroView.setTags(tags);
    }
}

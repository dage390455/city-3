package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.AlertLogRcContentAdapter;
import com.sensoro.smartcity.adapter.TimerShaftAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IAlertLogActivityView;
import com.sensoro.smartcity.presenter.AlertLogActivityPresenter;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.ScenesData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlertLogActivity extends BaseActivity<IAlertLogActivityView, AlertLogActivityPresenter> implements
        IAlertLogActivityView, TimerShaftAdapter.OnPhotoClickListener {
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.ac_alert_log_tv_name)
    TextView acAlertLogTvName;
    @BindView(R.id.ac_alert_tv_time)
    TextView acAlertTvTime;
    @BindView(R.id.ac_alert_imv_alert_icon)
    ImageView acAlertImvAlertIcon;
    @BindView(R.id.ac_alert_tv_alert_time)
    TextView acAlertTvAlertTime;
    @BindView(R.id.ac_alert_tv_alert_time_text)
    TextView acAlertTvAlertTimeText;
    @BindView(R.id.ac_alert_ll_alert_time)
    LinearLayout acAlertLlAlertTime;
    @BindView(R.id.ac_alert_imv_alert_count_icon)
    ImageView acAlertImvAlertCountIcon;
    @BindView(R.id.ac_alert_tv_alert_count)
    TextView acAlertTvAlertCount;
    @BindView(R.id.ac_alert_tv_alert_count_text)
    TextView acAlertTvAlertCountText;
    @BindView(R.id.ac_alert_ll_alert_count)
    LinearLayout acAlertLlAlertCount;
    @BindView(R.id.ac_alert_tv_contact_owner)
    TextView acAlertTvContactOwner;
    @BindView(R.id.ac_alert_tv_quick_navigation)
    TextView acAlertTvQuickNavigation;
    @BindView(R.id.ac_alert_tv_alert_confirm)
    TextView acAlertTvAlertConfirm;
    @BindView(R.id.ac_alert_rc_content)
    RecyclerView acAlertRcContent;
    private AlertLogRcContentAdapter alertLogRcContentAdapter;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.layout_alert_log_activity);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        initRcContent();
    }

    private void initRcContent() {
        alertLogRcContentAdapter = new AlertLogRcContentAdapter(mActivity);
        alertLogRcContentAdapter.setOnPhotoClickListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        acAlertRcContent.setLayoutManager(manager);
        acAlertRcContent.setAdapter(alertLogRcContentAdapter);
    }

    @Override
    protected AlertLogActivityPresenter createPresenter() {
        return new AlertLogActivityPresenter();
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

    @OnClick({R.id.include_text_title_tv_subtitle, R.id.ac_alert_tv_contact_owner, R.id.ac_alert_tv_quick_navigation, R.id.ac_alert_tv_alert_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_tv_subtitle:
                //历史日志

                break;
            case R.id.ac_alert_tv_contact_owner:
                mPresenter.doContactOwner();
                break;
            case R.id.ac_alert_tv_quick_navigation:
                mPresenter.doNavigation();
                break;
            case R.id.ac_alert_tv_alert_confirm:

                break;
        }
    }

    @Override
    public void setDeviceNameTextView(String name) {
        acAlertLogTvName.setText(name);
    }

    @Override
    public void setCurrentAlarmState(int state, String time) {
//        acAlertLlAlertTime.setBackground();
        acAlertTvAlertTime.setText(time);
    }

    @Override
    public void setAlarmCount(String count) {
        acAlertTvAlertCount.setText(count);
    }

    @Override
    public void updateAlertLogContentAdapter(List<AlarmInfo.RecordInfo> recordInfoList) {
        alertLogRcContentAdapter.setData(recordInfoList);
        alertLogRcContentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPhotoItemClick(int position, List<ScenesData> scenesDataList) {
        mPresenter.clickPhotoItem(position, scenesDataList);
    }
}

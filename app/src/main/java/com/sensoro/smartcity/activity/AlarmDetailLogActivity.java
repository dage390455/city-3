package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.AlertLogRcContentAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IAlarmDetailLogActivityView;
import com.sensoro.smartcity.model.AlarmPopupModel;
import com.sensoro.smartcity.presenter.AlarmDetailLogActivityPresenter;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.popup.AlarmPopUtilsTest;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlarmDetailLogActivity extends BaseActivity<IAlarmDetailLogActivityView, AlarmDetailLogActivityPresenter> implements
        IAlarmDetailLogActivityView, AlertLogRcContentAdapter.OnPhotoClickListener {
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.ac_alert_log_tv_name)
    TextView acAlertLogTvName;
    @BindView(R.id.ac_alert_tv_sn)
    TextView acAlertTvSn;
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
    private AlarmPopUtilsTest mAlarmPopUtils;
    private ProgressUtils mProgressUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_alert_log);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        mAlarmPopUtils = new AlarmPopUtilsTest(mActivity);
        mAlarmPopUtils.setOnPopupCallbackListener(mPresenter);
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
    protected AlarmDetailLogActivityPresenter createPresenter() {
        return new AlarmDetailLogActivityPresenter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AlarmPopUtilsTest.handlePhotoIntent(requestCode,resultCode,data);
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
        SensoroToast.getInstance().makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }

    @OnClick({R.id.include_text_title_tv_subtitle, R.id.ac_alert_tv_contact_owner, R.id.ac_alert_tv_quick_navigation, R.id.ac_alert_tv_alert_confirm, R.id.include_text_title_imv_arrows_left})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_tv_subtitle:
                //历史日志
                mPresenter.doAlarmHistory();
                break;
            case R.id.ac_alert_tv_contact_owner:
                mPresenter.doContactOwner();
                break;
            case R.id.ac_alert_tv_quick_navigation:
                mPresenter.doNavigation();
                break;
            case R.id.ac_alert_tv_alert_confirm:
                mPresenter.showAlarmPopupView();
                break;
            case R.id.include_text_title_imv_arrows_left:
                mPresenter.doBack();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mPresenter.doBack();
    }

    @Override
    public void setDeviceNameTextView(String name) {
        acAlertLogTvName.setText(name);
    }

    @Override
    public void setCurrentAlarmState(int state, String time) {
        switch (state) {
            case 0:
                acAlertLlAlertTime.setBackgroundResource(R.drawable.shape_bg_corner_f4_shadow);
                acAlertImvAlertIcon.setImageResource(R.drawable.alert_time_normal);
                acAlertTvAlertTime.setTextColor(mActivity.getResources().getColor(R.color.c_252525));
                acAlertTvAlertTimeText.setTextColor(mActivity.getResources().getColor(R.color.c_a6a6a6));
                break;
            case 1:
                acAlertLlAlertTime.setBackgroundResource(R.drawable.shape_bg_corner_f34_shadow);
                acAlertImvAlertIcon.setImageResource(R.drawable.alert_time_white);
                acAlertTvAlertTime.setTextColor(Color.WHITE);
                acAlertTvAlertTimeText.setTextColor(Color.WHITE);
                break;
        }
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
    public void showAlarmPopupView(AlarmPopupModel alarmPopupModel) {
        mAlarmPopUtils.show(alarmPopupModel);
    }

    @Override
    public void dismissAlarmPopupView() {
        mAlarmPopUtils.dismiss();
    }

    @Override
    public void setUpdateButtonClickable(boolean canClick) {
        mAlarmPopUtils.setUpdateButtonClickable(canClick);
    }

    @Override
    public void setConfirmText(String text) {
        acAlertTvAlertConfirm.setText(text);
    }

    @Override
    public void setConfirmBg(int resId) {
        acAlertTvAlertConfirm.setBackgroundResource(resId);
    }

    @Override
    public void setConfirmColor(int resId) {
        acAlertTvAlertConfirm.setTextColor(resId);
    }

    @Override
    public void setDeviceSn(String deviceSN) {
        acAlertTvSn.setText(deviceSN);
    }

    @Override
    public void onPhotoItemClick(int position, List<ScenesData> scenesDataList) {
        mPresenter.clickPhotoItem(position, scenesDataList);
    }

    @Override
    protected void onDestroy() {
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
        if (mAlarmPopUtils != null) {
            mAlarmPopUtils.onDestroyPop();
        }
        super.onDestroy();
    }
}

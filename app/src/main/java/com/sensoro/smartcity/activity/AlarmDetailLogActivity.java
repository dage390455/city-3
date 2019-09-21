package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.server.bean.DeviceAlarmLogInfo;
import com.sensoro.common.server.bean.ScenesData;
import com.sensoro.common.utils.HandlePhotoIntentUtils;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.AlertLogRcContentAdapter;
import com.sensoro.smartcity.imainviews.IAlarmDetailLogActivityView;
import com.sensoro.smartcity.model.AlarmPopupModel;
import com.sensoro.smartcity.presenter.AlarmDetailLogActivityPresenter;
import com.sensoro.smartcity.widget.popup.AlarmPopUtils;

import java.util.List;
import java.util.Locale;

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
    @BindView(R.id.iv_alarm_time_ac_alert)
    ImageView acAlertImvAlertIcon;
    @BindView(R.id.ac_alert_tv_alert_time)
    TextView acAlertTvAlertTime;
    @BindView(R.id.ac_alert_tv_alert_time_text)
    TextView acAlertTvAlertTimeText;
    //    @BindView(R.id.ac_alert_ll_alert_time)
//    LinearLayout acAlertLlAlertTime;
    @BindView(R.id.iv_alarm_count_ac_alert)
    ImageView acAlertImvAlertCountIcon;
    @BindView(R.id.ac_alert_tv_alert_count)
    TextView acAlertTvAlertCount;
    @BindView(R.id.ac_alert_tv_alert_count_text)
    TextView acAlertTvAlertCountText;
    //    @BindView(R.id.ac_alert_ll_alert_count)
//    LinearLayout acAlertLlAlertCount;
    @BindView(R.id.ac_alert_tv_contact_owner)
    TextView acAlertTvContactOwner;
    @BindView(R.id.ac_alert_tv_quick_navigation)
    TextView acAlertTvQuickNavigation;
    @BindView(R.id.ac_alert_tv_alert_confirm)
    TextView acAlertTvAlertConfirm;
    @BindView(R.id.iv_alarm_log_close_fire)
    ImageView ivAlarmLogCloseFire;
    @BindView(R.id.ac_alert_rc_content)
    RecyclerView acAlertRcContent;
    @BindView(R.id.tv_live_camera_count_ac_alert)
    TextView tvLiveCameraCountAcAlert;
    @BindView(R.id.ll_camera_live_ac_alert)
    LinearLayout llCameraLiveAcAlert;
    @BindView(R.id.tv_video_camera_count_ac_alert)
    TextView tvVideoCameraCountAcAlert;
    @BindView(R.id.ll_camera_video_ac_alert)
    LinearLayout llCameraVideoAcAlert;
    private AlertLogRcContentAdapter alertLogRcContentAdapter;
    private AlarmPopUtils mAlarmPopUtils;
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
        mAlarmPopUtils = new AlarmPopUtils(mActivity);
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
        HandlePhotoIntentUtils.handlePhotoIntent(requestCode, resultCode, data);
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

    @OnClick({R.id.include_text_title_tv_subtitle, R.id.ac_alert_tv_contact_owner, R.id.ac_alert_tv_quick_navigation,
            R.id.ac_alert_tv_alert_confirm, R.id.iv_alarm_log_close_fire, R.id.include_text_title_imv_arrows_left, R.id.ll_camera_live_ac_alert,
            R.id.ll_camera_video_ac_alert})
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
            case R.id.iv_alarm_log_close_fire:
                mPresenter.doCloseWarn();
                break;
            case R.id.include_text_title_imv_arrows_left:
                mPresenter.doBack();
                break;
            case R.id.ll_camera_video_ac_alert:
                mPresenter.doCameraVideo();
                break;
            case R.id.ll_camera_live_ac_alert:
                mPresenter.doCameraLive();
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
    public void setCurrentAlarmState(String time) {
        acAlertTvAlertTime.setText(time);
    }

    @Override
    public void setAlarmCount(String count) {
        acAlertTvAlertCount.setText(count);
    }

    @Override
    public void updateAlertLogContentAdapter(DeviceAlarmLogInfo deviceAlarmLogInfo) {
        alertLogRcContentAdapter.updateData(deviceAlarmLogInfo);
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
    public void setCameraLiveCount(List<String> liveCount) {
        if (liveCount != null && liveCount.size() > 0) {
            llCameraLiveAcAlert.setVisibility(View.VISIBLE);
            tvLiveCameraCountAcAlert.setText(
                    String.format(Locale.ROOT, "%s%d%s", mActivity.getString(R.string.relation_camera)
                            , liveCount.size(), mActivity.getString(R.string.upload_photo_dialog_append_title3)));
        } else {
            llCameraLiveAcAlert.setVisibility(View.GONE);
        }

    }

    @Override
    public void setLlVideoSizeAndContent(int size, String content) {
        if (size > 0) {
            llCameraVideoAcAlert.setVisibility(View.VISIBLE);
            tvVideoCameraCountAcAlert.setText(content);
        } else {
            llCameraVideoAcAlert.setVisibility(View.GONE);

        }

    }

    @Override
    public void setHistoryLogVisible(boolean visible) {
        includeTextTitleTvSubtitle.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setCloseWarnVisible(boolean visible) {
//        ivAlarmLogCloseFire.setVisibility(visible ? View.VISIBLE : View.GONE);
        ivAlarmLogCloseFire.setVisibility( View.VISIBLE );
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

package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.MonitorDeployDetailPhotoAdapter;
import com.sensoro.smartcity.adapter.MonitoringPointRcContentAdapter;
import com.sensoro.smartcity.adapter.TagAdapter;
import com.sensoro.smartcity.adapter.model.MonitoringPointRcContentAdapterModel;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.constant.MonitorPointOperationCode;
import com.sensoro.smartcity.imainviews.IMonitorPointDetailActivityView;
import com.sensoro.smartcity.presenter.MonitorPointDetailActivityPresenter;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SpacesItemDecoration;
import com.sensoro.smartcity.widget.TouchRecycleView;
import com.sensoro.smartcity.widget.dialog.MonitorPointOperatingDialogUtil;
import com.sensoro.smartcity.widget.dialog.TipOperationDialogUtils;
import com.sensoro.smartcity.widget.divider.BottomNoDividerItemDecoration;
import com.sensoro.smartcity.widget.toast.SensoroSuccessToast;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MonitorPointDetailActivity extends BaseActivity<IMonitorPointDetailActivityView,
        MonitorPointDetailActivityPresenter> implements IMonitorPointDetailActivityView, TipOperationDialogUtils.TipDialogUtilsClickListener, MonitorDeployDetailPhotoAdapter.OnRecyclerViewItemClickListener {
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
    @BindView(R.id.ac_monitoring_point_tv_erasure)
    TextView acMonitoringPointTvErasure;
    @BindView(R.id.ac_monitoring_point_tv_reset)
    TextView acMonitoringPointTvReset;
    @BindView(R.id.ac_monitoring_point_tv_psd)
    TextView acMonitoringPointTvPsd;
    @BindView(R.id.ac_monitoring_point_tv_query)
    TextView acMonitoringPointTvQuery;
    @BindView(R.id.ac_monitoring_point_tv_self_check)
    TextView acMonitoringPointTvSelfCheck;
    @BindView(R.id.ac_monitoring_point_tv_air_switch_config)
    TextView acMonitoringPointTvAirSwitchConfig;
    @BindView(R.id.ac_monitoring_point_ll_operation)
    LinearLayout acMonitoringPointLlOperation;
    @BindView(R.id.ac_monitor_deploy_photo)
    TouchRecycleView acMonitorDeployPhoto;
    MonitorDeployDetailPhotoAdapter mAdapter;

    private MonitoringPointRcContentAdapter mContentAdapter;
    private TagAdapter mTagAdapter;
    private ProgressUtils mProgressUtils;
    private TipOperationDialogUtils mTipUtils;
    private MonitorPointOperatingDialogUtil mOperatingUtil;
    private int mTipDialogType;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_monitoring_point_detail);
        ButterKnife.bind(this);
        initView();
        acMonitoringPointTvErasure.setClickable(false);
        acMonitoringPointTvReset.setClickable(true);
        mPresenter.initData(mActivity);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        includeImvTitleTvTitle.setText(R.string.monitoring_point_details);
        includeTextTitleTvSubtitle.setText(R.string.historical_log);
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
        monitorDetailRcTag.setLayoutManager(layoutManager);
        monitorDetailRcTag.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
        monitorDetailRcTag.setAdapter(mTagAdapter);
        //
        mContentAdapter = new MonitoringPointRcContentAdapter(mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        BottomNoDividerItemDecoration dividerItemDecoration = new BottomNoDividerItemDecoration(mActivity, BottomNoDividerItemDecoration.VERTICAL);
        acMonitoringPointRcContent.setLayoutManager(manager);
        acMonitoringPointRcContent.addItemDecoration(dividerItemDecoration);
        acMonitoringPointRcContent.setAdapter(mContentAdapter);
        //dialog
        initTipDialog();
        initEditDialog();
        initOperatingDialog();
        initMonitorPhoto();

    }

    private void initMonitorPhoto() {
        //
        acMonitorDeployPhoto.setIntercept(false);
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mActivity, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        int spacingInPixels = mActivity.getResources().getDimensionPixelSize(R.dimen.x10);
        acMonitorDeployPhoto.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
        acMonitorDeployPhoto.setLayoutManager(layoutManager);
        mAdapter = new MonitorDeployDetailPhotoAdapter(mActivity);
        acMonitorDeployPhoto.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    private void initEditDialog() {

    }

    private void initOperatingDialog() {
        mOperatingUtil = new MonitorPointOperatingDialogUtil(mActivity, false);
    }

    private void initTipDialog() {
        mTipUtils = new TipOperationDialogUtils(mActivity, false);
        mTipUtils.setTipDialogUtilsClickListener(this);
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
        mActivity.startActivityForResult(intent, requestCode);
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
    public void updateDeviceInfoAdapter(List<MonitoringPointRcContentAdapterModel> data) {
//        mContentAdapter.setDeviceInfo(deviceInfo);
//        mContentAdapter.notifyDataSetChanged();
        mContentAdapter.updateAdapter(data);
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
        if (mTipUtils != null) {
            mTipUtils.destroy();
        }
        if (mOperatingUtil != null) {
            mOperatingUtil.destroy();
        }
        SensoroSuccessToast.INSTANCE.cancelToast();
        super.onDestroy();
    }

    @Override
    public void updateTags(List<String> list) {
        mTagAdapter.updateTags(list);
    }

    @Override
    public void updateMonitorPhotos(List<ScenesData> data) {
        mAdapter.updateImages(data);
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

    @Override
    public void setDeviceOperationVisible(boolean isVisible) {
        acMonitoringPointLlOperation.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setErasureStatus(boolean isClickable) {
        Drawable drawable;
        if (isClickable) {
            drawable = getResources().getDrawable(R.drawable.erasure_clickable);
        } else {
            drawable = getResources().getDrawable(R.drawable.erasure_not_clickable);
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        acMonitoringPointTvErasure.setCompoundDrawables(null, drawable, null, null);
        acMonitoringPointTvErasure.setClickable(isClickable);
        acMonitoringPointTvErasure.setTextColor(getResources().getColor(isClickable ? R.color.c_252525 : R.color.c_a6a6a6));
    }

    @Override
    public void setResetStatus(boolean isClickable) {
        Drawable drawable;
        if (isClickable) {
            drawable = getResources().getDrawable(R.drawable.reset_clickable);
        } else {
            drawable = getResources().getDrawable(R.drawable.reset_not_clickable);
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        acMonitoringPointTvReset.setCompoundDrawables(null, drawable, null, null);
        acMonitoringPointTvReset.setClickable(isClickable);
        acMonitoringPointTvReset.setTextColor(getResources().getColor(isClickable ? R.color.c_252525 : R.color.c_a6a6a6));
    }

    @Override
    public void setSelfCheckStatus(boolean isClickable) {
        Drawable drawable;
        if (isClickable) {
            drawable = getResources().getDrawable(R.drawable.self_check_clickable);
        } else {
            drawable = getResources().getDrawable(R.drawable.self_check_not_clickable);
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        acMonitoringPointTvSelfCheck.setCompoundDrawables(null, drawable, null, null);
        acMonitoringPointTvSelfCheck.setClickable(isClickable);
        acMonitoringPointTvSelfCheck.setTextColor(getResources().getColor(isClickable ? R.color.c_252525 : R.color.c_a6a6a6));
    }

    @Override
    public void setAirSwitchConfigStatus(boolean isClickable) {
        Drawable drawable;
        if (isClickable) {
            drawable = getResources().getDrawable(R.drawable.air_switch_config_clickable);
        } else {
            drawable = getResources().getDrawable(R.drawable.air_switch_config_not_clickable);
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        acMonitoringPointTvAirSwitchConfig.setCompoundDrawables(null, drawable, null, null);
        acMonitoringPointTvAirSwitchConfig.setClickable(isClickable);
        acMonitoringPointTvAirSwitchConfig.setTextColor(getResources().getColor(isClickable ? R.color.c_252525 : R.color.c_a6a6a6));
    }

    @Override
    public void setQueryStatus(boolean isClickable) {
        Drawable drawable;
        if (isClickable) {
            drawable = getResources().getDrawable(R.drawable.query_clickable);
        } else {
            drawable = getResources().getDrawable(R.drawable.query_not_clickable);
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        acMonitoringPointTvQuery.setCompoundDrawables(null, drawable, null, null);
        acMonitoringPointTvQuery.setClickable(isClickable);
        acMonitoringPointTvQuery.setTextColor(getResources().getColor(isClickable ? R.color.c_252525 : R.color.c_a6a6a6));
    }

    @Override
    public void setPsdStatus(boolean isClickable) {
        Drawable drawable;
        if (isClickable) {
            drawable = getResources().getDrawable(R.drawable.psd_clickable);
        } else {
            drawable = getResources().getDrawable(R.drawable.psd_not_clickable);
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        acMonitoringPointTvPsd.setCompoundDrawables(null, drawable, null, null);
        acMonitoringPointTvPsd.setClickable(isClickable);
        acMonitoringPointTvPsd.setTextColor(getResources().getColor(isClickable ? R.color.c_252525 : R.color.c_a6a6a6));
    }


    @Override
    public void showOperationSuccessToast() {
        SensoroSuccessToast.INSTANCE.showToast(mActivity, Toast.LENGTH_SHORT);
    }

    @Override
    public void showErrorTipDialog(String errorMsg) {
        if (mTipUtils.isShowing()) {
            mTipUtils.setTipMessageText(errorMsg);
            return;
        }
        mTipUtils.setTipEtRootVisible(false);
        mTipUtils.setTipTitleText(mActivity.getString(R.string.request_failed));
        mTipUtils.setTipMessageText(errorMsg);
        mTipUtils.setTipCancelText(mActivity.getString(R.string.back), mActivity.getResources().getColor(R.color.c_252525));
        mTipUtils.setTipConfirmVisible(false);
        mTipUtils.show();
    }

    @Override
    public void showOperationTipLoadingDialog() {
        if (mOperatingUtil != null) {
            switch (mTipDialogType) {
                case MonitorPointOperationCode.ERASURE:
                    mOperatingUtil.setTipText(mActivity.getString(R.string.erasuring));
                    break;
                case MonitorPointOperationCode.RESET:
                    mOperatingUtil.setTipText(mActivity.getString(R.string.reseting));
                    break;
                case MonitorPointOperationCode.PSD:
                    mOperatingUtil.setTipText(mActivity.getString(R.string.psd_modifing));
                    break;
                case MonitorPointOperationCode.QUERY:
                    mOperatingUtil.setTipText(mActivity.getString(R.string.quering));
                    break;
                case MonitorPointOperationCode.SELF_CHECK:
                    mOperatingUtil.setTipText(mActivity.getString(R.string.self_checking));
                    break;
                case MonitorPointOperationCode.AIR_SWITCH_CONFIG:
                    mOperatingUtil.setTipText(mActivity.getString(R.string.configuring));
                    break;

            }
            mOperatingUtil.show();
        }
    }

    @Override
    public void dismissTipDialog() {
        if (mTipUtils != null) {
            mTipUtils.dismiss();
        }
    }

    @Override
    public void dismissOperatingLoadingDialog() {
        if (mOperatingUtil != null) {
            mOperatingUtil.dismiss();
        }
    }


    @OnClick({R.id.ac_monitoring_point_tv_erasure, R.id.ac_monitoring_point_tv_reset, R.id.ac_monitoring_point_tv_psd,
            R.id.ac_monitoring_point_tv_query, R.id.ac_monitoring_point_tv_self_check, R.id.ac_monitoring_point_tv_air_switch_config, R.id.include_text_title_tv_subtitle,
            R.id.ac_monitoring_point_cl_alert_contact, R.id.ac_monitoring_point_imv_location, R.id.ac_monitoring_point_cl_location_navigation,
            R.id.ac_monitoring_point_imv_detail, R.id.include_text_title_imv_arrows_left})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ac_monitoring_point_tv_erasure:
                showTipDialog(false, R.string.is_device_erasure, R.string.device_erasure_tip_message, R.string.erasure, R.color.c_f34a4a, MonitorPointOperationCode.ERASURE);
                break;
            case R.id.ac_monitoring_point_tv_reset:
                showTipDialog(false, R.string.is_device_reset, R.string.device_reset_tip_message, R.string.reset, R.color.c_f34a4a, MonitorPointOperationCode.RESET);
                break;
            case R.id.ac_monitoring_point_tv_psd:
                showTipDialog(false, R.string.is_device_psd, R.string.device_psd_tip_message, R.string.modify, R.color.c_f34a4a, MonitorPointOperationCode.PSD);
                break;
            case R.id.ac_monitoring_point_tv_query:
                showTipDialog(false, R.string.is_device_query, R.string.device_query_tip_message, R.string.monitor_point_detail_query, R.color.c_29c093, MonitorPointOperationCode.QUERY);
                break;
            case R.id.ac_monitoring_point_tv_self_check:
                showTipDialog(false, R.string.is_device_self_check, R.string.device_self_check_tip_message, R.string.self_check, R.color.c_29c093, MonitorPointOperationCode.SELF_CHECK);
                break;
            case R.id.ac_monitoring_point_tv_air_switch_config:
                showTipDialog(true, R.string.is_device_air_switch_config, R.string.device_air_switch_config_tip_message, R.string.air_switch_config, R.color.c_f34a4a, MonitorPointOperationCode.AIR_SWITCH_CONFIG);
                break;
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

    private void showTipDialog(boolean isEdit, @StringRes int title, @StringRes int message, @StringRes int confirm, @ColorRes int confirmColor, int type) {
        if (mTipUtils.isShowing()) {
            mTipUtils.dismiss();
        }
        mTipUtils.setTipEtRootVisible(isEdit);
        mTipUtils.setTipTitleText(mActivity.getString(title));
        mTipUtils.setTipMessageText(mActivity.getString(message));
        mTipUtils.setTipConfirmVisible(true);
        mTipUtils.setTipCancelText(mActivity.getString(R.string.back), mActivity.getResources().getColor(R.color.c_252525));
        mTipUtils.setTipConfirmText(mActivity.getString(confirm), mActivity.getResources().getColor(confirmColor));
        mTipDialogType = type;
        mTipUtils.show();
    }

    //tip dialog 点击事件
    @Override
    public void onCancelClick() {
        if (mTipUtils != null) {
            mTipUtils.dismiss();
        }
    }

    @Override
    public void onConfirmClick(String content,String diameter) {
        mPresenter.doOperation(mTipDialogType, content);
    }

    @Override
    public void onItemClick(View view, int position) {
        List<ScenesData> images = mAdapter.getImages();
        mPresenter.toPhotoDetail(position, images);
    }
}

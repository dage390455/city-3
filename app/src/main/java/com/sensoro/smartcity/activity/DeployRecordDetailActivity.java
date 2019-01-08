package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.ContactAdapter;
import com.sensoro.smartcity.adapter.TagAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IDeployRecordDetailActivityView;
import com.sensoro.smartcity.presenter.DeployRecordDetailActivityPresenter;
import com.sensoro.smartcity.server.bean.DeployRecordInfo;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SpacesItemDecoration;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeployRecordDetailActivity extends BaseActivity<IDeployRecordDetailActivityView, DeployRecordDetailActivityPresenter> implements IDeployRecordDetailActivityView {
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_cl_root)
    ConstraintLayout includeTextTitleClRoot;
    @BindView(R.id.ac_deploy_record_detail_tv_name)
    TextView acDeployRecordDetailTvName;
    @BindView(R.id.ac_deploy_record_detail_rc_tag)
    RecyclerView acDeployRecordDetailRcTag;
    @BindView(R.id.ac_deploy_record_detail_tv_time)
    TextView acDeployRecordDetailTvTime;
    @BindView(R.id.ac_deploy_record_detail_rc_contact)
    RecyclerView acDeployRecordDetailRcContact;
    @BindView(R.id.ac_deploy_record_detail_tv_we_chat)
    TextView acDeployRecordDetailTvWeChat;
    @BindView(R.id.ac_deploy_record_detail_tv_pic_count)
    TextView acDeployRecordDetailTvPicCount;
    @BindView(R.id.ac_deploy_record_detail_tv_fixed_point_signal)
    TextView acDeployRecordDetailTvFixedPointSignal;
    @BindView(R.id.ac_deploy_record_detail_tv_fixed_point_state)
    TextView acDeployRecordDetailTvFixedPointState;
    @BindView(R.id.ac_deploy_record_detail_ll_fixed_point)
    LinearLayout acDeployRecordDetailLlFixedPoint;
    @BindView(R.id.ac_deploy_record_detail_ll_deploy_pic)
    LinearLayout acDeployRecordDetailLlDeployPic;
    @BindView(R.id.ac_deploy_device_record_detail_tv_device_type)
    TextView acDeployDeviceRecordDetailTvDeviceType;
    @BindView(R.id.ac_deploy_device_record_detail_tv_device_sn)
    TextView acDeployDeviceRecordDetailTvDeviceSn;
    @BindView(R.id.ac_deploy_device_detail_deploy_record_setting_line)
    View acDeployDeviceDetailDeployRecordSettingLine;
    @BindView(R.id.ac_deploy_device_detail_ll_deploy_record_setting)
    LinearLayout acDeployDeviceDetailLlDeployRecordSetting;
    @BindView(R.id.ac_deploy_device_detail_tv_deploy_record_setting)
    TextView acDeployDeviceDetailTvDeployRecordSetting;
    @BindView(R.id.ll_deploy_record_detail_we_chat)
    LinearLayout llDeployRecordDetailWeChat;
    @BindView(R.id.line_we_chat)
    View lineWeChat;
    private TagAdapter mTagAdapter;
    private ContactAdapter mContactAdapter;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.actvity_deploy_record_detail);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
//        includeTextTitleTvSubtitle.setText("历史记录");
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        includeTextTitleTvTitle.setVisibility(View.VISIBLE);
        includeTextTitleTvTitle.setText(R.string.deploy_detail);
        initRcTag();
        if (!AppUtils.isChineseLanguage()) {
            lineWeChat.setVisibility(View.GONE);
            llDeployRecordDetailWeChat.setVisibility(View.GONE);
        }
        initRcContact();
    }

    private void initRcContact() {
        mContactAdapter = new ContactAdapter(mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        acDeployRecordDetailRcContact.setLayoutManager(manager);
        acDeployRecordDetailRcContact.setAdapter(mContactAdapter);
    }

    private void initRcTag() {
        mTagAdapter = new TagAdapter(mActivity, R.color.c_252525, R.color.c_dfdfdf);
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        acDeployRecordDetailRcTag.setLayoutManager(layoutManager);
        int spacingInPixels = mActivity.getResources().getDimensionPixelSize(R.dimen.x10);
        acDeployRecordDetailRcTag.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
        acDeployRecordDetailRcTag.setAdapter(mTagAdapter);

    }

    @Override
    protected DeployRecordDetailActivityPresenter createPresenter() {
        return new DeployRecordDetailActivityPresenter();
    }

    @Override
    public void startAC(Intent intent) {
        mActivity.startActivity(intent);
    }

    @Override
    public void finishAc() {
        finish();
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
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_LONG).show();
    }


    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.ac_deploy_record_detail_ll_fixed_point, R.id.ac_deploy_record_detail_ll_deploy_pic
            , R.id.include_text_title_tv_subtitle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
                break;
            case R.id.ac_deploy_record_detail_ll_fixed_point:
                mPresenter.doFixedPoint();
                break;
            case R.id.ac_deploy_record_detail_ll_deploy_pic:
                mPresenter.doDeployPic();
                break;
            case R.id.include_text_title_tv_subtitle:
                finishAc();
                break;
        }
    }

    @Override
    public void setSNTitle(String sn) {
        acDeployDeviceRecordDetailTvDeviceSn.setText(sn);
    }

    @Override
    public void setDeviceName(String deviceName) {
        acDeployRecordDetailTvName.setText(deviceName);
    }

    @Override
    public void updateTagList(List<String> tags) {
        mTagAdapter.updateTags(tags);
    }

    @Override
    public void setDeployTime(String time) {
        acDeployRecordDetailTvTime.setText(time);
    }

    @Override
    public void seDeployWeChat(String text) {
        acDeployRecordDetailTvWeChat.setText(text);
    }

    @Override
    public void setPicCount(String content) {
        acDeployRecordDetailTvPicCount.setText(content);
    }

    @Override
    public void updateContactList(List<DeployRecordInfo.NotificationBean> notifications) {
        mContactAdapter.updateContact(notifications);
    }

    @Override
    public void setPositionStatus(int status) {
        switch (status) {
            case 0:
                acDeployRecordDetailTvFixedPointState.setText(mActivity.getString(R.string.not_positioned));
                acDeployRecordDetailTvFixedPointState.setTextColor(mActivity.getResources().getColor(R.color.c_a6a6a6));
                break;
            case 1:
                acDeployRecordDetailTvFixedPointState.setText(mActivity.getString(R.string.positioned));
                acDeployRecordDetailTvFixedPointState.setTextColor(mActivity.getResources().getColor(R.color.c_252525));
                break;

        }
    }

    @Override
    public void refreshSingle(String signalQuality) {
        String signal_text = null;
        if (signalQuality != null) {
            switch (signalQuality) {
                case "good":
                    signal_text = mActivity.getString(R.string.signal_excellent);
                    acDeployRecordDetailTvFixedPointSignal.setBackground(getResources().getDrawable(R.drawable.shape_signal_good));
                    break;
                case "normal":
                    signal_text = mActivity.getString(R.string.signal_good);
                    acDeployRecordDetailTvFixedPointSignal.setBackground(getResources().getDrawable(R.drawable.shape_signal_normal));
                    break;
                case "bad":
                    signal_text = mActivity.getString(R.string.signal_weak);
                    acDeployRecordDetailTvFixedPointSignal.setBackground(getResources().getDrawable(R.drawable.shape_signal_bad));
                    break;
                default:
                    signal_text = mActivity.getString(R.string.no_signal);
                    acDeployRecordDetailTvFixedPointSignal.setBackground(getResources().getDrawable(R.drawable.shape_signal_none));
                    break;
            }
        } else {
            signal_text = mActivity.getString(R.string.no_signal);
            acDeployRecordDetailTvFixedPointSignal.setBackground(getResources().getDrawable(R.drawable.shape_signal_none));
        }
        acDeployRecordDetailTvFixedPointSignal.setText(signal_text);
    }

    @Override
    public void setDeployDeviceRecordDeviceType(String text) {
        acDeployDeviceRecordDetailTvDeviceType.setText(text);
    }

    @Override
    public void setDeployDetailDeploySettingVisible(boolean isVisible) {
        acDeployDeviceDetailDeployRecordSettingLine.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        acDeployDeviceDetailLlDeployRecordSetting.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setDeployDeviceDetailDeploySetting(String setting) {
        if (TextUtils.isEmpty(setting)) {
            setDeployDetailDeploySettingVisible(false);
            acDeployDeviceDetailTvDeployRecordSetting.setTextColor(mActivity.getResources().getColor(R.color.c_a6a6a6));
            acDeployDeviceDetailTvDeployRecordSetting.setText(mActivity.getString(R.string.not_setting));
        } else {
            setDeployDetailDeploySettingVisible(true);
            acDeployDeviceDetailTvDeployRecordSetting.setTextColor(mActivity.getResources().getColor(R.color.c_252525));
            acDeployDeviceDetailTvDeployRecordSetting.setText(setting);
        }

    }
}

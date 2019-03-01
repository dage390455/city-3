package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IDeployMonitorUploadCheckFragmentView;
import com.sensoro.smartcity.presenter.DeployMonitorUploadCheckFragmentPresenter;
import com.sensoro.smartcity.widget.TouchRecycleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DeployMonitorUploadCheckFragment extends BaseFragment<IDeployMonitorUploadCheckFragmentView, DeployMonitorUploadCheckFragmentPresenter> implements IDeployMonitorUploadCheckFragmentView {
    @BindView(R.id.tv_fg_deploy_upload_check_device_sn)
    TextView tvFgDeployUploadCheckDeviceSn;
    @BindView(R.id.tv_fg_deploy_upload_check_device_type)
    TextView tvFgDeployUploadCheckDeviceType;
    @BindView(R.id.tv_fg_deploy_upload_check_name_location)
    TextView tvFgDeployUploadCheckNameLocation;
    @BindView(R.id.ll_fg_deploy_upload_check_name_location)
    LinearLayout llFgDeployUploadCheckNameLocation;
    @BindView(R.id.view_fg_deploy_upload_check_name_address)
    View viewFgDeployUploadCheckNameAddress;
    @BindView(R.id.tv_fg_deploy_upload_check_tag)
    TextView tvFgDeployUploadCheckTag;
    @BindView(R.id.imv_fg_deploy_upload_check_tag)
    ImageView imvFgDeployUploadCheckTag;
    @BindView(R.id.tv_fg_deploy_upload_check_tag_required)
    TextView tvFgDeployUploadCheckTagRequired;
    @BindView(R.id.rc_fg_deploy_upload_check_tag)
    TouchRecycleView rcFgDeployUploadCheckTag;
    @BindView(R.id.rl_fg_deploy_upload_check_tag)
    RelativeLayout rlFgDeployUploadCheckTag;
    @BindView(R.id.view_fg_deploy_upload_check_tag)
    View viewFgDeployUploadCheckTag;
    @BindView(R.id.tv_fg_deploy_upload_check_alarm_contact_required)
    TextView tvFgDeployUploadCheckAlarmContactRequired;
    @BindView(R.id.rc_fg_deploy_upload_check_alarm_contact)
    TouchRecycleView rcFgDeployUploadCheckAlarmContact;
    @BindView(R.id.view_fg_deploy_upload_check_alarm_contact)
    View viewFgDeployUploadCheckAlarmContact;
    @BindView(R.id.tv_fg_deploy_upload_check_pic_size)
    TextView tvFgDeployUploadCheckPicSize;
    @BindView(R.id.iv_fg_deploy_upload_check_deploy_pic_arrow)
    ImageView ivFgDeployUploadCheckDeployPicArrow;
    @BindView(R.id.ll_fg_deploy_upload_check_deploy_pic)
    LinearLayout llFgDeployUploadCheckDeployPic;
    @BindView(R.id.ll_fg_deploy_upload_check_alarm_contact)
    LinearLayout llFgDeployUploadCheckAlarmContact;
    @BindView(R.id.tv_fg_deploy_upload_check_upload_tip)
    TextView tvFgDeployUploadCheckTvUploadTip;
    @BindView(R.id.tv_fg_deploy_upload_check_upload)
    TextView tvFgDeployUploadCheckUpload;
    @BindView(R.id.tv_fg_deploy_upload_check_mini_program)
    TextView tvFgDeployUploadCheckMiniProgram;
    @BindView(R.id.ll_fg_deploy_upload_check_mini_program)
    LinearLayout llFgDeployUploadCheckMiniProgram;

    @Override
    protected void initData(Context activity) {
        mPresenter.initData(activity);
    }

    @Override
    protected int initRootViewId() {
        return R.layout.fragment_deploy_monitor_upload_check;
    }

    @Override
    protected DeployMonitorUploadCheckFragmentPresenter createPresenter() {
        return new DeployMonitorUploadCheckFragmentPresenter();
    }

    @Override
    public void onFragmentStart() {

    }

    @Override
    public void onFragmentStop() {

    }


    @OnClick({R.id.ll_fg_deploy_upload_check_name_location, R.id.rl_fg_deploy_upload_check_tag,
            R.id.ll_fg_deploy_upload_check_deploy_pic, R.id.ll_fg_deploy_upload_check_alarm_contact
            , R.id.ll_fg_deploy_upload_check_mini_program})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_fg_deploy_upload_check_name_location:
                mPresenter.doNameAddress();
                break;
            case R.id.rl_fg_deploy_upload_check_tag:
                break;
            case R.id.ll_fg_deploy_upload_check_deploy_pic:
                break;
            case R.id.ll_fg_deploy_upload_check_alarm_contact:
                break;
            case R.id.ll_fg_deploy_upload_check_mini_program:
                break;
        }
    }


    @OnClick(R.id.ll_fg_deploy_upload_check_mini_program)
    public void onViewClicked() {
    }

    @Override
    public void startAC(Intent intent) {
        startAC(intent);
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
}

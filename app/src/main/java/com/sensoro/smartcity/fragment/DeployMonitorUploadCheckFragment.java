package com.sensoro.smartcity.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.DeployDeviceDetailAlarmContactAdapter;
import com.sensoro.smartcity.adapter.TagAdapter;
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IDeployMonitorUploadCheckFragmentView;
import com.sensoro.smartcity.model.DeployContactModel;
import com.sensoro.smartcity.presenter.DeployMonitorCheckActivityPresenter;
import com.sensoro.smartcity.presenter.DeployMonitorUploadCheckFragmentPresenter;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SpacesItemDecoration;
import com.sensoro.smartcity.widget.TouchRecycleView;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

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
    @BindView(R.id.iv_fg_deploy_upload_check_mini_program_arrow_icon)
    ImageView ivFgDeployUploadCheckMiniProgramArrow_icon;
    @BindView(R.id.tv_fg_deploy_upload_check_mini_program_description)
    TextView tvFgDeployUploadCheckMiniProgramDescription;
    @BindView(R.id.ll_fg_deploy_upload_check_mini_program)
    LinearLayout llFgDeployUploadCheckMiniProgram;
    private DeployDeviceDetailAlarmContactAdapter mAlarmContactAdapter;
    private TagAdapter mTagAdapter;
    private ProgressUtils mProgressUtils;
    private Activity mActivity;
    private ProgressDialog progressDialog;

    @Override
    protected void initData(Context activity) {
        mActivity = (Activity) activity;
        initView();
        mPresenter.initData(activity);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mRootFragment.getActivity()).build());
        initRcAlarmContact();
        initRcDeployDeviceTag();
        initUploadDialog();
    }

    private void initUploadDialog() {
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setProgressNumberFormat("");
        progressDialog.setCancelable(false);
    }

    private void initRcDeployDeviceTag() {
        rcFgDeployUploadCheckTag.setIntercept(true);
        mTagAdapter = new TagAdapter(mRootFragment.getActivity(), R.color.c_252525, R.color.c_dfdfdf);
        //
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mRootFragment.getActivity(), false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };

        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.x10);
        rcFgDeployUploadCheckTag.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels,false,false));
        rcFgDeployUploadCheckTag.setLayoutManager(layoutManager);
        rcFgDeployUploadCheckTag.setAdapter(mTagAdapter);
    }

    private void initRcAlarmContact() {
        rcFgDeployUploadCheckAlarmContact.setIntercept(true);
        mAlarmContactAdapter = new DeployDeviceDetailAlarmContactAdapter(mRootFragment.getActivity());
        LinearLayoutManager manager = new LinearLayoutManager(mRootFragment.getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rcFgDeployUploadCheckAlarmContact.setLayoutManager(manager);
        rcFgDeployUploadCheckAlarmContact.setAdapter(mAlarmContactAdapter);
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

        try {
            LogUtils.loge("---->>>" + DeployMonitorCheckActivityPresenter.deployAnalyzerModel.address);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void onFragmentStop() {

    }


    @OnClick({R.id.ll_fg_deploy_upload_check_name_location, R.id.rl_fg_deploy_upload_check_tag,
            R.id.ll_fg_deploy_upload_check_deploy_pic, R.id.ll_fg_deploy_upload_check_alarm_contact
            , R.id.ll_fg_deploy_upload_check_mini_program, R.id.tv_fg_deploy_upload_check_upload})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_fg_deploy_upload_check_name_location:
                mPresenter.doNameAddress();
                break;
            case R.id.rl_fg_deploy_upload_check_tag:
                mPresenter.doTag();
                break;
            case R.id.ll_fg_deploy_upload_check_deploy_pic:
                mPresenter.doSettingPhoto();
                break;
            case R.id.ll_fg_deploy_upload_check_alarm_contact:
                mPresenter.doAlarmContact();
                break;
            case R.id.ll_fg_deploy_upload_check_mini_program:
                mPresenter.doWeChatRelation();
                break;
            case R.id.tv_fg_deploy_upload_check_upload:
                mPresenter.doConfirm();
                break;
        }
    }


    @Override
    public void startAC(Intent intent) {
        if (mRootFragment.getActivity() != null) {
            mRootFragment.getActivity().startActivity(intent);
        }

    }

    @Override
    public void finishAc() {
        if (mRootFragment.getActivity() != null) {
            mRootFragment.getActivity().finish();
        }
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
    public void setAlarmContactAndPicAndMiniProgramVisible(boolean isVisible) {
        llFgDeployUploadCheckAlarmContact.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        viewFgDeployUploadCheckAlarmContact.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        llFgDeployUploadCheckDeployPic.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        llFgDeployUploadCheckMiniProgram.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        tvFgDeployUploadCheckMiniProgramDescription.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setDeployPhotoVisible(boolean isVisible) {
        llFgDeployUploadCheckDeployPic.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setNameAddressText(String nameAndAddress) {
        if (TextUtils.isEmpty(nameAndAddress)) {
            tvFgDeployUploadCheckNameLocation.setTextColor(getResources().getColor(R.color.c_a6a6a6));
            tvFgDeployUploadCheckNameLocation.setText(getString(R.string.required));
        } else {
            tvFgDeployUploadCheckNameLocation.setTextColor(getResources().getColor(R.color.c_252525));
            tvFgDeployUploadCheckNameLocation.setText(nameAndAddress);
        }
    }

    @Override
    public void setDeployDetailArrowWeChatVisible(boolean isVisible) {
        ivFgDeployUploadCheckMiniProgramArrow_icon.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setDeviceSn(String sn) {
        tvFgDeployUploadCheckDeviceSn.setText(sn);
    }

    @Override
    public void updateUploadTvText(String text) {
        tvFgDeployUploadCheckUpload.setText(text);
    }

    @Override
    public void setDeployDeviceType(String type) {
        tvFgDeployUploadCheckDeviceType.setText(type);
    }

    @Override
    public void updateContactData(List<DeployContactModel> list) {
        if (list.size() > 0) {
            tvFgDeployUploadCheckAlarmContactRequired.setVisibility(View.GONE);
            rcFgDeployUploadCheckAlarmContact.setVisibility(View.VISIBLE);
            mAlarmContactAdapter.updateDeployContactModels(list);
        } else {
            tvFgDeployUploadCheckAlarmContactRequired.setVisibility(View.VISIBLE);
            rcFgDeployUploadCheckAlarmContact.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateTagsData(List<String> tagList) {
        if (tagList.size() > 0) {
            tvFgDeployUploadCheckTagRequired.setVisibility(View.GONE);
            rcFgDeployUploadCheckTag.setVisibility(View.VISIBLE);
            mTagAdapter.updateTags(tagList);
        } else {
            tvFgDeployUploadCheckTagRequired.setVisibility(View.VISIBLE);
            rcFgDeployUploadCheckTag.setVisibility(View.GONE);
        }
    }

    @Override
    public void setUploadBtnStatus(boolean isEnable) {
        tvFgDeployUploadCheckUpload.setEnabled(isEnable);
        tvFgDeployUploadCheckUpload.setBackgroundResource(isEnable ? R.drawable.shape_bg_corner_29c_shadow : R.drawable.shape_bg_solid_df_corner);
    }

    @Override
    public void setDeployWeChatText(String weChatAccount) {
        if (TextUtils.isEmpty(weChatAccount)) {
            tvFgDeployUploadCheckMiniProgram.setTextColor(getResources().getColor(R.color.c_a6a6a6));
            tvFgDeployUploadCheckMiniProgram.setText(getString(R.string.optional));
        } else {
            tvFgDeployUploadCheckMiniProgram.setTextColor(getResources().getColor(R.color.c_252525));

            tvFgDeployUploadCheckMiniProgram.setText(weChatAccount);
        }
    }

    @Override
    public void setDeployPhotoText(String text) {
        if (TextUtils.isEmpty(text)) {
            tvFgDeployUploadCheckPicSize.setTextColor(getResources().getColor(R.color.c_a6a6a6));
            tvFgDeployUploadCheckPicSize.setText(getString(R.string.not_added));
        } else {
            tvFgDeployUploadCheckPicSize.setTextColor(getResources().getColor(R.color.c_252525));

            tvFgDeployUploadCheckPicSize.setText(text);
        }
    }

    @Override
    public void showStartUploadProgressDialog() {
        progressDialog.setTitle(mActivity.getString(R.string.please_wait));
        progressDialog.setProgress(0);
        progressDialog.show();
    }

    @Override
    public void dismissUploadProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void showUploadProgressDialog(String content, double percent) {
        if (progressDialog != null) {
//            String title = "正在上传第" + currentNum + "张，总共" + count + "张";
            progressDialog.setProgress((int) (percent * 100));
            progressDialog.setTitle(content);
            progressDialog.show();
        }
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_LONG).show();
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
    public void onDestroy() {
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
        super.onDestroy();
    }
}

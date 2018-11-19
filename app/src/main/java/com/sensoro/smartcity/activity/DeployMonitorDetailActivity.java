package com.sensoro.smartcity.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.DeployDeviceDetailAlarmContactAdapter;
import com.sensoro.smartcity.adapter.TagAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IDeployMonitorDetailActivityView;
import com.sensoro.smartcity.model.DeployContactModel;
import com.sensoro.smartcity.presenter.DeployMonitorDetailActivityPresenter;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SensoroToast;
import com.sensoro.smartcity.widget.SpacesItemDecoration;
import com.sensoro.smartcity.widget.TouchRecycleView;
import com.sensoro.smartcity.widget.dialog.CustomCornerDialog;
import com.sensoro.smartcity.widget.dialog.TipBleDialogUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeployMonitorDetailActivity extends BaseActivity<IDeployMonitorDetailActivityView, DeployMonitorDetailActivityPresenter>
        implements IDeployMonitorDetailActivityView, View.OnClickListener {


    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_cl_root)
    ConstraintLayout includeTextTitleClRoot;
    @BindView(R.id.ac_deploy_device_detail_tv_name_location)
    TextView acDeployDeviceDetailTvNameLocation;
    @BindView(R.id.ac_deploy_device_detail_ll_name_location)
    LinearLayout acDeployDeviceDetailLlNameLocation;
    @BindView(R.id.ac_deploy_device_detail_rc_tag)
    TouchRecycleView acDeployDeviceDetailRcTag;
    @BindView(R.id.ac_deploy_device_detail_rl_tag)
    RelativeLayout acDeployDeviceDetailRlTag;
    @BindView(R.id.ac_deploy_device_detail_rc_alarm_contact)
    TouchRecycleView acDeployDeviceDetailRcAlarmContact;
    @BindView(R.id.ac_deploy_device_detail_ll_alarm_contact)
    LinearLayout acDeployDeviceDetailLlAlarmContact;
    @BindView(R.id.ac_deploy_device_detail_tv_deploy_pic)
    TextView acDeployDeviceDetailTvDeployPic;
    @BindView(R.id.ac_deploy_device_detail_ll_deploy_pic)
    LinearLayout acDeployDeviceDetailLlDeployPic;
    @BindView(R.id.ac_deploy_device_detail_tv_fixed_point_signal)
    TextView acDeployDeviceDetailTvFixedPointSignal;
    @BindView(R.id.ac_deploy_device_detail_tv_fixed_point_state)
    TextView acDeployDeviceDetailTvFixedPointState;
    @BindView(R.id.ac_deploy_device_detail_ll_fixed_point)
    LinearLayout acDeployDeviceDetailLlFixedPoint;
    @BindView(R.id.ac_deploy_device_detail_tv_upload)
    TextView acDeployDeviceDetailTvUpload;
    @BindView(R.id.ac_deploy_device_detail_fixed_point_tv_near)
    TextView acDeployDeviceDetailFixedPointTvNear;
    @BindView(R.id.fl_not_own)
    FrameLayout flNotOwn;
    private DeployDeviceDetailAlarmContactAdapter mAlarmContactAdapter;
    private TagAdapter mTagAdapter;
    private TextView mDialogTvConfirm;
    private TextView mDialogTvCancel;
    private TextView mDialogTvTitle;
    private TextView mDialogTvMsg;
    private CustomCornerDialog mUploadDialog;
    private TipBleDialogUtils tipBleDialogUtils;
    private ProgressUtils mProgressUtils;
    private ProgressDialog progressDialog;
    private ProgressUtils mLoadBleConfigDialog;
    private ProgressUtils.Builder mLoadBleConfigDialogBuilder;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.actvity_deploy_device_detail_h);
        // todo 这个界面的标签要能滑动啊
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        includeTextTitleImvArrowsLeft = (ImageView) findViewById(R.id.include_text_title_imv_arrows_left);
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        tipBleDialogUtils = new TipBleDialogUtils(mActivity);
        mLoadBleConfigDialogBuilder = new ProgressUtils.Builder(mActivity);
        mLoadBleConfigDialog = new ProgressUtils(mLoadBleConfigDialogBuilder.setMessage(mActivity.getString(R.string.get_the_middle_profile)).build());

        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        updateUploadState(true);
        initUploadDialog();
        initRcAlarmContact();
        initRcDeployDeviceTag();
    }

    private void initRcDeployDeviceTag() {
        acDeployDeviceDetailRcTag.setIntercept(true);
        mTagAdapter = new TagAdapter(mActivity, R.color.c_252525, R.color.c_dfdfdf);
        //
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mActivity, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };

        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        int spacingInPixels = mActivity.getResources().getDimensionPixelSize(R.dimen.x10);
        acDeployDeviceDetailRcTag.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
        acDeployDeviceDetailRcTag.setLayoutManager(layoutManager);
        acDeployDeviceDetailRcTag.setAdapter(mTagAdapter);
    }

    private void initRcAlarmContact() {
        acDeployDeviceDetailRcAlarmContact.setIntercept(true);
        mAlarmContactAdapter = new DeployDeviceDetailAlarmContactAdapter(mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        acDeployDeviceDetailRcAlarmContact.setLayoutManager(manager);
        acDeployDeviceDetailRcAlarmContact.setAdapter(mAlarmContactAdapter);
    }

    @Override
    protected DeployMonitorDetailActivityPresenter createPresenter() {
        return new DeployMonitorDetailActivityPresenter();
    }


    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.include_text_title_tv_title, R.id.include_text_title_tv_subtitle,
            R.id.ac_deploy_device_detail_ll_name_location, R.id.ac_deploy_device_detail_rl_tag, R.id.ac_deploy_device_detail_ll_alarm_contact,
            R.id.ac_deploy_device_detail_ll_deploy_pic, R.id.ac_deploy_device_detail_ll_fixed_point, R.id.ac_deploy_device_detail_tv_upload})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
                break;
            case R.id.include_text_title_tv_title:
                break;
            case R.id.include_text_title_tv_subtitle:
                break;
            case R.id.ac_deploy_device_detail_ll_name_location:
                mPresenter.doNameAddress();
                break;
            case R.id.ac_deploy_device_detail_rl_tag:
                mPresenter.doTag();
                break;
            case R.id.ac_deploy_device_detail_ll_alarm_contact:
                mPresenter.doAlarmContact();
                break;
            case R.id.ac_deploy_device_detail_ll_deploy_pic:
                mPresenter.doSettingPhoto();
                break;
            case R.id.ac_deploy_device_detail_ll_fixed_point:
                mPresenter.doDeployMap();
                break;
            case R.id.ac_deploy_device_detail_tv_upload:
                //TODO 上传逻辑
                mPresenter.doConfirm();
                break;
        }
    }


    private void initConfirmDialog() {
        View view = View.inflate(mActivity, R.layout.dialog_frag_deploy_device_upload, null);
        mDialogTvCancel = view.findViewById(R.id.dialog_deploy_device_upload_tv_cancel);
        mDialogTvConfirm = view.findViewById(R.id.dialog_deploy_device_upload_tv_confirm);
        mDialogTvTitle = view.findViewById(R.id.dialog_deploy_device_upload_tv_title);
        mDialogTvMsg = view.findViewById(R.id.dialog_deploy_device_upload_tv_msg);

        mDialogTvConfirm.setOnClickListener(this);
        mDialogTvCancel.setOnClickListener(this);
//        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
//        builder.setView(view);
//        builder.setCancelable(false);
//        mUploadDialog = builder.create();
        mUploadDialog = new CustomCornerDialog(mActivity, R.style.CustomCornerDialogStyle, view);
    }

    @Override
    protected void onDestroy() {
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
        if (mUploadDialog != null) {
            mUploadDialog.cancel();
        }
        super.onDestroy();
    }

    private void initUploadDialog() {
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setProgressNumberFormat("");
        progressDialog.setCancelable(false);
    }

    @Override
    public void startAC(Intent intent) {
        startActivity(intent);
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
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }

    @Override
    public void updateUploadState(boolean isAvailable) {
        acDeployDeviceDetailTvUpload.setEnabled(isAvailable);
        acDeployDeviceDetailTvUpload.setBackgroundResource(isAvailable ? R.drawable.shape_bg_corner_29c_shadow :
                R.drawable.shape_bg_corner_dfdf_shadow);
    }

    @Override
    public void setDeviceTitleName(String name) {
        includeTextTitleTvTitle.setText(name);
    }

    @Override
    public void setNameAddressText(String text) {
        acDeployDeviceDetailTvNameLocation.setText(text);
    }

    @Override
    public void updateContactData(List<DeployContactModel> contacts) {
        mAlarmContactAdapter.updateDeployContactModels(contacts);
    }

    @Override
    public void updateTagsData(List<String> tagList) {
        mTagAdapter.updateTags(tagList);
    }

    @Override
    public void refreshSignal(boolean hasStation, String signal, int resSignalId, String locationInfo) {
        if (hasStation) {
            //TODO 背景选择器
            acDeployDeviceDetailTvFixedPointSignal.setVisibility(View.GONE);
            acDeployDeviceDetailTvFixedPointState.setText(locationInfo);
//        signalButton.setPadding(6, 10, 6, 10);
        } else {
            acDeployDeviceDetailTvFixedPointSignal.setVisibility(View.VISIBLE);
            //TODO 背景选择器
            acDeployDeviceDetailTvFixedPointSignal.setText(signal);
            acDeployDeviceDetailTvFixedPointSignal.setBackground(getResources().getDrawable(resSignalId));
            acDeployDeviceDetailTvFixedPointState.setText(locationInfo);
//        signalButton.setPadding(6, 10, 6, 10);
        }

    }

    @Override
    public void setDeployDeviceRlSignalVisible(boolean isVisible) {
        acDeployDeviceDetailTvFixedPointSignal.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setDeployContactRelativeLayoutVisible(boolean isVisible) {
        acDeployDeviceDetailLlAlarmContact.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setDeployDeviceDetailFixedPointNearVisible(boolean isVisible) {
        acDeployDeviceDetailFixedPointTvNear.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setDeployPhotoVisible(boolean isVisible) {
        acDeployDeviceDetailLlDeployPic.setVisibility(isVisible ? View.VISIBLE : View.GONE);
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
    public void dismissUploadProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void showStartUploadProgressDialog() {
        progressDialog.setTitle("请稍后");
        progressDialog.setProgress(0);
        progressDialog.show();
    }

    @Override
    public void setDeployPhotoText(String text) {
        acDeployDeviceDetailTvDeployPic.setText(text);
    }

    @Override
    public void showWarnDialog() {
        if (mUploadDialog == null) {
            initConfirmDialog();
            mUploadDialog.show();
        } else {
            mUploadDialog.show();
        }
    }

    @Override
    public void updateUploadTvText(String text) {
        acDeployDeviceDetailTvUpload.setText(text);
    }

    @Override
    public void showBleConfigDialog() {
        mLoadBleConfigDialog.showProgress();
    }

    @Override
    public void updateBleConfigDialogMessage(String msg) {
        mLoadBleConfigDialogBuilder.setMessage(msg);
    }

    @Override
    public void dismissBleConfigDialog() {
        mLoadBleConfigDialog.dismissProgress();
    }

    @Override
    public void showBleTips() {
        if (tipBleDialogUtils != null && !tipBleDialogUtils.isShowing()) {
            tipBleDialogUtils.show();
        }
    }

    @Override
    public void hideBleTips() {
        if (tipBleDialogUtils != null && tipBleDialogUtils.isShowing()) {
            tipBleDialogUtils.dismiss();
        }
    }

    @Override
    public void setNotOwnVisible(boolean isVisible) {
        flNotOwn.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (tipBleDialogUtils != null) {
            tipBleDialogUtils.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_deploy_device_upload_tv_confirm:
                mUploadDialog.dismiss();
                break;
            case R.id.dialog_deploy_device_upload_tv_cancel:
                mUploadDialog.dismiss();
                updateUploadState(false);
                mPresenter.requestUpload();
                break;
        }
    }
}

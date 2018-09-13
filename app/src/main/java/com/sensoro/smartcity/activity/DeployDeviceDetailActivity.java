package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.DeployDeviceDetailAlarmContactAdapter;
import com.sensoro.smartcity.adapter.DeployDeviceDetailTagAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IDeployDeviceDetailActivityView;
import com.sensoro.smartcity.presenter.DeployDeviceDetailActivityPresenter;
import com.sensoro.smartcity.widget.SensoroToast;
import com.sensoro.smartcity.widget.TouchRecyclerview;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeployDeviceDetailActivity extends BaseActivity<IDeployDeviceDetailActivityView, DeployDeviceDetailActivityPresenter>
implements IDeployDeviceDetailActivityView,View.OnClickListener{


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
    TouchRecyclerview acDeployDeviceDetailRcTag;
    @BindView(R.id.ac_deploy_device_detail_ll_tag)
    LinearLayout acDeployDeviceDetailLlTag;
    @BindView(R.id.ac_deploy_device_detail_rc_alarm_contact)
    TouchRecyclerview acDeployDeviceDetailRcAlarmContact;
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
    private DeployDeviceDetailAlarmContactAdapter mAlarmContactAdapter;
    private DeployDeviceDetailTagAdapter mTagAdapter;
    private TextView mDialogTvConfirm;
    private TextView mDialogTvCancel;
    private TextView mDialogTvTitle;
    private TextView mDialogTvMsg;
    private AlertDialog mUploadDialog;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.actvity_deploy_device_detail);
        // todo 这个界面的标签要能滑动啊
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        includeTextTitleTvTitle.setText("01A01117C6F");

        updateUploadState(true);

        initRcAlarmContact();

        initRcDeployDeviceTag();
    }

    private void initRcDeployDeviceTag() {
        acDeployDeviceDetailRcTag.setIntercept(false);
        mTagAdapter = new DeployDeviceDetailTagAdapter(mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        acDeployDeviceDetailRcTag.setLayoutManager(manager);
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
    protected DeployDeviceDetailActivityPresenter createPresenter() {
        return new DeployDeviceDetailActivityPresenter();
    }



    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.include_text_title_tv_title, R.id.include_text_title_tv_subtitle, R.id.ac_deploy_device_detail_ll_name_location, R.id.ac_deploy_device_detail_ll_tag, R.id.ac_deploy_device_detail_ll_alarm_contact, R.id.ac_deploy_device_detail_ll_deploy_pic, R.id.ac_deploy_device_detail_ll_fixed_point, R.id.ac_deploy_device_detail_tv_upload})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                break;
            case R.id.include_text_title_tv_title:
                break;
            case R.id.include_text_title_tv_subtitle:
                break;
            case R.id.ac_deploy_device_detail_ll_name_location:
                mPresenter.doNameAddress();
                break;
            case R.id.ac_deploy_device_detail_ll_tag:
                mPresenter.doTag();
                break;
            case R.id.ac_deploy_device_detail_ll_alarm_contact:
                mPresenter.doAlarmContact();
                break;
            case R.id.ac_deploy_device_detail_ll_deploy_pic:
                break;
            case R.id.ac_deploy_device_detail_ll_fixed_point:
                break;
            case R.id.ac_deploy_device_detail_tv_upload:
                toastShort("上传吗");
                if (mUploadDialog == null) {
                    initUpLoadDialog();
                    mUploadDialog.show();
                }else{
                    mUploadDialog.show();
                }

                break;
        }
    }

    private void initUpLoadDialog() {
        View view = View.inflate(mActivity, R.layout.dialog_frag_deploy_device_upload, null);
        mDialogTvCancel = view.findViewById(R.id.dialog_deploy_device_upload_tv_cancel);
        mDialogTvConfirm = view.findViewById(R.id.dialog_deploy_device_upload_tv_confirm);
        mDialogTvTitle = view.findViewById(R.id.dialog_deploy_device_upload_tv_title);
        mDialogTvMsg = view.findViewById(R.id.dialog_deploy_device_upload_tv_msg);

        mDialogTvConfirm.setOnClickListener(this);
        mDialogTvCancel.setOnClickListener(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setView(view);
        builder.setCancelable(false);
        mUploadDialog = builder.create();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUploadDialog!=null) {
            mUploadDialog.cancel();
        }
    }

    @Override
    public void startAC(Intent intent) {
        startActivity(intent);
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

    }

    @Override
    public void updateUploadState(boolean isAvailable) {
        acDeployDeviceDetailTvUpload.setEnabled(isAvailable);
        acDeployDeviceDetailTvUpload.setBackgroundResource(isAvailable?R.drawable.shape_bg_corner_29c_shadow:
                R.drawable.shape_bg_corner_dfdf_shadow);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_deploy_device_upload_tv_confirm:
                mUploadDialog.dismiss();
                break;
            case R.id.dialog_deploy_device_upload_tv_cancel:
                mUploadDialog.dismiss();
                break;
        }
    }
}

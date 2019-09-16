package com.sensoro.smartcity.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.sensoro.common.adapter.TagAdapter;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.manger.SensoroLinearLayoutManager;
import com.sensoro.common.model.EventData;
import com.sensoro.common.model.EventLoginData;
import com.sensoro.common.widgets.CustomCornerDialog;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.common.widgets.SpacesItemDecoration;
import com.sensoro.common.widgets.TouchRecycleView;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.imainviews.IDeployCameraDetailActivityView;
import com.sensoro.smartcity.presenter.DeployCameraDetailActivityPresenter;
import com.sensoro.smartcity.widget.dialog.DeployRetryDialogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeployCameraDetailActivity extends BaseActivity<IDeployCameraDetailActivityView, DeployCameraDetailActivityPresenter>
        implements IDeployCameraDetailActivityView, View.OnClickListener {


    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R.id.tv_ac_deploy_device_camera_upload)
    TextView tvAcDeployDeviceCameraUpload;
    @BindView(R.id.tv_ac_deploy_device_camera_upload_tip)
    TextView tvAcDeployDeviceCameraUploadTip;
    @BindView(R.id.tv_ac_deploy_device_camera_device_sn)
    TextView tvAcDeployDeviceCameraDeviceSn;
    @BindView(R.id.tv_ac_deploy_device_camera_device_type)
    TextView tvAcDeployDeviceCameraDeviceType;
    @BindView(R.id.tv_ac_deploy_device_camera_name_location)
    TextView tvAcDeployDeviceCameraNameLocation;
    @BindView(R.id.ll_ac_deploy_device_camera_name_location)
    LinearLayout llAcDeployDeviceCameraNameLocation;
    @BindView(R.id.tv_ac_deploy_device_camera_tag)
    TextView tvAcDeployDeviceCameraTag;
    @BindView(R.id.imv_ac_deploy_device_camera_tag)
    ImageView imvAcDeployDeviceCameraTag;
    @BindView(R.id.tv_ac_deploy_device_camera_tag_required)
    TextView tvAcDeployDeviceCameraTagRequired;
    @BindView(R.id.rc_ac_deploy_device_camera_tag)
    TouchRecycleView rcAcDeployDeviceCameraTag;
    @BindView(R.id.rl_ac_deploy_device_camera_tag)
    RelativeLayout rlAcDeployDeviceCameraTag;
    @BindView(R.id.tv_ac_deploy_device_camera_deploy_pic)
    TextView tvAcDeployDeviceCameraDeployPic;
    @BindView(R.id.ll_ac_deploy_device_camera_deploy_pic)
    LinearLayout llAcDeployDeviceCameraDeployPic;
    @BindView(R.id.tv_ac_deploy_device_camera_fixed_point_state)
    TextView tvAcDeployDeviceCameraFixedPointState;
    @BindView(R.id.ll_ac_deploy_device_camera_fixed_point)
    LinearLayout llAcDeployDeviceCameraFixedPoint;
    @BindView(R.id.line_ac_deploy_device_camera_deploy_setting)
    View lineAcDeployDeviceCameraDeploySetting;
    @BindView(R.id.tv_ac_deploy_device_camera_deploy_method)
    TextView tvAcDeployDeviceCameraDeployMethod;
    @BindView(R.id.ll_ac_deploy_device_camera_deploy_method)
    LinearLayout llAcDeployDeviceCameraDeployMethod;
    @BindView(R.id.tv_ac_deploy_device_camera_deploy_orientation)
    TextView tvAcDeployDeviceCameraDeployOrientation;
    @BindView(R.id.ll_ac_deploy_device_camera_deploy_orientation)
    LinearLayout llAcDeployDeviceCameraDeployOrientation;
    @BindView(R.id.last_view)
    View lastView;
    @BindView(R.id.tv_ac_deploy_device_camera_deploy_live)
    TextView tvAcDeployDeviceCameraDeployLive;
    @BindView(R.id.ll_ac_deploy_device_camera_deploy_live)
    LinearLayout llAcDeployDeviceCameraDeployLive;
    @BindView(R.id.fl_not_own)
    FrameLayout flNotOwn;

    private TagAdapter mTagAdapter;
    private TextView mDialogTvConfirm;
    private TextView mDialogTvCancel;
    private TextView mDialogTvTitle;
    private TextView mDialogTvMsg;
    private CustomCornerDialog mUploadDialog;
    private ProgressUtils mProgressUtils;
    private ProgressDialog progressDialog;
    private ProgressUtils mLoadBleConfigDialog;
    private ProgressUtils.Builder mLoadBleConfigDialogBuilder;
    private View line1;
    private DeployRetryDialogUtils retryDialog;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.actvity_deploy_camera_detail_h);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        includeTextTitleImvArrowsLeft = (ImageView) findViewById(R.id.include_text_title_imv_arrows_left);
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        mLoadBleConfigDialogBuilder = new ProgressUtils.Builder(mActivity);
        mLoadBleConfigDialog = new ProgressUtils(mLoadBleConfigDialogBuilder.setMessage(mActivity.getString(R.string.get_the_middle_profile)).build());
        includeTextTitleTvTitle.setText(R.string.deploy_device_detail_deploy_title);
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
//        updateUploadState(true);
        initUploadDialog();
        initRetryDialog();

        initRcDeployDeviceTag();
    }

    private void initRcDeployDeviceTag() {
        rcAcDeployDeviceCameraTag.setIntercept(true);
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
        rcAcDeployDeviceCameraTag.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
        rcAcDeployDeviceCameraTag.setLayoutManager(layoutManager);
        rcAcDeployDeviceCameraTag.setAdapter(mTagAdapter);
    }


    private void initRetryDialog() {
        retryDialog = new DeployRetryDialogUtils(mActivity);
        retryDialog.setonRetrylickListener(new DeployRetryDialogUtils.onRetrylickListener() {

            @Override
            public void onCancelClick() {
                retryDialog.dismiss();
                EventData eventData = new EventData();
                eventData.code = Constants.EVENT_DATA_DEPLOY_RESULT_FINISH;
                EventBus.getDefault().post(eventData);
                finishAc();
            }

            @Override
            public void onDismiss() {

            }

            @Override
            public void onConfirmClick() {
                mPresenter.doConfirm();
                retryDialog.dismiss();

            }
        });
    }
    @Override
    protected DeployCameraDetailActivityPresenter createPresenter() {
        return new DeployCameraDetailActivityPresenter();
    }


    private void initConfirmDialog() {
        View view = View.inflate(mActivity, R.layout.dialog_frag_deploy_device_upload, null);
        mDialogTvCancel = view.findViewById(R.id.dialog_deploy_device_upload_tv_cancel);
        mDialogTvConfirm = view.findViewById(R.id.dialog_deploy_device_upload_tv_confirm);
        mDialogTvTitle = view.findViewById(R.id.dialog_deploy_device_upload_tv_title);
        mDialogTvMsg = view.findViewById(R.id.dialog_deploy_device_upload_tv_msg);
        mDialogTvMsg.setText("摄像机离线");
        line1 = view.findViewById(R.id.line1);

        mDialogTvConfirm.setOnClickListener(this);
        mDialogTvCancel.setOnClickListener(this);
//        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
//        builder.setView(view);
//        builder.setCancelable(false);
//        mUploadDialog = builder.create();
        mUploadDialog = new CustomCornerDialog(mActivity, R.style.CustomCornerDialogStyle, view);
    }

    @Override
    protected void onStart() {
        mPresenter.onStart();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
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
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateUploadState(boolean isAvailable) {
        tvAcDeployDeviceCameraUpload.setEnabled(isAvailable);
        tvAcDeployDeviceCameraUpload.setBackgroundResource(isAvailable ? R.drawable.shape_bg_corner_29c_shadow :
                R.drawable.shape_bg_corner_dfdf_shadow);
    }

    @Override
    public void setDeviceSn(String sn) {
        tvAcDeployDeviceCameraDeviceSn.setText(sn);
    }

    @Override
    public void setNameAddressText(String text) {
        if (TextUtils.isEmpty(text)) {
            tvAcDeployDeviceCameraNameLocation.setTextColor(mActivity.getResources().getColor(R.color.c_a6a6a6));
            tvAcDeployDeviceCameraNameLocation.setText(mActivity.getString(R.string.required));
        } else {
            tvAcDeployDeviceCameraNameLocation.setTextColor(mActivity.getResources().getColor(R.color.c_252525));
            tvAcDeployDeviceCameraNameLocation.setText(text);
        }
    }

    @Override
    public void updateTagsData(List<String> tagList) {
        if (tagList.size() > 0) {
            tvAcDeployDeviceCameraTagRequired.setVisibility(View.GONE);
            rcAcDeployDeviceCameraTag.setVisibility(View.VISIBLE);
            mTagAdapter.updateTags(tagList);
        } else {
            tvAcDeployDeviceCameraTagRequired.setVisibility(View.VISIBLE);
            rcAcDeployDeviceCameraTag.setVisibility(View.GONE);
        }
    }


    @Override
    public void setDeployPhotoVisible(boolean isVisible) {
        llAcDeployDeviceCameraDeployPic.setVisibility(isVisible ? View.VISIBLE : View.GONE);
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
        progressDialog.setTitle(mActivity.getString(R.string.please_wait));
        progressDialog.setProgress(0);
        progressDialog.show();
    }

    @Override
    public void setDeployPhotoText(String text) {
        if (TextUtils.isEmpty(text)) {
            tvAcDeployDeviceCameraDeployPic.setTextColor(mActivity.getResources().getColor(R.color.c_a6a6a6));
            tvAcDeployDeviceCameraDeployPic.setText(mActivity.getString(R.string.not_added));
        } else {
            //缺少必选照⽚改为红⾊
            if (text.equals(mActivity.getResources().getString(R.string.missing_required_photo))) {
                tvAcDeployDeviceCameraDeployPic.setTextColor(mActivity.getResources().getColor(R.color.c_f34a4a));
            } else {
                tvAcDeployDeviceCameraDeployPic.setTextColor(mActivity.getResources().getColor(R.color.c_252525));
            }
            tvAcDeployDeviceCameraDeployPic.setText(text);
        }
    }

    @Override
    public void showWarnDialog(boolean canForceUpload) {
        if (mUploadDialog == null) {
            initConfirmDialog();
            setWarDialogStyle(canForceUpload);
            mUploadDialog.show();
        } else {
            setWarDialogStyle(canForceUpload);
            mUploadDialog.show();
        }
    }

    private void setWarDialogStyle(boolean canForceUpload) {
        if (canForceUpload) {
            line1.setVisibility(View.VISIBLE);
            mDialogTvCancel.setVisibility(View.VISIBLE);
            mDialogTvTitle.setText(R.string.deploy_result_is_upload);
            mDialogTvConfirm.setBackgroundResource(R.drawable.selector_item_white_ee_corner_right);
        } else {
            line1.setVisibility(View.GONE);
            mDialogTvCancel.setVisibility(View.GONE);
            mDialogTvTitle.setText(R.string.no_signal_can_not_uploaded);
            mDialogTvConfirm.setBackgroundResource(R.drawable.selector_item_white_corner_bottom);
        }
    }

    @Override
    public void updateUploadTvText(String text) {
        tvAcDeployDeviceCameraUpload.setText(text);
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
    public void setDeployPosition(boolean hasPosition, String text) {
        if (hasPosition) {
            tvAcDeployDeviceCameraFixedPointState.setTextColor(mActivity.getResources().getColor(R.color.c_252525));
            tvAcDeployDeviceCameraFixedPointState.setText(TextUtils.isEmpty(text) ? mActivity.getText(R.string.positioned) : text);
        } else {
            tvAcDeployDeviceCameraFixedPointState.setTextColor(mActivity.getResources().getColor(R.color.c_a6a6a6));
            tvAcDeployDeviceCameraFixedPointState.setText(mActivity.getString(R.string.required));
        }
    }


    @Override
    public void setNotOwnVisible(boolean isVisible) {
        flNotOwn.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        lastView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setDeployDeviceType(String text) {
        tvAcDeployDeviceCameraDeviceType.setText(text);
    }


    @Override
    public void setUploadBtnStatus(boolean isEnable) {
        tvAcDeployDeviceCameraUpload.setEnabled(isEnable);
        tvAcDeployDeviceCameraUpload.setBackgroundResource(isEnable ? R.drawable.shape_bg_corner_29c_shadow : R.drawable.shape_bg_corner_dfdf_shadow);
    }

    @Override
    public void setDeployMethod(String method) {
        if (TextUtils.isEmpty(method)) {
            tvAcDeployDeviceCameraDeployMethod.setTextColor(mActivity.getResources().getColor(R.color.c_a6a6a6));
            tvAcDeployDeviceCameraDeployMethod.setText(mActivity.getString(R.string.required));
        } else {
            tvAcDeployDeviceCameraDeployMethod.setTextColor(mActivity.getResources().getColor(R.color.c_252525));
            tvAcDeployDeviceCameraDeployMethod.setText(method);
        }
    }

    @Override
    public void setDeployOrientation(String orientation) {
        if (TextUtils.isEmpty(orientation)) {
            tvAcDeployDeviceCameraDeployOrientation.setTextColor(mActivity.getResources().getColor(R.color.c_a6a6a6));
            tvAcDeployDeviceCameraDeployOrientation.setText(mActivity.getString(R.string.required));
        } else {
            tvAcDeployDeviceCameraDeployOrientation.setTextColor(mActivity.getResources().getColor(R.color.c_252525));
            tvAcDeployDeviceCameraDeployOrientation.setText(orientation);
        }
    }

    @Override
    public void setDeployCameraStatus(String status) {
        if (status != null) {
            switch (status) {
                case "1":
                    //在线
                    tvAcDeployDeviceCameraDeployLive.setTextColor(mActivity.getResources().getColor(R.color.c_1dbb99));
                    Drawable drawableOnline = mActivity.getResources().getDrawable(R.drawable.shape_small_oval_f1dbb99);
                    drawableOnline.setBounds(0, 0, drawableOnline.getMinimumWidth(), drawableOnline.getMinimumHeight());
                    tvAcDeployDeviceCameraDeployLive.setText(mActivity.getString(R.string.deploy_camera_status_online));
                    tvAcDeployDeviceCameraDeployLive.setCompoundDrawables(drawableOnline, null, null, null);
                    break;
                default:
                    //其他情况认为是离线
                    tvAcDeployDeviceCameraDeployLive.setTextColor(mActivity.getResources().getColor(R.color.c_a6a6a6));
                    Drawable drawableOffline = mActivity.getResources().getDrawable(R.drawable.shape_small_oval_fa6a6a6);
                    drawableOffline.setBounds(0, 0, drawableOffline.getMinimumWidth(), drawableOffline.getMinimumHeight());
                    tvAcDeployDeviceCameraDeployLive.setText(mActivity.getString(R.string.deploy_camera_status_offline));
                    tvAcDeployDeviceCameraDeployLive.setCompoundDrawables(drawableOffline, null, null, null);
                    break;
            }
        } else {
            //拿不到数据认为设备离线
            tvAcDeployDeviceCameraDeployLive.setTextColor(mActivity.getResources().getColor(R.color.c_a6a6a6));
            Drawable drawableOffline = mActivity.getResources().getDrawable(R.drawable.shape_small_oval_fa6a6a6);
            drawableOffline.setBounds(0, 0, drawableOffline.getMinimumWidth(), drawableOffline.getMinimumHeight());
            tvAcDeployDeviceCameraDeployLive.setText(mActivity.getString(R.string.deploy_camera_status_offline));
            tvAcDeployDeviceCameraDeployLive.setCompoundDrawables(drawableOffline, null, null, null);
        }


    }

    @Override
    public void showRetryDialog() {
        EventLoginData userData = PreferencesHelper.getInstance().getUserData();
        if (userData != null) {
            if (userData.hasDeployOfflineTask) {
                if (null != retryDialog) {
                    retryDialog.show("网络异常是否重试", "离线上传", "立即重试");
                }
            }

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
                mPresenter.requestUpload();
                break;
        }
    }

    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.tv_ac_deploy_device_camera_upload, R.id.ll_ac_deploy_device_camera_name_location, R.id.rl_ac_deploy_device_camera_tag, R.id.ll_ac_deploy_device_camera_deploy_pic, R.id.ll_ac_deploy_device_camera_fixed_point, R.id.ll_ac_deploy_device_camera_deploy_method, R.id.ll_ac_deploy_device_camera_deploy_orientation, R.id.ll_ac_deploy_device_camera_deploy_live})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
                break;
            case R.id.tv_ac_deploy_device_camera_upload:
                mPresenter.doConfirm();
                break;
            case R.id.ll_ac_deploy_device_camera_name_location:
                mPresenter.doNameAddress();
                break;
            case R.id.rl_ac_deploy_device_camera_tag:
                mPresenter.doTag();
                break;
            case R.id.ll_ac_deploy_device_camera_deploy_pic:
                mPresenter.doSettingPhoto();
                break;
            case R.id.ll_ac_deploy_device_camera_fixed_point:
                mPresenter.doDeployMap();
                break;
            case R.id.ll_ac_deploy_device_camera_deploy_method:
                mPresenter.doDeployMethod();
                break;
            case R.id.ll_ac_deploy_device_camera_deploy_orientation:
                mPresenter.doDeployOrientation();
                break;
            case R.id.ll_ac_deploy_device_camera_deploy_live:
                mPresenter.doDeployCameraLive();
                break;
        }
    }
}

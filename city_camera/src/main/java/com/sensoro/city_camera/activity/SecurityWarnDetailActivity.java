package com.sensoro.city_camera.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sensoro.city_camera.IMainViews.ISecurityWarnDetailView;
import com.sensoro.city_camera.R;
import com.sensoro.city_camera.R2;
import com.sensoro.city_camera.adapter.SecurityWarnTimeLineAdapter;
import com.sensoro.city_camera.constants.SecurityConstants;
import com.sensoro.city_camera.dialog.SecurityCameraDetailsDialog;
import com.sensoro.city_camera.dialog.SecurityControlPersonDetailsDialog;
import com.sensoro.city_camera.dialog.SecurityWarnConfirmDialog;
import com.sensoro.city_camera.presenter.SecurityWarnDetailPresenter;
import com.sensoro.city_camera.util.MapUtil;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.server.security.bean.SecurityAlarmDetailInfo;
import com.sensoro.common.server.security.bean.SecurityAlarmEventInfo;
import com.sensoro.common.server.security.bean.SecurityAlarmInfo;
import com.sensoro.common.server.security.bean.SecurityCameraInfo;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.common.utils.DpUtils;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author : bin.tian
 * date   : 2019-06-24
 */
public class SecurityWarnDetailActivity extends BaseActivity<ISecurityWarnDetailView, SecurityWarnDetailPresenter>
        implements ISecurityWarnDetailView, IActivityIntent {

    @BindView(R2.id.include_text_title_imv_arrows_left)
    ImageView mBackIv;
    @BindView(R2.id.include_text_title_tv_title)
    TextView mTitleTv;
    @BindView(R2.id.include_text_title_tv_subtitle)
    TextView mSubtitle;
    @BindView(R2.id.security_warn_type_tv)
    TextView mSecurityWarnTypeTv;
    @BindView(R2.id.security_warn_title_tv)
    TextView mSecurityWarnTitleTv;
    @BindView(R2.id.security_warn_time_tv)
    TextView mSecurityWarnTimeTv;
    @BindView(R2.id.security_warn_video_tv)
    TextView mSecurityWarnVideoTv;
    @BindView(R2.id.security_warn_camera_tv)
    TextView mSecurityWarnCameraNameTv;
    @BindView(R2.id.security_warn_log_rv)
    RecyclerView mSecurityLogRv;
    @BindView(R2.id.security_warn_contact_owner_tv)
    TextView mSecurityWarnContactOwnerTv;
    @BindView(R2.id.security_warn_quick_navigation_tv)
    TextView mSecurityWarnNavigationTv;
    @BindView(R2.id.security_warn_alert_confirm_tv)
    TextView mSecurityWarnConfirmTv;
    @BindView(R2.id.confirm_result_tv)
    TextView mConfirmResultTv;
    @BindView(R2.id.security_warn_deploy_rl)
    View mSecurityWarnDeployRl;
    @BindView(R2.id.security_record_icon)
    ImageView mSecurityRecordIv;
    @BindView(R2.id.iv_left_photo)
    ImageView mLeftImageView;
    @BindView(R2.id.iv_right_photo)
    ImageView mRightImageView;
    @BindView(R2.id.tv_right_matchrate)
    TextView mMatchRateTv;
    @BindView(R2.id.space_center)
    View mSpaceView;
    @BindView(R2.id.left_image_rl)
    View mLeftPhotoRl;
    @BindView(R2.id.right_image_rl)
    View mRightPhotoRl;
    @BindView(R2.id.security_warn_video_rl)
    View mSecurityVideoRl;

    private ProgressUtils mProgressUtils;
    private SecurityWarnTimeLineAdapter mTimeLineAdapter;
    private int mWidth, mHeight;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.security_warn_detail_layout);
        ButterKnife.bind(this);
        initView();

        mPresenter.initData(this);


        MapUtil.startLocation(this);
    }

    @Override
    protected SecurityWarnDetailPresenter createPresenter() {
        return new SecurityWarnDetailPresenter();
    }

    private void initView() {
        resetImageViewSize();
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        mSubtitle.setVisibility(View.GONE);
        mTitleTv.setText(R.string.security_warn_detail_activity_title);
        mTimeLineAdapter = new SecurityWarnTimeLineAdapter(this);
        mSecurityLogRv.setHasFixedSize(true);
        mSecurityLogRv.setNestedScrollingEnabled(false);
        mSecurityLogRv.setLayoutManager(new LinearLayoutManager(this));
        mSecurityLogRv.setAdapter(mTimeLineAdapter);
    }

    @OnClick({R2.id.include_text_title_imv_arrows_left, R2.id.security_warn_video_rl, R2.id.security_warn_camera_rl,
            R2.id.security_warn_deploy_rl, R2.id.security_warn_contact_owner_tv,
            R2.id.security_warn_quick_navigation_tv, R2.id.security_warn_alert_confirm_tv, R2.id.iv_right_photo})
    public void onViewClicked(View view) {
        int viewId = view.getId();
        if (viewId == R.id.include_text_title_imv_arrows_left) {
            mPresenter.doBack();
        } else if (viewId == R.id.security_warn_video_rl) {
            mPresenter.toSecurityWarnRecord();
        } else if (viewId == R.id.security_warn_camera_rl) {
            //摄像机详情
            mPresenter.showCameraDetail();
        } else if (viewId == R.id.security_warn_deploy_rl) {
            //布控详情
            mPresenter.showDeployDetail();
        } else if (viewId == R.id.security_warn_contact_owner_tv) {
            mPresenter.doContactOwner();
        } else if (viewId == R.id.security_warn_quick_navigation_tv) {
            mPresenter.doNavigation();
        } else if (viewId == R.id.security_warn_alert_confirm_tv) {
            mPresenter.doConfirm();
        } else if (viewId == R.id.iv_right_photo) {
            previewImages(0);
        }
    }


    @Override
    public void updateSecurityWarnDetail(SecurityAlarmInfo securityAlarmInfo) {
        if (securityAlarmInfo == null) {
            finish();
            return;
        }

        mSecurityWarnTitleTv.setText(getString(R.string.start_include_backspace_text, securityAlarmInfo.getTaskName()));
        switch (securityAlarmInfo.getAlarmType()) {
            case SecurityConstants.SECURITY_TYPE_FOCUS:
                mSecurityWarnTypeTv.setText(R.string.focus_type);
                mSecurityWarnTypeTv.setBackgroundResource(R.drawable.security_type_focus_bg);
                Glide.with(this)
                        .asBitmap()
                        .load(securityAlarmInfo.getImageUrl())
                        .apply(new RequestOptions().skipMemoryCache(false)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .override(mWidth, mHeight)
                                .centerCrop()
                                .dontAnimate())
                        .into(mLeftImageView);

                Glide.with(this)
                        .asBitmap()
                        .load(securityAlarmInfo.getFaceUrl())
                        .apply(new RequestOptions().skipMemoryCache(false)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .override(mWidth, mHeight)
                                .centerCrop()
                                .dontAnimate())
                        .into(mRightImageView);
                mSecurityWarnDeployRl.setVisibility(View.VISIBLE);
                mMatchRateTv.setText(String.format("%s%%", Double.valueOf(securityAlarmInfo.getScore()).intValue()));
                break;
            case SecurityConstants.SECURITY_TYPE_FOREIGN:
                mSecurityWarnTypeTv.setText(R.string.external_type);
                mSecurityWarnTypeTv.setBackgroundResource(R.drawable.security_type_foreign_bg);
                mLeftPhotoRl.setVisibility(View.GONE);
                mSpaceView.setVisibility(View.GONE);
                mMatchRateTv.setVisibility(View.GONE);
                Glide.with(this)
                        .asBitmap()
                        .load(securityAlarmInfo.getFaceUrl())
                        .apply(new RequestOptions().skipMemoryCache(false)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .override(mWidth, mHeight)
                                .centerCrop()
                                .dontAnimate())
                        .into(mRightImageView);
                mSecurityWarnDeployRl.setVisibility(View.GONE);
                break;
            case SecurityConstants.SECURITY_TYPE_INVADE:
                mSecurityWarnTypeTv.setText(R.string.invade_type);
                mSecurityWarnTypeTv.setBackgroundResource(R.drawable.security_type_invade_bg);
                mSpaceView.setVisibility(View.GONE);
                mLeftPhotoRl.setVisibility(View.GONE);
                mMatchRateTv.setVisibility(View.GONE);
                Glide.with(this)
                        .asBitmap()
                        .load(securityAlarmInfo.getFaceUrl())
                        .apply(new RequestOptions().skipMemoryCache(false)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .override(mWidth, mHeight)
                                .centerCrop()
                                .dontAnimate())
                        .into(mRightImageView);
                mSecurityWarnDeployRl.setVisibility(View.GONE);
                break;
            default:
        }

        mSecurityWarnTimeTv.setText(DateUtil.getStrTimeToday(this, securityAlarmInfo.getAlarmTime(), 0));
        if (securityAlarmInfo instanceof SecurityAlarmDetailInfo) {
            SecurityAlarmDetailInfo securityAlarmDetailInfo = (SecurityAlarmDetailInfo) securityAlarmInfo;
            SecurityCameraInfo camera = securityAlarmDetailInfo.getCamera();
            if (camera != null) {
                mSecurityWarnCameraNameTv.setText(camera.getName());
            }
        } else {
            mSecurityWarnCameraNameTv.setText(securityAlarmInfo.getDeviceName());
        }

        updateSecurityConfirmResult(securityAlarmInfo);

    }

    @Override
    public void updateSecurityConfirmResult(SecurityAlarmInfo securityAlarmInfo) {
        if (securityAlarmInfo.getIsHandle() != SecurityConstants.SECURITY_IS_NOT_HANDLE
                && securityAlarmInfo.getIsEffective() == SecurityConstants.SECURITY_VALID) {
            mConfirmResultTv.setText(R.string.word_valid);
            mConfirmResultTv.setBackgroundResource(R.drawable.shape_camera_warn_valid);
            mConfirmResultTv.setVisibility(View.VISIBLE);
            mSecurityWarnConfirmTv.setVisibility(View.GONE);
        } else if (securityAlarmInfo.getIsHandle() != SecurityConstants.SECURITY_IS_NOT_HANDLE
                && securityAlarmInfo.getIsEffective() == SecurityConstants.SECURITY_INVALID) {
            mConfirmResultTv.setText(R.string.word_unvalid);
            mConfirmResultTv.setVisibility(View.VISIBLE);
            mConfirmResultTv.setBackgroundResource(R.drawable.shape_camera_warn_unvalid);
            mSecurityWarnConfirmTv.setVisibility(View.GONE);
        } else {
            mSecurityWarnConfirmTv.setVisibility(View.VISIBLE);
            mConfirmResultTv.setVisibility(View.GONE);
        }

        if (securityAlarmInfo.getIsHandle() != SecurityConstants.SECURITY_IS_NOT_HANDLE) {
            if (securityAlarmInfo.getIsEffective() == SecurityConstants.SECURITY_INVALID) {
                if (mRightImageView != null) {
                    mRightImageView.setAlpha(0.5f);
                    mLeftImageView.setAlpha(0.5f);
                } else {
                    mLeftImageView.setAlpha(0.5f);
                }
            }
        }
    }

    @Override
    public void updateSecurityWarnTimeLine(List<SecurityAlarmEventInfo> list) {
        if (mTimeLineAdapter != null) {
            mTimeLineAdapter.setDataList(list);
        }
    }

    @Override
    public void updateVideoRecordEnable(boolean isVideoRecordEnable) {
        mSecurityRecordIv.setImageResource(isVideoRecordEnable ? R.drawable.icon_security_video : R.drawable.icon_security_video_disable);
        mSecurityWarnVideoTv.setText(isVideoRecordEnable ? R.string.security_warn_video_title : R.string.security_warn_video_title_transcoding);
        mSecurityWarnVideoTv.setEnabled(isVideoRecordEnable);
        mSecurityVideoRl.setClickable(isVideoRecordEnable);
    }

    @Override
    public void showConfirmDialog(SecurityAlarmDetailInfo securityAlarmDetailInfo) {
        if(securityAlarmDetailInfo != null){
            SecurityWarnConfirmDialog securityWarnConfirmDialog = new SecurityWarnConfirmDialog();
            securityWarnConfirmDialog.setSecurityConfirmCallback(mPresenter);
            Bundle bundle = new Bundle();
            bundle.putString(SecurityWarnConfirmDialog.EXTRA_KEY_SECURITY_ID, securityAlarmDetailInfo.getId());
            bundle.putString(SecurityWarnConfirmDialog.EXTRA_KEY_SECURITY_TITLE, securityAlarmDetailInfo.getTaskName());
            bundle.putString(SecurityWarnConfirmDialog.EXTRA_KEY_SECURITY_TIME, String.valueOf(securityAlarmDetailInfo.getAlarmTime()));
            bundle.putInt(SecurityWarnConfirmDialog.EXTRA_KEY_SECURITY_TYPE, securityAlarmDetailInfo.getAlarmType());
            securityWarnConfirmDialog.setArguments(bundle);
            securityWarnConfirmDialog.show(getSupportFragmentManager());
        }
    }

    @Override
    public void showCameraDetailsDialog(SecurityAlarmDetailInfo securityAlarmDetailInfo) {
        if (null == securityAlarmDetailInfo || null == securityAlarmDetailInfo.getCamera()) {
            toastShort(getString(R.string.security_camera_info_error));
            return;
        }

        SecurityCameraDetailsDialog securityCameraDetailsDialog = new SecurityCameraDetailsDialog();
        securityCameraDetailsDialog.setSecurityCameraDetailsCallback(mPresenter);
        Bundle bundle = new Bundle();
        bundle.putString(SecurityCameraDetailsDialog.EXTRA_KEY_SECURITY_ID, securityAlarmDetailInfo.getId());
        bundle.putSerializable(SecurityCameraDetailsDialog.EXTRA_KEY_CAMERA_INFO, securityAlarmDetailInfo.getCamera());
        securityCameraDetailsDialog.setArguments(bundle);
        securityCameraDetailsDialog.show(getSupportFragmentManager());
    }

    @Override
    public void showDeployDetail(SecurityAlarmDetailInfo securityAlarmDetailInfo) {
        if (null == securityAlarmDetailInfo || null == securityAlarmDetailInfo.getObjectMainJson()) {
            return;
        }
        SecurityControlPersonDetailsDialog controlPersonDetailsDialog = new SecurityControlPersonDetailsDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(SecurityControlPersonDetailsDialog.EXTRA_KEY_DEPLOY_INFO, securityAlarmDetailInfo.getObjectMainJson());
        bundle.putString(SecurityControlPersonDetailsDialog.EXTRA_KEY_DEPLOY_IMAGE, securityAlarmDetailInfo.getImageUrl());
        controlPersonDetailsDialog.setArguments(bundle);
        controlPersonDetailsDialog.show(getSupportFragmentManager());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MapUtil.stopLocation();
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
        mActivity.setResult(resultCode);
    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {
        mActivity.setResult(resultCode, data);
    }

    @Override
    public void showProgressDialog() {
        if (mProgressUtils != null) {
            mProgressUtils.showProgress();
        }
    }

    @Override
    public void dismissProgressDialog() {
        if (mProgressUtils != null) {
            mProgressUtils.dismissProgress();
        }
    }

    private void previewImages(int position) {
        mPresenter.doPreviewImages(position);
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_LONG).show();
    }

    private void resetImageViewSize() {
        WindowManager w = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);

        int imageViewWidth = (metrics.widthPixels - DpUtils.dp2px(this, 52)) / 2;

        int height = imageViewWidth * 380 / 323;
        ViewGroup.LayoutParams leftParams = mLeftImageView.getLayoutParams();
        leftParams.height = height;
        leftParams.width = imageViewWidth;
        mLeftImageView.setLayoutParams(leftParams);

        mWidth = imageViewWidth;
        mHeight = height;

        ViewGroup.LayoutParams rightParams = mRightImageView.getLayoutParams();
        rightParams.height = height;
        rightParams.width = imageViewWidth;
        mRightImageView.setLayoutParams(rightParams);

    }
}

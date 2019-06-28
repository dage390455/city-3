package com.sensoro.city_camera.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sensoro.city_camera.IMainViews.ISecurityWarnDetailView;
import com.sensoro.city_camera.R;
import com.sensoro.city_camera.R2;
import com.sensoro.city_camera.constants.SecurityConstants;
import com.sensoro.city_camera.dialog.SecurityControlPersonDetailsDialog;
import com.sensoro.city_camera.dialog.SecurityWarnConfirmDialog;
import com.sensoro.city_camera.presenter.SecurityWarnDetailPresenter;
import com.sensoro.city_camera.util.MapUtil;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.server.security.bean.SecurityAlarmDetailInfo;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.common.widgets.MaxHeightRecyclerView;
import com.sensoro.common.widgets.ProgressUtils;

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
    @BindView(R2.id.security_warn_deploy_tv)
    TextView mSecurityWarnDeployTv;
    @BindView(R2.id.security_warn_log_rv)
    MaxHeightRecyclerView mSecurityLogRv;
    @BindView(R2.id.security_warn_contact_owner_tv)
    TextView mSecurityWarnContactOwnerTv;
    @BindView(R2.id.security_warn_quick_navigation_tv)
    TextView mSecurityWarnNavigationTv;
    @BindView(R2.id.security_warn_alert_confirm_tv)
    TextView mSecurityWarnConfirmTv;
    @BindView(R2.id.confirm_result_tv)
    TextView mConfirmResultTv;

    private SecurityWarnConfirmDialog mSecurityWarnConfirmDialog;
    private ProgressUtils mProgressUtils;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.security_warn_detail_layout);
        ButterKnife.bind(this);

        mPresenter.initData(this);

        initView();

        MapUtil.startLocation(this);
    }

    @Override
    protected SecurityWarnDetailPresenter createPresenter() {
        return new SecurityWarnDetailPresenter();
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        mSubtitle.setVisibility(View.GONE);
        mTitleTv.setText(R.string.security_warn_detail_activity_title);
    }

    @OnClick({R2.id.include_text_title_imv_arrows_left, R2.id.security_warn_video_tv, R2.id.security_warn_camera_tv,
            R2.id.security_warn_deploy_tv, R2.id.security_warn_contact_owner_tv,
            R2.id.security_warn_quick_navigation_tv, R2.id.security_warn_alert_confirm_tv})
    public void onViewClicked(View view) {
        int viewId = view.getId();
        if (viewId == R.id.include_text_title_imv_arrows_left) {
            mPresenter.doBack();
        } else if (viewId == R.id.security_warn_video_tv) {
            mPresenter.toSecurityWarnRecord();
        } else if (viewId == R.id.security_warn_camera_tv) {

        } else if (viewId == R.id.security_warn_deploy_tv) {

        } else if (viewId == R.id.security_warn_contact_owner_tv) {
//            SecurityCameraDetailsDialog cameraDetailsDialog = new SecurityCameraDetailsDialog();
//            cameraDetailsDialog.show(getSupportFragmentManager());
            SecurityControlPersonDetailsDialog securityControlPersonDetailsDialog = new SecurityControlPersonDetailsDialog();
            securityControlPersonDetailsDialog.show(getSupportFragmentManager());
//            SecurityWarnConfirmDialog warnConfirmDialog = new SecurityWarnConfirmDialog();
//            warnConfirmDialog.show(getSupportFragmentManager());

        } else if (viewId == R.id.security_warn_quick_navigation_tv) {
            mPresenter.doNavigation();
        } else if (viewId == R.id.security_warn_alert_confirm_tv) {
            mPresenter.doConfirm();
        }
    }

    @Override
    public void updateSecurityWarnDetail(SecurityAlarmDetailInfo securityAlarmDetailInfo) {
        if (securityAlarmDetailInfo == null) {
            finish();
            return;
        }

        mSecurityWarnTitleTv.setText(securityAlarmDetailInfo.getTaskName());
        switch (securityAlarmDetailInfo.getAlarmType()) {
            case SecurityConstants.SECURITY_TYPE_FOCUS:
                mSecurityWarnTypeTv.setText(R.string.focus_type);
                mSecurityWarnTypeTv.setBackgroundResource(R.drawable.security_type_focus_bg);

                findViewById(R.id.multi_image_vs).setVisibility(View.VISIBLE);
                Glide.with(this).load(securityAlarmDetailInfo.getImageUrl()).into((ImageView) findViewById(R.id.iv_left_photo));
                Glide.with(this).load(securityAlarmDetailInfo.getFaceUrl()).into((ImageView) findViewById(R.id.iv_right_photo));
                break;
            case SecurityConstants.SECURITY_TYPE_FOREIGN:
                mSecurityWarnTypeTv.setText(R.string.external_type);
                mSecurityWarnTypeTv.setBackgroundResource(R.drawable.security_type_foreign_bg);

                findViewById(R.id.single_image_vs).setVisibility(View.VISIBLE);
                Glide.with(this).load(securityAlarmDetailInfo.getFaceUrl()).into((ImageView) findViewById(R.id.iv_single_photo));
                break;
            case SecurityConstants.SECURITY_TYPE_INVADE:
                mSecurityWarnTypeTv.setText(R.string.invade_type);
                mSecurityWarnTypeTv.setBackgroundResource(R.drawable.security_type_invade_bg);

                findViewById(R.id.single_image_vs).setVisibility(View.VISIBLE);
                Glide.with(this).load(securityAlarmDetailInfo.getFaceUrl()).into((ImageView) findViewById(R.id.iv_single_photo));
                break;
            default:
        }

        mSecurityWarnTimeTv.setText(DateUtil.getStrTimeToday(this, Long.parseLong(securityAlarmDetailInfo.getAlarmTime()), 0));
        mSecurityWarnCameraNameTv.setText(securityAlarmDetailInfo.getCamera().getName());

        if(securityAlarmDetailInfo.getIsEffective() == SecurityConstants.SECURITY_VALID){
            mConfirmResultTv.setText(R.string.word_valid);
            mConfirmResultTv.setBackgroundResource(R.drawable.shape_camera_warn_valid);
        } else if(securityAlarmDetailInfo.getIsEffective() == SecurityConstants.SECURITY_INVALID){
            mConfirmResultTv.setText(R.string.word_unvalid);
            mConfirmResultTv.setBackgroundResource(R.drawable.shape_camera_warn_unvalid);
        } else {
            mConfirmResultTv.setVisibility(View.GONE);
        }

    }

    @Override
    public void showConfirmDialog(SecurityAlarmDetailInfo securityAlarmDetailInfo) {
        if (mSecurityWarnConfirmDialog == null) {
            mSecurityWarnConfirmDialog = new SecurityWarnConfirmDialog();
            mSecurityWarnConfirmDialog.setSecurityConfirmCallback(mPresenter);
        }
        Bundle bundle = new Bundle();
        bundle.putString(SecurityWarnConfirmDialog.EXTRA_KEY_SECURITY_ID, securityAlarmDetailInfo.getId());
        bundle.putString(SecurityWarnConfirmDialog.EXTRA_KEY_SECURITY_TITLE, securityAlarmDetailInfo.getTaskName());
        bundle.putString(SecurityWarnConfirmDialog.EXTRA_KEY_SECURITY_TIME, securityAlarmDetailInfo.getAlarmTime());
        bundle.putInt(SecurityWarnConfirmDialog.EXTRA_KEY_SECURITY_TYPE, securityAlarmDetailInfo.getAlarmType());
        mSecurityWarnConfirmDialog.setArguments(bundle);
        mSecurityWarnConfirmDialog.show(getSupportFragmentManager());
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

    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {

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
}

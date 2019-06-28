package com.sensoro.city_camera.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import com.sensoro.common.server.security.bean.SecurityContactsInfo;
import com.sensoro.common.server.security.bean.SecurityDeployPersonInfo;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.common.widgets.ProgressUtils;

import java.util.ArrayList;
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
    @BindView(R2.id.security_warn_deploy_tv)
    TextView mSecurityWarnDeployTv;
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

    private SecurityWarnConfirmDialog mSecurityWarnConfirmDialog;
    private SecurityCameraDetailsDialog mSecurityCameraDetailsDialog;
    private SecurityControlPersonDetailsDialog mControlPersonDetailsDialog;
    private ProgressUtils mProgressUtils;
    private SecurityWarnTimeLineAdapter mTimeLineAdapter;
    private View mSingleView;
    private View mMultiView;


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

        mTimeLineAdapter = new SecurityWarnTimeLineAdapter(this);
        mSecurityLogRv.setHasFixedSize(true);
        mSecurityLogRv.setNestedScrollingEnabled(false);
        mSecurityLogRv.setLayoutManager(new LinearLayoutManager(this));
        mSecurityLogRv.setAdapter(mTimeLineAdapter);
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
            //摄像机详情
            mPresenter.showCameraDetail();

        } else if (viewId == R.id.security_warn_deploy_tv) {
            //布控详情
            mPresenter.showDeployDetail();

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

                if(mMultiView == null){
                    mMultiView = ((ViewStub)findViewById(R.id.multi_image_vs)).inflate();
                    mMultiView.setVisibility(View.VISIBLE);
                }
                View leftView = findViewById(R.id.iv_left_photo);
                Glide.with(this).load(securityAlarmDetailInfo.getImageUrl()).into((ImageView) leftView);
                View rightView = findViewById(R.id.iv_right_photo);
                Glide.with(this).load(securityAlarmDetailInfo.getFaceUrl()).into((ImageView) rightView);
                leftView.setOnClickListener(v -> previewImages(0));
                rightView.setOnClickListener(v -> previewImages(1));

                break;
            case SecurityConstants.SECURITY_TYPE_FOREIGN:
                mSecurityWarnTypeTv.setText(R.string.external_type);
                mSecurityWarnTypeTv.setBackgroundResource(R.drawable.security_type_foreign_bg);

                if(mSingleView == null){
                    mSingleView = ((ViewStub)findViewById(R.id.single_image_vs)).inflate();
                    mSingleView.setVisibility(View.VISIBLE);
                }
                View singleView = findViewById(R.id.iv_single_photo);
                singleView.setOnClickListener(v -> previewImages(0));
                Glide.with(this).load(securityAlarmDetailInfo.getFaceUrl()).into((ImageView) singleView);
                break;
            case SecurityConstants.SECURITY_TYPE_INVADE:
                mSecurityWarnTypeTv.setText(R.string.invade_type);
                mSecurityWarnTypeTv.setBackgroundResource(R.drawable.security_type_invade_bg);

                if(mSingleView == null){
                    mSingleView = ((ViewStub)findViewById(R.id.single_image_vs)).inflate();
                    mSingleView.setVisibility(View.VISIBLE);
                }
                View singlePhotoView = findViewById(R.id.iv_single_photo);
                singlePhotoView.setOnClickListener(v -> previewImages(0));
                Glide.with(this).load(securityAlarmDetailInfo.getFaceUrl()).into((ImageView) singlePhotoView);
                break;
            default:
        }

        mSecurityWarnTimeTv.setText(DateUtil.getStrTimeToday(this, Long.parseLong(securityAlarmDetailInfo.getAlarmTime()), 0));
        mSecurityWarnCameraNameTv.setText(securityAlarmDetailInfo.getCamera().getName());

        updateSecurityConfirmResult(securityAlarmDetailInfo);

    }

    @Override
    public void updateSecurityConfirmResult(SecurityAlarmDetailInfo securityAlarmDetailInfo){
        if(securityAlarmDetailInfo.getIsEffective() == SecurityConstants.SECURITY_VALID){
            mConfirmResultTv.setText(R.string.word_valid);
            mConfirmResultTv.setBackgroundResource(R.drawable.shape_camera_warn_valid);
//            mSecurityWarnConfirmTv.setVisibility(View.GONE);
        } else if(securityAlarmDetailInfo.getIsEffective() == SecurityConstants.SECURITY_INVALID){
            mConfirmResultTv.setText(R.string.word_unvalid);
            mConfirmResultTv.setBackgroundResource(R.drawable.shape_camera_warn_unvalid);
//            mSecurityWarnConfirmTv.setVisibility(View.GONE);
        } else {
            mSecurityWarnConfirmTv.setVisibility(View.VISIBLE);
            mConfirmResultTv.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateSecurityWarnTimeLine(List<SecurityAlarmEventInfo> list) {
        if(mTimeLineAdapter != null){
            mTimeLineAdapter.setDataList(list);
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
    public void showCameraDetailsDialog(SecurityAlarmDetailInfo securityAlarmDetailInfo) {
        if(null == securityAlarmDetailInfo.getCamera()){
            return;
        }
        if (mSecurityCameraDetailsDialog == null) {
            mSecurityCameraDetailsDialog = new SecurityCameraDetailsDialog();
            mSecurityCameraDetailsDialog.setSecurityCameraDetailsCallback(mPresenter);
        }
        Bundle bundle = new Bundle();
        bundle.putString(SecurityCameraDetailsDialog.EXTRA_KEY_SECURITY_ID, securityAlarmDetailInfo.getId());
        bundle.putString(SecurityCameraDetailsDialog.EXTRA_KEY_CAMERA_NAME, securityAlarmDetailInfo.getCamera().getName());
        bundle.putString(SecurityCameraDetailsDialog.EXTRA_KEY_CAMERA_TYPE, securityAlarmDetailInfo.getCamera().getType());
        bundle.putInt(SecurityCameraDetailsDialog.EXTRA_KEY_CAMERA_SATUS, Integer.parseInt(securityAlarmDetailInfo.getCamera().getDeviceStatus()));
        bundle.putString(SecurityCameraDetailsDialog.EXTRA_KEY_CAMERA_SN, securityAlarmDetailInfo.getCamera().getSn());
        bundle.putString(SecurityCameraDetailsDialog.EXTRA_KEY_CAMERA_BRAND, securityAlarmDetailInfo.getCamera().getBrand());
        bundle.putStringArrayList(SecurityCameraDetailsDialog.EXTRA_KEY_CAMERA_LABEL, (ArrayList<String>) securityAlarmDetailInfo.getCamera().getLabel());
        bundle.putString(SecurityCameraDetailsDialog.EXTRA_KEY_CAMERA_VERSION, securityAlarmDetailInfo.getCamera().getVersion());

        List<SecurityContactsInfo> contactList =
                (securityAlarmDetailInfo.getCamera()!= null && securityAlarmDetailInfo.getCamera().getContact()!= null
                && securityAlarmDetailInfo.getCamera().getContact().size()>0)
                ?securityAlarmDetailInfo.getCamera().getContact():null;
        bundle.putString(SecurityCameraDetailsDialog.EXTRA_KEY_CAMERA_CONTACT,
                null != contactList?(contactList.get(0).getMobilePhone()+" | "+contactList.get(0).getMobilePhone()):"");
        bundle.putInt(SecurityCameraDetailsDialog.EXTRA_KEY_CAMERA_CONTACT_COUNT,contactList!=null?contactList.size():0);
        bundle.putString(SecurityCameraDetailsDialog.EXTRA_KEY_CAMERA_ADDRESS,securityAlarmDetailInfo.getCamera().getLocation());

        mSecurityCameraDetailsDialog.setArguments(bundle);
        mSecurityCameraDetailsDialog.show(getSupportFragmentManager());
    }

    @Override
    public void showDeployDetail(SecurityAlarmDetailInfo securityAlarmDetailInfo) {
        if(null ==securityAlarmDetailInfo.getObjectMainJson()){
            return;
        }
        if (mControlPersonDetailsDialog == null) {
            mControlPersonDetailsDialog = new SecurityControlPersonDetailsDialog();
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(SecurityControlPersonDetailsDialog.EXTRA_KEY_DEPLOY_INFO,securityAlarmDetailInfo.getObjectMainJson());
        mControlPersonDetailsDialog.setArguments(bundle);
        mControlPersonDetailsDialog.show(getSupportFragmentManager());

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

    private void previewImages(int position){
        mPresenter.doPreviewImages(position);
    }
}

package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.common.base.BaseFragment;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.common.widgets.dialog.TipBleDialogUtils;
import com.sensoro.common.widgets.dialog.TipDialogUtils;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.CameraListActivity;
import com.sensoro.smartcity.activity.ListMultiVideoActivity;
import com.sensoro.smartcity.activity.NearByDeviceActivity;
import com.sensoro.smartcity.activity.OfflineDeployActivity;
import com.sensoro.smartcity.imainviews.IManagerFragmentView;
import com.sensoro.smartcity.presenter.ManagerFragmentPresenter;
import com.sensoro.smartcity.widget.dialog.VersionDialogUtils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class ManagerFragment extends BaseFragment<IManagerFragmentView, ManagerFragmentPresenter> implements
        IManagerFragmentView, TipDialogUtils.TipDialogUtilsClickListener, VersionDialogUtils.VersionDialogUtilsClickListener {
    @BindView(R.id.fg_main_manage_tv_merchant_name)
    TextView fgMainManageTvMerchantName;
    @BindView(R.id.fg_main_manage_imv_merchant_icon)
    ImageView fgMainManageImvMerchantIcon;
    @BindView(R.id.fg_main_manage_tv_merchant_title)
    TextView fgMainManageTvMerchantTitle;
    @BindView(R.id.fg_main_manage_ll_change_merchants)
    LinearLayout fgMainManageLlChangeMerchants;
    @BindView(R.id.iv_merchant_arrow)
    ImageView ivMerchantArrow;
    @BindView(R.id.fg_main_manage_ll_deploy_device)
    LinearLayout fgMainManageLlDeployDevice;
    @BindView(R.id.fg_main_manage_ll_contract_management)
    LinearLayout fgMainManageLlContractManagement;
    @BindView(R.id.fg_main_manage_ll_polling_mission)
    LinearLayout fgMainManageLlPollingMission;
    @BindView(R.id.fg_main_manage_ll_scan_login_out)
    LinearLayout fgMainManageLlScanLoginOut;
    @BindView(R.id.fg_main_manage_ll_scan_login)
    LinearLayout fgMainManageLlScanLogin;
    @BindView(R.id.fg_main_manage_ll_maintenance_mission)
    LinearLayout fgMainManageLlMaintenanceMission;
    @BindView(R.id.fg_main_manage_ll_about_us)
    LinearLayout fgMainManageLlAboutUs;
    @BindView(R.id.fg_main_manage_tv_is_upgrade)
    View fgMainManageTvIsUpgrade;
    @BindView(R.id.fg_main_manage_ll_version_info)
    LinearLayout fgMainManageLlVersionInfo;
    @BindView(R.id.fg_main_manage_ll_exit)
    LinearLayout fgMainManageLlExit;
    @BindView(R.id.fg_main_manage_ll_main_function)
    LinearLayout fgMainManageLlMainFunction;
    @BindView(R.id.fg_main_manage_ll_signal_check)
    LinearLayout fgMainManageLlSignalCheck;
    @BindView(R.id.fg_main_manage_ll_wire_material_diameter)
    LinearLayout fgMainManageLlWireMaterialDiameter;
    @BindView(R.id.line1)
    FrameLayout line1;
    @BindView(R.id.line2)
    FrameLayout line2;
    @BindView(R.id.line3)
    FrameLayout line3;
    @BindView(R.id.line4)
    FrameLayout line4;
    @BindView(R.id.line5)
    FrameLayout line5;
    @BindView(R.id.line6)
    FrameLayout line6;
    @BindView(R.id.line7)
    FrameLayout line7;
    @BindView(R.id.line8)
    FrameLayout line8;
    @BindView(R.id.fg_main_manage_ll_camera)
    LinearLayout fgMainManageLlCamera;
    @BindView(R.id.fg_main_manage_ll_nameplate)
    LinearLayout fgMainManageLlNameplate;
    @BindView(R.id.line9)
    FrameLayout line9;
    @BindView(R.id.fg_main_manage_ll_basestation)
    LinearLayout fgMainManageLlBasestation;
    @BindView(R.id.line10)
    FrameLayout line10;
    @BindView(R.id.fg_main_manage_ll_nearby)
    LinearLayout fgMainManageLlNearby;
    @BindView(R.id.fg_main_manage_ll_deploy_retry)
    LinearLayout fgMainManageLlDeployRetry;
    @BindView(R.id.line11)
    FrameLayout line11;

    @BindView(R.id.line12)
    FrameLayout line12;
    @BindView(R.id.fg_main_manage_ll_forestfire_manage)
    LinearLayout fgMainManageLlForestfireCamera;
    @BindView(R.id.line13)
    FrameLayout line13;




    private ProgressUtils mProgressUtils;
    private TipDialogUtils mExitDialog;
    private VersionDialogUtils mVersionDialog;
    private TipBleDialogUtils tipBleDialogUtils;

    @Override
    protected void initData(Context activity) {
        initView();
        mPresenter.initData(mRootFragment.getActivity());
    }

    private void initView() {
        tipBleDialogUtils = new TipBleDialogUtils(mRootFragment.getActivity());
        tipBleDialogUtils.setTipDialogUtilsClickListener(new TipBleDialogUtils.TipDialogUtilsClickListener() {
            @Override
            public void onCancelClick() {
//                toastShort("");
            }

            @Override
            public void onConfirmClick() {
                mPresenter.doSignalCheck();
            }
        });
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mRootFragment.getActivity()).build());
        initExitDialog();
        initVersionDialog();
    }

    private void initVersionDialog() {
        mVersionDialog = new VersionDialogUtils(mRootFragment.getActivity());
        mVersionDialog.setTipMessageText(mRootFragment.getString(R.string.version_tip_text) + "  " + AppUtils.getVersionName(mRootFragment.getActivity()));
        mVersionDialog.setTipCacnleText(mRootFragment.getString(R.string.ok), mRootFragment.getActivity().getResources().getColor(R.color.c_a6a6a6));
        mVersionDialog.setVersionDialogUtilsClickListener(this);
    }

    private void initExitDialog() {
        mExitDialog = new TipDialogUtils(mRootFragment.getActivity());
        mExitDialog.setTipMessageText(mRootFragment.getString(R.string.tip_text_login_out));
        mExitDialog.setTipCacnleText(mRootFragment.getString(R.string.cancel), mRootFragment.getActivity().getResources().getColor(R.color.c_a6a6a6));
        mExitDialog.setTipConfirmText(mRootFragment.getString(R.string.quit), mRootFragment.getActivity().getResources().getColor(R.color.c_f34a4a));
        mExitDialog.setTipDialogUtilsClickListener(this);
    }

    @Override
    protected int initRootViewId() {
        return R.layout.fragment_main_manage;
    }

    @Override
    protected ManagerFragmentPresenter createPresenter() {
        return new ManagerFragmentPresenter();
    }


    @Override
    public void startAC(Intent intent) {
        Objects.requireNonNull(mRootFragment.getActivity()).startActivity(intent);
    }

    @Override
    public void finishAc() {
        Objects.requireNonNull(mRootFragment.getActivity()).finish();
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
    public void onFragmentStart() {
        mPresenter.onFragmentStart();
    }

    @Override
    public void onFragmentStop() {
        mPresenter.onFragmentStop();
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
        SensoroToast.getInstance().makeText(mRootFragment.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }

    @Override
    public void onDestroyView() {
        if (mRootView != null) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }

        if (mExitDialog != null) {
            mExitDialog.destory();
        }
        if (mVersionDialog != null) {
            mVersionDialog.destory();
        }
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
//        if (mAlarmPopupView != null) {
//            mAlarmPopupView.onDestroyPop();
//        }
        super.onDestroyView();
    }

    @OnClick(value = {R.id.fg_main_manage_ll_change_merchants, R.id.fg_main_manage_ll_deploy_device,
            R.id.fg_main_manage_ll_contract_management, R.id.fg_main_manage_ll_polling_mission,
            R.id.fg_main_manage_ll_maintenance_mission, R.id.fg_main_manage_ll_scan_login,
            R.id.fg_main_manage_ll_about_us, R.id.fg_main_manage_ll_version_info,
            R.id.fg_main_manage_ll_nameplate, R.id.fg_main_manage_ll_exit, R.id.fg_main_manage_ll_signal_check,
            R.id.fg_main_manage_ll_wire_material_diameter, R.id.fg_main_manage_ll_camera,
            R.id.fg_main_manage_ll_basestation, R.id.fg_main_manage_ll_nearby,R.id.fg_main_manage_ll_deploy_retry,R.id.fg_main_manage_ll_forestfire_manage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fg_main_manage_ll_change_merchants:
                mPresenter.doChangeMerchants();
                break;
            case R.id.fg_main_manage_ll_deploy_device:
                mPresenter.doScanDeploy();
                break;
            case R.id.fg_main_manage_ll_contract_management:
                mPresenter.doContract();
                break;
            case R.id.fg_main_manage_ll_polling_mission:
                mPresenter.doInspection();
                break;
            case R.id.fg_main_manage_ll_maintenance_mission:
                mPresenter.doMaintenanceMission();
                break;
            case R.id.fg_main_manage_ll_scan_login:
                mPresenter.doScanLogin();
                break;
            case R.id.fg_main_manage_ll_about_us:
                mPresenter.doAboutUs();
                break;
            case R.id.fg_main_manage_ll_version_info:
//                mPresenter.doVersionInfo();
                startAC(new Intent(mRootFragment.getActivity(), ListMultiVideoActivity.class));

                break;
            case R.id.fg_main_manage_ll_exit:
                mExitDialog.show();
                break;
            case R.id.fg_main_manage_ll_signal_check:
                if (SensoroCityApplication.getInstance().bleDeviceManager.isBluetoothEnabled()) {
                    mPresenter.doSignalCheck();
                } else {
                    showBleTips();
                }
                break;
            case R.id.fg_main_manage_ll_wire_material_diameter:
                mPresenter.doWireMaterial_diameter();
                break;
            case R.id.fg_main_manage_ll_camera:
                startAC(new Intent(mRootFragment.getActivity(), CameraListActivity.class));
                break;
            case R.id.fg_main_manage_ll_forestfire_manage:
                mPresenter.doManageForestFire();
                break;
            case R.id.fg_main_manage_ll_nameplate:
                mPresenter.doManageNameplate();
                break;
            case R.id.fg_main_manage_ll_basestation:
                mPresenter.doBaseStationList();
                break;
            case R.id.fg_main_manage_ll_nearby:
                startAC(new Intent(mRootFragment.getActivity(), NearByDeviceActivity.class));
                break;
            case R.id.fg_main_manage_ll_deploy_retry:


                startAC(new Intent(mRootFragment.getActivity(), OfflineDeployActivity.class));
                break;
        }
    }

    @Override
    public void setMerchantName(String name) {
        fgMainManageTvMerchantName.setText(name);
    }

    @Override
    public void setAppUpdateVisible(boolean isVisible) {
        fgMainManageTvIsUpgrade.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showVersionDialog() {
        mVersionDialog.show();
    }

    @Override
    public void setContractVisible(boolean isVisible) {
        fgMainManageLlContractManagement.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        line2.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setDeployOfflineTaskVisible(boolean hasDeployOfflineTask) {
        fgMainManageLlDeployRetry.setVisibility(hasDeployOfflineTask ? View.VISIBLE : View.GONE);
        line12.setVisibility(hasDeployOfflineTask ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setInspectionVisible(boolean isVisible) {
        fgMainManageLlPollingMission.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        line3.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setScanLoginVisible(boolean isVisible) {
        fgMainManageLlScanLoginOut.setVisibility(isVisible ? View.VISIBLE : View.GONE);

    }

    @Override
    public void setMerchantVisible(boolean isVisible) {
        ivMerchantArrow.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void changeMerchantTitle(boolean hasSubMerchant) {
        if (!hasSubMerchant) {
            fgMainManageImvMerchantIcon.setImageResource(R.drawable.single_merchant1);
            fgMainManageTvMerchantTitle.setText(R.string.business_name);
        }
    }

    @Override
    public void setSignalCheckVisible(boolean hasSignalCheck) {
        fgMainManageLlSignalCheck.setVisibility(hasSignalCheck ? View.VISIBLE : View.GONE);
        line6.setVisibility(hasSignalCheck ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setDeviceCameraVisible(boolean hasDeviceCamera) {
        fgMainManageLlCamera.setVisibility(hasDeviceCamera ? View.VISIBLE : View.GONE);
        line8.setVisibility(hasDeviceCamera ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setStationManagerVisible(boolean hasStationList) {
        line9.setVisibility(hasStationList ? View.VISIBLE : View.GONE);
        fgMainManageLlBasestation.setVisibility(hasStationList ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setNameplateVisible(boolean hasNameplate) {
        line10.setVisibility(hasNameplate ? View.VISIBLE : View.GONE);
        fgMainManageLlNameplate.setVisibility(hasNameplate ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setIBeaconVisible(boolean hasIBeacon) {
        line11.setVisibility(hasIBeacon ? View.VISIBLE : View.GONE);
        fgMainManageLlNearby.setVisibility(hasIBeacon ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setForestfireManagerVisible(boolean isVisible) {
        line13.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        fgMainManageLlForestfireCamera.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }


    @Override
    public void onCancelClick() {
        mExitDialog.dismiss();
    }

    @Override
    public void onConfirmClick() {
        mExitDialog.dismiss();
        mPresenter.doExitAccount();
    }

    @Override
    public void onVersionCancelClick() {
        mVersionDialog.dismiss();
    }

    @Override
    public void onVersionConfirmClick() {

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

    public void handlerActivityResult(int requestCode, int resultCode, Intent data) {
        if (tipBleDialogUtils != null) {
            tipBleDialogUtils.onActivityResult(requestCode, resultCode, data);
        }
    }
}

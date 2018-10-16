package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IManagerFragmentView;
import com.sensoro.smartcity.presenter.ManagerFragmentPresenter;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroToast;
import com.sensoro.smartcity.widget.dialog.TipDialogUtils;
import com.sensoro.smartcity.widget.dialog.VersionDialogUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class ManagerFragment extends BaseFragment<IManagerFragmentView, ManagerFragmentPresenter> implements
        IManagerFragmentView, TipDialogUtils.TipDialogUtilsClickListener, VersionDialogUtils.VersionDialogUtilsClickListener {
    @BindView(R.id.fg_main_manage_tv_merchant_name)
    TextView fgMainManageTvMerchantName;
    @BindView(R.id.fg_main_manage_imv_merchant_icon)
    ImageView fgMainManageimvMerchantIcon;
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
    @BindView(R.id.line1)
    View line1;
    @BindView(R.id.line2)
    View line2;
    @BindView(R.id.line3)
    View line3;
    @BindView(R.id.line4)
    View line4;
    private ProgressUtils mProgressUtils;
    private TipDialogUtils mExitDialog;
    private VersionDialogUtils mVersionDialog;

    @Override
    protected void initData(Context activity) {
        initView();
        mPresenter.initData(mRootFragment.getActivity());
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mRootFragment.getActivity()).build());
        initExitDialog();
        initVersionDialog();
    }

    private void initVersionDialog() {
        mVersionDialog = new VersionDialogUtils(mRootFragment.getActivity());
        mVersionDialog.setTipMessageText("现在App版本为最新版  " + AppUtils.getVersionName(mRootFragment.getActivity()));
        mVersionDialog.setTipCacnleText("好的", mRootFragment.getActivity().getResources().getColor(R.color.c_a6a6a6));
        mVersionDialog.setVersionDialogUtilsClickListener(this);
    }

    private void initExitDialog() {
        mExitDialog = new TipDialogUtils(mRootFragment.getActivity());
        mExitDialog.setTipMessageText("确定要退出登录吗？");
        mExitDialog.setTipCacnleText("取消", mRootFragment.getActivity().getResources().getColor(R.color.c_a6a6a6));
        mExitDialog.setTipConfirmText("退出", mRootFragment.getActivity().getResources().getColor(R.color.c_f34a4a));
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
        mRootFragment.getActivity().startActivity(intent);
    }

    @Override
    public void finishAc() {
        mRootFragment.getActivity().finish();
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
        SensoroToast.INSTANCE.makeText(mRootFragment.getActivity(), msg, Toast.LENGTH_SHORT).show();
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

    @OnClick({R.id.fg_main_manage_ll_change_merchants, R.id.fg_main_manage_ll_deploy_device, R.id.fg_main_manage_ll_contract_management, R.id.fg_main_manage_ll_polling_mission, R.id.fg_main_manage_ll_maintenance_mission, R.id.fg_main_manage_ll_scan_login, R.id.fg_main_manage_ll_about_us, R.id.fg_main_manage_ll_version_info, R.id.fg_main_manage_ll_exit})
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
                mPresenter.doVersionInfo();
                break;
            case R.id.fg_main_manage_ll_exit:
                mExitDialog.show();
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
            fgMainManageimvMerchantIcon.setImageResource(R.drawable.single_merchant);
            fgMainManageTvMerchantTitle.setText("商户名称");
        }
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
}

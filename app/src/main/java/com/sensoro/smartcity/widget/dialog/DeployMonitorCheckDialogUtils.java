package com.sensoro.smartcity.widget.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sensoro.smartcity.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DeployMonitorCheckDialogUtils {
    private final Activity mActivity;
    @BindView(R.id.iv_deploy_check_cancel)
    ImageView ivDeployCheckCancel;
    @BindView(R.id.iv_deploy_check_dialog_device_location)
    ImageView ivDeployCheckDialogDeviceLocation;
    @BindView(R.id.tv_deploy_check_dialog_device_location_error_desc)
    TextView tvDeployCheckDialogDeviceLocationErrorDesc;
    @BindView(R.id.iv_deploy_check_dialog_init_config)
    ImageView ivDeployCheckDialogInitConfig;
    @BindView(R.id.tv_deploy_check_dialog_init_config_error_desc)
    TextView tvDeployCheckDialogInitConfigErrorDesc;
    @BindView(R.id.tv_deploy_check_dialog_signal)
    TextView tvDeployCheckDialogSignal;
    @BindView(R.id.iv_deploy_check_dialog_signal_strength)
    ImageView ivDeployCheckDialogSignalStrength;
    @BindView(R.id.tv_deploy_check_dialog_signal_strength_error_desc)
    TextView tvDeployCheckDialogSignalStrengthErrorDesc;
    @BindView(R.id.iv_deploy_check_dialog_device_status)
    ImageView ivDeployCheckDialogDeviceStatus;
    @BindView(R.id.tv_deploy_check_dialog_device_status_error_desc)
    TextView tvDeployCheckDialogDeviceStatusErrorDesc;
    @BindView(R.id.tv_deploy_check_dialog_repair_suggest)
    TextView tvDeployCheckDialogRepairSuggest;
    @BindView(R.id.tv_deploy_check_dialog_test)
    TextView tvDeployCheckDialogTest;
    @BindView(R.id.tv_deploy_check_dialog_force_upload)
    TextView tvDeployCheckDialogForceUpload;
    @BindView(R.id.rl_deploy_check_dialog_device_location)
    RelativeLayout rlDeployCheckDialogDeviceLocation;
    @BindView(R.id.rl_deploy_check_dialog_init_config)
    RelativeLayout rlDeployCheckDialogInitConfig;
    @BindView(R.id.rl_deploy_check_dialog_signal_strength)
    RelativeLayout rlDeployCheckDialogSignalStrength;
    @BindView(R.id.rl_deploy_check_dialog_device_status)
    RelativeLayout rlDeployCheckDialogDeviceStatus;
    @BindView(R.id.tv_deploy_check_dialog_device_location)
    TextView tvDeployCheckDialogDeviceLocation;
    @BindView(R.id.tv_deploy_check_dialog_init_config)
    TextView tvDeployCheckDialogInitConfig;
    @BindView(R.id.tv_deploy_check_dialog_device_status)
    TextView tvDeployCheckDialogDeviceStatus;
    private CustomCornerDialog mDialog;
    private OnDeployCheckDialogListener listener;
    private final Unbinder bind;
    private RotateAnimation rotateAnimation;
    private final int grayColor;
    private final int blackColor;
    private final int yellowColor;
    private final int redColor;

    public DeployMonitorCheckDialogUtils(Activity activity) {
        View view = View.inflate(activity, R.layout.item_deploy_monitor_check_dialog, null);
        mActivity = activity;
        mDialog = new CustomCornerDialog(activity, R.style.CustomCornerDialogStyle, view);
        bind = ButterKnife.bind(this, view);
        grayColor = mActivity.getResources().getColor(R.color.c_a6a6a6);
        blackColor = mActivity.getResources().getColor(R.color.c_252525);
        yellowColor = mActivity.getResources().getColor(R.color.c_fdc83b);
        redColor = mActivity.getResources().getColor(R.color.c_f34a4a);
        init();

    }

    private void init() {
        rotateAnimation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(500);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setFillBefore(true);
        rotateAnimation.setFillAfter(true);
    }

    public void setCanceledOnTouchOutside(boolean canceled) {
        if (mDialog != null) {
            mDialog.setCanceledOnTouchOutside(canceled);
        }
    }

    public void setCancelable(boolean flag) {
        if (mDialog != null) {
            mDialog.setCancelable(flag);
        }

    }

    public void setOnDeployCheckDialogListener(OnDeployCheckDialogListener listener) {
        this.listener = listener;
    }

    /**
     * 展示的条目数
     *
     * @param status 1 一项 2 三项 3 四项
     */
    public void show(int status) {
        if (mDialog != null) {
            initDialogStatus(status);
            mDialog.show();
        }
    }


    private void initDialogStatus(int status) {
        switch (status) {
            case 1:
                rlDeployCheckDialogDeviceLocation.setVisibility(View.VISIBLE);
                rlDeployCheckDialogInitConfig.setVisibility(View.GONE);
                rlDeployCheckDialogDeviceStatus.setVisibility(View.GONE);
                rlDeployCheckDialogSignalStrength.setVisibility(View.GONE);
                break;
            case 2:
                rlDeployCheckDialogDeviceLocation.setVisibility(View.VISIBLE);
                rlDeployCheckDialogInitConfig.setVisibility(View.GONE);
                rlDeployCheckDialogDeviceStatus.setVisibility(View.VISIBLE);
                rlDeployCheckDialogSignalStrength.setVisibility(View.VISIBLE);
                break;
            case 3:
                rlDeployCheckDialogDeviceLocation.setVisibility(View.VISIBLE);
                rlDeployCheckDialogInitConfig.setVisibility(View.VISIBLE);
                rlDeployCheckDialogDeviceStatus.setVisibility(View.VISIBLE);
                rlDeployCheckDialogSignalStrength.setVisibility(View.VISIBLE);
                break;
        }

        ivDeployCheckCancel.setVisibility(View.GONE);

        ivDeployCheckDialogDeviceLocation.clearAnimation();
        ivDeployCheckDialogDeviceLocation.setImageResource(R.drawable.deploy_check_loading_imv);
        ivDeployCheckDialogDeviceLocation.setVisibility(View.GONE);
        ivDeployCheckDialogInitConfig.clearAnimation();
        ivDeployCheckDialogInitConfig.setImageResource(R.drawable.deploy_check_loading_imv);
        ivDeployCheckDialogInitConfig.setVisibility(View.GONE);
        ivDeployCheckDialogSignalStrength.clearAnimation();
        ivDeployCheckDialogSignalStrength.setImageResource(R.drawable.deploy_check_loading_imv);
        ivDeployCheckDialogSignalStrength.setVisibility(View.GONE);
        ivDeployCheckDialogDeviceStatus.clearAnimation();
        ivDeployCheckDialogDeviceStatus.setImageResource(R.drawable.deploy_check_loading_imv);
        ivDeployCheckDialogDeviceStatus.setVisibility(View.GONE);

        tvDeployCheckDialogDeviceLocationErrorDesc.setVisibility(View.GONE);
        tvDeployCheckDialogInitConfigErrorDesc.setVisibility(View.GONE);
        tvDeployCheckDialogSignalStrengthErrorDesc.setVisibility(View.GONE);
        tvDeployCheckDialogDeviceStatusErrorDesc.setVisibility(View.GONE);


        tvDeployCheckDialogDeviceLocation.setTextColor(grayColor);
        tvDeployCheckDialogInitConfig.setTextColor(grayColor);
        tvDeployCheckDialogSignal.setTextColor(grayColor);
        tvDeployCheckDialogSignal.setCompoundDrawables(null,null,null,null);
        tvDeployCheckDialogDeviceStatus.setTextColor(grayColor);

        tvDeployCheckDialogRepairSuggest.setVisibility(View.GONE);

        tvDeployCheckDialogTest.setVisibility(View.GONE);
        tvDeployCheckDialogForceUpload.setVisibility(View.GONE);
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public void startDeviceLocationLoading() {
        tvDeployCheckDialogDeviceLocation.setTextColor(blackColor);
        ivDeployCheckDialogDeviceLocation.setVisibility(View.VISIBLE);
        ivDeployCheckDialogDeviceLocation.startAnimation(rotateAnimation);
    }

    public void setDeviceLocationSuccess() {
        ivDeployCheckDialogDeviceLocation.clearAnimation();
        ivDeployCheckDialogDeviceLocation.setImageResource(R.drawable.deploy_test_success);
    }

    public void setDeviceLocationFailed() {
        ivDeployCheckDialogDeviceLocation.clearAnimation();
        ivDeployCheckDialogDeviceLocation.setVisibility(View.GONE);
        tvDeployCheckDialogDeviceLocationErrorDesc.setVisibility(View.VISIBLE);
        tvDeployCheckDialogDeviceLocationErrorDesc.setText(mActivity.getString(R.string.no_near));
    }

    public void startInitConfigLoading() {
        tvDeployCheckDialogInitConfig.setTextColor(blackColor);
        ivDeployCheckDialogInitConfig.setVisibility(View.VISIBLE);
        ivDeployCheckDialogInitConfig.startAnimation(rotateAnimation);
    }

    public void setInitConfigSuccess() {
        ivDeployCheckDialogInitConfig.clearAnimation();
        ivDeployCheckDialogInitConfig.setImageResource(R.drawable.deploy_test_success);
    }

    public void setInitConfigFailed() {
        ivDeployCheckDialogInitConfig.clearAnimation();
        ivDeployCheckDialogInitConfig.setVisibility(View.GONE);
        tvDeployCheckDialogInitConfigErrorDesc.setVisibility(View.VISIBLE);
        tvDeployCheckDialogInitConfigErrorDesc.setText(mActivity.getString(R.string.failed));
    }

    public void startSignalStrengthLoading() {
        tvDeployCheckDialogSignal.setTextColor(blackColor);
        ivDeployCheckDialogSignalStrength.setVisibility(View.VISIBLE);
        ivDeployCheckDialogSignalStrength.startAnimation(rotateAnimation);
    }

    /**
     * @param status 1信号优 2 信号良
     */
    public void setSignalStrengthSuccess(int status) {
        ivDeployCheckDialogSignalStrength.clearAnimation();
        ivDeployCheckDialogSignalStrength.setImageResource(R.drawable.deploy_test_success);
        Drawable drawable;
        String text;
        switch (status) {
            case 1:
                drawable = mActivity.getResources().getDrawable(R.drawable.signal_good);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                break;
            case 2:
                drawable = mActivity.getResources().getDrawable(R.drawable.signal_normal);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                break;
            default:
                drawable = mActivity.getResources().getDrawable(R.drawable.signal_good);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                break;
        }
        tvDeployCheckDialogSignal.setCompoundDrawables(null, null, drawable, null);

    }

    /**
     * @param status 1 信号差 2 无信号
     */
    public void setSignalStrengthFailed(int status) {
        ivDeployCheckDialogSignalStrength.clearAnimation();
        ivDeployCheckDialogSignalStrength.setVisibility(View.GONE);
        Drawable drawable;
        Drawable signalDrawable;
        String text;
        switch (status) {
            case 1:
                drawable = mActivity.getResources().getDrawable(R.drawable.deploy_check_yellow);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                signalDrawable = mActivity.getResources().getDrawable(R.drawable.signal_bad);
                signalDrawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                text = mActivity.getString(R.string.deploy_check_signal_bad);
                tvDeployCheckDialogSignalStrengthErrorDesc.setTextColor(yellowColor);
                break;
            case 2:
                drawable = mActivity.getResources().getDrawable(R.drawable.deploy_check_failed_red);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                signalDrawable = mActivity.getResources().getDrawable(R.drawable.signal_none);
                signalDrawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                text = mActivity.getString(R.string.deploy_check_signal_none);
                tvDeployCheckDialogSignalStrengthErrorDesc.setTextColor(redColor);
                break;
            default:
                drawable = mActivity.getResources().getDrawable(R.drawable.deploy_check_failed_red);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                text = mActivity.getString(R.string.deploy_check_signal_none);
                signalDrawable = mActivity.getResources().getDrawable(R.drawable.signal_none);
                signalDrawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tvDeployCheckDialogSignalStrengthErrorDesc.setTextColor(yellowColor);
                break;
        }
        tvDeployCheckDialogSignalStrengthErrorDesc.setVisibility(View.VISIBLE);
        tvDeployCheckDialogSignalStrengthErrorDesc.setText(text);
        tvDeployCheckDialogSignalStrengthErrorDesc.setCompoundDrawables(drawable, null, null, null);
        tvDeployCheckDialogSignal.setCompoundDrawables(null, null, signalDrawable, null);
    }

    public void startDeviceStatusLoading() {
        tvDeployCheckDialogDeviceStatus.setTextColor(blackColor);
        ivDeployCheckDialogDeviceStatus.setVisibility(View.VISIBLE);
        ivDeployCheckDialogDeviceStatus.startAnimation(rotateAnimation);
    }

    public void setDeviceStatusSuccess() {
        ivDeployCheckDialogDeviceStatus.clearAnimation();
        ivDeployCheckDialogDeviceStatus.setImageResource(R.drawable.deploy_test_success);
    }

    /**
     * @param status 1 故障 2 预警
     */
    public void setDeviceStatusFailed(int status) {
        ivDeployCheckDialogDeviceStatus.clearAnimation();
        ivDeployCheckDialogDeviceStatus.setVisibility(View.GONE);
        Drawable drawable;
        String text;
        switch (status) {
            case 1:
                drawable = mActivity.getResources().getDrawable(R.drawable.deploy_check_yellow);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                text = mActivity.getString(R.string.malfunctioning);
                tvDeployCheckDialogDeviceStatusErrorDesc.setTextColor(yellowColor);
                break;
            case 2:
                drawable = mActivity.getResources().getDrawable(R.drawable.deploy_check_failed_red);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                text = mActivity.getString(R.string.alarming);
                tvDeployCheckDialogDeviceStatusErrorDesc.setTextColor(redColor);
                break;
            default:
                drawable = mActivity.getResources().getDrawable(R.drawable.deploy_check_failed_red);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                text = mActivity.getString(R.string.alarming);
                tvDeployCheckDialogDeviceStatusErrorDesc.setTextColor(redColor);
                break;
        }
        tvDeployCheckDialogDeviceStatusErrorDesc.setVisibility(View.VISIBLE);
        tvDeployCheckDialogDeviceStatusErrorDesc.setText(text);
        tvDeployCheckDialogDeviceStatusErrorDesc.setCompoundDrawables(drawable,null,null,null);
    }

    public void setRetestButtonVisible(boolean isVisible) {
        tvDeployCheckDialogTest.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void setForceUploadButtonVisible(boolean isVisible) {
        tvDeployCheckDialogForceUpload.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void setRepairSuggest(CharSequence text) {
        tvDeployCheckDialogRepairSuggest.setVisibility(View.VISIBLE);
        tvDeployCheckDialogRepairSuggest.setText(text);
        tvDeployCheckDialogRepairSuggest.setMovementMethod(LinkMovementMethod.getInstance());
        tvDeployCheckDialogRepairSuggest.setHighlightColor(Color.TRANSPARENT);
    }

    public void setDeployCancelVisible(boolean isVisible) {
        ivDeployCheckCancel.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void setDeviceLocationVisible(boolean isVisible) {
        rlDeployCheckDialogDeviceLocation.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void setInitConfigVisible(boolean isVisible) {
        rlDeployCheckDialogInitConfig.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void setSignalStrengthVisible(boolean isVisible) {
        rlDeployCheckDialogSignalStrength.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void setDeviceStatusVisible(boolean isVisible) {
        rlDeployCheckDialogDeviceStatus.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }



    public void destroy() {
        if (mDialog != null) {
            bind.unbind();
            mDialog.cancel();
            mDialog = null;
        }
    }

    @OnClick({R.id.tv_deploy_check_dialog_test, R.id.tv_deploy_check_dialog_force_upload, R.id.iv_deploy_check_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_deploy_check_dialog_test:
                if (listener != null) {
                    listener.onClickTest();
                }
                break;
            case R.id.tv_deploy_check_dialog_force_upload:
                if (listener != null) {
                    listener.onClickForceUpload();
                }
                break;
            case R.id.iv_deploy_check_cancel:
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                break;
        }
    }



    public interface OnDeployCheckDialogListener {
        void onClickTest();

        void onClickForceUpload();

        void onClickDeviceDetailInfo();
    }
}

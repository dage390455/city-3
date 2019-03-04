package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IScanActivityView;
import com.sensoro.smartcity.presenter.ScanActivityPresenter;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.BarcodeType;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;

public class ScanActivity extends BaseActivity<IScanActivityView, ScanActivityPresenter> implements
        IScanActivityView, QRCodeView.Delegate {
    @BindView(R.id.ac_scan_qr_view)
    ZBarView acScanQrView;
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubTitle;
    @BindView(R.id.ac_scan_tv_input_sn)
    TextView acScanTvInputSn;
    @BindView(R.id.ac_scan_tv_open_flashlight)
    TextView acScanTvOpenFlashlight;
    @BindView(R.id.ac_scan_ll_bottom)
    LinearLayout acScanLlBottom;
    @BindView(R.id.include_text_title_cl_root)
    ConstraintLayout includeTextTitleClRoot;
    @BindView(R.id.include_text_title_divider)
    View acScanDivider;
    private boolean isFlashOn;
    private ProgressUtils mProgressUtils;
    private ImmersionBar immersionBar;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        includeTextTitleImvArrowsLeft.setColorFilter(mActivity.getResources().getColor(R.color.white));
        acScanDivider.setVisibility(View.GONE);
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        includeTextTitleTvTitle.setTextColor(Color.WHITE);
        includeTextTitleTvSubTitle.setVisibility(View.GONE);
        includeTextTitleClRoot.setBackgroundColor(Color.TRANSPARENT);
        acScanQrView.setDelegate(this);
        acScanQrView.getScanBoxView().setOnlyDecodeScanBoxArea(false);
        acScanQrView.getScanBoxView().setShowLocationPoint(false);
        acScanQrView.getScanBoxView().setAutoZoom(true);
        acScanQrView.changeToScanQRCodeStyle(); // 切换成扫描二维码样式
        acScanQrView.setType(BarcodeType.ONLY_QR_CODE, null); // 只识别 QR_CODE

    }

    @Override
    protected ScanActivityPresenter createPresenter() {
        return new ScanActivityPresenter();
    }

    @Override
    public boolean isActivityOverrideStatusBar() {
        immersionBar = ImmersionBar.with(mActivity);
        immersionBar
                .transparentStatusBar()
                .init();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        acScanQrView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
//        mZBarView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别
        startScan();
    }


    @Override
    protected void onStop() {
        super.onStop();
        stopScan();
    }

    @Override
    protected void onDestroy() {
        mProgressUtils.destroyProgress();
        acScanQrView.onDestroy();

        if (immersionBar != null) {
            immersionBar.destroy();
        }
        super.onDestroy();
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
        SensoroToast.INSTANCE.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }


    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.ac_scan_tv_input_sn, R.id.ac_scan_tv_open_flashlight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
                break;
            case R.id.ac_scan_tv_input_sn:
                mPresenter.openSNTextAc();
                break;
            case R.id.ac_scan_tv_open_flashlight:
                acScanQrView.getCameraPreview().surfaceCreated(null);
                if (isFlashOn) {
                    acScanQrView.closeFlashlight();
                } else {
                    acScanQrView.openFlashlight();
                }
                isFlashOn = !isFlashOn;
                break;
        }
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        mPresenter.processResult(result);
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        if (mPresenter.scanType != Constants.TYPE_SCAN_LOGIN) {
            try {
                // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
                String tipText = acScanQrView.getScanBoxView().getTipText();
                String ambientBrightnessTip = "\n" + mActivity.getString(R.string.the_environment_is_too_dark_please_turn_on_the_flash);
                if (isDark) {
                    if (!tipText.contains(ambientBrightnessTip)) {
                        acScanQrView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
                    }
                } else {
                    if (tipText.contains(ambientBrightnessTip)) {
                        int endIndex = tipText.indexOf(ambientBrightnessTip);
                        tipText = tipText.substring(0, endIndex);
                        acScanQrView.getScanBoxView().setTipText(tipText);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        try {
            LogUtils.loge(this, "扫描出错！！！！！！！");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void startScan() {
        acScanQrView.startSpotAndShowRect();
    }

    @Override
    public void stopScan() {
        acScanQrView.stopCamera();
    }

    @Override
    public void updateTitleText(String title) {
        includeTextTitleTvTitle.setText(title);
    }

    @Override
    public void setBottomVisible(boolean isVisible) {
        acScanLlBottom.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateQrTipText(String tip) {
        //源码没有提供修改的接口，xml中直接修改成了对准二维码，扫描
        acScanQrView.getScanBoxView().setQRCodeTipText(tip);
    }

}

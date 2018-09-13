package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IScanActivityView;
import com.sensoro.smartcity.presenter.ScanActivityPresenter;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class ScanActivity extends BaseActivity<IScanActivityView, ScanActivityPresenter> implements
        IScanActivityView, QRCodeView.Delegate {
    @BindView(R.id.ac_scan_qr_view)
    ZXingView acScanQrView;
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
    private boolean isFlashOn;
    private ProgressUtils mProgressUtils;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        includeTextTitleTvTitle.setTextColor(Color.WHITE);
        includeTextTitleTvSubTitle.setVisibility(View.GONE);
        includeTextTitleClRoot.setBackgroundColor(Color.TRANSPARENT);
        changeIconArrowsColor();

        acScanQrView.setDelegate(this);
        acScanQrView.getScanBoxView().setOnlyDecodeScanBoxArea(true);
        acScanQrView.getCameraPreview().setAutoFocusFailureDelay(0);
    }

    private void changeIconArrowsColor() {
        Drawable drawable = includeTextTitleImvArrowsLeft.getDrawable();
        Drawable.ConstantState state = drawable.getConstantState();
        DrawableCompat.wrap(state == null ? drawable : state.newDrawable()).mutate();
        drawable.setBounds(0, 0, drawable.getIntrinsicHeight(), drawable.getIntrinsicHeight());
        DrawableCompat.setTint(drawable, Color.WHITE);
        includeTextTitleImvArrowsLeft.setImageDrawable(drawable);
    }

    @Override
    protected ScanActivityPresenter createPresenter() {
        return new ScanActivityPresenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
    public void onScanQRCodeOpenCameraError() {
        LogUtils.loge(this,"扫描出错！！！！！！！");
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

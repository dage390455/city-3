package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gyf.immersionbar.ImmersionBar;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.imainviews.IScanActivityView;
import com.sensoro.smartcity.presenter.ScanActivityPresenter;
import com.sensoro.smartcity.widget.ViewFinderView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.szx.simplescanner.zbar.Result;
import cn.szx.simplescanner.zbar.ZBarScannerView;

@Route(path = ARouterConstants.ACTIVITY_SCAN)
public class ScanActivity extends BaseActivity<IScanActivityView, ScanActivityPresenter> implements
        IScanActivityView, ZBarScannerView.ResultHandler {
    @BindView(R.id.ac_scan_qr_view)
    FrameLayout scanQrViewRoot;
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
    private ZBarScannerView zBarScannerView;
    private ViewFinderView viewFinderView;

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
        //
        viewFinderView = new ViewFinderView(mActivity);
        zBarScannerView = new ZBarScannerView(mActivity, viewFinderView, this);
        scanQrViewRoot.addView(zBarScannerView);
        //TODO 暂时添加支持所有类型的扫描
//        ArrayList<BarcodeFormat> formats = new ArrayList<>(2);
//        formats.add(BarcodeFormat.QRCODE);
//        支持code39的条形码扫描
//        formats.add(BarcodeFormat.CODE39);
//        zBarScannerView.setFormats(formats);

    }

    @Override
    protected ScanActivityPresenter createPresenter() {
        return new ScanActivityPresenter();
    }

    @Override
    public boolean isActivityOverrideStatusBar() {
        immersionBar = ImmersionBar.with(mActivity);
        immersionBar.transparentStatusBar().init();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mProgressUtils.destroyProgress();

//        if (immersionBar != null) {
//            immersionBar.destroy();
//        }
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
        SensoroToast.getInstance().makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
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
                isFlashOn = !isFlashOn;
                zBarScannerView.setFlash(isFlashOn);
                break;
        }
    }

    @Override
    public void startScan() {
        zBarScannerView.getOneMoreFrame();
    }

    @Override
    public void stopScan() {
        zBarScannerView.stopCamera();
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
        viewFinderView.setTipText(tip);
    }

    @Override
    public void onResume() {
        super.onResume();
        zBarScannerView.startCamera();//打开系统相机，并进行基本的初始化
    }

    @Override
    public void onPause() {
        super.onPause();
        zBarScannerView.stopCamera();//释放相机资源等各种资源
    }

    @Override
    public void handleResult(Result rawResult) {
        String contents = rawResult.getContents();
        mPresenter.processResult(contents);
//        toastShort("Contents = " + contents + ", Format = " + rawResult.getBarcodeFormat().getName());
//        // Note:
//        // * Wait 2 seconds to resume the preview.
//        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
//        // * I don't know why this is the case but I don't have the time to figure out.
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startScan();
//            }
//        }, 2000);
    }
}

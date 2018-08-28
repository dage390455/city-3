package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IScanLoginFragmentView;
import com.sensoro.smartcity.presenter.ScanLoginFragmentPresenter;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroToast;

import cn.bingoogolapple.qrcode.core.QRCodeView;

import static com.sensoro.smartcity.constant.Constants.INPUT;

/**
 * Created by sensoro on 17/7/24.
 */

public class ScanLoginFragment extends BaseFragment<IScanLoginFragmentView,
        ScanLoginFragmentPresenter>
        implements IScanLoginFragmentView, View.OnClickListener, QRCodeView.Delegate {
    private static final String TAG = ScanLoginFragment.class.getSimpleName();

    private QRCodeView mQRCodeView;
    private ImageView mMenuImageView;

    private ProgressUtils mProgressUtils;


    public static ScanLoginFragment newInstance(String input) {
        ScanLoginFragment stationDeployFragment = new ScanLoginFragment();
        Bundle args = new Bundle();
        args.putString(INPUT, input);
        stationDeployFragment.setArguments(args);
        return stationDeployFragment;
    }


    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mRootFragment.getActivity()).build());
        mMenuImageView = (ImageView) mRootView.findViewById(R.id.deploy_iv_menu_list);
        mMenuImageView.setColorFilter(getResources().getColor(R.color.white));
        mMenuImageView.setOnClickListener(this);
        mQRCodeView = mRootView.findViewById(R.id.scan_view);
        mQRCodeView.setDelegate(this);
        mQRCodeView.getScanBoxView().setOnlyDecodeScanBoxArea(true);
        mQRCodeView.getCameraPreview().setAutoFocusFailureDelay(0);
    }

    @Override
    public void onDestroyView() {
        mProgressUtils.destroyProgress();
        mQRCodeView.onDestroy();
        super.onDestroyView();
    }


    @Override
    protected void initData(Context activity) {
        initView();
        mPresenter.initData(activity);
    }


    @Override
    protected int initRootViewId() {
        return R.layout.fragment_scan_login;
    }

    @Override
    protected ScanLoginFragmentPresenter createPresenter() {
        return new ScanLoginFragmentPresenter();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deploy_iv_menu_list:
                ((MainActivity) getActivity()).openMenu();
                break;
            default:
                break;
        }
    }


    @Override
    public void startScan() {
//        mQRCodeView.startCamera();
//        mQRCodeView.startSpotDelay(1000);
//        mQRCodeView.showScanRect();
        mQRCodeView.startSpotAndShowRect();
    }

    @Override
    public void stopScan() {
        mQRCodeView.stopCamera();
    }


    @Override
    public void toastShort(String msg) {
        SensoroToast.INSTANCE.makeText(mRootFragment.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }


    @Override
    public void startAC(Intent intent) {
        mRootFragment.getActivity().startActivity(intent);
    }

    @Override
    public void finishAc() {

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
    public void onScanQRCodeSuccess(String result) {
        mPresenter.processResult(result);
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

    @Override
    public void onFragmentStart() {
        startScan();
    }

    @Override
    public void onFragmentStop() {
        stopScan();
    }
}

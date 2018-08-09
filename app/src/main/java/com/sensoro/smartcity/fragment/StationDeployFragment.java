package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IStationDeployFragmentView;
import com.sensoro.smartcity.presenter.StationDeployFragmentPresenter;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroToast;

import cn.bingoogolapple.qrcode.core.QRCodeView;

import static com.sensoro.smartcity.constant.Constants.INPUT;

/**
 * Created by sensoro on 17/7/24.
 */

public class StationDeployFragment extends BaseFragment<IStationDeployFragmentView,
        StationDeployFragmentPresenter>
        implements IStationDeployFragmentView, View.OnClickListener, QRCodeView.Delegate {
    private static final String TAG = StationDeployFragment.class.getSimpleName();

    private QRCodeView mQRCodeView;
    private ImageView mMenuImageView;
    private ImageView flashImageView;
    private ImageView manualImageView;

    private ProgressUtils mProgressUtils;
    private boolean isFlashOn = false;
    private TextView sensorDeployTitle;


    public static StationDeployFragment newInstance(String input) {
        StationDeployFragment stationDeployFragment = new StationDeployFragment();
        Bundle args = new Bundle();
        args.putString(INPUT, input);
        stationDeployFragment.setArguments(args);
        return stationDeployFragment;
    }


    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mRootFragment.getActivity()).build());
        flashImageView = (ImageView) mRootView.findViewById(R.id.zxing_capture_iv_flash);
        manualImageView = (ImageView) mRootView.findViewById(R.id.zxing_capture_iv_manual);
        mMenuImageView = (ImageView) mRootView.findViewById(R.id.deploy_iv_menu_list);
        sensorDeployTitle = (TextView) mRootView.findViewById(R.id.sensor_deploy_title);
        mMenuImageView.setColorFilter(getResources().getColor(R.color.white));
        mMenuImageView.setOnClickListener(this);
        flashImageView.setOnClickListener(this);
        manualImageView.setOnClickListener(this);
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

//    public void hiddenRootView() {
//        if (mRootView != null) {
//            mRootView.setVisibility(View.GONE);
//        }
//    }
//
//    public void showRootView() {
//        try {
//            mRootView.setVisibility(View.VISIBLE);
//            if (mQRCodeView != null) {
//                mQRCodeView.showScanRect();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    protected void initData(Context activity) {
        initView();
        mPresenter.initData(activity);
    }


    @Override
    protected int initRootViewId() {
        return R.layout.fragment_station_deploy;
    }

    @Override
    protected StationDeployFragmentPresenter createPresenter() {
        return new StationDeployFragmentPresenter();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.zxing_capture_iv_flash:
                //改变mSurfaceCreated变量强制开启闪光灯 针对1.2.4
                mQRCodeView.getCameraPreview().surfaceCreated(null);
                if (isFlashOn) {
                    mQRCodeView.closeFlashlight();
                } else {
                    mQRCodeView.openFlashlight();
                }
                isFlashOn = !isFlashOn;
                break;
            case R.id.zxing_capture_iv_manual:
                mPresenter.openSNTextAc();
                break;
            case R.id.deploy_iv_menu_list:
                ((MainActivity) getActivity()).openMenu();
                break;
            default:
                break;
        }
    }


    @Override
    public void setFlashLightState(boolean isOn) {
        flashImageView.setBackgroundResource(isOn ? R.drawable.zxing_flash_on : R.drawable.zxing_flash_off);
    }

    @Override
    public void setTitle(String title) {
        if (sensorDeployTitle != null) {
            sensorDeployTitle.setText(title);
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
        SensoroToast.makeText(mRootFragment.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }


    @Override
    public void startAC(Intent intent) {

    }

    @Override
    public void finishAc() {

    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {
        mRootFragment.getActivity().startActivityForResult(intent, requestCode);
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

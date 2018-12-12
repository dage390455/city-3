package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.sensoro.libbleserver.ble.BLEDevice;
import com.sensoro.libbleserver.ble.SensoroConnectionCallback;
import com.sensoro.libbleserver.ble.SensoroDeviceConnection;
import com.sensoro.libbleserver.ble.SensoroWriteCallback;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployMonitorConfigurationView;
import com.sensoro.smartcity.model.DeployAnalyzerModel;
import com.sensoro.smartcity.util.BleObserver;
import com.sensoro.smartcity.util.LogUtils;

import java.io.Serializable;
import java.util.ArrayList;

public class DeployMonitorConfigurationPresenter extends BasePresenter<IDeployMonitorConfigurationView>
implements Runnable,BLEDeviceListener<BLEDevice>,SensoroConnectionCallback, SensoroWriteCallback {
    private Handler mHandler;
    private Activity mActivity;
    private boolean bleHasOpen;
    private DeployAnalyzerModel deployAnalyzerModel;
    private String mMacAddress;
    private SensoroDeviceConnection mConnection;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(this);
        BleObserver.getInstance().registerBleObserver(this);
        deployAnalyzerModel = (DeployAnalyzerModel) mActivity.getIntent().getSerializableExtra(Constants.EXTRA_DEPLOY_ANALYZER_MODEL);
    }

    @Override
    public void onDestroy() {
        BleObserver.getInstance().unregisterBleObserver(this);
        SensoroCityApplication.getInstance().bleDeviceManager.stopService();
        if (mConnection != null) {
            mConnection.disconnect();
        }
    }


    public void doConfiguration() {
        if (mMacAddress == null) {
            getView().toastShort(mActivity.getString(R.string.device_not_near));
            return;
        }

        mConnection = new SensoroDeviceConnection(mActivity, mMacAddress);
        try {
            mConnection.connect(deployAnalyzerModel.blePassword, DeployMonitorConfigurationPresenter.this);
//            getView().updateProgressDialogMessage(mActivity.getString(R.string.connecting));
//            stopScanService();
        } catch (Exception e) {
            e.printStackTrace();
            getView().dismissProgressDialog();
            getView().toastShort(mActivity.getString(R.string.ble_connect_failed));
        }
    }

    @Override
    public void run() {
        try {
            bleHasOpen = SensoroCityApplication.getInstance().bleDeviceManager.startService();
        } catch (Exception e) {
            e.printStackTrace();
            //TODO 提示
            getView().toastShort(mActivity.getString(R.string.check_ble_status));
        }
        if (!bleHasOpen) {
            bleHasOpen = SensoroCityApplication.getInstance().bleDeviceManager.enEnableBle();
            if (!bleHasOpen) {
                getView().toastShort(mActivity.getString(R.string.check_ble_status));
            }
        }
        mHandler.postDelayed(this, 3000);
    }

    @Override
    public void onNewDevice(BLEDevice bleDevice) {
        LogUtils.loge(this,bleDevice.getSn()+" "+deployAnalyzerModel.sn.equals(bleDevice.getSn()));
        if(deployAnalyzerModel.sn.equals(bleDevice.getSn())){
            mMacAddress = bleDevice.getMacAddress();
        }

    }

    @Override
    public void onGoneDevice(BLEDevice bleDevice) {
        if(deployAnalyzerModel.sn.equals(bleDevice.getSn())){
            mMacAddress = null;
        }

    }

    @Override
    public void onUpdateDevices(ArrayList<BLEDevice> deviceList) {

    }

    @Override
    public void onConnectedSuccess(BLEDevice bleDevice, int cmd) {

    }

    @Override
    public void onConnectedFailure(int errorCode) {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onWriteSuccess(Object o, int cmd) {

    }

    @Override
    public void onWriteFailure(int errorCode, int cmd) {

    }
}

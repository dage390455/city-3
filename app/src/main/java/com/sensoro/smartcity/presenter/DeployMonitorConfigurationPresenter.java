package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Entity;
import android.os.Handler;
import android.os.Looper;

import com.sensoro.libbleserver.ble.BLEDevice;
import com.sensoro.libbleserver.ble.SensoroConnectionCallback;
import com.sensoro.libbleserver.ble.SensoroDevice;
import com.sensoro.libbleserver.ble.SensoroDeviceConnection;
import com.sensoro.libbleserver.ble.SensoroSensorTest;
import com.sensoro.libbleserver.ble.SensoroWriteCallback;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployMonitorConfigurationView;
import com.sensoro.smartcity.model.DeployAnalyzerModel;
import com.sensoro.smartcity.util.BleObserver;
import com.sensoro.smartcity.util.LogUtils;

import java.util.ArrayList;
import java.util.Random;

public class DeployMonitorConfigurationPresenter extends BasePresenter<IDeployMonitorConfigurationView>
        implements Runnable, BLEDeviceListener<BLEDevice>, SensoroConnectionCallback, SensoroWriteCallback {
    private Handler mHandler;
    private Activity mActivity;
    private boolean bleHasOpen;
    private DeployAnalyzerModel deployAnalyzerModel;
    private String mMacAddress;
    private SensoroDeviceConnection mConnection;
    private SensoroDevice sensoroDevice;
    private SensoroSensorTest sensoroSensor;
    private Integer mEnterValue;

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
        try {
            Integer value = Integer.valueOf(getView().getEditTextValue());
            if (value < 50 || value > 560) {
                getView().toastShort(mActivity.getString(R.string.monitor_point_operation_error_value_range));
                return;
            }
            mEnterValue = value;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            getView().toastShort(mActivity.getString(R.string.enter_the_correct_number_format));
            return;
        }
        if (mMacAddress == null) {
            getView().toastShort(mActivity.getString(R.string.device_not_near));
            return;
        }

        mConnection = new SensoroDeviceConnection(mActivity, mMacAddress);
        try {
            mConnection.connect(deployAnalyzerModel.blePassword, DeployMonitorConfigurationPresenter.this);
            getView().showBleConfigurationDialog();
        } catch (Exception e) {
            e.printStackTrace();
            getView().dismissBleConfigurationDialog();
            getView().updateBtnRetryStatus();
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
        LogUtils.loge(this, bleDevice.getSn() + " " + deployAnalyzerModel.sn.equals(bleDevice.getSn()));
        if (deployAnalyzerModel.sn.equals(bleDevice.getSn())) {
            mMacAddress = bleDevice.getMacAddress();
        }

    }

    @Override
    public void onGoneDevice(BLEDevice bleDevice) {
        if (deployAnalyzerModel.sn.equals(bleDevice.getSn())) {
            mMacAddress = null;
        }

    }

    @Override
    public void onUpdateDevices(ArrayList<BLEDevice> deviceList) {

    }

    @Override
    public void onConnectedSuccess(BLEDevice bleDevice, int cmd) {
        getView().updateBleConfigurationDialogText(mActivity.getString(R.string.now_configuration));
        sensoroDevice = (SensoroDevice) bleDevice;
        sensoroSensor = sensoroDevice.getSensoroSensorTest();
        sensoroDevice.setHasMaxEirp(((SensoroDevice) bleDevice).hasMaxEirp());
        if (((SensoroDevice) bleDevice).hasMaxEirp()) {
            sensoroDevice.setMaxEirp(((SensoroDevice) bleDevice).getMaxEirp());
        }
        configAcrelFires();
    }

    private void configAcrelFires() {
        //在开始配置的时候，已经校验过，mEnterValue的值是50 到560
        int dev = 0;
        if (mEnterValue <= 250) {
            dev = 250;
        }else{
            dev = 400;
        }

        sensoroSensor.acrelFires.leakageTh = 1000;
        sensoroSensor.acrelFires.t1Th = 80;
        sensoroSensor.acrelFires.t2Th = 80;
        sensoroSensor.acrelFires.t3Th = 80;
        sensoroSensor.acrelFires.t4Th = 80;
        sensoroSensor.acrelFires.valHighSet = 1200;
        sensoroSensor.acrelFires.currHighSet = 800;
        sensoroSensor.acrelFires.currHighSet = 1000 * mEnterValue / dev;
        Random random = new Random();
        sensoroSensor.acrelFires.passwd =random.nextInt(9999)+1;
        sensoroSensor.acrelFires.currHighType = 1;
        sensoroSensor.acrelFires.valLowType = 0;
        sensoroSensor.acrelFires.valHighType = 1;
        sensoroSensor.acrelFires.chEnable = 0x1F;
        sensoroSensor.acrelFires.connectSw = 0;
//        sensoroSensor.acrelFires.
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

package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sensoro.libbleserver.ble.BLEDevice;
import com.sensoro.libbleserver.ble.SensoroConnectionCallback;
import com.sensoro.libbleserver.ble.SensoroDevice;
import com.sensoro.libbleserver.ble.SensoroDeviceConnection;
import com.sensoro.libbleserver.ble.SensoroSensor;
import com.sensoro.libbleserver.ble.SensoroWriteCallback;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceListener;
import com.sensoro.libbleserver.ble.scanner.SensoroUUID;
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
    private SensoroSensor sensoroSensor;
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
        mHandler.removeCallbacksAndMessages(null);
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
            getView().showBleConfigurationDialog(mActivity.getString(R.string.connecting));
            mConnection.connect(deployAnalyzerModel.blePassword, DeployMonitorConfigurationPresenter.this);
        } catch (Exception e) {
            e.printStackTrace();
            getView().dismissBleConfigurationDialog();
            updateBtnRetryStatus(mActivity.getString(R.string.ble_connect_failed));
        }
    }

    private void updateBtnRetryStatus(String message) {
        getView().updateBtnRetryStatus();
        getView().toastShort(message);
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
        LogUtils.loge("deployConfig", bleDevice.getSn() + " " + deployAnalyzerModel.sn.equals(bleDevice.getSn()));
        if (deployAnalyzerModel.sn.equals(bleDevice.getSn())) {
            mMacAddress = bleDevice.getMacAddress();
            getView().setTV("找到了");
        }

    }

    @Override
    public void onGoneDevice(BLEDevice bleDevice) {
        if (deployAnalyzerModel.sn.equals(bleDevice.getSn())) {
            mMacAddress = null;
            getView().setTV("丢失了");
        }

    }

    @Override
    public void onUpdateDevices(ArrayList<BLEDevice> deviceList) {

    }

    @Override
    public void onConnectedSuccess(BLEDevice bleDevice, int cmd) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getView().updateBleConfigurationDialogText(mActivity.getString(R.string.now_configuration));
            }
        });
        sensoroDevice = (SensoroDevice) bleDevice;
        sensoroSensor = sensoroDevice.getSensoroSensorTest();
//        sensoroDevice.setHasMaxEirp(((SensoroDevice) bleDevice).hasMaxEirp());
//        if (((SensoroDevice) bleDevice).hasMaxEirp()) {
//            sensoroDevice.setMaxEirp(((SensoroDevice) bleDevice).getMaxEirp());
//        }
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

        sensoroSensor.acrelFires.leakageTh = 1000;//漏电
        sensoroSensor.acrelFires.t1Th = 80;//A项线温度
        sensoroSensor.acrelFires.t2Th = 80;//B项线温度
        sensoroSensor.acrelFires.t3Th = 80;//C项线温度
        sensoroSensor.acrelFires.t4Th = 80;//箱体温度
        sensoroSensor.acrelFires.valHighSet = 1200;
        sensoroSensor.acrelFires.currHighSet = 800;
        sensoroSensor.acrelFires.currHighSet = 1000 * mEnterValue / dev;
        sensoroSensor.acrelFires.passwd =new Random().nextInt(9999)+1;// 1-9999 4位随机数
        LogUtils.loge("deployConfig", "密码是："+sensoroSensor.acrelFires.passwd);
        sensoroSensor.acrelFires.currHighType = 1;//打开保护，不关联脱扣
        sensoroSensor.acrelFires.valLowType = 0;//关闭保护，不关联脱扣
        sensoroSensor.acrelFires.valHighType = 1;//打开保护，不关联脱扣
        sensoroSensor.acrelFires.chEnable = 0x1F;//打开温度，打开漏电保护
        sensoroSensor.acrelFires.connectSw = 0;//关联脱扣器全部关闭
        sensoroSensor.acrelFires.ict = 2000;//漏电互感器变比 2000
        sensoroSensor.acrelFires.ct = dev / 5;
        byte[] bytes = new byte[3];
        bytes[0] = 0;
        bytes[1] = 1;
        bytes[2] = 0;
        sensoroSensor.acrelFires.cmd = SensoroUUID.bitsToInt(bytes);//自检命令

        try {
            mConnection.writeData05Configuration(sensoroDevice,this);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            updateBtnRetryStatus(mActivity.getString(R.string.ble_config_failed));
        }
    }

    @Override
    public void onConnectedFailure(int errorCode) {
//        mConnection.disconnect();
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getView().dismissBleConfigurationDialog();
                updateBtnRetryStatus(mActivity.getString(R.string.ble_connect_failed));
            }
        });


    }

    @Override
    public void onDisconnected() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getView().dismissBleConfigurationDialog();
                getView().toastShort(mActivity.getString(R.string.ble_device_disconnected));
            }
        });

    }

    @Override
    public void onWriteSuccess(Object o, int cmd) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getView().updateBleConfigurationDialogText(mActivity.getString(R.string.ble_config_success));
                getView().updateBleConfigurationDialogSuccessImv();
//                mConnection.writeAcrelCmd();
//                mConnection.disconnect();
            }
        });
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getView().dismissBleConfigurationDialog();
            }
        },500);
    }

    @Override
    public void onWriteFailure(int errorCode, int cmd) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getView().dismissBleConfigurationDialog();
                updateBtnRetryStatus(mActivity.getString(R.string.ble_config_failed));
            }
        });
//        mConnection.disconnect();
    }
}

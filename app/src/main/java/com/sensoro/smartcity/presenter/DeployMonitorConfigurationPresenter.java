package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.sensoro.libbleserver.ble.BLEDevice;
import com.sensoro.libbleserver.ble.SensoroConnectionCallback;
import com.sensoro.libbleserver.ble.SensoroDevice;
import com.sensoro.libbleserver.ble.SensoroDeviceConnection;
import com.sensoro.libbleserver.ble.SensoroSensor;
import com.sensoro.libbleserver.ble.SensoroWriteCallback;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployMonitorConfigurationView;
import com.sensoro.smartcity.model.DeployAnalyzerModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.bean.DeployContralSettingData;
import com.sensoro.smartcity.util.BleObserver;
import com.sensoro.smartcity.util.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
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
    private final HashSet<String> bleList = new HashSet<>();

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        deployAnalyzerModel = (DeployAnalyzerModel) mActivity.getIntent().getSerializableExtra(Constants.EXTRA_DEPLOY_ANALYZER_MODEL);
        mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(this);
        BleObserver.getInstance().registerBleObserver(this);
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        BleObserver.getInstance().unregisterBleObserver(this);
        if (mConnection != null) {
            mConnection.disconnect();
        }
        SensoroCityApplication.getInstance().bleDeviceManager.stopService();
    }


    public void doConfiguration(String valueStr) {
        if (!bleList.contains(deployAnalyzerModel.sn)) {
            getView().toastShort(mActivity.getString(R.string.device_not_near));
            return;
        }
        if (Constants.DEVICE_CONTROL_DEVICE_TYPES.get(1).equals(deployAnalyzerModel.deviceType)) {
            //安科瑞三相电连接
            try {
                Integer value = Integer.parseInt(valueStr);
                if (value < 50 || value > 560) {
                    getView().toastShort(mActivity.getString(R.string.monitor_point_operation_error_value_range));
                    return;
                }
                mEnterValue = value;
                mConnection = new SensoroDeviceConnection(mActivity, mMacAddress);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                getView().toastShort(mActivity.getString(R.string.enter_the_correct_number_format));
                return;
            }
            try {
                getView().showBleConfigurationDialog(mActivity.getString(R.string.connecting));
                mConnection.connect(deployAnalyzerModel.blePassword, DeployMonitorConfigurationPresenter.this);
            } catch (Exception e) {
                e.printStackTrace();
                getView().dismissBleConfigurationDialog();
                getView().toastShort(mActivity.getString(R.string.ble_connect_failed));
            }
        } else {
            //TODO fhsj 电器设备
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
        boolean isNearby = bleList.contains(deployAnalyzerModel.sn);
        getView().setTvNearVisible(isNearby);
        getView().updateBtnStatus(getView().hasEditTextContent() && isNearby);
        mHandler.postDelayed(this, 2000);
    }

    @Override
    public void onNewDevice(BLEDevice bleDevice) {
        String sn = bleDevice.getSn();
        LogUtils.loge("deployConfig", sn + " " + deployAnalyzerModel.sn.equals(sn));
        bleList.add(sn);
        if (TextUtils.isEmpty(mMacAddress)) {
            if (deployAnalyzerModel.sn.equals(sn)) {
                mMacAddress = bleDevice.getMacAddress();
            }

        }

    }

    @Override
    public void onGoneDevice(BLEDevice bleDevice) {
        String sn = bleDevice.getSn();
        bleList.remove(sn);
    }

    @Override
    public void onUpdateDevices(ArrayList<BLEDevice> deviceList) {
        for (BLEDevice device : deviceList) {
            String sn = device.getSn();
            bleList.add(sn);
            if (TextUtils.isEmpty(mMacAddress)) {
                if (deployAnalyzerModel.sn.equals(sn)) {
                    mMacAddress = device.getMacAddress();
                }
            }
        }
    }

    @Override
    public void onConnectedSuccess(BLEDevice bleDevice, int cmd) {
        if (getView() != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getView() != null) {
                        getView().updateBleConfigurationDialogText(mActivity.getString(R.string.now_configuration));
                    }
                }
            });
            //安科瑞电气火灾初始化配置
            sensoroDevice = (SensoroDevice) bleDevice;
            sensoroSensor = sensoroDevice.getSensoroSensorTest();
            configAcrelFires();
        }

    }

    private void configCompleted() {
        EventData eventData = new EventData();
        eventData.code = Constants.EVENT_DATA_DEPLOY_INIT_CONFIG_CODE;
        DeployContralSettingData deployContralSettingData = new DeployContralSettingData();
        deployContralSettingData.setInitValue(mEnterValue);
        eventData.data = deployContralSettingData;
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }

    private void configAcrelFires() {
        //在开始配置的时候，已经校验过，mEnterValue的值是50 到560
        int dev;
        if (mEnterValue <= 250) {
            dev = 250;
        } else {
            dev = 400;
        }
        sensoroSensor.acrelFires.leakageTh = 1000;//漏电
        sensoroSensor.acrelFires.t1Th = 80;//A项线温度
        sensoroSensor.acrelFires.t2Th = 80;//B项线温度
        sensoroSensor.acrelFires.t3Th = 80;//C项线温度
        sensoroSensor.acrelFires.t4Th = 80;//箱体温度
        sensoroSensor.acrelFires.valHighSet = 1200;
        sensoroSensor.acrelFires.valLowSet = 800;
        sensoroSensor.acrelFires.currHighSet = 1000 * mEnterValue / dev;
        sensoroSensor.acrelFires.passwd = new Random().nextInt(9999) + 1;// 1-9999 4位随机数
        sensoroSensor.acrelFires.currHighType = 1;//打开保护，不关联脱扣
        sensoroSensor.acrelFires.valLowType = 0;//关闭保护，不关联脱扣
        sensoroSensor.acrelFires.valHighType = 1;//打开保护，不关联脱扣
        sensoroSensor.acrelFires.chEnable = 0x1F;//打开温度，打开漏电保护
        sensoroSensor.acrelFires.connectSw = 0;//关联脱扣器全部关闭
        sensoroSensor.acrelFires.ict = 2000;//漏电互感器变比 2000
        sensoroSensor.acrelFires.ct = dev / 5;
        sensoroSensor.acrelFires.cmd = 2;
        mConnection.writeData05Configuration(sensoroDevice, this);
    }

    @Override
    public void onConnectedFailure(int errorCode) {
        if (getView() != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getView() != null) {
                        getView().dismissBleConfigurationDialog();
                        getView().toastShort(mActivity.getString(R.string.ble_connect_failed));
                    }

                }
            });
        }


    }

    @Override
    public void onDisconnected() {
        if (getView() != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getView() != null) {
                        getView().dismissBleConfigurationDialog();
                        getView().toastShort(mActivity.getString(R.string.ble_device_disconnected));
                    }

                }
            });
        }
    }

    @Override
    public void onWriteSuccess(Object o, int cmd) {
        if (getView() != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getView() != null) {
                        getView().updateBleConfigurationDialogText(mActivity.getString(R.string.ble_config_success));
                        getView().updateBleConfigurationDialogSuccessImv();
                    }

                }
            });
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getView() != null) {
                        getView().dismissBleConfigurationDialog();
                        configCompleted();
                    }

                }
            }, 1000);
        }

    }

    @Override
    public void onWriteFailure(int errorCode, int cmd) {
        if (getView() != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getView() != null) {
                        getView().dismissBleConfigurationDialog();
                        getView().toastShort(mActivity.getString(R.string.ble_config_failed));
                    }

                }
            });
        }

    }
}

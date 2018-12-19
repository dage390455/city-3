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
import com.sensoro.smartcity.analyzer.DeployConfigurationAnalyzer;
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
    private DeployConfigurationAnalyzer mConfigurationAnalyzer;
    private int[] mMinMaxValue;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        mConfigurationAnalyzer = new DeployConfigurationAnalyzer();
        deployAnalyzerModel = (DeployAnalyzerModel) mActivity.getIntent().getSerializableExtra(Constants.EXTRA_DEPLOY_ANALYZER_MODEL);
        mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(this);
        BleObserver.getInstance().registerBleObserver(this);
        init();
    }

    private void init() {
        mMinMaxValue = mConfigurationAnalyzer.analyzeDeviceType(deployAnalyzerModel.deviceType);
        if (mMinMaxValue == null) {
            getView().toastShort(mActivity.getString(R.string.deploy_configuration_analyze_failed));
        }else{
            getView().setTvEnterValueRange(mMinMaxValue[0],mMinMaxValue[1]);
        }
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
            getView().toastShort(mActivity.getString(R.string.deploy_configuration_not_discover_device));
            return;
        }
        checkAndConnect(valueStr);
    }

    private void checkAndConnect(String valueStr){
        if(mMinMaxValue== null){
            getView().toastShort(mActivity.getString(R.string.deploy_configuration_analyze_failed));
            return;
        }
        try {
            Integer value = Integer.parseInt(valueStr);
            if (value < mMinMaxValue[0] || value > mMinMaxValue[1]) {
                getView().toastShort(mActivity.getString(R.string.monitor_point_operation_error_value_range));
                return;
            }
            mEnterValue = value;
            mConnection = new SensoroDeviceConnection(mActivity, mMacAddress);
        } catch (Exception e) {
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
            SensoroDevice sensoroDevice= mConfigurationAnalyzer.configurationData((SensoroDevice) bleDevice,mEnterValue);
            if (sensoroDevice != null) {
                mConnection.writeData05Configuration(sensoroDevice,this);
            }else{
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getView()!=null) {
                            getView().toastShort(mActivity.getString(R.string.deploy_configuration_analyze_data_failed));
                        }
                        mConnection.disconnect();
                    }
                });

            }

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
                        mConnection.disconnect();
                    }

                }
            });
        }

    }
}

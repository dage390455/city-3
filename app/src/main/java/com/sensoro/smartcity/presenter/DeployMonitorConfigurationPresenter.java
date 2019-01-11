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
import com.sensoro.smartcity.server.bean.DeployControlSettingData;
import com.sensoro.smartcity.util.BleObserver;
import com.sensoro.smartcity.util.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

public class DeployMonitorConfigurationPresenter extends BasePresenter<IDeployMonitorConfigurationView>
        implements Runnable, BLEDeviceListener<BLEDevice>, SensoroConnectionCallback, SensoroWriteCallback {
    private Handler mHandler;
    private Activity mActivity;
    private boolean bleHasOpen;
    private DeployAnalyzerModel deployAnalyzerModel;
    private String mMacAddress;
    private SensoroDeviceConnection mConnection;
    private Integer mEnterValue;
    private final HashSet<String> bleList = new HashSet<>();
    private int[] mMinMaxValue;
    private Double diameterValue;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        deployAnalyzerModel = (DeployAnalyzerModel) mActivity.getIntent().getSerializableExtra(Constants.EXTRA_DEPLOY_ANALYZER_MODEL);
        mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(this);
        getView().setLlAcDeployConfigurationDiameterVisible(needDiameter());
        BleObserver.getInstance().registerBleObserver(this);
        init();
    }

    private void init() {
        mMinMaxValue = DeployConfigurationAnalyzer.analyzeDeviceType(deployAnalyzerModel.deviceType);
        if (mMinMaxValue == null) {
            getView().toastShort(mActivity.getString(R.string.deploy_configuration_analyze_failed));
        } else {
            getView().setTvEnterValueRange(mMinMaxValue[0], mMinMaxValue[1]);
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


    public void doConfiguration(String valueStr, String diameter) {
        if (!bleList.contains(deployAnalyzerModel.sn)) {
            getView().toastShort(mActivity.getString(R.string.deploy_configuration_not_discover_device));
            return;
        }
        checkAndConnect(valueStr, diameter);
    }

    private void checkAndConnect(String valueStr, String diameter) {
        if (mMinMaxValue == null) {
            getView().toastShort(mActivity.getString(R.string.deploy_configuration_analyze_failed));
            return;
        }
        try {
            mEnterValue = Integer.parseInt(valueStr);
            if (mEnterValue < mMinMaxValue[0] || mEnterValue > mMinMaxValue[1]) {
                getView().toastShort(mActivity.getString(R.string.electric_current) + mActivity.getString(R.string.monitor_point_operation_error_value_range) + mMinMaxValue[0] + "-" + mMinMaxValue[1]);
                return;
            }
            mConnection = new SensoroDeviceConnection(mActivity, mMacAddress);
        } catch (Exception e) {
            e.printStackTrace();
            getView().toastShort(mActivity.getString(R.string.electric_current) + mActivity.getString(R.string.enter_the_correct_number_format));
            return;
        }
        if (needDiameter()) {
            if (TextUtils.isEmpty(diameter)) {
                getView().toastShort(mActivity.getString(R.string.enter_wire_diameter_tip));
                return;
            }
            try {
                diameterValue = Double.parseDouble(diameter);
                if (diameterValue < 0 || diameterValue >= 200) {
                    getView().toastShort(mActivity.getString(R.string.diameter) + String.format(Locale.CHINESE, "%s%d-%d", mActivity.getString(R.string.monitor_point_operation_error_value_range), 0, 200));
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                getView().toastShort(mActivity.getString(R.string.diameter) + mActivity.getString(R.string.enter_the_correct_number_format));
                return;
            }
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

    public boolean needDiameter() {
        return "mantun_fires".equals(deployAnalyzerModel.deviceType);
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
        try {
            LogUtils.loge("deployConfig", sn + " " + deployAnalyzerModel.sn.equals(sn));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
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
                    if (isAttachedView()) {
                        getView().updateBleConfigurationDialogText(mActivity.getString(R.string.now_configuration));
                    }
                }
            });
            SensoroDevice sensoroDevice = DeployConfigurationAnalyzer.configurationData(deployAnalyzerModel.deviceType, (SensoroDevice) bleDevice, mEnterValue);
            if (sensoroDevice != null) {
                mConnection.writeData05Configuration(sensoroDevice, this);
            } else {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isAttachedView()) {
                            getView().toastShort(mActivity.getString(R.string.deploy_configuration_analyze_data_failed));
                            mConnection.disconnect();
                        }

                    }
                });

            }

        }

    }

    private void configCompleted() {
        EventData eventData = new EventData();
        eventData.code = Constants.EVENT_DATA_DEPLOY_INIT_CONFIG_CODE;
        DeployControlSettingData deployControlSettingData = new DeployControlSettingData();
        deployControlSettingData.setInitValue(mEnterValue);
        deployControlSettingData.setDiameterValue(diameterValue);
        eventData.data = deployControlSettingData;
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }


    @Override
    public void onConnectedFailure(int errorCode) {
        if (getView() != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isAttachedView()) {
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
                    if (isAttachedView()) {
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
                    if (isAttachedView()) {
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
                    if (isAttachedView()) {
                        getView().dismissBleConfigurationDialog();
                        getView().toastShort(mActivity.getString(R.string.ble_config_failed));
                        mConnection.disconnect();
                    }

                }
            });
        }

    }
}

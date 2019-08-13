package com.sensoro.smartcity.callback;


import com.sensoro.libbleserver.ble.entity.BLEDevice;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceListener;
import com.sensoro.common.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashSet;

public class BleObserver implements BLEDeviceListener<BLEDevice> {
    private final HashSet<BLEDeviceListener<BLEDevice>> hashSet = new HashSet<>();

    private BleObserver() {
    }

    public static BleObserver getInstance() {
        return BleObserverHolder.instance;
    }

    private static class BleObserverHolder {
        private static final BleObserver instance = new BleObserver();
    }

    public void registerBleObserver(BLEDeviceListener<BLEDevice> listener) {
        hashSet.add(listener);
    }

    public void unregisterBleObserver(BLEDeviceListener<BLEDevice> listener) {
        hashSet.remove(listener);
    }

    public void clearObserver() {
        hashSet.clear();
    }

    public boolean isRegisterBleObserver(BLEDeviceListener<BLEDevice> listener) {
        return hashSet.contains(listener);
    }

    @Override
    public void onNewDevice(BLEDevice bleDevice) {
        if (bleDevice != null) {
            try {
                LogUtils.loge("BleObserver-->> onNewDevice = " + bleDevice.getSn());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            if (!hashSet.isEmpty()) {
                for (BLEDeviceListener<BLEDevice> bleDeviceListener : hashSet) {
                    bleDeviceListener.onNewDevice(bleDevice);
                }
            }
        }

    }

    @Override
    public void onGoneDevice(BLEDevice bleDevice) {
        if (bleDevice != null) {
            try {
                LogUtils.loge("BleObserver-->> onGoneDevice = " + bleDevice.getSn());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            if (!hashSet.isEmpty()) {
                for (BLEDeviceListener<BLEDevice> bleDeviceListener : hashSet) {
                    bleDeviceListener.onGoneDevice(bleDevice);
                }
            }
        }

    }

    @Override
    public void onUpdateDevices(ArrayList<BLEDevice> deviceList) {
        if (deviceList != null && deviceList.size() > 0) {
            try {
                LogUtils.loge("BleObserver-->> onUpdateDevices.size() = " + deviceList.size());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            if (!hashSet.isEmpty()) {
                for (BLEDeviceListener<BLEDevice> bleDeviceListener : hashSet) {
                    bleDeviceListener.onUpdateDevices(deviceList);
                }
            }
        }

    }
}

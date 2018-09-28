package com.sensoro.smartcity.util;

import com.sensoro.libbleserver.ble.BLEDevice;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceListener;

import java.util.ArrayList;
import java.util.HashSet;

public class BleObserver implements BLEDeviceListener<BLEDevice> {
    private static BleObserver instance;
    private final HashSet<BLEDeviceListener<BLEDevice>> hashSet = new HashSet<>();

    private BleObserver() {
    }

    public static BleObserver getInstance() {
        if (instance == null) {
            synchronized (BleObserver.class) {
                if (instance == null) {
                    instance = new BleObserver();
                }
            }
        }
        return instance;
    }

    public void registerBleObserver(BLEDeviceListener<BLEDevice> listener) {
        hashSet.add(listener);
    }

    public void unregisterBleObserver(BLEDeviceListener<BLEDevice> listener) {
        if (hashSet.contains(listener)) {
            hashSet.remove(listener);
        }
    }

    public void clearObserver() {
        hashSet.clear();
    }

    @Override
    public void onNewDevice(BLEDevice bleDevice) {
        if (bleDevice != null) {
            LogUtils.loge("BleObserver-->> onNewDevice = " + bleDevice.getSn());
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
            LogUtils.loge("BleObserver-->> onGoneDevice = " + bleDevice.getSn());
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
            LogUtils.loge("BleObserver-->> onUpdateDevices.size() = " + deviceList.size());
            if (!hashSet.isEmpty()) {
                for (BLEDeviceListener<BLEDevice> bleDeviceListener : hashSet) {
                    bleDeviceListener.onUpdateDevices(deviceList);
                }
            }
        }

    }
}

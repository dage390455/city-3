package com.sensoro.smartcity.util;


import com.sensoro.libbleserver.ble.entity.BLEDevice;
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
        hashSet.remove(listener);
    }

    public void clearObserver() {
        hashSet.clear();
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

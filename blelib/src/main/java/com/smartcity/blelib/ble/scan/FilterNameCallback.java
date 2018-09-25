package com.smartcity.blelib.ble.scan;

import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;

import com.smartcity.blelib.ble.data.ScanResult;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * created by ddong1031 at 2017/11/30 0030
 */
public abstract class FilterNameCallback extends PeriodScanCallback {

    private String[] mFilterNames = null;
    private AtomicBoolean hasFound = new AtomicBoolean(false);


    public FilterNameCallback(long timeoutMillis, String[] filterNames) {
        super(timeoutMillis);
        this.mFilterNames = filterNames;
    }

    @Override
    public void onScanTimeout() {
        onDeviceNotFound();
    }

    @Override
    public void onScanCancel() {
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (device == null)
            return;

        if (TextUtils.isEmpty(device.getName())) {
            return;
        }

        if (!hasFound.get()) {

            ScanResult scanResult = new ScanResult(device, rssi, scanRecord,
                    System.currentTimeMillis());

            if (mFilterNames != null && mFilterNames.length > 1) {
                for (String mFilterName : mFilterNames) {
                    mFilterName = mFilterName.toUpperCase(Locale.ENGLISH);
                    if (device.getName().contains(mFilterName)) {
                        hasFound.set(true);
                        bleBluetooth.stopScan(FilterNameCallback.this);
                        onDeviceFound(scanResult);
                        return;
                    }
                }
            }

        }

    }

    @Override
    public void notifyScanCancel() {
        super.notifyScanCancel();
        hasFound.set(false);
    }

    public abstract void onDeviceFound(ScanResult sanResult);

    public abstract void onDeviceNotFound();
}

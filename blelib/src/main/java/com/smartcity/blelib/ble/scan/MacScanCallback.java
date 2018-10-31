package com.smartcity.blelib.ble.scan;

import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.smartcity.blelib.ble.data.ScanResult;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 　　　　　　　 ┏┓       ┏┓+ +
 * 　　　　　　　┏┛┻━━━━━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　 ┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 █████━█████  ┃+
 * 　　　　　　　┃　　　　　　 ┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　　　　　　　┃　　　　　　 ┃ + +
 * 　　　　　　　┗━━┓　　　 ┏━┛
 * ┃　　  ┃
 * 　　　　　　　　　┃　　  ┃ + + + +
 * 　　　　　　　　　┃　　　┃　Code is far away from bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ + 　　　　         神兽保佑,代码无bug
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　 ┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━━━┳┓┏┛ + + + +
 * 　　　　　　　　　 ┃┫┫　 ┃┫┫
 * 　　　　　　　　　 ┗┻┛　 ┗┻┛+ + + +
 * <p>
 * <p>
 * Created by Done on 2017/8/25.
 */

public abstract class MacScanCallback extends PeriodScanCallback {

    private String mMac;
    private AtomicBoolean hasFound = new AtomicBoolean(false);

    public MacScanCallback(String mac, long timeoutMillis) {
        super(timeoutMillis);
        this.mMac = mac;
        if (TextUtils.isEmpty(mac)) {
            onDeviceNotFound();
        }
    }

    @Override
    public void notifyScanCancel() {
        super.notifyScanCancel();
        hasFound.set(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (device == null)
            return;
        if (TextUtils.isEmpty(device.getAddress())) {
            return;
        }

        if (!hasFound.get()) {

            ScanResult scanResult = new ScanResult(device, rssi, scanRecord,
                    System.currentTimeMillis());

            if (mMac.equalsIgnoreCase(device.getAddress())) {
                hasFound.set(true);
                bleBluetooth.stopScan(MacScanCallback.this);
                onDeviceFound(scanResult);
            }
        }

    }

    @Override
    public void onScanTimeout() {
        onDeviceNotFound();
    }

    @Override
    public void onScanCancel() {

    }

    public abstract void onDeviceFound(ScanResult scanResult);

    public abstract void onDeviceNotFound();
}

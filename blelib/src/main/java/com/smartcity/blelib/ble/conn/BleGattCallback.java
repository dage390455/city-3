package com.smartcity.blelib.ble.conn;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.smartcity.blelib.ble.data.ScanResult;
import com.smartcity.blelib.ble.exception.BleException;

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

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class BleGattCallback extends BluetoothGattCallback {

    public void onFoundDevice(ScanResult scanResult) {
    }

    public void onConnecting(BluetoothGatt gatt, int status) {
    }

    /**
     * 1)设备不存在
     * @param exception
     */
    public abstract void onConnectError(BleException exception);

    public abstract void onConnectSuccess(BluetoothGatt gatt, int status);

    /**
     * 1)设备关机
     * 2)设备距离太远
     * 3)主机蓝牙关闭或者关机
     *
     * @param gatt
     * @param status
     * @param exception
     */
    public abstract void onDisConnected(BluetoothGatt gatt, int status, BleException exception);

    /**
     * 区分设备（新、旧）
     * @param version
     */
    public abstract void onDeviceVersion(String version);
}

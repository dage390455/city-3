package com.smartcity.blelib.ble.exception;

import android.bluetooth.BluetoothGatt;

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

public class ConnectException extends BleException {

    private BluetoothGatt bluetoothGatt;
    private int gattStatus;

    public ConnectException(BluetoothGatt bluetoothGatt, int gattStatus) {
        super(ERROR_CODE_GATT, "Gatt Exception Occurred! ");
        this.bluetoothGatt = bluetoothGatt;
        this.gattStatus = gattStatus;
    }

    public int getGattStatus() {
        return gattStatus;
    }

    public ConnectException setGattStatus(int gattStatus) {
        this.gattStatus = gattStatus;
        return this;
    }

    public BluetoothGatt getBluetoothGatt() {
        return bluetoothGatt;
    }

    public ConnectException setBluetoothGatt(BluetoothGatt bluetoothGatt) {
        this.bluetoothGatt = bluetoothGatt;
        return this;
    }

    @Override
    public String toString() {
        return "ConnectException{" +
                "gattStatus=" + gattStatus +
                ", bluetoothGatt=" + bluetoothGatt +
                "} " + super.toString();
    }
}

package com.smartcity.blelib;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.smartcity.blelib.ble.ServiceAnalyzer;
import com.smartcity.blelib.ble.bluetooth.BleBluetooth;
import com.smartcity.blelib.ble.conn.BleCharacterCallback;
import com.smartcity.blelib.ble.conn.BleGattCallback;
import com.smartcity.blelib.ble.conn.BleRssiCallback;
import com.smartcity.blelib.ble.data.ScanResult;
import com.smartcity.blelib.ble.exception.BleException;
import com.smartcity.blelib.ble.exception.BlueToothNotEnableException;
import com.smartcity.blelib.ble.exception.DeviceNotSupportedException;
import com.smartcity.blelib.ble.exception.NotFoundDeviceException;
import com.smartcity.blelib.ble.exception.handler.DefaultBleExceptionHandler;
import com.smartcity.blelib.ble.scan.IOnRequestPermissionsResult;
import com.smartcity.blelib.ble.scan.ListScanCallback;
import com.smartcity.blelib.command.CommandSender;

import java.util.List;
import java.util.UUID;
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleManager implements IOnRequestPermissionsResult {
    private static final String TAG = "BleManager";

    private final int PERMISSION_REQUEST_COARSE_LOCATION = 202;
    private ListScanCallback tempListScanCallback;


    private final Context mContext;
    private BleBluetooth bleBluetooth;
    private final DefaultBleExceptionHandler bleExceptionHandler;
    private BluetoothGatt curGatt;
    private static BleManager instance;
    private CommandSender.ISendResultObserver commandSendResultObserver;        //回调蓝牙的数据给上层


    private BleManager(Context context) {
        this.mContext = context;

        if (isSupportBle()) {
            if (bleBluetooth == null) {
                bleBluetooth = new BleBluetooth(context);
                //开启蓝牙
                enableBluetooth();
            }
        }

        bleExceptionHandler = new DefaultBleExceptionHandler(context);
    }

    public static BleManager getInstance(Context context) {
        if (instance == null) {
            synchronized (BleManager.class) {
                if (instance == null) {
                    instance = new BleManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * handle Exception Information
     */
    private void handleException(BleException exception) {
        bleExceptionHandler.handleException(exception);
    }

    /**
     * 修改，作了一下权限适配。
     * scan device around
     */
    public boolean scanDevice(ListScanCallback callback, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                //TODO 权限适配 终止
                this.tempListScanCallback = callback;
                activity.requestPermissions(new String[]{Manifest.permission
                        .ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                return false;
            } else {
                return scanDeviceAfterGetPermission(callback);
            }
        } else {
            return scanDeviceAfterGetPermission(callback);
        }
    }



    private boolean scanDeviceAfterGetPermission(ListScanCallback callback) {
        if (!isBlueEnable()) {
            handleException(new BlueToothNotEnableException());
            return false;
        }
        cancelScan();
        return bleBluetooth.startLeScan(callback);
    }

    /**
     * 为了兼容接口做了一个兼容的方法。
     *
     * @param device
     * @param time_out
     * @param autoConnect
     * @param callback
     */
    public void connectDevice(BluetoothDevice device,
                              long time_out,
                              boolean autoConnect,
                              final BleGattCallback callback) {
        connectDevice(new ScanResult(device), time_out, autoConnect, callback);
    }

    /**
     * SDK Interface1 - 连接设备
     * connect a searched device
     *
     * @param scanResult  扫描结果
     * @param time_out    超时时间
     * @param autoConnect 是否自动重连
     * @param callback    回调接口
     */
    public void connectDevice(ScanResult scanResult,
                              long time_out,
                              boolean autoConnect,
                              final BleGattCallback callback) {

        //Android设备一定要是18及以上。
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            callback.onConnectError(new DeviceNotSupportedException());
            return;
        }

        connectDeviceInner(scanResult, time_out, autoConnect, new BleGattCallback() {
            @Override
            public void onConnectError(BleException exception) {
                callback.onConnectError(exception);
                setCurGatt(null);
            }

            @Override
            public void onConnectSuccess(BluetoothGatt gatt, int status) {
                callback.onConnectSuccess(gatt, status);
                setCurGatt(gatt);
            }

            @Override
            public void onDisConnected(BluetoothGatt gatt, int status, BleException exception) {
                callback.onDisConnected(gatt, status, exception);
                setCurGatt(null);
            }

            @Override
            public void onDeviceVersion(String version) {
                callback.onDeviceVersion(version);
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                callback.onServicesDiscovered(gatt, status);
                setCurGatt(gatt);
            }
        });
    }


    /**
     * 构建一个中间的实现方法，以便在回调里notify，而不是给用户去操作。
     *
     * @param scanResult
     * @param time_out
     * @param autoConnect
     * @param callback
     */
    private void connectDeviceInner(ScanResult scanResult,
                                    long time_out,
                                    boolean autoConnect,
                                    final BleGattCallback callback) {
        connectDeviceOriginal(scanResult, time_out, autoConnect, new BleGattCallback() {
            @Override
            public void onConnectError(BleException exception) {
                callback.onConnectError(exception);
                ServiceAnalyzer.resetUUIDS();
            }

            @Override
            public void onConnectSuccess(BluetoothGatt gatt, int status) {
                callback.onConnectSuccess(gatt, status);
            }

            @Override
            public void onDisConnected(BluetoothGatt gatt, int status, BleException exception) {
                callback.onDisConnected(gatt, status, exception);
                ServiceAnalyzer.resetUUIDS();
            }

            @Override
            public void onDeviceVersion(String version) {
                callback.onDeviceVersion(version);
            }

            @Override
            public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
                callback.onServicesDiscovered(gatt, status);  //注意回调要在前面的判断之后执行.
            }
        });

    }


    /**
     * connect a searched device
     *
     * @param scanResult  扫描结果
     * @param time_out    超时时间
     * @param autoConnect 是否自动重连
     * @param callback    回调接口
     */
    private void connectDeviceOriginal(ScanResult scanResult,
                                       long time_out,
                                       boolean autoConnect,
                                       BleGattCallback callback) {
        if (!isBlueEnable()) {
            handleException(new BlueToothNotEnableException());
            return;
        }


        if (scanResult.getDevice() == null) {
            if (callback != null) {
                callback.onConnectError(new NotFoundDeviceException());
            }
        } else {
            if (callback != null) {
                callback.onFoundDevice(scanResult);
            }
            scanMacAndConnect(scanResult.getDevice().getAddress(), time_out, autoConnect, callback);
        }
    }

    /**
     * 断开当前的连接
     */
    public void disconnectDevice() {
        if (isConnected()) {
            disconnectBluetoothGatt();
        }else {
            bleBluetooth.refreshGattCache();
        }
    }


    /**
     * 获取当前已经连接的设备
     *
     * @return
     */
    public List<BluetoothDevice> getConnectedDevices() {
        if (null != bleBluetooth)
            return bleBluetooth.getConnectedDevices();
        else
            return null;
    }


    /**
     * scan a known name device, then connect
     *
     * @param deviceName
     * @param time_out
     * @param autoConnect
     * @param callback
     */
    private void scanNameAndConnect(String deviceName,
                                    long time_out,
                                    boolean autoConnect,
                                    BleGattCallback callback) {
        if (!isBlueEnable() && callback != null) {
            callback.onConnectError(new BlueToothNotEnableException());
        } else {
            bleBluetooth.scanNameAndConnect(deviceName, time_out, autoConnect, callback);
        }
    }

    /**
     * scan known names device, then connect
     *
     * @param deviceNames
     * @param time_out
     * @param autoConnect
     * @param callback
     */
    private void scanNamesAndConnect(String[] deviceNames,
                                     long time_out,
                                     boolean autoConnect,
                                     BleGattCallback callback) {
        if (!isBlueEnable() && callback != null) {
            callback.onConnectError(new BlueToothNotEnableException());
        } else {
            bleBluetooth.scanNameAndConnect(deviceNames, time_out, autoConnect, callback);
        }
    }

    /**
     * fuzzy search name
     *
     * @param fuzzyName
     * @param time_out
     * @param autoConnect
     * @param callback
     */
    private void scanfuzzyNameAndConnect(String fuzzyName,
                                         long time_out,
                                         boolean autoConnect,
                                         BleGattCallback callback) {
        if (!isBlueEnable() && callback != null) {
            callback.onConnectError(new BlueToothNotEnableException());
        } else {
            bleBluetooth.scanNameAndConnect(fuzzyName, time_out, autoConnect, true, callback);
        }
    }

    /**
     * fuzzy search name
     *
     * @param fuzzyNames
     * @param time_out
     * @param autoConnect
     * @param callback
     */
    private void scanfuzzyNamesAndConnect(String[] fuzzyNames,
                                          long time_out,
                                          boolean autoConnect,
                                          BleGattCallback callback) {
        if (!isBlueEnable() && callback != null) {
            callback.onConnectError(new BlueToothNotEnableException());
        } else {
            bleBluetooth.scanNameAndConnect(fuzzyNames, time_out, autoConnect, true, callback);
        }
    }

    /**
     * scan a known mca device, then connect
     *
     * @param deviceMac
     * @param time_out
     * @param autoConnect
     * @param callback
     */
    private void scanMacAndConnect(String deviceMac,
                                   long time_out,
                                   boolean autoConnect,
                                   BleGattCallback callback) {
        if (!isBlueEnable() && callback != null) {
            callback.onConnectError(new BlueToothNotEnableException());
        } else {
            bleBluetooth.scanMacAndConnect(deviceMac, time_out, autoConnect, callback);
        }
    }

    /**
     * cancel scan
     */
    private void cancelScan() {
        if (bleBluetooth != null) {
            bleBluetooth.cancelScan();
        }
    }

    /**
     * notify
     *
     * @param uuid_service
     * @param uuid_notify
     * @param callback
     * @return
     */
    private boolean notify(String uuid_service,
                           String uuid_notify,
                           BleCharacterCallback callback) {
        if (bleBluetooth != null) {
            return bleBluetooth.newBleConnector(commandSendResultObserver)
                    .withUUIDString(uuid_service, uuid_notify, null)
                    .enableCharacteristicNotify(callback, uuid_notify);
        } else {
            return false;
        }

    }

    /**
     * 根据uuid_service自动查询
     *
     * @param uuid_service
     * @param callback
     * @ret
     */
    public boolean notify(String uuid_service, BluetoothGatt gatt,
                          BleCharacterCallback callback) {
        //获取可通知、可写的UUID
        ServiceAnalyzer.get2UUID(gatt, UUID.fromString(uuid_service));
        if (bleBluetooth != null) {
            return bleBluetooth.newBleConnector(commandSendResultObserver)
                    .withUUIDString(uuid_service, ServiceAnalyzer.UUID_NOFITY.toString(), null)
                    .enableCharacteristicNotify(callback, ServiceAnalyzer.UUID_NOFITY.toString());
        } else {
            return false;
        }

    }


    /**
     * indicate
     *
     * @param uuid_service
     * @param callback
     * @return
     */
    public boolean indicate(String uuid_service, BluetoothGatt gatt,
                            BleCharacterCallback callback) {
        //获取可通知、可写的UUID
        ServiceAnalyzer.get2UUID(gatt, UUID.fromString(uuid_service));
        if (bleBluetooth != null) {
            return bleBluetooth.newBleConnector(commandSendResultObserver)
                    .withUUIDString(uuid_service, ServiceAnalyzer.UUID_INDICATE.toString(), null)
                    .enableCharacteristicIndicate(callback, ServiceAnalyzer.UUID_INDICATE
                            .toString());
        }
        return false;

    }

    /**
     * indicate
     *
     * @param uuid_service
     * @param uuid_indicate
     * @param callback
     * @return
     */
    public boolean indicate(String uuid_service,
                            String uuid_indicate,
                            BleCharacterCallback callback) {
        if (bleBluetooth != null) {
            return bleBluetooth.newBleConnector(commandSendResultObserver)
                    .withUUIDString(uuid_service, uuid_indicate, null)
                    .enableCharacteristicIndicate(callback, uuid_indicate);
        }
        return false;
    }


    /**
     * stop notify, remove callback
     *
     * @param uuid_service
     * @param uuid_notify
     * @return
     */
    public boolean stopNotify(String uuid_service, String uuid_notify) {
        boolean success = bleBluetooth.newBleConnector(commandSendResultObserver)
                .withUUIDString(uuid_service, uuid_notify, null)
                .disableCharacteristicNotify();
        if (success) {
            bleBluetooth.removeGattCallback(uuid_notify);
        }
        return success;
    }

    /**
     * stop indicate, remove callback
     *
     * @param uuid_service
     * @param uuid_indicate
     * @return
     */
    public boolean stopIndicate(String uuid_service, String uuid_indicate) {
        boolean success = bleBluetooth.newBleConnector(commandSendResultObserver)
                .withUUIDString(uuid_service, uuid_indicate, null)
                .disableCharacteristicIndicate();
        if (success) {
            bleBluetooth.removeGattCallback(uuid_indicate);
        }
        return success;
    }

    /**
     * write
     *
     * @param uuid_service
     * @param uuid_write
     * @param data
     * @param callback
     * @return
     */
    private boolean writeDevice(String uuid_service,
                                String uuid_write,
                                byte[] data,
                                BleCharacterCallback callback) {
        if (!isBlueEnable()) {
            handleException(new BlueToothNotEnableException());
            return false;
        }


        if (!isConnected()) {
            handleException(new NotFoundDeviceException());
            return false;
        }

        return bleBluetooth.newBleConnector(commandSendResultObserver)
                .withUUIDString(uuid_service, uuid_write, null)
                .writeCharacteristic(data, callback, uuid_write);
    }


    public boolean writeDevice(String uuid_service,
                               byte[] data,
                               BleCharacterCallback callback) {
        if (!isBlueEnable()) {
            handleException(new BlueToothNotEnableException());
            return false;
        }


        if (!isConnected()) {
            handleException(new NotFoundDeviceException());
            return false;
        }

        return bleBluetooth.newBleConnector(commandSendResultObserver)
                .withUUIDString(uuid_service, ServiceAnalyzer.UUID_WRITE.toString(), null)
                .writeCharacteristic(data, callback, ServiceAnalyzer.UUID_WRITE.toString());
    }

    /**
     * read
     *
     * @param uuid_service
     * @param uuid_read
     * @param callback
     * @return
     */
    public boolean readDevice(String uuid_service,
                              String uuid_read,
                              BleCharacterCallback callback) {
        return bleBluetooth.newBleConnector(commandSendResultObserver)
                .withUUIDString(uuid_service, uuid_read, null)
                .readCharacteristic(callback, uuid_read);
    }

    /**
     * read Rssi
     *
     * @param callback
     * @return
     */
    public boolean readRssi(BleRssiCallback callback) {
        return bleBluetooth.newBleConnector(commandSendResultObserver)
                .readRemoteRssi(callback);
    }


    /**
     * refresh Device Cache
     */
    public void refreshDeviceCache() {
        bleBluetooth.refreshGattCache();
    }

    /**
     * close gatt
     */
    private void disconnectBluetoothGatt() {
        if (bleBluetooth != null && getCurGatt() != null) {
            bleBluetooth.clearCallback();
            try {
                bleBluetooth.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * is support ble?
     *
     * @return
     */
    public boolean isSupportBle() {
        return mContext.getApplicationContext()
                .getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * open bluetooth
     */
    public void enableBluetooth() {
        if (bleBluetooth != null) {
            bleBluetooth.enableBluetoothIfDisabled();
        }
    }

    /**
     * close bluetooth
     */
    public void disableBluetooth() {
        if (bleBluetooth != null) {
            bleBluetooth.disableBluetooth();
        }
    }

    public boolean isBlueEnable() {
        return bleBluetooth != null && bleBluetooth.isBlueEnable();
    }


    public boolean isInScanning() {
        return bleBluetooth != null && bleBluetooth.isInScanning();
    }

    public boolean isConnectingOrConnected() {
        return bleBluetooth != null && bleBluetooth.isConnectingOrConnected();
    }

    public boolean isConnected() {
        return bleBluetooth != null && bleBluetooth.isConnected();
    }

    public boolean isServiceDiscovered() {
        return bleBluetooth != null && bleBluetooth.isServiceDiscovered();
    }

    /**
     * remove callback form a character
     */
    public void stopListenCharacterCallback(String uuid) {
        bleBluetooth.removeGattCallback(uuid);
    }

    /**
     * remove callback for gatt connect
     */
    public void stopListenConnectCallback() {
        bleBluetooth.removeConnectGattCallback();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scanDeviceAfterGetPermission(tempListScanCallback);
                }
                break;
        }
    }


    public BluetoothGatt getCurGatt() {
        return curGatt;
    }

    public void setCurGatt(BluetoothGatt curGatt) {
        this.curGatt = curGatt;
    }

    public CommandSender.ISendResultObserver getCommandSendResultObserver() {
        return commandSendResultObserver;
    }

    public void setCommandSendResultObserver(CommandSender.ISendResultObserver
                                                     commandSendResultObserver) {
        this.commandSendResultObserver = commandSendResultObserver;
    }

    public void connectDeviceWithFilterName(String[] filterName,
                                            long time_out,
                                            boolean autoConnect,
                                            BleGattCallback callback) {
        if (!isBlueEnable() && callback != null) {
            callback.onConnectError(new BlueToothNotEnableException());
        } else {
            bleBluetooth.scanFilterNameAndConnect(filterName, time_out, autoConnect, callback);
        }
    }
}

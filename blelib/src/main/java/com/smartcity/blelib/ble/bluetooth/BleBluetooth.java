package com.smartcity.blelib.ble.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.smartcity.blelib.ble.conn.BleConnector;
import com.smartcity.blelib.ble.conn.BleGattCallback;
import com.smartcity.blelib.ble.data.ScanResult;
import com.smartcity.blelib.ble.exception.BleException;
import com.smartcity.blelib.ble.exception.ConnectException;
import com.smartcity.blelib.ble.exception.NotFoundDeviceException;
import com.smartcity.blelib.ble.exception.ScanFailedException;
import com.smartcity.blelib.ble.scan.FilterNameCallback;
import com.smartcity.blelib.ble.scan.MacScanCallback;
import com.smartcity.blelib.ble.scan.NameScanCallback;
import com.smartcity.blelib.ble.scan.PeriodScanCallback;
import com.smartcity.blelib.command.CommandSender;
import com.smartcity.blelib.utils.BleLog;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
public class BleBluetooth {
    private static final String TAG = "BleBluetooth";

    private static final String CONNECT_CALLBACK_KEY = "connect_key";
    public static final String READ_RSSI_KEY = "rssi_key";

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_SCANNING = 1;
    private static final int STATE_CONNECTING = 2;
    private static final int STATE_CONNECTED = 3;
    private static final int STATE_SERVICES_DISCOVERED = 4;

    private static int connectionState = STATE_DISCONNECTED;
    private final Context context;
    private final BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private final BluetoothManager bluetoothManager;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final HashMap<String, BluetoothGattCallback> callbackHashMap = new HashMap<>();
    private PeriodScanCallback periodScanCallback;



    public BleBluetooth(Context context) {
        this.context = context = context.getApplicationContext();
        bluetoothManager = (BluetoothManager) context
                .getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    /**
     * 获取已经连接的设备
     *
     * @return
     */
    public List<BluetoothDevice> getConnectedDevices() {
        if (bluetoothManager != null)
            return bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
        else
            return null;
    }

    @Deprecated
    public BleConnector newBleConnector() {
        return new BleConnector(this);
    }

    public BleConnector newBleConnector(CommandSender.ISendResultObserver
                                                commandSendResultObserver) {
        return new BleConnector(this, commandSendResultObserver);
    }

    public boolean isInScanning() {
        return connectionState == STATE_SCANNING;
    }

    public boolean isConnectingOrConnected() {
        return connectionState >= STATE_CONNECTING;
    }

    public boolean isConnected() {
        return connectionState >= STATE_CONNECTED;
    }

    public boolean isServiceDiscovered() {
        return connectionState == STATE_SERVICES_DISCOVERED;
    }


    private void addConnectGattCallback(BleGattCallback callback) {
        callbackHashMap.put(CONNECT_CALLBACK_KEY, callback);
    }

    public void addGattCallback(String uuid, BluetoothGattCallback callback) {
        callbackHashMap.put(uuid, callback);
    }

    public void removeConnectGattCallback() {
        callbackHashMap.remove(CONNECT_CALLBACK_KEY);
    }

    public void removeGattCallback(String key) {
        callbackHashMap.remove(key);
    }

    public void clearCallback() {
        callbackHashMap.clear();
    }

    public BluetoothGattCallback getGattCallback(String uuid) {
        if (TextUtils.isEmpty(uuid))
            return null;
        return callbackHashMap.get(uuid);
    }

    public boolean startLeScan(PeriodScanCallback callback) {
        synchronized (this) {
            this.periodScanCallback = callback;
            callback.setBleBluetooth(this).notifyScanStarted();
            boolean success = bluetoothAdapter.startLeScan(callback);
            if (success) {
                connectionState = STATE_SCANNING;
            } else {
                callback.removeHandlerMsg();
            }
            return success;
        }
    }

    public void cancelScan() {
        if (periodScanCallback != null && connectionState == STATE_SCANNING)
            periodScanCallback.notifyScanCancel();
    }

    public void stopScan(BluetoothAdapter.LeScanCallback callback) {
        if (callback instanceof PeriodScanCallback) {
            ((PeriodScanCallback) callback).removeHandlerMsg();
        }
        bluetoothAdapter.stopLeScan(callback);
        if (connectionState == STATE_SCANNING) {
            connectionState = STATE_DISCONNECTED;
        }
    }

    public BluetoothGatt connect(final ScanResult scanResult,
                                 final boolean autoConnect,
                                 BleGattCallback callback) {
        synchronized (BleBluetooth.class) {
            BleLog.i("connect name: " + scanResult.getDevice().getName()
                    + "\nmac: " + scanResult.getDevice().getAddress()
                    + "\nautoConnect: " + autoConnect);
            addConnectGattCallback(callback);
            try {
                //睡眠50ms，提高连接效率
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                return scanResult.getDevice().connectGatt(context, autoConnect, coreGattCallback);
            }
        }

    }

    /**
     * 主动断开连接
     */
    public void disconnect() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            connectionState = STATE_DISCONNECTED;
//            refreshGattCache();
        } else {
            BleLog.d(TAG, "没有连接任何设备,不需要断开连接.");
        }
    }

    public void scanNameAndConnect(String name, long time_out, final boolean autoConnect, final
    BleGattCallback callback) {
        scanNameAndConnect(name, time_out, autoConnect, false, callback);
    }

    public void scanNameAndConnect(String name, long time_out, final boolean autoConnect, boolean
            fuzzy, final BleGattCallback callback) {
        if (TextUtils.isEmpty(name)) {
            if (callback != null) {
                callback.onConnectError(new NotFoundDeviceException());
            }
            return;
        }
        boolean success = startLeScan(new NameScanCallback(name, time_out, fuzzy) {

            @Override
            public void onDeviceFound(final ScanResult scanResult) {
//                runOnMainThread(new Runnable() {
//                    @Override
//                    public void run() {
                if (callback != null) {
                    callback.onFoundDevice(scanResult);
                }
                //TODO 对于三星NOTE3 设备 必须放在主线程连接才有效
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        connect(scanResult, autoConnect, callback);
                    }
                });
//                connect(scanResult, autoConnect, callback);
//                    }
//                });
            }

            @Override
            public void onDeviceNotFound() {
//                runOnMainThread(new Runnable() {
//                    @Override
//                    public void run() {
                if (callback != null) {
                    callback.onConnectError(new NotFoundDeviceException());
                }
//                    }
//                });
            }
        });
        if (!success && callback != null) {
            callback.onConnectError(new ScanFailedException());
        }
    }

    public void scanNameAndConnect(String[] names, long time_out, final boolean autoConnect,
                                   final BleGattCallback callback) {
        scanNameAndConnect(names, time_out, autoConnect, false, callback);
    }

    public void scanNameAndConnect(String[] names, long time_out, final boolean autoConnect,
                                   boolean fuzzy, final BleGattCallback callback) {
        if (names == null || names.length < 1) {
            if (callback != null) {
                callback.onConnectError(new NotFoundDeviceException());
            }
            return;
        }
        boolean success = startLeScan(new NameScanCallback(names, time_out, fuzzy) {

            @Override
            public void onDeviceFound(final ScanResult scanResult) {
//                runOnMainThread(new Runnable() {
//                    @Override
//                    public void run() {
                if (callback != null) {
                    callback.onFoundDevice(scanResult);
                }
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        connect(scanResult, autoConnect, callback);
                    }
                });
//                connect(scanResult, autoConnect, callback);
//                    }
//                });
            }

            @Override
            public void onDeviceNotFound() {
//                runOnMainThread(new Runnable() {
//                    @Override
//                    public void run() {
                if (callback != null) {
                    callback.onConnectError(new NotFoundDeviceException());
                }
//                    }
//                });
            }
        });
        if (!success && callback != null) {
            callback.onConnectError(new ScanFailedException());
        }
    }

    public void scanMacAndConnect(String mac, long time_out, final boolean autoConnect, final
    BleGattCallback callback) {
        if (TextUtils.isEmpty(mac)) {
            if (callback != null) {
                callback.onConnectError(new NotFoundDeviceException());
            }
            return;
        }
        boolean success = startLeScan(new MacScanCallback(mac, time_out) {
            //注意下面的runOnMainThread方法不能要，否则外层的异步数据同步返回有问题。
            @Override
            public void onDeviceFound(final ScanResult scanResult) {
//                runOnMainThread(new Runnable() {
//                    @Override
//                    public void run() {

                if (callback != null) {
                    callback.onFoundDevice(scanResult);
                }
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        connect(scanResult, autoConnect, callback);
                    }
                });
//                connect(scanResult, autoConnect, callback);
//                    }
//                });
            }

            @Override
            public void onDeviceNotFound() {
//                runOnMainThread(new Runnable() {
//                    @Override
//                    public void run() {
                if (callback != null) {
                    callback.onConnectError(new NotFoundDeviceException());
                }
//                    }
//                });
            }
        });
        if (!success && callback != null) {
            callback.onConnectError(new ScanFailedException());
        }
    }

    private boolean refreshDeviceCache() {
        try {
            final Method refresh = BluetoothGatt.class.getMethod("refresh");
            if (refresh != null) {
                boolean success = (Boolean) refresh.invoke(mBluetoothGatt);
                BleLog.i("refreshDeviceCache, is success:  " + success);
                return success;
            }
        } catch (Exception e) {
            BleLog.i("exception occur while refreshing device: " + e.getMessage());
            e.printStackTrace();
        } finally {
            mBluetoothGatt.close();
        }
        return false;
    }

    /**
     * 释放资源
     */
    public void refreshGattCache() {
        if (mBluetoothGatt != null) {
            refreshDeviceCache();
        }
    }

    public void enableBluetoothIfDisabled() {
        if (!isBlueEnable()) {
            enableBluetooth();
        }
    }

    public boolean isBlueEnable() {
        return bluetoothAdapter.isEnabled();
    }

    public void enableBluetooth() {
        bluetoothAdapter.enable();
    }

    public void disableBluetooth() {
        bluetoothAdapter.disable();
    }

    private void runOnMainThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            mHandler.post(runnable);
        }
    }

    public Context getContext() {
        return context;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public BluetoothGatt getmBluetoothGatt() {
        return mBluetoothGatt;
    }

    public int getConnectionState() {
        return connectionState;
    }

    private final BleGattCallback coreGattCallback = new BleGattCallback() {

        @Override
        public void onFoundDevice(ScanResult scanResult) {
            BleLog.i("BleGattCallback：onFoundDevice ");
        }

        @Override
        public void onConnecting(BluetoothGatt gatt, int status) {
            BleLog.i("BleGattCallback：onConnectSuccess ");

            mBluetoothGatt = gatt;
            Iterator iterator = callbackHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object call = entry.getValue();
                if (call instanceof BleGattCallback) {
                    ((BleGattCallback) call).onConnecting(gatt, status);
                }
            }
        }

        @Override
        public void onConnectSuccess(BluetoothGatt gatt, int status) {
            BleLog.i("BleGattCallback：onConnectSuccess ");

            mBluetoothGatt = gatt;
            Iterator iterator = callbackHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object call = entry.getValue();
                if (call instanceof BleGattCallback) {
                    ((BleGattCallback) call).onConnectSuccess(gatt, status);
                    gatt.discoverServices();
                }
            }
        }

        @Override
        public void onDisConnected(BluetoothGatt gatt, int status, BleException exception) {
            BleLog.i("BleGattCallback：onDisConnected ");
            refreshGattCache();
            mBluetoothGatt = null;
            Iterator iterator = callbackHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object call = entry.getValue();
                if (call instanceof BleGattCallback) {
                    ((BleGattCallback) call).onDisConnected(gatt, status, exception);
                }
            }
        }

        @Override
        public void onDeviceVersion(String version) {

        }

        @Override
        public void onConnectError(BleException exception) {
            BleLog.i("BleGattCallback：onConnectError ");
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            BleLog.i("BleGattCallback：onConnectionStateChange "
                    + '\n' + "status: " + status
                    + '\n' + "newState: " + newState
                    + '\n' + "currentThread: " + Thread.currentThread().getId());

            if (newState == BluetoothGatt.STATE_CONNECTED) {
                connectionState = STATE_CONNECTED;
                onConnectSuccess(gatt, status);

            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                connectionState = STATE_DISCONNECTED;
                onDisConnected(gatt, status, new ConnectException(gatt, status));

            } else if (newState == BluetoothGatt.STATE_CONNECTING) {
                connectionState = STATE_CONNECTING;
                onConnecting(gatt, status);
            }

            Iterator iterator = callbackHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object call = entry.getValue();
                if (call instanceof BluetoothGattCallback) {
                    ((BluetoothGattCallback) call).onConnectionStateChange(gatt, status, newState);
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            BleLog.i("BleGattCallback：onServicesDiscovered ");

            connectionState = STATE_SERVICES_DISCOVERED;
            Iterator iterator = callbackHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object call = entry.getValue();
                if (call instanceof BluetoothGattCallback) {
                    ((BluetoothGattCallback) call).onServicesDiscovered(gatt, status);
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic, int status) {
            BleLog.i("BleGattCallback：onCharacteristicRead ");

            Iterator iterator = callbackHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object call = entry.getValue();
                if (call instanceof BluetoothGattCallback) {
                    ((BluetoothGattCallback) call).onCharacteristicRead(gatt, characteristic,
                            status);
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic, int status) {
            BleLog.i("BleGattCallback：onCharacteristicWrite ");

            Iterator iterator = callbackHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object call = entry.getValue();
                if (call instanceof BluetoothGattCallback) {
                    ((BluetoothGattCallback) call).onCharacteristicWrite(gatt, characteristic,
                            status);
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic) {
            BleLog.i("BleGattCallback：onCharacteristicChanged ");

            Iterator iterator = callbackHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object call = entry.getValue();
                if (call instanceof BluetoothGattCallback) {
                    ((BluetoothGattCallback) call).onCharacteristicChanged(gatt, characteristic);
                }
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int
                status) {
            BleLog.i("BleGattCallback：onDescriptorRead ");

            Iterator iterator = callbackHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object call = entry.getValue();
                if (call instanceof BluetoothGattCallback) {
                    ((BluetoothGattCallback) call).onDescriptorRead(gatt, descriptor, status);
                }
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int
                status) {
            BleLog.i("BleGattCallback：onDescriptorWrite ");

            Iterator iterator = callbackHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object call = entry.getValue();
                if (call instanceof BluetoothGattCallback) {
                    ((BluetoothGattCallback) call).onDescriptorWrite(gatt, descriptor, status);
                }
            }
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            BleLog.i("BleGattCallback：onReliableWriteCompleted ");

            Iterator iterator = callbackHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object call = entry.getValue();
                if (call instanceof BluetoothGattCallback) {
                    ((BluetoothGattCallback) call).onReliableWriteCompleted(gatt, status);
                }
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            BleLog.i("BleGattCallback：onReadRemoteRssi ");

            Iterator iterator = callbackHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object call = entry.getValue();
                if (call instanceof BluetoothGattCallback) {
                    ((BluetoothGattCallback) call).onReadRemoteRssi(gatt, rssi, status);
                }
            }
        }
    };

    public void scanFilterNameAndConnect(String[] filterName, long time_out, final boolean
            autoConnect, final BleGattCallback callback) {
        if (filterName == null || filterName.length < 1) {
            if (callback != null) {
                callback.onConnectError(new NotFoundDeviceException());
            }
            return;
        }
        boolean success = startLeScan(new FilterNameCallback(time_out, filterName) {

            @Override
            public void onDeviceFound(final ScanResult scanResult) {
                if (callback != null) {
                    callback.onFoundDevice(scanResult);
                }
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        connect(scanResult, autoConnect, callback);
                    }
                });
            }

            @Override
            public void onDeviceNotFound() {
                if (callback != null) {
                    callback.onConnectError(new NotFoundDeviceException());
                }
            }
        });
        if (!success && callback != null) {
            callback.onConnectError(new ScanFailedException());
        }
    }

}

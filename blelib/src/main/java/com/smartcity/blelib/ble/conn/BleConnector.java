package com.smartcity.blelib.ble.conn;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.smartcity.blelib.ble.bluetooth.BleBluetooth;
import com.smartcity.blelib.ble.exception.GattException;
import com.smartcity.blelib.ble.exception.OtherException;
import com.smartcity.blelib.ble.exception.TimeoutException;
import com.smartcity.blelib.command.CommandSender;
import com.smartcity.blelib.utils.BleLog;
import com.smartcity.blelib.utils.HexUtil0;

import java.util.Arrays;
import java.util.UUID;
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
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleConnector {
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");


    private static final String TAG = BleConnector.class.getSimpleName();
    private static final String UUID_CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR =
            "00002902-0000-1000-8000-00805f9b34fb";

    private static final int MSG_WRITE_CHA = 1;
    private static final int MSG_WRIATE_DES = 2;
    private static final int MSG_READ_CHA = 3;
    private static final int MSG_READ_DES = 4;
    private static final int MSG_READ_RSSI = 5;
    private static final int MSG_NOTIFY_CHA = 6;
    private static final int MSG_NOTIY_DES = 7;
    private static final int MSG_INDICATE_DES = 8;

    private final BluetoothGatt bluetoothGatt;
    private BluetoothGattService service;
    private BluetoothGattCharacteristic characteristic;
    private BluetoothGattDescriptor descriptor;
    private final BleBluetooth bleBluetooth;
    private static int timeOutMillis = 10000;
    private final Handler mHandler;
    private CommandSender.ISendResultObserver commandSendResultObserver;        //回调蓝牙的数据给上层


    private static final class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            BleCallback call = (BleCallback) msg.obj;
            if (call != null) {
                call.onFailure(new TimeoutException());
            }
            msg.obj = null;
        }
    }


    @Deprecated
    public BleConnector(BleBluetooth bleBluetooth) {
        this.bleBluetooth = bleBluetooth;
        this.bluetoothGatt = bleBluetooth.getmBluetoothGatt();
        this.mHandler = new Handler(Looper.getMainLooper());
    }


    public BleConnector(BleBluetooth bleBluetooth, CommandSender.ISendResultObserver
            commandSendResultObserver) {
        this.bleBluetooth = bleBluetooth;
        this.bluetoothGatt = bleBluetooth.getmBluetoothGatt();
        this.mHandler = new Handler(Looper.getMainLooper());
        this.commandSendResultObserver = commandSendResultObserver;
    }

    public BleConnector(BleBluetooth bleBluetooth, BluetoothGattService service,
                        BluetoothGattCharacteristic characteristic, BluetoothGattDescriptor
                                descriptor) {
        this(bleBluetooth);
        this.service = service;
        this.characteristic = characteristic;
        this.descriptor = descriptor;
    }

    public BleConnector(BleBluetooth bleBluetooth,
                        UUID serviceUUID, UUID charactUUID,
                        UUID descriptorUUID, UUID client_characteristic_conifgUUID) {
        this(bleBluetooth);
        withUUID(serviceUUID, charactUUID, descriptorUUID);
    }

    public BleConnector(BleBluetooth bleBluetooth,
                        String serviceUUID, String charactUUID,
                        String descriptorUUID, String client_characteristic_conifgUUID) {
        this(bleBluetooth);
        withUUIDString(serviceUUID, charactUUID, descriptorUUID);
    }


    public BleConnector withUUID(UUID serviceUUID, UUID charactUUID, UUID descriptorUUID) {

        if (serviceUUID != null && bluetoothGatt != null) {
            service = bluetoothGatt.getService(serviceUUID);
        }

        if (service != null && charactUUID != null) {
            characteristic = service.getCharacteristic(charactUUID);
        }

        if (characteristic != null && descriptorUUID != null) {
            descriptor = characteristic.getDescriptor(descriptorUUID);
        }

        return this;
    }

    public BleConnector withUUIDString(String serviceUUID, String charactUUID,
                                       String descriptorUUID) {
        return withUUID(formUUID(serviceUUID), formUUID(charactUUID), formUUID(descriptorUUID));
    }

    private UUID formUUID(String uuid) {
        return uuid == null ? null : UUID.fromString(uuid);
    }




     /*------------------------------- main operation ----------------------------------- */


    /**
     * notify
     */
    public boolean enableCharacteristicNotify(BleCharacterCallback bleCallback, String
            uuid_notify) {

        if (getCharacteristic() != null
                && (getCharacteristic().getProperties() | BluetoothGattCharacteristic
                .PROPERTY_NOTIFY) > 0) {
            BleLog.w(TAG, "characteristic.getProperties():" + getCharacteristic().getProperties());

            handleCharacteristicNotificationCallback(bleCallback, uuid_notify);

            return setCharacteristicNotification(getBluetoothGatt(), getCharacteristic(), true,
                    bleCallback);

        } else {
            if (bleCallback != null) {
                bleCallback.onFailure(new OtherException("this characteristic not support " +
                        "notify!"));
                bleCallback.onInitiatedResult(false);
            }
            return false;
        }
    }

    /**
     * stop notify
     */
    public boolean disableCharacteristicNotify() {
        if (getCharacteristic() != null
                && (getCharacteristic().getProperties() | BluetoothGattCharacteristic
                .PROPERTY_NOTIFY) > 0) {
            BleLog.w(TAG, "characteristic.getProperties():" + getCharacteristic().getProperties());

            return setCharacteristicNotification(getBluetoothGatt(), getCharacteristic(), false,
                    null);
        } else {
            return false;
        }
    }

    /**
     * notify setting
     */
    private boolean setCharacteristicNotification(BluetoothGatt gatt,
                                                  BluetoothGattCharacteristic characteristic,
                                                  boolean enable,
                                                  BleCharacterCallback bleCallback) {
        if (gatt == null || characteristic == null) {
            if (bleCallback != null) {
                bleCallback.onFailure(new OtherException("gatt or characteristic equal null"));
                bleCallback.onInitiatedResult(false);
            }
            return false;
        }

        boolean success = gatt.setCharacteristicNotification(characteristic, enable);
        BleLog.d(TAG, "setCharacteristicNotification: " + enable
                + "\nsuccess: " + success
                + "\ncharacteristic.getUuid(): " + characteristic.getUuid());

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(formUUID
                (UUID_CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR));
        if (descriptor != null) {
            descriptor.setValue(enable ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE :
                    BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            boolean success2 = gatt.writeDescriptor(descriptor);
            if (bleCallback != null) {
                bleCallback.onInitiatedResult(success2);
            }
            return success2;
        }
        if (bleCallback != null) {
            bleCallback.onFailure(new OtherException("notify operation failed"));
            bleCallback.onInitiatedResult(false);
        }
        return false;
    }

    /**
     * indicate
     */
    public boolean enableCharacteristicIndicate(BleCharacterCallback bleCallback, String
            uuid_indicate) {
        if (getCharacteristic() != null
                && (getCharacteristic().getProperties() | BluetoothGattCharacteristic
                .PROPERTY_NOTIFY) > 0) {
            BleLog.w(TAG, "characteristic.getProperties():" + getCharacteristic().getProperties());

            handleCharacteristicIndicationCallback(bleCallback, uuid_indicate);

            return setCharacteristicIndication(getBluetoothGatt(), getCharacteristic(), true,
                    bleCallback);

        } else {
            if (bleCallback != null) {
                bleCallback.onFailure(new OtherException("this characteristic not support " +
                        "indicate!"));
            }
            return false;
        }
    }


    /**
     * stop indicate
     */
    public boolean disableCharacteristicIndicate() {
        if (getCharacteristic() != null
                && (getCharacteristic().getProperties() | BluetoothGattCharacteristic
                .PROPERTY_NOTIFY) > 0) {
            BleLog.w(TAG, "characteristic.getProperties():" + getCharacteristic().getProperties());

            return setCharacteristicIndication(getBluetoothGatt(), getCharacteristic(), false,
                    null);

        } else {
            return false;
        }
    }

    /**
     * indicate setting
     */
    private boolean setCharacteristicIndication(BluetoothGatt gatt,
                                                BluetoothGattCharacteristic characteristic,
                                                boolean enable,
                                                BleCharacterCallback bleCallback) {
        if (gatt == null || characteristic == null) {
            if (bleCallback != null) {
                bleCallback.onFailure(new OtherException("gatt or characteristic equal null"));
                bleCallback.onInitiatedResult(false);
            }
            return false;
        }

        boolean success = gatt.setCharacteristicNotification(characteristic, enable);
        BleLog.d(TAG, "setCharacteristicIndication:" + enable
                + "\nsuccess:" + success
                + "\ncharacteristic.getUuid():" + characteristic.getUuid());

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(formUUID
                (UUID_CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR));
        if (descriptor != null) {
            descriptor.setValue(enable ? BluetoothGattDescriptor.ENABLE_INDICATION_VALUE :
                    BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            boolean success2 = gatt.writeDescriptor(descriptor);
            if (bleCallback != null) {
                bleCallback.onInitiatedResult(success2);
            }
            return success2;
        }
        if (bleCallback != null) {
            bleCallback.onFailure(new OtherException("indicate operation failed"));
            bleCallback.onInitiatedResult(false);
        }
        return false;
    }


    /**
     * write
     */
    public boolean writeCharacteristic(byte[] data, BleCharacterCallback bleCallback, String
            uuid_write) {
        if (data == null) {
            if (bleCallback != null) {
                bleCallback.onFailure(new OtherException("the data to be written is empty"));
                bleCallback.onInitiatedResult(false);
            }
            return false;
        }

        if (getCharacteristic() == null
                || (getCharacteristic().getProperties() & (BluetoothGattCharacteristic
                .PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) == 0) {
            if (bleCallback != null) {
                bleCallback.onFailure(new OtherException("this characteristic not support write!"));
                bleCallback.onInitiatedResult(false);
            }
            return false;
        }

        BleLog.d(TAG, getCharacteristic().getUuid()
                + "\ncharacteristic.getProperties():" + getCharacteristic().getProperties()
                + "\ncharacteristic.getValue(): " + Arrays.toString(getCharacteristic().getValue())
                + "\ncharacteristic write bytes: " + Arrays.toString(data)
                + "\nhex: " + HexUtil0.encodeHexStr(data));

        handleCharacteristicWriteCallback(bleCallback, uuid_write);
        getCharacteristic().setValue(data);
        return handleAfterInitialed(getBluetoothGatt().writeCharacteristic(getCharacteristic()),
                bleCallback);
    }

    /**
     * read
     */
    public boolean readCharacteristic(BleCharacterCallback bleCallback, String uuid_read) {
        if (getCharacteristic() != null
                && (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) >
                0) {

            BleLog.d(TAG, getCharacteristic().getUuid()
                    + "\ncharacteristic.getProperties(): " + getCharacteristic().getProperties()
                    + "\ncharacteristic.getValue(): " + Arrays.toString(getCharacteristic()
                    .getValue()));

            setCharacteristicNotification(getBluetoothGatt(), getCharacteristic(), false,
                    bleCallback);
            handleCharacteristicReadCallback(bleCallback, uuid_read);
            return handleAfterInitialed(getBluetoothGatt().readCharacteristic(getCharacteristic()
            ), bleCallback);

        } else {
            if (bleCallback != null) {
                bleCallback.onFailure(new OtherException("this characteristic not support read!"));
                bleCallback.onInitiatedResult(false);
            }
            return false;
        }
    }

    /**
     * rssi
     */
    public boolean readRemoteRssi(BleRssiCallback bleCallback) {
        handleRSSIReadCallback(bleCallback);
        return handleAfterInitialed(getBluetoothGatt().readRemoteRssi(), bleCallback);
    }


    /**************************************** handle call back
     * ******************************************/

    /**
     * notify
     */
    private void handleCharacteristicNotificationCallback(final BleCharacterCallback bleCallback,
                                                          final String uuid_notify) {
        if (bleCallback != null) {

            listenAndTimer(bleCallback, MSG_NOTIFY_CHA, uuid_notify, new BluetoothGattCallback() {
                AtomicBoolean msgRemoved = new AtomicBoolean(false);

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt,
                                                    BluetoothGattCharacteristic characteristic) {
                    Log.d(TAG, " onCharacteristicChanged:  Notification  -thread ");
                    if (!msgRemoved.getAndSet(true)) {
                        mHandler.removeMessages(MSG_NOTIFY_CHA, this);
                    }
                    if (characteristic.getUuid().equals(UUID.fromString(uuid_notify))) {
                        bleCallback.onSuccess(characteristic);
                    }

                    if (commandSendResultObserver != null) {
                        commandSendResultObserver.onChange(getCharacteristicValue(characteristic)
                                , 0);
                    }
                }
            });
        }
    }

    /**
     * indicate
     */
    private void handleCharacteristicIndicationCallback(final BleCharacterCallback bleCallback,
                                                        final String uuid_indicate) {
        if (bleCallback != null) {

            listenAndTimer(bleCallback, MSG_INDICATE_DES, uuid_indicate, new
                    BluetoothGattCallback() {
                        AtomicBoolean msgRemoved = new AtomicBoolean(false);

                        @Override
                        public void onCharacteristicChanged(BluetoothGatt gatt,
                                                            BluetoothGattCharacteristic
                                                                    characteristic) {
                            Log.d(TAG, " onCharacteristicChanged:  Indication  -thread ");
                            if (!msgRemoved.getAndSet(true)) {
                                mHandler.removeMessages(MSG_INDICATE_DES, this);
                            }
                            if (characteristic.getUuid().equals(UUID.fromString(uuid_indicate))) {
                                bleCallback.onSuccess(characteristic);
                            }

                            if (commandSendResultObserver != null) {
                                commandSendResultObserver.onChange(getCharacteristicValue
                                                (characteristic)
                                        , 1);
                            }
                        }
                    });
        }
    }

    /**
     * write
     */
    private void handleCharacteristicWriteCallback(final BleCharacterCallback bleCallback,
                                                   final String uuid_write) {
        if (bleCallback != null) {

            listenAndTimer(bleCallback, MSG_WRITE_CHA, uuid_write, new BluetoothGattCallback() {
                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt,
                                                  BluetoothGattCharacteristic characteristic, int
                                                          status) {
                    Log.d(TAG, "onCharacteristicWrite ");
                    mHandler.removeMessages(MSG_WRITE_CHA, this);

                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        if (characteristic.getUuid().equals(UUID.fromString(uuid_write))) {
                            bleCallback.onSuccess(characteristic);

                            if (commandSendResultObserver != null) {
                                commandSendResultObserver.onWrite();
                            }

                        }
                    } else {
                        bleCallback.onFailure(new GattException(status));
                    }


                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt,
                                                    BluetoothGattCharacteristic characteristic) {
                    super.onCharacteristicChanged(gatt, characteristic);
                    String name = Thread.currentThread().getName();
                    Log.d(TAG, " onCharacteristicChanged:  Write  -thread " + name);
//                    //将解析成功的数据以广播形式发送出去。
//                    broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                    if (commandSendResultObserver != null) {
                        commandSendResultObserver.onChange(getCharacteristicValue(characteristic)
                                , 2);
                    }
                }
            });
        }
    }


    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer
        // .aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA,/* new String(data) + "\n" + */stringBuilder.toString());
            }
        }
        bleBluetooth.getContext().sendBroadcast(intent);
    }

    private String getCharacteristicValue(final BluetoothGattCharacteristic characteristic) {
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            return String.valueOf(heartRate);
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                return stringBuilder.toString();
            }
        }
        return null;
    }

    /**
     * read
     */
    private void handleCharacteristicReadCallback(final BleCharacterCallback bleCallback,
                                                  final String uuid_read) {
        if (bleCallback != null) {
            listenAndTimer(bleCallback, MSG_READ_CHA, uuid_read, new BluetoothGattCallback() {
                AtomicBoolean msgRemoved = new AtomicBoolean(false);

                @Override
                public void onCharacteristicRead(BluetoothGatt gatt,
                                                 BluetoothGattCharacteristic characteristic, int
                                                         status) {
                    if (!msgRemoved.getAndSet(true)) {
                        mHandler.removeMessages(MSG_READ_CHA, this);
                    }
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        if (characteristic.getUuid().equals(UUID.fromString(uuid_read))) {
                            bleCallback.onSuccess(characteristic);
                        }
                    } else {
                        bleCallback.onFailure(new GattException(status));
                    }
                }
            });
        }
    }

    /**
     * rssi
     */
    private void handleRSSIReadCallback(final BleRssiCallback bleCallback) {

        if (bleCallback != null) {
            listenAndTimer(bleCallback, MSG_READ_RSSI, BleBluetooth.READ_RSSI_KEY, new
                    BluetoothGattCallback() {
                        @Override
                        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                            mHandler.removeMessages(MSG_READ_RSSI, this);
                            if (status == BluetoothGatt.GATT_SUCCESS) {
                                bleCallback.onSuccess(rssi);
                            } else {
                                bleCallback.onFailure(new GattException(status));
                            }
                        }
                    });
        }
    }

    private boolean handleAfterInitialed(boolean initiated, BleCallback bleCallback) {
        if (bleCallback != null) {
            if (!initiated) {
                bleCallback.onFailure(new OtherException("write or read operation failed"));
            }
            bleCallback.onInitiatedResult(initiated);
        }
        return initiated;
    }


    /**
     * listen bleBluetooth gatt callback, and send a delayed message.
     */
    private void listenAndTimer(BleCallback bleCallback, int what, String uuid,
                                BluetoothGattCallback callback) {
        bleCallback.setBluetoothGattCallback(callback);
        bleBluetooth.addGattCallback(uuid, callback);

        Message msg = mHandler.obtainMessage(what, bleCallback);
        mHandler.sendMessageDelayed(msg, timeOutMillis);
    }



    /*------------------------------- getter and setter ----------------------------------- */


    public BluetoothGatt getBluetoothGatt() {
        return bluetoothGatt;
    }

//    public BleConnector setBluetoothGatt(BluetoothGatt bluetoothGatt) {
//        this.bluetoothGatt = bluetoothGatt;
//        return this;
//    }

    public BluetoothGattService getService() {
        return service;
    }

    public BleConnector setService(BluetoothGattService service) {
        this.service = service;
        return this;
    }

    public BluetoothGattCharacteristic getCharacteristic() {
        return characteristic;
    }

    public BleConnector setCharacteristic(BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
        return this;
    }

    public BluetoothGattDescriptor getDescriptor() {
        return descriptor;
    }

    public BleConnector setDescriptor(BluetoothGattDescriptor descriptor) {
        this.descriptor = descriptor;
        return this;
    }

    public int getTimeOutMillis() {
        return timeOutMillis;
    }

    public BleConnector setTimeOutMillis(int timeOutMillis) {
        this.timeOutMillis = timeOutMillis;
        return this;
    }
}

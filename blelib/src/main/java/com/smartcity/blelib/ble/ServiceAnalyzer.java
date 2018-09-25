package com.smartcity.blelib.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.List;
import java.util.UUID;

/**
 * Created by lianxiang on 2017/9/15.
 * Service 分析器，导航。
 */

public class ServiceAnalyzer {
    public static UUID UUID_NOFITY = null;
    public static UUID UUID_INDICATE = null;
    public static UUID UUID_WRITE = null;

    public static void resetUUIDS(){
        UUID_NOFITY = null;
        UUID_INDICATE = null;
        UUID_WRITE  =  null;
    }

    /**
     * 获取uuid对应的服务
     * @param gatt
     * @param uuid
     * @return
     */
    public static BluetoothGattService getSupportedGattService(BluetoothGatt gatt, UUID uuid) {
        BluetoothGattService mBluetoothGattService;
        if (gatt == null) return null;
        mBluetoothGattService=gatt.getService(uuid);
        return mBluetoothGattService;
    }


    /**
     * 获取对应服务可通知、可写的UUID。
     * @param gatt
     * @param UUIDService
     * @return
     */
    public static void get2UUID(BluetoothGatt gatt, UUID UUIDService){
        BluetoothGattService supportedGattService = getSupportedGattService(gatt, UUIDService);
        if(supportedGattService == null ) return ;
        List<BluetoothGattCharacteristic> characteristics = supportedGattService.getCharacteristics();

        for (BluetoothGattCharacteristic characteristic :characteristics) {
            //找到可通知的特征
            if (isNotifable(characteristic)) {
                UUID_NOFITY = characteristic.getUuid();

                //使所有的描述可通知
                for (BluetoothGattDescriptor descripter : characteristic.getDescriptors()){
                    descripter.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(descripter);
                }
            }else if(isIndicatable(characteristic)){
                UUID_INDICATE = characteristic.getUuid();

                //使所有的描述可通知
                for (BluetoothGattDescriptor descripter : characteristic.getDescriptors()){
                    descripter.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                    gatt.writeDescriptor(descripter);
                }
            }
        }


        //根据notify还是indicate来设置可写的描述
        for (BluetoothGattCharacteristic characteristic :characteristics) {
          if(isWritable(characteristic)){
                UUID_WRITE = characteristic.getUuid();

                //使所有的描述可通知
                for (BluetoothGattDescriptor descripter : characteristic.getDescriptors()){
                    if(UUID_NOFITY != null && UUID_INDICATE == null){
                        descripter.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    }else if(UUID_INDICATE != null && UUID_NOFITY == null){
                        descripter.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                    }
                    gatt.writeDescriptor(descripter);
                }
            }
        }
    }




    /**
     * 判断特征值是否可通知 - notify的方式
     * @param bluetoothGattCharacteristic
     * @return
     */
    public static boolean isNotifable(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        int charaProp = bluetoothGattCharacteristic.getProperties();
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0
               /* || (charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0*/) {
            return true;
        }
        return false;
    }


    /**
     * 判断特征值是否可indicate
     * @param bluetoothGattCharacteristic
     * @return
     */
    public static boolean isIndicatable(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        int charaProp = bluetoothGattCharacteristic.getProperties();
        if (/*(charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0
                || */(charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
            return true;
        }
        return false;
    }



    public static boolean isReadable(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        int charaProp = bluetoothGattCharacteristic.getProperties();
        // 可读
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            return true;
        }
        return false;
    }


    public static boolean isWritable(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        int charaProp = bluetoothGattCharacteristic.getProperties();
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0
                || (charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
            return true;
        }
        return false;
    }
}

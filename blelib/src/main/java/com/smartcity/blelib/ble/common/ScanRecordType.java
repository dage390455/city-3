package com.smartcity.blelib.ble.common;

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
 * <p>广播包中的数据类型：L_T_V格式
 * Created by Done on 2017/8/28.
 */

public class ScanRecordType {

    public static final int BLE_GAP_AD_TYPE_FLAGS = 0x01;
    public static final int BLE_GAP_AD_TYPE_16BIT_SERVICE_UUID_MORE_AVAILABLE = 0x02;
    public static final int BLE_GAP_AD_TYPE_16BIT_SERVICE_UUID_COMPLETE = 0x03;
    public static final int BLE_GAP_AD_TYPE_32BIT_SERVICE_UUID_MORE_AVAILABLE = 0x04;
    public static final int BLE_GAP_AD_TYPE_32BIT_SERVICE_UUID_COMPLETE = 0x05;
    public static final int BLE_GAP_AD_TYPE_128BIT_SERVICE_UUID_MORE_AVAILABLE = 0x06;
    public static final int BLE_GAP_AD_TYPE_128BIT_SERVICE_UUID_COMPLETE = 0x07;
    public static final int BLE_GAP_AD_TYPE_SHORT_LOCAL_NAME = 0x08;
    public static final int BLE_GAP_AD_TYPE_COMPLETE_LOCAL_NAME = 0x09;
    public static final int BLE_GAP_AD_TYPE_TX_POWER_LEVEL = 0x0A;
    public static final int BLE_GAP_AD_TYPE_CLASS_OF_DEVICE = 0x0D;
    public static final int BLE_GAP_AD_TYPE_SIMPLE_PAIRING_HASH_C = 0x0E;
    public static final int BLE_GAP_AD_TYPE_SIMPLE_PAIRING_RANDOMIZER_R = 0x0F;
    public static final int BLE_GAP_AD_TYPE_SECURITY_MANAGER_TK_VALUE = 0x10;
    public static final int BLE_GAP_AD_TYPE_SECURITY_MANAGER_OOB_FLAGS = 0x11;
    public static final int BLE_GAP_AD_TYPE_SLAVE_CONNECTION_INTERVAL_RANGE = 0x12;
    public static final int BLE_GAP_AD_TYPE_SOLICITED_SERVICE_UUIDS_16BIT = 0x14;
    public static final int BLE_GAP_AD_TYPE_SOLICITED_SERVICE_UUIDS_128BIT = 0x15;
    public static final int BLE_GAP_AD_TYPE_SERVICE_DATA = 0x16;
    public static final int BLE_GAP_AD_TYPE_PUBLIC_TARGET_ADDRESS = 0x17;
    public static final int BLE_GAP_AD_TYPE_RANDOM_TARGET_ADDRESS = 0x18;
    public static final int BLE_GAP_AD_TYPE_APPEARANCE = 0x19;
    public static final int BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA = 0xFF;


    public static final int BLE_SCAN_RECORD = 0x00;  //广播包中扫描包的Key
}

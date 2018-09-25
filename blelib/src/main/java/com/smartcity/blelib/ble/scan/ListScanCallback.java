package com.smartcity.blelib.ble.scan;

import android.bluetooth.BluetoothDevice;

import com.smartcity.blelib.ble.common.ScanRecordType;
import com.smartcity.blelib.ble.data.ScanResult;
import com.smartcity.blelib.utils.HexUtil0;

import java.util.ArrayList;
import java.util.List;
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

public abstract class ListScanCallback extends PeriodScanCallback {
    private static final String TAG = "ListScanCallback";

    private List<ScanResult> resultList = new ArrayList<>();
    private AtomicBoolean hasFound = new AtomicBoolean(false);

    public ListScanCallback(long timeoutMillis) {
        super(timeoutMillis);
    }

    @Override
    public void notifyScanCancel() {
        super.notifyScanCancel();
        resultList.clear();
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (device == null)
            return;
        ScanResult scanResult = new ScanResult(device, rssi, scanRecord,
                System.currentTimeMillis());
        String scanRecordStr = HexUtil0.encodeHexStr(scanRecord);
        if (scanRecord.length >= 62) {
            parseScanRecord(scanRecord, scanResult);
            scanResult.mScanRecordMap.put(ScanRecordType.BLE_SCAN_RECORD, scanRecordStr);
        }

        hasFound.set(false);
        for (ScanResult result : resultList) {
            if (result.getDevice().equals(device)) {
                hasFound.set(true);
            }
        }
        if (!hasFound.get()) {
            resultList.add(scanResult);
//                onScanning(scanResult);
        }
    }

    private void parseScanRecord(byte[] scanRecord, ScanResult scanResult) {
        int offset = 1;
        int curLen = 0;
        List<String> curContent = new ArrayList<>();
        byte[] copyContent;
        for (int i = 0; i < scanRecord.length; i++) {
            curLen = scanRecord[offset - 1] & 0xFF;
            if (curLen == 0) {
                return;
            }
            copyContent = new byte[curLen - 1];
            System.arraycopy(scanRecord, offset + 1, copyContent, 0, curLen - 2);
            curContent.add(HexUtil0.encodeHexStr(copyContent));
            switch (scanRecord[offset] & 0xFF) {
                case ScanRecordType.BLE_GAP_AD_TYPE_FLAGS:
                    scanResult.mBroadcastMap.put(ScanRecordType.BLE_GAP_AD_TYPE_FLAGS, curContent);
                    offset += curLen + 1;
                    break;
                case ScanRecordType.BLE_GAP_AD_TYPE_16BIT_SERVICE_UUID_MORE_AVAILABLE:
                    scanResult.mBroadcastMap.put(ScanRecordType
                            .BLE_GAP_AD_TYPE_16BIT_SERVICE_UUID_MORE_AVAILABLE, curContent);
                    offset += curLen + 1;
                    break;
                case ScanRecordType.BLE_GAP_AD_TYPE_16BIT_SERVICE_UUID_COMPLETE:
                    scanResult.mBroadcastMap.put(ScanRecordType
                            .BLE_GAP_AD_TYPE_16BIT_SERVICE_UUID_COMPLETE, curContent);
                    offset += curLen + 1;
                    break;
                case ScanRecordType.BLE_GAP_AD_TYPE_32BIT_SERVICE_UUID_MORE_AVAILABLE:
                    scanResult.mBroadcastMap.put(ScanRecordType
                            .BLE_GAP_AD_TYPE_32BIT_SERVICE_UUID_MORE_AVAILABLE, curContent);
                    offset += curLen + 1;
                    break;
                case ScanRecordType.BLE_GAP_AD_TYPE_32BIT_SERVICE_UUID_COMPLETE:
                    scanResult.mBroadcastMap.put(ScanRecordType
                            .BLE_GAP_AD_TYPE_32BIT_SERVICE_UUID_COMPLETE, curContent);
                    offset += curLen + 1;
                    break;
                case ScanRecordType.BLE_GAP_AD_TYPE_128BIT_SERVICE_UUID_MORE_AVAILABLE:
                    scanResult.mBroadcastMap.put(ScanRecordType
                            .BLE_GAP_AD_TYPE_128BIT_SERVICE_UUID_MORE_AVAILABLE, curContent);
                    offset += curLen + 1;
                    break;
                case ScanRecordType.BLE_GAP_AD_TYPE_128BIT_SERVICE_UUID_COMPLETE:
                    scanResult.mBroadcastMap.put(ScanRecordType
                            .BLE_GAP_AD_TYPE_128BIT_SERVICE_UUID_COMPLETE, curContent);
                    offset += curLen + 1;
                    break;
                case ScanRecordType.BLE_GAP_AD_TYPE_SHORT_LOCAL_NAME:
                    scanResult.mBroadcastMap.put(ScanRecordType.BLE_GAP_AD_TYPE_SHORT_LOCAL_NAME,
                            curContent);
                    offset += curLen + 1;
                    break;
                case ScanRecordType.BLE_GAP_AD_TYPE_COMPLETE_LOCAL_NAME:
                    scanResult.mBroadcastMap.put(ScanRecordType
                            .BLE_GAP_AD_TYPE_COMPLETE_LOCAL_NAME, curContent);
                    offset += curLen + 1;
                    break;
                case ScanRecordType.BLE_GAP_AD_TYPE_TX_POWER_LEVEL:
                    scanResult.mBroadcastMap.put(ScanRecordType.BLE_GAP_AD_TYPE_TX_POWER_LEVEL,
                            curContent);
                    offset += curLen + 1;
                    break;
                case ScanRecordType.BLE_GAP_AD_TYPE_CLASS_OF_DEVICE:
                    scanResult.mBroadcastMap.put(ScanRecordType.BLE_GAP_AD_TYPE_CLASS_OF_DEVICE,
                            curContent);
                    offset += curLen + 1;
                    break;
                case ScanRecordType.BLE_GAP_AD_TYPE_SIMPLE_PAIRING_HASH_C:
                    scanResult.mBroadcastMap.put(ScanRecordType
                            .BLE_GAP_AD_TYPE_SIMPLE_PAIRING_HASH_C, curContent);
                    offset += curLen + 1;
                    break;
                case ScanRecordType.BLE_GAP_AD_TYPE_SIMPLE_PAIRING_RANDOMIZER_R:
                    scanResult.mBroadcastMap.put(ScanRecordType
                            .BLE_GAP_AD_TYPE_SIMPLE_PAIRING_RANDOMIZER_R, curContent);
                    offset += curLen + 1;
                    break;
                case ScanRecordType.BLE_GAP_AD_TYPE_SECURITY_MANAGER_TK_VALUE:
                    scanResult.mBroadcastMap.put(ScanRecordType
                            .BLE_GAP_AD_TYPE_SECURITY_MANAGER_TK_VALUE, curContent);
                    offset += curLen + 1;
                    break;
                case ScanRecordType.BLE_GAP_AD_TYPE_SECURITY_MANAGER_OOB_FLAGS:
                    scanResult.mBroadcastMap.put(ScanRecordType
                            .BLE_GAP_AD_TYPE_SECURITY_MANAGER_OOB_FLAGS, curContent);
                    offset += curLen + 1;
                    break;
                case ScanRecordType.BLE_GAP_AD_TYPE_SLAVE_CONNECTION_INTERVAL_RANGE:
                    scanResult.mBroadcastMap.put(ScanRecordType
                            .BLE_GAP_AD_TYPE_SLAVE_CONNECTION_INTERVAL_RANGE, curContent);
                    offset += curLen + 1;
                    break;
                case ScanRecordType.BLE_GAP_AD_TYPE_SOLICITED_SERVICE_UUIDS_16BIT:
                    scanResult.mBroadcastMap.put(ScanRecordType
                            .BLE_GAP_AD_TYPE_SOLICITED_SERVICE_UUIDS_16BIT, curContent);
                    offset += curLen + 1;
                    break;
                case ScanRecordType.BLE_GAP_AD_TYPE_SOLICITED_SERVICE_UUIDS_128BIT:
                    scanResult.mBroadcastMap.put(ScanRecordType
                            .BLE_GAP_AD_TYPE_SOLICITED_SERVICE_UUIDS_128BIT, curContent);
                    offset += curLen + 1;
                    break;
                case ScanRecordType.BLE_GAP_AD_TYPE_SERVICE_DATA:
                    scanResult.mBroadcastMap.put(ScanRecordType.BLE_GAP_AD_TYPE_SERVICE_DATA,
                            curContent);
                    offset += curLen + 1;
                    break;
                case ScanRecordType.BLE_GAP_AD_TYPE_PUBLIC_TARGET_ADDRESS:
                    scanResult.mBroadcastMap.put(ScanRecordType
                            .BLE_GAP_AD_TYPE_PUBLIC_TARGET_ADDRESS, curContent);
                    offset += curLen + 1;
                    break;
                case ScanRecordType.BLE_GAP_AD_TYPE_RANDOM_TARGET_ADDRESS:
                    scanResult.mBroadcastMap.put(ScanRecordType
                            .BLE_GAP_AD_TYPE_RANDOM_TARGET_ADDRESS, curContent);
                    offset += curLen + 1;
                    break;
                case ScanRecordType.BLE_GAP_AD_TYPE_APPEARANCE:
                    scanResult.mBroadcastMap.put(ScanRecordType.BLE_GAP_AD_TYPE_APPEARANCE,
                            curContent);
                    offset += curLen + 1;
                    break;
                case ScanRecordType.BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA:
                    scanResult.mBroadcastMap.put(ScanRecordType
                            .BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA, curContent);
                    offset += curLen + 1;
                    break;
                default:
                    i = scanRecord.length;
                    break;
            }
        }

    }


    @Override
    public void onScanTimeout() {
        ScanResult[] results = new ScanResult[resultList.size()];
        for (int i = 0; i < results.length; i++) {
            results[i] = resultList.get(i);
        }
        onScanComplete(results);
    }

    @Override
    public void onScanCancel() {
        ScanResult[] resultArr = resultList.toArray(new ScanResult[resultList.size()]);
        onScanComplete(resultArr);
    }

//    public abstract void onScanning(ScanResult result);

    public abstract void onScanComplete(ScanResult[] results);

}

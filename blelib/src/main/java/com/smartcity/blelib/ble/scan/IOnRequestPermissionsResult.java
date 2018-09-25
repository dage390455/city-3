package com.smartcity.blelib.ble.scan;

/**
 * Created by lianxiang on 2017/9/14.
 * 权限回调
 */

public interface IOnRequestPermissionsResult {
    void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults);
}

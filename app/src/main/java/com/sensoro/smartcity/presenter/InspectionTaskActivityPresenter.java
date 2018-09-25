package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.sensoro.libbleserver.ble.BLEDevice;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceListener;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.InspectionActivity;
import com.sensoro.smartcity.activity.InspectionExceptionDetailActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IInspectionTaskActivityView;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.SensoroToast;

import java.util.ArrayList;

public class InspectionTaskActivityPresenter extends BasePresenter<IInspectionTaskActivityView> implements
        BLEDeviceListener<BLEDevice> {
    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        initBle();
    }

    @Override
    public void onDestroy() {
        stopScan();
    }

    private void initBle() {
        SensoroCityApplication.getInstance().bleDeviceManager.setBLEDeviceListener(this);
        startScan();
    }

    public void startScan() {
        try {
            boolean isEnable = SensoroCityApplication.getInstance().bleDeviceManager.startService();
            if (!isEnable) {
                SensoroToast.INSTANCE.makeText("未开启蓝牙", Toast.LENGTH_SHORT).show();
            } else {
                SensoroCityApplication.getInstance().bleDeviceManager.setBackgroundMode(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void stopScan() {
        try {
            SensoroCityApplication.getInstance().bleDeviceManager.stopService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doItemClick(int state) {
        Intent intent = new Intent();
        switch (state) {
            case 0:
                intent.setClass(mContext, InspectionActivity.class);
                break;
            case 1:
                intent.setClass(mContext, InspectionExceptionDetailActivity.class);
                break;
        }
        getView().startAC(intent);
    }

    @Override
    public void onNewDevice(BLEDevice bleDevice) {
        if (bleDevice != null) {
            LogUtils.loge("onNewDevice = " + bleDevice.getSn());
        }
    }

    @Override
    public void onGoneDevice(BLEDevice bleDevice) {
        if (bleDevice != null) {
            LogUtils.loge("onGoneDevice = " + bleDevice.getSn());
        }
    }

    @Override
    public void onUpdateDevices(ArrayList<BLEDevice> deviceList) {
        String temp = "";
        if (deviceList != null && deviceList.size() > 0) {
            for (BLEDevice device : deviceList) {
                if (device != null) {
                    temp += device.getSn() + ",";
                }
            }
        }
        LogUtils.loge("onUpdateDevices = " + temp);

    }
}

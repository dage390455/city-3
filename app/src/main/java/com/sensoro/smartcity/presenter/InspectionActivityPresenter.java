package com.sensoro.smartcity.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.sensoro.libbleserver.ble.BLEDevice;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceListener;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.InspectionTaskDetailActivity;
import com.sensoro.smartcity.activity.InspectionUploadExceptionActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IInspectionActivityView;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.SensoroToast;

import java.util.ArrayList;

public class InspectionActivityPresenter extends BasePresenter<IInspectionActivityView> implements
        BLEDeviceListener<BLEDevice>, Constants {
    private Context mContext;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private long startTime;

    @Override
    public void initData(Context context) {
        mContext = context;
        startTime = System.currentTimeMillis();
        //临时数据
        ArrayList<String> list = new ArrayList<>();
        list.add("5");
        list.add("望京soho");
        getView().updateTagsData(list);
        initBle();
        startScan();
    }

    private void initBle() {
        SensoroCityApplication.getInstance().bleDeviceManager.setBLEDeviceListener(this);
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

    @Override
    public void onDestroy() {
        stopScan();
        mHandler.removeCallbacksAndMessages(null);
    }

    public void doInspectionDetail() {
        Intent intent = new Intent(mContext, InspectionTaskDetailActivity.class);
        //todo 跳转巡检内容，加参数
        getView().startAC(intent);
    }

    public void doUploadException() {
        Intent intent = new Intent(mContext, InspectionUploadExceptionActivity.class);
        intent.putExtra(EXTRA_INSPECTION_START_TIME, startTime);
        getView().startAC(intent);
    }

    public void doNormal() {
        getView().showNormalDialog();
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

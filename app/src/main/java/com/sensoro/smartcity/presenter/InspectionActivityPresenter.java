package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.sensoro.libbleserver.ble.BLEDevice;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceListener;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.InspectionInstructionActivity;
import com.sensoro.smartcity.activity.InspectionTaskDetailActivity;
import com.sensoro.smartcity.activity.InspectionUploadExceptionActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IInspectionActivityView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.DeviceTypeModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.bean.InspectionTaskDeviceDetail;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.SensoroToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class InspectionActivityPresenter extends BasePresenter<IInspectionActivityView> implements
        BLEDeviceListener<BLEDevice>, IOnCreate, Constants, Runnable {
    private Activity mContext;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private long startTime;
    private InspectionTaskDeviceDetail mDeviceDetail;
    private HashSet<String> tempBleDevice = new HashSet<>();
    private boolean hasBleDevice = false;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        startTime = System.currentTimeMillis();
        //临时数据
        mDeviceDetail = (InspectionTaskDeviceDetail) mContext.getIntent().getSerializableExtra(EXTRA_INSPECTION_TASK_ITEM_DEVICE_DETAIL);
        if (mDeviceDetail != null) {
            List<String> tags = mDeviceDetail.getTags();
            getView().updateTagsData(tags);
            String name = mDeviceDetail.getName();
            String sn = mDeviceDetail.getSn();
            DeviceTypeModel model = SensoroCityApplication.getInstance().getDeviceTypeName(mDeviceDetail.getUnionType());
            if (!TextUtils.isEmpty(name)) {
                getView().setMonitorTitle(name);
            }
            if (!TextUtils.isEmpty(sn)) {
                if(model!=null){
                    getView().setMonitorSn(model.name + " " + sn);
                }else{
                    getView().setMonitorSn("未知 " + sn);
                }

            }
            initBle();
            startScan();
            mHandler.post(this);
        }

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
        EventBus.getDefault().unregister(this);
        stopScan();
        mHandler.removeCallbacksAndMessages(null);
    }

    public void doInspectionInstruction() {
        Intent intent = new Intent(mContext, InspectionInstructionActivity.class);
        ArrayList<String> deviceTypes = new ArrayList<>();
        deviceTypes.add(mDeviceDetail.getDeviceType());
        intent.putExtra(Constants.EXTRA_INSPECTION_INSTRUCTION_DEVICE_TYPE,deviceTypes);
        getView().startAC(intent);
    }

    public void doUploadException() {
        Intent intent = new Intent(mContext, InspectionUploadExceptionActivity.class);
        intent.putExtra(EXTRA_INSPECTION_TASK_ITEM_DEVICE_DETAIL,mDeviceDetail);
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
            if (!tempBleDevice.contains(bleDevice.getSn())) {
                tempBleDevice.add(bleDevice.getSn());
            }
        }
    }

    @Override
    public void onGoneDevice(BLEDevice bleDevice) {
        if (bleDevice != null) {
            LogUtils.loge("onGoneDevice = " + bleDevice.getSn());
            if (tempBleDevice.contains(bleDevice.getSn())) {
                tempBleDevice.remove(bleDevice.getSn());
            }
        }
    }

    @Override
    public void onUpdateDevices(ArrayList<BLEDevice> deviceList) {
        String temp = "";
        if (deviceList != null && deviceList.size() > 0) {
            for (BLEDevice device : deviceList) {
                if (device != null) {
                    temp += device.getSn() + ",";
                    if (!tempBleDevice.contains(device.getSn())) {
                        tempBleDevice.add(device.getSn());
                    }
                }
            }
        }
        LogUtils.loge("onUpdateDevices = " + temp);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        //TODO 可以修改以此种方式传递，方便管理
        int code = eventData.code;
        Object data = eventData.data;
        //上报异常结果成功
        if (code == EVENT_DATA_INSPECTION_UPLOAD_EXCEPTION_CODE) {
            getView().finishAc();
        }
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void run() {
        if (hasBleDevice) {
            return;
        }
//        String sn = mDeviceDetail.getSn();
        String sn = "02700017C6445B3B";
        if (tempBleDevice.contains(sn)) {
            hasBleDevice = true;
            getView().setConfirmState(hasBleDevice);
        }
        mHandler.postDelayed(this, 2 * 1000);
    }
}

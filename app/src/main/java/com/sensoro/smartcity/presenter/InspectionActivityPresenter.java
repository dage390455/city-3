package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.sensoro.libbleserver.ble.BLEDevice;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceListener;
import com.sensoro.smartcity.activity.InspectionInstructionActivity;
import com.sensoro.smartcity.activity.InspectionUploadExceptionActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IInspectionActivityView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.iwidget.IOnStart;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.InspectionTaskDeviceDetail;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.util.BleObserver;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InspectionActivityPresenter extends BasePresenter<IInspectionActivityView> implements
        BLEDeviceListener<BLEDevice>, IOnCreate, Constants, IOnStart, Runnable {
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
        mDeviceDetail = (InspectionTaskDeviceDetail) mContext.getIntent().getSerializableExtra(EXTRA_INSPECTION_TASK_ITEM_DEVICE_DETAIL);
        if (mDeviceDetail != null) {
            List<String> tags = mDeviceDetail.getTags();
            getView().updateTagsData(tags);
            String name = mDeviceDetail.getName();
            String sn = mDeviceDetail.getSn();
            String deviceType = mDeviceDetail.getDeviceType();
            if (!TextUtils.isEmpty(name)) {
                getView().setMonitorTitle(name);
            }
            if (!TextUtils.isEmpty(sn)) {
                getView().setMonitorSn(deviceType + " " + sn);
            }
            mHandler.post(this);
        }

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
    }

    public void doInspectionInstruction() {
        Intent intent = new Intent(mContext, InspectionInstructionActivity.class);
        intent.putExtra(Constants.EXTRA_INSPECTION_INSTRUCTION_DEVICE_TYPE, mDeviceDetail.getDeviceType());
        getView().startAC(intent);
    }

    public void doUploadException() {
        Intent intent = new Intent(mContext, InspectionUploadExceptionActivity.class);
        intent.putExtra(EXTRA_INSPECTION_TASK_ITEM_DEVICE_DETAIL, mDeviceDetail);
        intent.putExtra(EXTRA_INSPECTION_START_TIME, startTime);
        getView().startAC(intent);
    }

    public void doUploadNormal() {
        getView().showProgressDialog();
        long finishTime = System.currentTimeMillis();
        RetrofitServiceHelper.INSTANCE.doUploadInspectionResult(mDeviceDetail.getId(), null, null, 1, null, startTime, finishTime, null,
                null, null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<ResponseBase>(this) {
                    @Override
                    public void onCompleted(ResponseBase responseBase) {
                        if (responseBase.getErrcode() == 0) {
                            getView().toastShort("上报成功");
                            EventData eventData = new EventData();
                            eventData.code = EVENT_DATA_INSPECTION_UPLOAD_NORMAL_CODE;
                            EventBus.getDefault().post(eventData);
                            getView().finishAc();
                        } else {
                            getView().toastShort("上报失败");
                        }
                        getView().dismissProgressDialog();
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().toastShort(errorMsg);
                        Log.e("hcs", ":错误了::" + errorMsg);
                        getView().dismissProgressDialog();
                    }
                });
    }

    @Override
    public void onNewDevice(BLEDevice bleDevice) {
        if (!tempBleDevice.contains(bleDevice.getSn())) {
            tempBleDevice.add(bleDevice.getSn());
        }
    }

    @Override
    public void onGoneDevice(BLEDevice bleDevice) {
        if (tempBleDevice.contains(bleDevice.getSn())) {
            tempBleDevice.remove(bleDevice.getSn());
        }
    }

    @Override
    public void onUpdateDevices(ArrayList<BLEDevice> deviceList) {
        for (BLEDevice device : deviceList) {
            if (device != null) {
                if (!tempBleDevice.contains(device.getSn())) {
                    tempBleDevice.add(device.getSn());
                }
            }
        }

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

    @Override
    public void onStart() {
        BleObserver.getInstance().registerBleObserver(this);
    }

    @Override
    public void onStop() {
        BleObserver.getInstance().unregisterBleObserver(this);
    }
}

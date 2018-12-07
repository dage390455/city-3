package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.sensoro.libbleserver.ble.BLEDevice;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceListener;
import com.sensoro.smartcity.R;
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
import com.sensoro.smartcity.util.WidgetUtil;

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
            if (!TextUtils.isEmpty(name)) {
                getView().setMonitorTitle(name);
            }
            if (!TextUtils.isEmpty(sn)) {
                String inspectionDeviceName = WidgetUtil.getInspectionDeviceName(mDeviceDetail.getDeviceType());
                getView().setMonitorSn(inspectionDeviceName + " " + sn);
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
        ArrayList<String> deviceTypes = new ArrayList<>();
        deviceTypes.add(mDeviceDetail.getDeviceType());
        intent.putExtra(Constants.EXTRA_INSPECTION_INSTRUCTION_DEVICE_TYPE, deviceTypes);
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
                            getView().toastShort(mContext.getString(R.string.successful_report));
                            EventData eventData = new EventData();
                            eventData.code = EVENT_DATA_INSPECTION_UPLOAD_NORMAL_CODE;
                            EventBus.getDefault().post(eventData);
                            getView().finishAc();
                        } else {
                            getView().toastShort(mContext.getString(R.string.report_failure));
                        }
                        getView().dismissProgressDialog();
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().toastShort(errorMsg);
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
                tempBleDevice.add(device.getSn());
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        //上报异常结果成功
        switch (code) {
            case EVENT_DATA_INSPECTION_UPLOAD_EXCEPTION_CODE:
            case EVENT_DATA_DEPLOY_RESULT_FINISH:
            case EVENT_DATA_DEPLOY_RESULT_CONTINUE:
                getView().finishAc();
                break;
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
        String sn = mDeviceDetail.getSn();
//        String sn = "02700017C6445B3B";
        if (tempBleDevice.contains(sn)) {
            hasBleDevice = true;
            getView().setConfirmState(hasBleDevice);
        }
        mHandler.postDelayed(this, 1 * 1000);
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

package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.iwidget.IOnStart;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.InspectionTaskDeviceDetail;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.utils.WidgetUtil;
import com.sensoro.inspectiontask.R;
import com.sensoro.libbleserver.ble.entity.BLEDevice;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceListener;
import com.sensoro.smartcity.activity.InspectionInstructionActivity;
import com.sensoro.smartcity.activity.InspectionUploadExceptionActivity;
import com.sensoro.common.callback.BleObserver;
import com.sensoro.smartcity.imainviews.IInspectionActivityView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class InspectionActivityPresenter extends BasePresenter<IInspectionActivityView> implements
        BLEDeviceListener<BLEDevice>, IOnCreate, IOnStart, Runnable {
    private Activity mContext;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private long startTime;
    private InspectionTaskDeviceDetail mDeviceDetail;
    private final HashMap<String, BLEDevice> tempBleDevice = new HashMap<>();

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        startTime = System.currentTimeMillis();
        mDeviceDetail = (InspectionTaskDeviceDetail) mContext.getIntent().getSerializableExtra(Constants.EXTRA_INSPECTION_TASK_ITEM_DEVICE_DETAIL);
        mHandler.post(this);
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
        }

    }

    @Override
    public void onDestroy() {
        BleObserver.getInstance().unregisterBleObserver(this);
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
        intent.putExtra(Constants.EXTRA_INSPECTION_TASK_ITEM_DEVICE_DETAIL, mDeviceDetail);
        intent.putExtra(Constants.EXTRA_INSPECTION_START_TIME, startTime);
        getView().startAC(intent);
    }

    public void doUploadNormal() {
        getView().showProgressDialog();
        long finishTime = System.currentTimeMillis();
        RetrofitServiceHelper.getInstance().doUploadInspectionResult(mDeviceDetail.getId(), null, null, 1, null, startTime, finishTime, null,
                null, null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<ResponseResult>(this) {
                    @Override
                    public void onCompleted(ResponseResult responseBase) {
                        if (responseBase.getErrcode() == 0) {
                            getView().toastShort(mContext.getString(R.string.successful_report));
                            EventData eventData = new EventData();
                            eventData.code = Constants.EVENT_DATA_INSPECTION_UPLOAD_NORMAL_CODE;
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
        tempBleDevice.put(bleDevice.getSn(), bleDevice);
    }

    @Override
    public void onGoneDevice(BLEDevice bleDevice) {
        tempBleDevice.remove(bleDevice.getSn());
    }

    @Override
    public void onUpdateDevices(ArrayList<BLEDevice> deviceList) {
        for (BLEDevice device : deviceList) {
            if (device != null) {
                tempBleDevice.put(device.getSn(), device);
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        //上报异常结果成功
        switch (code) {
            case Constants.EVENT_DATA_INSPECTION_UPLOAD_EXCEPTION_CODE:
            case Constants.EVENT_DATA_DEPLOY_RESULT_FINISH:
            case Constants.EVENT_DATA_DEPLOY_RESULT_CONTINUE:
                getView().finishAc();
                break;
        }
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
        BleObserver.getInstance().registerBleObserver(this);
    }

    @Override
    public void run() {
        String sn = mDeviceDetail.getSn();
//        String sn="01A01117C62B0311";
        getView().setConfirmState(tempBleDevice.containsKey(sn));
        mHandler.postDelayed(this, 1 * 1000);
    }

    @Override
    public void onStart() {
        //todo 两个几面跳转 暂时去掉
//        ContextUtils.getContext().bleDeviceManager.startScan();
    }

    @Override
    public void onStop() {
//        ContextUtils.getContext().bleDeviceManager.stopScan();
    }
}

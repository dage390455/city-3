package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.DeployMonitorDetailActivity;
import com.sensoro.smartcity.activity.DeployResultActivity;
import com.sensoro.smartcity.activity.InspectionActivity;
import com.sensoro.smartcity.activity.InspectionExceptionDetailActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployManualActivityView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.InspectionIndexTaskInfo;
import com.sensoro.smartcity.server.bean.InspectionTaskDeviceDetail;
import com.sensoro.smartcity.server.bean.InspectionTaskDeviceDetailModel;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.InspectionTaskDeviceDetailRsp;
import com.sensoro.smartcity.util.DeployAnalyzerUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DeployManualActivityPresenter extends BasePresenter<IDeployManualActivityView> implements IOnCreate,
        Constants {
    private Activity mContext;
    private int scanType = -1;
    private InspectionIndexTaskInfo mTaskInfo;
    private InspectionTaskDeviceDetail mDeviceDetail;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        scanType = mContext.getIntent().getIntExtra(EXTRA_SCAN_ORIGIN_TYPE, -1);
        mTaskInfo = (InspectionIndexTaskInfo) mContext.getIntent().getSerializableExtra(EXTRA_INSPECTION_INDEX_TASK_INFO);
        mDeviceDetail = (InspectionTaskDeviceDetail) mContext.getIntent().getSerializableExtra(EXTRA_INSPECTION_DEPLOY_OLD_DEVICE_INFO);
    }

    public void clickNext(String text) {
        if (!TextUtils.isEmpty(text) && text.length() == 16) {
//            Intent intent = new Intent(this, DeployActivity.class);
//            intent.putExtra(EXTRA_SENSOR_SN, contentEditText.getText().toString().toUpperCase());
//            startActivity(intent);
            requestData(text);
        } else {
            getView().toastShort("请输入正确的SN,SN为16个字符");
        }
    }

    private void requestData(final String scanSerialNumber) {

        if (TextUtils.isEmpty(scanSerialNumber)) {
            getView().toastShort(mContext.getResources().getString(R.string.invalid_qr_code));
        } else {
            switch (scanType) {
                case TYPE_SCAN_DEPLOY_STATION:
                case TYPE_SCAN_DEPLOY_DEVICE:
                    getView().showProgressDialog();
                    DeployAnalyzerUtils.INSTANCE.getDeployAnalyzerResult(scanSerialNumber.toUpperCase(), mContext, new DeployAnalyzerUtils.OnDeployAnalyzerListener() {
                        @Override
                        public void onSuccess(Intent intent) {
                            getView().dismissProgressDialog();
                            getView().startAC(intent);
                        }

                        @Override
                        public void onError(int errType, Intent intent, String errMsg) {
                            getView().dismissProgressDialog();
                            if (intent != null) {
                                getView().startAC(intent);
                            } else {
                                getView().toastShort(errMsg);
                            }
                        }
                    });
                    break;
                case TYPE_SCAN_DEPLOY_DEVICE_CHANGE:
                    changeDevice(scanSerialNumber);
                    break;
                case TYPE_SCAN_INSPECTION:
                    scanInspectionDevice(scanSerialNumber);
                    break;
                default:
                    break;
            }

        }
    }

    private void changeDevice(final String scanSnNewDevice) {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getDeviceDetailInfoList(scanSnNewDevice.toUpperCase(), null, 1).subscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>() {
            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                    getView().toastShort(errorMsg);
                } else if (errorCode == 4013101 || errorCode == 4000013) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, DeployResultActivity.class);
                    intent.putExtra(EXTRA_SENSOR_RESULT, -1);
                    intent.putExtra(EXTRA_SENSOR_SN_RESULT, scanSnNewDevice);
                    intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, TYPE_SCAN_DEPLOY_DEVICE_CHANGE);
                    getView().startAC(intent);
                } else {
                    //TODO 控制逻辑
                    Intent intent = new Intent();
                    intent.setClass(mContext, DeployResultActivity.class);
                    intent.putExtra(EXTRA_SENSOR_RESULT, -1);
                    intent.putExtra(EXTRA_SENSOR_SN_RESULT, scanSnNewDevice);
                    intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, TYPE_SCAN_DEPLOY_DEVICE_CHANGE);
                    intent.putExtra(EXTRA_SENSOR_RESULT_ERROR, errorMsg);
                    getView().startAC(intent);
                }
            }

            @Override
            public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {
                getView().dismissProgressDialog();
                try {
                    if (deviceInfoListRsp.getData().size() > 0) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, DeployMonitorDetailActivity.class);
                        intent.putExtra(EXTRA_DEVICE_INFO, deviceInfoListRsp.getData().get(0));
                        intent.putExtra(EXTRA_INSPECTION_DEPLOY_OLD_DEVICE_INFO, mDeviceDetail);
                        intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, TYPE_SCAN_DEPLOY_DEVICE_CHANGE);
                        intent.putExtra("uid", mContext.getIntent().getStringExtra("uid"));
                        getView().startAC(intent);
                    } else {
                        Intent intent = new Intent();
                        intent.setClass(mContext, DeployResultActivity.class);
                        intent.putExtra(EXTRA_SENSOR_RESULT, -1);
                        intent.putExtra(EXTRA_SENSOR_SN_RESULT, scanSnNewDevice);
                        intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, TYPE_SCAN_DEPLOY_DEVICE_CHANGE);
                        getView().startAC(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void scanInspectionDevice(String scanInspectionDevice) {
        getView().showProgressDialog();
        //TODO 暂时处理
        RetrofitServiceHelper.INSTANCE.getInspectionDeviceList(mTaskInfo.getId(), null, scanInspectionDevice.toUpperCase(), null, null, null, null).
                subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<InspectionTaskDeviceDetailRsp>(this) {
            @Override
            public void onCompleted(InspectionTaskDeviceDetailRsp inspectionTaskDeviceDetailRsp) {
                getView().dismissProgressDialog();
                InspectionTaskDeviceDetailModel data = inspectionTaskDeviceDetailRsp.getData();
                List<InspectionTaskDeviceDetail> devices = data.getDevices();
                if (devices != null && devices.size() > 0) {
                    InspectionTaskDeviceDetail deviceDetail = devices.get(0);
                    Intent intent = new Intent();
                    int status = deviceDetail.getStatus();
                    switch (status) {
                        case 0:
                            intent.setClass(mContext, InspectionActivity.class);
                            break;
                        case 1:
                        case 2:
                            intent.setClass(mContext, InspectionExceptionDetailActivity.class);
                            break;
                    }
                    intent.putExtra(EXTRA_INSPECTION_TASK_ITEM_DEVICE_DETAIL, deviceDetail);
                    getView().startAC(intent);
                } else {
                    getView().toastShort("未查找到此巡检设备");
                }

            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
//        RetrofitServiceHelper.INSTANCE.getInspectionDeviceDetail(null, scanInspectionDevice, mTaskInfo.getId(), 1)
//                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<InspectionTaskExceptionDeviceRsp>() {
//            @Override
//            public void onCompleted(InspectionTaskExceptionDeviceRsp response) {
//                getView().dismissProgressDialog();
//                InspectionTaskExceptionDeviceModel taskDevice = response.getData();
//                int status = taskDevice.getStatus();
//                Intent intent = new Intent();
//                String deviceType = taskDevice.getDeviceType();
//                String sn = taskDevice.getSn();
//                String taskId = taskDevice.getTaskId();
//                InspectionTaskExceptionDeviceModel.DeviceBean device = taskDevice.getDevice();
//                String name = device.getName();
//                List<Double> lonlat = device.getLonlat();
//                List<String> tags = device.getTags();
//                String id = taskDevice.get_id();
////                private String name;
////                private String taskId;
////                private String sn;
////                private String deviceType;
////                private int status;
////                private String timecost;
////                private InspectionTaskDeviceDetail.MalfunctionBean malfunction;
////                private List<Double> lonlat;
////                private List<String> tags;
//
//                InspectionTaskDeviceDetail deviceDetail = new InspectionTaskDeviceDetail();
//                deviceDetail.setId(id);
//                deviceDetail.setDeviceType(deviceType);
//                deviceDetail.setLonlat(lonlat);
//                deviceDetail.setSn(sn);
//                deviceDetail.setTaskId(taskId);
//                deviceDetail.setName(name);
//                deviceDetail.setTags(tags);
//                deviceDetail.setStatus(status);
//                switch (status) {
//                    case 0:
//                        intent.setClass(mContext, InspectionActivity.class);
//                        break;
//                    case 1:
//                    case 2:
//                        intent.setClass(mContext, InspectionExceptionDetailActivity.class);
//                        break;
//                }
//                intent.putExtra(EXTRA_INSPECTION_TASK_ITEM_DEVICE_DETAIL, deviceDetail);
//                getView().startAC(intent);
//
//            }
//
//            @Override
//            public void onErrorMsg(int errorCode, String errorMsg) {
//                getView().dismissProgressDialog();
//                getView().toastShort(errorMsg);
//            }
//        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        //TODO 可以修改以此种方式传递，方便管理
        int code = eventData.code;
        if (code == EVENT_DATA_DEPLOY_RESULT_FINISH || code == EVENT_DATA_DEPLOY_RESULT_CONTINUE) {
            getView().finishAc();
        }
//        LogUtils.loge(this, eventData.toString());
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }
}

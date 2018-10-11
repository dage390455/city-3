package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.amap.api.maps.model.LatLng;
import com.sensoro.libbleserver.ble.BLEDevice;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceListener;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.InspectionActivity;
import com.sensoro.smartcity.activity.InspectionExceptionDetailActivity;
import com.sensoro.smartcity.activity.ScanActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IInspectionTaskActivityView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.iwidget.IOnStart;
import com.sensoro.smartcity.model.DeviceTypeModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.model.InspectionStatusCountModel;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.InspectionIndexTaskInfo;
import com.sensoro.smartcity.server.bean.InspectionTaskDeviceDetail;
import com.sensoro.smartcity.server.bean.InspectionTaskExecutionModel;
import com.sensoro.smartcity.server.response.InspectionTaskDeviceDetailRsp;
import com.sensoro.smartcity.server.response.InspectionTaskExecutionRsp;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.BleObserver;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InspectionTaskActivityPresenter extends BasePresenter<IInspectionTaskActivityView> implements
        BLEDeviceListener<BLEDevice>, IOnCreate, IOnStart, Constants, Runnable {
    private Activity mContext;
    private String tempSearch;
    private String tempDeviceType = null;
    private int cur_page = 0;
    private int finish = 2;
    private final List<InspectionTaskDeviceDetail> mDevices = new ArrayList<>();
    private static final HashSet<String> BLE_DEVICE_SET = new HashSet<>();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private volatile boolean canFreshBle = true;
    private InspectionIndexTaskInfo mTaskInfo;
    private volatile boolean bleHasOpen = false;
    private final List<String> selectDeviceList = new ArrayList<>();

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        //TODO 筛选类型
        mTaskInfo = (InspectionIndexTaskInfo) mContext.getIntent().getSerializableExtra(EXTRA_INSPECTION_INDEX_TASK_INFO);
        if (mTaskInfo != null) {
            requestSearchData(DIRECTION_DOWN, null);
            mHandler.post(this);
        }

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
        stopScan();
        mDevices.clear();
        BLE_DEVICE_SET.clear();
        selectDeviceList.clear();

    }

    private void stopScan() {
        try {
            SensoroCityApplication.getInstance().bleDeviceManager.stopService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doItemClick(int position, int status) {
        Intent intent = new Intent();
        InspectionTaskDeviceDetail deviceDetail = mDevices.get(position);
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
    }

    @Override
    public void onNewDevice(BLEDevice bleDevice) {
        BLE_DEVICE_SET.add(bleDevice.getSn());
    }

    @Override
    public void onGoneDevice(BLEDevice bleDevice) {
        try {
            if (BLE_DEVICE_SET.contains(bleDevice.getSn())) {
                BLE_DEVICE_SET.remove(bleDevice.getSn());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateDevices(ArrayList<BLEDevice> deviceList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (BLEDevice device : deviceList) {
            if (device != null) {
                stringBuilder.append(device.getSn()).append(",");
                BLE_DEVICE_SET.add(device.getSn());
            }
        }
        LogUtils.loge("onUpdateDevices = " + stringBuilder.toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        //TODO 可以修改以此种方式传递，方便管理
        int code = eventData.code;
        Object data = eventData.data;
        //上报异常结果成功
        if (code == EVENT_DATA_INSPECTION_UPLOAD_EXCEPTION_CODE) {
            //TODO 刷新上报异常结果
            requestSearchData(DIRECTION_DOWN, tempSearch);
        } else if (code == EVENT_DATA_INSPECTION_UPLOAD_NORMAL_CODE) {
            //TODO 正常上报结果
            requestSearchData(DIRECTION_DOWN, tempSearch);
        } else if (code == EVENT_DATA_DEPLOY_RESULT_FINISH) {
            getView().finishAc();
        } else if (code == EVENT_DATA_DEPLOY_RESULT_CONTINUE) {
            //TODO 设备更换结果
            requestSearchData(DIRECTION_DOWN, tempSearch);
        }
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    public void doInspectionStatus(final boolean needPop) {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getInspectTaskExecution(mTaskInfo.getId()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<InspectionTaskExecutionRsp>(this) {
            @Override
            public void onCompleted(InspectionTaskExecutionRsp inspectionTaskExecutionRsp) {
                InspectionTaskExecutionModel data = inspectionTaskExecutionRsp.getData();
                List<InspectionTaskExecutionModel.DeviceStatusBean> deviceStatus = data.getDeviceStatus();
                if (deviceStatus != null) {
                    int uncheck = 0;
                    int normalNum = 0;
                    int abnormalNum = 0;
                    for (InspectionTaskExecutionModel.DeviceStatusBean deviceStatusBean : deviceStatus) {
                        switch (deviceStatusBean.getType()) {
                            case "uncheck":
                                uncheck = deviceStatusBean.getNum();
                                break;
                            case "normal":
                                normalNum = deviceStatusBean.getNum();
                                break;
                            case "abnormal":
                                abnormalNum = deviceStatusBean.getNum();
                                break;
                        }
                    }
                    List<InspectionStatusCountModel> list = new ArrayList<>();
                    InspectionStatusCountModel sc1 = new InspectionStatusCountModel();
                    sc1.count = uncheck + normalNum + abnormalNum;
                    sc1.statusTitle = "全部状态";
                    sc1.status = 2;
                    list.add(sc1);
                    InspectionStatusCountModel sc2 = new InspectionStatusCountModel();
                    sc2.count = uncheck;
                    sc2.statusTitle = "未巡检";
                    sc2.status = 0;
                    list.add(sc2);
                    InspectionStatusCountModel sc3 = new InspectionStatusCountModel();
                    int check = normalNum + abnormalNum;
                    sc3.count = check;
                    sc3.statusTitle = "已巡检";
                    sc3.status = 1;
                    list.add(sc3);
                    if (needPop) {
                        getView().updateSelectDeviceStatusList(list);
                    }
                    getView().dismissProgressDialog();
                    getView().setBottomInspectionStateTitle("未巡检： " + uncheck, "我已巡检： " + check);
                }


            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);

            }
        });
    }

    public void doInspectionType(final boolean needPop) {
        if (selectDeviceList.size() > 0) {
            getView().updateSelectDeviceTypeList(selectDeviceList);
            getView().showSelectDeviceTypePop();
            return;
        }
        if (needPop) {
            getView().showProgressDialog();
        }
        RetrofitServiceHelper.INSTANCE.getInspectTaskExecution(mTaskInfo.getId()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<InspectionTaskExecutionRsp>(this) {
            @Override
            public void onCompleted(InspectionTaskExecutionRsp inspectionTaskExecutionRsp) {
                InspectionTaskExecutionModel data = inspectionTaskExecutionRsp.getData();
                List<InspectionTaskExecutionModel.DeviceTypesBean> deviceTypes = data.getDeviceTypes();
                if (deviceTypes != null) {
//                    ArrayList<DeviceTypeModel> types = new ArrayList<>();
                    for (InspectionTaskExecutionModel.DeviceTypesBean deviceTypesBean : deviceTypes) {
//                        String deviceType = deviceTypesBean.getDeviceType();
////
////                        List<DeviceTypeMutualModel.MergeTypeInfosBean> mergeTypeInfos = SensoroCityApplication.getInstance().mDeviceTypeMutualModel.getMergeTypeInfos();
////                        for (DeviceTypeMutualModel.MergeTypeInfosBean mergeTypeInfo : mergeTypeInfos) {
////                            if (mergeTypeInfo.getDeviceTypes().contains(deviceType)) {
////                                DeviceTypeModel deviceTypeModel = SensoroCityApplication.getInstance().getDeviceTypeName(mergeTypeInfo.getMergeType());
////                                if (deviceTypeModel != null) {
////                                    deviceTypeModel.deviceTypes = mergeTypeInfo.getDeviceTypes();
////                                    types.add(deviceTypeModel);
////                                }
////                                break;
////                            }
////                        }
//                        LogUtils.loge("doInspectionType --->>> " + deviceType);
                        selectDeviceList.add(deviceTypesBean.getDeviceType());
                    }
//                    types.add(0, SensoroCityApplication.getInstance().mDeviceTypeList.get(0));
                    getView().updateSelectDeviceTypeList(selectDeviceList);
                    if (needPop) {
                        getView().showSelectDeviceTypePop();
                    }
                } else {
                    if (needPop) {
                        getView().toastShort("好像出问题了");
                    }

                }
                getView().dismissProgressDialog();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                if (needPop) {
                    getView().toastShort(errorMsg);
                }
            }
        });
    }

    public void requestSearchData(final int direction, String searchText) {
        if (PreferencesHelper.getInstance().getUserData().isSupperAccount) {
            return;
        }
        if (TextUtils.isEmpty(searchText)) {
            tempSearch = null;
        } else {
            tempSearch = searchText;
        }
        canFreshBle = false;
        switch (direction) {
            case DIRECTION_DOWN:
                cur_page = 0;
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getInspectionDeviceList(mTaskInfo.getId(), tempSearch, null, finish, tempDeviceType, cur_page * 15, 15).
                        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<InspectionTaskDeviceDetailRsp>(this) {
                    @Override
                    public void onCompleted(InspectionTaskDeviceDetailRsp inspectionTaskDeviceDetailRsp) {
                        getView().dismissProgressDialog();
                        freshUI(direction, inspectionTaskDeviceDetailRsp);
                        getView().onPullRefreshComplete();
                        canFreshBle = true;
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().onPullRefreshComplete();
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                        canFreshBle = true;
                    }
                });
                doInspectionStatus(false);
                break;
            case DIRECTION_UP:
                cur_page++;
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getInspectionDeviceList(mTaskInfo.getId(), tempSearch, null, finish, tempDeviceType, cur_page * 15, 15).
                        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<InspectionTaskDeviceDetailRsp>(this) {
                    @Override
                    public void onCompleted(InspectionTaskDeviceDetailRsp inspectionTaskDeviceDetailRsp) {
                        if (inspectionTaskDeviceDetailRsp.getData().getDevices().size() == 0) {
                            getView().toastShort("没有更多数据了");
                            getView().onPullRefreshCompleteNoMoreData();
                            cur_page--;
                        } else {
                            freshUI(direction, inspectionTaskDeviceDetailRsp);
                            getView().onPullRefreshComplete();
                        }
                        getView().dismissProgressDialog();
                        canFreshBle = true;
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        cur_page--;
                        getView().onPullRefreshComplete();
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                        canFreshBle = true;
                    }
                });
                break;
            default:
                break;
        }
    }

    public void doCancelSearch() {
        tempSearch = null;
        requestSearchData(DIRECTION_DOWN, null);
    }

    private void freshUI(int direction, InspectionTaskDeviceDetailRsp inspectionTaskDeviceDetailRsp) {
        if (direction == DIRECTION_DOWN) {
            mDevices.clear();
        }
//        handleDeviceAlarmLogs(deviceAlarmLogRsp);
        List<InspectionTaskDeviceDetail> devices = inspectionTaskDeviceDetailRsp.getData().getDevices();
        if (devices != null) {
            mDevices.addAll(devices);
            if (!TextUtils.isEmpty(tempSearch)) {
//            getView().setSelectedDateSearchText(searchText);
                getView().setSearchButtonTextVisible(true);
            } else {
                getView().setSearchButtonTextVisible(false);
            }
            handlerInspectionTaskDevice();
            getView().updateInspectionTaskDeviceItem(mDevices);
        }
    }

    private void handlerInspectionTaskDevice() {
        //处理排序
        for (InspectionTaskDeviceDetail inspectionTaskDeviceDetail : mDevices) {
            int status = inspectionTaskDeviceDetail.getStatus();
            if (status == 0) {
                if (isNearBy(inspectionTaskDeviceDetail)) {
                    inspectionTaskDeviceDetail.setNearBy_local(true);
                    inspectionTaskDeviceDetail.setSort_local(4);
                } else {
                    inspectionTaskDeviceDetail.setNearBy_local(false);
                    inspectionTaskDeviceDetail.setSort_local(3);
                }
            } else {
                if (isNearBy(inspectionTaskDeviceDetail)) {
                    inspectionTaskDeviceDetail.setNearBy_local(true);
                    inspectionTaskDeviceDetail.setSort_local(2);
                } else {
                    inspectionTaskDeviceDetail.setNearBy_local(false);
                    inspectionTaskDeviceDetail.setSort_local(1);
                }
            }

        }
        Collections.sort(mDevices);
    }

    private boolean isNearBy(InspectionTaskDeviceDetail inspectionTaskDeviceDetail) {
        return BLE_DEVICE_SET.contains(inspectionTaskDeviceDetail.getSn());
    }

    public void doSelectStatusDevice(InspectionStatusCountModel item) {
        this.finish = item.status;
        requestSearchData(DIRECTION_DOWN, tempSearch);
    }

    @Override
    public void run() {
        try {
            bleHasOpen = SensoroCityApplication.getInstance().bleDeviceManager.startService();
            if (!bleHasOpen) {
                if (!bleHasOpen) {
                    bleHasOpen = SensoroCityApplication.getInstance().bleDeviceManager.enEnableBle();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            getView().toastShort("请检查蓝牙状态");
        }
        if (canFreshBle) {
            handlerInspectionTaskDevice();
            getView().updateInspectionTaskDeviceItem(mDevices);
        }
        LogUtils.loge("run canFreshBle ----->> " + canFreshBle);
        mHandler.postDelayed(this, 2 * 1000);
    }

    public void doNavigation(int position) {
        InspectionTaskDeviceDetail deviceDetail = mDevices.get(position);
        List<Double> lonlat = deviceDetail.getLonlat();
        if (lonlat != null && lonlat.size() > 1) {
            LatLng destPosition = new LatLng(lonlat.get(1), lonlat.get(0));
            if (AppUtils.doNavigation(mContext, destPosition)) {
                return;
            }
        }
        getView().toastShort("未获取到位置信息");
    }

    @Override
    public void onStart() {
        BleObserver.getInstance().registerBleObserver(this);
    }

    @Override
    public void onStop() {
        BleObserver.getInstance().unregisterBleObserver(this);
    }

    public void doInspectionScan() {
        Intent intent = new Intent(mContext, ScanActivity.class);
        intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, Constants.TYPE_SCAN_INSPECTION);
        intent.putExtra(EXTRA_INSPECTION_INDEX_TASK_INFO, mTaskInfo);
        getView().startAC(intent);
    }

    public void doSelectTypeDevice(DeviceTypeModel deviceTypeModel) {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> deviceTypes = deviceTypeModel.deviceTypes;
        if (deviceTypes != null && deviceTypes.size() > 0) {
            for (int i = 0; i < deviceTypes.size(); i++) {
                if (i + 1 == deviceTypes.size()) {
                    stringBuilder.append(deviceTypes.get(i));
                } else {
                    stringBuilder.append(deviceTypes.get(i)).append(",");
                }
            }
        }
        if (TextUtils.isEmpty(stringBuilder)) {
            tempDeviceType = null;
        } else {
            tempDeviceType = stringBuilder.toString();
        }
        requestSearchData(DIRECTION_DOWN, tempSearch);
    }
}

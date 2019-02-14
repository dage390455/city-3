package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.sensoro.libbleserver.ble.entity.BLEDevice;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.InspectionActivity;
import com.sensoro.smartcity.activity.InspectionExceptionDetailActivity;
import com.sensoro.smartcity.activity.ScanActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.constant.SearchHistoryTypeConstants;
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
import com.sensoro.smartcity.temp.TestUpdateActivity;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.BleObserver;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
    private final List<String> mSearchHistoryList = new ArrayList<>();
    private final HashMap<String,BLEDevice> BLE_DEVICE_SET = new HashMap<String, BLEDevice>();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private volatile boolean canFreshBle = true;
    private InspectionIndexTaskInfo mTaskInfo;
    private volatile boolean bleHasOpen = false;
    private final List<String> selectDeviceList = new ArrayList<>();
    private final int maxPageCount = 5000;

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
        doInspectionType(false);
        List<String> list = PreferencesHelper.getInstance().getSearchHistoryData(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_INSPECTION);
        if (list != null) {
            mSearchHistoryList.addAll(list);
            getView().UpdateSearchHistoryList(mSearchHistoryList);
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
                if (PreferencesHelper.getInstance().getUserData().hasInspectionDeviceModify) {
                    intent.setClass(mContext, InspectionActivity.class);
                } else {
                    getView().toastShort(mContext.getString(R.string.account_no_patrol_device_permissions));
                    return;
                }

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
        Log.e("ljh",":::"+bleDevice.getSn()+">>"+"02900017C607231D".equals(bleDevice.getSn()));
        BLE_DEVICE_SET.put(bleDevice.getSn(),bleDevice);
    }

    @Override
    public void onGoneDevice(BLEDevice bleDevice) {
        try {
            BLE_DEVICE_SET.remove(bleDevice.getSn());
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
                BLE_DEVICE_SET.put(device.getSn(),device);
            }
        }
        try {
            LogUtils.loge("onUpdateDevices = " + stringBuilder.toString());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            //TODO 刷新上报异常结果
            case EVENT_DATA_INSPECTION_UPLOAD_EXCEPTION_CODE:
                //TODO 正常上报结果
            case EVENT_DATA_INSPECTION_UPLOAD_NORMAL_CODE:
                //TODO 设备更换结果
            case EVENT_DATA_DEPLOY_RESULT_CONTINUE:
                requestSearchData(DIRECTION_DOWN, tempSearch);
                break;
            case EVENT_DATA_DEPLOY_RESULT_FINISH:
                getView().finishAc();
                break;

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
                    sc1.statusTitle = mContext.getString(R.string.all_states);
                    sc1.status = 2;
                    list.add(sc1);
                    InspectionStatusCountModel sc2 = new InspectionStatusCountModel();
                    sc2.count = uncheck;
                    sc2.statusTitle = mContext.getString(R.string.not_inspected);
                    sc2.status = 0;
                    list.add(sc2);
                    InspectionStatusCountModel sc3 = new InspectionStatusCountModel();
                    int check = normalNum + abnormalNum;
                    sc3.count = check;
                    sc3.statusTitle = mContext.getString(R.string.has_inspected);
                    sc3.status = 1;
                    list.add(sc3);
                    getView().updateSelectDeviceStatusList(list);
                    if (needPop) {
                        getView().showSelectDeviceStatusPop();
                    }
                    getView().dismissProgressDialog();
                    getView().setBottomInspectionStateTitle(mContext.getString(R.string.i_have_inspected) + "： " + check, mContext.getString(R.string.not_inspected) + "： " + uncheck);
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
        if (selectDeviceList.size() > 0 && needPop) {
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
                    for (InspectionTaskExecutionModel.DeviceTypesBean deviceTypesBean : deviceTypes) {
                        selectDeviceList.add(deviceTypesBean.getDeviceType());
                    }
                    getView().updateSelectDeviceTypeList(selectDeviceList);
                    if (needPop) {
                        getView().showSelectDeviceTypePop();
                    }
                } else {
                    if (needPop) {
                        getView().toastShort("服务器数据返回错误 deviceTypes");
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
                RetrofitServiceHelper.INSTANCE.getInspectionDeviceList(mTaskInfo.getId(), tempSearch, null, finish, tempDeviceType, cur_page * maxPageCount, maxPageCount).
                        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<InspectionTaskDeviceDetailRsp>(this) {
                    @Override
                    public void onCompleted(InspectionTaskDeviceDetailRsp inspectionTaskDeviceDetailRsp) {
                        freshUI(direction, inspectionTaskDeviceDetailRsp);
                        getView().dismissProgressDialog();
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
                RetrofitServiceHelper.INSTANCE.getInspectionDeviceList(mTaskInfo.getId(), tempSearch, null, finish, tempDeviceType, cur_page * maxPageCount, maxPageCount).
                        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<InspectionTaskDeviceDetailRsp>(this) {
                    @Override
                    public void onCompleted(InspectionTaskDeviceDetailRsp inspectionTaskDeviceDetailRsp) {
                        if (inspectionTaskDeviceDetailRsp.getData().getDevices().size() == 0) {
                            getView().toastShort(mContext.getString(R.string.no_more_data));
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
//            if (!TextUtils.isEmpty(tempSearch)) {
////            getView().setSelectedDateSearchText(searchText);
//                getView().setSearchButtonTextVisible(true);
//            } else {
//                getView().setSearchButtonTextVisible(false);
//            }
            handlerInspectionTaskDevice();
            getView().updateInspectionTaskDeviceItem(mDevices);
        }
    }

    private void handlerInspectionTaskDevice() {
        //处理排序
        AMapLocation lastKnownLocation = SensoroCityApplication.getInstance().mLocationClient.getLastKnownLocation();
        for (InspectionTaskDeviceDetail inspectionTaskDeviceDetail : mDevices) {
            int status = inspectionTaskDeviceDetail.getStatus();
            List<Double> lonlat = inspectionTaskDeviceDetail.getLonlat();
            double lance = 0;
            if (lonlat != null && lonlat.size() > 1) {
                if (lastKnownLocation != null) {
                    double latitude = lastKnownLocation.getLatitude();
                    double longitude = lastKnownLocation.getLongitude();
                    double netLatitude = lonlat.get(1);
                    double netLongitude = lonlat.get(0);
                    if (netLatitude != 0 && netLongitude != 0) {
                        lance = AppUtils.gps2m(latitude, longitude, netLatitude, netLongitude);
                    }
                }
            }
            if (status == 0) {
                if (isNearBy(inspectionTaskDeviceDetail)) {
                    inspectionTaskDeviceDetail.setNearBy_local(true);
                    inspectionTaskDeviceDetail.setSort_local(4);
                } else {
                    inspectionTaskDeviceDetail.setNearBy_local(false);
                    inspectionTaskDeviceDetail.setSort_local(3 - lance);
                }
            } else {
                if (isNearBy(inspectionTaskDeviceDetail)) {
                    inspectionTaskDeviceDetail.setNearBy_local(true);
                    inspectionTaskDeviceDetail.setSort_local(2);
                } else {
                    inspectionTaskDeviceDetail.setNearBy_local(false);
                    inspectionTaskDeviceDetail.setSort_local(1 - lance);
                }
            }
            String sn = inspectionTaskDeviceDetail.getSn();
            boolean nearBy_local = inspectionTaskDeviceDetail.isNearBy_local();
            try {
                LogUtils.loge(this, "handlerInspectionTaskDevice -->> sn = " + sn + ",isNear = " + nearBy_local);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        Collections.sort(mDevices);
    }

    private boolean isNearBy(InspectionTaskDeviceDetail inspectionTaskDeviceDetail) {
        return BLE_DEVICE_SET.containsKey(inspectionTaskDeviceDetail.getSn());
    }

    public void doSelectStatusDevice(InspectionStatusCountModel item, String searchText) {
        this.finish = item.status;
        requestSearchData(DIRECTION_DOWN, searchText);
    }

    @Override
    public void run() {
        bleHasOpen = SensoroCityApplication.getInstance().bleDeviceManager.isBluetoothEnabled();
        if (bleHasOpen) {
            try {
                bleHasOpen = SensoroCityApplication.getInstance().bleDeviceManager.startService();
            } catch (Exception e) {
                e.printStackTrace();
                getView().showBleTips();
            }
            if (bleHasOpen) {
                getView().hideBleTips();
            } else {
                getView().showBleTips();
            }
        } else {
            getView().showBleTips();
        }
        if (canFreshBle) {
            handlerInspectionTaskDevice();
            getView().updateInspectionTaskDeviceItem(mDevices);
        }
        try {
            LogUtils.loge("run canFreshBle ----->> " + canFreshBle);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        mHandler.postDelayed(this, 3 * 1000);
//
        if (BLE_DEVICE_SET.containsKey("02900017C607231D")) {
            BLEDevice bleDevice = BLE_DEVICE_SET.get("02900017C607231D");
            Intent intent = new Intent(mContext, TestUpdateActivity.class);
            intent.putExtra("sensoro_device", bleDevice);
            getView().startAC(intent);
            getView().finishAc();
        }
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
        getView().toastShort(mContext.getString(R.string.location_not_obtained));
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

    public void doSelectTypeDevice(DeviceTypeModel deviceTypeModel, String searchText) {
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
        requestSearchData(DIRECTION_DOWN, searchText);
    }

    public void clearSearchHistory() {
        PreferencesHelper.getInstance().clearSearchHistory(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_INSPECTION);
        mSearchHistoryList.clear();
        getView().UpdateSearchHistoryList(mSearchHistoryList);
    }

    public void save(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        PreferencesHelper.getInstance().saveSearchHistoryText(text, SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_INSPECTION);
        mSearchHistoryList.remove(text);
        mSearchHistoryList.add(0, text);
        getView().UpdateSearchHistoryList(mSearchHistoryList);
    }
}

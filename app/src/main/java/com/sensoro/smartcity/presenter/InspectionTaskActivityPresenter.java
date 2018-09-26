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
import com.sensoro.smartcity.activity.InspectionActivity;
import com.sensoro.smartcity.activity.InspectionExceptionDetailActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IInspectionTaskActivityView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.DeviceTypeModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.model.InspectionStatusCountModel;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.InspectionTaskDeviceDetail;
import com.sensoro.smartcity.server.bean.InspectionTaskExecutionModel;
import com.sensoro.smartcity.server.response.InspectionTaskDeviceRsp;
import com.sensoro.smartcity.server.response.InspectionTaskExecutionRsp;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.widget.SensoroToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InspectionTaskActivityPresenter extends BasePresenter<IInspectionTaskActivityView> implements
        BLEDeviceListener<BLEDevice>, IOnCreate, Constants, Runnable {
    private Activity mContext;
    private DeviceTypeModel typeModel;
    private String tempSearch;
    private int cur_page = 0;
    private int finish = 2;
    private final List<InspectionTaskDeviceDetail> mDevices = new ArrayList<>();
    public static final HashSet<String> BLE_DEVICE_SET = new HashSet<>();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private volatile boolean canFreshBle = true;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        //TODO 筛选类型
        initBle();
        requestSearchData(DIRECTION_DOWN, null);
        mHandler.post(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
        stopScan();
        mDevices.clear();
        BLE_DEVICE_SET.clear();
        SensoroCityApplication.getInstance().bleDeviceManager.setBLEDeviceListener(this);

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
        if (bleDevice != null) {
            LogUtils.loge("onNewDevice = " + bleDevice.getSn());
            BLE_DEVICE_SET.add(bleDevice.getSn());
        }
    }

    @Override
    public void onGoneDevice(BLEDevice bleDevice) {
        if (bleDevice != null) {
            LogUtils.loge("onGoneDevice = " + bleDevice.getSn());
            try {
                if (BLE_DEVICE_SET.contains(bleDevice.getSn())) {
                    BLE_DEVICE_SET.remove(bleDevice.getSn());
                }
            } catch (Exception e) {
                e.printStackTrace();
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
                }
            }
        }
        LogUtils.loge("onUpdateDevices = " + temp);
        if (deviceList != null && deviceList.size() > 0) {
            for (BLEDevice device : deviceList) {
                BLE_DEVICE_SET.add(device.getSn());
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
            //TODO 刷新上报异常结果
//            getView().finishAc();
        }
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    public void doInspectionStatus(final boolean needPop) {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getInspectTaskExecution("5bab5d34e51f3a4c850d0435").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<InspectionTaskExecutionRsp>() {
            @Override
            public void onCompleted(InspectionTaskExecutionRsp inspectionTaskExecutionRsp) {
                InspectionTaskExecutionModel data = inspectionTaskExecutionRsp.getData();
                List<InspectionTaskExecutionModel.DeviceStatusBean> deviceStatus = data.getDeviceStatus();
                if (deviceStatus!=null){
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

    public void doInspectionType() {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getInspectTaskExecution("5bab5d34e51f3a4c850d0435").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<InspectionTaskExecutionRsp>() {
            @Override
            public void onCompleted(InspectionTaskExecutionRsp inspectionTaskExecutionRsp) {
                InspectionTaskExecutionModel data = inspectionTaskExecutionRsp.getData();
                List<InspectionTaskExecutionModel.DeviceTypesBean> deviceTypes = data.getDeviceTypes();
                if (deviceTypes!=null){
                    for (InspectionTaskExecutionModel.DeviceTypesBean deviceTypesBean : deviceTypes) {
                        String deviceType = deviceTypesBean.getDeviceType();
                        LogUtils.loge("doInspectionType --->>> " + deviceType);
                    }
                }
                getView().dismissProgressDialog();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);

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
                RetrofitServiceHelper.INSTANCE.getInspectionDeviceList("5bab5d34e51f3a4c850d0435", tempSearch, null, finish, null, cur_page * 15, 15).
                        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<InspectionTaskDeviceRsp>() {
                    @Override
                    public void onCompleted(InspectionTaskDeviceRsp inspectionTaskDeviceRsp) {
                        freshUI(direction, inspectionTaskDeviceRsp);
                        getView().onPullRefreshComplete();
                        getView().dismissProgressDialog();
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().onPullRefreshComplete();
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }
                });
                doInspectionStatus(false);
                break;
            case DIRECTION_UP:
                cur_page++;
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getInspectionDeviceList("5bab5d34e51f3a4c850d0435", tempSearch, null, finish, null, cur_page * 15, 15).
                        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<InspectionTaskDeviceRsp>() {
                    @Override
                    public void onCompleted(InspectionTaskDeviceRsp inspectionTaskDeviceRsp) {
                        if (inspectionTaskDeviceRsp.getData().getDevices().size() == 0) {
                            getView().toastShort("没有更多数据了");
                            getView().onPullRefreshCompleteNoMoreData();
                            cur_page--;
                        } else {
                            freshUI(direction, inspectionTaskDeviceRsp);
                            getView().onPullRefreshComplete();
                        }
                        getView().dismissProgressDialog();
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        cur_page--;
                        getView().onPullRefreshComplete();
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
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

    private void freshUI(int direction, InspectionTaskDeviceRsp inspectionTaskDeviceRsp) {
        if (direction == DIRECTION_DOWN) {
            mDevices.clear();
        }
//        handleDeviceAlarmLogs(deviceAlarmLogRsp);
        List<InspectionTaskDeviceDetail> devices = inspectionTaskDeviceRsp.getData().getDevices();
        if (devices!=null){
            mDevices.addAll(devices);
            if (!TextUtils.isEmpty(tempSearch)) {
//            getView().setSelectedDateSearchText(searchText);
                getView().setSearchButtonTextVisible(true);
            } else {
                getView().setSearchButtonTextVisible(false);
            }
            getView().updateInspectionTaskDeviceItem(mDevices);
        }
        canFreshBle = true;
    }

    public void doSelectStatusDevice(InspectionStatusCountModel item) {
        this.finish = item.status;
        requestSearchData(DIRECTION_DOWN, tempSearch);
    }

    @Override
    public void run() {
        if (canFreshBle) {
            getView().updateInspectionTaskDeviceItem(mDevices);
        }
        LogUtils.loge("run canFreshBle ----->> " + canFreshBle);
        mHandler.postDelayed(this, 3 * 1000);
    }

    public void doNavigation(int position) {

    }
}

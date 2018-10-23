package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.ContractIndexActivity;
import com.sensoro.smartcity.activity.MonitorPointDetailActivity;
import com.sensoro.smartcity.activity.ScanActivity;
import com.sensoro.smartcity.activity.SearchMonitorActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IHomeFragmentView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.AlarmDeviceCountsBean;
import com.sensoro.smartcity.model.DeviceTypeModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.model.HomeTopModel;
import com.sensoro.smartcity.model.PushData;
import com.sensoro.smartcity.push.ThreadPoolManager;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.RetryWithDelay;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.bean.DeviceMergeTypesInfo;
import com.sensoro.smartcity.server.bean.DeviceTypeStyles;
import com.sensoro.smartcity.server.bean.MergeTypeStyles;
import com.sensoro.smartcity.server.response.DeviceAlarmLogRsp;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.DeviceTypeCountRsp;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.widget.popup.AlarmLogPopUtils;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class HomeFragmentPresenter extends BasePresenter<IHomeFragmentView> implements Constants, IOnCreate {
    private Activity mContext;
    private final List<DeviceInfo> mDataAlarmList = Collections.synchronizedList(new ArrayList<DeviceInfo>());
    private final List<DeviceInfo> mDataNormalList = Collections.synchronizedList(new ArrayList<DeviceInfo>());
    private final List<DeviceInfo> mDataLostList = Collections.synchronizedList(new ArrayList<DeviceInfo>());
    private final List<DeviceInfo> mDataInactiveList = Collections.synchronizedList(new ArrayList<DeviceInfo>());
    //TODO故障设备
    private final List<DeviceInfo> mDataProblemList = Collections.synchronizedList(new ArrayList<DeviceInfo>());
    //
    private final Handler mHandler = new Handler();
    private int page = 1;
    private volatile boolean needAlarmPlay = false;
    private volatile boolean needRefresh = false;
    private volatile boolean needRefreshTop = false;
    private volatile boolean needRefreshAll = false;
    private int totalMonitorPoint;
    private int mSoundId;
    private SoundPool mSoundPool;
    //TODO 联动类型选择
    private String mTypeSelectedType = null;
    private int mStatusSelectedIndex = 0;
    //
    private volatile int tempAlarmCount = 0;
    private volatile int tempNormalCount = 0;
    private final List<HomeTopModel> homeTopModels = new ArrayList<>();
    private final ArrayList<String> mMergeTypes = new ArrayList<>();
    //
    private boolean hasAlarmData;
    private boolean hasNormalData;
    private boolean hasLostData;
    private boolean hasInactiveData;
    //TODO 是否有故障设备
    private boolean hasProblemData;
    private final List<Integer> requestStatus = new ArrayList<>();
    private HomeTopModel mHomeTopModel;
    /**
     * 推送轮训
     */
    private final Runnable mTask = new Runnable() {
        @Override
        public void run() {
            //采用线程池处理
            ThreadPoolManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    if (needRefresh) {
                        Log.d("scheduleRefresh", "run: 刷新数据！");
                        scheduleRefresh();
                        needRefresh = false;
                    }
                    mHandler.postDelayed(mTask, 3000);
                }
            });

        }
    };

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        final SoundPool.OnLoadCompleteListener listener = new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                requestInitData(true);
            }
        };
        mSoundPool.setOnLoadCompleteListener(listener);
        mSoundId = mSoundPool.load(context, R.raw.alarm, 1);
        mHandler.postDelayed(mTask, 3000);
    }


    private void requestInitData(final boolean isInit) {
        if (PreferencesHelper.getInstance().getUserData().isSupperAccount) {
            return;
        }
        if (isInit) {
            getView().showProgressDialog();
        }
        LogUtils.loge(this, "刷新Top,内容数据： " + System.currentTimeMillis());
        RetrofitServiceHelper.INSTANCE.getDeviceTypeCount().subscribeOn(Schedulers
                .io()).flatMap(new Func1<DeviceTypeCountRsp, Observable<DeviceInfoListRsp>>() {
            @Override
            public Observable<DeviceInfoListRsp> call(DeviceTypeCountRsp deviceTypeCountRsp) {
                homeTopModels.clear();
                int alarmCount = deviceTypeCountRsp.getData().getAlarm();
                int normal = deviceTypeCountRsp.getData().getNormal();
                int lostCount = deviceTypeCountRsp.getData().getOffline();
                int inactiveCount = deviceTypeCountRsp.getData().getInactive();
                //
                hasAlarmData = alarmCount > 0;
                hasNormalData = normal > 0;
                hasLostData = lostCount > 0;
                hasInactiveData = inactiveCount > 0;
                if (hasAlarmData) {
                    HomeTopModel alarmModel = new HomeTopModel();
                    alarmModel.type = 0;
                    alarmModel.value = alarmCount;
                    homeTopModels.add(alarmModel);
                }
                HomeTopModel normalModel = new HomeTopModel();
                normalModel.type = 1;
                normalModel.value = normal;
                //
                homeTopModels.add(normalModel);
                HomeTopModel lostModel = new HomeTopModel();
                lostModel.type = 2;
                lostModel.value = lostCount;
                homeTopModels.add(lostModel);
                //
                HomeTopModel inactiveModel = new HomeTopModel();
                inactiveModel.type = 3;
                inactiveModel.value = inactiveCount;
                homeTopModels.add(inactiveModel);
                //
                totalMonitorPoint = alarmCount + normal + lostCount + inactiveCount;
                page = 1;
                if (homeTopModels.get(0) != null) {
                    mHomeTopModel = homeTopModels.get(0);
                    mStatusSelectedIndex = mHomeTopModel.type;
                }
                return RetrofitServiceHelper.INSTANCE.getDeviceBriefInfoList(page, null, mTypeSelectedType, mStatusSelectedIndex, null).doOnNext(new Action1<DeviceInfoListRsp>() {
                    @Override
                    public void call(DeviceInfoListRsp deviceInfoListRsp) {
                        List<DeviceInfo> data = deviceInfoListRsp.getData();
                        SensoroCityApplication.getInstance().setData(data);
                        requestOtherDeviceData(isInit);
                    }
                });
            }
        }).retryWhen(new RetryWithDelay(2, 500)).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>(this) {
            @Override
            public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {
                getView().setDetectionPoints(String.valueOf(totalMonitorPoint));
                getView().refreshTop(isInit, homeTopModels);
                List<DeviceInfo> data = deviceInfoListRsp.getData();
                freshDataList(true, mStatusSelectedIndex, data);
                getView().dismissProgressDialog();
                needRefreshAll = false;
                if (isInit) {
                    String currentDataStr = getCurrentDataStr();
                    int currentColor = getCurrentColor();
                    getView().setToolbarTitleBackgroundColor(currentColor);
                    getView().setToolbarTitleCount(currentDataStr);
                }
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().recycleViewRefreshComplete();
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
                needRefreshAll = false;
            }
        });
    }

    private void freshDataList(boolean needClear, int status, List<DeviceInfo> data) {
        switch (status) {
            case 0:
                if (needClear) {
                    mDataAlarmList.clear();
                }
                mDataAlarmList.addAll(data);
                getView().refreshData(mDataAlarmList);
                break;
            case 1:
                if (needClear) {
                    mDataNormalList.clear();
                }
                mDataNormalList.addAll(data);
                getView().refreshData(mDataNormalList);
                break;
            case 2:
                if (needClear) {
                    mDataLostList.clear();
                }
                mDataLostList.addAll(data);
                getView().refreshData(mDataLostList);
                break;
            case 3:
                if (needClear) {
                    mDataInactiveList.clear();
                }
                mDataInactiveList.addAll(data);
                getView().refreshData(mDataInactiveList);
                break;
        }
        getView().recycleViewRefreshComplete();

    }

    /**
     * 请求剩余的数据
     */
    private void requestOtherDeviceData(boolean isInit) {
        if (isInit) {
            mDataAlarmList.clear();
            mDataNormalList.clear();
            mDataLostList.clear();
            mDataInactiveList.clear();
        }
        requestStatus.clear();
        if (hasAlarmData) {
            requestStatus.add(0);
        }
        if (hasNormalData) {
            requestStatus.add(1);
        }
        if (hasLostData) {
            requestStatus.add(2);
        }
        if (hasInactiveData) {
            requestStatus.add(3);
        }
        if (requestStatus.contains(mStatusSelectedIndex)) {
            requestStatus.remove(mStatusSelectedIndex);
        }
        for (final Integer status : requestStatus) {
            RetrofitServiceHelper.INSTANCE.getDeviceBriefInfoList(page, null, mTypeSelectedType, status, null).subscribeOn(Schedulers.io())
                    .retryWhen(new RetryWithDelay(2, 100)).subscribe(new CityObserver<DeviceInfoListRsp>() {
                @Override
                public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {
                    List<DeviceInfo> data = deviceInfoListRsp.getData();
                    switch (status) {
                        case 0:
                            mDataAlarmList.clear();
                            mDataAlarmList.addAll(data);
                            SensoroCityApplication.getInstance().addData(mDataAlarmList);
                            break;
                        case 1:
                            mDataNormalList.clear();
                            mDataNormalList.addAll(data);
                            SensoroCityApplication.getInstance().addData(mDataNormalList);
                            break;
                        case 2:
                            mDataLostList.clear();
                            mDataLostList.addAll(data);
                            SensoroCityApplication.getInstance().addData(mDataLostList);
                            break;
                        case 3:
                            mDataInactiveList.clear();
                            mDataInactiveList.addAll(data);
                            SensoroCityApplication.getInstance().addData(mDataInactiveList);
                            break;
                    }
                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    LogUtils.loge("requestOtherDeviceData onErrorMsg-->> errorCode = " + errorCode + ",errorMsg = " + errorMsg);
                }
            });
        }
    }

    private void scheduleRefresh() {
        List<DeviceInfo> deviceInfoList = SensoroCityApplication.getInstance().getData();
        DeviceMergeTypesInfo localDevicesMergeTypes = PreferencesHelper.getInstance().getLocalDevicesMergeTypes();
        Map<String, DeviceTypeStyles> typeStylesMap = null;
        if (localDevicesMergeTypes != null) {
            DeviceMergeTypesInfo.DeviceMergeTypeConfig config = localDevicesMergeTypes.getConfig();
            if (config != null) {
                typeStylesMap = config.getDeviceType();
            }
        }
        for (int i = 0; i < deviceInfoList.size(); i++) {
            DeviceInfo deviceInfo = deviceInfoList.get(i);
            //TODO 过滤设备信息
            String mergeType = null;
            try {
                mergeType = typeStylesMap.get(deviceInfo.getDeviceType()).getMergeType();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            int status = deviceInfo.getStatus();
            switch (status) {
                case SENSOR_STATUS_ALARM:
                    handleDevicePush(deviceInfo, mergeType, mDataAlarmList);
                    break;
                case SENSOR_STATUS_NORMAL:
                    handleDevicePush(deviceInfo, mergeType, mDataNormalList);
                    break;
                case SENSOR_STATUS_LOST:
                    handleDevicePush(deviceInfo, mergeType, mDataLostList);
                    break;
                case SENSOR_STATUS_INACTIVE:
                    handleDevicePush(deviceInfo, mergeType, mDataInactiveList);
                    break;
                default:
                    break;
            }
        }
        //排序
        ArrayList<DeviceInfo> pushList = new ArrayList<>();
        pushList.addAll(mDataAlarmList);
        pushList.addAll(mDataNormalList);
        pushList.addAll(mDataLostList);
        pushList.addAll(mDataInactiveList);
        Collections.sort(pushList);
        //推送数据
        PushData pushData = new PushData();
        pushData.setDeviceInfoList(pushList);
        EventBus.getDefault().post(pushData);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //TODO 去掉只在主页可见出现的时候刷新
                if (needRefreshAll) {
                    requestInitData(false);
                } else {
                    switch (mStatusSelectedIndex) {
                        case SENSOR_STATUS_ALARM:
                            getView().refreshData(mDataAlarmList);
                            break;
                        case SENSOR_STATUS_NORMAL:
                            getView().refreshData(mDataNormalList);
                            break;
                        case SENSOR_STATUS_LOST:
                            getView().refreshData(mDataLostList);
                            break;
                        case SENSOR_STATUS_INACTIVE:
                            getView().refreshData(mDataInactiveList);
                            break;
                        default:
                            break;
                    }
                    if (needRefreshTop) {
                        getView().refreshTop(false, homeTopModels);
                        getView().setDetectionPoints(String.valueOf(totalMonitorPoint));
                        needRefreshTop = false;
                    }
                }
                if (needAlarmPlay) {
                    playSound();
                    needAlarmPlay = false;
                }
            }
        });

        LogUtils.logd("new dataList = " + pushList.size());
    }

    private void handleDevicePush(DeviceInfo deviceInfo, String mergeType, List<DeviceInfo> dataList) {
        for (int j = 0; j < dataList.size(); j++) {
            DeviceInfo currentDeviceInfo = dataList.get(j);
            if (currentDeviceInfo.getSn().equals(deviceInfo.getSn())) {
                if (TextUtils.isEmpty(mTypeSelectedType)) {
                    dataList.set(j, deviceInfo);
                } else {
                    if (mTypeSelectedType.equalsIgnoreCase(mergeType)) {
                        dataList.set(j, deviceInfo);
                    }
                }

            }
        }
        if (deviceInfo.isNewDevice()) {
            if (TextUtils.isEmpty(mTypeSelectedType)) {
                deviceInfo.setNewDevice(false);
                dataList.add(deviceInfo);
            } else {
                if (mTypeSelectedType.equalsIgnoreCase(mergeType)) {
                    deviceInfo.setNewDevice(false);
                    dataList.add(deviceInfo);
                }
            }

        }
    }

    public void playSound() {
        if (!"admin".equals(PreferencesHelper.getInstance().getUserData().roles)) {
            mSoundPool.play(mSoundId, 1, 1, 0, 0, 1);
        }
    }


    public void clickItem(int position) {
        switch (mStatusSelectedIndex) {
            case 0:
                goMonitorPointDetailActivity(position, mDataAlarmList);
                break;
            case 1:
                goMonitorPointDetailActivity(position, mDataNormalList);
                break;
            case 2:
                goMonitorPointDetailActivity(position, mDataLostList);
                break;
            case 3:
                goMonitorPointDetailActivity(position, mDataInactiveList);
                break;

        }
    }

    private void goMonitorPointDetailActivity(int position, List<DeviceInfo> dataList) {
        try {
            DeviceInfo deviceInfo = dataList.get(position);
            Intent intent = new Intent(mContext, MonitorPointDetailActivity.class);
            intent.putExtra(EXTRA_DEVICE_INFO, deviceInfo);
            intent.putExtra(EXTRA_SENSOR_NAME, deviceInfo.getName());
            intent.putExtra(EXTRA_SENSOR_TYPES, deviceInfo.getSensorTypes());
            intent.putExtra(EXTRA_SENSOR_STATUS, deviceInfo.getStatus());
            intent.putExtra(EXTRA_SENSOR_TIME, deviceInfo.getUpdatedTime());
            intent.putExtra(EXTRA_SENSOR_LOCATION, deviceInfo.getLonlat());
            getView().startAC(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mSoundPool != null) {
            mSoundPool.unload(mSoundId);
            mSoundPool.stop(mSoundId);
            mSoundPool.release();
            mSoundPool.setOnLoadCompleteListener(null);
            mSoundPool = null;
        }
        mHandler.removeCallbacksAndMessages(null);
//        mDataList.clear();
        mDataAlarmList.clear();
        mDataNormalList.clear();
        mDataLostList.clear();
        mDataInactiveList.clear();
        requestStatus.clear();
    }

    //子线程处理
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(EventData eventData) {
        //TODO 后台线程处理消息
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            case EVENT_DATA_SOCKET_DATA_INFO:
                if (data instanceof DeviceInfo) {
//                    LogUtils.loge("new Socket SN = " + ((DeviceInfo) data).getSn());
                    organizeJsonData((DeviceInfo) data);
                    needRefresh = true;
                }
                break;
            case EVENT_DATA_SOCKET_DATA_COUNT:
                if (data instanceof AlarmDeviceCountsBean) {
                    AlarmDeviceCountsBean alarmDeviceCountsBean = (AlarmDeviceCountsBean) data;
                    LogUtils.loge(this, alarmDeviceCountsBean.toString());
                    int currentAlarmCount = alarmDeviceCountsBean.get_$0();
                    int normalCount = alarmDeviceCountsBean.get_$1();
                    int lostCount = alarmDeviceCountsBean.get_$2();
                    int inactiveCount = alarmDeviceCountsBean.get_$3();
                    //
                    hasAlarmData = currentAlarmCount > 0;
                    hasNormalData = normalCount > 0;
                    hasLostData = lostCount > 0;
                    hasInactiveData = inactiveCount > 0;
                    if (tempAlarmCount == 0 && currentAlarmCount > 0) {
                        needAlarmPlay = true;
                    }
                    needRefresh = true;
                    //TODO 暂时刷新
                    if (currentAlarmCount != tempAlarmCount || normalCount != tempNormalCount) {
                        needRefreshAll = true;
                        tempAlarmCount = currentAlarmCount;
                        tempNormalCount = normalCount;
                        return;
                    }
                    tempAlarmCount = currentAlarmCount;
                    tempNormalCount = normalCount;
                    //
                    needRefreshTop = true;
                    //
                    homeTopModels.clear();
                    if (hasAlarmData) {
                        HomeTopModel alarmModel = new HomeTopModel();
                        alarmModel.type = 0;
                        alarmModel.value = tempAlarmCount;
                        homeTopModels.add(alarmModel);
                    }
                    //
                    HomeTopModel normalModel = new HomeTopModel();
                    normalModel.type = 1;
                    normalModel.value = normalCount;
                    homeTopModels.add(normalModel);
                    //
                    HomeTopModel lostModel = new HomeTopModel();
                    lostModel.type = 2;
                    lostModel.value = lostCount;
                    homeTopModels.add(lostModel);
                    //
                    HomeTopModel inactiveModel = new HomeTopModel();
                    inactiveModel.type = 3;
                    inactiveModel.value = inactiveCount;
                    homeTopModels.add(inactiveModel);
//                    }
                    totalMonitorPoint = currentAlarmCount + normalCount + lostCount + inactiveCount;
                }
                break;
            case EVENT_DATA_SEARCH_MERCHANT:
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        requestInitData(true);
                    }
                });
                break;
        }
    }

    public void requestWithDirection(int direction) {
        if (PreferencesHelper.getInstance().getUserData().isSupperAccount) {
            return;
        }
        try {
            getView().showProgressDialog();
            switch (direction) {
                case DIRECTION_DOWN:
                    page = 1;
                    RetrofitServiceHelper.INSTANCE.getDeviceBriefInfoList(page, null, mTypeSelectedType, mStatusSelectedIndex, null).subscribeOn(Schedulers
                            .io()).doOnNext(new Action1<DeviceInfoListRsp>() {
                        @Override
                        public void call(DeviceInfoListRsp deviceInfoListRsp) {
                            SensoroCityApplication.getInstance().setData(deviceInfoListRsp.getData());
                        }
                    }).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>(this) {
                        @Override
                        public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {
                            List<DeviceInfo> data = deviceInfoListRsp.getData();
                            freshDataList(true, mStatusSelectedIndex, data);
                            getView().dismissProgressDialog();
                        }

                        @Override
                        public void onErrorMsg(int errorCode, String errorMsg) {
                            getView().recycleViewRefreshComplete();
                            getView().dismissProgressDialog();
                            getView().toastShort(errorMsg);
                        }
                    });
                    break;
                case DIRECTION_UP:
                    page++;
                    RetrofitServiceHelper.INSTANCE.getDeviceBriefInfoList(page, null, mTypeSelectedType, mStatusSelectedIndex, null).subscribeOn(Schedulers
                            .io()).doOnNext(new Action1<DeviceInfoListRsp>() {
                        @Override
                        public void call(DeviceInfoListRsp deviceInfoListRsp) {
                            try {
                                List<DeviceInfo> data = deviceInfoListRsp.getData();
                                if (data.size() == 0) {
                                    page--;
                                } else {
                                    SensoroCityApplication.getInstance().addData(data);
                                }
                            } catch (Exception e) {
                                page--;
                                e.printStackTrace();
                            }
                        }
                    }).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>(this) {
                        @Override
                        public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {
                            getView().dismissProgressDialog();
                            try {
                                List<DeviceInfo> data = deviceInfoListRsp.getData();
                                if (data.size() == 0) {
//                                    getView().recycleViewRefreshCompleteNoMoreData();
                                    getView().recycleViewRefreshComplete();
                                    getView().toastShort("没有更多数据了");
                                } else {
                                    freshDataList(false, mStatusSelectedIndex, data);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onErrorMsg(int errorCode, String errorMsg) {
                            getView().recycleViewRefreshComplete();
                            getView().dismissProgressDialog();
                            getView().toastShort(errorMsg);
                        }
                    });
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理push来的json数据
     */
    private void organizeJsonData(DeviceInfo newDeviceInfo) {
        if (newDeviceInfo != null) {
            boolean isContains = false;
            for (int i = 0; i < SensoroCityApplication.getInstance().getData().size(); i++) {
                DeviceInfo deviceInfo = SensoroCityApplication.getInstance().getData().get(i);
                if (deviceInfo.getSn().equals(newDeviceInfo.getSn())) {
                    newDeviceInfo.setPushDevice(true);
                    SensoroCityApplication.getInstance().getData().set(i, newDeviceInfo);
                    isContains = true;
                    break;
                }
            }
            if (!isContains) {
                newDeviceInfo.setNewDevice(true);
                newDeviceInfo.setPushDevice(true);
                SensoroCityApplication.getInstance().getData().add(newDeviceInfo);
            }
        }
    }

    public void requestTopData() {
        if (PreferencesHelper.getInstance().getUserData().isSupperAccount) {
            return;
        }
        LogUtils.loge(this, "刷新Top信息： " + System.currentTimeMillis());
        RetrofitServiceHelper.INSTANCE.getDeviceTypeCount().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers
                .mainThread()).subscribe(new CityObserver<DeviceTypeCountRsp>(this) {


            @Override
            public void onCompleted(DeviceTypeCountRsp deviceTypeCountRsp) {
                homeTopModels.clear();
                int alarmCount = deviceTypeCountRsp.getData().getAlarm();
                int normal = deviceTypeCountRsp.getData().getNormal();
                int lostCount = deviceTypeCountRsp.getData().getOffline();
                int inactiveCount = deviceTypeCountRsp.getData().getInactive();
                hasAlarmData = alarmCount > 0;
                hasNormalData = normal > 0;
                hasLostData = lostCount > 0;
                hasInactiveData = inactiveCount > 0;
                //
                if (hasAlarmData) {
                    HomeTopModel alarmModel = new HomeTopModel();
                    alarmModel.type = 0;
                    alarmModel.value = alarmCount;
                    homeTopModels.add(alarmModel);
                }
                //
                HomeTopModel normalModel = new HomeTopModel();
                normalModel.type = 1;
                normalModel.value = normal;
                homeTopModels.add(normalModel);
                //
                HomeTopModel lostModel = new HomeTopModel();
                lostModel.type = 2;
                lostModel.value = lostCount;
                homeTopModels.add(lostModel);
                //
                HomeTopModel inactiveModel = new HomeTopModel();
                inactiveModel.type = 3;
                inactiveModel.value = inactiveCount;
                homeTopModels.add(inactiveModel);
                //
                totalMonitorPoint = alarmCount + normal + lostCount + inactiveCount;
                getView().setDetectionPoints(String.valueOf(totalMonitorPoint));
                getView().refreshTop(false, homeTopModels);
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
            }
        });
    }

    /**
     * 处理数据
     */
//    private void organizeDataList() {
//        this.mDataList.clear();
//        for (int i = 0; i < SensoroCityApplication.getInstance().getData().size(); i++) {
//            DeviceInfo deviceInfo = SensoroCityApplication.getInstance().getData().get(i);
//            switch (deviceInfo.getStatus()) {
//                case SENSOR_STATUS_ALARM:
//                    deviceInfo.setSort(1);
//                    break;
//                case SENSOR_STATUS_NORMAL:
//                    deviceInfo.setSort(2);
//                    break;
//                case SENSOR_STATUS_LOST:
//                    deviceInfo.setSort(3);
//                    break;
//                case SENSOR_STATUS_INACTIVE:
//                    deviceInfo.setSort(4);
//                    break;
//                default:
//                    break;
//            }
//            mDataList.add(deviceInfo);
//        }
//        //排序
//        Collections.sort(mDataList);
//    }
    public void requestDataByStatus(HomeTopModel homeTopModel) {

        this.mStatusSelectedIndex = homeTopModel.type;
        switch (mStatusSelectedIndex) {
            case 0:
                getView().refreshData(mDataAlarmList);
                break;
            case 1:
                getView().refreshData(mDataNormalList);
                break;
            case 2:
                getView().refreshData(mDataLostList);
                break;
            case 3:
                getView().refreshData(mDataInactiveList);
                break;
        }
        getView().recycleViewRefreshComplete();
        mHomeTopModel = homeTopModel;
//        requestWithDirection(DIRECTION_DOWN);
        String currentDataStr = getCurrentDataStr();
        int currentColor = getCurrentColor();
        getView().setToolbarTitleBackgroundColor(currentColor);
        getView().setToolbarTitleCount(currentDataStr);
        requestTopData();
    }

    public void requestDataByTypes(int position, DeviceTypeModel item) {
        if (position == 0) {
            mTypeSelectedType = null;
        } else {
            mTypeSelectedType = mMergeTypes.get(position - 1);
        }
        requestWithDirection(DIRECTION_DOWN);
        requestTopData();
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    public void doScanLogin() {
        if (PreferencesHelper.getInstance().getUserData() != null) {
            if (PreferencesHelper.getInstance().getUserData().hasScanLogin) {
                Intent intent = new Intent(mContext, ScanActivity.class);
                intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, Constants.TYPE_SCAN_LOGIN);
                getView().startAC(intent);
                return;
            }
        }
        getView().toastShort("无此权限");
    }

    public void clickAlarmInfo(int position) {
        DeviceInfo deviceInfo = null;
        switch (mStatusSelectedIndex) {
            case 0:
                deviceInfo = mDataAlarmList.get(position);
                break;
            case 1:
                deviceInfo = mDataNormalList.get(position);
                break;
            case 2:
                deviceInfo = mDataLostList.get(position);
                break;
            case 3:
                deviceInfo = mDataInactiveList.get(position);
                break;

        }
        if (deviceInfo != null) {
            requestAlarmInfo(deviceInfo);
        }

    }

    private void requestAlarmInfo(DeviceInfo deviceInfo) {
        //
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getDeviceAlarmLogList(1, deviceInfo.getSn(), null, null, null, null, null, null)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>(this) {

            @Override
            public void onCompleted(DeviceAlarmLogRsp deviceAlarmLogRsp) {
                getView().dismissProgressDialog();
                if (deviceAlarmLogRsp.getData().size() == 0) {
                    getView().toastShort("未获取到预警日志信息");
                } else {
                    DeviceAlarmLogInfo deviceAlarmLogInfo = deviceAlarmLogRsp.getData().get(0);
                    enterAlarmLogPop(deviceAlarmLogInfo);
                }
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }

    private void enterAlarmLogPop(DeviceAlarmLogInfo deviceAlarmLogInfo) {
        //TODO 弹起预警记录的dialog
        AlarmLogPopUtils mAlarmLogPop = new AlarmLogPopUtils(mContext);
        mAlarmLogPop.refreshData(deviceAlarmLogInfo);
        mAlarmLogPop.show();

    }

    public void doScanDeploy() {
        Intent intent = new Intent(mContext, ScanActivity.class);
        intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, Constants.TYPE_SCAN_DEPLOY_DEVICE);
        getView().startAC(intent);
    }

    public void doSearch() {
        Intent intent = new Intent(mContext, SearchMonitorActivity.class);
        getView().startAC(intent);
    }

    public void doContract() {
        if (PreferencesHelper.getInstance().getUserData() != null) {
            if (PreferencesHelper.getInstance().getUserData().hasContract) {
                Intent intent = new Intent(mContext, ContractIndexActivity.class);
                getView().startAC(intent);
                return;
            }
        }
        getView().toastShort("无此权限");
    }

    public void updateSelectDeviceTypePopAndShow() {
        mMergeTypes.clear();
        final DeviceMergeTypesInfo.DeviceMergeTypeConfig config = PreferencesHelper.getInstance().getLocalDevicesMergeTypes().getConfig();
        Map<String, MergeTypeStyles> mergeType = config.getMergeType();
        Set<Map.Entry<String, MergeTypeStyles>> entries = mergeType.entrySet();
        for (Map.Entry<String, MergeTypeStyles> entry : entries) {
            String key = entry.getKey();
            MergeTypeStyles mergeTypeStyles = entry.getValue();
            if (mergeTypeStyles.isOwn()) {
                mMergeTypes.add(key);
            }
        }
        Collections.sort(mMergeTypes);
        getView().updateSelectDeviceTypePopAndShow(mMergeTypes);
    }

    public void checkUpgrade() {
        UpgradeInfo upgradeInfo = Beta.getUpgradeInfo();
        if (upgradeInfo == null) {
            ThreadPoolManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    Beta.checkUpgrade(false, true);
                }
            });
        }
    }

    private String getCurrentDataStr() {
        if (mHomeTopModel == null) {
            mHomeTopModel = homeTopModels.get(0);
        }
        if (mHomeTopModel != null) {
            StringBuilder stringBuilder = new StringBuilder();
            switch (mHomeTopModel.type) {
                case 0:
                    stringBuilder.append("预警");
                    break;
                case 1:
                    stringBuilder.append("正常");
                    break;
                case 2:
                    stringBuilder.append("失联");
                    break;
                case 3:
                    stringBuilder.append("未激活");
                    break;


            }
            return stringBuilder.append("(").append(mHomeTopModel.value).append(")").toString();
        }
        return "未知";
    }

    public int getCurrentColor() {
        if (mHomeTopModel == null) {
            mHomeTopModel = homeTopModels.get(0);
        }
        switch (mHomeTopModel.type) {
            case 0:
                return R.color.c_f34a4a;
            case 1:
                return R.color.c_29c093;
            case 2:
                return R.color.c_5d5d5d;
            case 3:
                return R.color.c_b6b6b6;
        }
        return R.color.c_29c093;
    }
}

package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.ContractIndexActivity;
import com.sensoro.smartcity.activity.MonitorPointDetailActivity;
import com.sensoro.smartcity.activity.ScanActivity;
import com.sensoro.smartcity.activity.SearchMonitorActivity;
import com.sensoro.smartcity.adapter.MainHomeFragRcContentAdapter;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IHomeFragmentView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.AlarmDeviceCountsBean;
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
import com.sensoro.smartcity.server.bean.MergeTypeStyles;
import com.sensoro.smartcity.server.response.DeviceAlarmLogRsp;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.DeviceTypeCountRsp;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.popup.AlarmLogPopUtils;
import com.sensoro.smartcity.widget.popup.AlarmLogPopUtils.DialogDisplayStatusListener;
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

public class HomeFragmentPresenter extends BasePresenter<IHomeFragmentView> implements Constants, IOnCreate
        , DialogDisplayStatusListener {
    private Activity mContext;
    private final Handler mHandler = new Handler();
    private volatile int page = 1;
    private volatile boolean needAlarmPlay = false;
    //
    private volatile boolean needRefreshContent = false;
    private volatile boolean needRefreshHeader = false;
    //
    private volatile int totalMonitorPoint;
    private int mSoundId;
    private SoundPool mSoundPool;
    //TODO 联动类型选择
    private String mTypeSelectedType;
    private volatile int tempAlarmCount = 0;
    private final List<HomeTopModel> mHomeTopModels = new ArrayList<>();
    private final ArrayList<String> mMergeTypes = new ArrayList<>();
    //
    private HomeTopModel mCurrentHomeTopModel;
    //
    private final HomeTopModel alarmModel = new HomeTopModel();
    private final HomeTopModel normalModel = new HomeTopModel();
    private final HomeTopModel lostModel = new HomeTopModel();
    private final HomeTopModel inactiveModel = new HomeTopModel();
    /**
     * 推送轮训
     */
    private final Runnable mTask = new Runnable() {
        @Override
        public void run() {
            //是否有权限
            if (PreferencesHelper.getInstance().getUserData().hasDeviceBrief) {
                //采用线程池处理
                ThreadPoolManager.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (needRefreshContent || needRefreshHeader) {
                            Log.d("scheduleRefresh", "run: 刷新数据！");
                            scheduleRefresh();
                        }
                        mHandler.postDelayed(mTask, 3000);
                    }
                });
            }


        }
    };

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        //
        alarmModel.innerAdapter = new MainHomeFragRcContentAdapter(mContext);
        normalModel.innerAdapter = new MainHomeFragRcContentAdapter(mContext);
        lostModel.innerAdapter = new MainHomeFragRcContentAdapter(mContext);
        inactiveModel.innerAdapter = new MainHomeFragRcContentAdapter(mContext);
        //
        mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        final SoundPool.OnLoadCompleteListener listener = new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (PreferencesHelper.getInstance().getUserData().hasDeviceBrief) {
                    requestInitData();
                }

            }
        };
        mSoundPool.setOnLoadCompleteListener(listener);
        mSoundId = mSoundPool.load(context, R.raw.alarm, 1);
        mHandler.postDelayed(mTask, 3000);
        getView().setImvHeaderLeftVisible(false);
        alarmModel.type = 0;
        //
        normalModel.type = 1;
        mHomeTopModels.add(normalModel);
        //
        lostModel.type = 2;
        mHomeTopModels.add(lostModel);
        //
        inactiveModel.type = 3;
        mHomeTopModels.add(inactiveModel);
        getView().refreshHeaderData(true, mHomeTopModels);
    }


    private void requestInitData() {
        if (PreferencesHelper.getInstance().getUserData().isSupperAccount) {
            return;
        }
        getView().showProgressDialog();
        LogUtils.loge(this, "刷新Top,内容数据： " + System.currentTimeMillis());
        RetrofitServiceHelper.INSTANCE.getDeviceTypeCount().subscribeOn(Schedulers
                .io()).flatMap(new Func1<DeviceTypeCountRsp, Observable<DeviceInfoListRsp>>() {
            @Override
            public Observable<DeviceInfoListRsp> call(DeviceTypeCountRsp deviceTypeCountRsp) {
                mHomeTopModels.clear();
                alarmModel.clearData();
                normalModel.clearData();
                lostModel.clearData();
                inactiveModel.clearData();
                SensoroCityApplication.getInstance().getData().clear();
                final int alarmCount = deviceTypeCountRsp.getData().getAlarm();
                int normal = deviceTypeCountRsp.getData().getNormal();
                int lostCount = deviceTypeCountRsp.getData().getOffline();
                int inactiveCount = deviceTypeCountRsp.getData().getInactive();
                if (alarmCount > 0) {
                    alarmModel.value = alarmCount;
                    tempAlarmCount = alarmCount;
                    mHomeTopModels.add(alarmModel);
                }
                //
                normalModel.value = normal;
                mHomeTopModels.add(normalModel);
                //
                lostModel.value = lostCount;
                mHomeTopModels.add(lostModel);
                //
                inactiveModel.value = inactiveCount;
                mHomeTopModels.add(inactiveModel);
                //
                totalMonitorPoint = alarmCount + normal + lostCount + inactiveCount;
                page = 1;
                mCurrentHomeTopModel = mHomeTopModels.get(0);

                return getAllDeviceInfoListRspObservable(true);
            }
        }).retryWhen(new RetryWithDelay(2, 100)).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>(this) {
            @Override
            public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {
                freshHeaderContentData();
                getView().dismissProgressDialog();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                freshHeaderContentData();
                getView().toastShort(errorMsg);
                getView().dismissProgressDialog();
            }
        });
    }

    private void freshHeaderContentData() {
        getView().setDetectionPoints(String.valueOf(totalMonitorPoint));
        getView().refreshHeaderData(true, mHomeTopModels);
        getView().refreshContentData(true, mHomeTopModels);
        String currentDataStr = getCurrentDataStr();
        int currentColor = getCurrentColor();
        getView().setToolbarTitleBackgroundColor(currentColor);
        getView().setToolbarTitleCount(currentDataStr);
    }

    @NonNull
    private Observable<DeviceInfoListRsp> getAllDeviceInfoListRspObservable(final boolean needClear) {
        return RetrofitServiceHelper.INSTANCE.getDeviceBriefInfoList(page, null, mTypeSelectedType, 0, null).subscribeOn(Schedulers.io()).flatMap(new Func1<DeviceInfoListRsp, Observable<DeviceInfoListRsp>>() {
            @Override
            public Observable<DeviceInfoListRsp> call(DeviceInfoListRsp deviceInfoListRsp) {

                List<DeviceInfo> data = deviceInfoListRsp.getData();
                if (needClear) {
                    alarmModel.mDeviceList.clear();
                }
                if (data != null && data.size() > 0) {
                    alarmModel.mDeviceList.clear();
                    alarmModel.mDeviceList.addAll(data);
                }
                return RetrofitServiceHelper.INSTANCE.getDeviceBriefInfoList(page, null, mTypeSelectedType, 1, null);
            }
        }).flatMap(new Func1<DeviceInfoListRsp, Observable<DeviceInfoListRsp>>() {
            @Override
            public Observable<DeviceInfoListRsp> call(DeviceInfoListRsp deviceInfoListRsp) {
                List<DeviceInfo> data = deviceInfoListRsp.getData();
                if (needClear) {
                    normalModel.mDeviceList.clear();
                }
                if (data != null && data.size() > 0) {
                    normalModel.mDeviceList.clear();
                    normalModel.mDeviceList.addAll(data);
                }
                return RetrofitServiceHelper.INSTANCE.getDeviceBriefInfoList(page, null, mTypeSelectedType, 2, null);
            }
        }).flatMap(new Func1<DeviceInfoListRsp, Observable<DeviceInfoListRsp>>() {
            @Override
            public Observable<DeviceInfoListRsp> call(DeviceInfoListRsp deviceInfoListRsp) {
                List<DeviceInfo> data = deviceInfoListRsp.getData();
                if (needClear) {
                    lostModel.mDeviceList.clear();
                }
                if (data != null && data.size() > 0) {
                    lostModel.mDeviceList.clear();
                    lostModel.mDeviceList.addAll(data);
                }
                return RetrofitServiceHelper.INSTANCE.getDeviceBriefInfoList(page, null, mTypeSelectedType, 3, null);
            }
        }).doOnNext(new Action1<DeviceInfoListRsp>() {
            @Override
            public void call(DeviceInfoListRsp deviceInfoListRsp) {
                List<DeviceInfo> data = deviceInfoListRsp.getData();
                if (needClear) {
                    inactiveModel.mDeviceList.clear();
                }
                if (data != null && data.size() > 0) {
                    inactiveModel.mDeviceList.clear();
                    inactiveModel.mDeviceList.addAll(data);
                }
                SensoroCityApplication.getInstance().addData(alarmModel.mDeviceList);
                SensoroCityApplication.getInstance().addData(normalModel.mDeviceList);
                SensoroCityApplication.getInstance().addData(lostModel.mDeviceList);
                SensoroCityApplication.getInstance().addData(inactiveModel.mDeviceList);
            }
        });
    }

    private void freshDataList(HomeTopModel homeTopModel) {
        homeTopModel.innerAdapter.updateData(homeTopModel.mDeviceList);
        getView().recycleViewRefreshComplete();
    }

    /**
     * 请求剩余的数据
     */

    private synchronized void scheduleRefresh() {
        if (needRefreshContent) {
            List<DeviceInfo> deviceInfoList = SensoroCityApplication.getInstance().getData();
            for (int i = 0; i < deviceInfoList.size(); i++) {
                DeviceInfo deviceInfo = deviceInfoList.get(i);
                //TODO 过滤设备信息
                String mergeType = deviceInfo.getMergeType();
                if (TextUtils.isEmpty(mergeType)) {
                    mergeType = WidgetUtil.handleMergeType(deviceInfo.getDeviceType());
                }
                int status = deviceInfo.getStatus();
                switch (status) {
                    case SENSOR_STATUS_ALARM:
                        handleDevicePush(deviceInfo, mergeType, alarmModel.mDeviceList);
                        break;
                    case SENSOR_STATUS_NORMAL:
                        handleDevicePush(deviceInfo, mergeType, normalModel.mDeviceList);
                        break;
                    case SENSOR_STATUS_LOST:
                        handleDevicePush(deviceInfo, mergeType, lostModel.mDeviceList);
                        break;
                    case SENSOR_STATUS_INACTIVE:
                        handleDevicePush(deviceInfo, mergeType, inactiveModel.mDeviceList);
                        break;
                    default:
                        break;
                }
            }

            //排序
            ArrayList<DeviceInfo> pushList = new ArrayList<>();
            pushList.addAll(alarmModel.mDeviceList);
            pushList.addAll(normalModel.mDeviceList);
            pushList.addAll(lostModel.mDeviceList);
            pushList.addAll(inactiveModel.mDeviceList);
            SensoroCityApplication.getInstance().addData(pushList);
            Collections.sort(pushList);
            //推送数据
            PushData pushData = new PushData();
            pushData.setDeviceInfoList(pushList);
            EventBus.getDefault().post(pushData);
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //TODO 去掉只在主页可见出现的时候刷新
                    alarmModel.innerAdapter.updateData(alarmModel.mDeviceList);
                    normalModel.innerAdapter.updateData(normalModel.mDeviceList);
                    lostModel.innerAdapter.updateData(lostModel.mDeviceList);
                    inactiveModel.innerAdapter.updateData(inactiveModel.mDeviceList);
                    needRefreshContent = false;
                }
            });
            LogUtils.logd("new dataList = " + pushList.size());
        }
        if (needRefreshHeader) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getView().refreshHeaderData(false, mHomeTopModels);
                    getView().setDetectionPoints(String.valueOf(totalMonitorPoint));
                    if (needAlarmPlay) {
                        playSound();
                        needAlarmPlay = false;
                    }
                    needRefreshHeader = false;
                }
            });
        }


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


    public void clickItem(int position, HomeTopModel homeTopModel) {
        try {
            DeviceInfo deviceInfo = homeTopModel.innerAdapter.getData().get(position);
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
        mHomeTopModels.clear();
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
                    needRefreshContent = true;
                }
                break;
            case EVENT_DATA_SOCKET_DATA_COUNT:
                if (data instanceof AlarmDeviceCountsBean) {
                    AlarmDeviceCountsBean alarmDeviceCountsBean = (AlarmDeviceCountsBean) data;
                    int currentAlarmCount = alarmDeviceCountsBean.get_$0();
                    int normalCount = alarmDeviceCountsBean.get_$1();
                    int lostCount = alarmDeviceCountsBean.get_$2();
                    int inactiveCount = alarmDeviceCountsBean.get_$3();
                    //
                    if (tempAlarmCount == 0 && currentAlarmCount > 0) {
                        needAlarmPlay = true;
                    }
                    if (currentAlarmCount > tempAlarmCount) {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mCurrentHomeTopModel.type != 0) {
                                    getView().showAlarmInfoView();
                                }
                            }
                        });
                    }
                    LogUtils.loge("EVENT_DATA_SOCKET_DATA_COUNT-->> tempAlarmCount = " + tempAlarmCount + ",currentAlarmCount = " + currentAlarmCount + ",mCurrentHomeTopModel.type = " + mCurrentHomeTopModel.type);
                    tempAlarmCount = currentAlarmCount;
                    //
                    mHomeTopModels.clear();
                    if (currentAlarmCount > 0) {
                        alarmModel.value = tempAlarmCount;
                        mHomeTopModels.add(alarmModel);
                    }
                    //
                    normalModel.value = normalCount;
                    mHomeTopModels.add(normalModel);
                    //
                    lostModel.value = lostCount;
                    mHomeTopModels.add(lostModel);
                    //
                    inactiveModel.value = inactiveCount;
                    mHomeTopModels.add(inactiveModel);
                    totalMonitorPoint = currentAlarmCount + normalCount + lostCount + inactiveCount;
                    needRefreshHeader = true;
                }
                break;
            case EVENT_DATA_SEARCH_MERCHANT:
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        requestInitData();
                    }
                });
                break;
        }
    }

    public void requestWithDirection(int direction, boolean needShowProgress, final HomeTopModel homeTopModel) {
        if (PreferencesHelper.getInstance().getUserData().isSupperAccount) {
            return;
        }
        try {
            if (needShowProgress) {
                getView().showProgressDialog();
            }
            switch (direction) {
                case DIRECTION_DOWN:
                    page = 1;
                    RetrofitServiceHelper.INSTANCE.getDeviceBriefInfoList(page, null, mTypeSelectedType, homeTopModel.type, null).subscribeOn(Schedulers
                            .io()).doOnNext(new Action1<DeviceInfoListRsp>() {
                        @Override
                        public void call(DeviceInfoListRsp deviceInfoListRsp) {
                            List<DeviceInfo> data = deviceInfoListRsp.getData();
                            SensoroCityApplication.getInstance().addData(data);
                            homeTopModel.mDeviceList.clear();
                            homeTopModel.mDeviceList.addAll(data);
                        }
                    }).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>(this) {
                        @Override
                        public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {
                            freshDataList(homeTopModel);
                            getView().dismissProgressDialog();
                        }

                        @Override
                        public void onErrorMsg(int errorCode, String errorMsg) {
                            getView().dismissProgressDialog();
                            getView().toastShort(errorMsg);
                            getView().recycleViewRefreshComplete();
                        }
                    });
                    break;
                case DIRECTION_UP:
                    page++;
                    RetrofitServiceHelper.INSTANCE.getDeviceBriefInfoList(page, null, mTypeSelectedType, homeTopModel.type, null).subscribeOn(Schedulers
                            .io()).doOnNext(new Action1<DeviceInfoListRsp>() {
                        @Override
                        public void call(DeviceInfoListRsp deviceInfoListRsp) {
                            try {
                                List<DeviceInfo> data = deviceInfoListRsp.getData();
                                if (data.size() == 0) {
                                    page--;
                                } else {
                                    SensoroCityApplication.getInstance().addData(data);
                                    homeTopModel.mDeviceList.addAll(data);
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
                                    getView().toastShort("没有更多数据了");
                                }
                                freshDataList(homeTopModel);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onErrorMsg(int errorCode, String errorMsg) {
                            getView().dismissProgressDialog();
                            getView().toastShort(errorMsg);
                            getView().recycleViewRefreshComplete();
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


    public void requestDataByStatus(HomeTopModel homeTopModel) {
        mCurrentHomeTopModel = homeTopModel;
        String currentDataStr = getCurrentDataStr();
        int currentColor = getCurrentColor();
        getView().setToolbarTitleBackgroundColor(currentColor);
        getView().setToolbarTitleCount(currentDataStr);
        int index = mHomeTopModels.indexOf(homeTopModel);
        if (index == 0) {
            getView().setImvHeaderLeftVisible(false);
        } else {
            getView().setImvHeaderLeftVisible(true);
        }
        if (index == mHomeTopModels.size() - 1) {
            getView().setImvHeaderRightVisible(false);
        } else {
            getView().setImvHeaderRightVisible(true);
        }
    }

    public void requestDataByTypes(int position, HomeTopModel homeTopModel) {

        if (position == 0) {
            mTypeSelectedType = null;
        } else {
            mTypeSelectedType = mMergeTypes.get(position - 1);
        }
        page = 1;
        mCurrentHomeTopModel = homeTopModel;
        getView().showProgressDialog();
        getAllDeviceInfoListRspObservable(true).retryWhen(new RetryWithDelay(2, 100)).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>(this) {
            @Override
            public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {

                getView().refreshContentData(false, mHomeTopModels);
                String currentDataStr = getCurrentDataStr();
                int currentColor = getCurrentColor();
                getView().setToolbarTitleBackgroundColor(currentColor);
                getView().setToolbarTitleCount(currentDataStr);
                getView().dismissProgressDialog();
//                needRefreshAll = false;
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
//                needRefreshAll = false;
            }
        });
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

    public void clickAlarmInfo(int position, HomeTopModel homeTopModel) {
        try {
            DeviceInfo deviceInfo = homeTopModel.innerAdapter.getData().get(position);
            requestAlarmInfo(deviceInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestAlarmInfo(DeviceInfo deviceInfo) {
        //
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getDeviceAlarmLogList(1, deviceInfo.getSn(), null, null, null, null, null, null)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>(this) {

            @Override
            public void onCompleted(DeviceAlarmLogRsp deviceAlarmLogRsp) {
//                getView().dismissProgressDialog();
                if (deviceAlarmLogRsp.getData().size() == 0) {
                    getView().dismissProgressDialog();
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
        AlarmLogPopUtils mAlarmLogPop = new AlarmLogPopUtils(mContext, this);
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

    @Override
    public void onDialogShow() {
        getView().dismissProgressDialog();
    }

    private String getCurrentDataStr() {
        if (mCurrentHomeTopModel == null) {
            mCurrentHomeTopModel = mHomeTopModels.get(0);
        }
        StringBuilder stringBuilder = new StringBuilder();
        switch (mCurrentHomeTopModel.type) {
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
        return stringBuilder.append("(").append(mCurrentHomeTopModel.value).append(")").toString();
    }

    private int getCurrentColor() {
        if (mCurrentHomeTopModel == null) {
            mCurrentHomeTopModel = mHomeTopModels.get(0);
        }
        switch (mCurrentHomeTopModel.type) {
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

    public boolean hasContentData() {
        if (mCurrentHomeTopModel != null) {
            return mCurrentHomeTopModel.innerAdapter.getData().size() > 0;
        }
        return false;
    }
}

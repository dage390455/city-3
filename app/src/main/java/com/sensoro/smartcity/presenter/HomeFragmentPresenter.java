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
    private volatile boolean needRefreshContent = false;
    private volatile boolean needShowAlarmWindow = false;
    private volatile boolean needRefreshHeader = false;
    //
    private volatile int totalMonitorPoint;
    private int mSoundId;
    private SoundPool mSoundPool;
    //TODO 联动类型选择
    private volatile String mTypeSelectedType;
    private final ArrayList<String> mMergeTypes = new ArrayList<>();

    private volatile int tempAlarmCount = 0;
    private final List<HomeTopModel> mHomeTopModels = new ArrayList<>();
    //
    private volatile HomeTopModel mCurrentHomeTopModel;
    //
    private final HomeTopModel alarmModel = new HomeTopModel();
    private final HomeTopModel malfunctionModel = new HomeTopModel();
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
                        Log.d("scheduleRefresh", "run: 刷新数据！");
                        scheduleRefresh();
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
        alarmModel.type = 0;
        alarmModel.innerAdapter = new MainHomeFragRcContentAdapter(mContext);
        normalModel.type = 1;
        normalModel.innerAdapter = new MainHomeFragRcContentAdapter(mContext);
        lostModel.type = 2;
        lostModel.innerAdapter = new MainHomeFragRcContentAdapter(mContext);
        inactiveModel.type = 3;
        inactiveModel.innerAdapter = new MainHomeFragRcContentAdapter(mContext);
        malfunctionModel.type = 4;
        malfunctionModel.innerAdapter = new MainHomeFragRcContentAdapter(mContext);
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
        getView().setImvHeaderLeftVisible(false);
    }


    public void requestInitData(boolean needShowProgressDialog) {
        if (PreferencesHelper.getInstance().getUserData().isSupperAccount) {
            return;
        }
        if (!PreferencesHelper.getInstance().getUserData().hasDeviceBrief) {
            return;
        }
        if (needShowProgressDialog) {
            getView().showProgressDialog();
        }
        LogUtils.loge(this, "刷新Top,内容数据： " + System.currentTimeMillis());
        RetrofitServiceHelper.INSTANCE.getDeviceTypeCount().subscribeOn(Schedulers
                .io()).flatMap(new Func1<DeviceTypeCountRsp, Observable<DeviceInfoListRsp>>() {
            @Override
            public Observable<DeviceInfoListRsp> call(DeviceTypeCountRsp deviceTypeCountRsp) {
                mHomeTopModels.clear();
                alarmModel.clearData();
                malfunctionModel.clearData();
                normalModel.clearData();
                lostModel.clearData();
                inactiveModel.clearData();
                final int alarmCount = deviceTypeCountRsp.getData().getAlarm();
                int normal = deviceTypeCountRsp.getData().getNormal();
                int lostCount = deviceTypeCountRsp.getData().getOffline();
                int inactiveCount = deviceTypeCountRsp.getData().getInactive();
                int malfunctionCount = deviceTypeCountRsp.getData().getMalfunction();
                if (alarmCount > 0) {
                    alarmModel.value = alarmCount;
                    tempAlarmCount = alarmCount;
                    mHomeTopModels.add(alarmModel);
                }
                if (malfunctionCount > 0) {
                    malfunctionModel.value = malfunctionCount;
                    mHomeTopModels.add(malfunctionModel);
                }
                if (normal > 0) {
                    normalModel.value = normal;
                    mHomeTopModels.add(normalModel);
                }
                if (lostCount > 0) {
                    lostModel.value = lostCount;
                    mHomeTopModels.add(lostModel);
                }
                if (inactiveCount > 0) {
                    inactiveModel.value = inactiveCount;
                    mHomeTopModels.add(inactiveModel);
                }
                //
                totalMonitorPoint = alarmCount + normal + lostCount + inactiveCount + malfunctionCount;
                page = 1;
                return getAllDeviceInfoListRspObservable(true);
            }
        }).retryWhen(new RetryWithDelay(2, 100)).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>(this) {
            @Override
            public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {
                getView().setDetectionPoints(WidgetUtil.handlerNumber(String.valueOf(totalMonitorPoint)));
                getView().refreshHeaderData(true, mHomeTopModels);
                getView().refreshContentData(true, mHomeTopModels);
                if (mHomeTopModels.size() <= 1) {
                    getView().setImvHeaderLeftVisible(false);
                    getView().setImvHeaderRightVisible(false);
                }
                if (mHomeTopModels.size() > 0) {
                    updateHeaderTop(mHomeTopModels.get(0));
                }
                getView().dismissProgressDialog();
                getView().dismissAlarmInfoView();
                getView().recycleViewRefreshComplete();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().setDetectionPoints(WidgetUtil.handlerNumber(String.valueOf(totalMonitorPoint)));
                getView().refreshHeaderData(true, mHomeTopModels);
                getView().refreshContentData(true, mHomeTopModels);
                if (mHomeTopModels.size() <= 1) {
                    getView().setImvHeaderLeftVisible(false);
                    getView().setImvHeaderRightVisible(false);
                }
                if (mHomeTopModels.size() > 0) {
                    updateHeaderTop(mHomeTopModels.get(0));
                }
                getView().toastShort(errorMsg);
                getView().dismissProgressDialog();
                getView().dismissAlarmInfoView();
                getView().recycleViewRefreshComplete();
            }
        });
    }

    public void updateHeaderTop(HomeTopModel homeTopModel) {
        try {
            mCurrentHomeTopModel = homeTopModel;
            String currentDataStr = getCurrentDataStr();
            int currentColor = getCurrentColor();
            getView().setToolbarTitleBackgroundColor(currentColor);
            getView().setToolbarTitleCount(currentDataStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                return RetrofitServiceHelper.INSTANCE.getDeviceBriefInfoList(page, null, mTypeSelectedType, 4, null);
            }
        }).flatMap(new Func1<DeviceInfoListRsp, Observable<DeviceInfoListRsp>>() {
            @Override
            public Observable<DeviceInfoListRsp> call(DeviceInfoListRsp deviceInfoListRsp) {
                List<DeviceInfo> data = deviceInfoListRsp.getData();
                if (needClear) {
                    malfunctionModel.mDeviceList.clear();
                }
                if (data != null && data.size() > 0) {
                    malfunctionModel.mDeviceList.clear();
                    malfunctionModel.mDeviceList.addAll(data);
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

    private void scheduleRefresh() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (needRefreshContent) {
                    if (homeTopModelCacheFresh[0] || homeTopModelCacheFresh[1] || homeTopModelCacheFresh[2] || homeTopModelCacheFresh[3] || homeTopModelCacheFresh[4]) {
                        getView().refreshContentData(false, mHomeTopModels);
                        homeTopModelCacheFresh[0] = false;
                        homeTopModelCacheFresh[1] = false;
                        homeTopModelCacheFresh[2] = false;
                        homeTopModelCacheFresh[3] = false;
                        homeTopModelCacheFresh[4] = false;
                    }
                    needRefreshContent = false;
                }
                if (needRefreshHeader) {
                    getView().refreshHeaderData(false, mHomeTopModels);
                    getView().setDetectionPoints(WidgetUtil.handlerNumber(String.valueOf(totalMonitorPoint)));
                    if (needAlarmPlay) {
                        playSound();
                    }
                    shoAlarmWindow();
                    needAlarmPlay = false;
                    needRefreshHeader = false;
                }

            }
        });
    }

    private void handleDevicePush(DeviceInfo deviceInfo) {
        String mergeType = deviceInfo.getMergeType();
        if (TextUtils.isEmpty(mergeType)) {
            try {
                String deviceType = deviceInfo.getDeviceType();
                DeviceMergeTypesInfo.DeviceMergeTypeConfig config = PreferencesHelper.getInstance().getLocalDevicesMergeTypes().getConfig();
                mergeType = config.getDeviceType().get(deviceType).getMergeType();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (TextUtils.isEmpty(mTypeSelectedType)) {
            organizeJsonData(deviceInfo);
            addSocketData(deviceInfo);
        } else {
            if (mTypeSelectedType.equalsIgnoreCase(mergeType)) {
                organizeJsonData(deviceInfo);
                addSocketData(deviceInfo);
            }
        }

    }


    private void playSound() {
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
            case EVENT_DATA_DEPLOY_RESULT_FINISH:
            case EVENT_DATA_SOCKET_DATA_INFO:
                if (data instanceof DeviceInfo) {
                    handleDevicePush((DeviceInfo) data);
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
                    int malfunctionCount = alarmDeviceCountsBean.get_$4();
                    //
                    if (tempAlarmCount == 0 && currentAlarmCount > 0) {
                        needAlarmPlay = true;
                    }
                    LogUtils.loge("malfunctionCount = " + malfunctionCount);
                    needShowAlarmWindow = currentAlarmCount > tempAlarmCount;
                    LogUtils.loge("EVENT_DATA_SOCKET_DATA_COUNT-->> tempAlarmCount = " + tempAlarmCount + ",currentAlarmCount = " + currentAlarmCount + ",mCurrentHomeTopModel.type = " + mCurrentHomeTopModel.type);
                    tempAlarmCount = currentAlarmCount;
                    //
                    mHomeTopModels.clear();
                    if (currentAlarmCount > 0) {
                        alarmModel.value = tempAlarmCount;
                        mHomeTopModels.add(alarmModel);
                    }
                    if (malfunctionCount > 0) {
                        malfunctionModel.value = malfunctionCount;
                        mHomeTopModels.add(malfunctionModel);
                    }
                    if (normalCount > 0) {
                        normalModel.value = normalCount;
                        mHomeTopModels.add(normalModel);
                    }
                    if (lostCount > 0) {
                        lostModel.value = lostCount;
                        mHomeTopModels.add(lostModel);
                    }
                    if (inactiveCount > 0) {
                        inactiveModel.value = inactiveCount;
                        mHomeTopModels.add(inactiveModel);
                    }
                    totalMonitorPoint = currentAlarmCount + normalCount + lostCount + inactiveCount + malfunctionCount;
                    needRefreshHeader = true;
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

    private void shoAlarmWindow() {
        //这里是为了控制显示问题，暂时采用延时方式
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    int position = getView().getFirstVisibleItemPosition();
                    requestDataByStatus(mHomeTopModels.get(position));
                    LogUtils.loge("shoAlarmWindow  position = " + position + ",mCurrentHomeTopModel.type = " + mCurrentHomeTopModel.type + ",mCurrentHomeTopModel.value = " + mCurrentHomeTopModel.value);
                    if (needShowAlarmWindow && mCurrentHomeTopModel.type != 0) {
                        getView().showAlarmInfoView();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    needShowAlarmWindow = false;
                }
            }
        }, 300);
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
                                    getView().toastShort(mContext.getString(R.string.no_more_data));
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
    private final boolean[] homeTopModelCacheFresh = {false, false, false, false, false};

    private void organizeJsonData(DeviceInfo newDeviceInfo) {
        int status = newDeviceInfo.getStatus();
        String sn = newDeviceInfo.getSn();
        synchronized (alarmModel.mDeviceList) {
            for (int i = 0; i < alarmModel.mDeviceList.size(); i++) {
                DeviceInfo currentDeviceInfo = alarmModel.mDeviceList.get(i);
                if (currentDeviceInfo.getSn().equalsIgnoreCase(sn)) {
                    if (status == SENSOR_STATUS_ALARM) {
                        alarmModel.mDeviceList.set(i, currentDeviceInfo);
                    } else {
                        alarmModel.mDeviceList.remove(i);
                    }
                    homeTopModelCacheFresh[0] = true;
                    return;
                }
            }
        }
        synchronized (malfunctionModel.mDeviceList) {
            for (int i = 0; i < malfunctionModel.mDeviceList.size(); i++) {
                DeviceInfo currentDeviceInfo = malfunctionModel.mDeviceList.get(i);
                if (currentDeviceInfo.getSn().equalsIgnoreCase(sn)) {
                    if (status == SENSOR_STATUS_MALFUNCTION) {
                        malfunctionModel.mDeviceList.set(i, currentDeviceInfo);
                    } else {
                        malfunctionModel.mDeviceList.remove(i);
                    }
                    homeTopModelCacheFresh[4] = true;
                    return;
                }
            }
        }
        synchronized (normalModel.mDeviceList) {
            for (int i = 0; i < normalModel.mDeviceList.size(); i++) {
                DeviceInfo currentDeviceInfo = normalModel.mDeviceList.get(i);
                if (currentDeviceInfo.getSn().equalsIgnoreCase(sn)) {
                    if (status == SENSOR_STATUS_NORMAL) {
                        normalModel.mDeviceList.set(i, currentDeviceInfo);
                    } else {
                        normalModel.mDeviceList.remove(i);
                    }
                    homeTopModelCacheFresh[1] = true;
                    return;
                }
            }
        }
        synchronized (lostModel.mDeviceList) {
            for (int i = 0; i < lostModel.mDeviceList.size(); i++) {
                DeviceInfo currentDeviceInfo = lostModel.mDeviceList.get(i);
                if (currentDeviceInfo.getSn().equalsIgnoreCase(sn)) {
                    if (status == SENSOR_STATUS_LOST) {
                        lostModel.mDeviceList.set(i, currentDeviceInfo);
                    } else {
                        lostModel.mDeviceList.remove(i);
                    }
                    homeTopModelCacheFresh[2] = true;
                    return;
                }
            }
        }
        synchronized (inactiveModel.mDeviceList) {
            for (int i = 0; i < inactiveModel.mDeviceList.size(); i++) {
                DeviceInfo currentDeviceInfo = inactiveModel.mDeviceList.get(i);
                if (currentDeviceInfo.getSn().equalsIgnoreCase(sn)) {
                    if (status == SENSOR_STATUS_INACTIVE) {
                        inactiveModel.mDeviceList.set(i, currentDeviceInfo);
                    } else {
                        inactiveModel.mDeviceList.remove(i);
                    }
                    homeTopModelCacheFresh[3] = true;
                    return;
                }
            }
        }
    }

    private void addSocketData(DeviceInfo newDeviceInfo) {
        int status = newDeviceInfo.getStatus();
        switch (status) {
            case SENSOR_STATUS_ALARM:
                if (!homeTopModelCacheFresh[0]) {
                    synchronized (alarmModel.mDeviceList) {
                        alarmModel.mDeviceList.add(0, newDeviceInfo);
                        homeTopModelCacheFresh[0] = true;
                    }
                }
                break;
            case SENSOR_STATUS_NORMAL:
                if (!homeTopModelCacheFresh[1]) {
                    synchronized (normalModel.mDeviceList) {
                        normalModel.mDeviceList.add(0, newDeviceInfo);
                        homeTopModelCacheFresh[1] = true;
                    }
                }
                break;
            case SENSOR_STATUS_LOST:
                if (!homeTopModelCacheFresh[2]) {
                    synchronized (lostModel.mDeviceList) {
                        lostModel.mDeviceList.add(0, newDeviceInfo);
                        homeTopModelCacheFresh[2] = true;
                    }
                }
                break;
            case SENSOR_STATUS_INACTIVE:
                if (!homeTopModelCacheFresh[3]) {
                    synchronized (inactiveModel.mDeviceList) {
                        inactiveModel.mDeviceList.add(0, newDeviceInfo);
                        homeTopModelCacheFresh[3] = true;
                    }
                }
                break;
            case SENSOR_STATUS_MALFUNCTION:
                if (!homeTopModelCacheFresh[4]) {
                    synchronized (inactiveModel.mDeviceList) {
                        malfunctionModel.mDeviceList.add(0, newDeviceInfo);
                        homeTopModelCacheFresh[4] = true;
                    }
                }
                break;
            default:
                break;
        }
    }


    public void requestDataByStatus(HomeTopModel homeTopModel) {
        try {
            updateHeaderTop(homeTopModel);
            int index = mHomeTopModels.indexOf(homeTopModel);
            if (mHomeTopModels.size() <= 1) {
                getView().setImvHeaderLeftVisible(false);
                getView().setImvHeaderRightVisible(false);
            } else {
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void requestDataByTypes(int position, final HomeTopModel homeTopModel) {
        try {
            if (position == 0) {
                mTypeSelectedType = null;
            } else {
                mTypeSelectedType = mMergeTypes.get(position - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mTypeSelectedType = null;
        }
        page = 1;
        getView().showProgressDialog();
        getAllDeviceInfoListRspObservable(true).retryWhen(new RetryWithDelay(2, 100)).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>(this) {
            @Override
            public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {

                getView().refreshContentData(false, mHomeTopModels);
                updateHeaderTop(homeTopModel);
                getView().dismissProgressDialog();
                getView().dismissAlarmInfoView();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
                getView().dismissAlarmInfoView();
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
        getView().toastShort(mContext.getString(R.string.no_such_permission));
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
                    getView().toastShort(mContext.getString(R.string.no_alert_log_information_was_obtained));
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
        getView().toastShort(mContext.getString(R.string.no_such_permission));
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
        try {
            if (mCurrentHomeTopModel == null) {
                mCurrentHomeTopModel = mHomeTopModels.get(0);
            }
            StringBuilder stringBuilder = new StringBuilder();
            switch (mCurrentHomeTopModel.type) {
                case 0:
                    stringBuilder.append(mContext.getString(R.string.main_page_warn));
                    break;
                case 1:
                    stringBuilder.append(mContext.getString(R.string.normal));
                    break;
                case 2:
                    stringBuilder.append(mContext.getString(R.string.status_lost));
                    break;
                case 3:
                    stringBuilder.append(mContext.getString(R.string.status_inactive));
                    break;
                case 4:
                    stringBuilder.append(mContext.getString(R.string.status_malfunction));
                    break;
            }
            return stringBuilder.append("(").append(mCurrentHomeTopModel.value).append(")").toString();
        } catch (Exception e) {
            return "";
        }
    }

    private int getCurrentColor() {
        try {
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
                case 4:
                    return R.color.c_fdc83b;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.color.c_29c093;
    }
}

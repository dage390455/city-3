package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.Log;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.activity.SearchDeviceActivity;
import com.sensoro.smartcity.activity.SensorDetailActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IIndexFragmentView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.iwidget.IOnDestroy;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.model.PushData;
import com.sensoro.smartcity.push.ThreadPoolManager;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.LogUtils;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.DeviceTypeCountRsp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class IndexFragmentPresenter extends BasePresenter<IIndexFragmentView> implements IOnCreate, IOnDestroy,
        Constants {
    private final List<DeviceInfo> mDataList = new ArrayList<>();
    private final Handler mHandler = new Handler();
    private int page = 1;
    private volatile boolean isAlarmPlay = false;
    private volatile boolean isNeedRefresh = false;
    private int mSoundId;
    private SoundPool mSoundPool;
    private boolean mIsVisibleToUser = true;
    //

    private int mTypeSelectedIndex = 0;
    private int mStatusSelectedIndex = 0;

    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        final SoundPool.OnLoadCompleteListener listener = new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                boolean supperAccount = ((MainActivity) mContext).isSupperAccount();
                if (supperAccount) {
                    return;
                }
                requestTopData(true);
            }
        };
        mSoundPool.setOnLoadCompleteListener(listener);
        mSoundId = mSoundPool.load(context, R.raw.alarm, 1);
        mHandler.postDelayed(mTask, 3000);
        requestWithDirection(DIRECTION_DOWN);
    }

    public void setTypeSelectedIndex(int mTypeSelectedIndex) {
        this.mTypeSelectedIndex = mTypeSelectedIndex;
    }

    public void setStatusSelectedIndex(int mStatusSelectedIndex) {
        this.mStatusSelectedIndex = mStatusSelectedIndex;
    }


    public void switchIndexGridOrList(int switchType) {
        if (switchType == TYPE_LIST) {
            getView().switchToTypeGrid();
        } else {
            getView().switchToTypeList();
        }
        page = 1;
        requestWithDirection(DIRECTION_DOWN);
        getView().returnTop();
    }

    public void clickItem(int position) {
        int index = position - 1;
        if (index >= 0) {
            DeviceInfo deviceInfo = mDataList.get(index);
            Intent intent = new Intent(mContext, SensorDetailActivity.class);
            intent.putExtra(EXTRA_DEVICE_INFO, deviceInfo);
            intent.putExtra(EXTRA_SENSOR_NAME, deviceInfo.getName());
            intent.putExtra(EXTRA_SENSOR_TYPES, deviceInfo.getSensorTypes());
            intent.putExtra(EXTRA_SENSOR_STATUS, deviceInfo.getStatus());
            intent.putExtra(EXTRA_SENSOR_TIME, deviceInfo.getUpdatedTime());
            intent.putExtra(EXTRA_SENSOR_LOCATION, deviceInfo.getLonlat());
            getView().startAC(intent);
        }
    }

    public void requestWithDirection(int direction) {
        boolean supperAccount = ((MainActivity) mContext).isSupperAccount();
        if (supperAccount) {
            return;
        }
        try {
            String type = mTypeSelectedIndex == 0 ? null : INDEX_TYPE_VALUES[mTypeSelectedIndex];
            Integer status = mStatusSelectedIndex == 0 ? null : INDEX_STATUS_VALUES[mStatusSelectedIndex - 1];
            getView().showProgressDialog();
            if (direction == DIRECTION_DOWN) {
                page = 1;
                RetrofitServiceHelper.INSTANCE.getDeviceBriefInfoList(page, type, status, null).subscribeOn(Schedulers
                        .io()).map(new Func1<DeviceInfoListRsp, DeviceInfoListRsp>() {
                    @Override
                    public DeviceInfoListRsp call(DeviceInfoListRsp deviceInfoListRsp) {
                        //去除rfid类型
                        List<DeviceInfo> list = deviceInfoListRsp.getData();
                        Iterator<DeviceInfo> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            DeviceInfo next = iterator.next();
                            String[] sensorTypes = next.getSensorTypes();
                            if (sensorTypes != null && sensorTypes.length > 0) {
                                final List<String> sensorTypesList = Arrays.asList(sensorTypes);
                                if (sensorTypesList.contains("rfid")) {
                                    iterator.remove();
                                }
                            }
                        }
                        return deviceInfoListRsp;
                    }
                }).doOnNext(new Action1<DeviceInfoListRsp>() {
                    @Override
                    public void call(DeviceInfoListRsp deviceInfoListRsp) {
                        SensoroCityApplication.getInstance().setData(deviceInfoListRsp.getData());
                        organizeDataList();
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>() {
                    @Override
                    public void onCompleted() {
                        getView().dismissProgressDialog();
                    }

                    @Override
                    public void onNext(DeviceInfoListRsp deviceInfoListRsp) {
                        getView().refreshData(mDataList);
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }
                });
            } else {
                page++;
                RetrofitServiceHelper.INSTANCE.getDeviceBriefInfoList(page, type, status, null).subscribeOn(Schedulers
                        .io()).map(new Func1<DeviceInfoListRsp, DeviceInfoListRsp>() {
                    @Override
                    public DeviceInfoListRsp call(DeviceInfoListRsp deviceInfoListRsp) {
                        //去除rfid类型
                        List<DeviceInfo> list = deviceInfoListRsp.getData();
                        Iterator<DeviceInfo> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            DeviceInfo next = iterator.next();
                            String[] sensorTypes = next.getSensorTypes();
                            if (sensorTypes != null && sensorTypes.length > 0) {
                                final List<String> sensorTypesList = Arrays.asList(sensorTypes);
                                if (sensorTypesList.contains("rfid")) {
                                    iterator.remove();
                                }
                            }
                        }
                        return deviceInfoListRsp;
                    }
                }).doOnNext(new Action1<DeviceInfoListRsp>() {
                    @Override
                    public void call(DeviceInfoListRsp deviceInfoListRsp) {
                        try {
                            List<DeviceInfo> data = deviceInfoListRsp.getData();
                            if (data.size() == 0) {
                                page--;
                            } else {
                                SensoroCityApplication.getInstance().addData(data);
                                organizeDataList();
                            }
                        } catch (Exception e) {
                            page--;
                            e.printStackTrace();
                        }
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>() {
                    @Override
                    public void onCompleted() {
                        getView().dismissProgressDialog();
                        getView().recycleViewRefreshComplete();
                    }

                    @Override
                    public void onNext(DeviceInfoListRsp deviceInfoListRsp) {
                        try {
                            List<DeviceInfo> data = deviceInfoListRsp.getData();
                            if (data.size() == 0) {
                                getView().toastShort("没有更多数据了");
                            } else {
                                getView().refreshData(mDataList);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }
                });
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
                    if (newDeviceInfo.getStatus() == SENSOR_STATUS_ALARM && deviceInfo.getStatus() != SENSOR_STATUS_ALARM) {
                        isAlarmPlay = true;
                    }
                    newDeviceInfo.setPushDevice(true);
                    SensoroCityApplication.getInstance().getData().set(i, newDeviceInfo);
                    isContains = true;
                    break;
                }
            }
            if (!isContains) {
                if (newDeviceInfo.getStatus() == SENSOR_STATUS_ALARM) {
                    isAlarmPlay = true;
                }
                newDeviceInfo.setNewDevice(true);
                newDeviceInfo.setPushDevice(true);
                SensoroCityApplication.getInstance().getData().add(newDeviceInfo);
            }
            isNeedRefresh = true;
        }
    }

    public void playSound() {
        String roles = ((MainActivity) mContext).getRoles();
        if (roles != null && !roles.equals("admin")) {
            mSoundPool.play(mSoundId, 1, 1, 0, 0, 1);
        }
    }

    public void requestTopData(final boolean isFirstInit) {
        if (isFirstInit) {
            getView().showProgressDialog();
        }
        RetrofitServiceHelper.INSTANCE.getDeviceTypeCount().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers
                .mainThread()).subscribe(new CityObserver<DeviceTypeCountRsp>() {


            @Override
            public void onCompleted() {
                getView().dismissProgressDialog();
            }

            @Override
            public void onNext(DeviceTypeCountRsp deviceTypeCountRsp) {
                int alarmCount = deviceTypeCountRsp.getData().getAlarm();
                int lostCount = deviceTypeCountRsp.getData().getOffline();
                int inactiveCount = deviceTypeCountRsp.getData().getInactive();
                getView().refreshTop(isFirstInit, alarmCount, lostCount, inactiveCount);
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
            }
        });
    }

    private void filterBySearch() {
        if (mTypeSelectedIndex == 0 && mStatusSelectedIndex == 0) {
        } else {
            List<DeviceInfo> tempTypeList = new ArrayList<>();
            for (int i = 0; i < mDataList.size(); i++) {
                DeviceInfo deviceInfo = mDataList.get(i);
                String unionType = deviceInfo.getUnionType();
                if (unionType != null) {
                    if (unionType.equalsIgnoreCase(SENSOR_MENU_ARRAY[mTypeSelectedIndex]) || mTypeSelectedIndex == 0) {
                        tempTypeList.add(deviceInfo);
                    }
                }
            }

            List<DeviceInfo> tempStatusList = new ArrayList<>();
            if (mStatusSelectedIndex != 0) {
                for (int i = 0; i < tempTypeList.size(); i++) {
                    DeviceInfo deviceInfo = tempTypeList.get(i);
                    int status = INDEX_STATUS_VALUES[mStatusSelectedIndex - 1];
                    if (deviceInfo.getStatus() == status) {
                        tempStatusList.add(deviceInfo);
                    }
                }
            } else {
                tempStatusList.addAll(tempTypeList);
            }
            mDataList.clear();
            mDataList.addAll(tempStatusList);
        }
    }

    private boolean isMatcher(DeviceInfo deviceInfo) {
        if (mTypeSelectedIndex == 0 && mStatusSelectedIndex == 0) {
            return true;
        } else {
            boolean isMatcherType = false;
            boolean isMatcherStatus = false;
            String unionType = deviceInfo.getUnionType();
            if (unionType != null) {
                String[] unionTypeArray = unionType.split("\\|");
                List<String> unionTypeList = Arrays.asList(unionTypeArray);
                String[] menuTypeArray = SENSOR_MENU_ARRAY[mTypeSelectedIndex].split("\\|");
                if (mTypeSelectedIndex == 0) {
                    isMatcherType = true;
                } else {
                    for (String menuType : menuTypeArray) {
                        if (unionTypeList.contains(menuType)) {
                            isMatcherType = true;
                            break;
                        }
                    }
                }
            }
            if (mStatusSelectedIndex != 0) {
                int status = INDEX_STATUS_VALUES[mStatusSelectedIndex - 1];
                if (deviceInfo.getStatus() == status) {
                    isMatcherStatus = true;
                }
            } else {
                isMatcherStatus = true;
            }
            return isMatcherStatus && isMatcherType;
        }
    }

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
                    if (isNeedRefresh) {
                        Log.d("scheduleRefresh", "run: 刷新数据！");
                        scheduleRefresh();
                    }
                    mHandler.postDelayed(mTask, 3000);
                }
            });

        }
    };

    public void refreshWithSearch(final DeviceInfoListRsp deviceInfoListRsp) {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                mDataList.clear();
                for (int i = 0; i < deviceInfoListRsp.getData().size(); i++) {
                    DeviceInfo deviceInfo = deviceInfoListRsp.getData().get(i);
                    switch (deviceInfo.getStatus()) {
                        case SENSOR_STATUS_ALARM:
                            deviceInfo.setSort(1);
                            break;
                        case SENSOR_STATUS_NORMAL:
                            deviceInfo.setSort(2);
                            break;
                        case SENSOR_STATUS_LOST:
                            deviceInfo.setSort(3);
                            break;
                        case SENSOR_STATUS_INACTIVE:
                            deviceInfo.setSort(4);
                            break;
                        default:
                            break;
                    }
                    mDataList.add(deviceInfo);
                }
                filterBySearch();
                //排序
                Collections.sort(mDataList);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getView().refreshData(mDataList);
                    }
                });

            }
        });

    }

    /**
     * 处理数据
     */
    private void organizeDataList() {
        this.mDataList.clear();
        for (int i = 0; i < SensoroCityApplication.getInstance().getData().size(); i++) {
            DeviceInfo deviceInfo = SensoroCityApplication.getInstance().getData().get(i);
            switch (deviceInfo.getStatus()) {
                case SENSOR_STATUS_ALARM:
                    deviceInfo.setSort(1);
                    break;
                case SENSOR_STATUS_NORMAL:
                    deviceInfo.setSort(2);
                    break;
                case SENSOR_STATUS_LOST:
                    deviceInfo.setSort(3);
                    break;
                case SENSOR_STATUS_INACTIVE:
                    deviceInfo.setSort(4);
                    break;
                default:
                    break;
            }
            if (isMatcher(deviceInfo)) {
                mDataList.add(deviceInfo);
            }
        }
        //排序
        Collections.sort(mDataList);
    }


    private void scheduleRefresh() {
        List<DeviceInfo> deviceInfoList = SensoroCityApplication.getInstance().getData();
        for (int i = 0; i < deviceInfoList.size(); i++) {
            DeviceInfo deviceInfo = deviceInfoList.get(i);
            switch (deviceInfo.getStatus()) {
                case SENSOR_STATUS_ALARM:
                    deviceInfo.setSort(1);
                    break;
                case SENSOR_STATUS_NORMAL:
                    deviceInfo.setSort(2);
                    break;
                case SENSOR_STATUS_LOST:
                    deviceInfo.setSort(3);
                    break;
                case SENSOR_STATUS_INACTIVE:
                    deviceInfo.setSort(4);
                    break;
                default:
                    break;
            }
            for (int j = 0; j < mDataList.size(); j++) {
                DeviceInfo currentDeviceInfo = mDataList.get(j);
                if (currentDeviceInfo.getSn().equals(deviceInfo.getSn())) {
                    mDataList.set(j, deviceInfo);
                }
            }
            if (deviceInfo.isNewDevice() && isMatcher(deviceInfo)) {
                deviceInfo.setNewDevice(false);
                mDataList.add(deviceInfo);
            }
        }
        //推送数据
        PushData pushData = new PushData();
        pushData.setAlarmStatus(isAlarmPlay);
        pushData.setDeviceInfoList(mDataList);
        EventBus.getDefault().post(pushData);
        //只在主页可见出现的时候刷新
        if (mIsVisibleToUser) {
            requestTopData(false);
            //排序
            Collections.sort(mDataList);
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getView().refreshData(mDataList);
                }
            });

        }
        if (isAlarmPlay) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playSound();
                }
            });

            isAlarmPlay = false;
        }
        isNeedRefresh = false;
        LogUtils.logd("new dataList = " + mDataList.size());
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
        mDataList.clear();
    }

    public void toSearchAc() {
        Intent intent = new Intent(mContext, SearchDeviceActivity.class);
        getView().startAC(intent);
    }

    public void onHiddenChanged(boolean hidden) {
        mIsVisibleToUser = hidden;
    }

    //子线程处理
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(EventData eventData) {
        //TODO 可以修改以此种方式传递，方便管理
        int code = eventData.code;
        Object data = eventData.data;
        if (code == EVENT_DATA_SOCKET_DATA) {
            if (data instanceof DeviceInfo) {
                LogUtils.loge("new SN = " + ((DeviceInfo) data).getSn());
                organizeJsonData((DeviceInfo) data);
            }
        }
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }
}

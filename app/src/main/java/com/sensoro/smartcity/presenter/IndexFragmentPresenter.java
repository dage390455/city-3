package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.activity.SearchDeviceActivity;
import com.sensoro.smartcity.activity.SensorDetailActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IIndexFragmentView;
import com.sensoro.smartcity.iwidget.IOndestroy;
import com.sensoro.smartcity.model.PushData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.NumberDeserializer;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.DeviceTypeCountRsp;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class IndexFragmentPresenter extends BasePresenter<IIndexFragmentView> implements IOndestroy,
        Constants {
    private final List<DeviceInfo> mDataList = new ArrayList<>();
    private final Handler mHandler = new Handler();
    private final Gson gson = new GsonBuilder().registerTypeAdapter(double.class, new NumberDeserializer())
            .registerTypeAdapter(int.class, new NumberDeserializer())
            .registerTypeAdapter(Number.class, new NumberDeserializer()).create();
    private int page = 1;
    private volatile boolean isAlarmPlay = false;
    private volatile boolean isNeedRefresh = false;
    private int mSoundId;
    private SoundPool mSoundPool;
    private boolean mIsVisibleToUser = true;
    //

    private int mTypeSelectedIndex = 0;
    private int mStatusSelectedIndex = 0;

    public void setTypeSelectedIndex(int mTypeSelectedIndex) {
        this.mTypeSelectedIndex = mTypeSelectedIndex;
    }

    public void setStatusSelectedIndex(int mStatusSelectedIndex) {
        this.mStatusSelectedIndex = mStatusSelectedIndex;
    }

    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
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
                        .io()).doOnNext(new Action1<DeviceInfoListRsp>() {
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
                        .io()).doOnNext(new Action1<DeviceInfoListRsp>() {
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
     *
     * @param json
     */
    public void organizeJsonData(String json) {
        Log.d("push....", "organizeJsonData----->> " + json);
        DeviceInfo data = gson.fromJson(json, DeviceInfo.class);
        if (data != null) {
            boolean isContains = false;
            for (int i = 0; i < SensoroCityApplication.getInstance().getData().size(); i++) {
                DeviceInfo deviceInfo = SensoroCityApplication.getInstance().getData().get(i);
                if (deviceInfo.getSn().equals(data.getSn())) {
                    if (data.getStatus() == SENSOR_STATUS_ALARM && deviceInfo.getStatus() != SENSOR_STATUS_ALARM) {
                        isAlarmPlay = true;
                    }
                    data.setPushDevice(true);
                    SensoroCityApplication.getInstance().getData().set(i, data);
                    isContains = true;
                    break;
                }
            }
            if (!isContains) {
                if (data.getStatus() == SENSOR_STATUS_ALARM) {
                    isAlarmPlay = true;
                }
                data.setNewDevice(true);
                data.setPushDevice(true);
                SensoroCityApplication.getInstance().getData().add(data);
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
                    for (int j = 0; j < menuTypeArray.length; j++) {
                        String menuType = menuTypeArray[j];
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
            if (isNeedRefresh) {
                Log.e("", "run: 刷新数据！");
                scheduleRefresh();
            }
            mHandler.postDelayed(this, 3000);
        }
    };

    public void refreshWithSearch(DeviceInfoListRsp deviceInfoListRsp) {
        this.mDataList.clear();
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
        getView().refreshData(mDataList);
    }

    private boolean isMainActivityTop() {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(MainActivity.class.getName());
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
    }


    private void scheduleRefresh() {
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
        if (isMainActivityTop() && mIsVisibleToUser) {
            requestTopData(false);
            getView().refreshData(mDataList);
        }
        if (isAlarmPlay) {
            playSound();
            isAlarmPlay = false;
        }
        isNeedRefresh = false;
    }


    @Override
    public void onDestroy() {
        if (mSoundPool != null) {
            mSoundPool.unload(mSoundId);
            mSoundPool.stop(mSoundId);
            mSoundPool.release();
            mSoundPool.setOnLoadCompleteListener(null);
            mSoundPool = null;
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    public void toSearchAc() {
        Intent intent = new Intent(mContext, SearchDeviceActivity.class);
//        Bundle value = new Bundle();
//        value.putParcelableArrayList(mDataList);
//        intent.putExtra("111", value);
//        intent.putExtra(EXTRA_FRAGMENT_INDEX, 1);

//                int size = mDataList.size();
//                intent.putExtra("", value);
//                Bundle bundle = new Bundle();
//        getView().startAC(intent);
        getView().startACForResult(intent, RESULT_CODE_SEARCH_MERCHANT);
    }

    public void onHiddenChanged(boolean hidden) {
        mIsVisibleToUser = hidden;
    }
}

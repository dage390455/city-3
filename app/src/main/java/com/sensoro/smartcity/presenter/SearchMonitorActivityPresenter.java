package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.MonitorPointElectricDetailActivity;
import com.sensoro.smartcity.analyzer.AlarmPopupConfigAnalyzer;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.ISearchMonitorActivityView;
import com.sensoro.smartcity.model.AlarmPopupModel;
import com.sensoro.common.iwidget.IOnStart;
import com.sensoro.smartcity.model.AlarmPopModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.push.ThreadPoolManager;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.response.DeviceAlarmLogRsp;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.DevicesAlarmPopupConfigRsp;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.popup.AlarmLogPopUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class SearchMonitorActivityPresenter extends BasePresenter<ISearchMonitorActivityView> implements Constants,
        IOnStart {
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private String history;


    private final List<String> mHistoryKeywords = new ArrayList<>();

    private int page = 1;
    private final List<DeviceInfo> mDataList = new ArrayList<>();
    private final List<DeviceInfo> originHistoryList = Collections.synchronizedList(new ArrayList<DeviceInfo>());
    private final List<DeviceInfo> currentList = new ArrayList<>();

    private final List<String> searchStrList = Collections.synchronizedList(new ArrayList<String>());
    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
//        originHistoryList.addAll(SensoroCityApplication.getInstance().getData());
        currentList.addAll(originHistoryList);
        mPref = mContext.getSharedPreferences(PREFERENCE_DEVICE_HISTORY, Activity.MODE_PRIVATE);
        mEditor = mPref.edit();
        history = mPref.getString(PREFERENCE_KEY_DEVICE, "");
        if (!TextUtils.isEmpty(history)) {
            mHistoryKeywords.clear();
            mHistoryKeywords.addAll(Arrays.asList(history.split(",")));
        }
        if (mHistoryKeywords.size() > 0) {
            getView().setSearchHistoryLayoutVisible(true);
            getView().updateSearchHistoryData(mHistoryKeywords);
        } else {
            getView().setSearchHistoryLayoutVisible(false);
        }

    }


    @Override
    public void onStart() {
//        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
//        EventBus.getDefault().unregister(this);
        getView().hideSoftInput();
    }

//    private void hideSoftInput() {
//        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            boolean active = imm.isActive();
//            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//        }
//
//    }

//    @Subscribe(threadMode = ThreadMode.BACKGROUND)
//    public void onMessageEvent(PushData data) {
//        if (data != null) {
//            boolean needFresh = false;
//            List<DeviceInfo> deviceInfoList = data.getDeviceInfoList();
//            for (int i = 0; i < mDataList.size(); i++) {
//                DeviceInfo deviceInfo = mDataList.get(i);
//                for (DeviceInfo in : deviceInfoList) {
//                    if (in.getSn().equals(deviceInfo.getSn())) {
//                        mDataList.set(i, in);
//                        needFresh = true;
//                    }
//                }
//            }
//            if (needFresh) {
//                mContext.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (isActivityTop() && getView().getSearchDataListVisible()) {
//                            if (getView() != null) {
//                                getView().refreshData(mDataList);
//                            }
//
//                        }
//                    }
//                });
//            }
//        }
//    }


    public void clickRelationItem(int position) {
        String s = searchStrList.get(position);
        save(s);
        requestWithDirection(DIRECTION_DOWN, s);
    }


    private void refreshCacheData() {
        this.mDataList.clear();
//        this.mDataList.clear();
//        this.mRelationLayout.setVisibility(View.GONE);
//        this.mIndexListLayout.setVisibility(VISIBLE);
        for (int i = 0; i < currentList.size(); i++) {
            DeviceInfo deviceInfo = currentList.get(i);
            switch (deviceInfo.getStatus()) {
                case SENSOR_STATUS_ALARM:
                    deviceInfo.setSort(1);
                    break;
                case SENSOR_STATUS_MALFUNCTION:
                    deviceInfo.setSort(2);
                    break;
                case SENSOR_STATUS_NORMAL:
                    deviceInfo.setSort(3);
                    break;
                case SENSOR_STATUS_LOST:
                    deviceInfo.setSort(4);
                    break;
                case SENSOR_STATUS_INACTIVE:
                    deviceInfo.setSort(5);
                    break;
                default:
                    break;
            }
//            if (isMatcher(deviceInfo)) {
            mDataList.add(deviceInfo);
//            }
        }
//        getView().refreshContentData(mDataList);
    }

    public void filterDeviceInfo(final String filter) {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                synchronized (SearchMonitorActivityPresenter.class) {
                    List<DeviceInfo> originDeviceInfoList = new ArrayList<>(originHistoryList);
                    ArrayList<DeviceInfo> deleteDeviceInfoList = new ArrayList<>();
                    for (DeviceInfo deviceInfo : originDeviceInfoList) {
                        String name = deviceInfo.getName();
                        if (!TextUtils.isEmpty(name)) {
                            if (!(name.contains(filter.toUpperCase()))) {
                                deleteDeviceInfoList.add(deviceInfo);
                            }
                        } else {
                            deleteDeviceInfoList.add(deviceInfo);
                        }

                    }
                    originDeviceInfoList.removeAll(deleteDeviceInfoList);
                    final List<String> tempList = new ArrayList<>();
                    for (DeviceInfo deviceInfo : originDeviceInfoList) {
                        String name = deviceInfo.getName();
                        if (!TextUtils.isEmpty(name)) {
                            tempList.add(name);
                        }
                    }
                    searchStrList.clear();
                    searchStrList.addAll(tempList);
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isAttachedView()) {
                                getView().updateRelationData(tempList);
                            }

                        }
                    });
                }

            }
        });


    }

    public void save(String text) {
        String oldText = mPref.getString(PREFERENCE_KEY_DEVICE, "");
        //
        List<String> oldHistoryList = new ArrayList<String>();
        if (!TextUtils.isEmpty(oldText)) {
            oldHistoryList.addAll(Arrays.asList(oldText.split(",")));
        }
        oldHistoryList.remove(text);
        oldHistoryList.add(0, text);
        ArrayList<String> tempList = new ArrayList<>();
        for (String str : oldHistoryList) {
            if (tempList.size() < 20) {
                tempList.add(str);
            }
        }
        mHistoryKeywords.clear();
        mHistoryKeywords.addAll(tempList);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < tempList.size(); i++) {
            if (i == (tempList.size() - 1)) {
                stringBuilder.append(tempList.get(i));
            } else {
                stringBuilder.append(tempList.get(i)).append(",");
            }
        }
        mEditor.putString(PREFERENCE_KEY_DEVICE, stringBuilder.toString());
        mEditor.commit();
        //

    }

    public void cleanHistory() {
        mEditor.clear();
        mHistoryKeywords.clear();
        mEditor.commit();
        getView().updateSearchHistoryData(mHistoryKeywords);
//        getView().setSearchHistoryLayoutVisible(false);
    }

    public void requestWithDirection(int direction, String searchText) {
        getView().setSearchHistoryLayoutVisible(false);
//        getView().setRelationLayoutVisible(false);
        getView().setIndexListLayoutVisible(true);
        getView().showProgressDialog();
        if (direction == DIRECTION_DOWN) {
            page = 1;
            RetrofitServiceHelper.getInstance().getDeviceBriefInfoList(page, null, null, null, searchText).subscribeOn
                    (Schedulers.io()).doOnNext(new Consumer<DeviceInfoListRsp>() {
                @Override
                public void accept(DeviceInfoListRsp deviceInfoListRsp) throws Exception {
                    if (deviceInfoListRsp.getData().size() == 0) {
                        mDataList.clear();
                    } else {
                        currentList.clear();
                        currentList.addAll(deviceInfoListRsp.getData());
                        refreshCacheData();
                    }
                }

            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>(this) {


                @Override
                public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {
                    getView().dismissProgressDialog();
                    try {
                        getView().refreshData(mDataList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getView().recycleViewRefreshComplete();
                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    getView().dismissProgressDialog();
                    getView().recycleViewRefreshComplete();
                    getView().toastShort(errorMsg);
                }
            });
        } else {
            page++;
            RetrofitServiceHelper.getInstance().getDeviceBriefInfoList(page, null, null, null, searchText).subscribeOn
                    (Schedulers.io()).doOnNext(new Consumer<DeviceInfoListRsp>() {
                @Override
                public void accept(DeviceInfoListRsp deviceInfoListRsp) throws Exception {
                    try {
                        if (deviceInfoListRsp.getData().size() == 0) {
                            page--;
                        } else {
                            currentList.addAll(deviceInfoListRsp.getData());
                            refreshCacheData();
                        }
                    } catch (Exception e) {
                        page--;
                        e.printStackTrace();
                    }
                }

            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>(this) {


                @Override
                public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {
                    if (deviceInfoListRsp.getData().size() == 0) {
                        getView().toastShort(mContext.getString(R.string.no_more_data));
                    } else {
                        getView().refreshData(mDataList);
                    }
                    getView().dismissProgressDialog();
                    getView().recycleViewRefreshComplete();
                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    page--;
                    getView().dismissProgressDialog();
                    getView().recycleViewRefreshComplete();
                    getView().toastShort(errorMsg);
                }
            });
        }
    }

    public void clickItem(int position) {
        if (position >= 0) {
            DeviceInfo deviceInfo = mDataList.get(position);
            Intent intent = new Intent();
            intent.setClass(mContext, MonitorPointElectricDetailActivity.class);
            intent.putExtra(EXTRA_DEVICE_INFO, deviceInfo);
            intent.putExtra(EXTRA_SENSOR_NAME, deviceInfo.getName());
            intent.putExtra(EXTRA_SENSOR_TYPES, deviceInfo.getSensorTypes());
            intent.putExtra(EXTRA_SENSOR_STATUS, deviceInfo.getStatus());
            intent.putExtra(EXTRA_SENSOR_TIME, deviceInfo.getUpdatedTime());
            intent.putExtra(EXTRA_SENSOR_LOCATION, deviceInfo.getLonlat().toArray());
            getView().startAC(intent);
        }
    }


    @Override
    public void onDestroy() {
        mDataList.clear();
        mHistoryKeywords.clear();
        originHistoryList.clear();
        currentList.clear();
        searchStrList.clear();
    }


    public void clickAlarmInfo(int position) {
        DeviceInfo deviceInfo = mDataList.get(position);
        requestAlarmInfo(deviceInfo);
    }

    private void requestAlarmInfo(DeviceInfo deviceInfo) {
        //
        getView().showProgressDialog();
        RetrofitServiceHelper.getInstance().getDeviceAlarmLogList(1, deviceInfo.getSn(), null, null, null, null, null, null)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>(this) {

            @Override
            public void onCompleted(DeviceAlarmLogRsp deviceAlarmLogRsp) {
                getView().dismissProgressDialog();
                if (deviceAlarmLogRsp.getData().size() == 0) {
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

    private void enterAlarmLogPop(final DeviceAlarmLogInfo deviceAlarmLogInfo) {
        final AlarmLogPopUtils mAlarmLogPop = new AlarmLogPopUtils(mContext);
        mAlarmLogPop.refreshData(deviceAlarmLogInfo);
        if (PreferencesHelper.getInstance().getAlarmPopupDataBeanCache() == null) {
            RetrofitServiceHelper.getInstance().getDevicesAlarmPopupConfig().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DevicesAlarmPopupConfigRsp>(this) {
                @Override
                public void onCompleted(DevicesAlarmPopupConfigRsp devicesAlarmPopupConfigRsp) {
                    PreferencesHelper.getInstance().saveAlarmPopupDataBeanCache(devicesAlarmPopupConfigRsp.getData());
                    final AlarmPopupModel alarmPopupModel = new AlarmPopupModel();
                    String deviceName = deviceAlarmLogInfo.getDeviceName();
                    if (TextUtils.isEmpty(deviceName)) {
                        alarmPopupModel.title = deviceAlarmLogInfo.getDeviceSN();
                    } else {
                        alarmPopupModel.title = deviceName;
                    }
                    alarmPopupModel.alarmStatus = deviceAlarmLogInfo.getAlarmStatus();
                    alarmPopupModel.updateTime = deviceAlarmLogInfo.getUpdatedTime();
                    alarmPopupModel.mergeType = WidgetUtil.handleMergeType(deviceAlarmLogInfo.getDeviceType());
                    alarmPopupModel.sensorType = deviceAlarmLogInfo.getSensorType();
                    //
                    AlarmPopupConfigAnalyzer.handleAlarmPopupModel(null, alarmPopupModel);
                    mAlarmLogPop.show(alarmPopupModel);
                    getView().dismissProgressDialog();

                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    getView().toastShort(errorMsg);
                    getView().dismissProgressDialog();
                }
            });
        } else {
            final AlarmPopupModel alarmPopupModel = new AlarmPopupModel();
            String deviceName = deviceAlarmLogInfo.getDeviceName();
            if (TextUtils.isEmpty(deviceName)) {
                alarmPopupModel.title = deviceAlarmLogInfo.getDeviceSN();
            } else {
                alarmPopupModel.title = deviceName;
            }
            alarmPopupModel.alarmStatus = deviceAlarmLogInfo.getAlarmStatus();
            alarmPopupModel.updateTime = deviceAlarmLogInfo.getUpdatedTime();
            alarmPopupModel.mergeType = WidgetUtil.handleMergeType(deviceAlarmLogInfo.getDeviceType());
            alarmPopupModel.sensorType = deviceAlarmLogInfo.getSensorType();
            //
            AlarmPopupConfigAnalyzer.handleAlarmPopupModel(null, alarmPopupModel);
            mAlarmLogPop.show(alarmPopupModel);
        }
    }

    public void updateSearchHistoryData() {
        getView().updateSearchHistoryData(mHistoryKeywords);
    }
}

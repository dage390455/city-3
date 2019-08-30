package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.manger.ThreadPoolManager;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.AlarmPopupDataBean;
import com.sensoro.common.server.bean.DeviceAlarmLogInfo;
import com.sensoro.common.server.bean.ScenesData;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.analyzer.AlarmPopupConfigAnalyzer;
import com.sensoro.smartcity.imainviews.IAlarmHistoryLogActivityView;
import com.sensoro.smartcity.model.AlarmPopupModel;
import com.sensoro.smartcity.model.CalendarDateModel;
import com.sensoro.smartcity.model.EventAlarmStatusModel;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.popup.AlarmPopUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AlarmHistoryLogActivityPresenter extends BasePresenter<IAlarmHistoryLogActivityView> implements IOnCreate, AlarmPopUtils.OnPopupCallbackListener, Runnable {
    private Activity mContext;
    private Long startTime;
    private Long endTime;
    private volatile int cur_page = 1;
    private String mSn;
    private final List<DeviceAlarmLogInfo> mDeviceAlarmLogInfoList = new ArrayList<>();
    private AlarmPopUtils alarmPopUtils;
    private DeviceAlarmLogInfo mCurrentDeviceAlarmLogInfo;
    private volatile boolean needFresh = false;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        mSn = mContext.getIntent().getStringExtra(Constants.EXTRA_SENSOR_SN);
        alarmPopUtils = new AlarmPopUtils(mContext);
        alarmPopUtils.setOnPopupCallbackListener(this);
        requestDataByFilter(Constants.DIRECTION_DOWN);
        mHandler.post(this);

    }

    private void scheduleRefresh() {
        if (needFresh) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isAttachedView()) {
                        synchronized (mDeviceAlarmLogInfoList) {
                            getView().updateAlarmListAdapter(mDeviceAlarmLogInfoList);
                        }
                    }
                    needFresh = false;
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
        if (alarmPopUtils != null) {
            alarmPopUtils.onDestroyPop();
        }
    }

    public void onClickHistoryConfirm(DeviceAlarmLogInfo deviceAlarmLogInfo) {
        mCurrentDeviceAlarmLogInfo = deviceAlarmLogInfo;
        //
        if (PreferencesHelper.getInstance().getAlarmPopupDataBeanCache() == null) {
            RetrofitServiceHelper.getInstance().getDevicesAlarmPopupConfig().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<AlarmPopupDataBean>>(this) {
                @Override
                public void onCompleted(ResponseResult<AlarmPopupDataBean> devicesAlarmPopupConfigRsp) {
                    PreferencesHelper.getInstance().saveAlarmPopupDataBeanCache(devicesAlarmPopupConfigRsp.getData());
                    final AlarmPopupModel alarmPopupModel = new AlarmPopupModel();
                    String deviceName = mCurrentDeviceAlarmLogInfo.getDeviceName();
                    if (TextUtils.isEmpty(deviceName)) {
                        alarmPopupModel.title = mCurrentDeviceAlarmLogInfo.getDeviceSN();
                    } else {
                        alarmPopupModel.title = deviceName;
                    }
                    alarmPopupModel.alarmStatus = mCurrentDeviceAlarmLogInfo.getAlarmStatus();
                    alarmPopupModel.updateTime = mCurrentDeviceAlarmLogInfo.getUpdatedTime();
                    alarmPopupModel.mergeType = WidgetUtil.handleMergeType(mCurrentDeviceAlarmLogInfo.getDeviceType());
                    alarmPopupModel.sensorType = mCurrentDeviceAlarmLogInfo.getSensorType();
                    //
                    AlarmPopupConfigAnalyzer.handleAlarmPopupModel(null, alarmPopupModel);
                    alarmPopUtils.show(alarmPopupModel);
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
            alarmPopUtils.show(alarmPopupModel);
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(EventAlarmStatusModel eventAlarmStatusModel) {
        switch (eventAlarmStatusModel.status) {
            case Constants.MODEL_ALARM_STATUS_EVENT_CODE_CREATE:
                // 做一些预警发生的逻辑
                handleSocketData(eventAlarmStatusModel.deviceAlarmLogInfo, true);
                break;
            case Constants.MODEL_ALARM_STATUS_EVENT_CODE_RECOVERY:
                break;
            // 做一些预警恢复的逻辑
            case Constants.MODEL_ALARM_STATUS_EVENT_CODE_CONFIRM:
                handleSocketData(eventAlarmStatusModel.deviceAlarmLogInfo, false);
                break;
            // 做一些预警被确认的逻辑
            case Constants.MODEL_ALARM_STATUS_EVENT_CODE_RECONFIRM:
                // 做一些预警被再次确认的逻辑
                break;
            default:
                // 未知逻辑 可以联系我确认 有可能是bug
                break;
        }
    }

    private void handleSocketData(DeviceAlarmLogInfo deviceAlarmLogInfo, boolean isNewInfo) {
        synchronized (mDeviceAlarmLogInfoList) {
            // 处理只针对当前集合做处理
            boolean needAdd = true;
            for (int i = 0; i < mDeviceAlarmLogInfoList.size(); i++) {
                DeviceAlarmLogInfo tempLogInfo = mDeviceAlarmLogInfoList.get(i);
                if (tempLogInfo.get_id().equals(deviceAlarmLogInfo.get_id())) {
                    mDeviceAlarmLogInfoList.set(i, deviceAlarmLogInfo);
                    needAdd = false;
                    needFresh = true;
                    break;
                }
            }
            if (needAdd && isNewInfo) {
                if (mSn.equals(deviceAlarmLogInfo.getDeviceSN())) {
                    if (mDeviceAlarmLogInfoList.size() > 0) {
                        DeviceAlarmLogInfo current = mDeviceAlarmLogInfoList.get(0);
                        long newCreatedTime = deviceAlarmLogInfo.getCreatedTime();
                        long createdTime = current.getCreatedTime();
                        if (newCreatedTime > createdTime) {
                            mDeviceAlarmLogInfoList.add(0, deviceAlarmLogInfo);
                            needFresh = true;
                        }
                    } else {
                        mDeviceAlarmLogInfoList.add(deviceAlarmLogInfo);
                        needFresh = true;
                    }
                }
            }

        }
    }


    public void onCalendarBack(CalendarDateModel calendarDateModel) {
        if (isAttachedView()) {
            getView().setDateSelectVisible(true);
        }
        startTime = DateUtil.strToDate(calendarDateModel.startDate).getTime();
        endTime = DateUtil.strToDate(calendarDateModel.endDate).getTime();
        if (isAttachedView()) {
            getView().setDateSelectText(DateUtil.getCalendarYearMothDayFormatDate(startTime) + " ~ " + DateUtil
                    .getCalendarYearMothDayFormatDate(endTime));
        }
//        getView().setSelectedDateSearchText(DateUtil.getMothDayFormatDate(startTime) + "-" + DateUtil
//                .getMothDayFormatDate(endTime));
        endTime += 1000 * 60 * 60 * 24;
        requestDataByFilter(Constants.DIRECTION_DOWN);
    }

    public void requestDataByFilter(final int direction) {
        switch (direction) {
            case Constants.DIRECTION_DOWN:
                cur_page = 1;
                if (isAttachedView()) {
                    getView().showProgressDialog();
                }
                RetrofitServiceHelper.getInstance().getDeviceAlarmLogList(cur_page, mSn, null, null, null, startTime,
                        endTime,
                        null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<DeviceAlarmLogInfo>>>(this) {

                    @Override
                    public void onCompleted(ResponseResult<List<DeviceAlarmLogInfo>> deviceAlarmLogRsp) {
                        freshUI(direction, deviceAlarmLogRsp);
                        getView().onPullRefreshComplete();
                        getView().dismissProgressDialog();
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        freshEmptyData();
                        getView().onPullRefreshComplete();
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }
                });
                break;
            case Constants.DIRECTION_UP:
                cur_page++;
                if (isAttachedView()) {
                    getView().showProgressDialog();
                }
                RetrofitServiceHelper.getInstance().getDeviceAlarmLogList(cur_page, mSn, null, null, null, startTime,
                        endTime,
                        null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<DeviceAlarmLogInfo>>>(this) {


                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        cur_page--;
                        getView().onPullRefreshComplete();
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }

                    @Override
                    public void onCompleted(ResponseResult<List<DeviceAlarmLogInfo>> deviceAlarmLogRsp) {
                        getView().dismissProgressDialog();
                        List<DeviceAlarmLogInfo> data = deviceAlarmLogRsp.getData();
                        if (data == null || data.size() == 0) {
                            cur_page--;
                            getView().toastShort(mContext.getString(R.string.no_more_data));
                        } else {
                            freshUI(direction, deviceAlarmLogRsp);
                        }
                        getView().onPullRefreshComplete();
                    }
                });
                break;
            default:
                break;
        }


    }

    private void freshEmptyData() {
        if (isAttachedView()) {
            synchronized (mDeviceAlarmLogInfoList) {
                mDeviceAlarmLogInfoList.clear();
                getView().updateAlarmListAdapter(mDeviceAlarmLogInfoList);
            }
        }
    }

    private void freshUI(int direction, ResponseResult<List<DeviceAlarmLogInfo>> deviceAlarmLogRsp) {
        final List<DeviceAlarmLogInfo> deviceAlarmLogInfoList = deviceAlarmLogRsp.getData();
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                synchronized (mDeviceAlarmLogInfoList) {
                    if (direction == Constants.DIRECTION_DOWN) {
                        mDeviceAlarmLogInfoList.clear();
                    }
                    mDeviceAlarmLogInfoList.addAll(deviceAlarmLogInfoList);
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isAttachedView()) {
                                getView().updateAlarmListAdapter(mDeviceAlarmLogInfoList);
                            }

                        }
                    });
                }
            }
        });
    }

    private void pushAlarmFresh(DeviceAlarmLogInfo deviceAlarmLogInfo) {
        EventData eventData = new EventData();
        eventData.code = Constants.EVENT_DATA_ALARM_FRESH_ALARM_DATA;
        eventData.data = deviceAlarmLogInfo;
        EventBus.getDefault().post(eventData);
    }

    private void freshDeviceAlarmLogInfo(DeviceAlarmLogInfo deviceAlarmLogInfo) {
        synchronized (mDeviceAlarmLogInfoList) {
            // 处理只针对当前集合做处理
            boolean canRefresh = false;
            for (int i = 0; i < mDeviceAlarmLogInfoList.size(); i++) {
                DeviceAlarmLogInfo tempLogInfo = mDeviceAlarmLogInfoList.get(i);
                if (tempLogInfo.get_id().equals(deviceAlarmLogInfo.get_id())) {
                    mDeviceAlarmLogInfoList.set(i, deviceAlarmLogInfo);
                    canRefresh = true;
                    break;
                }
            }
            if (canRefresh) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isAttachedView()) {
                            getView().updateAlarmListAdapter(mDeviceAlarmLogInfoList);
                        }
                    }
                });
            }

        }
    }

    public void closeDateSearch() {
        if (isAttachedView()) {
            getView().setDateSelectVisible(false);
        }
        startTime = null;
        endTime = null;
        requestDataByFilter(Constants.DIRECTION_DOWN);
    }

    public void doSelectDate() {
        if (startTime == null || endTime == null) {
            startTime = -1L;
            endTime = -1L;
        }
        if (isAttachedView()) {
            getView().showCalendar(startTime, endTime);
        }
    }


    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPopupCallback(AlarmPopupModel alarmPopupModel, List<ScenesData> scenesDataList) {
        if (isAttachedView()) {
            getView().showProgressDialog();
        }
        Map<String, Integer> alarmPopupServerData = AlarmPopupConfigAnalyzer.createAlarmPopupServerData(alarmPopupModel);
        RetrofitServiceHelper.getInstance().doUpdatePhotosUrl(mCurrentDeviceAlarmLogInfo.get_id(), alarmPopupServerData, alarmPopupModel.securityRisksList,
                alarmPopupModel.mRemark, false, scenesDataList).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<ResponseResult<DeviceAlarmLogInfo>>(this) {

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        if (isAttachedView()) {
                            getView().dismissProgressDialog();
                            getView().toastShort(errorMsg);
                        }
                    }

                    @Override
                    public void onCompleted(ResponseResult<DeviceAlarmLogInfo> deviceAlarmItemRsp) {
                        if (deviceAlarmItemRsp.getErrcode() == ResponseResult.CODE_SUCCESS) {
                            DeviceAlarmLogInfo deviceAlarmLogInfo = deviceAlarmItemRsp.getData();
                            if (isAttachedView()) {
                                getView().toastShort(mContext.getResources().getString(R.string
                                        .tips_commit_success));
                            }
                            freshDeviceAlarmLogInfo(deviceAlarmLogInfo);
                            pushAlarmFresh(deviceAlarmLogInfo);
                        } else {
                            if (isAttachedView()) {
                                getView().toastShort(mContext.getResources().getString(R.string
                                        .tips_commit_failed));
                            }
                        }
                        if (isAttachedView()) {
                            getView().dismissProgressDialog();
                        }
                        if (alarmPopUtils != null) {
                            alarmPopUtils.dismiss();
                        }
                    }
                });
    }

    @Override
    public void run() {
        scheduleRefresh();
        mHandler.postDelayed(this, 3000);
    }

}

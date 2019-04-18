package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;

import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeviceCameraActivityView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.CalendarDateModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.server.bean.DeviceCameraInfo;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.server.response.DeviceCameraListRsp;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.widget.popup.AlarmPopUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DeviceCameraActivityPresenter extends BasePresenter<IDeviceCameraActivityView> implements IOnCreate, Constants, AlarmPopUtils.OnPopupCallbackListener {
    private Activity mContext;
    private Long startTime;
    private Long endTime;
    private volatile int cur_page = 1;
    private final List<DeviceAlarmLogInfo> mDeviceAlarmLogInfoList = new ArrayList<>();
    private final Comparator<DeviceAlarmLogInfo> deviceAlarmLogInfoComparator = new Comparator<DeviceAlarmLogInfo>() {
        @Override
        public int compare(DeviceAlarmLogInfo o1, DeviceAlarmLogInfo o2) {
            long l = o2.getCreatedTime() - o1.getCreatedTime();
            if (l > 0) {
                return 1;
            } else if (l < 0) {
                return -1;
            } else {
                return 0;
            }

        }
    };

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
//        requestDataByFilter(DIRECTION_DOWN);
        requestData();

    }

    private void requestData() {
        getView().showProgressDialog();
        RetrofitServiceHelper.getInstance().getDeviceCameraList(null, null, null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceCameraListRsp>(null) {
            @Override
            public void onCompleted(DeviceCameraListRsp deviceCameraListRsp) {
                List<DeviceCameraInfo> data = deviceCameraListRsp.getData();
                getView().updateDeviceCameraAdapter(data);
                getView().dismissProgressDialog();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    public void onClickDeviceCamera(DeviceCameraInfo deviceCameraInfo) {

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        //
        switch (code) {
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
//        requestDataByFilter(DIRECTION_DOWN);
    }

//    public void requestDataByFilter(final int direction) {
//        switch (direction) {
//            case DIRECTION_DOWN:
//                cur_page = 1;
//                if (isAttachedView()) {
//                    getView().showProgressDialog();
//                }
//                RetrofitServiceHelper.getInstance().getDeviceAlarmLogList(cur_page, mSn, null, null, null, startTime,
//                        endTime,
//                        null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>(this) {
//
//                    @Override
//                    public void onCompleted(DeviceAlarmLogRsp deviceAlarmLogRsp) {
//                        freshUI(direction, deviceAlarmLogRsp);
//                        if (isAttachedView()) {
//                            getView().onPullRefreshComplete();
//                            getView().dismissProgressDialog();
//                        }
//                    }
//
//                    @Override
//                    public void onErrorMsg(int errorCode, String errorMsg) {
//                        if (isAttachedView()) {
//                            getView().onPullRefreshComplete();
//                            getView().dismissProgressDialog();
//                            getView().toastShort(errorMsg);
//                        }
//                    }
//                });
//                break;
//            case DIRECTION_UP:
//                cur_page++;
//                if (isAttachedView()) {
//                    getView().showProgressDialog();
//                }
//                RetrofitServiceHelper.getInstance().getDeviceAlarmLogList(cur_page, mSn, null, null, null, startTime,
//                        endTime,
//                        null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>(this) {
//
//
//                    @Override
//                    public void onErrorMsg(int errorCode, String errorMsg) {
//                        cur_page--;
//                        if (isAttachedView()) {
//                            getView().onPullRefreshComplete();
//                            getView().dismissProgressDialog();
//                            getView().toastShort(errorMsg);
//                        }
//                    }
//
//                    @Override
//                    public void onCompleted(DeviceAlarmLogRsp deviceAlarmLogRsp) {
//                        if (isAttachedView()) {
//                            getView().dismissProgressDialog();
//                        }
//                        if (deviceAlarmLogRsp.getData().size() == 0) {
//                            cur_page--;
//                            if (isAttachedView()) {
//                                getView().toastShort(mContext.getString(R.string.no_more_data));
//                                getView().onPullRefreshCompleteNoMoreData();
//                            }
//                        } else {
//                            freshUI(direction, deviceAlarmLogRsp);
//                            if (isAttachedView()) {
//                                getView().onPullRefreshComplete();
//                            }
//                        }
//                    }
//                });
//                break;
//            default:
//                break;
//        }
//
//
//    }

//    private void freshUI(int direction, DeviceAlarmLogRsp deviceAlarmLogRsp) {
//        if (direction == DIRECTION_DOWN) {
//            mDeviceAlarmLogInfoList.clear();
//        }
//        final List<DeviceAlarmLogInfo> deviceAlarmLogInfoList = deviceAlarmLogRsp.getData();
//        ThreadPoolManager.getInstance().execute(new Runnable() {
//            @Override
//            public void run() {
//                synchronized (mDeviceAlarmLogInfoList) {
//                    out:
//                    for (int i = 0; i < deviceAlarmLogInfoList.size(); i++) {
//                        DeviceAlarmLogInfo deviceAlarmLogInfo = deviceAlarmLogInfoList.get(i);
//                        for (int j = 0; j < mDeviceAlarmLogInfoList.size(); j++) {
//                            if (mDeviceAlarmLogInfoList.get(j).get_id().equals(deviceAlarmLogInfo.get_id())) {
//                                mDeviceAlarmLogInfoList.set(i, deviceAlarmLogInfo);
//                                break out;
//                            }
//                        }
//                        mDeviceAlarmLogInfoList.add(deviceAlarmLogInfo);
//                    }
//                    Collections.sort(mDeviceAlarmLogInfoList, deviceAlarmLogInfoComparator);
//                    mContext.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (isAttachedView()) {
//                                getView().updateAlarmListAdapter(mDeviceAlarmLogInfoList);
//                            }
//                        }
//                    });
//                }
//            }
//        });
////        handleDeviceAlarmLogs(deviceAlarmLogRsp);
////        getView().updateAlarmListAdapter(mDeviceAlarmLogInfoList);
//    }

    private void pushAlarmFresh(DeviceAlarmLogInfo deviceAlarmLogInfo) {
        EventData eventData = new EventData();
        eventData.code = EVENT_DATA_ALARM_FRESH_ALARM_DATA;
        eventData.data = deviceAlarmLogInfo;
        EventBus.getDefault().post(eventData);
    }

//    private void freshDeviceAlarmLogInfo(DeviceAlarmLogInfo deviceAlarmLogInfo) {
//        synchronized (mDeviceAlarmLogInfoList) {
//            // 处理只针对当前集合做处理
//            boolean canRefresh = false;
//            for (int i = 0; i < mDeviceAlarmLogInfoList.size(); i++) {
//                DeviceAlarmLogInfo tempLogInfo = mDeviceAlarmLogInfoList.get(i);
//                if (tempLogInfo.get_id().equals(deviceAlarmLogInfo.get_id())) {
//                    AlarmInfo.RecordInfo[] recordInfoArray = deviceAlarmLogInfo.getRecords();
//                    deviceAlarmLogInfo.setSort(1);
//                    for (AlarmInfo.RecordInfo recordInfo : recordInfoArray) {
//                        if (recordInfo.getType().equals("recovery")) {
//                            deviceAlarmLogInfo.setSort(4);
//                            break;
//                        }
//                    }
//                    mDeviceAlarmLogInfoList.set(i, deviceAlarmLogInfo);
//                    canRefresh = true;
//                    break;
//                }
//            }
//            if (canRefresh) {
//                Collections.sort(mDeviceAlarmLogInfoList, deviceAlarmLogInfoComparator);
//                mContext.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (isAttachedView()) {
//                            getView().updateAlarmListAdapter(mDeviceAlarmLogInfoList);
//                        }
//                    }
//                });
//            }
//
//        }
//    }

    public void closeDateSearch() {
        if (isAttachedView()) {
            getView().setDateSelectVisible(false);
        }
        startTime = null;
        endTime = null;
//        requestDataByFilter(DIRECTION_DOWN);
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
    public void onPopupCallback(int statusResult, int statusType, int statusPlace, List<ScenesData> scenesDataList, String remark) {
        if (isAttachedView()) {
            getView().showProgressDialog();
        }
//        RetrofitServiceHelper.getInstance().doUpdatePhotosUrl(mCurrentDeviceAlarmLogInfo.get_id(), statusResult,
//                statusType, statusPlace,
//                remark, false, scenesDataList).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                .subscribe
//                        (new CityObserver<DeviceAlarmItemRsp>(this) {
//
//
//                            @Override
//                            public void onErrorMsg(int errorCode, String errorMsg) {
//                                if (isAttachedView()) {
//                                    getView().dismissProgressDialog();
//                                    getView().toastShort(errorMsg);
//                                }
//                            }
//
//                            @Override
//                            public void onCompleted(DeviceAlarmItemRsp deviceAlarmItemRsp) {
//                                if (deviceAlarmItemRsp.getErrcode() == ResponseBase.CODE_SUCCESS) {
//                                    DeviceAlarmLogInfo deviceAlarmLogInfo = deviceAlarmItemRsp.getData();
//                                    if (isAttachedView()) {
//                                        getView().toastShort(mContext.getResources().getString(R.string
//                                                .tips_commit_success));
//                                    }
//                                    freshDeviceAlarmLogInfo(deviceAlarmLogInfo);
//                                    pushAlarmFresh(deviceAlarmLogInfo);
//                                } else {
//                                    if (isAttachedView()) {
//                                        getView().toastShort(mContext.getResources().getString(R.string
//                                                .tips_commit_failed));
//                                    }
//                                }
//                                if (isAttachedView()) {
//                                    getView().dismissProgressDialog();
//                                }
//                                if (alarmPopUtils != null) {
//                                    alarmPopUtils.dismiss();
//                                }
//
//                            }
//                        });
    }


    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    //////////////////////////////////////////////////////////////
    //    private void freshDeviceAlarmLogInfo(DeviceAlarmLogInfo deviceAlarmLogInfo) {
//        if (mDeviceAlarmLogInfoList.contains(deviceAlarmLogInfo)) {
//            for (int i = 0; i < mDeviceAlarmLogInfoList.size(); i++) {
//                DeviceAlarmLogInfo tempLogInfo = mDeviceAlarmLogInfoList.get(i);
//                if (tempLogInfo.get_id().equals(deviceAlarmLogInfo.get_id())) {
//                    AlarmInfo.RecordInfo[] recordInfoArray = deviceAlarmLogInfo.getRecords();
//                    deviceAlarmLogInfo.setSort(1);
//                    for (int j = 0; j < recordInfoArray.length; j++) {
//                        AlarmInfo.RecordInfo recordInfo = recordInfoArray[j];
//                        if (recordInfo.getType().equals("recovery")) {
//                            deviceAlarmLogInfo.setSort(4);
//                            break;
//                        }
//                    }
//                    mDeviceAlarmLogInfoList.set(i, deviceAlarmLogInfo);
//                    break;
//                }
//            }
//        } else {
//            mDeviceAlarmLogInfoList.add(0, deviceAlarmLogInfo);
//        }
//
//        getView().updateAlarmListAdapter(mDeviceAlarmLogInfoList);
//    }

    //    /**
//     * 处理接收的数据
//     */
//    private void handleDeviceAlarmLogs(DeviceAlarmLogRsp deviceAlarmLogRsp) {
//        List<DeviceAlarmLogInfo> deviceAlarmLogInfoList = deviceAlarmLogRsp.getData();
//        for (int i = 0; i < deviceAlarmLogInfoList.size(); i++) {
//            DeviceAlarmLogInfo deviceAlarmLogInfo = deviceAlarmLogInfoList.get(i);
//            AlarmInfo.RecordInfo[] recordInfoArray = deviceAlarmLogInfo.getRecords();
//            boolean isHaveRecovery = false;
//            for (AlarmInfo.RecordInfo recordInfo : recordInfoArray) {
//                if (recordInfo.getType().equals("recovery")) {
//                    deviceAlarmLogInfo.setSort(4);
//                    isHaveRecovery = true;
//                    break;
//                } else {
//                    deviceAlarmLogInfo.setSort(1);
//                }
//            }
//            switch (deviceAlarmLogInfo.getDisplayStatus()) {
//                case DISPLAY_STATUS_CONFIRM:
//                    if (isHaveRecovery) {
//                        deviceAlarmLogInfo.setSort(2);
//                    } else {
//                        deviceAlarmLogInfo.setSort(1);
//                    }
//                    break;
//                case DISPLAY_STATUS_ALARM:
//                    if (isHaveRecovery) {
//                        deviceAlarmLogInfo.setSort(2);
//                    } else {
//                        deviceAlarmLogInfo.setSort(1);
//                    }
//                    break;
//                case DISPLAY_STATUS_MIS_DESCRIPTION:
//                    if (isHaveRecovery) {
//                        deviceAlarmLogInfo.setSort(3);
//                    } else {
//                        deviceAlarmLogInfo.setSort(1);
//                    }
//                    break;
//                case DISPLAY_STATUS_TEST:
//                    if (isHaveRecovery) {
//                        deviceAlarmLogInfo.setSort(4);
//                    } else {
//                        deviceAlarmLogInfo.setSort(1);
//                    }
//                    break;
//                default:
//                    break;
//            }
//            mDeviceAlarmLogInfoList.add(deviceAlarmLogInfo);
//        }
//        //            Collections.sort(mDeviceAlarmLogInfoList);
//    }

    //    private void freshDeviceAlarmLogInfo(DeviceAlarmLogInfo deviceAlarmLogInfo) {
//        for (int i = 0; i < mDeviceAlarmLogInfoList.size(); i++) {
//            DeviceAlarmLogInfo tempLogInfo = mDeviceAlarmLogInfoList.get(i);
//            if (tempLogInfo.get_id().equals(deviceAlarmLogInfo.get_id())) {
//                AlarmInfo.RecordInfo[] recordInfoArray = deviceAlarmLogInfo.getRecords();
//                deviceAlarmLogInfo.setSort(1);
//                for (int j = 0; j < recordInfoArray.length; j++) {
//                    AlarmInfo.RecordInfo recordInfo = recordInfoArray[j];
//                    if (recordInfo.getType().equals("recovery")) {
//                        deviceAlarmLogInfo.setSort(4);
//                        break;
//                    }
//                }
//                mDeviceAlarmLogInfoList.set(i, deviceAlarmLogInfo);
//////                Collections.sort(mDeviceAlarmLogInfoList);
//                break;
//            }
//        }
//        ArrayList<DeviceAlarmLogInfo> tempList = new ArrayList<>(mDeviceAlarmLogInfoList);
//        getView().updateAlarmListAdapter(tempList);
//        pushAlarmCount(tempList);
//    }

}

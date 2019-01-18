package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IAlarmHistoryLogActivityView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.AlarmPopModel;
import com.sensoro.smartcity.model.CalendarDateModel;
import com.sensoro.smartcity.model.EventAlarmStatusModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.push.ThreadPoolManager;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.server.response.DeviceAlarmItemRsp;
import com.sensoro.smartcity.server.response.DeviceAlarmLogRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.smartcity.widget.imagepicker.bean.ImageItem;
import com.sensoro.smartcity.widget.popup.AlarmPopUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.sensoro.smartcity.widget.imagepicker.ImagePicker.EXTRA_RESULT_BY_TAKE_PHOTO;

public class AlarmHistoryLogActivityPresenter extends BasePresenter<IAlarmHistoryLogActivityView> implements IOnCreate, Constants, AlarmPopUtils.OnPopupCallbackListener {
    private Activity mContext;
    private Long startTime;
    private Long endTime;
    private volatile int cur_page = 1;
    private String mSn;
    private final List<DeviceAlarmLogInfo> mDeviceAlarmLogInfoList = new ArrayList<>();
    private AlarmPopUtils alarmPopUtils;
    private DeviceAlarmLogInfo mCurrentDeviceAlarmLogInfo;
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
        mSn = mContext.getIntent().getStringExtra(EXTRA_SENSOR_SN);
        alarmPopUtils = new AlarmPopUtils(mContext);
        alarmPopUtils.setOnPopupCallbackListener(this);
        requestDataByFilter(DIRECTION_DOWN);

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (alarmPopUtils != null) {
            alarmPopUtils.onDestroyPop();
        }
    }

    public void onClickHistoryConfirm(DeviceAlarmLogInfo deviceAlarmLogInfo) {
        mCurrentDeviceAlarmLogInfo = deviceAlarmLogInfo;
        alarmPopUtils.show();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        //
        switch (code) {
            case EVENT_DATA_ALARM_SOCKET_DISPLAY_STATUS:
                if (data instanceof EventAlarmStatusModel) {
                    EventAlarmStatusModel tempEventAlarmStatusModel = (EventAlarmStatusModel) data;
                    switch (tempEventAlarmStatusModel.status) {
                        case MODEL_ALARM_STATUS_EVENT_CODE_CREATE:
                            // 做一些预警发生的逻辑
                            handleSocketData(tempEventAlarmStatusModel.deviceAlarmLogInfo, true);
                            break;
                        case MODEL_ALARM_STATUS_EVENT_CODE_RECOVERY:
                            break;
                        // 做一些预警恢复的逻辑
                        case MODEL_ALARM_STATUS_EVENT_CODE_CONFIRM:
                            handleSocketData(tempEventAlarmStatusModel.deviceAlarmLogInfo, false);
                            break;
                        // 做一些预警被确认的逻辑
                        case MODEL_ALARM_STATUS_EVENT_CODE_RECONFIRM:
                            // 做一些预警被再次确认的逻辑
                            break;
                        default:
                            // 未知逻辑 可以联系我确认 有可能是bug
                            break;
                    }
                }
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
                    AlarmInfo.RecordInfo[] recordInfoArray = deviceAlarmLogInfo.getRecords();
                    deviceAlarmLogInfo.setSort(1);
                    for (AlarmInfo.RecordInfo recordInfo : recordInfoArray) {
                        if (recordInfo.getType().equals("recovery")) {
                            deviceAlarmLogInfo.setSort(4);
                            break;
                        }
                    }
                    mDeviceAlarmLogInfoList.set(i, deviceAlarmLogInfo);
                    needAdd = false;
                    break;
                }
            }
            if (needAdd && isNewInfo) {
                mDeviceAlarmLogInfoList.add(0, deviceAlarmLogInfo);
            }
            Collections.sort(mDeviceAlarmLogInfoList, deviceAlarmLogInfoComparator);
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isAttachedView()){
                        getView().updateAlarmListAdapter(mDeviceAlarmLogInfoList);
                    }
                }
            });

        }
    }

    public void handlerActivityResult(int requestCode, int resultCode, Intent data) {
        //对照片信息统一处理
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> tempImages = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (tempImages != null) {
                    boolean fromTakePhoto = data.getBooleanExtra(EXTRA_RESULT_BY_TAKE_PHOTO, false);
                    EventData eventData = new EventData();
                    eventData.code = EVENT_DATA_ALARM_POP_IMAGES;
                    AlarmPopModel alarmPopModel = new AlarmPopModel();
                    alarmPopModel.requestCode = requestCode;
                    alarmPopModel.resultCode = resultCode;
                    alarmPopModel.fromTakePhoto = fromTakePhoto;
                    alarmPopModel.imageItems = tempImages;
                    eventData.data = alarmPopModel;
                    EventBus.getDefault().post(eventData);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (requestCode == REQUEST_CODE_PREVIEW && data != null) {
                ArrayList<ImageItem> tempImages = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (tempImages != null) {
                    EventData eventData = new EventData();
                    eventData.code = EVENT_DATA_ALARM_POP_IMAGES;
                    AlarmPopModel alarmPopModel = new AlarmPopModel();
                    alarmPopModel.requestCode = requestCode;
                    alarmPopModel.resultCode = resultCode;
                    alarmPopModel.imageItems = tempImages;
                    eventData.data = alarmPopModel;
                    EventBus.getDefault().post(eventData);
                }
            }
        } else if (resultCode == RESULT_CODE_RECORD) {
            //拍视频
            if (data != null && requestCode == REQUEST_CODE_RECORD) {
                ImageItem imageItem = (ImageItem) data.getSerializableExtra("path_record");
                if (imageItem != null) {
                    try {
                        LogUtils.loge("--- 从视频返回  path = " + imageItem.path);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    ArrayList<ImageItem> tempImages = new ArrayList<>();
                    tempImages.add(imageItem);
                    EventData eventData = new EventData();
                    eventData.code = EVENT_DATA_ALARM_POP_IMAGES;
                    AlarmPopModel alarmPopModel = new AlarmPopModel();
                    alarmPopModel.requestCode = requestCode;
                    alarmPopModel.resultCode = resultCode;
                    alarmPopModel.imageItems = tempImages;
                    eventData.data = alarmPopModel;
                    EventBus.getDefault().post(eventData);
                }
            } else if (requestCode == REQUEST_CODE_PLAY_RECORD) {
                EventData eventData = new EventData();
                eventData.code = EVENT_DATA_ALARM_POP_IMAGES;
                AlarmPopModel alarmPopModel = new AlarmPopModel();
                alarmPopModel.requestCode = requestCode;
                alarmPopModel.resultCode = resultCode;
                eventData.data = alarmPopModel;
                EventBus.getDefault().post(eventData);
            }

        }
    }

    public void onCalendarBack(CalendarDateModel calendarDateModel) {
        if (isAttachedView()){
            getView().setDateSelectVisible(true);
        }
        startTime = DateUtil.strToDate(calendarDateModel.startDate).getTime();
        endTime = DateUtil.strToDate(calendarDateModel.endDate).getTime();
        if (isAttachedView()){
            getView().setDateSelectText(DateUtil.getCalendarYearMothDayFormatDate(startTime) + " ~ " + DateUtil
                    .getCalendarYearMothDayFormatDate(endTime));
        }
//        getView().setSelectedDateSearchText(DateUtil.getMothDayFormatDate(startTime) + "-" + DateUtil
//                .getMothDayFormatDate(endTime));
        endTime += 1000 * 60 * 60 * 24;
        requestDataByFilter(DIRECTION_DOWN);
    }

    public void requestDataByFilter(final int direction) {
        switch (direction) {
            case DIRECTION_DOWN:
                cur_page = 1;
                if (isAttachedView()){
                    getView().showProgressDialog();
                }
                RetrofitServiceHelper.INSTANCE.getDeviceAlarmLogList(cur_page, mSn, null, null, null, startTime,
                        endTime,
                        null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>(this) {

                    @Override
                    public void onCompleted(DeviceAlarmLogRsp deviceAlarmLogRsp) {
                        freshUI(direction, deviceAlarmLogRsp);
                        if (isAttachedView()){
                            getView().onPullRefreshComplete();
                            getView().dismissProgressDialog();
                        }
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        if (isAttachedView()){
                            getView().onPullRefreshComplete();
                            getView().dismissProgressDialog();
                            getView().toastShort(errorMsg);
                        }
                    }
                });
                break;
            case DIRECTION_UP:
                cur_page++;
                if (isAttachedView()){
                    getView().showProgressDialog();
                }
                RetrofitServiceHelper.INSTANCE.getDeviceAlarmLogList(cur_page, mSn, null, null, null, startTime,
                        endTime,
                        null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>(this) {


                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        cur_page--;
                        if (isAttachedView()){
                            getView().onPullRefreshComplete();
                            getView().dismissProgressDialog();
                            getView().toastShort(errorMsg);
                        }
                    }

                    @Override
                    public void onCompleted(DeviceAlarmLogRsp deviceAlarmLogRsp) {
                        if (isAttachedView()){
                            getView().dismissProgressDialog();
                        }
                        if (deviceAlarmLogRsp.getData().size() == 0) {
                            cur_page--;
                            if (isAttachedView()){
                                getView().toastShort(mContext.getString(R.string.no_more_data));
                                getView().onPullRefreshCompleteNoMoreData();
                            }
                        } else {
                            freshUI(direction, deviceAlarmLogRsp);
                            if (isAttachedView()){
                                getView().onPullRefreshComplete();
                            }
                        }
                    }
                });
                break;
            default:
                break;
        }


    }

    private void freshUI(int direction, DeviceAlarmLogRsp deviceAlarmLogRsp) {
        if (direction == DIRECTION_DOWN) {
            mDeviceAlarmLogInfoList.clear();
        }
        final List<DeviceAlarmLogInfo> deviceAlarmLogInfoList = deviceAlarmLogRsp.getData();
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                synchronized (mDeviceAlarmLogInfoList) {
                    out:
                    for (int i = 0; i < deviceAlarmLogInfoList.size(); i++) {
                        DeviceAlarmLogInfo deviceAlarmLogInfo = deviceAlarmLogInfoList.get(i);
                        for (int j = 0; j < mDeviceAlarmLogInfoList.size(); j++) {
                            if (mDeviceAlarmLogInfoList.get(j).get_id().equals(deviceAlarmLogInfo.get_id())) {
                                mDeviceAlarmLogInfoList.set(i, deviceAlarmLogInfo);
                                break out;
                            }
                        }
                        mDeviceAlarmLogInfoList.add(deviceAlarmLogInfo);
                    }
                    Collections.sort(mDeviceAlarmLogInfoList, deviceAlarmLogInfoComparator);
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isAttachedView()){
                                getView().updateAlarmListAdapter(mDeviceAlarmLogInfoList);
                            }
                        }
                    });
                }
            }
        });
//        handleDeviceAlarmLogs(deviceAlarmLogRsp);
//        getView().updateAlarmListAdapter(mDeviceAlarmLogInfoList);
    }

    private void pushAlarmFresh(DeviceAlarmLogInfo deviceAlarmLogInfo) {
        EventData eventData = new EventData();
        eventData.code = EVENT_DATA_ALARM_FRESH_ALARM_DATA;
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
                    AlarmInfo.RecordInfo[] recordInfoArray = deviceAlarmLogInfo.getRecords();
                    deviceAlarmLogInfo.setSort(1);
                    for (AlarmInfo.RecordInfo recordInfo : recordInfoArray) {
                        if (recordInfo.getType().equals("recovery")) {
                            deviceAlarmLogInfo.setSort(4);
                            break;
                        }
                    }
                    mDeviceAlarmLogInfoList.set(i, deviceAlarmLogInfo);
                    canRefresh = true;
                    break;
                }
            }
            if (canRefresh) {
                Collections.sort(mDeviceAlarmLogInfoList, deviceAlarmLogInfoComparator);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isAttachedView()){
                            getView().updateAlarmListAdapter(mDeviceAlarmLogInfoList);
                        }
                    }
                });
            }

        }
    }

    public void closeDateSearch() {
        if (isAttachedView()){
            getView().setDateSelectVisible(false);
        }
        startTime = null;
        endTime = null;
        requestDataByFilter(DIRECTION_DOWN);
    }

    public void doSelectDate() {
        if (startTime == null || endTime == null) {
            startTime = -1L;
            endTime = -1L;
        }
        if (isAttachedView()){
            getView().showCalendar(startTime, endTime);
        }
    }

    @Override
    public void onPopupCallback(int statusResult, int statusType, int statusPlace, List<ScenesData> scenesDataList, String remark) {
        if (isAttachedView()){
            getView().showProgressDialog();
        }
        RetrofitServiceHelper.INSTANCE.doUpdatePhotosUrl(mCurrentDeviceAlarmLogInfo.get_id(), statusResult,
                statusType, statusPlace,
                remark, false, scenesDataList).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe
                        (new CityObserver<DeviceAlarmItemRsp>(this) {


                            @Override
                            public void onErrorMsg(int errorCode, String errorMsg) {
                                if (isAttachedView()){
                                    getView().dismissProgressDialog();
                                    getView().toastShort(errorMsg);
                                }
                            }

                            @Override
                            public void onCompleted(DeviceAlarmItemRsp deviceAlarmItemRsp) {
                                if (deviceAlarmItemRsp.getErrcode() == ResponseBase.CODE_SUCCESS) {
                                    DeviceAlarmLogInfo deviceAlarmLogInfo = deviceAlarmItemRsp.getData();
                                    if (isAttachedView()){
                                        getView().toastShort(mContext.getResources().getString(R.string
                                                .tips_commit_success));
                                    }
                                    freshDeviceAlarmLogInfo(deviceAlarmLogInfo);
                                    pushAlarmFresh(deviceAlarmLogInfo);
                                } else {
                                    if (isAttachedView()){
                                        getView().toastShort(mContext.getResources().getString(R.string
                                                .tips_commit_failed));
                                    }
                                }
                                if (isAttachedView()){
                                    getView().dismissProgressDialog();
                                }
                                if (alarmPopUtils != null) {
                                    alarmPopUtils.dismiss();
                                }

                            }
                        });
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

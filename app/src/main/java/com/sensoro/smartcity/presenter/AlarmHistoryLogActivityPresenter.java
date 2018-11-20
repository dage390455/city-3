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

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
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

    public void onClickHistoryConfirm(int position) {
        mCurrentDeviceAlarmLogInfo = mDeviceAlarmLogInfoList.get(position);
        alarmPopUtils.show();
        //
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        //TODO 可以修改以此种方式传递，方便管理
        int code = eventData.code;
        Object data = eventData.data;
        //
        switch (code) {
            case EVENT_DATA_ALARM_SOCKET_DISPLAY_STATUS:
                if (data instanceof EventAlarmStatusModel) {
                    EventAlarmStatusModel tempEventAlarmStatusModel = (EventAlarmStatusModel) data;
                    if (mCurrentDeviceAlarmLogInfo.getDeviceSN().equals(tempEventAlarmStatusModel.deviceAlarmLogInfo.getDeviceSN())) {
                        switch (tempEventAlarmStatusModel.status) {
                            case MODEL_ALARM_STATUS_EVENT_CODE_CREATE:
                                // 做一些预警发生的逻辑
                                mDeviceAlarmLogInfoList.add(0, tempEventAlarmStatusModel.deviceAlarmLogInfo);
                                getView().updateAlarmListAdapter(mDeviceAlarmLogInfoList);
                                break;
                            case MODEL_ALARM_STATUS_EVENT_CODE_RECOVERY:
                                // 做一些预警恢复的逻辑
                            case MODEL_ALARM_STATUS_EVENT_CODE_CONFIRM:
                                // 做一些预警被确认的逻辑
                            case MODEL_ALARM_STATUS_EVENT_CODE_RECONFIRM:
                                freshDeviceAlarmLogInfo(tempEventAlarmStatusModel.deviceAlarmLogInfo);
                                // 做一些预警被再次确认的逻辑
                                break;
                            default:
                                // 未知逻辑 可以联系我确认 有可能是bug
                                break;
                        }
                    }
                }
                break;
        }
    }

    public void handlerActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO 对照片信息统一处理
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
                    LogUtils.loge("--- 从视频返回  path = " + imageItem.path);
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
        getView().setDateSelectVisible(true);
        startTime = DateUtil.strToDate(calendarDateModel.startDate).getTime();
        endTime = DateUtil.strToDate(calendarDateModel.endDate).getTime();
        getView().setDateSelectText(DateUtil.getMothDayFormatDate(startTime) + "-" + DateUtil
                .getMothDayFormatDate(endTime));
//        getView().setSelectedDateSearchText(DateUtil.getMothDayFormatDate(startTime) + "-" + DateUtil
//                .getMothDayFormatDate(endTime));
        endTime += 1000 * 60 * 60 * 24;
        requestDataByFilter(DIRECTION_DOWN);
    }

    /**
     * 通过回显日期搜索
     *
     * @param startDate
     * @param endDate
     */
    private void requestDataByDate(String startDate, String endDate) {
        startTime = DateUtil.strToDate(startDate).getTime();
        endTime = DateUtil.strToDate(endDate).getTime();
//        getView().setSelectedDateSearchText(DateUtil.getMothDayFormatDate(startTime) + "-" + DateUtil
//                .getMothDayFormatDate(endTime));
        endTime += 1000 * 60 * 60 * 24;
//        getView().showProgressDialog();
//        RetrofitServiceHelper.INSTANCE.getDeviceAlarmLogList(1, null, null, null, startTime, endTime,
//                null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>(this) {
//
//
//            @Override
//            public void onCompleted(DeviceAlarmLogRsp deviceAlarmLogRsp) {
//                if (deviceAlarmLogRsp.getData().size() == 0) {
//                    getView().toastShort("没有更多数据了");
//                }
//                freshUI(DIRECTION_DOWN, deviceAlarmLogRsp, null);
//                getView().onPullRefreshComplete();
//                getView().dismissProgressDialog();
//            }
//
//            @Override
//            public void onErrorMsg(int errorCode, String errorMsg) {
//                getView().onPullRefreshComplete();
//                getView().dismissProgressDialog();
//                getView().toastShort(errorMsg);
//            }
//        });
    }

    public void requestDataByFilter(final int direction) {
        //
        switch (direction) {
            case DIRECTION_DOWN:
                cur_page = 1;
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getDeviceAlarmLogList(cur_page, mSn, null, null, null, startTime,
                        endTime,
                        null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>(this) {

                    @Override
                    public void onCompleted(DeviceAlarmLogRsp deviceAlarmLogRsp) {
                        freshUI(direction, deviceAlarmLogRsp);
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
                break;
            case DIRECTION_UP:
                cur_page++;
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getDeviceAlarmLogList(cur_page, mSn, null, null, null, startTime,
                        endTime,
                        null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>(this) {


                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        cur_page--;
                        getView().onPullRefreshComplete();
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }

                    @Override
                    public void onCompleted(DeviceAlarmLogRsp deviceAlarmLogRsp) {
                        getView().dismissProgressDialog();
                        if (deviceAlarmLogRsp.getData().size() == 0) {
                            cur_page--;
                            getView().toastShort(mContext.getString(R.string.no_more_data));
                            getView().onPullRefreshCompleteNoMoreData();
                        } else {
                            freshUI(direction, deviceAlarmLogRsp);
                            getView().onPullRefreshComplete();
                        }
                    }
                });
                break;
            default:
                break;
        }


    }

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

    private void freshUI(int direction, DeviceAlarmLogRsp deviceAlarmLogRsp) {
        if (direction == DIRECTION_DOWN) {
            mDeviceAlarmLogInfoList.clear();
        }
        handleDeviceAlarmLogs(deviceAlarmLogRsp);
        getView().updateAlarmListAdapter(mDeviceAlarmLogInfoList);
    }

    private void pushAlarmFresh(DeviceAlarmLogInfo deviceAlarmLogInfo) {
        EventData eventData = new EventData();
        eventData.code = EVENT_DATA_ALARM_FRESH_ALARM_DATA;
        eventData.data = deviceAlarmLogInfo;
        EventBus.getDefault().post(eventData);
    }

    /**
     * 处理接收的数据
     */
    private void handleDeviceAlarmLogs(DeviceAlarmLogRsp deviceAlarmLogRsp) {
        List<DeviceAlarmLogInfo> deviceAlarmLogInfoList = deviceAlarmLogRsp.getData();
        for (int i = 0; i < deviceAlarmLogInfoList.size(); i++) {
            DeviceAlarmLogInfo deviceAlarmLogInfo = deviceAlarmLogInfoList.get(i);
            AlarmInfo.RecordInfo[] recordInfoArray = deviceAlarmLogInfo.getRecords();
            boolean isHaveRecovery = false;
            for (AlarmInfo.RecordInfo recordInfo : recordInfoArray) {
                if (recordInfo.getType().equals("recovery")) {
                    deviceAlarmLogInfo.setSort(4);
                    isHaveRecovery = true;
                    break;
                } else {
                    deviceAlarmLogInfo.setSort(1);
                }
            }
            switch (deviceAlarmLogInfo.getDisplayStatus()) {
                case DISPLAY_STATUS_CONFIRM:
                    if (isHaveRecovery) {
                        deviceAlarmLogInfo.setSort(2);
                    } else {
                        deviceAlarmLogInfo.setSort(1);
                    }
                    break;
                case DISPLAY_STATUS_ALARM:
                    if (isHaveRecovery) {
                        deviceAlarmLogInfo.setSort(2);
                    } else {
                        deviceAlarmLogInfo.setSort(1);
                    }
                    break;
                case DISPLAY_STATUS_MIS_DESCRIPTION:
                    if (isHaveRecovery) {
                        deviceAlarmLogInfo.setSort(3);
                    } else {
                        deviceAlarmLogInfo.setSort(1);
                    }
                    break;
                case DISPLAY_STATUS_TEST:
                    if (isHaveRecovery) {
                        deviceAlarmLogInfo.setSort(4);
                    } else {
                        deviceAlarmLogInfo.setSort(1);
                    }
                    break;
                default:
                    break;
            }
            mDeviceAlarmLogInfoList.add(deviceAlarmLogInfo);
        }
        //            Collections.sort(mDeviceAlarmLogInfoList);
    }

    public void closeDateSearch() {
        getView().setDateSelectVisible(false);
        startTime = null;
        endTime = null;
        requestDataByFilter(DIRECTION_DOWN);
    }

    public void doSelectDate() {
        if (startTime == null || endTime == null) {
            startTime = -1L;
            endTime = -1L;
        }
        getView().showCalendar(startTime, endTime);
    }

    @Override
    public void onPopupCallback(int statusResult, int statusType, int statusPlace, List<ScenesData> scenesDataList, String remark) {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.doUpdatePhotosUrl(mCurrentDeviceAlarmLogInfo.get_id(), statusResult,
                statusType, statusPlace,
                remark, false, scenesDataList).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe
                        (new CityObserver<DeviceAlarmItemRsp>(this) {


                            @Override
                            public void onErrorMsg(int errorCode, String errorMsg) {
                                getView().dismissProgressDialog();
                                getView().toastShort(errorMsg);
                            }

                            @Override
                            public void onCompleted(DeviceAlarmItemRsp deviceAlarmItemRsp) {
                                if (deviceAlarmItemRsp.getErrcode() == ResponseBase.CODE_SUCCESS) {
                                    DeviceAlarmLogInfo deviceAlarmLogInfo = deviceAlarmItemRsp.getData();
                                    getView().toastShort(mContext.getResources().getString(R.string
                                            .tips_commit_success));
                                    freshDeviceAlarmLogInfo(deviceAlarmLogInfo);
                                    pushAlarmFresh(deviceAlarmLogInfo);
                                } else {
                                    getView().toastShort(mContext.getResources().getString(R.string
                                            .tips_commit_failed));
                                }
                                getView().dismissProgressDialog();
                                if (alarmPopUtils != null) {
                                    alarmPopUtils.dismiss();
                                }

                            }
                        });
    }

    private void freshDeviceAlarmLogInfo(DeviceAlarmLogInfo deviceAlarmLogInfo) {
        if (mDeviceAlarmLogInfoList.contains(deviceAlarmLogInfo)) {
            for (int i = 0; i < mDeviceAlarmLogInfoList.size(); i++) {
                DeviceAlarmLogInfo tempLogInfo = mDeviceAlarmLogInfoList.get(i);
                if (tempLogInfo.get_id().equals(deviceAlarmLogInfo.get_id())) {
                    AlarmInfo.RecordInfo[] recordInfoArray = deviceAlarmLogInfo.getRecords();
                    deviceAlarmLogInfo.setSort(1);
                    for (int j = 0; j < recordInfoArray.length; j++) {
                        AlarmInfo.RecordInfo recordInfo = recordInfoArray[j];
                        if (recordInfo.getType().equals("recovery")) {
                            deviceAlarmLogInfo.setSort(4);
                            break;
                        }
                    }
                    mDeviceAlarmLogInfoList.set(i, deviceAlarmLogInfo);
                    break;
                }
            }
        } else {
            mDeviceAlarmLogInfoList.add(0, deviceAlarmLogInfo);
        }

        getView().updateAlarmListAdapter(mDeviceAlarmLogInfoList);
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }
}

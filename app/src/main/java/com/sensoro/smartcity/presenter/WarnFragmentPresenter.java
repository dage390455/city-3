package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.LinearLayout;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.AlarmDetailLogActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IWarnFragmentView;
import com.sensoro.smartcity.iwidget.IOnCreate;
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
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.widget.popup.AlarmPopUtils;
import com.sensoro.smartcity.widget.popup.CalendarPopUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class WarnFragmentPresenter extends BasePresenter<IWarnFragmentView> implements IOnCreate, Constants,
        AlarmPopUtils.OnPopupCallbackListener, CalendarPopUtils.OnCalendarPopupCallbackListener {
    private final List<DeviceAlarmLogInfo> mDeviceAlarmLogInfoList = new ArrayList<>();
    private volatile int cur_page = 1;
    private long startTime;
    private long endTime;
    private Activity mContext;
    private boolean isReConfirm = false;
    private DeviceAlarmLogInfo mCurrentDeviceAlarmLogInfo;
    private CalendarPopUtils mCalendarPopUtils;
    private String tempSearch;

    //
    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        mCalendarPopUtils = new CalendarPopUtils(mContext);
        mCalendarPopUtils.setOnCalendarPopupCallbackListener(this);
        if (PreferencesHelper.getInstance().getUserData().hasAlarmInfo) {
            requestSearchData(DIRECTION_DOWN, null);
        }
    }

    public void doContactOwner(int position) {
        DeviceAlarmLogInfo deviceAlarmLogInfo = mDeviceAlarmLogInfoList.get(position);
        String phoneNumber = deviceAlarmLogInfo.getDeviceNotification().getContent();
        if (TextUtils.isEmpty(phoneNumber)) {
            getView().toastShort(mContext.getString(R.string.no_find_contact_phone_number));
        } else {
            AppUtils.diallPhone(phoneNumber, mContext);
        }

    }

    private void freshUI(int direction, DeviceAlarmLogRsp deviceAlarmLogRsp) {
        if (direction == DIRECTION_DOWN) {
            mDeviceAlarmLogInfoList.clear();
        }
        handleDeviceAlarmLogs(deviceAlarmLogRsp);
        if (!TextUtils.isEmpty(tempSearch)) {
//            getView().setSelectedDateSearchText(searchText);
            getView().setSearchButtonTextVisible(true);
        } else {
            getView().setSearchButtonTextVisible(false);
        }
        getView().updateAlarmListAdapter(mDeviceAlarmLogInfoList);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mDeviceAlarmLogInfoList.clear();
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
            for (int j = 0; j < recordInfoArray.length; j++) {
                AlarmInfo.RecordInfo recordInfo = recordInfoArray[j];
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

    /**
     * 必须
     *
     * @param direction
     * @param searchText
     */
    public void requestSearchData(final int direction, String searchText) {
        if (PreferencesHelper.getInstance().getUserData().isSupperAccount) {
            return;
        }
        if (TextUtils.isEmpty(searchText)) {
            tempSearch = null;
        } else {
            tempSearch = searchText;
        }
        Long temp_startTime = null;
        Long temp_endTime = null;
        if (getView().isSelectedDateLayoutVisible()) {
            temp_startTime = startTime;
            temp_endTime = endTime;
        }
        switch (direction) {
            case DIRECTION_DOWN:
                cur_page = 1;
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getDeviceAlarmLogList(cur_page, null, null, null, tempSearch, temp_startTime,
                        temp_endTime,
                        null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>(this) {


                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().onPullRefreshComplete();
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }

                    @Override
                    public void onCompleted(DeviceAlarmLogRsp deviceAlarmLogRsp) {
                        getView().dismissProgressDialog();
                        freshUI(direction, deviceAlarmLogRsp);
                        getView().onPullRefreshComplete();

                    }
                });
                break;
            case DIRECTION_UP:
                cur_page++;
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getDeviceAlarmLogList(cur_page, null, null, null, tempSearch, temp_startTime,
                        temp_endTime,
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
                            getView().toastShort(mContext.getString(R.string.no_more_data));
                            getView().onPullRefreshCompleteNoMoreData();
                            cur_page--;
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


    /**
     * 通过回显日期搜索
     *
     * @param startDate
     * @param endDate
     */
    private void requestDataByDate(String startDate, String endDate) {
        getView().setSelectedDateLayoutVisible(true);
        startTime = DateUtil.strToDate(startDate).getTime();
        endTime = DateUtil.strToDate(endDate).getTime();
        getView().setSelectedDateSearchText(DateUtil.getMothDayFormatDate(startTime) + "-" + DateUtil
                .getMothDayFormatDate(endTime));
        endTime += 1000 * 60 * 60 * 24;
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getDeviceAlarmLogList(1, null, null, null, tempSearch, startTime, endTime,
                null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>(this) {


            @Override
            public void onCompleted(DeviceAlarmLogRsp deviceAlarmLogRsp) {
                getView().dismissProgressDialog();
                if (deviceAlarmLogRsp.getData().size() == 0) {
                    getView().toastShort(mContext.getString(R.string.no_more_data));
                }
                freshUI(DIRECTION_DOWN, deviceAlarmLogRsp);
                getView().onPullRefreshComplete();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().onPullRefreshComplete();
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }


    public void clickItem(int position, boolean isReConfirm) {
        try {
            this.isReConfirm = isReConfirm;
            Intent intent = new Intent(mContext, AlarmDetailLogActivity.class);
            intent.putExtra(EXTRA_ALARM_INFO, mDeviceAlarmLogInfoList.get(position));
            getView().startAC(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void clickItemByConfirmStatus(int position, boolean isReConfirm) {
        this.isReConfirm = isReConfirm;
        mCurrentDeviceAlarmLogInfo = mDeviceAlarmLogInfoList.get(position);
        getView().showAlarmPopupView();
    }

    private void freshDeviceAlarmLogInfo(DeviceAlarmLogInfo deviceAlarmLogInfo) {
        //TODO 处理只针对当前集合做处理
        if (mDeviceAlarmLogInfoList.contains(deviceAlarmLogInfo)) {
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
////                Collections.sort(mDeviceAlarmLogInfoList);
                    break;
                }
            }
            getView().updateAlarmListAdapter(mDeviceAlarmLogInfoList);
        }
//        else {
//            mDeviceAlarmLogInfoList.add(0, deviceAlarmLogInfo);
//        }
//        ArrayList<DeviceAlarmLogInfo> tempList = new ArrayList<>(mDeviceAlarmLogInfoList);
//        getView().updateAlarmListAdapter(mDeviceAlarmLogInfoList);
    }


    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPopupCallback(int statusResult, int statusType, int statusPlace, List<ScenesData> scenesDataList, String remark) {
        getView().showProgressDialog();
        getView().setUpdateButtonClickable(false);
        RetrofitServiceHelper.INSTANCE.doUpdatePhotosUrl(mCurrentDeviceAlarmLogInfo.get_id(), statusResult,
                statusType, statusPlace,
                remark, isReConfirm, scenesDataList).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe
                        (new CityObserver<DeviceAlarmItemRsp>(this) {


                            @Override
                            public void onErrorMsg(int errorCode, String errorMsg) {
                                getView().setUpdateButtonClickable(true);
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
                                } else {
                                    getView().toastShort(mContext.getResources().getString(R.string
                                            .tips_commit_failed));
                                }
                                getView().dismissProgressDialog();
                                getView().dismissAlarmPopupView();
                            }
                        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        //TODO 可以修改以此种方式传递，方便管理
        int code = eventData.code;
        Object data = eventData.data;
        //
        switch (code) {
            case EVENT_DATA_ALARM_DETAIL_RESULT:
                if (data instanceof DeviceAlarmLogInfo) {
                    freshDeviceAlarmLogInfo((DeviceAlarmLogInfo) data);
                }
                break;
            case EVENT_DATA_SEARCH_MERCHANT:
                if (PreferencesHelper.getInstance().getUserData().hasAlarmInfo) {
                    requestSearchData(DIRECTION_DOWN, null);
                }
                break;
            case EVENT_DATA_ALARM_FRESH_ALARM_DATA:
                //仅在无搜索状态和日历选择时进行刷新
                if (TextUtils.isEmpty(tempSearch) && !getView().getSearchTextVisible()) {
                    if (data instanceof List) {
                        if (data instanceof DeviceAlarmLogInfo) {
                            DeviceAlarmLogInfo deviceAlarmLogInfo = (DeviceAlarmLogInfo) data;
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
                                    getView().updateAlarmListAdapter(mDeviceAlarmLogInfoList);
                                    break;
                                }

                            }
                        }
                    }
                }
                break;
            case EVENT_DATA_ALARM_SOCKET_DISPLAY_STATUS:
                //仅在无搜索状态和日历选择时进行刷新
                if (TextUtils.isEmpty(tempSearch) && !getView().getSearchTextVisible()) {
                    if (data instanceof EventAlarmStatusModel) {
                        EventAlarmStatusModel tempEventAlarmStatusModel = (EventAlarmStatusModel) data;

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


    public void doCancelSearch() {
        tempSearch = null;
        requestSearchData(DIRECTION_DOWN, tempSearch);
    }

    public void doCalendar(LinearLayout fgMainWarnTitleRoot) {
        long temp_startTime = -1;
        long temp_endTime = -1;
        if (getView().isSelectedDateLayoutVisible()) {
            temp_startTime = startTime;
            temp_endTime = endTime;
        }

        mCalendarPopUtils.show(fgMainWarnTitleRoot, temp_startTime, temp_endTime);


    }

    @Override
    public void onCalendarPopupCallback(CalendarDateModel calendarDateModel) {
        requestDataByDate(calendarDateModel.startDate, calendarDateModel.endDate);
    }
}

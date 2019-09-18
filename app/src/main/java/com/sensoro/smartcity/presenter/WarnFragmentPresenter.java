package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.LinearLayout;

import com.sensoro.common.analyzer.PreferencesSaveAnalyzer;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.constant.SearchHistoryTypeConstants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.manger.ThreadPoolManager;
import com.sensoro.common.model.DeviceNotificationBean;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.AlarmPopupDataBean;
import com.sensoro.common.server.bean.DeviceAlarmLogInfo;
import com.sensoro.common.server.bean.ScenesData;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.common.widgets.dialog.WarningContactDialogUtil;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.AlarmDetailLogActivity;
import com.sensoro.smartcity.analyzer.AlarmPopupConfigAnalyzer;
import com.sensoro.smartcity.imainviews.IWarnFragmentView;
import com.sensoro.smartcity.model.AlarmPopupModel;
import com.sensoro.common.model.CalendarDateModel;
import com.sensoro.smartcity.model.EventAlarmStatusModel;
import com.sensoro.common.utils.WidgetUtil;
import com.sensoro.smartcity.widget.popup.AlarmPopUtils;
import com.sensoro.common.widgets.CalendarPopUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WarnFragmentPresenter extends BasePresenter<IWarnFragmentView> implements IOnCreate, AlarmPopUtils.OnPopupCallbackListener, CalendarPopUtils.OnCalendarPopupCallbackListener, Runnable {
    private final List<DeviceAlarmLogInfo> mDeviceAlarmLogInfoList = new ArrayList<>();
    private final List<String> mSearchHistoryList = new ArrayList<>();
    private volatile int cur_page = 1;
    private long startTime;
    private long endTime;
    private Activity mContext;
    private boolean isReConfirm = false;
    private DeviceAlarmLogInfo mCurrentDeviceAlarmLogInfo;
    private CalendarPopUtils mCalendarPopUtils;
    private String tempSearch;
    private volatile boolean needFresh = false;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        mCalendarPopUtils = new CalendarPopUtils(mContext);
        mCalendarPopUtils
                .setMonthStatus(1)
                .setOnCalendarPopupCallbackListener(this);
        if (PreferencesHelper.getInstance().getUserData().hasAlarmInfo) {
            requestSearchData(Constants.DIRECTION_DOWN, null);
            mHandler.post(this);
        }
        List<String> list = PreferencesHelper.getInstance().getSearchHistoryData(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_WARN);
        if (list != null) {
            mSearchHistoryList.addAll(list);
            getView().updateSearchHistoryList(mSearchHistoryList);
        }

    }

    public void doContactOwner(DeviceAlarmLogInfo deviceAlarmLogInfo) {
        List<DeviceNotificationBean> deviceNotifications = WidgetUtil.handleDeviceNotifications(deviceAlarmLogInfo.getDeviceNotifications());
        if (deviceNotifications.isEmpty()) {
            getView().toastShort(mContext.getString(R.string.no_find_contact_phone_number));
        } else {
            if (deviceNotifications.size() > 1) {
                WarningContactDialogUtil dialogUtil = new WarningContactDialogUtil(mContext);
                dialogUtil.show(deviceNotifications);
            } else {
                DeviceNotificationBean deviceNotificationBean = deviceNotifications.get(0);
                String content = deviceNotificationBean.getContent();
                AppUtils.diallPhone(content, mContext);
            }
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

    private void freshUI(final int direction, ResponseResult<List<DeviceAlarmLogInfo>> deviceAlarmLogRsp) {
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

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
        mDeviceAlarmLogInfoList.clear();
    }

    /**
     * 必须
     *
     * @param direction
     * @param searchText
     */
    public void requestSearchData(final int direction, String searchText) {
        if (!PreferencesHelper.getInstance().getUserData().hasAlarmInfo) {
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
            case Constants.DIRECTION_DOWN:
                cur_page = 1;
                getView().showProgressDialog();
                RetrofitServiceHelper.getInstance().getDeviceAlarmLogList(cur_page, null, null, null, tempSearch, temp_startTime,
                        temp_endTime,
                        null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<DeviceAlarmLogInfo>>>(this) {


                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        freshEmptyData();
                        getView().onPullRefreshComplete();
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }

                    @Override
                    public void onCompleted(ResponseResult<List<DeviceAlarmLogInfo>> deviceAlarmLogRsp) {
                        getView().dismissProgressDialog();
                        freshUI(direction, deviceAlarmLogRsp);
                        getView().onPullRefreshComplete();
                    }
                });
                break;
            case Constants.DIRECTION_UP:
                cur_page++;
                getView().showProgressDialog();
                RetrofitServiceHelper.getInstance().getDeviceAlarmLogList(cur_page, null, null, null, tempSearch, temp_startTime,
                        temp_endTime,
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
                            getView().toastShort(mContext.getString(R.string.no_more_data));
                            cur_page--;
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
        getView().setSelectedDateSearchText(DateUtil.getCalendarYearMothDayFormatDate(startTime) + " ~ " + DateUtil
                .getCalendarYearMothDayFormatDate(endTime));
        endTime += 1000 * 60 * 60 * 24;
        getView().showProgressDialog();
        cur_page = 1;
        RetrofitServiceHelper.getInstance().getDeviceAlarmLogList(cur_page, null, null, null, tempSearch, startTime, endTime,
                null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<DeviceAlarmLogInfo>>>(this) {


            @Override
            public void onCompleted(ResponseResult<List<DeviceAlarmLogInfo>> deviceAlarmLogRsp) {
                getView().dismissProgressDialog();
                List<DeviceAlarmLogInfo> data = deviceAlarmLogRsp.getData();
                if (data == null || data.size() == 0) {
                    getView().toastShort(mContext.getString(R.string.no_more_data));
                }
                freshUI(Constants.DIRECTION_DOWN, deviceAlarmLogRsp);
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


    public void clickItem(DeviceAlarmLogInfo deviceAlarmLogInfo, boolean isReConfirm) {
        this.isReConfirm = isReConfirm;
        Intent intent = new Intent(mContext, AlarmDetailLogActivity.class);
        intent.putExtra(Constants.EXTRA_ALARM_INFO, deviceAlarmLogInfo);
        getView().startAC(intent);
    }

    public void clickItemByConfirmStatus(final DeviceAlarmLogInfo deviceAlarmLogInfo, boolean isReConfirm) {
        this.isReConfirm = isReConfirm;
        mCurrentDeviceAlarmLogInfo = deviceAlarmLogInfo;
        if (PreferencesHelper.getInstance().getAlarmPopupDataBeanCache() == null) {
            RetrofitServiceHelper.getInstance().getDevicesAlarmPopupConfig().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<AlarmPopupDataBean>>(this) {
                @Override
                public void onCompleted(ResponseResult<AlarmPopupDataBean> devicesAlarmPopupConfigRsp) {
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
                    alarmPopupModel.sensorType = mCurrentDeviceAlarmLogInfo.getSensorType();
                    //
                    AlarmPopupConfigAnalyzer.handleAlarmPopupModel(null, alarmPopupModel);
                    getView().showAlarmPopupView(alarmPopupModel);
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
            alarmPopupModel.sensorType = mCurrentDeviceAlarmLogInfo.getSensorType();
            //
            AlarmPopupConfigAnalyzer.handleAlarmPopupModel(null, alarmPopupModel);
            getView().showAlarmPopupView(alarmPopupModel);
        }

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
                            synchronized (mDeviceAlarmLogInfoList) {
                                getView().updateAlarmListAdapter(mDeviceAlarmLogInfoList);
                            }
                        }

                    }
                });
            }

        }
    }


    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(EventAlarmStatusModel eventAlarmStatusModel) {
        //仅在无搜索状态和日历选择时进行刷新
        if (TextUtils.isEmpty(tempSearch) && !getView().getSearchTextVisible()) {
            switch (eventAlarmStatusModel.status) {
                // 做一些预警发生的逻辑
                case Constants.MODEL_ALARM_STATUS_EVENT_CODE_CREATE:
                    handleSocketData(eventAlarmStatusModel.deviceAlarmLogInfo, true);
                    break;
                // 做一些预警恢复的逻辑
                case Constants.MODEL_ALARM_STATUS_EVENT_CODE_RECOVERY:
                    // 做一些预警被确认的逻辑
                case Constants.MODEL_ALARM_STATUS_EVENT_CODE_CONFIRM:
                    // 做一些预警被再次确认的逻辑
                case Constants.MODEL_ALARM_STATUS_EVENT_CODE_RECONFIRM:
                    handleSocketData(eventAlarmStatusModel.deviceAlarmLogInfo, false);
                    break;
                default:
                    // 未知逻辑 可以联系我确认 有可能是bug
                    handleSocketData(eventAlarmStatusModel.deviceAlarmLogInfo, false);
                    break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            case Constants.EVENT_DATA_ALARM_DETAIL_RESULT:
                if (TextUtils.isEmpty(tempSearch) && !getView().getSearchTextVisible()) {
                    if (data instanceof DeviceAlarmLogInfo) {
                        freshDeviceAlarmLogInfo((DeviceAlarmLogInfo) data);
                    }
                }
                break;
            case Constants.EVENT_DATA_SEARCH_MERCHANT:
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isAttachedView()) {
                            getView().cancelSearchData();
                        }
                    }
                });
                break;
            case Constants.EVENT_DATA_ALARM_FRESH_ALARM_DATA:
                //仅在无搜索状态和日历选择时进行刷新
                if (TextUtils.isEmpty(tempSearch) && !getView().getSearchTextVisible()) {
                    if (data instanceof DeviceAlarmLogInfo) {
                        freshDeviceAlarmLogInfo((DeviceAlarmLogInfo) data);
                    }
                }
                break;
        }
    }


    public void doCancelSearch() {
        tempSearch = null;
        requestSearchData(Constants.DIRECTION_DOWN, null);
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
        getView().setSearchHistoryVisible(false);
        if (!TextUtils.isEmpty(tempSearch)) {
//            PreferencesHelper.getInstance().saveSearchHistoryText(tempSearch, SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_WARN);
//            //为了调整 搜索顺序，所以先删除，再添加
//            mSearchHistoryList.remove(tempSearch);
//            mSearchHistoryList.add(0, tempSearch);
//            getView().updateSearchHistoryList(mSearchHistoryList);
            save(tempSearch);

        }

    }

    @Override
    public void run() {
        scheduleRefresh();
        mHandler.postDelayed(this, 3000);
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

    public void save(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
//        mSearchHistoryList.remove(text);
//        PreferencesHelper.getInstance().saveSearchHistoryText(text, SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_WARN);
        List<String> warnList = PreferencesSaveAnalyzer.handleDeployRecord(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_WARN, text);
//        mSearchHistoryList.add(0, text);
        mSearchHistoryList.clear();
        mSearchHistoryList.addAll(warnList);
        getView().updateSearchHistoryList(mSearchHistoryList);

    }

    public void clearSearchHistory() {
        PreferencesSaveAnalyzer.clearAllData(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_WARN);
        mSearchHistoryList.clear();
        getView().updateSearchHistoryList(mSearchHistoryList);
    }

    @Override
    public void onPopupCallback(AlarmPopupModel alarmPopupModel, List<ScenesData> scenesDataList) {
        getView().showProgressDialog();
        getView().setUpdateButtonClickable(false);
        Map<String, Integer> alarmPopupServerData = AlarmPopupConfigAnalyzer.createAlarmPopupServerData(alarmPopupModel);
        RetrofitServiceHelper.getInstance().doUpdatePhotosUrl(mCurrentDeviceAlarmLogInfo.get_id(), alarmPopupServerData, alarmPopupModel.securityRisksList,
                alarmPopupModel.mRemark, isReConfirm, scenesDataList).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<ResponseResult<DeviceAlarmLogInfo>>(this) {

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().setUpdateButtonClickable(true);
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }

                    @Override
                    public void onCompleted(ResponseResult<DeviceAlarmLogInfo> deviceAlarmItemRsp) {
                        if (deviceAlarmItemRsp.getErrcode() == ResponseResult.CODE_SUCCESS) {
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

    public void doCloseWarn(DeviceAlarmLogInfo deviceAlarmLogInfo) {
        //TODO 调用关闭火警 然后刷新界面
        getView().showProgressDialog();

        RetrofitServiceHelper.getInstance().doCloseFireWarn(deviceAlarmLogInfo.getDeviceSN()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<Object>>() {
            @Override
            public void onCompleted(ResponseResult<Object> objectResponseResult) {

                getView().showProgressDialog();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().showProgressDialog();
                getView().toastShort(errorMsg);
            }
        });

    }
}

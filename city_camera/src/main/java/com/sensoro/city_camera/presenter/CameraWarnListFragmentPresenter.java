package com.sensoro.city_camera.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RelativeLayout;

import com.sensoro.city_camera.IMainViews.ICameraWarnListFragmentView;
import com.sensoro.city_camera.R;
import com.sensoro.city_camera.activity.SecurityWarnDetailActivity;
import com.sensoro.city_camera.dialog.SecurityWarnConfirmDialog;
import com.sensoro.city_camera.model.FilterModel;
import com.sensoro.common.analyzer.PreferencesSaveAnalyzer;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.constant.SearchHistoryTypeConstants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.manger.ThreadPoolManager;
import com.sensoro.common.model.CalendarDateModel;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.EventCameraWarnStatusModel;
import com.sensoro.common.server.security.bean.SecurityAlarmInfo;
import com.sensoro.common.server.security.response.HandleAlarmRsp;
import com.sensoro.common.server.security.response.SecurityAlarmListRsp;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.common.widgets.CalendarPopUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sensoro.common.constant.Constants.DIRECTION_DOWN;

/**
 * 安防预警列表
 *
 * @author wangqinghao
 */
public class CameraWarnListFragmentPresenter extends BasePresenter<ICameraWarnListFragmentView> implements IOnCreate, Runnable,
        CalendarPopUtils.OnCalendarPopupCallbackListener, SecurityWarnConfirmDialog.SecurityConfirmCallback {

    public static final long FILTER_STATUS_ALL = 0;
    public static final long FILTER_STATUS_UNPROCESS = 2;
    public static final long FILTER_STATUS_EFFECTIVE = 3;
    public static final long FILTER_STATUS_INVALID = 4;

    public static final long FILTER_TIME_ALL = 0;
    public static final long FILTER_TIME_24H = 24 * 3600;
    public static final long FILTER_TIME_3DAY = 3 * 24 * 3600;
    public static final long FILTER_TIME_7DAY = 7 * 24 * 3600;
    public static final long FILTER_TIME_CUSTOM = -1;

    //筛选条件
    private FilterModel mCurrentCaptureTimeModel;
    private FilterModel mCurrentProcessStatusModel;
    private List<FilterModel> mCaptureTimeModelList = new ArrayList<>();
    private List<FilterModel> mProcessStatusModelList = new ArrayList<>();


    //搜索文字
    private String tempSearchText;
    //搜索抓拍时间参数
    private long startTime;
    private long endTime;
    private String dateSearchText;
    //自定义日历开始结束时间参数
    private long customStartTime;
    private long customEndTime;
    //处理状态
    private long handleStatus = FILTER_STATUS_UNPROCESS;

    private final List<SecurityAlarmInfo> mSecurityAlarmInfoList = new ArrayList<>();
    private final List<String> mSearchHistoryList = new ArrayList<>();
    private volatile int cur_page = 0;

    private Activity mContext;
    private CalendarPopUtils mCalendarPopUtils;

    private volatile boolean needFresh = false;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    public static final int REQUEST_CODE_DETAIL = 100;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        initCaptureTimeDialog();
        initProcessStatusDialog();

        mCalendarPopUtils = new CalendarPopUtils(mContext);
        mCalendarPopUtils
                .setMonthStatus(1)
                .setOnCalendarPopupCallbackListener(this);

        initFilterStatus();
        requestSearchData(Constants.DIRECTION_DOWN);
        mHandler.post(this);

        //安防历史搜索记录
        List<String> list = PreferencesHelper.getInstance().getSearchHistoryData(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_WARN);
        if (list != null) {
            mSearchHistoryList.addAll(list);
            getView().updateSearchHistoryList(mSearchHistoryList);
        }

    }


    public void initCaptureTimeDialog() {
        mCaptureTimeModelList = new ArrayList<>();
        mCurrentCaptureTimeModel = new FilterModel(mContext.getString(R.string.Unlimited), FILTER_TIME_ALL, true, true);
        mCaptureTimeModelList.add(mCurrentCaptureTimeModel);
        mCaptureTimeModelList.add(new FilterModel(mContext.getString(R.string.twentyfour_hours), FILTER_TIME_24H, false, false));
        mCaptureTimeModelList.add(new FilterModel(mContext.getString(R.string.three_days), FILTER_TIME_3DAY, false, false));
        mCaptureTimeModelList.add(new FilterModel(mContext.getString(R.string.seven_days), FILTER_TIME_7DAY, false, false));
        mCaptureTimeModelList.add(new FilterModel(mContext.getString(R.string.customize_time), FILTER_TIME_CUSTOM, false, false));
        getView().updateFilterCaptureTimeList(mCaptureTimeModelList);
    }


    public void initProcessStatusDialog() {

        mProcessStatusModelList = new ArrayList<>();
        mProcessStatusModelList.add(new FilterModel(mContext.getString(R.string.word_all), FILTER_STATUS_ALL, false, true));
        mCurrentProcessStatusModel = new FilterModel(mContext.getString(R.string.unprocessed), FILTER_STATUS_UNPROCESS, true, false);
        mProcessStatusModelList.add(mCurrentProcessStatusModel);
        mProcessStatusModelList.add(new FilterModel(mContext.getString(R.string.effective_warn), FILTER_STATUS_EFFECTIVE, false, false));
        mProcessStatusModelList.add(new FilterModel(mContext.getString(R.string.invalid_warn), FILTER_STATUS_INVALID, false, false));
        getView().updateFilterProcessStatusList(mProcessStatusModelList);
    }

    private void freshUI(final int direction, SecurityAlarmListRsp securityAlarmListRsp) {
        final List<SecurityAlarmInfo> securityAlarmInfoList = securityAlarmListRsp.getData().list;
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if (direction == Constants.DIRECTION_DOWN) {
                    mSecurityAlarmInfoList.clear();
                }
                synchronized (mSecurityAlarmInfoList) {
                    mSecurityAlarmInfoList.addAll(securityAlarmInfoList);
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isAttachedView()) {
                                getView().updateCameraWarnsListAdapter(mSecurityAlarmInfoList);
                            }
                            if (direction == Constants.DIRECTION_DOWN) {
                                getView().SmoothToTopList();
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
        mSecurityAlarmInfoList.clear();

    }


    /**
     * 搜索数据
     *
     * @param direction DIRECTION_DOWN：下拉重新请求  DIRECTION_UP：上滑继续请求
     */
    public void requestSearchData(final int direction) {
        switch (direction) {
            case Constants.DIRECTION_DOWN:
                cur_page = 0;
                getView().showProgressDialog();
                //查询起始位置，默认0
                RetrofitServiceHelper.getInstance().getSecurityAlarmList(
                        (cur_page == 0 ? 0 : cur_page * 20 - 1),
                        (startTime == 0 ? null : startTime + ""),
                        (endTime == 0 ? null : endTime + ""), (int) handleStatus, tempSearchText, 0
                ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<SecurityAlarmListRsp>(this) {


                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().onPullRefreshComplete();
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }

                    @Override
                    public void onCompleted(SecurityAlarmListRsp securityAlarmListRsp) {
                        getView().dismissProgressDialog();
                        freshUI(direction, securityAlarmListRsp);
                        getView().onPullRefreshComplete();
                    }
                });

                break;
            case Constants.DIRECTION_UP:
                cur_page++;
                getView().showProgressDialog();
                int offsetUp = cur_page == 0 ? 1 : cur_page * 20 - 1;
                RetrofitServiceHelper.getInstance().getSecurityAlarmList(
                        (cur_page == 0 ? 0 : cur_page * 20 - 1),
                        (startTime == 0 ? null : startTime + ""),
                        (endTime == 0 ? null : endTime + ""), (int) handleStatus, tempSearchText, 0
                ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<SecurityAlarmListRsp>(this) {


                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        cur_page--;
                        getView().onPullRefreshComplete();
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }

                    @Override
                    public void onCompleted(SecurityAlarmListRsp securityAlarmListRsp) {
                        getView().dismissProgressDialog();
                        if (securityAlarmListRsp.getData().list.size() == 0) {
                            getView().toastShort(mContext.getString(R.string.no_more_data));
                            getView().onPullRefreshCompleteNoMoreData();
                            cur_page--;
                        } else {
                            freshUI(direction, securityAlarmListRsp);
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
     * List Item点击事件处理
     *
     * @param securityAlarmInfo
     */
    public void clickItem(SecurityAlarmInfo securityAlarmInfo) {
        Intent intent = new Intent(mContext, SecurityWarnDetailActivity.class);
        intent.putExtra("id", securityAlarmInfo.getId());
        intent.putExtra("SecurityAlarmInfo", securityAlarmInfo);
        if (isAttachedView()) {
            getView().startACForResult(intent, REQUEST_CODE_DETAIL);
        }

    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(EventCameraWarnStatusModel eventAlarmStatusModel) {
        if (TextUtils.isEmpty(tempSearchText) && !getView().getSearchTextCancelVisible()) {
            switch (eventAlarmStatusModel.status) {

            }
        }


    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {

        }

    }

    public void doCancelSearch() {
        tempSearchText = null;
        requestSearchData(Constants.DIRECTION_DOWN);
    }

    /**
     * 首次刷新列表
     */
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
                        getView().updateCameraWarnsListAdapter(mSecurityAlarmInfoList);
                    }
                    needFresh = false;
                }
            });
        }
    }

    /**
     * 保存历史搜索记录
     *
     * @param text
     */
    public void save(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        List<String> warnList = PreferencesSaveAnalyzer.handleDeployRecord(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_WARN, text);
        mSearchHistoryList.clear();
        mSearchHistoryList.addAll(warnList);
        getView().updateSearchHistoryList(mSearchHistoryList);

    }

    /**
     * 清除历史搜索记录
     */
    public void clearSearchHistory() {
        PreferencesSaveAnalyzer.clearAllData(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_WARN);
        mSearchHistoryList.clear();
        getView().updateSearchHistoryList(mSearchHistoryList);
    }

    /**
     * Item Button 点击事件处理
     */
    public void clickItemByConfirmStatus(final SecurityAlarmInfo securityAlarmInfo) {
        getView().showConfirmDialog(securityAlarmInfo);
    }

    /**
     * 日历选择时间回调
     *
     * @param calendarDateModel
     */
    @Override
    public void onCalendarPopupCallback(CalendarDateModel calendarDateModel) {
        mCurrentCaptureTimeModel = mCaptureTimeModelList.get(mCaptureTimeModelList.size() - 1);
        customStartTime = DateUtil.strToDate(calendarDateModel.startDate).getTime();
        customEndTime = DateUtil.strToDate(calendarDateModel.endDate).getTime();
        /*dateSearchText = DateUtil.getCalendarYearMothDayFormatDate(startTime) + " ~ " + DateUtil
                .getCalendarYearMothDayFormatDate(endTime);*/
        dateSearchText = DateUtil.getMonthDate(customStartTime) + " ~ " + DateUtil
                .getMonthDate(customEndTime);
        int elmentPos = mCaptureTimeModelList.indexOf(mCurrentCaptureTimeModel);
        //更新自定义时间文字
        mCurrentCaptureTimeModel.statusTitle = dateSearchText;
        //更新抓拍时间列表数据
        mCaptureTimeModelList.get(elmentPos).statusTitle = dateSearchText;
        getView().updateFilterCaptureTimeList(mCaptureTimeModelList);

        getView().setFilterCaptureTimeView(mCurrentCaptureTimeModel);
        filterDataByTime(mCurrentCaptureTimeModel.status);

        getView().setSearchHistoryVisible(false);
        if (!TextUtils.isEmpty(tempSearchText)) {
            save(tempSearchText);
        }
    }


    /**
     * 选择筛选过滤时间
     */
    public void filterDataByTime(long filterTimeInval) {
        //不限
        if (filterTimeInval == FILTER_TIME_ALL) {
            startTime = 0;
            endTime = 0;
        } else if (filterTimeInval == FILTER_TIME_CUSTOM) {
            startTime = customStartTime;
            endTime = customEndTime + 1000 * 60 * 60 * 24;
        } else {
            endTime = System.currentTimeMillis();
            startTime = endTime - filterTimeInval * 1000;
        }

        requestSearchData(DIRECTION_DOWN);
    }

    /**
     * 设置 搜索关键字
     *
     * @param searchText
     */
    public void setFilterText(String searchText) {
        if (TextUtils.isEmpty(searchText)) {
            tempSearchText = null;
        } else {
            tempSearchText = searchText;
        }
        save(searchText);
        requestSearchData(Constants.DIRECTION_DOWN);

    }

    /**
     * 设置抓拍时间 不限/24h/3day/7day
     */
    public void setFilterCapturetime(int positon) {
        if (positon > -1 && positon < mCaptureTimeModelList.size()) {
            if (customStartTime != 0 || customEndTime != 0) {
                //init  更新抓拍时间选项列表
                customStartTime = 0;
                customEndTime = 0;
                dateSearchText = "";
                mCaptureTimeModelList.get(mCaptureTimeModelList.size() - 1).statusTitle = mContext.getString(R.string.customize_time);
                getView().updateFilterCaptureTimeList(mCaptureTimeModelList);
            }

            mCurrentCaptureTimeModel = mCaptureTimeModelList.get(positon);
            filterDataByTime(mCurrentCaptureTimeModel.status);
        }
        getView().setFilterCaptureTimeView(mCurrentCaptureTimeModel);


    }

    /**
     * 自定义时间：弹出日历选择日期范围
     *
     * @param fgMainWarnTitleRoot
     */
    public void doCalendar(RelativeLayout fgMainWarnTitleRoot) {

        long temp_startTime = -1;
        long temp_endTime = -1;
        if (customStartTime != 0 && customEndTime != 0) {
            temp_startTime = customStartTime;
            temp_endTime = customEndTime;
        }
        mCalendarPopUtils.show(fgMainWarnTitleRoot, temp_startTime, temp_endTime);
    }

    /**
     * 设置处理状态View
     */
    public void setFilterProcessStatus(int positon) {
        if (positon > -1 && positon < mProcessStatusModelList.size()) {
            mCurrentProcessStatusModel = mProcessStatusModelList.get(positon);
            filterDataByStatus(mCurrentProcessStatusModel.status);
        }
        getView().setFilterProcessStatusView(mCurrentProcessStatusModel);
    }


    /**
     * 通过预警处理状态筛选
     *
     * @param filterStatusType
     */
    public void filterDataByStatus(long filterStatusType) {
        handleStatus = filterStatusType;
        requestSearchData(DIRECTION_DOWN);
    }

    /**
     * 初始化筛选条件
     */
    public void initFilterStatus() {
        startTime = 0;
        endTime = 0;
        customStartTime = 0;
        customEndTime = 0;
        handleStatus = FILTER_STATUS_UNPROCESS;
        tempSearchText = "";
        getView().setFilterCaptureTimeView(mCurrentCaptureTimeModel);
        getView().setFilterProcessStatusView(mCurrentProcessStatusModel);
    }

    @Override
    public void onConfirmClick(String id, int isEffective, String operationDetail) {
        RetrofitServiceHelper.getInstance().handleSecurityAlarm(id, isEffective, operationDetail)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<HandleAlarmRsp>(this) {
                    @Override
                    public void onCompleted(HandleAlarmRsp handleAlarmRsp) {
                        requestSearchData(Constants.DIRECTION_DOWN);
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {

                    }
                });
    }
}

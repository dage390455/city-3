package com.sensoro.city_camera.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.LinearLayout;

import com.sensoro.city_camera.IMainViews.ICameraWarnListFragmentView;
import com.sensoro.city_camera.R;
import com.sensoro.city_camera.activity.SecurityWarnDetailActivity;
import com.sensoro.city_camera.dialog.SecurityWarnConfirmDialog;
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

    public static final int FILTER_STATUS_ALL = 0;
    public static final int FILTER_STATUS_UNPROCESS = 2;
    public static final int FILTER_STATUS_EFFECTIVE = 3;
    public static final int FILTER_STATUS_INVALID = 4;

    public static final long FILTER_TIME_ALL = 0;
    public static final long FILTER_TIME_24H = 24 * 3600;
    public static final long FILTER_TIME_3DAY = 3 * 24 * 3600;
    public static final long FILTER_TIME_7DAY = 7 * 24 * 3600;

    //搜索文字
    private String tempSearchText;
    //抓拍时间
    private long startTime;
    private long endTime;
    private String dateSearchText;
    //处理状态
    private int handleStatus = FILTER_STATUS_UNPROCESS;

    private final List<SecurityAlarmInfo> mSecurityAlarmInfoList = new ArrayList<>();
    private final List<String> mSearchHistoryList = new ArrayList<>();
    private volatile int cur_page = 0;

    private Activity mContext;
    private SecurityAlarmInfo mCurrentSecurityAlarmInfo;
    private CalendarPopUtils mCalendarPopUtils;

    private volatile boolean needFresh = false;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    public static final int REQUEST_CODE_DETAIL = 100;

    private final Comparator<SecurityAlarmInfo> cameraWarnInfoComparator = new Comparator<SecurityAlarmInfo>() {
        @Override
        public int compare(SecurityAlarmInfo o1, SecurityAlarmInfo o2) {
            long l = o2.getAlarmTime() - o1.getAlarmTime();
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
        mCalendarPopUtils = new CalendarPopUtils(mContext);
        mCalendarPopUtils
                .setMonthStatus(1)
                .setOnCalendarPopupCallbackListener(this);

        if (true) {
            innitFilterStatus();
            requestSearchData(Constants.DIRECTION_DOWN);
            mHandler.post(this);
        }
        //安防历史搜索记录
        List<String> list = PreferencesHelper.getInstance().getSearchHistoryData(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_WARN);
        if (list != null) {
            mSearchHistoryList.addAll(list);
            getView().updateSearchHistoryList(mSearchHistoryList);
        }

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
                    out:
                    for (int i = 0; i < securityAlarmInfoList.size(); i++) {
                        SecurityAlarmInfo securityAlarmInfo = securityAlarmInfoList.get(i);
                        for (int j = 0; j < mSecurityAlarmInfoList.size(); j++) {
                            if (mSecurityAlarmInfoList.get(j).getId().equals(securityAlarmInfo.getId())) {
                                mSecurityAlarmInfoList.set(i, securityAlarmInfo);
                                break out;
                            }
                        }
                        mSecurityAlarmInfoList.add(securityAlarmInfo);
                    }
                    Collections.sort(mSecurityAlarmInfoList, cameraWarnInfoComparator);
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isAttachedView()) {
                                getView().updateCameraWarnsListAdapter(mSecurityAlarmInfoList);
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
                        (endTime == 0 ? null : endTime + ""), handleStatus, tempSearchText, 0
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
                        (cur_page == 0 ? 1 : cur_page * 20 - 1),
                        (startTime == 0 ? null : startTime + ""),
                        (endTime == 0 ? null : endTime + ""), handleStatus, tempSearchText, 0
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
        getView().startACForResult(intent, REQUEST_CODE_DETAIL);

    }

    /**
     * 单个安防预警确认
     *
     * @param securityAlarmInfo
     */
    public void cameraWarnConfirm(final SecurityAlarmInfo securityAlarmInfo) {
        getView().toastLong(securityAlarmInfo.getTaskName());
    }

    /**
     * 刷新单条数据
     */
    public void refreshSigleCameraWarnInfo() {

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

    private void freshSingleWarnLogInfo(SecurityAlarmInfo securityAlarmInfo) {
        synchronized (securityAlarmInfo) {
            // 处理只针对当前集合做处理
            boolean canRefresh = false;
            for (int i = 0; i < mSecurityAlarmInfoList.size(); i++) {
                SecurityAlarmInfo tempLogInfo = mSecurityAlarmInfoList.get(i);
                if (tempLogInfo.getId().equals(securityAlarmInfo.getId())) {
                    //刷新单个信息
                    /*AlarmInfo.RecordInfo[] recordInfoArray = deviceAlarmLogInfo.getRecords();
                    deviceAlarmLogInfo.setSort(1);
                    for (AlarmInfo.RecordInfo recordInfo : recordInfoArray) {
                        if (recordInfo.getType().equals("recovery")) {
                            deviceAlarmLogInfo.setSort(4);
                            break;
                        }
                    }
                    mDeviceAlarmLogInfoList.set(i, deviceAlarmLogInfo);*/
                    canRefresh = true;
                    break;
                }
            }
            if (canRefresh) {
                Collections.sort(mSecurityAlarmInfoList, cameraWarnInfoComparator);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isAttachedView()) {
                            getView().updateCameraWarnsListAdapter(mSecurityAlarmInfoList);
                        }

                    }
                });
            }

        }
    }

    /**
     * 日历选择时间回调
     *
     * @param calendarDateModel
     */
    @Override
    public void onCalendarPopupCallback(CalendarDateModel calendarDateModel) {
        startTime = DateUtil.strToDate(calendarDateModel.startDate).getTime();
        endTime = DateUtil.strToDate(calendarDateModel.endDate).getTime();
        /*dateSearchText = DateUtil.getCalendarYearMothDayFormatDate(startTime) + " ~ " + DateUtil
                .getCalendarYearMothDayFormatDate(endTime);*/
        dateSearchText = DateUtil.getMonthDate(startTime) + " ~ " + DateUtil
                .getMonthDate(endTime);

        getView().setCustomizeCaptureTime(dateSearchText);
        endTime += 1000 * 60 * 60 * 24;
        requestSearchData(DIRECTION_DOWN);

        getView().setSearchHistoryVisible(false);
        if (!TextUtils.isEmpty(tempSearchText)) {
            save(tempSearchText);
        }


    }


    /**
     * 选择筛选过滤时间
     */
    public void filterDataByTime(long filterTimeInval) {
        if (filterTimeInval == 0) {
            startTime = 0;
            endTime = 0;
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

    }


    /**
     * 通过预警处理状态筛选
     *
     * @param filterStatusType
     */
    public void filterDataByStatus(int filterStatusType) {
        handleStatus = filterStatusType;
        requestSearchData(DIRECTION_DOWN);
    }

    /**
     * 自定义时间 弹出日历选择日期范围
     *
     * @param fgMainWarnTitleRoot
     */
    public void doCalendar(LinearLayout fgMainWarnTitleRoot) {
        long temp_startTime = -1;
        long temp_endTime = -1;
        if (startTime != 0 && endTime != 0) {
            temp_startTime = startTime;
            temp_endTime = endTime;
        }
        mCalendarPopUtils.show(fgMainWarnTitleRoot, temp_startTime, temp_endTime);
    }

    /**
     * 初始化筛选条件
     */
    public void innitFilterStatus() {
        startTime = 0;
        endTime = 0;
        handleStatus = FILTER_STATUS_UNPROCESS;
        tempSearchText = "";
        getView().initFilterView();
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

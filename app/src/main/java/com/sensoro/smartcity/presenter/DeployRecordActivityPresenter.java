package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.LinearLayout;

import com.sensoro.smartcity.activity.DeployRecordDetailActivity;
import com.sensoro.smartcity.activity.ScanActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.constant.SearchHistoryTypeConstants;
import com.sensoro.smartcity.imainviews.IDeployRecordActivityView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.CalendarDateModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeployRecordInfo;
import com.sensoro.smartcity.server.response.DeployRecordRsp;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.widget.popup.CalendarPopUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DeployRecordActivityPresenter extends BasePresenter<IDeployRecordActivityView>
        implements Constants, IOnCreate, CalendarPopUtils.OnCalendarPopupCallbackListener {
    private String tempSearch;
    private Long startTime;
    private Long endTime;
    private Activity mActivity;
    private CalendarPopUtils mCalendarPopUtils;
    private volatile int cur_page = 1;
    private final List<String> mSearchHistoryList = new ArrayList<>();

    @Override
    public void initData(Context context) {
        onCreate();
        mActivity = (Activity) context;
        mCalendarPopUtils = new CalendarPopUtils(mActivity);
        mCalendarPopUtils.setOnCalendarPopupCallbackListener(this);

        List<String> list = PreferencesHelper.getInstance().getSearchHistoryData(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_RECORD);
        if (list != null) {
            mSearchHistoryList.addAll(list);
            getView().updateSearchHistoryList(mSearchHistoryList);
        }

        requestSearchData(DIRECTION_DOWN, null);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    public void requestSearchData(int direction, String searchText) {
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
        String owners = null;
        String signalQuality = null;
        if (getView().isSelectedDateLayoutVisible()) {
            temp_startTime = startTime;
            temp_endTime = endTime + 1000 * 60 * 60 * 24;
        }

        switch (direction) {
            case DIRECTION_DOWN:
                cur_page = 1;
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getDeployRecordList(searchText, temp_startTime, temp_endTime, owners, signalQuality).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeployRecordRsp>() {
                    @Override
                    public void onCompleted(DeployRecordRsp recordRsp) {
                        List<DeployRecordInfo> data = recordRsp.getData();
                        if (!TextUtils.isEmpty(tempSearch)) {
//            getView().setSelectedDateSearchText(searchText);
                            getView().setSearchButtonTextVisible(true);
                        } else {
                            getView().setSearchButtonTextVisible(false);
                        }
                        getView().onPullRefreshComplete();
                        getView().updateRcContentData(data);
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
                break;
        }
    }

    public void doRecorDetail(DeployRecordInfo deployRecordInfo) {
        Intent intent = new Intent(mActivity, DeployRecordDetailActivity.class);
        intent.putExtra(EXTRA_DEPLOY_RECORD_DETAIL, deployRecordInfo);
        getView().startAC(intent);
    }

    public void doDeployNewDevice() {
        Intent intent = new Intent(mActivity, ScanActivity.class);
        intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, Constants.TYPE_SCAN_DEPLOY_DEVICE);
        getView().startAC(intent);
    }

    public void doCalendar(LinearLayout acDeployRecordTitleRoot) {
        long temp_startTime = -1;
        long temp_endTime = -1;
        if (getView().isSelectedDateLayoutVisible()) {
            temp_startTime = startTime;
            temp_endTime = endTime;
        }

        mCalendarPopUtils.show(acDeployRecordTitleRoot, temp_startTime, temp_endTime);
    }

    @Override
    public void onCalendarPopupCallback(CalendarDateModel calendarDateModel) {
        getView().setSelectedDateLayoutVisible(true);
        startTime = DateUtil.strToDate(calendarDateModel.startDate).getTime();
        endTime = DateUtil.strToDate(calendarDateModel.endDate).getTime();
        getView().setSelectedDateSearchText(DateUtil.getMothDayFormatDate(startTime) + "-" + DateUtil
                .getMothDayFormatDate(endTime));
        getView().setSearchHistoryVisible(false);
        if (!TextUtils.isEmpty(tempSearch)) {
            PreferencesHelper.getInstance().saveSearchHistoryText(tempSearch, SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_WARN);
            //为了调整 搜索顺序，所以先删除，再添加
            mSearchHistoryList.remove(tempSearch);
            mSearchHistoryList.add(0, tempSearch);
            getView().updateSearchHistoryList(mSearchHistoryList);

        }
        requestSearchData(DIRECTION_DOWN, getView().getSearchText());

    }

    public void doCancelSearch() {
        tempSearch = null;
        requestSearchData(DIRECTION_DOWN, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        switch (code) {
            case EVENT_DATA_DEPLOY_RESULT_FINISH:
                getView().finishAc();
                break;
            case EVENT_DATA_DEPLOY_RESULT_CONTINUE:
                requestSearchData(DIRECTION_DOWN, null);
                break;
        }
//        LogUtils.loge(this, eventData.toString());
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    public void save(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        mSearchHistoryList.remove(text);
        PreferencesHelper.getInstance().saveSearchHistoryText(text, SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_RECORD);
        mSearchHistoryList.add(0, text);
        getView().updateSearchHistoryList(mSearchHistoryList);
    }

    public void clearSearchHistory() {
        PreferencesHelper.getInstance().clearSearchHistory(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_RECORD);
        mSearchHistoryList.clear();
        getView().updateSearchHistoryList(mSearchHistoryList);
    }
}

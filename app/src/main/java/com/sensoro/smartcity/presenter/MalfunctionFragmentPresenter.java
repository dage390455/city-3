package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.LinearLayout;

import com.sensoro.common.analyzer.PreferencesSaveAnalyzer;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.constant.SearchHistoryTypeConstants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.MalfunctionListInfo;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.smartcity.activity.MalfunctionDetailActivity;
import com.sensoro.smartcity.imainviews.IMalfunctionFragmentView;
import com.sensoro.smartcity.model.CalendarDateModel;
import com.sensoro.smartcity.widget.popup.CalendarPopUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MalfunctionFragmentPresenter extends BasePresenter<IMalfunctionFragmentView> implements IOnCreate,
        CalendarPopUtils.OnCalendarPopupCallbackListener {
    private String tempSearch;
    private long startTime;
    private long endTime;
    private Activity mContext;
    private CalendarPopUtils mCalendarPopUtils;
    private int cur_page;
    private final List<MalfunctionListInfo> mMalfunctionInfoList = new ArrayList<>();
    private final List<String> mSearchHistoryList = new ArrayList<>();

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        mCalendarPopUtils = new CalendarPopUtils(mContext);
        mCalendarPopUtils.setMonthStatus(1)
                .setOnCalendarPopupCallbackListener(this);
        List<String> list = PreferencesHelper.getInstance().getSearchHistoryData(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MALFUNCTION);
        if (list != null) {
            mSearchHistoryList.addAll(list);
            getView().UpdateSearchHistoryList(mSearchHistoryList);
        }
    }


    public void requestSearchData(final int direction, String searchText) {
        if (!PreferencesHelper.getInstance().getUserData().hasMalfunction) {
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
                RetrofitServiceHelper.getInstance().getDeviceMalfunctionLogList(cur_page, null, null, tempSearch, temp_startTime,
                        temp_endTime).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<MalfunctionListInfo>>>(this) {


                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().onPullRefreshComplete();
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }

                    @Override
                    public void onCompleted(ResponseResult<List<MalfunctionListInfo>> malfunctionListRsp) {
                        getView().dismissProgressDialog();
                        refreshUI(direction, malfunctionListRsp);
                        getView().onPullRefreshComplete();

                    }
                });
                break;
            case Constants.DIRECTION_UP:
                cur_page++;
                getView().showProgressDialog();
                RetrofitServiceHelper.getInstance().getDeviceMalfunctionLogList(cur_page, null, null, tempSearch, temp_startTime,
                        temp_endTime).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<MalfunctionListInfo>>>(this) {


                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        cur_page--;
                        getView().onPullRefreshComplete();
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }

                    @Override
                    public void onCompleted(ResponseResult<List<MalfunctionListInfo>> malfunctionListRsp) {
                        getView().dismissProgressDialog();
                        if (malfunctionListRsp.getData().size() == 0) {
                            getView().toastShort("没有更多数据了");
                            getView().onPullRefreshCompleteNoMoreData();
                            cur_page--;
                        } else {
                            refreshUI(direction, malfunctionListRsp);
                            getView().onPullRefreshComplete();
                        }

                    }
                });
                break;
            default:
                break;
        }
    }

    public void doCancelSearch() {
        tempSearch = null;
        requestSearchData(Constants.DIRECTION_DOWN, null);
    }

    public void doCalendar(LinearLayout fgMainTopSearchTitleRoot) {
        long temp_startTime = -1;
        long temp_endTime = -1;
        if (getView().isSelectedDateLayoutVisible()) {
            temp_startTime = startTime;
            temp_endTime = endTime;
        }

        mCalendarPopUtils.show(fgMainTopSearchTitleRoot, temp_startTime, temp_endTime);
    }

    @Override
    public void onCalendarPopupCallback(CalendarDateModel calendarDateModel) {
        requestDataByDate(calendarDateModel.startDate, calendarDateModel.endDate);
        getView().setSearchHistoryVisible(false);
        if (!TextUtils.isEmpty(tempSearch)) {
//            PreferencesHelper.getInstance().saveSearchHistoryText(tempSearch, SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MALFUNCTION);
//            mSearchHistoryList.remove(tempSearch);
//            mSearchHistoryList.add(0, tempSearch);
//            getView().UpdateSearchHistoryList(mSearchHistoryList);
            save(tempSearch);

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
        RetrofitServiceHelper.getInstance().getDeviceMalfunctionLogList(1, null, null, tempSearch, startTime, endTime
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<MalfunctionListInfo>>>(this) {


            @Override
            public void onCompleted(ResponseResult<List<MalfunctionListInfo>> malfunctionListRsp) {
                getView().dismissProgressDialog();
                if (malfunctionListRsp.getData().size() == 0) {
                    getView().toastShort("没有更多数据了");
                }
                refreshUI(Constants.DIRECTION_DOWN, malfunctionListRsp);
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

    private void refreshUI(int directionDown, ResponseResult<List<MalfunctionListInfo>> malfunctionListRsp) {
        if (directionDown == Constants.DIRECTION_DOWN) {
            mMalfunctionInfoList.clear();
        }
        handleMalfunctionLists(malfunctionListRsp);
//        if (!TextUtils.isEmpty(tempSearch)) {
////            getView().setSelectedDateSearchText(searchText);
//            getView().setSearchButtonTextVisible(true);
//        } else {
//            getView().setSearchButtonTextVisible(false);
//        }
        getView().updateAlarmListAdapter(mMalfunctionInfoList);
    }

    private void handleMalfunctionLists(ResponseResult<List<MalfunctionListInfo>> malfunctionListRsp) {
        List<MalfunctionListInfo> malfunctionListInfoList = malfunctionListRsp.getData();
        mMalfunctionInfoList.addAll(malfunctionListInfoList);
        //            Collections.sort(mDeviceAlarmLogInfoList);
    }

    public void doMalfunctionDetail(MalfunctionListInfo item) {
        Intent intent = new Intent(mContext, MalfunctionDetailActivity.class);
        intent.putExtra(Constants.EXTRA_MALFUNCTION_INFO, item);
        getView().startAC(intent);

    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mMalfunctionInfoList.clear();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            case Constants.EVENT_DATA_SEARCH_MERCHANT:
                if (isAttachedView()) {
                    getView().cancelSearchState();
                }
                break;
        }
    }

    public void clearSearchHistory() {
        PreferencesSaveAnalyzer.clearAllData(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MALFUNCTION);
        mSearchHistoryList.clear();
        getView().UpdateSearchHistoryList(mSearchHistoryList);
    }

    public void save(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
//        PreferencesHelper.getInstance().saveSearchHistoryText(text, SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MALFUNCTION);
        List<String> malfunctionList = PreferencesSaveAnalyzer.handleDeployRecord(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MALFUNCTION, text);
        mSearchHistoryList.clear();
        mSearchHistoryList.addAll(malfunctionList);
        getView().UpdateSearchHistoryList(mSearchHistoryList);
    }
}

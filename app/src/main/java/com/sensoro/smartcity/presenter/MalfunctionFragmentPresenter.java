package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.LinearLayout;

import com.sensoro.smartcity.activity.MalfunctionDetailActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.constant.SearchHistoryTypeConstants;
import com.sensoro.smartcity.imainviews.IMalfunctionFragmentView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.CalendarDateModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.MalfunctionListInfo;
import com.sensoro.smartcity.server.response.MalfunctionListRsp;
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

public class MalfunctionFragmentPresenter extends BasePresenter<IMalfunctionFragmentView> implements IOnCreate, Constants,
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
        mCalendarPopUtils.setOnCalendarPopupCallbackListener(this);
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
            case DIRECTION_DOWN:
                cur_page = 1;
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getDeviceMalfunctionLogList(cur_page, null, null, tempSearch, temp_startTime,
                        temp_endTime).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<MalfunctionListRsp>(this) {


                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().onPullRefreshComplete();
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }

                    @Override
                    public void onCompleted(MalfunctionListRsp malfunctionListRsp) {
                        getView().dismissProgressDialog();
                        refreshUI(direction, malfunctionListRsp);
                        getView().onPullRefreshComplete();

                    }
                });
                break;
            case DIRECTION_UP:
                cur_page++;
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getDeviceMalfunctionLogList(cur_page, null, null, tempSearch, temp_startTime,
                        temp_endTime).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<MalfunctionListRsp>(this) {


                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        cur_page--;
                        getView().onPullRefreshComplete();
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }

                    @Override
                    public void onCompleted(MalfunctionListRsp malfunctionListRsp) {
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
        requestSearchData(DIRECTION_DOWN, null);
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
            PreferencesHelper.getInstance().saveSearchHistoryText(tempSearch, SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MALFUNCTION);
            mSearchHistoryList.remove(tempSearch);
            mSearchHistoryList.add(0, tempSearch);
            getView().UpdateSearchHistoryList(mSearchHistoryList);

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
        RetrofitServiceHelper.INSTANCE.getDeviceMalfunctionLogList(1, null, null, tempSearch, startTime, endTime
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<MalfunctionListRsp>(this) {


            @Override
            public void onCompleted(MalfunctionListRsp malfunctionListRsp) {
                getView().dismissProgressDialog();
                if (malfunctionListRsp.getData().size() == 0) {
                    getView().toastShort("没有更多数据了");
                }
                refreshUI(DIRECTION_DOWN, malfunctionListRsp);
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

    private void refreshUI(int directionDown, MalfunctionListRsp malfunctionListRsp) {
        if (directionDown == DIRECTION_DOWN) {
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

    private void handleMalfunctionLists(MalfunctionListRsp malfunctionListRsp) {
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
            case EVENT_DATA_SEARCH_MERCHANT:
                if (isAttachedView()) {
                    getView().cancelSearchState();
                }
                break;
        }
    }

    public void clearSearchHistory() {
        PreferencesHelper.getInstance().clearSearchHistory(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MALFUNCTION);
        mSearchHistoryList.clear();
        getView().UpdateSearchHistoryList(mSearchHistoryList);
    }

    public void save(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        PreferencesHelper.getInstance().saveSearchHistoryText(text, SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MALFUNCTION);
        mSearchHistoryList.remove(text);
        mSearchHistoryList.add(0, text);
        getView().UpdateSearchHistoryList(mSearchHistoryList);
    }
}

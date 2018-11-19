package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.LinearLayout;

import com.sensoro.smartcity.activity.MalfunctionDetailActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IMalfunctionFragmentView;
import com.sensoro.smartcity.model.CalendarDateModel;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.MalfunctionListInfo;
import com.sensoro.smartcity.server.response.MalfunctionListRsp;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.widget.popup.CalendarPopUtils;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.sensoro.smartcity.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.smartcity.constant.Constants.DIRECTION_UP;

public class MalfunctionFragmentPresenter extends BasePresenter<IMalfunctionFragmentView> implements
        CalendarPopUtils.OnCalendarPopupCallbackListener {
    private String tempSearch;
    private long startTime;
    private long endTime;
    private Activity mContext;
    private CalendarPopUtils mCalendarPopUtils;
    private int cur_page;
    private List<MalfunctionListInfo> mMalfunctionInfoList = new ArrayList<>();

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

    private void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

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
        requestSearchData(DIRECTION_DOWN, tempSearch);
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
        if (!TextUtils.isEmpty(tempSearch)) {
//            getView().setSelectedDateSearchText(searchText);
            getView().setSearchButtonTextVisible(true);
        } else {
            getView().setSearchButtonTextVisible(false);
        }
        getView().updateAlarmListAdapter(mMalfunctionInfoList);
    }

    private void handleMalfunctionLists(MalfunctionListRsp malfunctionListRsp) {
        List<MalfunctionListInfo> malfunctionListInfoList = malfunctionListRsp.getData();
        mMalfunctionInfoList.addAll(malfunctionListInfoList);
        //            Collections.sort(mDeviceAlarmLogInfoList);
    }

    public void doMalfunctionDetail(MalfunctionListInfo item) {
        Intent intent = new Intent(mContext,MalfunctionDetailActivity.class);
        intent.putExtra(Constants.EXTRA_MALFUNCTION_INFO,item);
        getView().startAC(intent);

    }
}

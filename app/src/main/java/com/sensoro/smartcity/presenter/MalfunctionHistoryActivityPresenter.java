package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.MalfunctionListInfo;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.imainviews.IMalfunctionHistoryActivityView;
import com.sensoro.smartcity.model.CalendarDateModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MalfunctionHistoryActivityPresenter extends BasePresenter<IMalfunctionHistoryActivityView> {
    private Activity mActivity;
    private String mSn;
    private int cur_page;
    private Long startTime;
    private Long endTime;
    private final List<MalfunctionListInfo> mMalfunctionInfoList = new ArrayList<>();
    private final Comparator<MalfunctionListInfo> deviceMalfunctionInfoComparator = new Comparator<MalfunctionListInfo>() {
        @Override
        public int compare(MalfunctionListInfo o1, MalfunctionListInfo o2) {
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
        mActivity = (Activity) context;
        mSn = mActivity.getIntent().getStringExtra(Constants.EXTRA_SENSOR_SN);
        requestDataByFilter(Constants.DIRECTION_DOWN);
    }

    public void requestDataByFilter(final int direction) {
        switch (direction) {
            case Constants.DIRECTION_DOWN:
                cur_page = 1;
                getView().showProgressDialog();
                RetrofitServiceHelper.getInstance().getDeviceMalfunctionLogList(cur_page, mSn, null, null, startTime,
                        endTime).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<MalfunctionListInfo>>>(this) {

                    @Override
                    public void onCompleted(ResponseResult<List<MalfunctionListInfo>> malfunctionListRsp) {
                        freshUI(direction, malfunctionListRsp);
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
            case Constants.DIRECTION_UP:
                cur_page++;
                getView().showProgressDialog();
                RetrofitServiceHelper.getInstance().getDeviceMalfunctionLogList(cur_page, mSn, null, null, startTime,
                        endTime).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<MalfunctionListInfo>>>(this) {


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
                            cur_page--;
                            getView().toastShort(mActivity.getString(R.string.no_more_data));
                            getView().onPullRefreshCompleteNoMoreData();
                        } else {
                            freshUI(direction, malfunctionListRsp);
                            getView().onPullRefreshComplete();
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    private void freshUI(int direction, ResponseResult<List<MalfunctionListInfo>> malfunctionListRsp) {
        if (direction == Constants.DIRECTION_DOWN) {
            mMalfunctionInfoList.clear();
        }
        List<MalfunctionListInfo> malfunctionListInfoList = malfunctionListRsp.getData();
        mMalfunctionInfoList.addAll(malfunctionListInfoList);
        Collections.sort(mMalfunctionInfoList, deviceMalfunctionInfoComparator);
        getView().updateMalfunctionListAdapter(mMalfunctionInfoList);
    }

    @Override
    public void onDestroy() {

    }

    public void doSelectDate() {
        if (startTime == null || endTime == null) {
            startTime = -1L;
            endTime = -1L;
        }
        getView().showCalendar(startTime, endTime);
    }

    public void closeDateSearch() {
        getView().setDateSelectVisible(false);
        startTime = null;
        endTime = null;
        requestDataByFilter(Constants.DIRECTION_DOWN);
    }

    public void onCalendarBack(CalendarDateModel calendarDateModel) {
        getView().setDateSelectVisible(true);
        startTime = DateUtil.strToDate(calendarDateModel.startDate).getTime();
        endTime = DateUtil.strToDate(calendarDateModel.endDate).getTime();
        getView().setDateSelectText(DateUtil.getCalendarYearMothDayFormatDate(startTime) + " ~ " + DateUtil
                .getCalendarYearMothDayFormatDate(endTime));
//        getView().setSelectedDateSearchText(DateUtil.getMothDayFormatDate(startTime) + "-" + DateUtil
//                .getMothDayFormatDate(endTime));
        endTime += 1000 * 60 * 60 * 24;
        requestDataByFilter(Constants.DIRECTION_DOWN);
    }
}

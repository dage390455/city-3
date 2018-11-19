package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.DeployManualActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IMalfunctionHistoryActivityView;
import com.sensoro.smartcity.model.CalendarDateModel;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.server.bean.MalfunctionListInfo;
import com.sensoro.smartcity.server.response.DeviceAlarmLogRsp;
import com.sensoro.smartcity.server.response.MalfunctionListRsp;
import com.sensoro.smartcity.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.sensoro.smartcity.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.smartcity.constant.Constants.DIRECTION_UP;
import static com.sensoro.smartcity.constant.Constants.DISPLAY_STATUS_ALARM;
import static com.sensoro.smartcity.constant.Constants.DISPLAY_STATUS_CONFIRM;
import static com.sensoro.smartcity.constant.Constants.DISPLAY_STATUS_MIS_DESCRIPTION;

public class MalfunctionHistoryActivityPresenter extends BasePresenter<IMalfunctionHistoryActivityView> {
    private Activity mActivity;
    private String mSn;
    private int cur_page;
    private Long startTime;
    private Long endTime;
    private final List<MalfunctionListInfo> mMalfunctionInfoList = new ArrayList<>();

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        mSn = mActivity.getIntent().getStringExtra(Constants.EXTRA_SENSOR_SN);
        requestDataByFilter(DIRECTION_DOWN);
    }

    public void requestDataByFilter(final int direction) {
        switch (direction) {
            case DIRECTION_DOWN:
                cur_page = 1;
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getDeviceMalfunctionLogList(cur_page, mSn, null, null, startTime,
                        endTime).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<MalfunctionListRsp>(this) {

                    @Override
                    public void onCompleted(MalfunctionListRsp malfunctionListRsp) {
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
            case DIRECTION_UP:
                cur_page++;
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getDeviceMalfunctionLogList(cur_page, mSn, null, null, startTime,
                        endTime).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<MalfunctionListRsp>(this) {


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

    private void freshUI(int direction, MalfunctionListRsp malfunctionListRsp) {
        if (direction == DIRECTION_DOWN) {
            mMalfunctionInfoList.clear();
        }
        handleMalfunctioninfo(malfunctionListRsp);
        getView().updateMalfunctionListAdapter(mMalfunctionInfoList);
    }

    private void handleMalfunctioninfo(MalfunctionListRsp malfunctionListRsp) {
        List<MalfunctionListInfo> malfunctionListInfoList = malfunctionListRsp.getData();
        mMalfunctionInfoList.addAll(malfunctionListInfoList);
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
        requestDataByFilter(DIRECTION_DOWN);
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
}

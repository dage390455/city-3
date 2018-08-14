package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.AlarmDetailActivity;
import com.sensoro.smartcity.activity.CalendarActivity;
import com.sensoro.smartcity.activity.SearchAlarmActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IAlarmListFragmentView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.CalendarDateModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.model.SearchAlarmResultModel;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.server.response.DeviceAlarmItemRsp;
import com.sensoro.smartcity.server.response.DeviceAlarmLogRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.widget.popup.SensoroPopupAlarmView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AlarmListFragmentPresenter extends BasePresenter<IAlarmListFragmentView> implements IOnCreate, Constants,
        SensoroPopupAlarmView.OnPopupCallbackListener {
    private final List<DeviceAlarmLogInfo> mDeviceAlarmLogInfoList = new ArrayList<>();
    private volatile int cur_page = 1;
    private long startTime;
    private long endTime;
    private Activity mContext;
    private boolean isReConfirm = false;
    private DeviceAlarmLogInfo mCurrentDeviceAlarmLogInfo;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
    }

    private void freshUI(int direction, DeviceAlarmLogRsp deviceAlarmLogRsp, String searchText) {
        if (direction == DIRECTION_DOWN) {
            mDeviceAlarmLogInfoList.clear();
        }
        handleDeviceAlarmLogs(deviceAlarmLogRsp);
        if (!TextUtils.isEmpty(searchText)) {
            getView().setAlarmSearchText(searchText);
        }
        getView().updateAlarmListAdapter(mDeviceAlarmLogInfoList);
    }

    /**
     * 处理接收的数据
     *
     * @param deviceAlarmLogRsp
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


    private void freshUI(String type) {
        try {
            List<DeviceAlarmLogInfo> tempList = new ArrayList<>();
            String typeArray[] = type.split(",");
            for (int i = 0; i < mDeviceAlarmLogInfoList.size(); i++) {
                DeviceAlarmLogInfo alarmLogInfo = mDeviceAlarmLogInfoList.get(i);
                String alarmType = alarmLogInfo.getSensorType();
                boolean isContains = Arrays.asList(typeArray).contains(alarmType);
                if (isContains) {
                    tempList.add(alarmLogInfo);
                }
            }
            getView().updateAlarmListAdapter(tempList);
        } catch (Exception e) {
            e.printStackTrace();
            getView().toastShort(mContext.getResources().getString(R.string.tips_data_error));
        }

    }

    private void requestDataBySearchDown(Long startTime, Long endTime, final String searchType) {
        switch (SensoroCityApplication.getInstance().saveSearchType) {
            case Constants.TYPE_DEVICE_NAME:
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getDeviceAlarmLogList(cur_page, null, searchType, null, startTime,
                        endTime,
                        null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>() {


                    @Override
                    public void onCompleted() {
                        getView().dismissProgressDialog();
                        getView().onPullRefreshComplete();
                    }

                    @Override
                    public void onNext(DeviceAlarmLogRsp deviceAlarmLogRsp) {
                        freshUI(DIRECTION_DOWN, deviceAlarmLogRsp, null);
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }
                });
                break;
            case Constants.TYPE_DEVICE_SN:
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getDeviceAlarmLogList(cur_page, searchType, null, null, startTime,
                        endTime,
                        null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>() {


                    @Override
                    public void onCompleted() {
                        getView().dismissProgressDialog();
                        getView().onPullRefreshComplete();
                    }

                    @Override
                    public void onNext(DeviceAlarmLogRsp deviceAlarmLogRsp) {
                        freshUI(DIRECTION_DOWN, deviceAlarmLogRsp, null);
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }
                });
                break;
            case Constants.TYPE_DEVICE_PHONE_NUM:
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getDeviceAlarmLogList(cur_page, null, null, searchType, startTime,
                        endTime,
                        null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>() {


                    @Override
                    public void onCompleted() {
                        getView().dismissProgressDialog();
                        getView().onPullRefreshComplete();
                    }

                    @Override
                    public void onNext(DeviceAlarmLogRsp deviceAlarmLogRsp) {
                        freshUI(DIRECTION_DOWN, deviceAlarmLogRsp, null);
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }
                });
                break;
            default:
                break;
        }

    }

    private void requestDataBySearchUp(Long startTime, Long endTime, final String searchType) {
        switch (SensoroCityApplication.getInstance().saveSearchType) {
            case Constants.TYPE_DEVICE_NAME:
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getDeviceAlarmLogList(cur_page, null, searchType, null, startTime,
                        endTime,
                        null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>() {


                    @Override
                    public void onCompleted() {
                        getView().dismissProgressDialog();
                        getView().onPullRefreshComplete();
                    }

                    @Override
                    public void onNext(DeviceAlarmLogRsp deviceAlarmLogRsp) {
                        if (deviceAlarmLogRsp.getData().size() == 0) {
                            getView().toastShort("没有更多数据了");
                            cur_page--;
                        } else {
                            freshUI(DIRECTION_UP, deviceAlarmLogRsp, null);
                        }
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        cur_page--;
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }
                });
                break;
            case Constants.TYPE_DEVICE_SN:
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getDeviceAlarmLogList(cur_page, searchType, null, null, startTime,
                        endTime,
                        null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>() {


                    @Override
                    public void onCompleted() {
                        getView().dismissProgressDialog();
                        getView().onPullRefreshComplete();
                    }

                    @Override
                    public void onNext(DeviceAlarmLogRsp deviceAlarmLogRsp) {
                        if (deviceAlarmLogRsp.getData().size() == 0) {
                            getView().toastShort("没有更多数据了");
                            cur_page--;
                        } else {
                            freshUI(DIRECTION_UP, deviceAlarmLogRsp, null);
                        }
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        cur_page--;
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }
                });
                break;
            case Constants.TYPE_DEVICE_PHONE_NUM:
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getDeviceAlarmLogList(cur_page, null, null, searchType, startTime,
                        endTime,
                        null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>() {


                    @Override
                    public void onCompleted() {
                        getView().dismissProgressDialog();
                        getView().onPullRefreshComplete();
                    }

                    @Override
                    public void onNext(DeviceAlarmLogRsp deviceAlarmLogRsp) {
                        if (deviceAlarmLogRsp.getData().size() == 0) {
                            getView().toastShort("没有更多数据了");
                            cur_page--;
                        } else {
                            freshUI(DIRECTION_UP, deviceAlarmLogRsp, null);
                        }
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        cur_page--;
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }
                });
                break;
            default:
                break;
        }

    }

    public void requestSearchData(int direction, boolean isForce, String searchText) {
        if (getView().getPullRefreshState() == PullToRefreshBase.State.RESET && !isForce || TextUtils.isEmpty
                (searchText)) {
            return;
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
                requestDataBySearchDown(temp_startTime, temp_endTime, searchText);
                break;
            case DIRECTION_UP:
                cur_page++;
                requestDataBySearchUp(temp_startTime, temp_endTime, searchText);
                break;
            default:
                break;
        }
    }

    public void requestDataAll(final int direction, boolean isForce) {
        if (getView().getPullRefreshState() == PullToRefreshBase.State.RESET && !isForce) {
            return;
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
                RetrofitServiceHelper.INSTANCE.getDeviceAlarmLogList(cur_page, null, null, null, temp_startTime,
                        temp_endTime,
                        null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>() {


                    @Override
                    public void onCompleted() {
                        getView().dismissProgressDialog();
                        getView().onPullRefreshComplete();
                    }

                    @Override
                    public void onNext(DeviceAlarmLogRsp deviceAlarmLogRsp) {
                        if (deviceAlarmLogRsp.getData().size() == 0) {
                            getView().toastShort("没有更多数据了");
                        } else {
                            freshUI(direction, deviceAlarmLogRsp, null);
                        }
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }
                });
                break;
            case DIRECTION_UP:
                cur_page++;
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getDeviceAlarmLogList(cur_page, null, null, null, temp_startTime,
                        temp_endTime,
                        null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>() {


                    @Override
                    public void onCompleted() {
                        getView().dismissProgressDialog();
                        getView().onPullRefreshComplete();
                    }

                    @Override
                    public void onNext(DeviceAlarmLogRsp deviceAlarmLogRsp) {
                        if (deviceAlarmLogRsp.getData().size() == 0) {
                            cur_page--;
                            getView().toastShort("没有更多数据了");
                        } else {
                            freshUI(direction, deviceAlarmLogRsp, null);
                        }
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        cur_page--;
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
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
                .getMothDayFormatDate
                        (endTime));
        endTime += 1000 * 60 * 60 * 24;
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getDeviceAlarmLogList(1, null, null, null, startTime, endTime,
                null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>() {


            @Override
            public void onCompleted() {
                getView().dismissProgressDialog();
                getView().onPullRefreshComplete();
            }

            @Override
            public void onNext(DeviceAlarmLogRsp deviceAlarmLogRsp) {
                if (deviceAlarmLogRsp.getData().size() == 0) {
                    getView().toastShort("没有更多数据了");
                }
                freshUI(DIRECTION_DOWN, deviceAlarmLogRsp, null);
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }

    public void clickItem(int position) {
        Intent intent = new Intent(mContext, AlarmDetailActivity.class);
        intent.putExtra(EXTRA_ALARM_INFO, mDeviceAlarmLogInfoList.get(position - 1));
        intent.putExtra(EXTRA_ALARM_IS_RE_CONFIRM, isReConfirm);
        getView().startAC(intent);
    }

    public void clickItemByConfirmStatus(int position, boolean isReConfirm) {
        this.isReConfirm = isReConfirm;
        mCurrentDeviceAlarmLogInfo = mDeviceAlarmLogInfoList.get(position);
        getView().showAlarmPopupView();
    }

    private void freshDeviceAlarmLogInfo(DeviceAlarmLogInfo deviceAlarmLogInfo) {
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
////                Collections.sort(mDeviceAlarmLogInfoList);
                break;
            }
        }
        ArrayList<DeviceAlarmLogInfo> tempList = new ArrayList<>(mDeviceAlarmLogInfoList);
        getView().updateAlarmListAdapter(tempList);
    }

    /**
     * 直接进入搜索界面
     */
    public void searchByImageView() {
        long temp_startTime = -1;
        long temp_endTime = -1;
        if (getView().isSelectedDateLayoutVisible()) {
            temp_startTime = startTime;
            temp_endTime = endTime;
        }
        Intent searchIntent = new Intent(mContext, SearchAlarmActivity.class);
        searchIntent.putExtra(PREFERENCE_KEY_START_TIME, temp_startTime);
        searchIntent.putExtra(PREFERENCE_KEY_END_TIME, temp_endTime);
        searchIntent.putExtra(EXTRA_FRAGMENT_INDEX, 2);
        getView().startAC(searchIntent);

    }

    /**
     * 通过搜索内容搜索
     *
     * @param charSequence
     */
    public void searchByEditText(CharSequence charSequence) {
        long temp_startTime = -1;
        long temp_endTime = -1;
        if (getView().isSelectedDateLayoutVisible()) {
            temp_startTime = startTime;
            temp_endTime = endTime;
        }
        Intent searchIntent = new Intent(mContext, SearchAlarmActivity.class);
        if (!TextUtils.isEmpty(charSequence) && getView().isSearchLayoutVisible()) {
            searchIntent.putExtra(EXTRA_SEARCH_CONTENT, charSequence.toString().trim());
        } else {
            searchIntent.putExtra(EXTRA_SEARCH_CONTENT, "");
        }
        searchIntent.putExtra(PREFERENCE_KEY_START_TIME, temp_startTime);
        searchIntent.putExtra(PREFERENCE_KEY_END_TIME, temp_endTime);
        searchIntent.putExtra(EXTRA_FRAGMENT_INDEX, 2);
        getView().startAC(searchIntent);
    }

    /**
     * 单独点击日期
     */
    public void clickByDate() {
        long temp_startTime = -1;
        long temp_endTime = -1;
        if (getView().isSelectedDateLayoutVisible()) {
            temp_startTime = startTime;
            temp_endTime = endTime;
        }
        Intent intent = new Intent(mContext, CalendarActivity.class);
        if (getView().isSelectedDateLayoutVisible()) {
            intent.putExtra(PREFERENCE_KEY_START_TIME, temp_startTime);
            intent.putExtra(PREFERENCE_KEY_END_TIME, temp_endTime);
        }
        getView().startAC(intent);
    }

    @Override
    public void onPopupCallback(int statusResult, int statusType, int statusPlace, List<String> images, String remark) {
        getView().showProgressDialog();
        getView().setUpdateButtonClickable(false);
        RetrofitServiceHelper.INSTANCE.doUpdatePhotosUrl(mCurrentDeviceAlarmLogInfo.get_id(), statusResult,
                statusType, statusPlace,
                remark, isReConfirm, images).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe
                        (new CityObserver<DeviceAlarmItemRsp>() {


                            @Override
                            public void onCompleted() {
                                getView().dismissProgressDialog();
                                getView().dismissAlarmPopupView();
                            }

                            @Override
                            public void onNext(DeviceAlarmItemRsp deviceAlarmItemRsp) {
                                if (deviceAlarmItemRsp.getErrcode() == ResponseBase.CODE_SUCCESS) {
                                    DeviceAlarmLogInfo deviceAlarmLogInfo = deviceAlarmItemRsp.getData();
                                    getView().toastShort(mContext.getResources().getString(R.string
                                            .tips_commit_success));
                                    freshDeviceAlarmLogInfo(deviceAlarmLogInfo);
                                } else {
                                    getView().toastShort(mContext.getResources().getString(R.string
                                            .tips_commit_failed));
                                }
                            }

                            @Override
                            public void onErrorMsg(int errorCode, String errorMsg) {
                                getView().setUpdateButtonClickable(true);
                                getView().dismissProgressDialog();
                                getView().dismissAlarmPopupView();
                                getView().toastShort(errorMsg);
                            }
                        });
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mDeviceAlarmLogInfoList.clear();
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        //TODO 可以修改以此种方式传递，方便管理
        int code = eventData.code;
        Object data = eventData.data;
        //
        if (code == EVENT_DATA_SELECT_CALENDAR) {
            if (data instanceof CalendarDateModel) {
                requestDataByDate(((CalendarDateModel) data).startDate, ((CalendarDateModel) data).endDate);
            }
        } else if (code == EVENT_DATA_ALARM_DETAIL_RESULT) {
            if (data instanceof DeviceAlarmLogInfo) {
                freshDeviceAlarmLogInfo((DeviceAlarmLogInfo) data);
            }
        } else if (code == EVENT_DATA_SEARCH_ALARM_RESULT) {
            if (data instanceof SearchAlarmResultModel) {
                freshUI(DIRECTION_DOWN, ((SearchAlarmResultModel) data).deviceAlarmLogRsp, ((SearchAlarmResultModel)
                        data).searchAlarmText);
            }
        }
    }

    //    @Override
//    public void onPopupCallback(int status, String remark) {
////        byte[] bytes = new byte[0];
////        try {
////            bytes = remark.getBytes("UTF-8");
////        } catch (UnsupportedEncodingException e) {
////            e.printStackTrace();
////        }
////        if (bytes.length > 30) {
////            Toast.makeText(mContext, "最大不能超过32个字符", Toast.LENGTH_SHORT).show();
////            return;
////        }
////        if (remark.length() > 30) {
////            getView().toastShort("最大不能超过30个字符");
////            return;
////        }
//        getView().showProgressDialog();
//        RetrofitServiceHelper.INSTANCE.doAlarmConfirm(mCurrentDeviceAlarmLogInfo.get_id(), status,
//                remark, isReConfirm).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe
//                (new CityObserver<DeviceAlarmItemRsp>() {
//
//
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onNext(DeviceAlarmItemRsp deviceAlarmItemRsp) {
//                        getView().dismissProgressDialog();
//                        getView().dismissAlarmPopupView();
//                        if (deviceAlarmItemRsp.getErrcode() == ResponseBase.CODE_SUCCESS) {
//                            DeviceAlarmLogInfo deviceAlarmLogInfo = deviceAlarmItemRsp.getData();
//                            getView().toastShort(mContext.getResources().getString(R.string.tips_commit_success));
//                            freshDeviceAlarmLogInfo(deviceAlarmLogInfo);
//                        } else {
//                            getView().toastShort(mContext.getResources().getString(R.string.tips_commit_failed));
//                        }
//                    }
//
//                    @Override
//                    public void onErrorMsg(int errorCode, String errorMsg) {
//                        getView().dismissProgressDialog();
//                        getView().dismissAlarmPopupView();
//                        getView().toastShort(errorMsg);
//                    }
//                });
//    }
}

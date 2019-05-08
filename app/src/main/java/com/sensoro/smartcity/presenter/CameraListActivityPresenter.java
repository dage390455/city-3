package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.CameraDetailActivity;
import com.sensoro.smartcity.analyzer.PreferencesSaveAnalyzer;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.constant.SearchHistoryTypeConstants;
import com.sensoro.smartcity.imainviews.ICameraListActivityView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.CalendarDateModel;
import com.sensoro.smartcity.model.CameraFilterModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceCameraDetailInfo;
import com.sensoro.smartcity.server.bean.DeviceCameraInfo;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.server.response.CameraFilterRsp;
import com.sensoro.smartcity.server.response.DeviceCameraDetailRsp;
import com.sensoro.smartcity.server.response.DeviceCameraListRsp;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.widget.popup.AlarmPopUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CameraListActivityPresenter extends BasePresenter<ICameraListActivityView> implements IOnCreate, Constants, AlarmPopUtils.OnPopupCallbackListener {
    private Activity mContext;
    private Long startTime;
    private Long endTime;
    private volatile int cur_page = 1;
    private final List<DeviceCameraInfo> deviceCameraInfos = new ArrayList<>();
    private final List<String> mSearchHistoryList = new ArrayList<>();
    private HashMap filterHashMap = new HashMap();

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        Serializable serializableExtra = mContext.getIntent().getSerializableExtra(EXTRA_DEVICE_CAMERA_DETAIL_INFO_LIST);
        if (serializableExtra instanceof ArrayList) {
            getView().setSmartRefreshEnable(false);
            deviceCameraInfos.clear();
            List<DeviceCameraInfo> data = (List<DeviceCameraInfo>) serializableExtra;
            deviceCameraInfos.addAll(data);
            getView().updateDeviceCameraAdapter(deviceCameraInfos);
            getView().onPullRefreshComplete();
            getView().dismissProgressDialog();
        } else {
            requestDataByFilter(DIRECTION_DOWN);
        }
        List<String> list = PreferencesHelper.getInstance().getSearchHistoryData(SearchHistoryTypeConstants.TYPE_SEARCH_CAMERALIST);
        if (list != null) {
            mSearchHistoryList.addAll(list);
            getView().updateSearchHistoryList(mSearchHistoryList);
        }


    }


    public void save(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
//        mSearchHistoryList.remove(text);
//        PreferencesHelper.getInstance().saveSearchHistoryText(text, SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_WARN);
        List<String> warnList = PreferencesSaveAnalyzer.handleDeployRecord(SearchHistoryTypeConstants.TYPE_SEARCH_CAMERALIST, text);
//        mSearchHistoryList.add(0, text);
        mSearchHistoryList.clear();
        mSearchHistoryList.addAll(warnList);
        getView().updateSearchHistoryList(mSearchHistoryList);

    }

    public void clearSearchHistory() {
        PreferencesSaveAnalyzer.clearAllData(SearchHistoryTypeConstants.TYPE_SEARCH_CAMERALIST);
        mSearchHistoryList.clear();
        getView().updateSearchHistoryList(mSearchHistoryList);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    public void getFilterPopData() {

        getView().showProgressDialog();
        RetrofitServiceHelper.getInstance().getCameraFilter().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<CameraFilterRsp>(this) {
            @Override
            public void onCompleted(CameraFilterRsp cameraFilterRsp) {
                List<CameraFilterModel> data = cameraFilterRsp.getData();
                if (data != null) {
                    getView().updateFilterPop(data);
                }
                getView().dismissProgressDialog();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
                getView().dismissProgressDialog();
            }
        });

    }

    public void onClickDeviceCamera(final DeviceCameraInfo deviceCameraInfo) {
        String sn = deviceCameraInfo.getSn();
        final String cid = deviceCameraInfo.getCid();
//        deviceCameraInfo.getInfo().

//        getView().startAC(new Intent(mContext, CameraDetailActivity.class));
        getView().showProgressDialog();
        RetrofitServiceHelper.getInstance().getDeviceCamera(sn).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceCameraDetailRsp>(null) {
            @Override
            public void onCompleted(DeviceCameraDetailRsp deviceCameraDetailRsp) {
                DeviceCameraDetailInfo data = deviceCameraDetailRsp.getData();
                if (data != null) {
                    String hls = data.getHls();
                    String name = data.getCamera().getName();
                    Intent intent = new Intent();
                    intent.setClass(mContext, CameraDetailActivity.class);
                    intent.putExtra("cid", cid);
                    intent.putExtra("hls", hls);
                    intent.putExtra("cameraName", name);
                    intent.putExtra("deviceStatus", deviceCameraInfo.getDeviceStatus());
                    intent.putExtra("lastCover", data.getLastCover());
                    getView().startAC(intent);
                } else {
                    getView().toastShort(mContext.getString(R.string.camera_info_get_failed));

                }
                getView().dismissProgressDialog();

            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
                getView().dismissProgressDialog();
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        //
        switch (code) {
        }
    }


    public void onCalendarBack(CalendarDateModel calendarDateModel) {
        if (isAttachedView()) {
            getView().setDateSelectVisible(true);
        }
        startTime = DateUtil.strToDate(calendarDateModel.startDate).getTime();
        endTime = DateUtil.strToDate(calendarDateModel.endDate).getTime();
        if (isAttachedView()) {
            getView().setDateSelectText(DateUtil.getCalendarYearMothDayFormatDate(startTime) + " ~ " + DateUtil
                    .getCalendarYearMothDayFormatDate(endTime));
        }
//        getView().setSelectedDateSearchText(DateUtil.getMothDayFormatDate(startTime) + "-" + DateUtil
//                .getMothDayFormatDate(endTime));
        endTime += 1000 * 60 * 60 * 24;
        requestDataByFilter(DIRECTION_DOWN);
    }


    public void getDeviceCameraListByFilter(HashMap map) {
        cur_page = 1;
        HashMap hashMap = new HashMap();
        hashMap.put("pageSize", 20);
        hashMap.put("page", cur_page);

        if (null != map) {

            filterHashMap = map;
            hashMap.putAll(map);
        } else {
            filterHashMap.clear();
        }
        requestData(hashMap, DIRECTION_DOWN);

    }

    public void clearMap() {

        filterHashMap.clear();
    }


    public void requestData(final HashMap hashMap, final int directionDown) {

        if (isAttachedView()) {
            getView().showProgressDialog();
        }
        RetrofitServiceHelper.getInstance().getDeviceCameraListByFilter(hashMap).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceCameraListRsp>(this) {
            @Override
            public void onCompleted(DeviceCameraListRsp deviceCameraListRsp) {


                if (cur_page == 1) {
                    deviceCameraInfos.clear();
                }
                List<DeviceCameraInfo> data = deviceCameraListRsp.getData();


                if (directionDown == DIRECTION_DOWN && data.size() == 0) {
                    if (isAttachedView()) {
                        getView().toastShort(mContext.getString(R.string.no_more_data));
                        getView().onPullRefreshCompleteNoMoreData();
                    }
                }
                if (data != null && data.size() > 0) {
                    deviceCameraInfos.addAll(data);
                }
                getView().updateDeviceCameraAdapter(deviceCameraInfos);
                getView().onPullRefreshComplete();
                getView().dismissProgressDialog();


            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
                getView().onPullRefreshComplete();

            }
        });
    }

    public void requestDataByFilter(final int direction) {
        HashMap hashMap = new HashMap(16);
        hashMap.put("pageSize", 20);
        if (filterHashMap.size() > 0) {
            hashMap.putAll(filterHashMap);
        }
        switch (direction) {
            case DIRECTION_DOWN:
                cur_page = 1;
                hashMap.put("page", cur_page);


                requestData(hashMap, DIRECTION_DOWN);
                break;
            case DIRECTION_UP:
                cur_page++;
                hashMap.put("page", cur_page);

                requestData(hashMap, DIRECTION_UP);

                break;
            default:

                break;
        }


    }


    public void closeDateSearch() {
        if (isAttachedView()) {
            getView().setDateSelectVisible(false);
        }
        startTime = null;
        endTime = null;
//        requestDataByFilter(DIRECTION_DOWN);
    }

    public void doSelectDate() {
        if (startTime == null || endTime == null) {
            startTime = -1L;
            endTime = -1L;
        }
        if (isAttachedView()) {
            getView().showCalendar(startTime, endTime);
        }
    }

    @Override
    public void onPopupCallback(int statusResult, int statusType, int statusPlace, List<ScenesData> scenesDataList, String remark) {
        if (isAttachedView()) {
            getView().showProgressDialog();
        }
//        RetrofitServiceHelper.getInstance().doUpdatePhotosUrl(mCurrentDeviceAlarmLogInfo.get_id(), statusResult,
//                statusType, statusPlace,
//                remark, false, scenesDataList).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                .subscribe
//                        (new CityObserver<DeviceAlarmItemRsp>(this) {
//
//
//                            @Override
//                            public void onErrorMsg(int errorCode, String errorMsg) {
//                                if (isAttachedView()) {
//                                    getView().dismissProgressDialog();
//                                    getView().toastShort(errorMsg);
//                                }
//                            }
//
//                            @Override
//                            public void onCompleted(DeviceAlarmItemRsp deviceAlarmItemRsp) {
//                                if (deviceAlarmItemRsp.getErrcode() == ResponseBase.CODE_SUCCESS) {
//                                    DeviceAlarmLogInfo deviceAlarmLogInfo = deviceAlarmItemRsp.getData();
//                                    if (isAttachedView()) {
//                                        getView().toastShort(mContext.getResources().getString(R.string
//                                                .tips_commit_success));
//                                    }
//                                    freshDeviceAlarmLogInfo(deviceAlarmLogInfo);
//                                    pushAlarmFresh(deviceAlarmLogInfo);
//                                } else {
//                                    if (isAttachedView()) {
//                                        getView().toastShort(mContext.getResources().getString(R.string
//                                                .tips_commit_failed));
//                                    }
//                                }
//                                if (isAttachedView()) {
//                                    getView().dismissProgressDialog();
//                                }
//                                if (alarmPopUtils != null) {
//                                    alarmPopUtils.dismiss();
//                                }
//
//                            }
//                        });
    }


    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

}

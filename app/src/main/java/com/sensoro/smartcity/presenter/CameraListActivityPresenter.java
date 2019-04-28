package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.ICameraListActivityView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.CalendarDateModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceCameraDetailInfo;
import com.sensoro.smartcity.server.bean.DeviceCameraInfo;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.server.response.DeviceCameraDetailRsp;
import com.sensoro.smartcity.server.response.DeviceCameraListRsp;
import com.sensoro.smartcity.activity.CameraDetailActivity;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.widget.popup.AlarmPopUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CameraListActivityPresenter extends BasePresenter<ICameraListActivityView> implements IOnCreate, Constants, AlarmPopUtils.OnPopupCallbackListener {
    private Activity mContext;
    private Long startTime;
    private Long endTime;
    private volatile int cur_page = 1;
    private final List<DeviceCameraInfo> deviceCameraInfos = new ArrayList<>();

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

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    public void onClickDeviceCamera(DeviceCameraInfo deviceCameraInfo) {
        String sn = deviceCameraInfo.getSn();
        final String cid = deviceCameraInfo.getCid();
//        deviceCameraInfo.getInfo().

//        getView().startAC(new Intent(mContext, CameraDetailActivity.class));
        getView().showProgressDialog();
        RetrofitServiceHelper.getInstance().getDeviceCamera(sn).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceCameraDetailRsp>(null) {
            @Override
            public void onCompleted(DeviceCameraDetailRsp deviceCameraDetailRsp) {
                DeviceCameraDetailInfo data = deviceCameraDetailRsp.getData();
                String hls = data.getHls();
                Intent intent = new Intent();
                intent.setClass(mContext, CameraDetailActivity.class);
                intent.putExtra("cid", cid);
                intent.putExtra("hls", hls);
                getView().startAC(intent);
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

    public void requestDataByFilter(final int direction) {
        switch (direction) {
            case DIRECTION_DOWN:
                cur_page = 1;
                if (isAttachedView()) {
                    getView().showProgressDialog();
                }
                RetrofitServiceHelper.getInstance().getDeviceCameraList(20, cur_page, null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceCameraListRsp>(null) {
                    @Override
                    public void onCompleted(DeviceCameraListRsp deviceCameraListRsp) {
                        deviceCameraInfos.clear();
                        List<DeviceCameraInfo> data = deviceCameraListRsp.getData();
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
                break;
            case DIRECTION_UP:
                cur_page++;
                if (isAttachedView()) {
                    getView().showProgressDialog();
                }
                RetrofitServiceHelper.getInstance().getDeviceCameraList(20, cur_page, null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceCameraListRsp>(null) {
                    @Override
                    public void onCompleted(DeviceCameraListRsp deviceCameraListRsp) {
                        List<DeviceCameraInfo> data = deviceCameraListRsp.getData();
                        if (data != null && data.size() > 0) {
                            deviceCameraInfos.addAll(data);
                            getView().updateDeviceCameraAdapter(deviceCameraInfos);
                        } else {
                            getView().toastShort(mContext.getString(R.string.no_more_data));
                        }
                        getView().dismissProgressDialog();
                        getView().onPullRefreshComplete();
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                        getView().onPullRefreshComplete();
                    }
                });
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

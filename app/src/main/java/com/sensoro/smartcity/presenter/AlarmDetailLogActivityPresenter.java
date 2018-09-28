package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.amap.api.maps.model.LatLng;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageAlarmPhotoDetailActivity;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.AlarmHistoryLogActivity;
import com.sensoro.smartcity.activity.VideoPlayActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IAlarmDetailLogActivityView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.AlarmPopModel;
import com.sensoro.smartcity.model.EventAlarmStatusModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.server.response.AlarmCountRsp;
import com.sensoro.smartcity.server.response.DeviceAlarmItemRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.popup.AlarmPopUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.lzy.imagepicker.ImagePicker.EXTRA_RESULT_BY_TAKE_PHOTO;

public class AlarmDetailLogActivityPresenter extends BasePresenter<IAlarmDetailLogActivityView> implements Constants, IOnCreate, AlarmPopUtils.OnPopupCallbackListener {
    private final List<AlarmInfo.RecordInfo> mList = new ArrayList<>();
    private DeviceAlarmLogInfo deviceAlarmLogInfo;
    private boolean isReConfirm = false;
    private Activity mContext;
    private LatLng destPosition = null;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        deviceAlarmLogInfo = (DeviceAlarmLogInfo) mContext.getIntent().getSerializableExtra(EXTRA_ALARM_INFO);
        refreshData(true);
    }

    public void doBack() {
        EventData eventData = new EventData();
        eventData.code = EVENT_DATA_ALARM_DETAIL_RESULT;
        eventData.data = deviceAlarmLogInfo;
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        //TODO 可以修改以此种方式传递，方便管理
        int code = eventData.code;
        Object data = eventData.data;
        //
        if (code == EVENT_DATA_ALARM_FRESH_ALARM_DATA) {
            if (data instanceof DeviceAlarmLogInfo) {
                if (this.deviceAlarmLogInfo.get_id().equals(((DeviceAlarmLogInfo) data).get_id())) {
                    this.deviceAlarmLogInfo = (DeviceAlarmLogInfo) data;
                }

            }
        } else if (code == EVENT_DATA_ALARM_SOCKET_DISPLAY_STATUS) {
            if (data instanceof EventAlarmStatusModel) {
                EventAlarmStatusModel tempEventAlarmStatusModel = (EventAlarmStatusModel) data;
                if (deviceAlarmLogInfo.get_id().equals(tempEventAlarmStatusModel.deviceAlarmLogInfo.get_id())) {
                    switch (tempEventAlarmStatusModel.status) {
                        case MODEL_ALARM_STATUS_EVENT_CODE_RECOVERY:
                            // 做一些预警恢复的逻辑
                        case MODEL_ALARM_STATUS_EVENT_CODE_CONFIRM:
                            // 做一些预警被确认的逻辑
                        case MODEL_ALARM_STATUS_EVENT_CODE_RECONFIRM:
                            // 做一些预警被再次确认的逻辑
                            deviceAlarmLogInfo = tempEventAlarmStatusModel.deviceAlarmLogInfo;
                            refreshData(false);
                            break;
                        default:
                            // 未知逻辑 可以联系我确认 有可能是bug
                            break;
                    }
                }
            }
        }
    }

    public void refreshData(boolean isInit) {
        //
        String deviceName = deviceAlarmLogInfo.getDeviceName();
        getView().setDeviceNameTextView(TextUtils.isEmpty(deviceName) ? deviceAlarmLogInfo.getDeviceSN() : deviceName);
        String alarmTime = DateUtil.getFullParseDate(deviceAlarmLogInfo.getUpdatedTime());
        //TODO 半年累计报警次数
        long current = System.currentTimeMillis();
        if (isInit) {
            getView().showProgressDialog();
        }
        RetrofitServiceHelper.INSTANCE.getAlarmCount(current - 3600 * 24 * 180 * 1000L, current, null, deviceAlarmLogInfo.getDeviceSN()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<AlarmCountRsp>() {
            @Override
            public void onCompleted(AlarmCountRsp alarmCountRsp) {
                int count = alarmCountRsp.getCount();
                getView().setAlarmCount(count + "");
                getView().dismissProgressDialog();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
//        getView().setDisplayStatus(deviceAlarmLogInfo.getDisplayStatus());
//        getView().setSensoroIv(deviceAlarmLogInfo.getSensorType());
        AlarmInfo.RecordInfo[] recordInfoArray = deviceAlarmLogInfo.getRecords();
        if (recordInfoArray != null) {
            mList.clear();
            for (int i = recordInfoArray.length - 1; i >= 0; i--) {
                mList.add(recordInfoArray[i]);
            }
            getView().updateAlertLogContentAdapter(mList);
            //
            switch (deviceAlarmLogInfo.getDisplayStatus()) {
                case DISPLAY_STATUS_CONFIRM:
                    isReConfirm = false;
                    getView().setConfirmText("预警确认");
                    break;
                case DISPLAY_STATUS_ALARM:
                case DISPLAY_STATUS_MIS_DESCRIPTION:
                case DISPLAY_STATUS_TEST:
                case DISPLAY_STATUS_RISKS:
                    isReConfirm = true;
                    getView().setConfirmText("再次确认");
                    break;
            }
            for (AlarmInfo.RecordInfo recordInfo : recordInfoArray) {
                if (recordInfo.getType().equals("recovery")) {
                    getView().setCurrentAlarmState(0, alarmTime);
                    return;
                }
            }
            getView().setCurrentAlarmState(1, alarmTime);
        }


    }

    public void clickPhotoItem(int position, List<ScenesData> scenesDataList) {
        //
        ArrayList<ImageItem> items = new ArrayList<>();
        if (scenesDataList != null && scenesDataList.size() > 0) {
            for (ScenesData scenesData : scenesDataList) {
                ImageItem imageItem = new ImageItem();
                imageItem.fromUrl = true;
                if ("video".equals(scenesData.type)) {
                    imageItem.isRecord = true;
                    imageItem.thumbPath = scenesData.thumbUrl;
                    imageItem.path = scenesData.url;
                } else {
                    imageItem.path = scenesData.url;
                    imageItem.isRecord = false;
                }
                items.add(imageItem);
            }
            ImageItem imageItem = items.get(position);
            if (imageItem.isRecord) {
                Intent intent = new Intent();
                intent.setClass(mContext, VideoPlayActivity.class);
                intent.putExtra("path_record", (Serializable) imageItem);
                intent.putExtra("video_del", true);
                getView().startAC(intent);
            } else {
                //
                Intent intentPreview = new Intent(mContext, ImageAlarmPhotoDetailActivity.class);
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, items);
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                getView().startAC(intentPreview);
            }

        }

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mList.clear();
    }

    public void doContactOwner() {
        String tempNumber = null;
        outer:
        for (AlarmInfo.RecordInfo recordInfo : mList) {
            String type = recordInfo.getType();
            if ("sendVoice".equals(type)) {
                AlarmInfo.RecordInfo.Event[] phoneList = recordInfo.getPhoneList();
                for (AlarmInfo.RecordInfo.Event event : phoneList) {
                    String source = event.getSource();
                    String number = event.getNumber();
                    if (!TextUtils.isEmpty(number)) {
                        if ("attach".equals(source)) {
                            LogUtils.loge("单独联系人：" + number);
                            tempNumber = number;
                            break outer;

                        } else if ("group".equals(source)) {
                            LogUtils.loge("分组联系人：" + number);
                            tempNumber = number;
                            break;
                        } else if ("notification".equals(source)) {
                            LogUtils.loge("账户联系人：" + number);
                            tempNumber = number;
                            break;
                        }

                    }

                }
            }
        }
        if (TextUtils.isEmpty(tempNumber)) {
            getView().toastShort("未找到电话联系人");
        } else {
            AppUtils.diallPhone(tempNumber, mContext);
        }
    }

    public void doNavigation() {
        double[] deviceLonlat = deviceAlarmLogInfo.getDeviceLonlat();
        if (deviceLonlat != null && deviceLonlat.length > 1) {
            destPosition = new LatLng(deviceLonlat[1], deviceLonlat[0]);
            if (AppUtils.doNavigation(mContext, destPosition)) {
                return;
            }
        }
        getView().toastShort("未获取到位置信息");
    }

    @Override
    public void onPopupCallback(int statusResult, int statusType, int statusPlace, List<ScenesData> scenesDataList, String remark) {
        getView().setUpdateButtonClickable(false);
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.doUpdatePhotosUrl(deviceAlarmLogInfo.get_id(), statusResult, statusType,
                statusPlace,
                remark, isReConfirm, scenesDataList).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe
                        (new CityObserver<DeviceAlarmItemRsp>(this) {


                            @Override
                            public void onCompleted(DeviceAlarmItemRsp deviceAlarmItemRsp) {
                                if (deviceAlarmItemRsp.getErrcode() == ResponseBase.CODE_SUCCESS) {
                                    getView().toastShort(mContext.getResources().getString(R.string
                                            .tips_commit_success));
                                    deviceAlarmLogInfo = deviceAlarmItemRsp.getData();
                                    refreshData(false);
                                } else {
                                    getView().toastShort(mContext.getResources().getString(R.string
                                            .tips_commit_failed));
                                }
                                getView().dismissProgressDialog();
                                getView().dismissAlarmPopupView();
                            }

                            @Override
                            public void onErrorMsg(int errorCode, String errorMsg) {
                                getView().dismissProgressDialog();
                                getView().toastShort(errorMsg);
                                getView().setUpdateButtonClickable(true);
                            }
                        });
    }

    public void handlerActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO 对照片信息统一处理
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> tempImages = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (tempImages != null) {
                    boolean fromTakePhoto = data.getBooleanExtra(EXTRA_RESULT_BY_TAKE_PHOTO, false);
                    EventData eventData = new EventData();
                    eventData.code = EVENT_DATA_ALARM_POP_IMAGES;
                    AlarmPopModel alarmPopModel = new AlarmPopModel();
                    alarmPopModel.requestCode = requestCode;
                    alarmPopModel.resultCode = resultCode;
                    alarmPopModel.fromTakePhoto = fromTakePhoto;
                    alarmPopModel.imageItems = tempImages;
                    eventData.data = alarmPopModel;
                    EventBus.getDefault().post(eventData);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (requestCode == REQUEST_CODE_PREVIEW && data != null) {
                ArrayList<ImageItem> tempImages = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (tempImages != null) {
                    EventData eventData = new EventData();
                    eventData.code = EVENT_DATA_ALARM_POP_IMAGES;
                    AlarmPopModel alarmPopModel = new AlarmPopModel();
                    alarmPopModel.requestCode = requestCode;
                    alarmPopModel.resultCode = resultCode;
                    alarmPopModel.imageItems = tempImages;
                    eventData.data = alarmPopModel;
                    EventBus.getDefault().post(eventData);
                }
            }
        } else if (resultCode == RESULT_CODE_RECORD) {
            //拍视频
            if (data != null && requestCode == REQUEST_CODE_RECORD) {
                ImageItem imageItem = (ImageItem) data.getSerializableExtra("path_record");
                if (imageItem != null) {
                    LogUtils.loge("--- 从视频返回  path = " + imageItem.path);
                    ArrayList<ImageItem> tempImages = new ArrayList<>();
                    tempImages.add(imageItem);
                    EventData eventData = new EventData();
                    eventData.code = EVENT_DATA_ALARM_POP_IMAGES;
                    AlarmPopModel alarmPopModel = new AlarmPopModel();
                    alarmPopModel.requestCode = requestCode;
                    alarmPopModel.resultCode = resultCode;
                    alarmPopModel.imageItems = tempImages;
                    eventData.data = alarmPopModel;
                    EventBus.getDefault().post(eventData);
                }
            } else if (requestCode == REQUEST_CODE_PLAY_RECORD) {
                EventData eventData = new EventData();
                eventData.code = EVENT_DATA_ALARM_POP_IMAGES;
                AlarmPopModel alarmPopModel = new AlarmPopModel();
                alarmPopModel.requestCode = requestCode;
                alarmPopModel.resultCode = resultCode;
                eventData.data = alarmPopModel;
                EventBus.getDefault().post(eventData);
            }

        }
    }

    public void doAlarmHistory() {
        Intent intent = new Intent(mContext, AlarmHistoryLogActivity.class);
        intent.putExtra(EXTRA_SENSOR_SN, deviceAlarmLogInfo.getDeviceSN());
        getView().startAC(intent);
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }
}

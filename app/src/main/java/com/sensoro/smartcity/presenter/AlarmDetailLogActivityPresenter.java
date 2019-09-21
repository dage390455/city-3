package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.model.DeviceNotificationBean;
import com.sensoro.common.model.EventData;
import com.sensoro.common.model.ImageItem;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.AlarmCloudVideoBean;
import com.sensoro.common.server.bean.AlarmInfo;
import com.sensoro.common.server.bean.AlarmPopupDataBean;
import com.sensoro.common.server.bean.DeviceAlarmLogInfo;
import com.sensoro.common.server.bean.ForestFireCameraDetailInfo;
import com.sensoro.common.server.bean.ScenesData;
import com.sensoro.common.server.response.AlarmCountRsp;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.common.widgets.FireWaringCloseDialogUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.common.widgets.dialog.WarningContactDialogUtil;
import com.sensoro.forestfire.activity.AlarmForestFireCameraLiveDetailActivity;
import com.sensoro.forestfire.activity.AlarmForestFireCameraVideoDetailActivity;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.AlarmCameraLiveDetailActivity;
import com.sensoro.smartcity.activity.AlarmCameraVideoDetailActivity;
import com.sensoro.smartcity.activity.AlarmHistoryLogActivity;
import com.sensoro.smartcity.activity.VideoPlayActivity;
import com.sensoro.smartcity.analyzer.AlarmPopupConfigAnalyzer;
import com.sensoro.smartcity.imainviews.IAlarmDetailLogActivityView;
import com.sensoro.smartcity.model.AlarmPopupModel;
import com.sensoro.smartcity.model.EventAlarmStatusModel;
import com.sensoro.common.utils.CityAppUtils;
import com.sensoro.common.utils.WidgetUtil;
import com.sensoro.common.imagepicker.ImagePicker;
import com.sensoro.common.imagepicker.ui.ImageAlarmPhotoDetailActivity;
import com.sensoro.smartcity.widget.popup.AlarmPopUtils;
import com.shuyu.gsyvideoplayer.utils.NetworkUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AlarmDetailLogActivityPresenter extends BasePresenter<IAlarmDetailLogActivityView> implements IOnCreate, AlarmPopUtils.OnPopupCallbackListener {
    private DeviceAlarmLogInfo deviceAlarmLogInfo;
    private ForestFireCameraDetailInfo.ListBean mListBean;
    private final List<String>  mForestFireLiveList=new ArrayList<>();
    private boolean isReConfirm = false;
    private Activity mContext;
    private LatLng destPosition = null;
    private AlarmCloudVideoBean mVideoBean;
    private FireWaringCloseDialogUtils firewaringCloseDialogUtils;



    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        boolean needHideHistory = mContext.getIntent().getBooleanExtra(Constants.EXTRA_ALARM_HISTORY_VISIBLE, false);
        if (needHideHistory) {
            getView().setHistoryLogVisible(false);
        } else {
            getView().setHistoryLogVisible(true);
        }
        deviceAlarmLogInfo = (DeviceAlarmLogInfo) mContext.getIntent().getSerializableExtra(Constants.EXTRA_ALARM_INFO);

        getAlarmCount();

        getCloudVideo();

        getForestFireCameraLive();


        refreshData(true);

    }

    private void getCloudVideo() {

        if (!PreferencesHelper.getInstance().getUserData().hasDeviceCameraList) {
            getView().setLlVideoSizeAndContent(-1, null);
            return;
        }
        String[] eventIds = {deviceAlarmLogInfo.get_id()};
        RetrofitServiceHelper.getInstance().getCloudVideo(eventIds)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<ResponseResult<List<AlarmCloudVideoBean>>>(this) {
                    @Override
                    public void onCompleted(ResponseResult<List<AlarmCloudVideoBean>> response) {
                        List<AlarmCloudVideoBean> data = response.getData();
                        if (data != null && data.size() > 0) {
                            mVideoBean = data.get(0);
                            List<AlarmCloudVideoBean.MediasBean> mMedias = mVideoBean.getMedias();
                            if (mMedias != null && mMedias.size() > 0) {
                                String text = String.format(Locale.ROOT, "%s%d%s", mContext.getString(R.string.alarm_camera_video)
                                        , mMedias.size(), mContext.getString(R.string.video_unit_duan));
                                getView().setLlVideoSizeAndContent(mMedias.size(), text);
                            } else {
                                getView().setLlVideoSizeAndContent(-1, null);
                            }
                        } else {
                            getView().setLlVideoSizeAndContent(-1, null);
                        }
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().setLlVideoSizeAndContent(-1, null);
                    }
                });
    }


    private void getForestFireCameraLive() {

        if (!PreferencesHelper.getInstance().getUserData().hasDeviceCameraList||!Constants.FOREST_FIRE_DEVICE_TYPE.equals(deviceAlarmLogInfo.getDeviceType())) {
            getView().setLlVideoSizeAndContent(-1, null);
            return;
        }

        String testsn="72057600540672047";
//        deviceAlarmLogInfo.getDeviceSN()
        RetrofitServiceHelper.getInstance().getForestFireDeviceCameraDetail(testsn).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<ForestFireCameraDetailInfo>>(this) {
            @Override
            public void onCompleted(ResponseResult<ForestFireCameraDetailInfo> deviceCameraDetailRsp) {

                ForestFireCameraDetailInfo  mForestFireCameraDetailInfo= deviceCameraDetailRsp.getData();
                if(mForestFireCameraDetailInfo!=null&&mForestFireCameraDetailInfo.getList()!=null&&mForestFireCameraDetailInfo.getList().size()>0){
                    mListBean= mForestFireCameraDetailInfo.getList().get(0);
                    mForestFireLiveList.clear();
                    if(mListBean.getCamera()!=null){//说明是单目的
                        mForestFireLiveList.add(mListBean.getHls());
                    }else if(mListBean.getMultiVideoInfo()!=null&&mListBean.getMultiVideoInfo().size()>0){//说明是多目的
                        for(ForestFireCameraDetailInfo.MultiVideoInfoBean item:mListBean.getMultiVideoInfo()){
                            mForestFireLiveList.add(item.getHls());
                        }
                    }
                }

                getView().setCameraLiveCount(mForestFireLiveList);

            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().setCameraLiveCount(null);
            }
        });
    }

    public void doBack() {
        EventData eventData = new EventData();
        eventData.code = Constants.EVENT_DATA_ALARM_DETAIL_RESULT;
        eventData.data = deviceAlarmLogInfo;
        EventBus.getDefault().post(eventData);
        if (isAttachedView()) {
            getView().finishAc();
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(EventAlarmStatusModel eventAlarmStatusModel) {
        if (deviceAlarmLogInfo.get_id().equals(eventAlarmStatusModel.deviceAlarmLogInfo.get_id())) {
            switch (eventAlarmStatusModel.status) {
                case Constants.MODEL_ALARM_STATUS_EVENT_CODE_RECOVERY:
                    // 做一些预警恢复的逻辑
                case Constants.MODEL_ALARM_STATUS_EVENT_CODE_CONFIRM:
                    // 做一些预警被确认的逻辑
                case Constants.MODEL_ALARM_STATUS_EVENT_CODE_RECONFIRM:
                    // 做一些预警被再次确认的逻辑
                    deviceAlarmLogInfo = eventAlarmStatusModel.deviceAlarmLogInfo;
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isAttachedView()) {
                                refreshData(false);
                            }

                        }
                    });
                    break;
                default:
                    // 未知逻辑 可以联系我确认 有可能是bug
                    break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        //
        switch (code) {
            case Constants.EVENT_DATA_ALARM_FRESH_ALARM_DATA:
                if (data instanceof DeviceAlarmLogInfo) {
                    if (this.deviceAlarmLogInfo.get_id().equals(((DeviceAlarmLogInfo) data).get_id())) {
                        this.deviceAlarmLogInfo = (DeviceAlarmLogInfo) data;
                        refreshData(false);
                    }

                }
                break;
        }
    }

    private void refreshData(boolean isInit) {
        //
        String deviceName = deviceAlarmLogInfo.getDeviceName();
        String deviceSN = deviceAlarmLogInfo.getDeviceSN();
        if (isAttachedView()) {
            getView().setDeviceNameTextView(TextUtils.isEmpty(deviceName) ? deviceSN : deviceName);
        }
        if (TextUtils.isEmpty(deviceSN)) {
            deviceSN = mContext.getString(R.string.device_number) + mContext.getString(R.string.unknown);
        } else {
            deviceSN = mContext.getString(R.string.device_number) + deviceSN;
        }
        if (isAttachedView()) {
            getView().setDeviceSn(deviceSN);
        }

        //TODO 如果是森林火灾，直播入口显示逻辑需要根据直播实时详情接口返回的结果判断
        if (PreferencesHelper.getInstance().getUserData().hasDeviceCameraList) {
            if(Constants.FOREST_FIRE_DEVICE_TYPE.equals(deviceAlarmLogInfo.getDeviceType())){
                getView().setCameraLiveCount(mForestFireLiveList);
            }else{
                getView().setCameraLiveCount(deviceAlarmLogInfo.getCameras());
            }
        } else {
            getView().setCameraLiveCount(null);
        }

//        if (PreferencesHelper.getInstance().getUserData().hasDeviceCameraList) {
//            getView().setCameraLiveCount(deviceAlarmLogInfo.getCameras());
//        } else {
//            getView().setCameraLiveCount(null);
//        }


        long createdTime = deviceAlarmLogInfo.getCreatedTime();
        String alarmTime = DateUtil.getStrTimeToday(mContext, createdTime, 1);
        if (isInit) {
            if (isAttachedView()) {
                getView().showProgressDialog();
            }
        }
        if (isAttachedView()) {
            getView().updateAlertLogContentAdapter(deviceAlarmLogInfo);
        }
//        getView().setDisplayStatus(deviceAlarmLogInfo.getDisplayStatus());
//        getView().setSensoroIv(deviceAlarmLogInfo.getSensorType());
        AlarmInfo.RecordInfo[] recordInfoArray = deviceAlarmLogInfo.getRecords();
        if (recordInfoArray != null) {
            //
            int displayStatus = deviceAlarmLogInfo.getDisplayStatus();
            switch (displayStatus) {
                case Constants.DISPLAY_STATUS_CONFIRM:
                    isReConfirm = false;
                    if (isAttachedView()) {
                        getView().setConfirmColor(mContext.getResources().getColor(R.color.white));
                        getView().setConfirmBg(R.drawable.shape_btn_corner_29c_bg_4dp);
                        getView().setConfirmText(mContext.getString(R.string.alarm_log_alarm_warn_confirm));
                    }
                    break;
                case Constants.DISPLAY_STATUS_ALARM:
                case Constants.DISPLAY_STATUS_MIS_DESCRIPTION:
                case Constants.DISPLAY_STATUS_TEST:
                case Constants.DISPLAY_STATUS_RISKS:
                    isReConfirm = true;
                    if (isAttachedView()) {
                        getView().setConfirmColor(mContext.getResources().getColor(R.color.c_252525));
                        getView().setConfirmBg(R.drawable.shape_bg_solid_fa_stroke_df_corner_4dp);
                        getView().setConfirmText(mContext.getString(R.string.confirming_again));
                    }
                    break;
            }
            boolean isAlarm = false;
            for (AlarmInfo.RecordInfo recordInfo : recordInfoArray) {
                if ("recovery".equals(recordInfo.getType())) {
                    isAlarm = false;
                } else {
                    isAlarm = true;
                }
            }
            boolean needShowCloseFire = false;
            if (Constants.FOREST_FIRE_DEVICE_TYPE.equals(deviceAlarmLogInfo.getDeviceType()) && isAlarm) {
                if (Constants.DISPLAY_STATUS_ALARM == displayStatus || Constants.DISPLAY_STATUS_RISKS == displayStatus) {
                    needShowCloseFire = true;
                }
            }
            //TODO 是否显示关闭火警
            if (isAttachedView()) {
                getView().setCurrentAlarmState(alarmTime);
                getView().setCloseWarnVisible(needShowCloseFire);
            }

        }


    }

    private void getAlarmCount() {
        long current = System.currentTimeMillis();
        RetrofitServiceHelper.getInstance().getAlarmCount(current - 3600 * 24 * 180 * 1000L, current,
                null, deviceAlarmLogInfo.getDeviceSN())
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<AlarmCountRsp>(this) {
                    @Override
                    public void onCompleted(AlarmCountRsp alarmCountRsp) {
                        int count = alarmCountRsp.getCount();
                        if (isAttachedView()) {
                            getView().setAlarmCount(count + "");
                            getView().dismissProgressDialog();
                        }
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        if (isAttachedView()) {
                            getView().dismissProgressDialog();
                            getView().toastShort(errorMsg);
                        }
                    }
                });
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
                intent.putExtra(Constants.EXTRA_PATH_RECORD, (Serializable) imageItem);
                intent.putExtra(Constants.EXTRA_VIDEO_DEL, true);
                if (isAttachedView()) {
                    getView().startAC(intent);
                }
            } else {
                //
                Intent intentPreview = new Intent(mContext, ImageAlarmPhotoDetailActivity.class);
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, items);
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                if (isAttachedView()) {
                    getView().startAC(intentPreview);
                }
            }

        }

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    public void doContactOwner() {

        List<DeviceNotificationBean> deviceNotifications = WidgetUtil.handleDeviceNotifications(deviceAlarmLogInfo.getDeviceNotifications());
        if (deviceNotifications.isEmpty()) {
            getView().toastShort(mContext.getString(R.string.no_find_contact_phone_number));
        } else {
            if (deviceNotifications.size() > 1) {
                WarningContactDialogUtil dialogUtil = new WarningContactDialogUtil(mContext);
                dialogUtil.show(deviceNotifications);
            } else {
                DeviceNotificationBean deviceNotificationBean = deviceNotifications.get(0);
                String content = deviceNotificationBean.getContent();
                AppUtils.diallPhone(content, mContext);
            }
        }
    }

    public void doNavigation() {
        double[] deviceLonlat = deviceAlarmLogInfo.getDeviceLonlat();
        if (deviceLonlat != null && deviceLonlat.length > 1) {
            destPosition = new LatLng(deviceLonlat[1], deviceLonlat[0]);
            if (CityAppUtils.doNavigation(mContext, destPosition)) {
                return;
            } else {
                if (isAttachedView()) {
                    getView().toastShort(mContext.getString(R.string.location_not_obtained));
                }
            }
        } else {
            if (isAttachedView()) {
                getView().toastShort(mContext.getString(R.string.location_not_obtained));
            }
        }

    }


    public void doAlarmHistory() {
        Intent intent = new Intent(mContext, AlarmHistoryLogActivity.class);
        intent.putExtra(Constants.EXTRA_SENSOR_SN, deviceAlarmLogInfo.getDeviceSN());
        if (isAttachedView()) {
            getView().startAC(intent);
        }
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPopupCallback(AlarmPopupModel alarmPopupModel, List<ScenesData> scenesDataList) {
        if (isAttachedView()) {
            getView().setUpdateButtonClickable(false);
            getView().showProgressDialog();
        }
        Map<String, Integer> alarmPopupServerData = AlarmPopupConfigAnalyzer.createAlarmPopupServerData(alarmPopupModel);
        RetrofitServiceHelper.getInstance().doUpdatePhotosUrl(deviceAlarmLogInfo.get_id(), alarmPopupServerData, alarmPopupModel.securityRisksList,
                alarmPopupModel.mRemark, isReConfirm, scenesDataList).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<ResponseResult<DeviceAlarmLogInfo>>(this) {

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        if (isAttachedView()) {
                            getView().dismissProgressDialog();
                            getView().toastShort(errorMsg);
                            getView().setUpdateButtonClickable(true);
                        }
                    }

                    @Override
                    public void onCompleted(ResponseResult<DeviceAlarmLogInfo> deviceAlarmItemRsp) {
                        if (deviceAlarmItemRsp.getErrcode() == ResponseResult.CODE_SUCCESS) {
                            if (isAttachedView()) {
                                getView().toastShort(mContext.getResources().getString(R.string
                                        .tips_commit_success));
                            }
                            deviceAlarmLogInfo = deviceAlarmItemRsp.getData();
                            refreshData(false);
                        } else {
                            if (isAttachedView()) {
                                getView().toastShort(mContext.getResources().getString(R.string
                                        .tips_commit_failed));
                            }
                        }
                        if (isAttachedView()) {
                            getView().dismissProgressDialog();
                            getView().dismissAlarmPopupView();
                        }
                    }
                });
    }

    public void showAlarmPopupView() {
        if (PreferencesHelper.getInstance().getAlarmPopupDataBeanCache() == null) {
            RetrofitServiceHelper.getInstance().getDevicesAlarmPopupConfig().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<AlarmPopupDataBean>>(this) {
                @Override
                public void onCompleted(ResponseResult<AlarmPopupDataBean> devicesAlarmPopupConfigRsp) {
                    PreferencesHelper.getInstance().saveAlarmPopupDataBeanCache(devicesAlarmPopupConfigRsp.getData());
                    final AlarmPopupModel alarmPopupModel = new AlarmPopupModel();
                    String deviceName = deviceAlarmLogInfo.getDeviceName();
                    if (TextUtils.isEmpty(deviceName)) {
                        alarmPopupModel.title = deviceAlarmLogInfo.getDeviceSN();
                    } else {
                        alarmPopupModel.title = deviceName;
                    }
                    alarmPopupModel.alarmStatus = deviceAlarmLogInfo.getAlarmStatus();
                    alarmPopupModel.updateTime = deviceAlarmLogInfo.getUpdatedTime();
                    alarmPopupModel.mergeType = WidgetUtil.handleMergeType(deviceAlarmLogInfo.getDeviceType());
                    alarmPopupModel.sensorType = deviceAlarmLogInfo.getSensorType();
                    //
                    AlarmPopupConfigAnalyzer.handleAlarmPopupModel(null, alarmPopupModel);
                    getView().showAlarmPopupView(alarmPopupModel);
                    getView().dismissProgressDialog();

                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    getView().toastShort(errorMsg);
                    getView().dismissProgressDialog();
                }
            });
        } else {
            final AlarmPopupModel alarmPopupModel = new AlarmPopupModel();
            String deviceName = deviceAlarmLogInfo.getDeviceName();
            if (TextUtils.isEmpty(deviceName)) {
                alarmPopupModel.title = deviceAlarmLogInfo.getDeviceSN();
            } else {
                alarmPopupModel.title = deviceName;
            }
            alarmPopupModel.alarmStatus = deviceAlarmLogInfo.getAlarmStatus();
            alarmPopupModel.updateTime = deviceAlarmLogInfo.getUpdatedTime();
            alarmPopupModel.mergeType = WidgetUtil.handleMergeType(deviceAlarmLogInfo.getDeviceType());
            alarmPopupModel.sensorType = deviceAlarmLogInfo.getSensorType();
            //
            AlarmPopupConfigAnalyzer.handleAlarmPopupModel(null, alarmPopupModel);
            getView().showAlarmPopupView(alarmPopupModel);
        }
    }


    public void doCameraVideo() {
        if ((!NetworkUtils.isAvailable(mContext))) {
            SensoroToast.getInstance().makeText(mContext.getResources().getString(R.string.disconnected_from_network), Toast.LENGTH_SHORT).show();

            return;
        }

        if (Constants.FOREST_FIRE_DEVICE_TYPE.equals(deviceAlarmLogInfo.getDeviceType())) {
            Intent intent = new Intent(mContext, AlarmForestFireCameraVideoDetailActivity.class);
            intent.putExtra(Constants.EXTRA_ALARM_CAMERA_VIDEO, mVideoBean);
            getView().startAC(intent);
        } else {
            Intent intent = new Intent(mContext, AlarmCameraVideoDetailActivity.class);
            intent.putExtra(Constants.EXTRA_ALARM_CAMERA_VIDEO, mVideoBean);
            getView().startAC(intent);
        }

    }

    public void doCameraLive() {
        if ((!NetworkUtils.isAvailable(mContext))) {
            SensoroToast.getInstance().makeText(mContext.getResources().getString(R.string.disconnected_from_network), Toast.LENGTH_SHORT).show();
            return;
        }

        if (Constants.FOREST_FIRE_DEVICE_TYPE.equals(deviceAlarmLogInfo.getDeviceType())) {
            Intent intent = new Intent(mContext, AlarmForestFireCameraLiveDetailActivity.class);
            intent.putExtra(Constants.EXTRA_ALARM_FOREST_FIRE_CAMERAS, mListBean);
            getView().startAC(intent);
        } else {
            Intent intent = new Intent(mContext, AlarmCameraLiveDetailActivity.class);
            ArrayList<String> cameras = new ArrayList<>(deviceAlarmLogInfo.getCameras());
            intent.putExtra(Constants.EXTRA_ALARM_CAMERAS, cameras);
            getView().startAC(intent);
        }

    }

    public void doCloseWarn() {
        //弹窗二次确认
        //TODO 调用关闭火警 然后刷新界面
        if (firewaringCloseDialogUtils == null) {
            firewaringCloseDialogUtils = new FireWaringCloseDialogUtils(mContext);
        }
        firewaringCloseDialogUtils.setTipTitleText(mContext.getString(R.string.confirm_to_turn_off_the_fire))
                .setTipMessageText(mContext.getString(R.string.turn_off_the_fire_tips))
                .setTipConfirmText(mContext.getString(R.string.confirm_close), mContext.getResources().getColor(R.color.c_f35a58))
                .setTipCacnleText(mContext.getString(R.string.cancel), mContext.getResources().getColor(R.color.c_252525))
                .setTipDialogUtilsClickListener(new FireWaringCloseDialogUtils.TipDialogUtilsClickListener() {
                    @Override
                    public void onCancelClick() {
                        firewaringCloseDialogUtils.dismiss();
                    }

                    @Override
                    public void onConfirmClick() {
                        getView().showProgressDialog();
                        RetrofitServiceHelper.getInstance().doCloseFireWarn(deviceAlarmLogInfo.getDeviceSN()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<Object>>(AlarmDetailLogActivityPresenter.this) {
                            @Override
                            public void onCompleted(ResponseResult<Object> objectResponseResult) {
                                getView().toastShort("success");
                                getView().dismissProgressDialog();
                            }

                            @Override
                            public void onErrorMsg(int errorCode, String errorMsg) {
                                getView().toastShort(errorMsg);
                                getView().dismissProgressDialog();
                            }
                        });
                    }
                });
        firewaringCloseDialogUtils.show();
    }
}

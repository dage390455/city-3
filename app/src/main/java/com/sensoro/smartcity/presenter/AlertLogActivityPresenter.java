package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageAlarmPhotoDetailActivity;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.VideoPlayActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IAlertLogActivityView;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.server.response.DeviceAlarmItemRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.popup.AlarmPopUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.sensoro.smartcity.util.AppUtils.isAppInstalled;

public class AlertLogActivityPresenter extends BasePresenter<IAlertLogActivityView> implements Constants, AlarmPopUtils.OnPopupCallbackListener {
    private final List<AlarmInfo.RecordInfo> mList = new ArrayList<>();
    private DeviceAlarmLogInfo deviceAlarmLogInfo;
    private boolean isReConfirm = false;
    private Activity mContext;
    private LatLng destPosition = null;
    private LatLng startPosition = null;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        deviceAlarmLogInfo = (DeviceAlarmLogInfo) mContext.getIntent().getSerializableExtra(EXTRA_ALARM_INFO);
        isReConfirm = mContext.getIntent().getBooleanExtra(EXTRA_ALARM_IS_RE_CONFIRM, false);
        refreshData();
    }

    public void doBack() {
        EventData eventData = new EventData();
        eventData.code = EVENT_DATA_ALARM_DETAIL_RESULT;
        eventData.data = deviceAlarmLogInfo;
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }

    public void refreshData() {
        //
        String deviceName = deviceAlarmLogInfo.getDeviceName();
        getView().setDeviceNameTextView(TextUtils.isEmpty(deviceName) ? deviceAlarmLogInfo.getDeviceSN() : deviceName);
        getView().setCurrentAlarmState(deviceAlarmLogInfo.getDisplayStatus(), DateUtil.getFullParseDate(deviceAlarmLogInfo.getUpdatedTime()));
        //TODO 半年累计报警次数
        getView().setAlarmCount(deviceAlarmLogInfo.getDisplayStatus() + 10 + "");
//        getView().setDisplayStatus(deviceAlarmLogInfo.getDisplayStatus());
//        getView().setSensoroIv(deviceAlarmLogInfo.getSensorType());
        AlarmInfo.RecordInfo[] recordInfoArray = deviceAlarmLogInfo.getRecords();
        if (recordInfoArray != null) {
            mList.clear();
            for (int i = recordInfoArray.length - 1; i >= 0; i--) {
                mList.add(recordInfoArray[i]);
            }
            for (AlarmInfo.RecordInfo recordInfo : recordInfoArray) {
                if (recordInfo.getType().equals("recovery")) {
//                    getView().setStatusInfo("于" + DateUtil.getFullParseDate(recordInfo.getUpdatedTime()) + "恢复正常", R
//                            .color.sensoro_normal, R.drawable.shape_status_normal);
//                    break;
                } else {
//                    getView().setStatusInfo(mContext.getResources().getString(R.string.alarming), R.color.sensoro_alarm,
//                            R.drawable.shape_status_alarm);
                }
            }
        }
        getView().updateAlertLogContentAdapter(mList);
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
                    imageItem.recordPath = scenesData.url;
                    imageItem.path = scenesData.thumbUrl;
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
        AlarmInfo.RecordInfo[] records = deviceAlarmLogInfo.getRecords();
        if (records != null && records.length > 0) {
            for (AlarmInfo.RecordInfo recordInfo : records) {
                double[] deviceLonlat = recordInfo.getDeviceLonlat();
                if (deviceLonlat != null && deviceLonlat.length > 1) {
                    destPosition = new LatLng(deviceLonlat[1], deviceLonlat[0]);
                    AMapLocation lastKnownLocation = SensoroCityApplication.getInstance().mLocationClient.getLastKnownLocation();
                    if (lastKnownLocation != null) {
                        double lat = lastKnownLocation.getLatitude();//获取纬度
                        double lon = lastKnownLocation.getLongitude();//获取经度
                        LatLng startPosition = new LatLng(lat, lon);
                        if (isAppInstalled(mContext, "com.autonavi.minimap")) {
                            openGaoDeMap(startPosition);
                        } else if (isAppInstalled(mContext, "com.baidu.BaiduMap")) {
                            openBaiDuMap(startPosition);
                        } else {
                            openOther(startPosition);
                        }
                        return;
                    }
                }
            }
        }
        getView().toastShort("未获取到位置信息");
    }


    private void openGaoDeMap(LatLng startPosition) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        Uri uri = Uri.parse("amapuri://route/plan/?sid=BGVIS1&slat=" + startPosition.latitude + "&slon=" +
                startPosition.longitude + "&sname=当前位置" + "&did=BGVIS2&dlat=" + destPosition.latitude + "&dlon=" +
                destPosition.longitude +
                "&dname=设备部署位置" + "&dev=0&t=0");
        intent.setData(uri);
        //启动该页面即可
        getView().startAC(intent);
    }

    private void openBaiDuMap(LatLng startPosition) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("baidumap://map/direction?origin=name:当前位置|latlng:" + startPosition.latitude + "," +
                startPosition.longitude +
                "&destination=name:设备部署位置|latlng:" + destPosition.latitude + "," + destPosition.longitude +
                "&mode=driving&coord_type=gcj02"));
        getView().startAC(intent);
    }

    private void openOther(LatLng startPosition) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        String url = "http://uri.amap.com/navigation?from=" + startPosition.longitude + "," + startPosition.latitude
                + ",当前位置" +
                "&to=" + destPosition.longitude + "," + destPosition.latitude + "," +
                "设备部署位置&mode=car&policy=1&src=mypage&coordinate=gaode&callnative=0";
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        getView().startAC(intent);
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
                                    refreshData();
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
}

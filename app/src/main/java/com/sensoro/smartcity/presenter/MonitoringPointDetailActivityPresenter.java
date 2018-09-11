package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.MonitoringPointDetailActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IMonitoringPointDetailActivityView;
import com.sensoro.smartcity.iwidget.IOnStart;
import com.sensoro.smartcity.model.PushData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.bean.DeviceRecentInfo;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.DeviceRecentRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.sensoro.smartcity.util.AppUtils.isAppInstalled;

public class MonitoringPointDetailActivityPresenter extends BasePresenter<IMonitoringPointDetailActivityView> implements IOnStart, Constants, AMapLocationListener, GeocodeSearch.OnGeocodeSearchListener {
    private Activity mContext;
    private DeviceInfo mDeviceInfo;

    private LatLng startPosition = null;
    private LatLng destPosition = null;
    private AMapLocationClient mLocationClient;
    private final List<DeviceRecentInfo> mRecentInfoList = new ArrayList<>();
    private GeocodeSearch geocoderSearch;
    private int textColor;
    private String tempAddress = "未知街道";

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        mDeviceInfo = (DeviceInfo) mContext.getIntent().getSerializableExtra(EXTRA_DEVICE_INFO);
        locate();
        freshDestPosition();
        requestDeviceRecentLog();
    }

    private void freshTopData() {
        switch (mDeviceInfo.getStatus()) {
            case SENSOR_STATUS_ALARM:
//                getView().setStatusImageView(R.drawable.shape_status_alarm);
                textColor = mContext.getResources().getColor(R.color.sensoro_alarm);
                break;
            case SENSOR_STATUS_INACTIVE:
//                getView().setStatusImageView(R.drawable.shape_status_inactive);
                textColor = mContext.getResources().getColor(R.color.sensoro_inactive);
                break;
            case SENSOR_STATUS_LOST:
//                getView().setStatusImageView(R.drawable.shape_status_lost);
                textColor = mContext.getResources().getColor(R.color.sensoro_lost);
                break;
            case SENSOR_STATUS_NORMAL:
//                getView().setStatusImageView(R.drawable.shape_status_normal);
                textColor = mContext.getResources().getColor(R.color.sensoro_normal);
                break;
            default:
                break;
        }
        getView().setAlarmStateColor(textColor);
        String name = mDeviceInfo.getName();
        String sn = mDeviceInfo.getSn();
        //TODO 显示sn还是姓名等
        getView().setTitleNameTextView(TextUtils.isEmpty(name) ? mContext.getResources().getString(R
                .string.unname) : name);
        mDeviceInfo.getSensorTypes();
        String contact = mDeviceInfo.getContact();
        String address = mDeviceInfo.getAddress();
        String content = mDeviceInfo.getContent();
        if (TextUtils.isEmpty(contact)) {
            contact = "未设定";
        }
        if (TextUtils.isEmpty(content)) {
            content = "未设定";
        }
        getView().setDeviceLocation(this.tempAddress);
        getView().setContractName(contact);
        getView().setContractPhone(content);
        getView().setUpdateTime(DateUtil.getFullParseDate(mDeviceInfo.getUpdatedTime()));
    }

    private void freshDestPosition() {
        double[] lonlat = mDeviceInfo.getLonlat();
        if (mDeviceInfo.getSensorTypes().length > 0) {
            destPosition = new LatLng(lonlat[1], lonlat[0]);
            RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(mDeviceInfo.getLonlat()[1], mDeviceInfo
                    .getLonlat()[0]), 200, GeocodeSearch.AMAP);
            geocoderSearch.getFromLocationAsyn(query);
        }
    }

    private void requestDeviceRecentLog() {
        long endTime = mDeviceInfo.getUpdatedTime();
        long startTime = endTime - 2 * 1000 * 60 * 60 * 24;
        String sn = mDeviceInfo.getSn();
        getView().showProgressDialog();
        //合并请求
        Observable<DeviceInfoListRsp> deviceDetailInfoList = RetrofitServiceHelper.INSTANCE.getDeviceDetailInfoList
                (sn, null, 1);
        Observable<DeviceRecentRsp> deviceHistoryList = RetrofitServiceHelper.INSTANCE.getDeviceHistoryList(sn,
                startTime, endTime);
        Observable.merge(deviceDetailInfoList, deviceHistoryList).subscribeOn(Schedulers.io()).doOnNext(new Action1<ResponseBase>() {


            @Override
            public void call(ResponseBase responseBase) {
                if (responseBase instanceof DeviceInfoListRsp) {
                    DeviceInfoListRsp response = (DeviceInfoListRsp) responseBase;
                    if (response.getData().size() > 0) {
                        DeviceInfo deviceInfo = response.getData().get(0);
                        String[] tags = deviceInfo.getTags();
                        mDeviceInfo.setTags(tags);
                    }
                } else if (responseBase instanceof DeviceRecentRsp) {
                    DeviceRecentRsp response = ((DeviceRecentRsp) responseBase);
                    String data = response.getData().toString();
                    try {
//                        String[] sensorTypes = response.getSensorTypes();
                        JSONObject jsonObject = new JSONObject(data);
//                        sensorTypesList = SortUtils.sortSensorTypes(sensorTypes);
                        Iterator<String> iterator = jsonObject.keys();
                        while (iterator.hasNext()) {
                            String keyStr = iterator.next();
                            if (!TextUtils.isEmpty(keyStr)) {
                                JSONObject object = jsonObject.getJSONObject(keyStr);
                                if (object != null) {
                                    DeviceRecentInfo recentInfo = RetrofitServiceHelper.INSTANCE.getGson().fromJson
                                            (object.toString(),
                                                    DeviceRecentInfo.class);
                                    recentInfo.setDate(keyStr);
                                    mRecentInfoList.add(recentInfo);
                                }

                            }
                        }
                        Collections.sort(mRecentInfoList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseBase>() {


            @Override
            public void onCompleted() {
                getView().dismissProgressDialog();
            }

            @Override
            public void onNext(ResponseBase responseBase) {
//                getView().setMapLayoutVisible(true);
//                getView().setMapViewVisible(true);
//                refreshBatteryLayout();
//                refreshKLayout();
                freshTopData();
                getView().updateDeviceInfoAdapter(mDeviceInfo);
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }

    private void locate() {
        geocoderSearch = new GeocodeSearch(mContext);
        geocoderSearch.setOnGeocodeSearchListener(this);
        mLocationClient = new AMapLocationClient(mContext);
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        mLocationOption.setOnceLocationLatest(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(PushData data) {
        if (data != null) {
            List<DeviceInfo> deviceInfoList = data.getDeviceInfoList();
            String sn = mDeviceInfo.getSn();
            for (DeviceInfo deviceInfo : deviceInfoList) {
                if (sn.equals(deviceInfo.getSn())) {
                    mDeviceInfo = deviceInfo;
                    break;
                }
            }
            if (mDeviceInfo != null && AppUtils.isActivityTop(mContext, MonitoringPointDetailActivity.class)) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        freshTopData();
                        getView().updateDeviceInfoAdapter(mDeviceInfo);
//                        freshStructData();
//                        freshMarker();
                    }
                });


            }
        }
    }

    @Override
    public void onDestroy() {
        mRecentInfoList.clear();
//        if (tempUpBitmap != null) {
//            tempUpBitmap.recycle();
//            tempUpBitmap = null;
//        }
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
//        if (sensorTypesList != null) {
//            sensorTypesList.clear();
//            sensorTypesList = null;
//        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            double lat = aMapLocation.getLatitude();//获取纬度
            double lon = aMapLocation.getLongitude();//获取经度
            startPosition = new LatLng(lat, lon);
            System.out.println("lat===>" + lat);
            System.out.println("lon===>" + lon);
        } else {
            //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
            Log.e("地图错误", "定位失败, 错误码:" + aMapLocation.getErrorCode() + ", 错误信息:"
                    + aMapLocation.getErrorInfo());
        }
    }

    public void doNavigation() {
        if (startPosition == null) {
            getView().toastShort("定位失败，请重试");
            return;
        }
        if (isAppInstalled(mContext, "com.autonavi.minimap")) {
            openGaoDeMap();
        } else if (isAppInstalled(mContext, "com.baidu.BaiduMap")) {
            openBaiDuMap();
        } else {
            openOther();
        }
    }

    private void openGaoDeMap() {

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

    private void openBaiDuMap() {
        Intent intent = new Intent();
        intent.setData(Uri.parse("baidumap://map/direction?origin=name:当前位置|latlng:" + startPosition.latitude + "," +
                startPosition.longitude +
                "&destination=name:设备部署位置|latlng:" + destPosition.latitude + "," + destPosition.longitude +
                "&mode=driving&coord_type=gcj02"));
        getView().startAC(intent);
    }

    private void openOther() {
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
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        String formatAddress = regeocodeResult.getRegeocodeAddress().getFormatAddress();
        LogUtils.loge(this, "onRegeocodeSearched: " + "code = " + i + ",address = " + formatAddress);
        tempAddress = formatAddress;
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        LogUtils.loge(this, "onGeocodeSearched: " + "onGeocodeSearched");
    }
}

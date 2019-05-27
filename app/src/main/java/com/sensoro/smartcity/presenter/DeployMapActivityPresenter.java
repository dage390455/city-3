package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.geocoder.RegeocodeRoad;
import com.amap.api.services.geocoder.StreetNumber;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.DeviceInfo;
import com.sensoro.common.server.response.BaseStationDetailRsp;
import com.sensoro.common.server.response.DeployDeviceDetailRsp;
import com.sensoro.common.server.response.DeviceDeployRsp;
import com.sensoro.common.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployMapActivityView;
import com.sensoro.smartcity.model.DeployAnalyzerModel;
import com.sensoro.smartcity.util.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DeployMapActivityPresenter extends BasePresenter<IDeployMapActivityView> implements IOnCreate, AMap.OnCameraChangeListener, AMap.OnMarkerClickListener, AMap.OnMapLoadedListener, AMap.OnMapTouchListener, AMap.InfoWindowAdapter, GeocodeSearch.OnGeocodeSearchListener, Constants {
    private AMap aMap;
    private Marker deviceMarker;
    private GeocodeSearch geocoderSearch;
    private Activity mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private DeployAnalyzerModel deployAnalyzerModel;
    private Marker locationMarker;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        geocoderSearch = new GeocodeSearch(mContext);
        geocoderSearch.setOnGeocodeSearchListener(this);
        deployAnalyzerModel = (DeployAnalyzerModel) mContext.getIntent().getSerializableExtra(EXTRA_DEPLOY_ANALYZER_MODEL);
        switch (deployAnalyzerModel.deployType) {
            case TYPE_SCAN_DEPLOY_STATION:
                //基站部署
                getView().setSignalVisible(false);
                break;
            case TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
                //巡检设备更换
            case TYPE_SCAN_DEPLOY_DEVICE:
                //设备部署
                getView().setSignalVisible(false);
                getView().refreshSignal(deployAnalyzerModel.updatedTime, deployAnalyzerModel.signal);
                break;
            case TYPE_SCAN_LOGIN:
                break;
            case TYPE_SCAN_INSPECTION:
                //扫描巡检设备
                break;
            case TYPE_SCAN_DEPLOY_POINT_DISPLAY:
                //回显地图数据
                getView().setSaveVisible(false);
                getView().refreshSignal(deployAnalyzerModel.signal);
                getView().setSubtitleVisible(false);
                break;
            default:
                break;
        }
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
    }


    private void setMarkerAddress(RegeocodeAddress regeocodeAddress) {
        StringBuilder stringBuilder = new StringBuilder();
        //
        String province = regeocodeAddress.getProvince();
        String city = regeocodeAddress.getCity();
        //
        String district = regeocodeAddress.getDistrict();// 区或县或县级市
        //
        //
        String township = regeocodeAddress.getTownship();// 乡镇
        //
        String streetName = null;// 道路
        List<RegeocodeRoad> regeocodeRoads = regeocodeAddress.getRoads();// 道路列表
        if (regeocodeRoads != null && regeocodeRoads.size() > 0) {
            RegeocodeRoad regeocodeRoad = regeocodeRoads.get(0);
            if (regeocodeRoad != null) {
                streetName = regeocodeRoad.getName();
            }
        }
        //
        String streetNumber = null;// 门牌号
        StreetNumber number = regeocodeAddress.getStreetNumber();
        if (number != null) {
            String street = number.getStreet();
            if (street != null) {
                streetNumber = street + number.getNumber();
            } else {
                streetNumber = number.getNumber();
            }
        }
        //
        String building = regeocodeAddress.getBuilding();// 标志性建筑,当道路为null时显示
        //省
        if (!TextUtils.isEmpty(province)) {
            stringBuilder.append(province);
        }
        //市
        if (!TextUtils.isEmpty(city)) {
            if (!city.contains(province)) {
                stringBuilder.append(city);
            }
        }
        //区县
        if (!TextUtils.isEmpty(district)) {
            stringBuilder.append(district);
        }
        //乡镇
        if (!TextUtils.isEmpty(township)) {
            stringBuilder.append(township);
        }
        //道路
        if (!TextUtils.isEmpty(streetName)) {
            stringBuilder.append(streetName);
        }
        //标志性建筑
        if (!TextUtils.isEmpty(building)) {
            stringBuilder.append(building);
        } else {
            //门牌号
            if (!TextUtils.isEmpty(streetNumber)) {
                stringBuilder.append(streetNumber);
            }
        }
        String address;
        if (TextUtils.isEmpty(stringBuilder)) {
            address = township;
        } else {
            address = stringBuilder.append("附近").toString();
        }
        if (!TextUtils.isEmpty(address)) {
            deployAnalyzerModel.address = address;
        }
        try {
            LogUtils.loge("deployMapModel", "----" + deployAnalyzerModel.address);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        if (TextUtils.isEmpty(address)) {
            deviceMarker.hideInfoWindow();
        } else {
            deviceMarker.setTitle(address);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    deviceMarker.showInfoWindow();
                }
            });
        }
        //
    }

//    private void setMapCustomStyleFile() {
//        String styleName = "custom_config.data";
//        FileOutputStream outputStream = null;
//        InputStream inputStream = null;
//        String filePath = null;
//        try {
//            inputStream = mContext.getAssets().open(styleName);
//            byte[] b = new byte[inputStream.available()];
//            inputStream.read(b);
//
//            filePath = mContext.getFilesDir().getAbsolutePath();
//            File file = new File(filePath + "/" + styleName);
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//            outputStream = new FileOutputStream(file);
//            outputStream.write(b);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (inputStream != null) {
//                    inputStream.close();
//                }
//
//                if (outputStream != null) {
//                    outputStream.close();
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        aMap.setCustomMapStylePath(filePath + "/" + styleName);
//        //暂时去掉自定义的定位显示，防止查看自动定位
////        MyLocationStyle myLocationStyle = new MyLocationStyle();
////        myLocationStyle.radiusFillColor(Color.argb(25, 73, 144, 226));
////        myLocationStyle.strokeWidth(0);
////        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
////        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.deploy_map_location));
////        myLocationStyle.showMyLocation(true);
////        aMap.setMyLocationStyle(myLocationStyle);
//    }

    public void initMap(AMap map) {
        this.aMap = map;
//        setMapCustomStyleFile();
        aMap.getUiSettings().setTiltGesturesEnabled(false);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.getUiSettings().setLogoBottomMargin(-50);
        aMap.setMyLocationEnabled(false);
        aMap.setMapCustomEnable(true);
        String styleName = "custom_config.data";
        aMap.setCustomMapStylePath(mContext.getFilesDir().getAbsolutePath() + "/" + styleName);
        aMap.setOnMapLoadedListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setInfoWindowAdapter(this);
        aMap.setOnMapTouchListener(this);
    }

    public void doSaveLocation() {
        if (deployAnalyzerModel.latLng.size() == 2) {
            switch (deployAnalyzerModel.mapSourceType) {
                case DEPLOY_MAP_SOURCE_TYPE_DEPLOY_MONITOR_DETAIL:
                    getView().showProgressDialog();
                    if (PreferencesHelper.getInstance().getUserData().hasSignalConfig && deployAnalyzerModel.deployType != TYPE_SCAN_DEPLOY_STATION) {
                        RetrofitServiceHelper.getInstance().getDeployDeviceDetail(deployAnalyzerModel.sn, deployAnalyzerModel.latLng.get(0), deployAnalyzerModel.latLng.get(1))
                                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeployDeviceDetailRsp>(this) {
                            @Override
                            public void onCompleted(DeployDeviceDetailRsp deployDeviceDetailRsp) {
                                deployAnalyzerModel.blePassword = deployDeviceDetailRsp.getData().getBlePassword();
                                List<Integer> channelMask = deployDeviceDetailRsp.getData().getChannelMask();
                                if (channelMask != null && channelMask.size() > 0) {
                                    deployAnalyzerModel.channelMask.clear();
                                    deployAnalyzerModel.channelMask.addAll(channelMask);
                                }
                                getView().dismissProgressDialog();
                                handlerResult();
                            }

                            @Override
                            public void onErrorMsg(int errorCode, String errorMsg) {
                                getView().dismissProgressDialog();
                                //TODO 可以添加是否需要处理channelmask字段
                                handlerResult();
                            }
                        });
                    } else {
                        handlerResult();
                    }
                    break;
                case DEPLOY_MAP_SOURCE_TYPE_DEPLOY_RECORD:
                    break;
                case DEPLOY_MAP_SOURCE_TYPE_MONITOR_MAP_CONFIRM:
                    getView().showProgressDialog();
                    RetrofitServiceHelper.getInstance().doDevicePositionCalibration(deployAnalyzerModel.sn, deployAnalyzerModel.latLng.get(0), deployAnalyzerModel.latLng.get(1)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceDeployRsp>(this) {
                        @Override
                        public void onCompleted(DeviceDeployRsp deviceDeployRsp) {
                            getView().dismissProgressDialog();
                            DeviceInfo data = deviceDeployRsp.getData();
                            EventData eventData = new EventData();
                            eventData.code = EVENT_DATA_DEVICE_POSITION_CALIBRATION;
                            eventData.data = data;
                            EventBus.getDefault().post(eventData);
                            getView().finishAc();
                        }

                        @Override
                        public void onErrorMsg(int errorCode, String errorMsg) {
                            getView().dismissProgressDialog();
                            getView().toastShort(errorMsg);
                        }
                    });
                    break;

                case DEPLOY_MAP_SOURCE_TYPE_BASESTATION:
                    getView().showProgressDialog();
                    RetrofitServiceHelper.getInstance().updateStationLocation(deployAnalyzerModel.sn, deployAnalyzerModel.latLng.get(0), deployAnalyzerModel.latLng.get(1)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<BaseStationDetailRsp>(this) {
                        @Override
                        public void onCompleted(BaseStationDetailRsp deviceDeployRsp) {
                            getView().dismissProgressDialog();


                            EventData eventData = new EventData();
                            eventData.code = EVENT_DATA_UPDATEBASESTATION;
                            eventData.data = deviceDeployRsp.getData().getLonlatLabel();
                            EventBus.getDefault().post(eventData);
                            getView().finishAc();
                        }

                        @Override
                        public void onErrorMsg(int errorCode, String errorMsg) {
                            getView().dismissProgressDialog();
                            getView().toastShort(errorMsg);
                        }
                    });

                    break;
            }
        }
    }

    private void handlerResult() {
        EventData eventData = new EventData();
        eventData.code = EVENT_DATA_DEPLOY_MAP;
        eventData.data = deployAnalyzerModel;
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }

    public void refreshSignal() {
        switch (deployAnalyzerModel.mapSourceType) {
            case DEPLOY_MAP_SOURCE_TYPE_DEPLOY_MONITOR_DETAIL:
                getView().showProgressDialog();
                RetrofitServiceHelper.getInstance().getDeviceDetailInfoList(deployAnalyzerModel.sn, null, 1).subscribeOn(Schedulers.io()).observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>(this) {


                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }

                    @Override
                    public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {
                        if (deviceInfoListRsp.getData().size() > 0) {
                            DeviceInfo deviceInfo = deviceInfoListRsp.getData().get(0);
                            String signal = deviceInfo.getSignal();
                            getView().refreshSignal(deviceInfo.getUpdatedTime(), signal);
                        }
                        getView().dismissProgressDialog();
                    }
                });
                break;
            case DEPLOY_MAP_SOURCE_TYPE_DEPLOY_RECORD:
                break;
            case DEPLOY_MAP_SOURCE_TYPE_MONITOR_MAP_CONFIRM:
                break;
        }

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        switch (deployAnalyzerModel.mapSourceType) {
            case DEPLOY_MAP_SOURCE_TYPE_DEPLOY_RECORD:
                break;
            case DEPLOY_MAP_SOURCE_TYPE_DEPLOY_MONITOR_DETAIL:
            case DEPLOY_MAP_SOURCE_TYPE_MONITOR_MAP_CONFIRM:
            case DEPLOY_MAP_SOURCE_TYPE_BASESTATION:
                if (cameraPosition != null) {
                    //解决不能回显的bug 不能直接赋值
                    deviceMarker.setPosition(cameraPosition.target);
                    System.out.println("====>onCameraChange");
                }
                break;
        }

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        switch (deployAnalyzerModel.mapSourceType) {
            case DEPLOY_MAP_SOURCE_TYPE_DEPLOY_RECORD:
                if (deployAnalyzerModel.latLng.size() == 2) {
                    LatLonPoint lp = new LatLonPoint(deployAnalyzerModel.latLng.get(1), deployAnalyzerModel.latLng.get(0));
                    RegeocodeQuery query = new RegeocodeQuery(lp, 200, GeocodeSearch.AMAP);
                    System.out.println("====>onCameraChangeFinish=>" + lp.getLatitude() + "&" + lp.getLongitude());
                    deviceMarker.setInfoWindowEnable(true);
                    geocoderSearch.getFromLocationAsyn(query);
                }

                break;
            case DEPLOY_MAP_SOURCE_TYPE_DEPLOY_MONITOR_DETAIL:
            case DEPLOY_MAP_SOURCE_TYPE_MONITOR_MAP_CONFIRM:
            case DEPLOY_MAP_SOURCE_TYPE_BASESTATION:
                if (cameraPosition != null) {
                    deployAnalyzerModel.latLng.clear();
                    deployAnalyzerModel.latLng.add(cameraPosition.target.longitude);
                    deployAnalyzerModel.latLng.add(cameraPosition.target.latitude);
                    //
                    LatLonPoint lp = new LatLonPoint(deployAnalyzerModel.latLng.get(1), deployAnalyzerModel.latLng.get(0));
                    RegeocodeQuery query = new RegeocodeQuery(lp, 200, GeocodeSearch.AMAP);
                    System.out.println("====>onCameraChangeFinish=>" + lp.getLatitude() + "&" + lp.getLongitude());
                    deviceMarker.setPosition(cameraPosition.target);
                    geocoderSearch.getFromLocationAsyn(query);
                }
                break;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapLoaded() {
        aMap.setOnCameraChangeListener(this);
        //
        MarkerOptions locationOption = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.deploy_map_location))
                .anchor(0.5f, 0.6f)
                .draggable(false);
        locationMarker = aMap.addMarker(locationOption);
        AMapLocation lastKnownLocation = SensoroCityApplication.getInstance().mLocationClient.getLastKnownLocation();
        if (lastKnownLocation != null) {
            double lat = lastKnownLocation.getLatitude();//获取纬度
            double lon = lastKnownLocation.getLongitude();//获取经度
            locationMarker.setPosition(new LatLng(lat, lon));
        }
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.deploy_map_cur);
        MarkerOptions markerOption = new MarkerOptions().icon(bitmapDescriptor)
                .anchor(0.5f, 1)
                .draggable(true);
        deviceMarker = aMap.addMarker(markerOption);
        //加载完地图之后添加监听防止位置错乱
        if (deployAnalyzerModel.latLng.size() == 2) {
//可视化区域，将指定位置指定到屏幕中心位置
            LatLng latLng = new LatLng(deployAnalyzerModel.latLng.get(1), deployAnalyzerModel.latLng.get(0));
            CameraUpdate update = CameraUpdateFactory
                    .newCameraPosition(new CameraPosition(latLng, 15, 0, 30));
            aMap.moveCamera(update);
            deviceMarker.setPosition(latLng);
            LatLonPoint lp = new LatLonPoint(latLng.latitude, latLng.longitude);
            RegeocodeQuery query = new RegeocodeQuery(lp, 200, GeocodeSearch.AMAP);
            geocoderSearch.getFromLocationAsyn(query);
            try {
                LogUtils.loge("latLng = " + latLng.toString());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } else {
            backToCurrentLocation();
        }

    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int i) {
        System.out.println("====>onRegeocodeSearched");
        try {

            RegeocodeAddress regeocodeAddress = result.getRegeocodeAddress();
            setMarkerAddress(regeocodeAddress);
//            updateAddressInfo(result, i);
            //
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateAddressInfo(RegeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null && result.getRegeocodeAddress().getFormatAddress() != null) {
                String addressName = result.getRegeocodeAddress().getFormatAddress();
                if (TextUtils.isEmpty(addressName)) {
                    deviceMarker.hideInfoWindow();
                } else {
                    deployAnalyzerModel.address = addressName;
                    deviceMarker.setTitle(addressName);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            deviceMarker.showInfoWindow();
                        }
                    });
                }
            } else {
                try {
                    LogUtils.loge("查找位置无结果");
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        } else {
            try {
                LogUtils.loge("查找位置失败：rCode = " + rCode);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        try {
            LogUtils.loge("deployMapModel", "----" + deployAnalyzerModel.address);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    public void onTouch(MotionEvent motionEvent) {

    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = mContext.getLayoutInflater().inflate(R.layout.layout_marker, null);
        TextView info = (TextView) view.findViewById(R.id.marker_info);
        info.setText(marker.getTitle());
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    public void backToCurrentLocation() {
        if (aMap != null && deviceMarker != null) {
            LatLng latLng;
            CameraUpdate update;
            switch (deployAnalyzerModel.mapSourceType) {
                case DEPLOY_MAP_SOURCE_TYPE_DEPLOY_RECORD:
                    if (deployAnalyzerModel.latLng.size() == 2) {
                        latLng = new LatLng(deployAnalyzerModel.latLng.get(1), deployAnalyzerModel.latLng.get(0));
                        update = CameraUpdateFactory
                                .newCameraPosition(new CameraPosition(latLng, 15, 0, 30));
                        aMap.moveCamera(update);
                        deviceMarker.setPosition(latLng);
                    }
                    break;
                case DEPLOY_MAP_SOURCE_TYPE_DEPLOY_MONITOR_DETAIL:
                case DEPLOY_MAP_SOURCE_TYPE_MONITOR_MAP_CONFIRM:
                    AMapLocation lastKnownLocation = SensoroCityApplication.getInstance().mLocationClient.getLastKnownLocation();
                    if (lastKnownLocation != null) {
                        double lat = lastKnownLocation.getLatitude();//获取纬度
                        double lon = lastKnownLocation.getLongitude();//获取经度
                        latLng = new LatLng(lat, lon);
                        //可视化区域，将指定位置指定到屏幕中心位置
                        update = CameraUpdateFactory
                                .newCameraPosition(new CameraPosition(latLng, 15, 0, 30));
                        aMap.moveCamera(update);
                        if (locationMarker != null) {
                            locationMarker.setPosition(latLng);
                        }
                        deviceMarker.setPosition(latLng);
                    }
                    break;
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        //上报异常结果成功
        switch (code) {
            case EVENT_DATA_SOCKET_DATA_INFO:
                if (data instanceof DeviceInfo) {
                    DeviceInfo deviceInfo = (DeviceInfo) data;
                    String sn = deviceInfo.getSn();
                    try {
                        if (deployAnalyzerModel.sn.equalsIgnoreCase(sn)) {
                            deployAnalyzerModel.updatedTime = deviceInfo.getUpdatedTime();
                            deployAnalyzerModel.signal = deviceInfo.getSignal();
                            try {
                                LogUtils.loge(this, "地图也刷新信号 -->> deployMapModel.updatedTime = " + deployAnalyzerModel.updatedTime + ",deployMapModel.signal = " + deployAnalyzerModel.signal);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                            getView().refreshSignal(deployAnalyzerModel.updatedTime, deployAnalyzerModel.signal);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }
}

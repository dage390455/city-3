package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.geocoder.RegeocodeRoad;
import com.amap.api.services.geocoder.StreetNumber;
import com.lzy.imagepicker.bean.ImageItem;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.DeploySettingPhotoActivity;
import com.sensoro.smartcity.activity.DeployResultActivity;
import com.sensoro.smartcity.activity.DeploySettingContactActivity;
import com.sensoro.smartcity.activity.DeploySettingNameActivity;
import com.sensoro.smartcity.activity.DeploySettingTagActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployActivityView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.DeployContactModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.response.DeviceDeployRsp;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.server.response.StationInfo;
import com.sensoro.smartcity.server.response.StationInfoRsp;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.RegexUtils;
import com.sensoro.smartcity.widget.popup.UpLoadPhotosUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DeployActivityPresenter extends BasePresenter<IDeployActivityView> implements IOnCreate, AMap
        .OnMapClickListener, Constants, AMap.OnCameraChangeListener, AMap.OnMapLoadedListener, AMap
        .OnMarkerClickListener, AMap.InfoWindowAdapter, AMap.OnMapTouchListener, GeocodeSearch
        .OnGeocodeSearchListener, AMapLocationListener {
    private AMap aMap;
    private Marker smoothMoveMarker;
    private CameraUpdate mUpdata;
    private LatLng latLng;
    private MyLocationStyle myLocationStyle;
    private GeocodeSearch geocoderSearch;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private final List<String> tagList = new ArrayList<>();
    private String contact = null;
    private String content = null;
    private RegeocodeQuery query;
    private Activity mContext;
    private Handler mHandler;
    private DeviceInfo deviceInfo;
    private boolean hasStation;
    private String mAddress;
    private final ArrayList<ImageItem> images = new ArrayList<>();
    private UpLoadPhotosUtils upLoadPhotosUtils;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        mHandler = new Handler(Looper.getMainLooper());
        deviceInfo = (DeviceInfo) mContext.getIntent().getSerializableExtra(EXTRA_DEVICE_INFO);
        hasStation = mContext.getIntent().getBooleanExtra(EXTRA_IS_STATION_DEPLOY, false);
        getView().setDeployContactRelativeLayoutVisible(!hasStation);
        getView().setDeployDeviceRlSignalVisible(!hasStation);
        getView().setDeployPhotoVisible(!hasStation);
    }


    public void initMap(AMap map) {
        this.aMap = map;
        if (deviceInfo != null) {
            setMapCustomStyleFile();
            aMap.getUiSettings().setTiltGesturesEnabled(false);
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.getUiSettings().setMyLocationButtonEnabled(false);
            aMap.setMyLocationEnabled(true);
            aMap.setMapCustomEnable(true);
//            String styleName = "custom_config.data";
//            aMap.setCustomMapStylePath(mContext.getFilesDir().getAbsolutePath() + "/" + styleName);
            aMap.setOnMapClickListener(this);
            aMap.setOnCameraChangeListener(this);
            aMap.setOnMapLoadedListener(this);
            aMap.setOnMarkerClickListener(this);
            aMap.setInfoWindowAdapter(this);
            aMap.setOnMapTouchListener(this);
            aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
            myLocationStyle = new MyLocationStyle();
            myLocationStyle.radiusFillColor(Color.argb(25, 73, 144, 226));
            myLocationStyle.strokeWidth(0);
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_direction));
            myLocationStyle.showMyLocation(true);
            aMap.setMyLocationStyle(myLocationStyle);
        } else {
            Intent intent = new Intent();
            intent.setClass(mContext, DeployResultActivity.class);
            intent.putExtra(EXTRA_IS_STATION_DEPLOY, hasStation);
            intent.putExtra(EXTRA_SENSOR_RESULT, -1);
            getView().startAC(intent);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (cameraPosition != null) {
            latLng = cameraPosition.target;
            smoothMoveMarker.setPosition(latLng);
            System.out.println("====>onCameraChange");
        }

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        latLng = cameraPosition.target;
        smoothMoveMarker.setPosition(latLng);
        LatLonPoint lp = new LatLonPoint(latLng.latitude, latLng.longitude);
        System.out.println("====>onCameraChangeFinish=>" + lp.getLatitude() + "&" + lp.getLongitude());
        query = new RegeocodeQuery(lp, 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    @Override
    public void onMapLoaded() {
        String sn = deviceInfo.getSn();
        getView().setTitleTextView(sn);
        String name = deviceInfo.getName();
        if (TextUtils.isEmpty(name)) {
            name = mContext.getResources().getString(R.string.tips_hint_name_address_set);
        }
        getView().setNameAddressEditText(name);
        if (deviceInfo.getAlarms() != null) {
            AlarmInfo alarmInfo = deviceInfo.getAlarms();
            String contact = alarmInfo.getNotification().getContact();
            if (contact != null) {
                this.contact = contact;
                String content = alarmInfo
                        .getNotification().getContent();
                this.content = content;
                if (TextUtils.isEmpty(contact) || TextUtils.isEmpty(content)) {
                    getView().setContactEditText(mContext.getResources().getString(R.string.tips_hint_contact));
                } else {
                    getView().setContactEditText(contact + ":" + content);
                }

            }
        }

        String tags[] = deviceInfo.getTags();
        if (tags != null) {
            for (String tag : tags) {
                if (!TextUtils.isEmpty(tag)) {
                    tagList.add(tag);
                }
            }
            getView().refreshTagLayout(tagList);
//            if (tagList.size() == 0) {
//                getView().addDefaultTextView();
//            }
        } else {
            getView().addDefaultTextView();
        }
        String signal = deviceInfo.getSignal();
        if (!hasStation) {
            getView().refreshSignal(deviceInfo.getUpdatedTime(), signal);
        }
        //
        locate();
        //
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_move_location);
        MarkerOptions markerOption = new MarkerOptions().icon(bitmapDescriptor)
                .anchor(0.5f, 0.5f)
                .draggable(true);
        smoothMoveMarker = aMap.addMarker(markerOption);

        geocoderSearch = new GeocodeSearch(mContext);
        geocoderSearch.setOnGeocodeSearchListener(this);
        getView().setUploadButtonClickable(true);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
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

    @Override
    public void onTouch(MotionEvent motionEvent) {

    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        System.out.println("====>onRegeocodeSearched");
        try {
            RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
            setMarkerAddress(regeocodeAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    private void setMapCustomStyleFile() {
        String styleName = "custom_config.data";
        FileOutputStream outputStream = null;
        InputStream inputStream = null;
        String filePath = null;
        try {
            inputStream = mContext.getAssets().open(styleName);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);

            filePath = mContext.getFilesDir().getAbsolutePath();
            File file = new File(filePath + "/" + styleName);
            if (!file.exists()) {
                file.createNewFile();
            }
            outputStream = new FileOutputStream(file);
            outputStream.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        aMap.setCustomMapStylePath(filePath + "/" + styleName);

    }

    private void locate() {

        mLocationClient = new AMapLocationClient(mContext);
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
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

    private String tagListToString() {
        if (tagList.size() == 0) {
            return null;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < tagList.size(); i++) {
                String tag = tagList.get(i);
                if (!TextUtils.isEmpty(tag)) {
                    if (i == tagList.size() - 1) {
                        stringBuilder.append(tag);
                    } else {
                        stringBuilder.append(tag + ",");
                    }
                }

            }
            return stringBuilder.toString();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        System.out.println("====>onLocationChanged");
        if (aMapLocation != null) {
            double lat = aMapLocation.getLatitude();//获取纬度
            double lon = aMapLocation.getLongitude();//获取经度
            latLng = new LatLng(lat, lon);
            if (aMap != null) {
                //可视化区域，将指定位置指定到屏幕中心位置
                mUpdata = CameraUpdateFactory
                        .newCameraPosition(new CameraPosition(latLng, 15, 0, 30));
                aMap.moveCamera(mUpdata);
            }
            smoothMoveMarker.setPosition(latLng);
        } else {
            //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
            Log.e("地图错误", "定位失败, 错误码:" + aMapLocation.getErrorCode() + ", 错误信息:"
                    + aMapLocation.getErrorInfo());
        }
    }

    public void requestUpload(final String sn, final String name) {
//        例：大悦城20层走廊2号配电箱
        final String tags = tagListToString();
        String name_default = mContext.getString(R.string.tips_hint_name_address);
        if (TextUtils.isEmpty(name) || name.equals(name_default) || name.equals(mContext.getResources().getString(R
                .string.tips_hint_name_address_set))) {
            getView().toastShort(mContext.getResources().getString(R.string.tips_input_name));
            getView().setUploadButtonClickable(true);
            return;
        } else {
            byte[] bytes = new byte[0];
            try {
                bytes = name.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (bytes.length > 48) {
                getView().toastShort("名称/地址最长不能超过16个汉字或48个字符");
                getView().setUploadButtonClickable(true);
                return;
            }
        }
        if (latLng == null) {
            getView().toastShort(mContext.getResources().getString(R.string.tips_hint_location));
            getView().setUploadButtonClickable(true);
        } else {
            final double lon = latLng.longitude;
            final double lan = latLng.latitude;
            if (hasStation) {
                LogUtils.loge(tags);
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.doStationDeploy(sn, lon, lan, tags, name).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CityObserver<StationInfoRsp>() {


                            @Override
                            public void onCompleted() {
                                getView().dismissProgressDialog();
                                getView().finishAc();
                            }

                            @Override
                            public void onNext(StationInfoRsp stationInfoRsp) {
                                freshStation(stationInfoRsp);
                            }


                            @Override
                            public void onErrorMsg(int errorCode, String errorMsg) {
                                getView().dismissProgressDialog();
                                getView().setUploadButtonClickable(true);
                                if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                                    getView().toastShort(errorMsg);
                                } else if (errorCode == 4013101 || errorCode == 4000013) {
                                    freshError(sn, null);
                                } else {
                                    freshError(sn, errorMsg);
                                }
                            }
                        });
            } else {
                if (TextUtils.isEmpty(contact) || name.equals(content)) {
                    getView().toastShort("请输入联系人名称和电话号码");
                    getView().setUploadButtonClickable(true);
                    return;
                }
                if (!RegexUtils.checkPhone(content)) {
                    getView().toastShort(mContext.getResources().getString(R.string.tips_phone_empty));
                    getView().setUploadButtonClickable(true);
                    return;
                }

                if (images.size() > 0) {
                    //TODO 图片提交
                    final UpLoadPhotosUtils.UpLoadPhotoListener upLoadPhotoListener = new UpLoadPhotosUtils
                            .UpLoadPhotoListener() {

                        @Override
                        public void onStart() {
                            getView().showStartUploadProgressDialog();
                        }

                        @Override
                        public void onComplete(List<String> imagesUrl) {
                            String s = "";
                            for (String temp : imagesUrl) {
                                s += temp + "\n";
                            }
                            getView().dismissUploadProgressDialog();
                            LogUtils.loge(this, "上传成功---" + s);
                            //TODO 上传结果
                            doDeployResult(sn, name, tags, lon, lan, contact, content, imagesUrl);
                        }

                        @Override
                        public void onError(String errMsg) {
                            getView().dismissUploadProgressDialog();
                            getView().toastShort(errMsg);
                        }

                        @Override
                        public void onProgress(int index, double percent) {
                            getView().showUploadProgressDialog(index, images.size(), percent);
                        }
                    };
                    upLoadPhotosUtils = new UpLoadPhotosUtils(mContext, upLoadPhotoListener);
                    upLoadPhotosUtils.doUploadPhoto(images);
                } else {
                    doDeployResult(sn, name, tags, lon, lan, contact, content, null);
                }
            }
        }

    }

    private void doDeployResult(final String sn, String name, String tags, double lon, double lan, String contact,
                                String content, List<String> imgUrls) {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.doDevicePointDeploy(sn, lon, lan, tags, name,
                contact, content, imgUrls).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<DeviceDeployRsp>() {


                    @Override
                    public void onCompleted() {
                        getView().dismissProgressDialog();
                        getView().finishAc();
                    }

                    @Override
                    public void onNext(DeviceDeployRsp deviceDeployRsp) {
                        freshPoint(deviceDeployRsp);
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().setUploadButtonClickable(true);
                        if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                            getView().toastShort(errorMsg);
                        } else if (errorCode == 4013101 || errorCode == 4000013) {
                            freshError(sn, null);
                        } else {
                            freshError(sn, errorMsg);
                        }
                    }
                });
    }

    private void freshError(String scanSN, String errorInfo) {
        //
        Intent intent = new Intent();
        intent.setClass(mContext, DeployResultActivity.class);
        intent.putExtra(EXTRA_SENSOR_RESULT, -1);
        intent.putExtra(EXTRA_SENSOR_SN_RESULT, scanSN);
        intent.putExtra(EXTRA_IS_STATION_DEPLOY, hasStation);
        if (!TextUtils.isEmpty(errorInfo)) {
            intent.putExtra(EXTRA_SENSOR_RESULT_ERROR, errorInfo);
        }
        getView().startAC(intent);
    }

    private void freshPoint(DeviceDeployRsp deviceDeployRsp) {
        int errCode = deviceDeployRsp.getErrcode();
        int resultCode = 1;
        if (errCode != ResponseBase.CODE_SUCCESS) {
            resultCode = errCode;
        }
        Intent intent = new Intent(mContext, DeployResultActivity.class);
        intent.putExtra(EXTRA_SENSOR_RESULT, resultCode);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        DeviceInfo data = deviceDeployRsp.getData();
        data.setAddress(mAddress);
        intent.putExtra(EXTRA_DEVICE_INFO, data);
        intent.putExtra(EXTRA_SETTING_CONTACT, contact);
        intent.putExtra(EXTRA_SETTING_CONTENT, content);
        intent.putExtra(EXTRA_IS_STATION_DEPLOY, false);
        getView().startAC(intent);
    }

    private void freshStation(StationInfoRsp stationInfoRsp) {
        String s = stationInfoRsp.toString();
        LogUtils.loge(s);
        int errCode = stationInfoRsp.getErrcode();
        int resultCode = 1;
        if (errCode != ResponseBase.CODE_SUCCESS) {
            resultCode = errCode;
        }
        //
        StationInfo stationInfo = stationInfoRsp.getData();
        double[] lonLat = stationInfo.getLonlat();
        String name = stationInfo.getName();
        String sn = stationInfo.getSn();
        String[] tags = stationInfo.getTags();
        int normalStatus = stationInfo.getNormalStatus();
        long updatedTime = stationInfo.getUpdatedTime();
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setSn(sn);
        deviceInfo.setTags(tags);
        deviceInfo.setLonlat(lonLat);
        deviceInfo.setStatus(normalStatus);
        deviceInfo.setAddress(mAddress);
        deviceInfo.setUpdatedTime(updatedTime);
        if (!TextUtils.isEmpty(name)) {
            deviceInfo.setName(name);
        }
        Intent intent = new Intent(mContext, DeployResultActivity.class);
        intent.putExtra(EXTRA_SENSOR_RESULT, resultCode);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        intent.putExtra(EXTRA_IS_STATION_DEPLOY, true);
        intent.putExtra(EXTRA_DEVICE_INFO, deviceInfo);
        getView().startAC(intent);
    }

    public void doSettingByNameAndAddress(String nameAddress) {
        Intent intent = new Intent(mContext, DeploySettingNameActivity.class);
        intent.putExtra(EXTRA_SETTING_NAME_ADDRESS, nameAddress);
        getView().startAC(intent);
    }

    public void doSettingByTag() {
        Intent intent = new Intent(mContext, DeploySettingTagActivity.class);
        intent.putStringArrayListExtra(EXTRA_SETTING_TAG_LIST, (ArrayList<String>) tagList);
        getView().startAC(intent);
    }

    public void doSettingContact() {
        Intent intent = new Intent(mContext, DeploySettingContactActivity.class);
        if (!TextUtils.isEmpty(contact)) {
            intent.putExtra(EXTRA_SETTING_CONTACT, contact);
        }
        if (!TextUtils.isEmpty(content)) {
            intent.putExtra(EXTRA_SETTING_CONTENT, content);
        }
        getView().startAC(intent);
    }

    public void doSignal(String sn) {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getDeviceDetailInfoList(sn, null, 1).subscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>() {


            @Override
            public void onCompleted() {
                getView().dismissProgressDialog();
            }

            @Override
            public void onNext(DeviceInfoListRsp deviceInfoListRsp) {
                if (deviceInfoListRsp.getData().size() > 0) {
                    DeviceInfo deviceInfo = deviceInfoListRsp.getData().get(0);
                    String signal = deviceInfo.getSignal();
                    getView().refreshSignal(deviceInfo.getUpdatedTime(), signal);
                }
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });

    }

    private void setMarkerAddress(RegeocodeAddress regeocodeAddress) {
        StringBuffer stringBuffer = new StringBuffer();
        String subLoc = regeocodeAddress.getDistrict();// 区或县或县级市
        String ts = regeocodeAddress.getTownship();// 乡镇
        String thf = null;// 道路
        List<RegeocodeRoad> regeocodeRoads = regeocodeAddress.getRoads();// 道路列表
        if (regeocodeRoads != null && regeocodeRoads.size() > 0) {
            RegeocodeRoad regeocodeRoad = regeocodeRoads.get(0);
            if (regeocodeRoad != null) {
                thf = regeocodeRoad.getName();
            }
        }
        String subthf = null;// 门牌号
        StreetNumber streetNumber = regeocodeAddress.getStreetNumber();
        if (streetNumber != null) {
            subthf = streetNumber.getNumber();
        }
        String fn = regeocodeAddress.getBuilding();// 标志性建筑,当道路为null时显示
//        if (subLoc != null) {
//            stringBuffer.append(subLoc);
//        }
//        if (ts != null) {
//            stringBuffer.append(ts);
//        }
        if (thf != null) {
            stringBuffer.append(thf);
        }
        if (subthf != null) {
            stringBuffer.append(subthf);
        }
        String address = stringBuffer.toString();
        if (TextUtils.isEmpty(address)) {
            address = ts;
        }
        mAddress = address;
        System.out.println(address);
        if (TextUtils.isEmpty(address)) {
            smoothMoveMarker.hideInfoWindow();
        } else {
            smoothMoveMarker.setTitle(address);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    smoothMoveMarker.showInfoWindow();
                }
            });
        }
    }

    public void doSettingPhoto() {
        //
        Intent intent = new Intent(mContext, DeploySettingPhotoActivity.class);
        if (images.size() > 0) {
            intent.putExtra(EXTRA_DEPLOY_TO_PHOTO, images);
        }
        getView().startAC(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        //TODO 可以修改以此种方式传递，方便管理
        int code = eventData.code;
        Object data = eventData.data;
        if (code == EVENT_DATA_DEPLOY_RESULT_FINISH || code == EVENT_DATA_DEPLOY_RESULT_CONTINUE) {
            getView().finishAc();
        } else if (code == EVENT_DATA_DEPLOY_SETTING_NAME_ADDRESS) {
            if (data instanceof String) {
                getView().setNameAddressEditText((String) data);
            }
        } else if (code == EVENT_DATA_DEPLOY_SETTING_TAG) {
            if (data instanceof List) {
                tagList.clear();
                tagList.addAll((List<String>) data);
                getView().refreshTagLayout(tagList);
            }
        } else if (code == EVENT_DATA_DEPLOY_SETTING_CONTACT) {
            if (data instanceof DeployContactModel) {
                contact = ((DeployContactModel) data).name;
                content = ((DeployContactModel) data).phone;
                getView().setContactEditText(contact + ":" + content);
            }
        } else if (code == EVENT_DATA_DEPLOY_SETTING_PHOTO) {
            if (data instanceof List) {
                images.clear();
                images.addAll((ArrayList<ImageItem>) data);
                if (images.size() > 0) {
                    getView().setDeployPhotoText("已选择" + images.size() + "张图片");
                } else {
                    getView().setDeployPhotoText("未选择部署图片");
                }
            }
        }
        LogUtils.loge(this, eventData.toString());
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        tagList.clear();
        images.clear();
    }
}

package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.mapbox.mapboxsdk.maps.widgets.MyLocationViewSettings;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.DeployMapENActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IMonitorPointMapENActivityView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.DeployAnalyzerModel;
import com.sensoro.smartcity.model.DeployContactModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.model.EventLoginData;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.GPSUtil;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.util.WidgetUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MonitorPointMapENActivityPresenter extends BasePresenter<IMonitorPointMapENActivityView> implements Constants, IOnCreate, OnMapReadyCallback, MapboxMap.InfoWindowAdapter {

    private Activity mContext;
    private MapboxMap aMap;
    private DeviceInfo mDeviceInfo;
    private double[] currentLonlat;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        mDeviceInfo = (DeviceInfo) mContext.getIntent().getSerializableExtra(EXTRA_DEVICE_INFO);
        EventLoginData userData = PreferencesHelper.getInstance().getUserData();
        if (userData != null) {
            getView().setPositionCalibrationVisible(userData.hasDevicePositionCalibration);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            case EVENT_DATA_DEVICE_POSITION_CALIBRATION:
                if (data instanceof DeviceInfo) {
                    DeviceInfo pushDeviceInfo = (DeviceInfo) data;
                    if (pushDeviceInfo.getSn().equalsIgnoreCase(mDeviceInfo.getSn())) {
                        mDeviceInfo.cloneSocketData(pushDeviceInfo);
                        refreshMap();
                    }
                }
                break;
        }
    }

    private boolean changeLonLat() {
        currentLonlat = mDeviceInfo.getLonlat();
        if (currentLonlat != null && currentLonlat.length == 2) {
            currentLonlat = GPSUtil.gcj02_To_Gps84(currentLonlat[1], currentLonlat[0]);
            return true;
        }
        return false;
    }

    private void refreshMap() {
        aMap.clear();
        if (changeLonLat()) {
            LatLng latLng = new LatLng(currentLonlat[0], currentLonlat[1]);
            IconFactory iconFactory = IconFactory.getInstance(mContext);
            Icon icon = iconFactory.fromResource(R.drawable.deploy_map_cur);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.icon(icon).title("国外")
                    .position(latLng);
            Marker markerView = aMap.addMarker(markerOptions);
            aMap.setInfoWindowAdapter(this);
            markerView.setPosition(latLng);
            CameraPosition position = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(16)
                    .tilt(20)
                    .build();

            aMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
            LogUtils.loge("latLng = " + latLng.toString());
        } else {
            backToCurrentLocation();
        }
    }


    public void doNavigation() {
        if (!AppUtils.doNavigation(mContext, currentLonlat, WidgetUtil.getDeviceTypeName(mDeviceInfo.getDeviceType()))) {
            getView().toastShort(mContext.getString(R.string.location_not_obtained));
        }
    }

    public void backToCurrentLocation() {
        LatLng latLng = new LatLng(currentLonlat[0], currentLonlat[1]);
        CameraPosition position = new CameraPosition.Builder()
                .target(latLng)
                .zoom(16)
                .tilt(20)
                .build();
        aMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    public void doPositionConfirm() {
        Intent intent = new Intent();
        DeployAnalyzerModel deployAnalyzerModel = new DeployAnalyzerModel();
        deployAnalyzerModel.sn = mDeviceInfo.getSn();
        deployAnalyzerModel.status = mDeviceInfo.getStatus();
        deployAnalyzerModel.deviceType = mDeviceInfo.getDeviceType();
        String contact = mDeviceInfo.getContact();
        String content = mDeviceInfo.getContent();
        if (!TextUtils.isEmpty(content)) {
            DeployContactModel deployContactModel = new DeployContactModel();
            deployContactModel.phone = content;
            deployContactModel.name = contact;
            deployAnalyzerModel.deployContactModelList.add(deployContactModel);
        }
        double[] lonlat = mDeviceInfo.getLonlat();
        if (lonlat != null && lonlat.length == 2) {
            deployAnalyzerModel.latLng.add(lonlat[0]);
            deployAnalyzerModel.latLng.add(lonlat[1]);
        }
        deployAnalyzerModel.updatedTime = mDeviceInfo.getUpdatedTime();
        deployAnalyzerModel.signal = mDeviceInfo.getSignal();
        String tempAddress = mDeviceInfo.getAddress();
        if (TextUtils.isEmpty(tempAddress)) {
            deployAnalyzerModel.address = tempAddress;
        }
        deployAnalyzerModel.mapSourceType = DEPLOY_MAP_SOURCE_TYPE_MONITOR_MAP_CONFIRM;
        deployAnalyzerModel.deployType = TYPE_SCAN_DEPLOY_DEVICE;
        intent.setClass(mContext, DeployMapENActivity.class);
        intent.putExtra(EXTRA_DEPLOY_ANALYZER_MODEL, deployAnalyzerModel);
        getView().startAC(intent);
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.aMap = mapboxMap;
        aMap.setMyLocationEnabled(true);
        aMap.setInfoWindowAdapter(this);
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setCompassEnabled(false);//隐藏指南针
        uiSettings.setLogoEnabled(false);//隐藏logo
        uiSettings.setTiltGesturesEnabled(true);//设置是否可以调整地图倾斜角
        uiSettings.setRotateGesturesEnabled(true);//设置是否可以旋转地图
        uiSettings.setAttributionEnabled(false);//设置是否显示那个提示按钮
        MyLocationViewSettings locationSettings = aMap.getMyLocationViewSettings();
        locationSettings.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.deploy_map_location), new int[]{20, 20, 20, 20});
        locationSettings.setForegroundTintColor(ContextCompat.getColor(mContext, R.color.transparent));
        locationSettings.setAccuracyTintColor(ContextCompat.getColor(mContext, R.color.transparent));
        locationSettings.setAccuracyAlpha(50);
        locationSettings.setTilt(30);
        refreshMap();
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull com.mapbox.mapboxsdk.annotations.Marker marker) {
        View view = mContext.getLayoutInflater().inflate(R.layout.layout_marker, null);
        TextView info = (TextView) view.findViewById(R.id.marker_info);
        info.setText(marker.getTitle());
        return view;
    }
}
